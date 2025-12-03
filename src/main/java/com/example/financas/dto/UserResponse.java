package com.example.financas.dto;

import java.util.UUID;

public record UserResponse(UUID id,
        String username) {
}