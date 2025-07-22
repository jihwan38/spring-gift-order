package gift;

import gift.member.entity.Member;
import gift.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = "spring.sql.init.mode=never")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void 회원저장(){
        var member = new Member("user@example.com", "salt", "password", null);
        assertThat(member.getId()).isNull();

        var actual = memberRepository.save(member);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getEmail()).isEqualTo("user@example.com");
        assertThat(actual.getSalt()).isEqualTo("salt");
        assertThat(actual.getPassword()).isEqualTo("password");
        assertThat(actual.getRole()).isEqualTo("USER");
    }

    @Test
    void 모든_회원조회(){
        memberRepository.save(new Member("user1@example.com", "salt1", "password1", null));
        memberRepository.save(new Member("user2@example.com", "salt2", "password2", null));
        memberRepository.save(new Member("user3@example.com", "salt3", "password3", null));

        var actual = memberRepository.findAll();

        assertThat(actual.size()).isEqualTo(3);
        assertThat(actual).extracting(Member::getEmail)
                .containsExactlyInAnyOrder("user1@example.com", "user2@example.com", "user3@example.com");
    }

    @Test
    void 단일_회원조회(){
        var saved = memberRepository.save(new Member("user1@example.com", "salt1", "password1", null));

        var actual = memberRepository.findById(saved.getId()).get();

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getEmail()).isEqualTo("user1@example.com");
        assertThat(actual.getSalt()).isEqualTo("salt1");
        assertThat(actual.getPassword()).isEqualTo("password1");
        assertThat(actual.getRole()).isEqualTo("USER");
    }

    @Test
    void 회원수정(){
        var saved = memberRepository.save(new Member("user1@example.com", "salt1", "password1", null));

        saved.updateMember("user2@example.com", "salt2", "password2", "ADMIN");

        testEntityManager.flush();
        var actual = memberRepository.findById(saved.getId()).get();

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getEmail()).isEqualTo("user2@example.com");
        assertThat(actual.getSalt()).isEqualTo("salt2");
        assertThat(actual.getPassword()).isEqualTo("password2");
        assertThat(actual.getRole()).isEqualTo("ADMIN");
    }

    @Test
    void 회원삭제(){
        var saved = memberRepository.save(new Member("user1@example.com", "salt1", "password1", null));

        memberRepository.deleteById(saved.getId());
        var actual = memberRepository.findById(saved.getId());

        assertThat(actual).isEmpty();
    }
}
