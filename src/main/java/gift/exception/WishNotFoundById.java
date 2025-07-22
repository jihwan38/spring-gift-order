package gift.exception;

public class WishNotFoundById extends RuntimeException {
    public WishNotFoundById(Long id) {
        super(id +"에 해당하는 위시를 찾을 수 없습니다.");
    }
}
