package gift.product.dto.response;

import gift.product.entity.Product;
import java.util.List;

public record ProductResponseDto(
        Long id,
        String name,
        Long price,
        String imageUrl,
        Boolean isKakaoApprovedByMd,
        List<OptionResponseDto> optionList
) {
    public static ProductResponseDto from(Product product) {
        List<OptionResponseDto> optionDtos = product.getOptions().stream()
                .map(OptionResponseDto::from)
                .toList();

        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl(),
                product.getIsKakaoApprovedByMd(),
                optionDtos
        );
    }
}