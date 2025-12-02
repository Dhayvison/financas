package com.example.financas.controller;

import com.example.financas.dto.LoginRequest;
import com.example.financas.dto.TokenResponse;
import com.example.financas.dto.UserResponse;
import com.example.financas.model.User;
import com.example.financas.service.TokenService;
import com.example.financas.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService,
            UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {

        // 1. Cria o objeto de autenticação (sem autenticar ainda)
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.username(), loginRequest.password());

        // 2. Autentica: Chama o AuthenticationManager, que usa o UserDetailsService
        // (Se falhar, lança exceção)
        Authentication authentication = authenticationManager.authenticate(authToken);

        // 3. Se bem-sucedido, gera o token
        String token = tokenService.generateToken((User) authentication.getPrincipal());

        // 4. Retorna o token para o cliente
        return ResponseEntity.ok(new TokenResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody LoginRequest registrationRequest) {
        User newUser = userService.registrarNovoUsuario(
                registrationRequest.username(),
                registrationRequest.password());

        UserResponse response = new UserResponse(
                newUser.getId(),
                newUser.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}