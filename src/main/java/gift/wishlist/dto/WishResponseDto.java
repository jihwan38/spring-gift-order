package gift.wishlist.dto;

import gift.wishlist.entity.Wishlist;

public record WishResponseDto(
        Long id,
        Long memberId,
        Long productId,
        String name,
        Long price,
        String imageUrl,
        int quantity
) {
    public static WishResponseDto from(Wishlist wishlist) {
        return new WishResponseDto(
                wishlist.getId(),
                wishlist.getMember().getId(),
                wishlist.getProduct().getId(),
                wishlist.getProduct().getName(),
                wishlist.getProduct().getPrice(),
                wishlist.getProduct().getImageUrl(),
                wishlist.getQuantity()
        );
    }
}
