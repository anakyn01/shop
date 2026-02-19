package com.hbk.service;

import com.hbk.dto.LoginRequest;
import com.hbk.dto.MemberRegisterRequest;
import com.hbk.entity.Member;
import com.hbk.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 + 자동 로그인
    public Member register(MemberRegisterRequest request, HttpSession session) {

        if (!request.getPassword().equals(request.getRepeatPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        Member member = Member.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .gender(request.getGender())
                .companyName(request.getCompanyName())
                .position(request.getPosition())
                .tel(request.getTel())
                .address(request.getAddress())
                .detailAddress(request.getDetailAddress())
                .createdAt(LocalDateTime.now())
                .build();

        Member saved = memberRepository.save(member);

        // 회원가입 즉시 로그인
        session.setAttribute("LOGIN_MEMBER_ID", saved.getId());
        session.setMaxInactiveInterval(60 * 60 * 48); // 48시간

        return saved;
    }

    // 로그인
    public void login(LoginRequest request, HttpSession session) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일이 존재하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        session.setAttribute("LOGIN_MEMBER_ID", member.getId());
        session.setMaxInactiveInterval(60 * 60 * 48);
        Long memberId = 1L; // 예시 (DB 조회 결과)
        session.setAttribute("LOGIN_MEMBER_ID", memberId);
    }

    // 로그아웃
    public void logout(HttpSession session) {
        session.invalidate();
    }
}
