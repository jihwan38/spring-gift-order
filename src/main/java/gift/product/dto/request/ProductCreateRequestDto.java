package gift.product.dto.request;


import gift.product.validator.KakaoInName;
import gift.product.validator.KakaoProductInfo;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@KakaoInName
public record ProductCreateRequestDto(
        @NotBlank(message = "상품 이름을 입력해 주세요.")
        @Size(max = 15, message = "상품 이름은 최대 15자까지 입력 가능합니다." )
        @Pattern(
                regexp = "^[a-zA-Z0-9가-힣\\s()\\[\\]+\\-&/_]*$",
                message = "특수문자는 (), [], +, -, &, /, _ 만 가능합니다."
        )
        String name,

        @NotNull(message = "가격을 입력해 주세요.")
        @Min(value = 0, message = "가격은 0원 이상부터 입력 가능합니다.")
        Long price,

        @NotBlank(message = "이미지 URL을 입력해 주세요.")
        @URL(message = "올바른 URL 형식이 아닙니다. 예시: http://....")
        String imageUrl,

        Boolean isKakaoApprovedByMd,

        @Size(min = 1, message= "옵션은 하나 이상이어야 합니다.")
        List<OptionRequestDto> optionRequestDtoList
        )implements KakaoProductInfo { }
