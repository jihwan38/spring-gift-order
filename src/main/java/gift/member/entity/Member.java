package gift.member.entity;

import gift.member.util.PasswordUtil;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String salt;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @ColumnDefault("USER")
    private String role;

    protected Member() {}

    public Member(Long id, String email, String salt, String password, String role) {
        this.id = id;
        this.email = email;
        this.salt = salt;
        this.password = password;
        this.role = (role == null) ? "USER" : role;
    }

    public Member(String email, String salt, String password, String role) {
        this.email = email;
        this.salt = salt;
        this.password = password;
        this.role = (role == null) ? "USER" : role;
    }

    public Long getId() {return id;}
    public String getEmail() {return email;}
    public String getSalt() {return salt;}
    public String getPassword() {return password;}
    public String getRole() {return role;}

    public void verifyPassword(String rawPassword, PasswordUtil passwordUtil) {
        String hashedPassword = passwordUtil.hashPassword(rawPassword, this.salt);

        if (!hashedPassword.equals(this.password)) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
    }

    public void updateMember(String email, String salt, String password, String role) {
        this.email = email;
        this.salt = salt;
        this.password = password;
        this.role = (role == null) ? "USER" : role;
    }
}
