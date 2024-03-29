package com.example.muahexanh_resigtration.controllers;

import com.example.muahexanh_resigtration.dtos.LoginDTO;
import com.example.muahexanh_resigtration.dtos.ProjectDTO;
import com.example.muahexanh_resigtration.entities.CommunityLeaderEntity;
import com.example.muahexanh_resigtration.entities.ProjectEntity;
import com.example.muahexanh_resigtration.responses.CommunityLeader.CommunityLeaderResponseUser;
import com.example.muahexanh_resigtration.responses.CommunityLeader.CommunityLeaderResponse;
import com.example.muahexanh_resigtration.responses.ResponseObject;
import com.example.muahexanh_resigtration.services.CommunityLeader.iCommunityLeaderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/communityleader")
@RequiredArgsConstructor
public class CommunityLeaderController {
    private final iCommunityLeaderService communityLeaderService;


    @GetMapping("/{id}") public ResponseEntity<?> getLeaderById( @PathVariable long id)
    {
        try
        {
            CommunityLeaderEntity communityLeader = communityLeaderService.getCommunityLeaderById(id);
            return ResponseEntity.ok(
                    ResponseObject.builder().
                            data(CommunityLeaderResponse.fromCommunityLeader(communityLeader))
                            .message("Get community leader by id successfully")
                            .status(HttpStatus.OK) .build()); }
        catch (Exception e) {
            return ResponseEntity.badRequest().
                body(ResponseObject.builder() .message("An error occurred: " + e.getMessage())
                        .status(HttpStatus.BAD_REQUEST) .build()); }
    }
    @GetMapping("/search")
    public ResponseEntity<ResponseObject> SearchProjectByTitle(
            @Valid @RequestParam("id") Long leaderId,
            @Valid @RequestParam("title") String title

    ) {
        try {

            List<ProjectEntity> listProjects = communityLeaderService.searchProjectByTitle(leaderId, title);
            return ResponseEntity.ok(ResponseObject.builder()
                    .data(listProjects)
                    .message("Get detail project successfully")
                    .status(HttpStatus.OK)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("An error occurred: " + e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }

    @GetMapping("/filterByStatus")
    public ResponseEntity<?> filterProjectsByStatus(@Valid @RequestParam("id") Long leaderId,
            @Valid @RequestParam("status") String status) {
        try {
            List<ProjectEntity> filteredProjects = communityLeaderService.filterProjectsByStatus(leaderId, status);
            return ResponseEntity.ok(filteredProjects);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginCommunityLeader(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            CommunityLeaderResponseUser communityLeader = communityLeaderService.loginCommunityLeader(loginDTO);
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("id", communityLeader.getId());
            dataMap.put("fullName", communityLeader.getFullName());
            dataMap.put("phoneNumber", communityLeader.getPhoneNumber());
            dataMap.put("email", communityLeader.getEmail());

            // return a community leader id
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .message("Found community leader account")
                            .status(HttpStatus.OK)
                            .data(dataMap)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/createProject")
    public  ResponseEntity<?> createProjectOfCommunityLeader (@Valid @RequestParam("communityLeaderId") Long leaderId,
                                             @Valid @RequestBody ProjectDTO projectDTO,
                                             BindingResult result) {
        try {
            if (leaderId == null || leaderId <= 0) {
                return ResponseEntity.badRequest().body("Invalid communityLeaderId");
            }

            // Kiểm tra xem projectDTO có hợp lệ không
            if (projectDTO == null) {
                return ResponseEntity.badRequest().body("ProjectDTO is required");
            }
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages );
            }
            CommunityLeaderEntity projectResponse = communityLeaderService.createProjectOfComCommunityLeader(leaderId, projectDTO);
            if (projectResponse == null) {
                return ResponseEntity.badRequest().body("Response is Null");
            }
            return ResponseEntity.ok(ResponseObject.builder()
                    .data(projectResponse)
                    .message("Create project successfully")
                    .status(HttpStatus.OK)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
