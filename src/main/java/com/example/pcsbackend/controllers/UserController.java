package com.example.pcsbackend.controllers;

import com.example.pcsbackend.dto.UserRequestDto;
import com.example.pcsbackend.entities.User;
import com.example.pcsbackend.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/signup")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserRequestDto userDto) {
        User user = userService.createUserFromDto(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping("/{email}")
    public ResponseEntity<User> updateUserByEmail(@PathVariable String email, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUserByEmail(email, user));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUserByEmail(@PathVariable String email) {
        userService.deleteUserByEmail(email);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @RequestBody User userData,
            HttpServletRequest request) {
        Optional<User> userOpt = userService.getUserByEmail(userData.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(userData.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        // Create session
        HttpSession session = request.getSession(true);
        session.setAttribute("userId", user.getId());
        session.setAttribute("userEmail", user.getEmail());

        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok("Logout successful");
    }

    @GetMapping("/session-info")
    public ResponseEntity<?> getSessionInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No active session");
        }
        return ResponseEntity.ok("User ID: " + session.getAttribute("userId") + ", Email: " + session.getAttribute("userEmail"));
    }
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null) {
            return ResponseEntity.ok(new User());
        }

        String email = (String) session.getAttribute("userEmail");
        Optional<User> userOpt = userService.getUserByEmail(email);

        if (userOpt.isEmpty()) {
			session.invalidate();
        }

        return ResponseEntity.ok(userOpt.get());
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsersByEmail(@RequestParam String emailPart) {
        return ResponseEntity.ok(userService.searchUsersByEmail(emailPart));
    }
}
