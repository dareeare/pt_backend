package medicalcenter.authservice.service;

import lombok.RequiredArgsConstructor;
import medicalcenter.authservice.exception.NotFoundException;
import medicalcenter.authservice.exception.TokenExpiredException;
import medicalcenter.authservice.model.entity.RefreshToken;
import medicalcenter.authservice.model.entity.User;
import medicalcenter.authservice.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository tokenRepository;
    private final JwtService jwtService;

    @Transactional
    public RefreshToken create(User user) {
        if (tokenRepository.findByUser(user).isPresent()) {
            tokenRepository.deleteByUser(user);
        }

        String token = jwtService.generateRefreshToken(user);
        RefreshToken refreshToken = new RefreshToken(
                null, token, user, LocalDateTime.now().plus(jwtService.getRefreshExpirationTime(), ChronoUnit.MILLIS)
        );

        return tokenRepository.save(refreshToken);
    }

    public RefreshToken findByToken(String token) {
        return tokenRepository
                .findByToken(token)
                .orElseThrow(() -> new NotFoundException("Failed to retrieve a token"));
    }

    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.deleteById(token.getId());
            throw new TokenExpiredException("Token is expired. Make a new signin request.");
        }
        return token;
    }
}
