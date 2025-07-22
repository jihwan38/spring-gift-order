package gift.member.controller;

import gift.member.dto.request.RegisterRequestDto;
import gift.member.dto.request.UpdateRequestDto;
import gift.member.dto.response.MemberResponseDto;
import gift.member.dto.view.MemberFormDto;
import gift.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/members")
public class MemberAdminController {

    private final MemberService memberService;

    public MemberAdminController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public String listMembers(Model model) {
        List<MemberResponseDto> members = memberService.getMembers();
        model.addAttribute("members", members);
        return "admin/members";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("member", MemberFormDto.emptyForm());
        return "admin/add-member-form";
    }

    @PostMapping("/add")
    public String addMember(@Valid @ModelAttribute("member") RegisterRequestDto requestDto,
                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/add-member-form";
        }

        memberService.register(requestDto);
        return "redirect:/admin/members";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        MemberResponseDto memberResponseDto = memberService.getMemberById(id);

        MemberFormDto memberFormDto = MemberFormDto.from(memberResponseDto);
        model.addAttribute("member", memberFormDto);
        model.addAttribute("memberId", id);
        return "admin/edit-member-form";
    }

    @PostMapping("/edit/{id}")
    public String updateMember(@PathVariable Long id,
                               @Valid @ModelAttribute("member") MemberFormDto memberFormDto,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("memberId", id);
            return "admin/edit-member-form";
        }
        UpdateRequestDto updateRequestDto = new UpdateRequestDto(
                memberFormDto.email(),
                memberFormDto.password(),
                memberFormDto.role()
        );
        memberService.updateMember(id, updateRequestDto);
        return "redirect:/admin/members";
    }

    @PostMapping("/delete/{id}")
    public String deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return "redirect:/admin/members";
    }
}