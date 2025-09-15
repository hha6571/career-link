package com.career.careerlink.employers.member.contorller;

import com.career.careerlink.employers.member.dto.EmployerMemberDto;
import com.career.careerlink.employers.member.dto.EmployerMemberSearchRequest;
import com.career.careerlink.employers.member.dto.EmployerSignupDto;
import com.career.careerlink.employers.member.service.EmpMemberService;
import com.career.careerlink.global.response.SkipWrap;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/emp")
@RequiredArgsConstructor
@Validated
public class EmpMemberController {

    private final EmpMemberService empMemberService;

    /**
     * 기업회원가입
     * @param dto
     */
    @PostMapping("/signup")
    public void empSignup(@RequestBody EmployerSignupDto dto) {
        empMemberService.empSignup(dto);
    }

    /**
     * 기업회원 리스트 조회
     */
    @GetMapping("/members")
    public Page<EmployerMemberDto> getEmployerMembers(EmployerMemberSearchRequest req , Principal principal) {
        String employerUserId = principal.getName();
        return empMemberService.getEmployerMembers(req, employerUserId);
    }

    /**
     * 기업회원 승인처리 (단건)
     */
    @SkipWrap
    @PostMapping("/members/{targetEmployerUserId}/approve")
    public int approveOne(@PathVariable @NotBlank String targetEmployerUserId, Principal principal) {
        String employerUserId = principal.getName();
        return empMemberService.approveOne(targetEmployerUserId, employerUserId);
    }

    /**
     * 기업회원 승인처리 (다건)
     */
    @SkipWrap
    @PostMapping("/members/approve-bulk")
    public int approveBulk(@RequestBody List<String> targetEmployerUserIds, Principal principal) {
        String employerUserId = principal.getName();
        return empMemberService.approveBulk(targetEmployerUserIds, employerUserId);
    }

}