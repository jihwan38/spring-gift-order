package gift.wishlist.repository;


import gift.member.entity.Member;
import gift.product.entity.Product;
import gift.wishlist.entity.Wishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByMemberAndProduct(Member member, Product product);

    Page<Wishlist> findAllByMember(Member member, Pageable pageable);
}
