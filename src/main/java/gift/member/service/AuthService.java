package gift.member.service;

import gift.exception.InvalidLoginException;
import gift.exception.MemberNotFoundByEmailException;
import gift.member.dto.request.LoginRequestDto;
import gift.member.dto.response.MemberResponseDto;
import gift.member.dto.request.RegisterRequestDto;
import gift.member.entity.Member;
import gift.member.token.TokenProvider;
import gift.member.util.PasswordUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final MemberService memberService;
    private final TokenProvider tokenProvider;
    private final PasswordUtil passwordUtil;

    public AuthService(MemberService memberService, TokenProvider tokenProvider, PasswordUtil passwordUtil) {
        this.memberService = memberService;
        this.tokenProvider = tokenProvider;
        this.passwordUtil = passwordUtil;
    }

    public String login(LoginRequestDto loginRequestDto) {
        try{
            Member member = memberService.getMemberByEmail(loginRequestDto.email());

            member.verifyPassword(loginRequestDto.password(), passwordUtil);

            return tokenProvider.generateToken(MemberResponseDto.from(member));

        }catch(IllegalArgumentException | MemberNotFoundByEmailException e){
            throw new InvalidLoginException();
        }
    }

    public String registerAndLogin(RegisterRequestDto registerRequestDto) {
        MemberResponseDto memberResponseDto = memberService.register(registerRequestDto);

        return tokenProvider.generateToken(memberResponseDto);
    }
}
