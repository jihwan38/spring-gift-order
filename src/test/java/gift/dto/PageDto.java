package gift.dto;

import java.util.List;

public record PageDto<T>(
        List<T> content,
        int number,
        int size,
        long totalElements) {
}
