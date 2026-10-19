package com.krishu.caretracev2.Controller;

import com.krishu.caretracev2.DTO.ImportantPlaceRequest;
import com.krishu.caretracev2.DTO.ImportantPlaceResponse;
import com.krishu.caretracev2.Service.ImportantPlaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/importantPlace")
public class ImportantPlaceController {

    private final ImportantPlaceService importantPlaceService;

    public ImportantPlaceController(ImportantPlaceService importantPlaceService){
        this.importantPlaceService=importantPlaceService;
    }

    @PostMapping("/addPlace/{patientId}")
    public ResponseEntity<ImportantPlaceResponse> addPlace(Authentication authentication,
                                                           @RequestPart ImportantPlaceRequest request
            , @PathVariable String patientId,@RequestPart MultipartFile photoFile) throws IOException {
        return ResponseEntity.ok(importantPlaceService.addImportantPlace(authentication,
                request,patientId,photoFile));
    }

    @GetMapping("/getAllPlaces/{patientId}")
    public ResponseEntity<List<ImportantPlaceResponse>> getAllPlaces(Authentication authentication,
                                                                     @PathVariable String patientId){
        return ResponseEntity.ok(importantPlaceService.getAllImportantPlaces(authentication,patientId));
    }

    @GetMapping("/getPlace/{placeId}")
    public ResponseEntity<ImportantPlaceResponse> getPlace(Authentication authentication,
                                                           @PathVariable String placeId){
        return ResponseEntity.ok(importantPlaceService.getPlace(authentication,placeId));
    }

    @DeleteMapping("/deletePlace/{placeId}")
    public void deletePlace(Authentication authentication,@PathVariable String placeId){
        importantPlaceService.deleteImportantPlace(authentication,placeId);
    }
}
