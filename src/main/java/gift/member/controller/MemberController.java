package gift.member.controller;


import gift.member.dto.request.RegisterRequestDto;
import gift.member.dto.request.UpdateRequestDto;
import gift.member.dto.response.MemberResponseDto;
import gift.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<MemberResponseDto> addMember(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        MemberResponseDto memberResponseDto = memberService.register(registerRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(memberResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<MemberResponseDto>> getMembers() {
        List<MemberResponseDto> members = memberService.getMembers();

        return ResponseEntity.ok(members);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponseDto> getMemberById(@PathVariable Long id) {
        MemberResponseDto member = memberService.getMemberById(id);

        return ResponseEntity.ok(member);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberResponseDto> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRequestDto updateRequestDto) {

        MemberResponseDto memberResponseDto = memberService.updateMember(id, updateRequestDto);

        return ResponseEntity.ok(memberResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);

        return ResponseEntity.noContent().build();
    }
}
