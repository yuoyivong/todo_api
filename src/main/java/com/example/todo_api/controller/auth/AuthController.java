package com.example.todo_api.controller.auth;

import com.example.todo_api.configuration.exception.InvalidAccount;
import com.example.todo_api.configuration.jwt.JwtTokenUtil;
import com.example.todo_api.model.user.AppUser;
import com.example.todo_api.model.user.AppUserRequest;
import com.example.todo_api.model.user.AuthRequest;
import com.example.todo_api.model.user.AuthResponse;
import com.example.todo_api.model.utils.MessageConstance;
import com.example.todo_api.model.utils.MessageResponse;
import com.example.todo_api.service.user.AppUserServiceImp;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@Controller
@AllArgsConstructor
@RequestMapping("/api/todo/v1/auth")
public class AuthController {
    private final AppUserServiceImp appUserService;
    private AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private JwtTokenUtil jwtTokenUtil;


    @PostMapping("/sign-up")
    public ResponseEntity<?> register(@Valid@RequestBody AppUserRequest appUserRequest) {
        AppUser appUser = appUserService.insertNewUser(appUserRequest);
        MessageResponse<?> messageResponse = new MessageResponse<>(LocalDateTime.now(), 200, "success", appUser);
        return ResponseEntity.ok().body(messageResponse);
    }
    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequest authRequest) throws Exception {
        authenticate(authRequest.getEmail(), authRequest.getPassword());
        final UserDetails userDetails = appUserService.loadUserByUsername(authRequest.getEmail());
        final String token = jwtTokenUtil.generateToken(userDetails);
        AuthResponse authResponse = new AuthResponse(token);
        return ResponseEntity.ok(authResponse);
    }

//    @GetMapping("/get-current-user")
//    public ResponseEntity<?> getCurrentUser(){
//        AppUser userApp = appUserService.getUserByCurentId(currentId());
//        System.out.println(currentId());
//        MessageResponse<?> messageResponse = new MessageResponse<>(LocalDateTime.now(),200,"success",userApp);
//        return ResponseEntity.ok().body(nu);
//
//    }

    private void authenticate(String username, String password) throws Exception {
        try {
            UserDetails userApp = appUserService.loadUserByUsername(username);
            if (userApp == null) {
                throw new InvalidAccount(MessageConstance.INVALID_ACCOUNT);
            }
            if (!passwordEncoder.matches(password, userApp.getPassword())) {
                throw new InvalidAccount(MessageConstance.INVALID_ACCOUNT);
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
            authenticationManager.authenticate(authentication);
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }




}
