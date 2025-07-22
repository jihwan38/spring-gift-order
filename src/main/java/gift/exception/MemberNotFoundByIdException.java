package gift.exception;

public class MemberNotFoundByIdException extends RuntimeException {
    public MemberNotFoundByIdException(Long id) {
        super(id + "에 해당하는 멤버가 없습니다.");
    }
}
