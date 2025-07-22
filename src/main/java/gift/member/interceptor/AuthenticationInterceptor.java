package gift.member.interceptor;

import gift.member.token.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    private final TokenProvider tokenProvider;

    public AuthenticationInterceptor(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("Authorization 헤더가 없습니다.");
            return false;
        }

        String token = authHeader.substring(7);

        if(!tokenProvider.isValidToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("유효하지 않은 토큰입니다.");
            return false;
        }

        Long memberId = tokenProvider.getMemberIdFromToken(token);
        request.setAttribute("memberId", memberId);
        return true;
    }
}
