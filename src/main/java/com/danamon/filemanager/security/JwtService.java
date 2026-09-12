package com.danamon.filemanager.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final long expiration;

    public JwtService(
            JwtEncoder jwtEncoder,
            JwtDecoder jwtDecoder,
            @Value("${jwt.expiration}") long expiration) {

        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.expiration = expiration;
    }

    public String generateToken(String username) {

        Instant now = Instant.now();

        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .subject(username)
                        .issuedAt(now)
                        .expiresAt(
                                now.plusMillis(expiration)
                        )
                        .claim("role", "ADMIN")
                        .build();
        JwsHeader header =
                JwsHeader.with(MacAlgorithm.HS256)
                        .build();

        return jwtEncoder
                .encode(
                        JwtEncoderParameters.from(
                                header,
                                claims
                        )
                )
                .getTokenValue();
    }

    public String extractUsername(String token) {
        return jwtDecoder
                .decode(token)
                .getSubject();
    }

    public boolean isValid(String token) {
        try {
            jwtDecoder.decode(token);
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
