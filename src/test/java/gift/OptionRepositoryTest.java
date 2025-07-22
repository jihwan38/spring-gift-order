package gift;


import gift.product.entity.Option;
import gift.product.repository.OptionRepository;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@TestPropertySource(properties = "spring.sql.init.mode=never")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class OptionRepositoryTest {
    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private ProductRepository productRepository;

    private Product savedProduct;

    @BeforeEach
    void setUp() {
        Product product = new Product("테스트 상품", 1000L, "http://image.url", false);
        savedProduct = productRepository.save(product);
    }

    @Test
    @DisplayName("새로운 옵션 성공적으로 저장")
    void saveOption() {
        Option option = new Option("옵션1", 50);
        option.setProduct(savedProduct);

        Option savedOption = optionRepository.save(option);

        assertAll(
                () -> assertThat(savedOption).isNotNull(),
                () -> assertThat(savedOption.getId()).isNotNull(),
                () -> assertThat(savedOption.getName()).isEqualTo("옵션1"),
                () -> assertThat(savedOption.getQuantity()).isEqualTo(50)
        );
    }

    @Test
    @DisplayName("상품의 옵션 성공적으로 조회")
    void findAllOption() {
        Option option1 = new Option("옵션1", 10);
        Option option2 = new Option("옵션2", 20);
        Option option3 = new Option("옵션3", 30);

        option1.setProduct(savedProduct);
        option2.setProduct(savedProduct);
        option3.setProduct(savedProduct);

        optionRepository.save(option1);
        optionRepository.save(option2);
        optionRepository.save(option3);

        List<Option> options = optionRepository.findAll();

        assertAll(
                () -> assertThat(options).hasSize(3),
                () -> assertThat(options).contains(option1, option2, option3)
        );
    }

    @Test
    @DisplayName("옵션 정보를 성공적으로 수정")
    void updateOption() {
        Option option = new Option("수정 전 옵션", 100);
        option.setProduct(savedProduct);
        Option savedOption = optionRepository.save(option);

        savedOption.updateOption("수정 후 옵션", 50);
        Option updatedOption = optionRepository.findById(savedOption.getId()).get();

        assertAll(
                () -> assertThat(updatedOption.getName()).isEqualTo("수정 후 옵션"),
                () -> assertThat(updatedOption.getQuantity()).isEqualTo(50)
        );
    }

    @Test
    @DisplayName("옵션을 성공적으로 삭제")
    void deleteOption() {
        Option option = new Option("옵션", 100);
        option.setProduct(savedProduct);
        Option savedOption = optionRepository.save(option);

        optionRepository.deleteById(savedOption.getId());

        assertThat(optionRepository.findById(savedOption.getId())).isEmpty();
    }
}
