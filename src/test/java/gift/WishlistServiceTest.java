package gift;


import gift.exception.ProductNotFoundException;
import gift.member.entity.Member;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import gift.wishlist.dto.WishRequestDto;
import gift.wishlist.dto.WishResponseDto;
import gift.wishlist.entity.Wishlist;
import gift.wishlist.repository.WishlistRepository;
import gift.wishlist.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class WishlistServiceTest {
    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private WishlistService wishlistService;

    private Member member ;
    private Product product1;
    private Product product2;
    private Wishlist wish1;
    private Wishlist wish2;

    @BeforeEach
    void setUp() {
        member = new Member(1L, "user@example.com", "salt", "password", "USER");
        product1 = new Product(10L, "일반상품", 1000L, "http://image1.url", false);
        product2 = new Product(20L, "카카오상품", 2000L, "http://image2.url", true);
        wish1 = new Wishlist(1L, member, product1, 3);
        wish2 = new Wishlist(2L, member, product2, 1);
    }


    @Test
    void addWish_새로운위시등록후_Dto정상반환(){
        WishRequestDto request = new WishRequestDto(product1.getId(), 2);
        Wishlist savedWish = new Wishlist(1L, member, product1, request.quantity());

        given(productRepository.findById(request.productId()))
                .willReturn(Optional.of(product1));
        given(wishlistRepository.save(any(Wishlist.class))).willReturn(savedWish);

        WishResponseDto response = wishlistService.addWish(member, request);

        assertThat(response.id()).isEqualTo(savedWish.getId());
        assertThat(response.memberId()).isEqualTo(savedWish.getMember().getId());
        assertThat(response.productId()).isEqualTo(savedWish.getProduct().getId());
        assertThat(response.name()).isEqualTo(savedWish.getProduct().getName());
        assertThat(response.price()).isEqualTo(savedWish.getProduct().getPrice());
        assertThat(response.imageUrl()).isEqualTo(savedWish.getProduct().getImageUrl());
        assertThat(response.quantity()).isEqualTo(savedWish.getQuantity());
    }

    @Test
    void addWish_상품없음_예외발생(){
        WishRequestDto request = new WishRequestDto(100L, 2);
        given(productRepository.findById(request.productId()))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> wishlistService.addWish(member, request))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("100");
    }

    @Test
    void getWishesByMemberId_위시리스트가_비어있을_경우(){
        Pageable pageable = PageRequest.of(0, 10);
        given(wishlistRepository.findAllByMember(member, pageable))
                .willReturn(Page.empty());

        var response = wishlistService.getWishesByMember(member, pageable);

        assertThat(response).isEmpty();
    }

    @Test
    void getWishesByMemberId_위시리스트에_위시가_존재할_경우(){
        Pageable pageable = PageRequest.of(0, 10);

        List<Wishlist> wishContent = List.of(wish1, wish2);

        Page<Wishlist> wishPage = new PageImpl<>(wishContent, pageable, wishContent.size());

        given(wishlistRepository.findAllByMember(any(Member.class), any(Pageable.class)))
                .willReturn(wishPage);

        var response = wishlistService.getWishesByMember(member, pageable);

        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getContent().get(0).productId()).isEqualTo(wish1.getProduct().getId());
    }

    @Test
    void deleteWish_정상적으로_위시삭제(){
        given(wishlistRepository.findById(wish1.getId())).willReturn(Optional.of(wish1));
        willDoNothing().given(wishlistRepository)
                .delete(wish1);

        assertDoesNotThrow(() -> wishlistService.deleteWish(member, wish1.getId()));

    }

    @Test
    void delteWish_존재하지_않는_위시_삭제시_repository예외_그대로_전파(){
        Long wishId = wish1.getId();

        given(wishlistRepository.findById(wishId))
                .willReturn(Optional.of(wish1));

        willDoNothing().given(wishlistRepository).delete(wish1);

        wishlistService.deleteWish(member, wishId);

        verify(wishlistRepository, times(1)).findById(wishId);
        verify(wishlistRepository, times(1)).delete(wish1);
    }
}
