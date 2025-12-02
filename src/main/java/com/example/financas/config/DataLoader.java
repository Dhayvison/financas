package com.example.financas.config;

import com.example.financas.service.UserService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    @Value("${initial-user.username}")
    private String initialUsername;

    @Value("${initial-user.password}")
    private String initialPassword;

    @Bean
    public CommandLineRunner initDatabase(UserService userService, PasswordEncoder passwordEncoder) {
        return args -> {
            try {
                System.out.println("Criando usuário inicial a partir de application.properties...");
                userService.registrarNovoUsuario(initialUsername, initialPassword);
                System.out.println("Usuário inicial criado: " + initialUsername + " / Senha: " + initialPassword);
            } catch (Exception e) {
                System.out.println("Usuário inicial inválido. Motivo: " + e.getMessage());
            }
        };
    }
}