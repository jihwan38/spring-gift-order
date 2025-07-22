package gift.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
        @NotBlank(message = "이메일을 입력하세요.")
        @Email(message = "이메일 형식에 맞지 않습니다.")
        @Size(max = 200, message = "이메일은 최대 200자입니다.")
        String email,

        @NotBlank(message = "비밀번호를 입력하세요.")
        @Size(max = 200, message = "비밀번호는 최대 200자입니다.")
        String password
) {
}
