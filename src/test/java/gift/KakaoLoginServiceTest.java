package gift;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.exception.KakaoClientException;
import gift.exception.KakaoServerException;
import gift.kakao.dto.KakaoTokenResponseDto;
import gift.kakao.dto.KakaoUserInfoResponseDto;
import gift.kakao.repository.UserKakaoTokenRepository;
import gift.kakao.service.KakaoLoginService;
import gift.member.dto.response.MemberResponseDto;
import gift.member.dto.response.TokenResponseDto;
import gift.member.entity.Member;
import gift.member.repository.MemberRepository;
import gift.member.token.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@ExtendWith(MockitoExtension.class)
public class KakaoLoginServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private UserKakaoTokenRepository userKakaoTokenRepository;

    private MockRestServiceServer mockServer;
    private KakaoLoginService kakaoLoginService;
    private ObjectMapper objectMapper = new ObjectMapper();

    private final String CLIENT_ID = "test-client-id";
    private final String REDIRECT_URI = "http://localhost:8080/callback";
    private final String TOKEN_URI = "https://kauth.kakao.com/oauth/token";
    private final String USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";
    private final String CHECK_AGREE_URI = "https://kapi.kakao.com/v2/user/scopes";

    @BeforeEach
    void setUp() {
        RestClient.Builder restClientBuilder = RestClient.builder();

        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();

        kakaoLoginService = new TestableKakaoLoginService(
                CLIENT_ID,
                REDIRECT_URI,
                TOKEN_URI,
                USER_INFO_URI,
                CHECK_AGREE_URI,
                memberRepository,
                userKakaoTokenRepository,
                tokenProvider,
                restClientBuilder.build()
        );

        mockServer.reset();
    }

    @Test
    @DisplayName("신규 회원이 카카오로 로그인 시 회원가입 후 토큰을 반환한다")
    void loginWithKakao_NewUser_ReturnToken() throws Exception {
        String testCode = "test-authorization-code";
        String kakaoAccessToken = "fake-kakao-access-token";
        Long kakaoUserId = 12345L;
        String myJwtToken = "our-final-jwt-token";

        KakaoTokenResponseDto tokenResponse = new KakaoTokenResponseDto(
                "bearer",
                kakaoAccessToken,
                21599,
                "refresh-token",
                5183999,
                "account_email profile"
        );

        KakaoUserInfoResponseDto userInfoResponse = new KakaoUserInfoResponseDto(kakaoUserId);

        mockServer.expect(requestTo(TOKEN_URI))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andRespond(withSuccess(objectMapper.writeValueAsString(tokenResponse), MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo(USER_INFO_URI))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + kakaoAccessToken))
                .andRespond(withSuccess(objectMapper.writeValueAsString(userInfoResponse), MediaType.APPLICATION_JSON));

        when(memberRepository.findByProviderAndProviderId("KAKAO", kakaoUserId.toString()))
                .thenReturn(Optional.empty());

        Member newMember = new Member("KAKAO", kakaoUserId.toString());

        when(memberRepository.save(any(Member.class))).thenReturn(newMember);

        when(tokenProvider.generateToken(any(MemberResponseDto.class))).thenReturn(myJwtToken);

        TokenResponseDto result = kakaoLoginService.loginUsingKakao(testCode);

        assertThat(result).isNotNull();
        assertThat(result.token()).isEqualTo(myJwtToken);

        verify(memberRepository).save(any(Member.class));
        verify(tokenProvider).generateToken(any(MemberResponseDto.class));

        mockServer.verify();
    }

    @Test
    @DisplayName("기존 회원이 카카오로 로그인 시 DB 조회 후 토큰을 반환한다")
    void loginWithKakao_ExistingUser_ReturnToken() throws Exception {
        String testCode = "test-authorization-code";
        String kakaoAccessToken = "fake-kakao-access-token";
        Long kakaoUserId = 54321L;
        String myJwtToken = "our-final-jwt-token-for-existing-user";

        KakaoTokenResponseDto tokenResponse = new KakaoTokenResponseDto(
                "bearer",
                kakaoAccessToken,
                21599,
                "refresh-token",
                5183999,
                "account_email profile"
        );

        KakaoUserInfoResponseDto userInfoResponse = new KakaoUserInfoResponseDto(kakaoUserId);

        mockServer.expect(requestTo(TOKEN_URI))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(objectMapper.writeValueAsString(tokenResponse), MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo(USER_INFO_URI))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + kakaoAccessToken))
                .andRespond(withSuccess(objectMapper.writeValueAsString(userInfoResponse), MediaType.APPLICATION_JSON));

        Member existingMember = new Member("KAKAO", kakaoUserId.toString());
        when(memberRepository.findByProviderAndProviderId("KAKAO", kakaoUserId.toString()))
                .thenReturn(Optional.of(existingMember));

        when(tokenProvider.generateToken(any(MemberResponseDto.class))).thenReturn(myJwtToken);

        TokenResponseDto result = kakaoLoginService.loginUsingKakao(testCode);

        assertThat(result).isNotNull();
        assertThat(result.token()).isEqualTo(myJwtToken);

        verify(memberRepository, never()).save(any(Member.class));
        mockServer.verify();
    }

    @Test
    @DisplayName("카카오 토큰 발급 실패 시 예외가 발생한다")
    void loginWithKakao_TokenRequestFailed_ThrowException() {
        String testCode = "invalid-code";
        String errorResponse = """
            {
                "error": "invalid_grant",
                "error_description": "invalid_authorization_code"
            }
            """;

        mockServer.expect(requestTo(TOKEN_URI))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorResponse));

        assertThatThrownBy(() -> kakaoLoginService.loginUsingKakao(testCode))
                .isInstanceOf(KakaoClientException.class)
                .hasMessage("유효하지 않은 인가코드입니다.");

        mockServer.verify();
    }

    @Test
    @DisplayName("카카오 서버 에러 시 예외가 발생한다")
    void loginWithKakao_ServerError_ThrowException() {
        String testCode = "test-code";

        mockServer.expect(requestTo(TOKEN_URI))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError().contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> kakaoLoginService.loginUsingKakao(testCode))
                .isInstanceOf(KakaoServerException.class)
                .hasMessage("카카오 서버에 문제가 발생했습니다.");

        mockServer.verify();
    }

    @Test
    @DisplayName("카카오로부터 null 응답을 받으면 예외가 발생한다")
    void loginWithKakao_Null_ThrowException() throws Exception {
        String testCode = "test-code";

        KakaoTokenResponseDto nullTokenResponse = new KakaoTokenResponseDto(
                "bearer",
                null,
                21599,
                "refresh-token",
                5183999,
                "account_email profile"
        );

        mockServer.expect(requestTo(TOKEN_URI))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(objectMapper.writeValueAsString(nullTokenResponse), MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> kakaoLoginService.loginUsingKakao(testCode))
                .isInstanceOf(KakaoServerException.class)
                .hasMessage("카카오로부터 유효한 엑세스 토큰을 받지 못했습니다.");

        mockServer.verify();
    }

    private static class TestableKakaoLoginService extends KakaoLoginService {
        private final RestClient testRestClient;

        public TestableKakaoLoginService(
                String clientId,
                String redirectUri,
                String tokenUri,
                String userInfoUri,
                String checkAgreeUri,
                MemberRepository memberRepository,
                UserKakaoTokenRepository userKakaoTokenRepository,
                TokenProvider tokenProvider,
                RestClient restClient) {
            super(clientId, redirectUri, tokenUri, userInfoUri, checkAgreeUri, memberRepository, userKakaoTokenRepository, tokenProvider);
            this.testRestClient = restClient;

            try {
                var field = KakaoLoginService.class.getDeclaredField("restClient");
                field.setAccessible(true);
                field.set(this, testRestClient);
            } catch (Exception e) {
                throw new RuntimeException("테스트용 RestClient 주입에 실패하였습니다.", e);
            }
        }
    }
}