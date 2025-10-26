package medicalcenter.authservice.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalcenter.authservice.exception.AlreadyExistsException;
import medicalcenter.authservice.exception.NotFoundException;
import medicalcenter.authservice.model.RoleEnum;
import medicalcenter.authservice.model.dto.JwtResponse;
import medicalcenter.authservice.model.dto.LoginRequest;
import medicalcenter.authservice.model.dto.RefreshTokenRequest;
import medicalcenter.authservice.model.dto.RegisterUserDto;
import medicalcenter.authservice.model.dto.VerifyUserDto;
import medicalcenter.authservice.model.entity.RefreshToken;
import medicalcenter.authservice.model.entity.Role;
import medicalcenter.authservice.model.entity.User;
import medicalcenter.authservice.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final EmailService emailService;

    public void register(RegisterUserDto request) {
        log.trace("register method in AuthService");
        if (userRepository.existsByPhone(request.phone())) {
            String message = "User with email '%s' already exisits".formatted(request.phone());
            log.error(message);
            throw new AlreadyExistsException(message);
        }

        Role role = new Role(1L, RoleEnum.ROLE_DOCTOR, List.of());

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phone(request.phone())
                .email(request.email())
                .birthDate(request.birthDate())
                .password(passwordEncoder.encode(request.password()))
                .avatarUrl(request.avatarUrl())
                .role(role)
                .isEmailVerified(false)

                .build();

        user.setVerificationCode(generateVerificationCode());
        user.setVerificationExpiration(LocalDateTime.now().plusMinutes(15));
        user.setIsActive(false);
        sendVerificationEmail(user);
        userRepository.save(user);
    }

    public JwtResponse login(LoginRequest request) {
        log.trace("login method in AuthService");
        User user = userRepository.findByPhone(request.phone())
                .orElseThrow(() -> new NotFoundException("Failed to retrieve user"));

        if (!user.getIsActive()) {
            String message = "Account not verified. Please verify it.";
            log.error(message);
            throw new RuntimeException(message);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.phone(), request.password())
        );

        user = (User) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.create(user);

        return new JwtResponse(accessToken, refreshToken.getToken());
    }
    
    public JwtResponse refresh(RefreshTokenRequest request) {
        log.trace("refresh method in AuthService");
        RefreshToken refreshToken = refreshTokenService.findByToken(request.token());
        refreshToken = refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();
        String accessToken = jwtService.generateAccessToken(user);
        refreshToken = refreshTokenService.create(user);
        return new JwtResponse(accessToken, refreshToken.getToken());
    }

    public void verifyUser(VerifyUserDto input) {
        log.trace("verifyUser method in AuthService");
        Optional<User> optional = userRepository.findByEmail(input.email());
        if (optional.isPresent()) {
            User user = optional.get();
            if (user.getVerificationExpiration().isBefore(LocalDateTime.now())) {
                String message = "Verification code has expired";
                log.error(message);
                throw new RuntimeException(message);
            }
            if (user.getVerificationCode().equals(input.verificationCode())) {
                user.setIsActive(true);
                user.setVerificationCode(null);
                user.setVerificationExpiration(null);
                userRepository.save(user);
            } else {
                String message = "Invalid verification code.";
                log.error(message);
                throw new RuntimeException(message);
            }
        } else {
            String message = "User not found.";
            log.error(message);
            throw new RuntimeException(message);
        }
    }

    public void resendVerificationCode(String email) {
        log.trace("refreshVerificationCode method");
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isEnabled()) {
                String message = "Account is already verified";
                log.error(message);
                throw new RuntimeException(message);
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationExpiration(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            String message = "User not found";
            log.error(message);
            throw new RuntimeException(message);
        }
    }

    private void sendVerificationEmail(User user) {
        log.trace("Sending verification email");
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            String message = "Failed to send a message";
            log.error(message);
            throw new RuntimeException(message, e);
        }
    }
    
    private String generateVerificationCode() {
        log.trace("generating verification code");
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
