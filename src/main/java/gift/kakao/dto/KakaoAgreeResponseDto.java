package gift.kakao.dto;

import java.util.List;

public record KakaoAgreeResponseDto(
        List<AgreeScopeInfo> agreeScopes) {
}
