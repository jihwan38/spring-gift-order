package gift.kakao;

import gift.member.dto.response.MemberResponseDto;
import gift.member.entity.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KakaoCallbackController {
    @GetMapping("/")
    public ResponseEntity<String> handleKakaoCallback(
            @RequestParam("code") String code) {
        return ResponseEntity.ok("인가 코드: " + code);
    }
}
