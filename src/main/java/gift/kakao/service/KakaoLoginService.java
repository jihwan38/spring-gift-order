package gift.kakao.service;


import gift.kakao.dto.KakaoTokenResponseDto;
import gift.kakao.dto.KakaoUserInfoResponseDto;
import gift.member.dto.response.MemberResponseDto;
import gift.member.dto.response.TokenResponseDto;
import gift.member.entity.Member;
import gift.member.repository.MemberRepository;
import gift.member.token.TokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class KakaoLoginService {
    private final String clientId;
    private final String redirectUri;
    private final String tokenUri;
    private final String userInfoUri;
    private final RestClient restClient;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    public KakaoLoginService(
            @Value("${kakao.client_id}") String clientId,
            @Value("${kakao.redirect_uri}")String redirectUri,
            @Value("${kakao.token_uri}") String tokenUri,
            @Value("${kakao.user_info_uri}")  String userInfoUri, MemberRepository memberRepository, TokenProvider tokenProvider) {
                this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.tokenUri = tokenUri;
                this.userInfoUri = userInfoUri;
        this.memberRepository = memberRepository;
        this.restClient = RestClient.create();
        this.tokenProvider = tokenProvider;
    }

    public TokenResponseDto loginUsingKakao(String code) {
        String accessToken = getAccessToken(code);

        KakaoUserInfoResponseDto userInfoResponseDto = getUserInfo(accessToken);

        Member member = registerOrLoginUser(userInfoResponseDto);

        String myAccessToken = tokenProvider.generateToken(MemberResponseDto.from(member));

        return new TokenResponseDto(myAccessToken);
    }


    private String getAccessToken(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        KakaoTokenResponseDto kakaoTokenResponseDto = restClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    throw new RuntimeException("인가코드를 사용한 조회에 실패했습니다." + response.getStatusCode());
                }))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
                    throw new RuntimeException("카카오 서버에 문제가 생겼습니다." + response.getStatusCode());
                }))
                .body(KakaoTokenResponseDto.class);

        if (kakaoTokenResponseDto == null || kakaoTokenResponseDto.accessToken() == null) {
            throw new RuntimeException("카카오 토큰 응답이 비어있거나 액세스 토큰이 없습니다.");
        }

        return kakaoTokenResponseDto.accessToken();
    }

    private KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        return restClient.get()
                .uri(userInfoUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    throw new RuntimeException("카카오 사용자 정보 조회를 실패했습니다." + response.getStatusCode());
                }))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
                    throw new RuntimeException("카카오 서버에 문제가 생겼습니다." + response.getStatusCode());
                }))
                .body(KakaoUserInfoResponseDto.class);
    }

    private Member registerOrLoginUser(KakaoUserInfoResponseDto userInfoResponseDto) {
        String provider = "KAKAO";
        String providerId = userInfoResponseDto.id().toString();

        return memberRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    Member newMember = new Member(provider, providerId);
                    return memberRepository.save(newMember);
                });
    }
}
