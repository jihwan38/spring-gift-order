package gift.product.controller;

import gift.product.dto.request.OptionRequestDto;
import gift.product.dto.response.OptionResponseDto;
import gift.product.service.OptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/options")
public class OptionController {
    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @PostMapping
    public ResponseEntity<OptionResponseDto> addOption(
            @PathVariable Long productId,
            @RequestBody @Valid OptionRequestDto optionRequestDto) {
        OptionResponseDto optionResponseDto = optionService.addOption(productId, optionRequestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(optionResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<OptionResponseDto>> getOptions(
            @PathVariable Long productId) {
        List<OptionResponseDto> options = optionService.getOptions(productId);

        return ResponseEntity.ok(options);
    }

    @PutMapping("/{optionId}")
    public ResponseEntity<OptionResponseDto> updateOption(
            @PathVariable Long productId,
            @PathVariable Long optionId,
            @RequestBody @Valid OptionRequestDto optionRequestDto){
        OptionResponseDto optionResponseDto = optionService.updateOption(productId, optionId, optionRequestDto);

        return ResponseEntity.ok(optionResponseDto);
    }

    @DeleteMapping("/{optionId}")
    public ResponseEntity<Void> deleteOption(
            @PathVariable Long productId,
            @PathVariable Long optionId){
        optionService.deleteOption(productId, optionId);

        return ResponseEntity.noContent().build();
    }
}
