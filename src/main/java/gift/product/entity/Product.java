package gift.product.entity;


import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "is_kakao_approved_by_md", nullable = false)
    @ColumnDefault(value = "FALSE")
    private Boolean isKakaoApprovedByMd;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();

    protected Product() {
    }

    public Product(Long id, String name, Long price, String imageUrl, Boolean isKakaoApprovedByMd) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isKakaoApprovedByMd = (isKakaoApprovedByMd == null) ? false : isKakaoApprovedByMd;
    }

    public Product(String name, Long price, String imageUrl, Boolean isKakaoApprovedByMd) {
        this(null, name, price, imageUrl, isKakaoApprovedByMd);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Boolean getIsKakaoApprovedByMd() {
        return isKakaoApprovedByMd;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void updateProduct(String name, Long price, String imageUrl, Boolean isKakaoApprovedByMd) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isKakaoApprovedByMd = (isKakaoApprovedByMd == null) ? false : isKakaoApprovedByMd;
    }

    public void addOption(Option option) {
        validateOptionForAdd(option.getName());
        this.options.add(option);
        option.setProduct(this);
    }

    public void validateOptionForUpdate(String name, Long optionId) {
        checkDuplicateOptionName(name, optionId);
    }

    public void decreaseOptionQuantity(Long optionId, int quantity) {
        Option option = getOptionByOptionId(optionId);

        option.decreaseQuantity(quantity);

        if(option.getQuantity() <= 0){
            this.removeOption(option);
        }
    }

    public void removeOptionByOptionId(Long optionId) {
        Option option = getOptionByOptionId(optionId);

        removeOption(option);
    }

    private void removeOption(Option option) {
        if (this.options.size() <= 1) {
            throw new IllegalArgumentException("상품의 옵션이 한 개이기 때문에 삭제가 불가능합니다.");
        }

        this.options.remove(option);
    }

    public Option getOptionByOptionId(Long optionId) {
        return this.options.stream()
                .filter(option -> option.getId().equals(optionId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(optionId + "에 해당하는 옵션을 찾을 수 없습니다."));
    }

    private void validateOptionForAdd(String name) {
        checkDuplicateOptionName(name, null);
    }

    private void checkDuplicateOptionName(String name, Long optionId) {
        Stream<Option> stream = this.options.stream();

        if (optionId != null) {
            stream = stream.filter(option -> !option.getId().equals(optionId));
        }

        boolean isDuplicated = stream.anyMatch(
                option -> option.getName().equals(name));

        if (isDuplicated) {
            throw new IllegalArgumentException(name + "는 이미 존재하는 옵션명입니다.");
        }
    }
}
