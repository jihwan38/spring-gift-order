package gift.member.dto.view;

import gift.member.dto.response.MemberResponseDto;


public record MemberFormDto(
        String email,
        String password,
        String role
) {
    public static MemberFormDto emptyForm(){return new MemberFormDto("", "", "");}

    public static MemberFormDto from(MemberResponseDto memberResponseDto) {
        return new MemberFormDto(
                memberResponseDto.email(),
                "",
                memberResponseDto.role()
        );
    }
}
