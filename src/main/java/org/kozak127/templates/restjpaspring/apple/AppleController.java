package org.kozak127.templates.restjpaspring.apple;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.kozak127.templates.restjpaspring.DefaultController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


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
    @Transactional(readOnly = true)
    public ResponseEntity<AppleDTO> getAppleById(@PathVariable String id) {
        return appleRepository.findById(id)
                .map(AppleDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Save apple", description = "Save apple")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Apple saved successfully")
    })
    @PostMapping
    @Transactional
    public ResponseEntity<AppleDTO> saveApple(@RequestBody AppleDTO appleDTO) {
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
    @Transactional
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
    @Transactional
    public ResponseEntity<AppleDTO> updateAppleById(@PathVariable String id, @RequestBody AppleDTO toUpdateDTO) {
        return appleRepository.findById(id)
                .map(foundApple -> {
                    Apple updatedApple = foundApple.toBuilder()
                            .name(toUpdateDTO.getName())
                            .build();
                    Apple savedApple = appleRepository.save(updatedApple);
                    return AppleDTO.fromEntity(savedApple);
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
