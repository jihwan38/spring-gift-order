package gift.member.controller;

import gift.member.dto.request.LoginRequestDto;
import gift.member.dto.request.RegisterRequestDto;
import gift.member.dto.response.TokenResponseDto;
import gift.member.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
public class MemberAuthController {
    private final AuthService authService;

    public MemberAuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody  RegisterRequestDto registerRequestDto) {
        String token = authService.registerAndLogin(registerRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new TokenResponseDto(token));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> loginM(
            @Valid @RequestBody LoginRequestDto loginRequestDto) {
        String token = authService.login(loginRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(new TokenResponseDto(token));
    }
}
