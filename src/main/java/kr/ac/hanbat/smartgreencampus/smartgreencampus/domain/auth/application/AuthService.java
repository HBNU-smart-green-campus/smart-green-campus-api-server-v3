package kr.ac.hanbat.smartgreencampus.smartgreencampus.domain.auth.application;


import kr.ac.hanbat.smartgreencampus.smartgreencampus.domain.auth.web.dto.LoginRequest;
import kr.ac.hanbat.smartgreencampus.smartgreencampus.domain.auth.web.dto.SignupRequest;
import kr.ac.hanbat.smartgreencampus.smartgreencampus.domain.member.persistence.Member;
import kr.ac.hanbat.smartgreencampus.smartgreencampus.domain.member.persistence.MemberRepository;
import kr.ac.hanbat.smartgreencampus.smartgreencampus.global.config.bcrypt.EncryptHelper;
import kr.ac.hanbat.smartgreencampus.smartgreencampus.global.exception.illegal.IllegalValueException;
import kr.ac.hanbat.smartgreencampus.smartgreencampus.global.exception.duplicate.DuplicateMemberException;
import kr.ac.hanbat.smartgreencampus.smartgreencampus.global.exception.nullcheck.NullMemberException;
import kr.ac.hanbat.smartgreencampus.smartgreencampus.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final EncryptHelper encryptHelper;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Long signUpMember(final SignupRequest request) {

        final Member member = Member.builder()
                .email(request.email())
                .password(encryptHelper.encrypt(request.password1()))
                .build();

        validateDuplicateEmail(request.email());
        memberRepository.save(member);

        log.info(member.getEmail() + "님이 회원가입을 하셨습니다.");
        return member.getId();
    }


    private void validateDuplicateEmail(final String email) {
        final Optional<Member> member = memberRepository.findByEmail(email);

        if (member.isPresent()) {
            throw new DuplicateMemberException();
        }
    }


    /** 로그인 */
    public String loginMember(final LoginRequest request) {

        final Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(NullMemberException::new);

        if (!encryptHelper.isMatch(request.password(), member.getPassword())) {
            log.error("비밀번호가 일치하지 않습니다.");
            throw new IllegalValueException();
        }

        log.info(member.getEmail() + "님이 로그인 하셨습니다.");
        return jwtTokenProvider.createToken(member.getEmail());
    }
}
