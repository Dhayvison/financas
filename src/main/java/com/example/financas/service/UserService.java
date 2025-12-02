package com.example.financas.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.financas.model.User;
import com.example.financas.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> buscarPorId(Long id) {
        return userRepository.findById(id);
    }

    public User registrarNovoUsuario(String username, String rawPassword) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do usuário não pode ser vazio.");
        }

        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("A senha do usuário não pode ser vazia.");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Já existe um usuário com este nome.");
        }

        User newUser = new User();

        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(rawPassword));

        return userRepository.save(newUser);
    }

    public void deletar(Long id) {
        userRepository.deleteById(id);
    }
}
