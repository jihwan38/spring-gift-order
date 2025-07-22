package gift.exception;

public class InvalidLoginException extends RuntimeException {
    public InvalidLoginException() {
        super("이메일 또는 비밀번호가 일치하지 않습니다.");
    }
}
