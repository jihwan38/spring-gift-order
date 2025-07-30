package gift.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.exception.KakaoClientException;
import gift.exception.KakaoServerException;
import gift.kakao.repository.UserKakaoTokenRepository;
import gift.kakao.service.KakaoLoginService;
import gift.member.entity.Member;
import gift.order.dto.MessageLinkDto;
import gift.order.dto.MessageTemplateDto;
import gift.order.entity.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class KakaoMessageService {
    private final RestClient restClient;
    private final String talkUri;
    private final ObjectMapper objectMapper;
    private final UserKakaoTokenRepository userKakaoTokenRepository;
    private final KakaoLoginService kakaoLoginService;

    public KakaoMessageService(@Value("${kakao.talk_uri}") String talkUri,
                               ObjectMapper objectMapper,
                               UserKakaoTokenRepository userKakaoTokenRepository, KakaoLoginService kakaoLoginService) {
        this.talkUri = talkUri;
        this.objectMapper = objectMapper;
        this.userKakaoTokenRepository = userKakaoTokenRepository;
        this.kakaoLoginService = kakaoLoginService;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(3000);

        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendMessage(Order order){
        Member member = order.getMember();

        String accessToken = userKakaoTokenRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalArgumentException("카카오 엑세스 토큰 조회에 실패하였습니다."))
                .getAccessToken();

        if (!kakaoLoginService.checkTalkMessageAgree(accessToken)) {
            return;
        }

        String text = createText(order);

        MessageLinkDto messageLinkDto = new MessageLinkDto("http://spring-product-web.com",
                "http://m.spring-product-web.com");

        MessageTemplateDto messageTemplateDto = new MessageTemplateDto("text", text, messageLinkDto);

        String jsonTemplate;

        try {
            jsonTemplate = objectMapper.writeValueAsString(messageTemplateDto);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("카카오 메시지 생성에 실패하였습니다.");
        }

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("template_object", jsonTemplate);

        restClient.post()
                .uri(talkUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    throw new KakaoClientException("카카오에 잘못된 템플릿을 전달하였습니다.");
                }))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
                    throw new KakaoServerException("카카오 서버에 문제가 발생했습니다.");
                }))
                .toBodilessEntity();
    }

    private String createText(Order order){
        return String.format("""
                [주문내역]
                주문번호: %d
                상품명: %s
                옵션명: %s
                수량: %d
                메시지: %s
                """,
                order.getId(),
                order.getOption().getProduct().getName(),
                order.getOption().getName(),
                order.getQuantity(),
                order.getMessage());
    }
}
