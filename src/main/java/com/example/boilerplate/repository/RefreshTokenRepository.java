package com.example.boilerplate.repository;

import com.example.boilerplate.model.RefreshToken;
import com.example.boilerplate.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.List;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    List<RefreshToken> findByUserAndExpiresAtAfter(User user, Instant now);
    void deleteByExpiresAtBefore(Instant now);
}
