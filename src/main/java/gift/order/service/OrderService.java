package gift.order.service;


import gift.exception.ConcurrencyConflictException;
import gift.member.entity.Member;
import gift.order.dto.OrderRequestDto;
import gift.order.dto.OrderResponseDto;
import gift.order.entity.Order;
import gift.order.repository.OrderRepository;
import gift.product.entity.Option;
import gift.product.entity.Product;
import gift.product.repository.OptionRepository;
import gift.wishlist.repository.WishlistRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final WishlistRepository wishlistRepository;
    private final OptionRepository optionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository,
                        WishlistRepository wishlistRepository,
                        OptionRepository optionRepository,
                        ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.wishlistRepository = wishlistRepository;
        this.optionRepository = optionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 300)
    )
    public OrderResponseDto orderProduct(Member loginMember, OrderRequestDto orderRequestDto) {
        Option option = getOptionByOptionId(orderRequestDto.optionId());

        option.decreaseQuantity(orderRequestDto.quantity());

        wishlistRepository.findByMemberAndProduct(loginMember, option.getProduct())
                .ifPresent(wishlistRepository::delete);

        Order savedOrder = orderRepository.save(new Order(option, orderRequestDto.quantity(), orderRequestDto.message(), loginMember));

        eventPublisher.publishEvent(savedOrder);

        return OrderResponseDto.from(savedOrder);
    }

    @Recover
    public OrderResponseDto recover(ObjectOptimisticLockingFailureException e,
                        Member loginMember,
                        OrderRequestDto orderRequestDto) {
        throw new ConcurrencyConflictException("요청이 많아 실패했습니다.");
    }

    private Option getOptionByOptionId(Long optionId) {
        return optionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException(optionId + "에 해당하는 옵션을 찾지 못했습니다."));
    }
}
