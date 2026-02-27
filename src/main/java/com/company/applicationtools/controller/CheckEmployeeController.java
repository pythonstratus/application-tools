package com.company.applicationtools.controller;

import com.company.applicationtools.dto.CheckEmployeeResponse;
import com.company.applicationtools.dto.CheckEmployeeSearchRequest;
import com.company.applicationtools.service.CheckEmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/check-employee")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Check Employee API", description = "Read-only dynamic employee lookup")
public class CheckEmployeeController {

    private final CheckEmployeeService checkEmployeeService;

    @GetMapping("/health")
    @Operation(summary = "Health check")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Check Employee Service is healthy");
    }

    @PostMapping("/search")
    @Operation(summary = "Search employees with dynamic field/operator/value",
               description = "Select a field, operator, and value to query the employee table. " +
                             "Results include data from ENTEMP LEFT JOINed with ENTITY_USER for Last Access Date.")
    public ResponseEntity<List<CheckEmployeeResponse>> search(
            @Valid @RequestBody CheckEmployeeSearchRequest request) {
        return ResponseEntity.ok(checkEmployeeService.search(request));
    }

    @GetMapping("/fields")
    @Operation(summary = "Get searchable fields for dropdown",
               description = "Returns the list of fields available in the Check Employee search dropdown")
    public ResponseEntity<List<Map<String, String>>> getFields() {
        return ResponseEntity.ok(checkEmployeeService.getSearchableFields());
    }

    @GetMapping("/operators")
    @Operation(summary = "Get available operators for dropdown",
               description = "Returns the list of operators (=, >, LIKE, IN, BETWEEN, etc.)")
    public ResponseEntity<List<Map<String, String>>> getOperators() {
        return ResponseEntity.ok(checkEmployeeService.getOperators());
    }
}
