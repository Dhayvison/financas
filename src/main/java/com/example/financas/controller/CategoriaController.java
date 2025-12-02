package com.example.financas.controller;

import com.example.financas.model.Categoria;
import com.example.financas.model.User;
import com.example.financas.repository.CategoriaRepository;
import com.example.financas.service.CategoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> listarCategoriasDoUsuario(
            @AuthenticationPrincipal User user) {
        List<Categoria> categorias = categoriaService.listar(user);
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscarPorId(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Optional<Categoria> categoria = categoriaService.buscarPorId(id);

        return categoria.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Categoria> criarCategoria(
            @RequestBody Categoria categoria,
            @AuthenticationPrincipal User user) {
        Categoria novaCategoria = categoriaService.salvar(categoria, user);
        return new ResponseEntity<>(novaCategoria, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> updateCategoria(
            @PathVariable Long id,
            @RequestBody Categoria categoriaDetails,
            @AuthenticationPrincipal User userLogado) {
        Categoria categoriaExistente = categoriaService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada com id: " + id));

        if (!categoriaExistente.getUser().getId().equals(userLogado.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        categoriaExistente.setNome(categoriaDetails.getNome());

        Categoria categoriaAtualizada = categoriaService.salvar(categoriaExistente, userLogado);
        return ResponseEntity.ok(categoriaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCategoria(
            @PathVariable Long id,
            @AuthenticationPrincipal User userLogado) {

        Categoria categoriaExistente = categoriaService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada com id: " + id));

        if (!categoriaExistente.getUser().getId().equals(userLogado.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        categoriaService.deletar(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}