package gift.kakao.repository;

import gift.kakao.entity.UserKakaoToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserKakaoTokenRepository extends JpaRepository<UserKakaoToken, Long> {
}
