package tn.smarthealth.gateway.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String ISSUER = "smart-healthcare-auth";
    private static final String[] IDENTITY_HEADERS = {"X-User-Name", "X-User-Role", "X-Patient-Id"};

    private final MACVerifier verifier;
    private final AccessPolicy policy;

    public JwtAuthenticationFilter(@Value("${jwt.secret}") String secret, AccessPolicy policy)
            throws JOSEException {
        byte[] key = secret.getBytes(StandardCharsets.UTF_8);
        if (key.length < 32) {
            throw new IllegalStateException("JWT_SECRET must be at least 32 characters long");
        }
        this.verifier = new MACVerifier(key);
        this.policy = policy;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // CORS preflight and public paths pass through (identity headers are still cleaned)
        if (HttpMethod.OPTIONS.equals(request.getMethod()) || policy.isPublic(path)) {
            return chain.filter(withIdentity(exchange, null));
        }

        JWTClaimsSet claims = verify(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
        if (claims == null) {
            return reject(exchange, HttpStatus.UNAUTHORIZED, "Missing or invalid token");
        }
        String role = (String) claims.getClaim("role");
        String patientId = (String) claims.getClaim("patientId");
        if (!policy.isAllowed(path, role, patientId)) {
            return reject(exchange, HttpStatus.FORBIDDEN, "Role not allowed for this resource");
        }
        return chain.filter(withIdentity(exchange, claims));
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private JWTClaimsSet verify(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        try {
            SignedJWT jwt = SignedJWT.parse(authorization.substring(7).trim());
            if (!JWSAlgorithm.HS256.equals(jwt.getHeader().getAlgorithm()) || !jwt.verify(verifier)) {
                return null;
            }
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            Date exp = claims.getExpirationTime();
            if (exp == null || exp.before(new Date())
                    || !ISSUER.equals(claims.getIssuer())
                    || claims.getClaim("role") == null) {
                return null;
            }
            return claims;
        } catch (ParseException | JOSEException e) {
            return null;
        }
    }

    // Removes any identity header sent by the client (anti-spoofing), then sets them from the token
    private ServerWebExchange withIdentity(ServerWebExchange exchange, JWTClaimsSet claims) {
        ServerHttpRequest mutated = exchange.getRequest().mutate().headers(headers -> {
            for (String name : IDENTITY_HEADERS) {
                headers.remove(name);
            }
            if (claims != null) {
                headers.set("X-User-Name", claims.getSubject());
                headers.set("X-User-Role", (String) claims.getClaim("role"));
                Object patientId = claims.getClaim("patientId");
                if (patientId != null) {
                    headers.set("X-Patient-Id", patientId.toString());
                }
            }
        }).build();
        return exchange.mutate().request(mutated).build();
    }

    // Same error body shape as auth-service: {status, message, timestamp}
    private Mono<Void> reject(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"status\":" + status.value() + ",\"message\":\"" + message
                + "\",\"timestamp\":\"" + LocalDateTime.now() + "\"}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}