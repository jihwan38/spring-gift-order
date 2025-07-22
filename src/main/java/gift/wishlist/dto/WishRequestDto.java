package gift.wishlist.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record WishRequestDto(
        @NotNull(message = "상품의 id를 입력해주세요.")
        Long productId,

        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
        int quantity
) {
}
