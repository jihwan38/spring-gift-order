package gift.kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AgreeScopeInfo(
        String id,
        Boolean agreed
) {
}
