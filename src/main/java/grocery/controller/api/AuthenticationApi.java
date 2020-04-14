package grocery.controller.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthenticationApi {
    @GetMapping(value = "/api/auth")
    public Map<String, String> auth() {
        Map<String, String> response = new HashMap<>();
        response.put("result", "success");
        return response;
    }
}
