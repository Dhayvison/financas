package com.example.financas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.financas.model.Categoria;
import com.example.financas.model.User;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    // Adicione métodos customizados aqui, por exemplo:
    Optional<Categoria> findByNome(String nome);

    List<Categoria> findByUser(User user);
}
