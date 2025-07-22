package gift.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
      super("ID가 " + id + "인 상품은 없습니다.");
    }
}
