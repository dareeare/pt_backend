package medicalcenter.authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalcenter.authservice.model.dto.JwtResponse;
import medicalcenter.authservice.model.dto.LoginRequest;
import medicalcenter.authservice.model.dto.RefreshTokenRequest;
import medicalcenter.authservice.model.dto.RegisterUserDto;
import medicalcenter.authservice.model.dto.VerifyUserDto;
import medicalcenter.authservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterUserDto dto) {
        log.trace("register method in auth controller");
        authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        log.trace("login method in auth controller");
        JwtResponse login = authService.login(request);
        return ResponseEntity.ok(login);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.trace("refresh method in auth controller");
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@Valid @RequestBody VerifyUserDto dto) {
        log.trace("verify method in auth controller");
        try {
            authService.verifyUser(dto);
            return ResponseEntity.ok("Account verified successfully.");
        } catch (RuntimeException ex) {
            log.error(ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resend(@RequestParam String email) {
        log.trace("resend method in auth controller");
        try {
            boolean resent = authService.resendVerificationCode(email);
            String message = resent ? "Verification code resent successfully" : "Account is already verified";
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
