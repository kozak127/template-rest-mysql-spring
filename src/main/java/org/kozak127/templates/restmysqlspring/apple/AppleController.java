package org.kozak127.templates.restmysqlspring.apple;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.kozak127.templates.restmysqlspring.DefaultController;


@RestController
@RequestMapping("/api/apples")
@Tag(name = "Apples")
@RequiredArgsConstructor
public class AppleController extends DefaultController {

    private final AppleRepository appleRepository;

    @Operation(summary = "Get apple with specified ID",
            description = "Get apple with specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Apple fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Cannot find Apple with specified ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AppleDTO> getAppleById(@PathVariable String id) {
        return appleRepository.findById(id)
                .map(AppleDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Save apple", description = "Save apple")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Apple saved successfully"),
            @ApiResponse(responseCode = "409", description = "Conflicting apple ID. Entity already exists")
    })
    @PostMapping
    public ResponseEntity<AppleDTO> saveApple(@RequestBody AppleDTO appleDTO) {

        if (appleRepository.existsById(appleDTO.getId())) {
            return ResponseEntity.status(409).build();
        }

        Apple toSave = Apple.fromDto(appleDTO);
        Apple savedApple = appleRepository.save(toSave);
        AppleDTO toReturn = AppleDTO.fromEntity(savedApple);
        return ResponseEntity.status(HttpStatus.CREATED).body(toReturn);
    }

    @Operation(summary = "Delete apple by ID", description = "Delete apple by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Apple deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Cannot find Apple with specified ID")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppleById(@PathVariable String id) {
        if (appleRepository.existsById(id)) {
            appleRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update apple", description = "Update apple")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Apple updated successfully"),
            @ApiResponse(responseCode = "404", description = "Cannot find Apple with specified ID")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AppleDTO> updateAppleById(@PathVariable String id, @RequestBody AppleDTO toUpdateDTO) {
        return appleRepository.findById(id)
                .map(foundApple -> {
                    Apple toUpdate = Apple.fromDto(toUpdateDTO);
                    return AppleDTO.fromEntity(appleRepository.save(toUpdate));
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
