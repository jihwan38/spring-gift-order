package gift.member.util;


import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class PasswordUtil {
    public String getSalt(){
        SecureRandom radom = new SecureRandom();
        byte[] salt = new byte[16];
        radom.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public String hashPassword(String password, String salt){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] digest = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(digest);
        } catch(NoSuchAlgorithmException e){
            throw new RuntimeException("비밀번호 해싱 오류", e);
        }
    }
}
