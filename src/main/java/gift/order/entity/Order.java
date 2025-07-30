package gift.order.entity;

import gift.member.entity.Member;
import gift.product.entity.Option;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private Option option;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "order_date_time", nullable = false)
    private LocalDateTime orderDateTime;

    @Column(name = "message", nullable = false)
    String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    Member member;

    protected Order() {
    }

    public Order(Long id,
                 Option option,
                 int quantity,
                 LocalDateTime orderDateTime,
                 String message,
                 Member member) {
        this.id = id;
        this.option = option;
        this.quantity = quantity;
        this.orderDateTime = orderDateTime;
        this.message = message;
        this.member = member;
    }

    public Order(Option option,
                 int quantity,
                 String message,
                 Member member) {
        this(null, option, quantity, null, message, member);
    }

    @PrePersist
    protected void onCreate() {
        orderDateTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Option getOption() {
        return option;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public String getMessage() {
        return message;
    }

    public Member getMember() {
        return member;
    }
}
