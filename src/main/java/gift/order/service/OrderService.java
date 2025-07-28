package gift.order.service;


import gift.member.entity.Member;
import gift.order.dto.OrderRequestDto;
import gift.order.dto.OrderResponseDto;
import gift.order.entity.Order;
import gift.order.repository.OrderRepository;
import gift.product.entity.Option;
import gift.product.entity.Product;
import gift.product.repository.OptionRepository;
import gift.wishlist.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final WishlistRepository wishlistRepository;

    private final OptionRepository optionRepository;
    private final KakaoMessageService kakaoMessageService;


    public OrderService(OrderRepository orderRepository, WishlistRepository wishlistRepository, OptionRepository optionRepository, KakaoMessageService kakaoMessageService) {
        this.orderRepository = orderRepository;
        this.wishlistRepository = wishlistRepository;
        this.optionRepository = optionRepository;
        this.kakaoMessageService = kakaoMessageService;
    }

    @Transactional
    public OrderResponseDto orderProduct(Member loginMember, OrderRequestDto orderRequestDto) {
        Option option = getOptionByOptionId(orderRequestDto.optionId());

        Product product = option.getProduct();

        product.decreaseOptionQuantity(option, orderRequestDto.quantity());

        wishlistRepository.findByMemberAndProduct(loginMember, product)
                .ifPresent(wishlistRepository::delete);

        Order savedOrder = orderRepository.save(new Order(option, orderRequestDto.quantity(), orderRequestDto.message(), loginMember));



        kakaoMessageService.sendMessage(savedOrder);

        return OrderResponseDto.from(savedOrder);
    }

    private Option getOptionByOptionId(Long optionId) {
        return optionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException(optionId + "에 해당하는 옵션을 찾지 못했습니다."));
    }
}
