package gift.tool;

import gift.member.util.PasswordUtil;

import java.util.Scanner;

public class AdminGenerator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("생성할 관리자 계정의 이메일을 입력하세요: ");
        String email = scanner.nextLine();

        System.out.print("생성할 관리자 계정의 비밀번호를 입력하세요: ");
        String rawPassword = scanner.nextLine();

        scanner.close();

        PasswordUtil passwordUtil = new PasswordUtil();

        String salt = passwordUtil.getSalt();
        String hashedPassword = passwordUtil.hashPassword(rawPassword, salt);

        System.out.println("아래 INSERT 문을 복사하여 'data.sql' 파일에 붙여넣으세요.\n");

        String sql = String.format(
                "INSERT INTO member (email, salt, password, role) VALUES ('%s', '%s', '%s', 'ADMIN');",
                email,
                salt,
                hashedPassword
        );
        System.out.println(sql);
    }
}
