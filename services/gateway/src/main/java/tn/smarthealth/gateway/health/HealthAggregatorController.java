package tn.smarthealth.gateway.health;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class HealthAggregatorController {

    private final WebClient client = WebClient.create();
    private final Map<String, String> targets = new LinkedHashMap<>();

    public HealthAggregatorController(@Value("${services.auth-url}") String auth,
                                      @Value("${services.rest-url}") String rest,
                                      @Value("${services.soap-url}") String soap,
                                      @Value("${services.graphql-url}") String graphql,
                                      @Value("${services.telemetry-health-url}") String telemetry,
                                      @Value("${services.orchestrator-url}") String orchestrator) {
        targets.put("auth", auth);
        targets.put("rest", rest);
        targets.put("soap", soap);
        targets.put("graphql", graphql);
        targets.put("telemetry", telemetry);
        targets.put("orchestrator", orchestrator);
    }

    @GetMapping("/api/health")
    public Mono<ResponseEntity<Map<String, Object>>> health() {
        return Flux.fromIterable(targets.entrySet())
                .flatMap(e -> check(e.getKey(), e.getValue()))
                .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .map(results -> {
                    boolean allUp = results.values().stream().allMatch("UP"::equals);
                    Map<String, Object> body = new LinkedHashMap<>();
                    body.put("status", allUp ? "UP" : "DEGRADED");
                    body.put("services", new TreeMap<>(results));
                    return ResponseEntity.status(allUp ? 200 : 503).body(body);
                });
    }

    private Mono<Map.Entry<String, String>> check(String name, String baseUrl) {
        return client.get().uri(baseUrl + "/actuator/health")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() { })
                .map(m -> "UP".equals(m.get("status")) ? "UP" : "DOWN")
                .timeout(Duration.ofSeconds(2))
                .onErrorReturn("DOWN")
                .map(status -> Map.entry(name, status));
    }
}