package gift;

import gift.product.entity.Option;
import gift.product.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;


public class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product("테스트 상품", 10000L, "image.jpg", true);
        product.addOption(new Option(1L, "옵션1", 10, null));
        product.addOption(new Option(2L, "옵션2", 20, null));
    }

    @Test
    @DisplayName("새로운 옵션을 성공적으로 추가")
    void addOption_Success() {
        Option newOption = new Option("새 옵션", 5);

        product.addOption(newOption);

        assertAll(
                () -> assertThat(product.getOptions()).hasSize(3),
                () -> assertThat(newOption.getProduct()).isEqualTo(product)
        );
    }

    @Test
    @DisplayName("중복된 이름의 옵션을 추가하면 예외가 발생")
    void addOption_Fail() {
        Option duplicateOption = new Option("옵션1", 10);

        assertThatThrownBy(() -> product.addOption(duplicateOption))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("옵션1는 이미 존재하는 옵션명입니다.");
    }

    @Test
    @DisplayName("옵션 수량을 감소 후 수량이 0이 되면 해당 옵션을 제거한다")
    void decreaseOptionQuantity_And_RemoveOption() {
        product.decreaseOptionQuantity(1L, 10);

        assertAll(
                () -> assertThat(product.getOptions()).hasSize(1),
                () -> assertThat(product.getOptions().get(0).getName()).isEqualTo("옵션2")
        );
    }

    @Test
    @DisplayName("옵션이 하나만 남았을 때 삭제를 시도하면 예외가 발생")
    void removeOption_Fail_LastOption() {
        product.removeOptionById(1L);

        assertAll(
                () -> assertThat(product.getOptions()).hasSize(1),
                () -> assertThatThrownBy(() -> product.removeOptionById(2L))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("상품의 옵션이 한 개이기 때문에 삭제가 불가능합니다.")
        );
    }
}