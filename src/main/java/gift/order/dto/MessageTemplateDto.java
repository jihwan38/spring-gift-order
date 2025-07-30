package gift.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MessageTemplateDto(
        @JsonProperty("object_type")
        String objectType,

        @JsonProperty("text")
        String text,

        @JsonProperty("link")
        MessageLinkDto messageLinkDto
) {
}
