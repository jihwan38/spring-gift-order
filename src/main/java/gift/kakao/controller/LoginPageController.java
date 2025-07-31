package gift.kakao.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.UriComponentsBuilder;


@Controller
public class LoginPageController {
    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    @Value("${kakao.oauth_uri}")
    private String kakaoOauthUri;

    @GetMapping("/kakao/login")
    public String login(Model model){
        String location = UriComponentsBuilder
                .fromUriString(kakaoOauthUri)
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .toUriString();

        model.addAttribute("location", location);

        return "kakao/login";
    }
}
