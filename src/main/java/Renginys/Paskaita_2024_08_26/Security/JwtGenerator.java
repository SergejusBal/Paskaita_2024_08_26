package Renginys.Paskaita_2024_08_26.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

public class JwtGenerator {

    private static final String SECRET_KEY = "sdfsadfsadfasdfasdfasdfasdfasdfasdf";

    public static String generateJwt(int userId) {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + 120 * 60 * 1000); // 120 minutes

        String jwt = Jwts.builder()
                .setIssuer("manokompanija.eu")
                .setSubject("manokompanija.eu")
                .claim("UserId", userId + "")
                .claim("DateOfLogin", new java.text.SimpleDateFormat("yyyy-MM-dd").format(now))
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return jwt;
    }
}