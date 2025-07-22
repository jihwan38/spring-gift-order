package gift.product.dto.request;

import jakarta.validation.constraints.*;

public record OptionRequestDto(
        @NotNull(message = "옵션의 이름을 입력해주세요.")
        @Size(max = 50, message = "옵션 이름은 최대 50자까지 입력 가능합니다." )
        @Pattern(
                regexp = "^[a-zA-Z0-9가-힣\\s()\\[\\]+\\-&/_]*$",
                message = "특수문자는 (), [], +, -, &, /, _ 만 가능합니다."
        )
        String name,

        @NotNull(message = "옵션의 수량을 입력해주세요.")
        @Min(value = 1, message = "수량은 1이상이어야 합니다.")
        @Max(value = 99_999_999, message = "수량은 1억개 미만까지 가능합니다.")
        Integer quantity
) {
}
