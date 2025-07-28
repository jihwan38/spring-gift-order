package gift.kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoAgreeResponseDto(
        @JsonProperty("scopes")
        List<AgreeScopeInfo> agreeScopes) {
}
