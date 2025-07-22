package gift;



import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = "spring.sql.init.mode=never")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void 상품_저장(){
        var product = new Product("새로운 상품", 1000L, "http://image.url", null);
        assertThat(product.getId()).isNull();
        var actual = productRepository.save(product);
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo("새로운 상품");
        assertThat(actual.getPrice()).isEqualTo(1000L);
        assertThat(actual.getImageUrl()).isEqualTo("http://image.url");
        assertThat(actual.getIsKakaoApprovedByMd()).isEqualTo(false);
    }

    @Test
    void 상품조회(){
        var product = new Product("새로운 상품", 1000L, "http://image.url", null);
        var saved = productRepository.save(product);
        var actual = productRepository.findById(saved.getId()).get();
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo("새로운 상품");
        assertThat(actual.getPrice()).isEqualTo(1000L);
        assertThat(actual.getImageUrl()).isEqualTo("http://image.url");
        assertThat(actual.getIsKakaoApprovedByMd()).isEqualTo(false);
    }

    @Test
    void 모든_상품조회(){
       productRepository.save(new Product("상품1", 1000L, "http://image.url", null));
       productRepository.save(new Product("상품2", 2000L, "http://image.url", null));
       productRepository.save(new Product("상품3", 3000L, "http://image.url", null));

       var actual = productRepository.findAll();

       assertThat(actual.size()).isEqualTo(3);
       assertThat(actual).extracting(Product::getName)
               .containsExactlyInAnyOrder("상품1", "상품2", "상품3");
    }

    @Test
    void 상품수정(){
        var saved = productRepository.save(new Product("상품1", 1000L, "http://image.url", null));

        var product = productRepository.findById(saved.getId()).get();
        product.updateProduct("카카오상품", 2000L, "http://update_image.url", true);
        entityManager.flush();

        var actual = productRepository.findById(saved.getId()).get();
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo("카카오상품");
        assertThat(actual.getPrice()).isEqualTo(2000L);
        assertThat(actual.getImageUrl()).isEqualTo("http://update_image.url");
        assertThat(actual.getIsKakaoApprovedByMd()).isEqualTo(true);
    }

    @Test
    void 상품삭제(){
        var saved = productRepository.save(new Product("상품1", 1000L, "http://image.url", null));

        productRepository.deleteById(saved.getId());

        var actual = productRepository.findById(saved.getId());

        assertThat(actual).isEmpty();
    }

}
