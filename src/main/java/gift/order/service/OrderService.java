package gift.order.service;

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

    public OrderService(OrderRepository orderRepository, WishlistRepository wishlistRepository, ProductRepository productRepository, OptionRepository optionRepository) {
        this.orderRepository = orderRepository;
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
        this.optionRepository = optionRepository;
    }

    @Transactional
    public OrderResponseDto orderProduct(Member loginMember, OrderRequestDto orderRequestDto) {
        Option option = getOptionByOptionId(orderRequestDto.optionId());

        Product product = option.getProduct();

        product.decreaseOptionQuantity(option, orderRequestDto.quantity());

        wishlistRepository.findByMemberAndProduct(loginMember, product)
                .ifPresent(wishlistRepository::delete);

        Order newOrder = new Order(option, orderRequestDto.quantity(), orderRequestDto.message(), loginMember);

        return OrderResponseDto.from(orderRepository.save(newOrder));
    }

    private Option getOptionByOptionId(Long optionId) {
        return optionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException(optionId + "에 해당하는 옵션을 찾지 못했습니다."));
    }
}
