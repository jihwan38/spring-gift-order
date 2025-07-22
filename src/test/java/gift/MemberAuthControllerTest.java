package gift;

import gift.member.dto.request.LoginRequestDto;
import gift.member.dto.request.RegisterRequestDto;
import gift.member.dto.response.TokenResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Sql("/test.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberAuthControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private RestClient.Builder builder;

    private RestClient client;

    @BeforeEach
    void setUp() {
        this.client = builder.baseUrl("http://localhost:" + port).build();
    }

    @Test
    void 회원가입_성공() {
        var requestDto = new RegisterRequestDto("jihwan@example.com", "jihwan13");


        var response = client.post()
                .uri("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestDto)
                .retrieve()
                .toEntity(TokenResponseDto.class);


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().token()).isNotBlank();
    }

    @Test
    void 중복된_이메일_때문에_회원가입_실패() {
        var requestDto = new RegisterRequestDto("admin@daum.net", "password");

        var response = client.post()
                .uri("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {})
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void 로그인_성공() {
        var requestDto = new LoginRequestDto("admin@daum.net", "123");

        var response = client.post()
                .uri("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestDto)
                .retrieve()
                .toEntity(Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("token")).isNotNull();
    }
    @Test
    void 이메일_때문에_로그인_실패() {
        var requestDto = new LoginRequestDto("notuser@example.com", "123456789");


        var response = client.post()
                .uri("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {})
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isEqualTo("이메일 또는 비밀번호가 일치하지 않습니다.");
    }

    @Test
    void 로그인_실패_잘못된_비밀번호() {
        var requestDto = new LoginRequestDto("admin@daum.net", "wrong_password");

        var response = client.post()
                .uri("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {})
                .toEntity(String.class);


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isEqualTo("이메일 또는 비밀번호가 일치하지 않습니다.");
    }
}


