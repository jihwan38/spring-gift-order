package gift.product.dto.view;

import gift.product.dto.response.ProductResponseDto;

public record ProductFormDto(
        String name,
        Long price,
        String imageUrl,
        Boolean isKakaoApprovedByMd
) {
    public static ProductFormDto emptyForm(){
        return new ProductFormDto("", 0L, "", Boolean.FALSE);
    }

    public static ProductFormDto from(ProductResponseDto responseDto){
        return new ProductFormDto(
                responseDto.name(),
                responseDto.price(),
                responseDto.imageUrl(),
                responseDto.isKakaoApprovedByMd()
        );
    }
}
