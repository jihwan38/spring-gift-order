package gift;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.exception.KakaoClientException;
import gift.exception.KakaoServerException;
import gift.kakao.entity.UserKakaoToken;
import gift.kakao.repository.UserKakaoTokenRepository;
import gift.kakao.service.KakaoLoginService;
import gift.member.entity.Member;
import gift.order.entity.Order;
import gift.order.service.KakaoMessageService;
import gift.product.entity.Option;
import gift.product.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClient;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@ExtendWith(MockitoExtension.class)
public class KakaoMessageServiceTest {

    private KakaoMessageService kakaoMessageService;

    @Mock
    private UserKakaoTokenRepository userKakaoTokenRepository;

    @Mock
    private KakaoLoginService kakaoLoginService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    private MockRestServiceServer mockServer;

    private Member member;
    private Order order;
    private final String KAKAO_TALK_URI = "https://kapi.kakao.com/v2/api/talk/memo/default/send";
    private final String TEST_ACCESS_TOKEN = "test-access-token";

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();

        mockServer = MockRestServiceServer.bindTo(restTemplate).build();

        RestClient restClient = RestClient.create(restTemplate);

        kakaoMessageService = new KakaoMessageService(KAKAO_TALK_URI, objectMapper, userKakaoTokenRepository, kakaoLoginService);

        try {
            java.lang.reflect.Field restClientField = KakaoMessageService.class.getDeclaredField("restClient");
            restClientField.setAccessible(true);
            restClientField.set(kakaoMessageService, restClient);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("RestClient Mock 주입에 실패했습니다.", e);
        }

        member = new Member(1L, "test@email.com", "salt","password", "USER", "LOCAL", "test@email.com");

        Product product = new Product("테스트 상품", 10000L, "http://image.url", null);
        Option option = new Option(100L, "테스트 옵션", 100, product);
        order = new Order(option, 5, "테스트 메시지", member);
    }

    @Test
    @DisplayName("예외를 던지지 않고 메시지 전송을 성공하는 테스트")
    void sendMessage_Success() {
        // given
        UserKakaoToken token = new UserKakaoToken(member.getId(), TEST_ACCESS_TOKEN, "refresh", null, null);
        when(userKakaoTokenRepository.findById(member.getId())).thenReturn(Optional.of(token));
        when(kakaoLoginService.checkTalkMessageAgree(token.getAccessToken())).thenReturn(true);
        mockServer.expect(requestTo(KAKAO_TALK_URI))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_ACCESS_TOKEN))
                .andExpect(content().contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(content().string(containsString("template_object=")))
                .andRespond(withSuccess());

        // when, then
        assertDoesNotThrow(() -> kakaoMessageService.sendMessage(order));
        mockServer.verify();
    }

    @Test
    @DisplayName("사용자가 메시지 수신에 동의하지 않을 경우 return 검증")
    void sendMessage_EarlyReturn_WhenNotAgreed() {
        // given
        UserKakaoToken token = new UserKakaoToken(member.getId(), TEST_ACCESS_TOKEN, "refresh", null, null);
        when(userKakaoTokenRepository.findById(member.getId())).thenReturn(Optional.of(token));
        when(kakaoLoginService.checkTalkMessageAgree(token.getAccessToken())).thenReturn(false);

        // when
        kakaoMessageService.sendMessage(order);

        // then
        mockServer.verify();
    }

    @Test
    @DisplayName("사용자 토큰 조회 실패 시 예외 발생")
    void sendMessage_Fail_WhenTokenNotFound() {
        // given
        when(userKakaoTokenRepository.findById(member.getId())).thenReturn(Optional.empty());

        // when, then
        assertThrows(IllegalArgumentException.class,
                () -> kakaoMessageService.sendMessage(order));
    }

    @Test
    @DisplayName("JSON 변환 실패 시 예외 발생")
    void sendMessage_Fail_WhenJsonProcessingFails() throws JsonProcessingException {
        // given
        UserKakaoToken token = new UserKakaoToken(member.getId(), TEST_ACCESS_TOKEN, "refresh", null, null);
        when(userKakaoTokenRepository.findById(member.getId())).thenReturn(Optional.of(token));
        when(kakaoLoginService.checkTalkMessageAgree(token.getAccessToken())).thenReturn(true);
        doThrow(JsonProcessingException.class).when(objectMapper).writeValueAsString(any());

        // when, then
        assertThrows(IllegalArgumentException.class,
                () -> kakaoMessageService.sendMessage(order));
    }

    @Test
    @DisplayName("카카오 API 4xx 에러 시 KakaoClientException 발생")
    void sendMessage_Fail_When4xxError() {
        // given
        UserKakaoToken token = new UserKakaoToken(member.getId(), TEST_ACCESS_TOKEN, "refresh", null, null);
        when(userKakaoTokenRepository.findById(member.getId())).thenReturn(Optional.of(token));
        when(kakaoLoginService.checkTalkMessageAgree(token.getAccessToken())).thenReturn(true);
        mockServer.expect(requestTo(KAKAO_TALK_URI))
                .andRespond(withBadRequest());

        // when, then
        assertThrows(KakaoClientException.class, () -> kakaoMessageService.sendMessage(order));
        mockServer.verify();
    }

    @Test
    @DisplayName("카카오 API 5xx 에러 시 KakaoServerException 발생")
    void sendMessage_Fail_When5xxError() {
        // given
        UserKakaoToken token = new UserKakaoToken(member.getId(), TEST_ACCESS_TOKEN, "refresh", null, null);
        when(userKakaoTokenRepository.findById(member.getId())).thenReturn(Optional.of(token));
        when(kakaoLoginService.checkTalkMessageAgree(token.getAccessToken())).thenReturn(true);
        mockServer.expect(requestTo(KAKAO_TALK_URI))
                .andRespond(withServerError());

        // when, then
        assertThrows(KakaoServerException.class,
                () -> kakaoMessageService.sendMessage(order));
        mockServer.verify();
    }
}
