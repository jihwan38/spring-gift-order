package gift;

import gift.member.entity.Member;
import gift.member.repository.MemberRepository;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import gift.wishlist.entity.Wishlist;
import gift.wishlist.repository.WishlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;


import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = "spring.sql.init.mode=never")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class WishlistRepositoryTest {
    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;
    private Product product;


    @BeforeEach
    void setUp() {
        member = memberRepository.save(new Member("user@example.com", "salt", "password", "USER"));
        product = productRepository.save(new Product("테스트상품", 10000L, "http://image.url", Boolean.FALSE));
    }

    @Test
    void 새로운_위시리스트_등록(){
        var wish = new Wishlist(member, product, 3);

        var saved = wishlistRepository.save(wish);

        wishlistRepository.flush();
        var found = wishlistRepository.findById(saved.getId()).get();

        assertThat(found.getId()).isNotNull();
        assertThat(found.getMember()).isEqualTo(member);
        assertThat(found.getProduct()).isEqualTo(product);
        assertThat(found.getQuantity()).isEqualTo(3);
    }

    @Test
    void 기존_위시리스트_수량추가(){
        var wish = wishlistRepository.save(new Wishlist(member, product, 3));
        wish.addQuantity(3);

        var saved = wishlistRepository.save(wish);
        wishlistRepository.flush();
        var found = wishlistRepository.findById(saved.getId()).get();

        assertThat(found.getId()).isNotNull();
        assertThat(found.getMember()).isEqualTo(member);
        assertThat(found.getProduct()).isEqualTo(product);
        assertThat(found.getQuantity()).isEqualTo(6);
    }

    @Test
    void 위시리스트조회(){
        wishlistRepository.save(new Wishlist(member, product, 3));
        var pageable = PageRequest.of(0, 10);

        var wishlists = wishlistRepository.findAllByMember(member, pageable);

        assertThat(wishlists.getTotalElements()).isEqualTo(1);
        assertThat(wishlists.getContent().get(0).getProduct()).isEqualTo(product);
        assertThat(wishlists.getContent().get(0).getQuantity()).isEqualTo(3);
    }

    @Test
    void 위시리스트삭제(){
        var saved = wishlistRepository.save(new Wishlist(member, product, 3));
        var saveId = saved.getId();

        wishlistRepository.delete(saved);

        assertThat(wishlistRepository.findById(saveId)).isEmpty();
    }
}
