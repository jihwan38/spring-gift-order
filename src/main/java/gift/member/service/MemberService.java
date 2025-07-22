package gift.member.service;

import gift.exception.EmailExistsException;
import gift.exception.MemberNotFoundByEmailException;
import gift.exception.MemberNotFoundByIdException;
import gift.member.dto.request.UpdateRequestDto;
import gift.member.dto.response.MemberResponseDto;
import gift.member.dto.request.RegisterRequestDto;
import gift.member.entity.Member;
import gift.member.repository.MemberRepository;
import gift.member.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordUtil passwordUtil;

    public MemberService(MemberRepository memberRepository, PasswordUtil passwordUtil) {
        this.memberRepository = memberRepository;
        this.passwordUtil = passwordUtil;
    }

    public MemberResponseDto register(RegisterRequestDto registerRequestDto) {
        if(memberRepository.findByEmail(registerRequestDto.email()).isPresent()) {
            throw new EmailExistsException();
        }

        String salt = passwordUtil.getSalt();
        String hashedPassword = passwordUtil.hashPassword(registerRequestDto.password(), salt);

        Member member = new Member(
                registerRequestDto.email(),
                salt,
                hashedPassword,
                "USER");

        return MemberResponseDto.from(memberRepository.save(member));
    }

    @Transactional(readOnly = true)
    public List<MemberResponseDto> getMembers() {
        return memberRepository.findAll()
                .stream()
                .map(MemberResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public MemberResponseDto getMemberById(Long id) {
        Member member = findById(id);

        return MemberResponseDto.from(member);
    }

    @Transactional(readOnly = true)
    public Member getMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundByEmailException(email));

        return member;
    }

    public MemberResponseDto updateMember(Long id, UpdateRequestDto updateRequestDto) {
        Member member = findById(id);

        memberRepository.findByEmail(updateRequestDto.email())
                .filter(foundMember -> !foundMember.getId().equals(id))
                .ifPresent(m -> {
                    throw new EmailExistsException();
                });

        String newPassword = updateRequestDto.password();
        String salt = member.getSalt();
        String hashedPassword = member.getPassword();

        if(newPassword != null && !newPassword.isBlank()){
            salt = passwordUtil.getSalt();
            hashedPassword = passwordUtil.hashPassword(newPassword, salt);
        }

        member.updateMember(
                updateRequestDto.email(),
                salt,
                hashedPassword,
                updateRequestDto.role());

        return MemberResponseDto.from(member);
    }

    public void deleteMember(Long id) {
        if(!memberRepository.existsById(id)) {
            throw new MemberNotFoundByIdException(id);
        }

        memberRepository.deleteById(id);
    }

    private Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundByIdException(id));
    }
}
