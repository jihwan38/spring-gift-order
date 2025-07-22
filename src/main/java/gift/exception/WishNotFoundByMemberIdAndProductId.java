package gift.exception;

public class WishNotFoundByMemberIdAndProductId extends RuntimeException {
    public WishNotFoundByMemberIdAndProductId(Long memberId, Long productId) {
      super(memberId + ": " + productId + "에 해당하는 위시리스트가 존재하지 않습니다.");
    }
}
