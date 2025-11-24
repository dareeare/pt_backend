package medicalcenter.authservice.controller;

import lombok.RequiredArgsConstructor;
import medicalcenter.authservice.service.AuthService;
import medicalcenter.authservice.service.UserSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;
    private final UserSyncService userSyncService;

    @DeleteMapping("/users/{phone}")
    public ResponseEntity<?> deleteUser(
            @PathVariable String phone,
            @RequestHeader("X-Admin-Secret") String secret) {
        
        if (!"admin-secret-key-123".equals(secret)) {
            return ResponseEntity.status(403).body("Invalid secret");
        }

        try {
            authService.deleteUser(phone); // This will delete from AuthService DB
            userSyncService.deleteUser(phone); // This will call UserService to delete profile
            return ResponseEntity.ok("User deleted successfully from both services");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

