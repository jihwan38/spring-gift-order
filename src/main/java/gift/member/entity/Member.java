package gift.member.entity;

import gift.member.util.PasswordUtil;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "member", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"provider", "provider_id"})
})

public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column
    private String salt;

    @Column
    private String password;

    @Column(nullable = false)
    @ColumnDefault("USER")
    private String role;

    @Column(nullable = false)
    private String provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;


    protected Member() {}

    public Member(Long id, String email, String salt, String password, String role, String provider, String providerId) {
        this.id = id;
        this.email = email;
        this.salt = salt;
        this.password = password;
        this.role = (role == null) ? "USER" : role;
        this.provider = provider;
        this.providerId = providerId;
    }

    public Member(String email, String salt, String password, String role) {
        this(null, email, salt, password, role, "LOCAL", email);
    }

    public Member(String provider, String providerId) {
        this(null, null, null, null, "USER", provider, providerId);
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
