package gift.member.dto.response;

import gift.member.entity.Member;

public record MemberResponseDto(
        Long id,
        String email,
        String role
) {
    public static MemberResponseDto from(Member member) {
        MemberResponseDto memberResponseDto = new MemberResponseDto(member.getId(), member.getEmail(), member.getRole());

        return memberResponseDto;
    }
}
