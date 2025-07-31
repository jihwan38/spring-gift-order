package gift.product.dto.view;

import gift.product.dto.response.OptionResponseDto;
import gift.product.dto.response.ProductResponseDto;

import java.util.List;


public record ProductView(
        Long id,
        String name,
        String formattedPrice,
        String imageUrl,
        List<OptionResponseDto> optionList
) {
    public static ProductView from(ProductResponseDto responseDto){
        String formattedPrice = String.format("%,d원", responseDto.price());

        return new ProductView(
                responseDto.id(),
                responseDto.name(),
                formattedPrice,
                responseDto.imageUrl(),
                responseDto.optionList()
        );
    }
}
