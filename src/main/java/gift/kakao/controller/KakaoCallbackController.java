package gift.kakao.controller;

import gift.kakao.service.KakaoLoginService;
import gift.member.dto.response.TokenResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KakaoCallbackController {
    private final KakaoLoginService kakaoLoginService;

    public KakaoCallbackController(KakaoLoginService kakaoLoginService) {
        this.kakaoLoginService = kakaoLoginService;
    }

    @GetMapping("/")
    public ResponseEntity<String> handleKakaoCallback(
            @RequestParam("code") String code) {
        TokenResponseDto tokenResponseDto = kakaoLoginService.loginUsingKakao(code);

        return ResponseEntity.ok().body(tokenResponseDto.toString());
    }
}
