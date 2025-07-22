package gift.exception;

public class WishlistAccessDeniedException extends RuntimeException {
    public WishlistAccessDeniedException(Long wishId) {
        super(wishId + "번 위시리스트에 접근 권한이 없습니다.");
    }
}
