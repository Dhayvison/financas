package com.example.financas.controller;

import com.example.financas.model.Transacao;
import com.example.financas.model.User;
import com.example.financas.service.TransacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @PostMapping
    public ResponseEntity<Transacao> criarTransacao(
            @RequestBody Transacao transacao,
            @AuthenticationPrincipal User userLogado) {

        Transacao novaTransacao = transacaoService.criar(transacao, userLogado);
        return new ResponseEntity<>(novaTransacao, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Transacao>> listarTransacoesDoUsuario(@AuthenticationPrincipal User userLogado) {

        List<Transacao> transacoes = transacaoService.listarPorUsuario(userLogado);
        return ResponseEntity.ok(transacoes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transacao> updateTransacao(
            @PathVariable Long id,
            @RequestBody Transacao transacaoDetails,
            @AuthenticationPrincipal User userLogado) {

        Transacao transacaoAtualizada = transacaoService.atualizar(id, transacaoDetails, userLogado);
        return ResponseEntity.ok(transacaoAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteTransacao(
            @PathVariable Long id,
            @AuthenticationPrincipal User userLogado) {

        transacaoService.deletar(id, userLogado);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}