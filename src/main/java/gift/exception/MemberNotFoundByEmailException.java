package gift.exception;

public class MemberNotFoundByEmailException extends RuntimeException {
    public MemberNotFoundByEmailException(String email) {
        super(email +"에 해당하는 멤버가 없습니다.");
    }
}
