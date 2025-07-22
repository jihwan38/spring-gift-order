package gift.product.dto.response;

import gift.product.entity.Product;


public record ProductResponseDto(Long id, String name, Long price, String imageUrl, Boolean isKakaoApprovedByMd) {
    public static ProductResponseDto from(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl(),
                product.getIsKakaoApprovedByMd()
        );
    }
}


