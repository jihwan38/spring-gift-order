package gift.kakao.service;


import gift.exception.KakaoClientException;
import gift.exception.KakaoServerException;
import gift.kakao.dto.AgreeScopeInfo;
import gift.kakao.dto.KakaoAgreeResponseDto;
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
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class KakaoLoginService {
    private final String clientId;
    private final String redirectUri;
    private final String tokenUri;
    private final String userInfoUri;
    private final String checkAgreeUri;
    private final RestClient restClient;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    public KakaoLoginService(
            @Value("${kakao.client_id}") String clientId,
            @Value("${kakao.redirect_uri}")String redirectUri,
            @Value("${kakao.token_uri}") String tokenUri,
            @Value("${kakao.user_info_uri}")  String userInfoUri,
            @Value("${kakao.check_agree_uri}")String checkAgreeUri,
            MemberRepository memberRepository,
            TokenProvider tokenProvider) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.tokenUri = tokenUri;
        this.userInfoUri = userInfoUri;
        this.checkAgreeUri = checkAgreeUri;
        this.memberRepository = memberRepository;
        this.tokenProvider = tokenProvider;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(5000);

        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    @Transactional
    public TokenResponseDto loginUsingKakao(String code) {
        String accessToken = getAccessToken(code);

        KakaoUserInfoResponseDto userInfoResponseDto = getUserInfo(accessToken);

        Member member = registerOrLoginUser(userInfoResponseDto);

        String myAccessToken = tokenProvider.generateToken(MemberResponseDto.from(member));

        return new TokenResponseDto(myAccessToken);
    }

    public boolean checkTalkMessageAgree(String accessToken) {
        URI uri = UriComponentsBuilder.fromUriString(checkAgreeUri)
                .queryParam("scopes", "[\"talk_message\"]")
                .build()
                .toUri();

        KakaoAgreeResponseDto kakaoAgreeResponseDto = restClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer" + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    throw new KakaoClientException("유효하지 않은 access 토큰입니다.");
                }))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
                    throw new RuntimeException("카카오 서버에 문제가 발생했습니다.");
                }))
                .body(KakaoAgreeResponseDto.class);

        if(kakaoAgreeResponseDto == null || kakaoAgreeResponseDto.agreeScopes() == null) {
            throw new KakaoServerException("카카오로부터 메시지 수신 동의항목을 받아내지 못했습니다.");
        }

        return kakaoAgreeResponseDto.agreeScopes()
                .stream()
                .filter(scope -> "talk_message".equals(scope.id()))
                .findFirst()
                .map(AgreeScopeInfo::agreed)
                .orElse(false);
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
                    throw new KakaoClientException("유효하지 않은 인가코드입니다.");
                }))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
                    throw new KakaoServerException("카카오 서버에 문제가 발생했습니다.");
                }))
                .body(KakaoTokenResponseDto.class);

        if (kakaoTokenResponseDto == null || kakaoTokenResponseDto.accessToken() == null) {
            throw new KakaoServerException("카카오로부터 유효한 엑세스 토큰을 받지 못했습니다.");
        }

        return kakaoTokenResponseDto.accessToken();
    }

    private KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        KakaoUserInfoResponseDto kakaoUserInfoResponseDto = restClient.get()
                .uri(userInfoUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    throw new KakaoClientException("유요하지 않은 access 토큰입니다.");
                }))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
                    throw new RuntimeException("카카오 서버에 문제가 발생했습니다..");
                }))
                .body(KakaoUserInfoResponseDto.class);

        if(kakaoUserInfoResponseDto == null || kakaoUserInfoResponseDto.id() == null) {
            throw new KakaoServerException("카카오로부터 유저의 id를 받지 못했습니다.");
        }

        return kakaoUserInfoResponseDto;
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
