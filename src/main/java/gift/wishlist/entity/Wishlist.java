package gift.wishlist.entity;

import gift.member.entity.Member;
import gift.product.entity.Product;
import jakarta.persistence.*;

@Entity
@Table(name = "wishlist")
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    protected Wishlist() {}

    public Wishlist(Long id, Member member, Product product, int quantity) {
        this.id = id;
        this.member = member;
        this.product = product;
        this.quantity = quantity;
    }

    public Wishlist(Member member, Product product, int quantity) {
        this(null, member, product, quantity);
    }

    public Long getId() {return this.id;}
    public Member getMember() {return this.member;}
    public Product getProduct() {return this.product;}
    public int getQuantity() {return this.quantity;}

    public void addQuantity(int quantity) {this.quantity += quantity;}

    public boolean isOwner(Member member) {
        return this.member.equals(member);
    }
}
