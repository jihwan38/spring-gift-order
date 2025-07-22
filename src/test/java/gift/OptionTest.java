package gift;

import gift.product.entity.Option;
import gift.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class OptionTest {
    @Test
    @DisplayName("옵션의 이름과 수량 변경")
    void updateOption() {
        Option option = new Option("기존 이름", 100);

        option.updateOption("새로운 이름", 20);

        assertAll(
                () -> assertThat(option.getName()).isEqualTo("새로운 이름"),
                () -> assertThat(option.getQuantity()).isEqualTo(20)
        );
    }

    @Test
    @DisplayName("옵션의 상품 소속 변경")
    void changeProduct(){
        Product product1 = new Product("상품1", 1000L, "http://image.url", false);
        Product product2 = new Product("상품2", 2000L, "http://image.url", false);
        Option option = new Option("옵션", 10);

        product1.addOption(option);

        option.setProduct(product2);

        assertAll(
                () -> assertThat(option.getProduct()).isEqualTo(product2),
                () -> assertThat(product1.getOptions()).doesNotContain(option)
        );
    }
}
