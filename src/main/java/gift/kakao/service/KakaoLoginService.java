package gift.kakao.service;


import gift.kakao.dto.KakaoTokenResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class KakaoLoginService {
    private final String clientId;
    private final String tokenUri;
    private final String userInfoUri;
    private final RestClient restClient;

    public KakaoLoginService(
            @Value("${kakao.client_id}") String clientId,
            @Value("${kakao.token_uri}") String tokenUri,
            @Value("${kakao.user_info_uri}")  String userInfoUri) {
                this.clientId = clientId;
                this.tokenUri = tokenUri;
                this.userInfoUri = userInfoUri;
                this.restClient = RestClient.create();
    }


    private String getAccessToken(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", tokenUri);
        params.add("code", code);

        KakaoTokenResponseDto kakaoTokenResponseDto = restClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    throw new RuntimeException("클라이언트 오류가 발생했습니다.");
                }))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
                    throw new RuntimeException("서버 오류가 발생했습니다.");
                }))
                .body(KakaoTokenResponseDto.class);

        return kakaoTokenResponseDto.accessToken();
    }
}
