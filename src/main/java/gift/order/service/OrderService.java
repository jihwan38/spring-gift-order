package gift.order.service;

import gift.kakao.service.KakaoLoginService;
import gift.member.entity.Member;
import gift.order.dto.OrderRequestDto;
import gift.order.dto.OrderResponseDto;
import gift.order.entity.Order;
import gift.order.repository.OrderRepository;
import gift.product.entity.Option;
import gift.product.entity.Product;
import gift.product.repository.OptionRepository;
import gift.product.repository.ProductRepository;
import gift.wishlist.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final OptionRepository optionRepository;
    private final KakaoMessageService kakaoMessageService;
    private final KakaoLoginService kakaoLoginService;

    public OrderService(OrderRepository orderRepository, WishlistRepository wishlistRepository, ProductRepository productRepository, OptionRepository optionRepository, KakaoMessageService kakaoMessageService, KakaoLoginService kakaoLoginService) {
        this.orderRepository = orderRepository;
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
        this.optionRepository = optionRepository;
        this.kakaoMessageService = kakaoMessageService;
        this.kakaoLoginService = kakaoLoginService;
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
