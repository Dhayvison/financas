package com.example.financas.controller;

import com.example.financas.model.Transacao;
import com.example.financas.service.TransacaoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @GetMapping
    public ResponseEntity<List<Transacao>> listar() {
        return ResponseEntity.ok(transacaoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transacao> buscarPorId(@PathVariable("id") Long id) {
        Optional<Transacao> t = transacaoService.buscarPorId(id);
        return t.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Transacao> criar(@RequestBody Transacao transacao) {
        Transacao criada = transacaoService.salvar(transacao);
        return new ResponseEntity<>(criada, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transacao> atualizar(@PathVariable Long id, @RequestBody Transacao transacao) {
        if (!transacaoService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        transacao.setId(id);
        Transacao atualizada = transacaoService.salvar(transacao);
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!transacaoService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        transacaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/saldo")
    public ResponseEntity<java.math.BigDecimal> saldo() {
        return ResponseEntity.ok(transacaoService.calcularSaldo());
    }
}
