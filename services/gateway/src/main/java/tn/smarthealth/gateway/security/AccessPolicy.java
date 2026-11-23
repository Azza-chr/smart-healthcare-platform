package tn.smarthealth.gateway.security;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class AccessPolicy {

    private static final Set<String> ALL = Set.of("PATIENT", "MEDECIN", "INFIRMIER", "ADMIN");
    private static final Set<String> STAFF = Set.of("MEDECIN", "INFIRMIER", "ADMIN");
    private static final Set<String> DOCTOR_ADMIN = Set.of("MEDECIN", "ADMIN");

    private record Rule(String prefix, Set<String> roles) {
    }

    private static final List<Rule> RULES = List.of(
            new Rule("/api/rest/", ALL),
            new Rule("/api/graphql", STAFF),
            new Rule("/api/soap/", DOCTOR_ADMIN),
            new Rule("/api/orchestrator/", DOCTOR_ADMIN));

    // A patient may only open his own file: /api/rest/patients/{id}...
    private static final Pattern PATIENT_RESOURCE = Pattern.compile("^/api/rest/patients/([^/]+)");

    public boolean isPublic(String path) {
        return path.equals("/auth/login") || path.equals("/api/health") || path.startsWith("/actuator/health");
    }

    public boolean isAllowed(String path, String role, String patientId) {
        if (role == null) {
            return false;
        }
        Optional<Rule> rule = RULES.stream().filter(r -> path.startsWith(r.prefix())).findFirst();
        if (rule.isEmpty() || !rule.get().roles().contains(role)) {
            return false;   // unknown route: denied by default
        }
        if ("PATIENT".equals(role)) {
            Matcher m = PATIENT_RESOURCE.matcher(path);
            if (m.find()) {
                return m.group(1).equals(patientId);
            }
        }
        return true;
    }
}