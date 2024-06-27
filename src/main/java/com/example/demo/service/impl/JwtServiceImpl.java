package com.example.demo.service.impl;

import com.example.demo.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

//    @Value("${jwt.axpiryTime}")
    private static final long axpiryTime = 86400000;

//    @Value("${jwt.secretKey}")
    private static final String secretKey = "1cadea0f581198a8cabcb34f54450a2834bd140dadd7dd797f0642aadad3a7cb";

    private static final long expiryHouse = 1;

    private static final long expiryDay = 14;


    @Override
    public String generatedToken(UserDetails userDetails) {
//        xử lý token
        return generateTokenString(new HashMap<>(), userDetails);
    }

    @Override
    public String extractToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String generatedRefreshToken(UserDetails userDetails) {
        return generateRefreshTokenString(new HashMap<>(), userDetails);
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractToken(token);

        return username.equals(userDetails.getUsername());
    }

    //    claims: thông tin bí mật, email, phone, không cho hiển thị dưới token
    private String generateTokenString(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis())) // ngày tạo token
                .setExpiration(new Date(System.currentTimeMillis() + axpiryTime)) //thời gian hết hạn
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    private String generateRefreshTokenString(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis())) // ngày tạo token
                .setExpiration(new Date(System.currentTimeMillis() + axpiryTime * expiryDay)) //thời gian hết hạn
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsTFunction){
        final Claims claims = extraAllClaim(token);
        return claimsTFunction.apply(claims);
    }

    private Claims extraAllClaim(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


}
