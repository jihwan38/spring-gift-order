package gift.member.token;

import gift.member.dto.response.MemberResponseDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenProvider {
    private final SecretKey secretKey;

    public TokenProvider(@Value("${jwt.secret.key}") String secretValue) {
        this.secretKey = Keys.hmacShaKeyFor(secretValue.getBytes());
    }

    public String generateToken(MemberResponseDto memberResponseDto) {
        Date now = new Date();

        return Jwts.builder()
                .setSubject(memberResponseDto.id().toString())
                .claim("member", memberResponseDto.email())
                .claim("role", memberResponseDto.role())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 1000 * 60 * 60))
                .signWith(secretKey)
                .compact();
    }

    public boolean isValidToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            return true;
        }catch(Exception e){
            return false;
        }
    }

    public Long getMemberIdFromToken(String token){
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.parseLong(claims.getSubject());
    }

    public String getRoleFromToken(String token){
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("role").toString();
    }
}
