package gift.wishlist.controller;

import gift.member.entity.Member;
import gift.member.token.LoginMember;
import gift.wishlist.dto.WishRequestDto;
import gift.wishlist.dto.WishResponseDto;
import gift.wishlist.service.WishlistService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PostMapping
    public ResponseEntity<WishResponseDto> addWishlist(
            @LoginMember Member member,
            @RequestBody WishRequestDto wishRequestDto) {
        WishResponseDto wishResponseDto =
                wishlistService.addWish(member, wishRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(wishResponseDto);
    }

    @GetMapping
    public ResponseEntity<Page<WishResponseDto>> getWishlist(
            @LoginMember Member member,
            @PageableDefault(sort = "id")
            Pageable pageable) {
        Page<WishResponseDto> wishResponseDto = wishlistService.getWishesByMember(member, pageable);

        return ResponseEntity.ok(wishResponseDto);
    }

    @DeleteMapping("/{wishId}")
    public ResponseEntity<Void> deleteWishlist(
            @LoginMember Member member,
            @PathVariable Long wishId) {
        wishlistService.deleteWish(member, wishId);

        return ResponseEntity.noContent().build();
    }
}
