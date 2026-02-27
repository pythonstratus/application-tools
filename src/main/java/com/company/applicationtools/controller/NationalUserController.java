package com.company.applicationtools.controller;

import com.company.applicationtools.dto.*;
import com.company.applicationtools.service.NationalUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/national-users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "National User Options API", description = "CRUD operations for national users")
public class NationalUserController {

    private final NationalUserService nationalUserService;

    @GetMapping("/health")
    @Operation(summary = "Health check")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("National User Service is healthy");
    }

    // =========================================================================
    // Search
    // =========================================================================

    @PostMapping("/search")
    @Operation(summary = "Search national users by any combination of fields",
               description = "All fields are optional. Returns matching users from ENTEMP.")
    public ResponseEntity<List<NationalUserResponse>> search(
            @RequestBody NationalUserSearchRequest request) {
        return ResponseEntity.ok(nationalUserService.search(request));
    }

    // =========================================================================
    // Get By ID
    // =========================================================================

    @GetMapping("/{roid}/{seid}")
    @Operation(summary = "Get a national user by composite key (ROID + SEID)")
    public ResponseEntity<NationalUserResponse> getById(
            @Parameter(description = "ROID (Assignment Number)") @PathVariable Long roid,
            @Parameter(description = "SEID") @PathVariable String seid) {
        return ResponseEntity.ok(nationalUserService.getById(roid, seid));
    }

    // =========================================================================
    // Add User
    // =========================================================================

    @PostMapping
    @Operation(summary = "Add a new national user",
               description = "Creates a new record in ENTEMP. First + Last name are combined into NAME column.")
    public ResponseEntity<NationalUserResponse> addUser(
            @Valid @RequestBody NationalUserRequest request) {
        NationalUserResponse created = nationalUserService.addUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // =========================================================================
    // Modify User
    // =========================================================================

    @PutMapping("/{roid}/{seid}")
    @Operation(summary = "Modify an existing national user",
               description = "Updates all editable fields for the user identified by ROID + SEID")
    public ResponseEntity<NationalUserResponse> modifyUser(
            @Parameter(description = "ROID") @PathVariable Long roid,
            @Parameter(description = "SEID") @PathVariable String seid,
            @Valid @RequestBody NationalUserRequest request) {
        return ResponseEntity.ok(nationalUserService.modifyUser(roid, seid, request));
    }

    // =========================================================================
    // Delete User
    // =========================================================================

    @DeleteMapping("/{roid}/{seid}")
    @Operation(summary = "Delete a national user",
               description = "Permanently removes the user from ENTEMP")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ROID") @PathVariable Long roid,
            @Parameter(description = "SEID") @PathVariable String seid) {
        nationalUserService.deleteUser(roid, seid);
        return ResponseEntity.noContent().build();
    }

    // =========================================================================
    // Org Dropdown
    // =========================================================================

    @GetMapping("/orgs")
    @Operation(summary = "Get distinct User Org values for dropdown",
               description = "Returns distinct ORG values from ENTEMP table")
    public ResponseEntity<List<String>> getOrgs() {
        return ResponseEntity.ok(nationalUserService.getDistinctOrgs());
    }
}
