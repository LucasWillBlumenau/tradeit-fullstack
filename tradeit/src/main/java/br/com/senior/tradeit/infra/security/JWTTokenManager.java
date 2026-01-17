package br.com.senior.tradeit.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class JWTTokenManager {

    private static final int TWO_HOURS_IN_SECONDS = 2 * 60 * 60;
    private static final String ISSUER = "TradeIt API";
    private static final String SUBJECT = "User Details";

    @Value("${jwt-secret}")
    private String secret;

    public String createToken(String username) {
        Instant expiresAt = Instant.now()
                .plusSeconds(TWO_HOURS_IN_SECONDS);

        return JWT.create()
                .withSubject(SUBJECT)
                .withClaim("username", username)
                .withExpiresAt(expiresAt)
                .withIssuer(ISSUER)
                .sign(Algorithm.HMAC256(secret));
    }

    public String validateToken(String token) {
        if (token == null) {
            return null;
        }

        var verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject(SUBJECT)
                .withIssuer(ISSUER)
                .build();

        try {
            return verifier.verify(token)
                    .getClaim("username")
                    .asString();
        } catch (JWTVerificationException e) {
            return null;
        }

    }

}
