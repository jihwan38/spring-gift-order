package gift.kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoAgreeResponseDto(
        List<AgreeScopeInfo> agreeScopes) {
}
