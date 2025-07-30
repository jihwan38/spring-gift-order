package gift.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderRequestDto(
        @NotNull(message = "옵션의 id 값은 필수입니다.")
        Long optionId,

        @NotNull(message = "상품의 수량 값은 필수입니다.")
        Integer quantity,

        @NotBlank(message = "메시지 입력은 필수이며 공백 또한 불가능합니다.")
        String message
) {
}
