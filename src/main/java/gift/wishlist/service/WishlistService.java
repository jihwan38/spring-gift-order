package gift.wishlist.service;


import gift.exception.ProductNotFoundException;
import gift.exception.WishNotFoundById;
import gift.exception.WishlistAccessDeniedException;
import gift.member.entity.Member;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import gift.wishlist.dto.WishRequestDto;
import gift.wishlist.dto.WishResponseDto;
import gift.wishlist.entity.Wishlist;
import gift.wishlist.repository.WishlistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    public WishlistService(WishlistRepository wishlistRepository, ProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
    }

    public WishResponseDto addWish(Member member, WishRequestDto wishRequestDto) {
        Product product = productRepository.findById(wishRequestDto.productId())
                .orElseThrow(() -> new ProductNotFoundException(wishRequestDto.productId()));

        Wishlist wishlist = wishlistRepository.findByMemberAndProduct(member, product)
                .map(wish -> {
                    wish.addQuantity(wishRequestDto.quantity());
                    return wish;
                })
                .orElseGet(() -> {
                    return wishlistRepository.save(new Wishlist(member, product, wishRequestDto.quantity()));
                });

        return WishResponseDto.from(wishlist);
    }

    @Transactional(readOnly = true)
    public Page<WishResponseDto> getWishesByMember(Member member, Pageable pageable) {
        return wishlistRepository.findAllByMember(member, pageable)
                .map(WishResponseDto::from);
    }

    public void deleteWish(Member member, Long wishId) {
        Wishlist wishlist = wishlistRepository.findById(wishId)
                        .orElseThrow(() -> new WishNotFoundById(wishId));

        if(!wishlist.isOwner(member)) {
            throw new WishlistAccessDeniedException(wishId);
        }

        wishlistRepository.delete(wishlist);
    }
}
