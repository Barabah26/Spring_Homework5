package com.example.spring_homework5;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class JwtValidator {
    public static void main(String[] args) {
        String jwtString = "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiSmFuZSBEb2UiLCJlbWFpbCI6ImphbmVAZXhhbXBsZS5jb20iLCJzdWIiOiJqYW5lIiwianRpIjoiMzg5OWIxMTQtMDZlOC00M2QwLWEzMmQtZTg0YzZiNTE4MGRlIiwiaWF0IjoxNzE4MjkxMzIxLCJleHAiOjE3MTgyOTE2MjF9.0Ttwk-d1BhUBK-AhXmTA89sahdvqx-BiMhcvQE6vOos\n";

        String secret = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";

        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret),
                SignatureAlgorithm.HS256.getJcaName());

        Jws<Claims> jwt = Jwts.parserBuilder()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(jwtString);

        System.out.println(jwt);
    }
}
