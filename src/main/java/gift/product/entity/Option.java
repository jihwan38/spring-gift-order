package gift.product.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "option")
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    protected Option() {}

    public Option(Long id, String name, int quantity, Product product) {
        validateQuantity(quantity);
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.product = product;
    }

    public Option(String name, int quantity) {
        this(null, name, quantity, null);
    }

    public Long getId() {return id;}
    public String getName() {return name;}
    public int getQuantity() {return quantity;}
    public Product getProduct() {return product;}

    public void decreaseQuantity(int quantity) {
        this.quantity -= quantity;
    }

    public void setProduct(Product product) {
        if(this.product != null) {
            this.product.getOptions().remove(this);
        }

        this.product = product;
    }

    public void updateOption(String name, int quantity){
        validateQuantity(quantity);
        this.name = name;
        this.quantity = quantity;
    }

    private void validateQuantity(int quantity) {
        if(quantity < 1) {
            throw new IllegalArgumentException("옵션의 최소 수량은 1개입니다.");
        }
    }
}
