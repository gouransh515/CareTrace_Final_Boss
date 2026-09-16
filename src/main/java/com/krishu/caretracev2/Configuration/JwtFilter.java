package com.krishu.caretracev2.Configuration;

import com.krishu.caretracev2.Service.JwtService;
import com.krishu.caretracev2.Service.MyUserDetailService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MyUserDetailService userDetailService;

    public JwtFilter(JwtService jwtService, MyUserDetailService userDetailService) {
        this.jwtService = jwtService;
        this.userDetailService = userDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader=request.getHeader("Authorization");
        String id=null;
        String token=null;
        if(authHeader!=null && authHeader.startsWith("Bearer ")){
            token=authHeader.substring(7);
            try {
                id = jwtService.extractId(token);
            } catch (ExpiredJwtException e) {
                logger.debug("JWT expired: {}",e.getCause());
            } catch (JwtException e) {
                logger.debug("Invalid JWT: {}",e.getCause());
            }
        }
        if(id!=null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetail=userDetailService.loadUserByUsername(id);
            if(jwtService.validateToken(token,id)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals("/loginUser") || path.equals("/register");
    }
}
