package gift;

import gift.member.entity.Member;
import gift.order.dto.OrderRequestDto;
import gift.order.dto.OrderResponseDto;
import gift.order.entity.Order;
import gift.order.repository.OrderRepository;
import gift.order.service.OrderService;
import gift.product.entity.Option;
import gift.product.entity.Product;
import gift.product.repository.OptionRepository;
import gift.wishlist.entity.Wishlist;
import gift.wishlist.repository.WishlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private Option mockOption;

    private Product product;
    private Member member;
    private OrderRequestDto orderRequestDto;

    @BeforeEach
    void setUp() {
        product = new Product("테스트 상품", 5000L, "http://image.url", false);
        member = new Member("test@example.com", "test", "test", "USER");
        lenient().when(mockOption.getProduct()).thenReturn(product);
        orderRequestDto = new OrderRequestDto(1L, 5, "test message");
    }

    @Test
    @DisplayName("위시리스트에 상품이 존재할 경우 삭제 후 주문 성공")
    void orderProduct_Success_Wishlist(){
        //given
        Wishlist wishlist = new Wishlist(member, product, 5);
        Order savedOrder = new Order(mockOption, 5, "test message", member);
        when(mockOption.getId()).thenReturn(1L);
        when(optionRepository.findById(orderRequestDto.optionId())).thenReturn(Optional.of(mockOption));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(wishlistRepository.findByMemberAndProduct(member, product)).thenReturn(Optional.of(wishlist));
        //when
        OrderResponseDto response = orderService.orderProduct(member, orderRequestDto);
        //then
        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.optionId()).isEqualTo(orderRequestDto.optionId()),
                () -> assertThat(response.quantity()).isEqualTo(orderRequestDto.quantity()),
                () -> assertThat(response.message()).isEqualTo(orderRequestDto.message())
        );

        verify(mockOption, times(1)).decreaseQuantity(orderRequestDto.quantity());
        verify(wishlistRepository, times(1)).delete(wishlist);
        verify(eventPublisher, times(1)).publishEvent(any(Order.class));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("옵션 ID가 존재하지 않아 IllegalArgumentException 발생")
    void orderProduct_Invalid_Option(){
        when(optionRepository.findById(orderRequestDto.optionId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> orderService.orderProduct(member, orderRequestDto));
        verify(orderRepository, never()).save(any(Order.class));
    }

}
