package com.career.careerlink.users.controller;

import com.career.careerlink.users.dto.LoginRequestDto;
import com.career.careerlink.users.dto.SignupRequestDto;
import com.career.careerlink.users.dto.TokenRequestDto;
import com.career.careerlink.users.dto.TokenResponse;
import com.career.careerlink.users.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/check-id")
    public ResponseEntity<Map<String, Boolean>> checkLoginId(@RequestParam String loginId) {
        boolean exists = userService.isLoginIdDuplicate(loginId);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupRequestDto dto) {
        userService.signup(dto);
        return ResponseEntity.ok().build();
    }

//    @PostMapping("/login")
//    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequestDto dto, HttpServletResponse response) {
//        return ResponseEntity.ok(userService.login(dto, response));
//    }
    /**
     * 변경 요약 / 사용 가이드
     *
     * 1) 컨트롤러 리턴
     *    - 굳이 ResponseEntity로 감싸지 말고, 도메인 객체(List/DTO/Page 등)만 그대로 반환.
     *    - GlobalResponseAdvice 가 자동으로 { header, body, (optional) pagination } 형식으로 래핑된다.
     *
     * 2) 상태코드 기본 규칙(컨트롤러가 별도 지정 안 했을 때)
     *    - GET    → 200 OK
     *    - POST   → 201 Created
     *    - PUT/PATCH → 200 OK
     *    - DELETE → body == null 이면 204 No Content, body 있으면 200 OK
     *
     * 3) ResponseEntity를 써야 하는 경우
     *    - 상태코드/헤더를 직접 지정해야 할 때만 ResponseEntity를 사용.
     *    - 이 경우에도 body는 동일하게 {header, body, pagination}으로 감싸져 내려간다.
     *    - 예) POST지만 200으로 내리고 싶다면:
     *        return ResponseEntity.ok(service.doSomething(...));
     *
     * 4) 문자열 응답
     *    - String을 리턴해도 기본적으로 JSON으로 래핑됨.
     *
     * 5) @SkipWrap (공통 래핑 건너뛰기)
     *    - 파일 다운로드, 외부 콜백 등 “바디 원문 그대로” 내려야 할 때만 사용.
     *    - 주의: SkipWrap을 쓰면 프런트 공통 인터셉터가 기대하는 포맷이 아닐 수 있다.
     *    - 예)
     *      @SkipWrapWW
     *      @GetMapping("/health")
     *      public String health() {
     *          return "OK";  // 래핑 없이 그대로 "OK"
     *      }
     *
     * 7) 요약
     *    - 기본은 “그냥 DTO 리턴” → Advice가 메시지/코드/페이지네이션까지 처리.
     *    - 특수한 상태/헤더가 필요하면 ResponseEntity.
     *    - 바디 원문 그대로 내려야 하면 @SkipWrap.
     */

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequestDto dto, HttpServletResponse response) {
        return userService.login(dto, response);
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(@CookieValue("refreshToken") String refreshToken,
                                                 @RequestHeader("Authorization") String accessToken,
                                                 HttpServletResponse response) {
        TokenRequestDto dto = new TokenRequestDto();
        dto.setAccessToken(accessToken);
        dto.setRefreshToken(refreshToken);
        return ResponseEntity.ok(userService.reissue(dto, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        userService.logout(token);
        return ResponseEntity.ok().build();
    }
}