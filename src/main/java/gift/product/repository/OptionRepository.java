package gift.product.repository;

import gift.product.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
    @Modifying
    @Query("DELETE FROM Option o WHERE o.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);
}
