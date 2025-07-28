package gift.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MessageLinkDto(
        @JsonProperty("web_url")
        String webUrl,

        @JsonProperty("mobile_web_url")
        String mobileWebUrl
) {
}
