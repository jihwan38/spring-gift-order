package gift.exception;

public class EmailExistsException extends RuntimeException {
    public EmailExistsException() {
        super("이미 가입된 이메일입니다.");
    }
}
