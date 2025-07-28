package gift.order.controller;

import gift.member.entity.Member;
import gift.member.token.LoginMember;
import gift.order.dto.OrderRequestDto;
import gift.order.dto.OrderResponseDto;
import gift.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> order(
            @LoginMember Member loginMember,
            @RequestBody OrderRequestDto orderRequestDto) {
        OrderResponseDto orderResponseDto = orderService.orderProduct(loginMember, orderRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponseDto);
    }
}
