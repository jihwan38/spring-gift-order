package gift;

import gift.exception.ConcurrencyConflictException;
import gift.member.entity.Member;
import gift.order.dto.OrderRequestDto;
import gift.order.dto.OrderResponseDto;
import gift.order.entity.Order;
import gift.order.repository.OrderRepository;
import gift.order.service.KakaoMessageService;
import gift.order.service.OrderService;
import gift.product.entity.Option;
import gift.product.entity.Product;
import gift.product.repository.OptionRepository;
import gift.wishlist.repository.WishlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@SpringBootTest
@TestPropertySource(properties = {
        "kakao.client_id=test_client_id"
})
@EnableRetry
public class OrderServiceRetryTest {
    @Autowired
    private OrderService orderService;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private WishlistRepository wishlistRepository;

    @MockitoBean
    private OptionRepository optionRepository;

    @MockitoBean
    private KakaoMessageService kakaoMessageService;

    private Member member;
    private OrderRequestDto orderRequestDto;
    private Option mockOption;
    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product("테스트 상품", 5000L, "http://image.url", false);
        member = new Member("test@example.com", "test", "test", "USER");
        mockOption = mock(Option.class);
        lenient().when(mockOption.getProduct()).thenReturn(product);
        orderRequestDto = new OrderRequestDto(1L, 5, "test message");
    }

    @Test
    @DisplayName("첫 번째 시도 실패 후 두 번째 시도 성공하는 동시성 테스트")
    void orderProduct_Success_Concurrency(){
        //given
        when(mockOption.getId()).thenReturn(1L);
        when(optionRepository.findById(orderRequestDto.optionId())).thenReturn(Optional.of(mockOption));
        Order savedOrder = new Order(mockOption, 5, "test message", member);
        when(orderRepository.save(any(Order.class)))
                .thenThrow(new ObjectOptimisticLockingFailureException("", null))
                .thenReturn(savedOrder);

        //when
        OrderResponseDto response = orderService.orderProduct(member, orderRequestDto);

        //then
        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.optionId()).isEqualTo(orderRequestDto.optionId()),
                ()->assertThat(response.quantity()).isEqualTo(orderRequestDto.quantity()),
                ()->assertThat(response.message()).isEqualTo(orderRequestDto.message())
        );
        verify(optionRepository, times(2)).findById(orderRequestDto.optionId());
        verify(mockOption, times(2)).decreaseQuantity(orderRequestDto.quantity());
        verify(orderRepository, times(2)).save(any(Order.class));
        verify(kakaoMessageService, times(1)).sendMessage(any(Order.class));
    }

    @Test
    @DisplayName("recover 메서드 호출 테스트")
    void orderProduct_Recover(){
        when(mockOption.getId()).thenReturn(1L);
        when(optionRepository.findById(orderRequestDto.optionId())).thenReturn(Optional.of(mockOption));
        when(orderRepository.save(any(Order.class)))
                .thenThrow(new ObjectOptimisticLockingFailureException("", null))
                .thenThrow(new ObjectOptimisticLockingFailureException("", null))
                .thenThrow(new ObjectOptimisticLockingFailureException("", null));

        assertThrows(ConcurrencyConflictException.class,
                () -> orderService.orderProduct(member, orderRequestDto));
    }
}