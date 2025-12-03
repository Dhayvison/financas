package com.example.financas.service;

import com.example.financas.exception.ResourceNotFoundException;
import com.example.financas.model.Categoria;
import com.example.financas.model.TipoTransacao;
import com.example.financas.model.Transacao;
import com.example.financas.model.User;
import com.example.financas.repository.CategoriaRepository;
import com.example.financas.repository.TransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {
    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private TransacaoService transacaoService;

    private User usuarioProprietario;
    private User usuarioInvasor;
    private Categoria categoriaProprietario;
    private Transacao transacaoProprietario;

    @BeforeEach
    void setUp() {
        usuarioProprietario = new User();
        usuarioProprietario.setId(UUID.randomUUID());
        usuarioProprietario.setUsername("proprietario");

        usuarioInvasor = new User();
        usuarioInvasor.setId(UUID.randomUUID());
        usuarioInvasor.setUsername("invasor");

        categoriaProprietario = new Categoria(10L, "Aluguel", usuarioProprietario);

        transacaoProprietario = new Transacao(
                "Teste Aluguel",
                new BigDecimal("1000.00"),
                LocalDate.now(),
                TipoTransacao.DESPESA,
                usuarioProprietario,
                categoriaProprietario);

        transacaoProprietario.setId(100L);
    }

    @Test
    void criarTransacaoComSucesso() {
        when(categoriaRepository.findById(categoriaProprietario.getId()))
                .thenReturn(Optional.of(categoriaProprietario));

        when(transacaoRepository.save(any(Transacao.class)))
                .thenReturn(transacaoProprietario);

        Transacao resultado = transacaoService.criar(transacaoProprietario, usuarioProprietario);

        assertNotNull(resultado);
        assertEquals(usuarioProprietario.getId(), resultado.getUser().getId());
        verify(transacaoRepository, times(1)).save(any(Transacao.class));
    }

    @Test
    void criarTransacaoFalhaSeCategoriaNaoPertenceAoUsuario() {
        Categoria categoriaInvasora = new Categoria(20L, "Invasão", usuarioInvasor);
        transacaoProprietario.setCategoria(categoriaInvasora);

        when(categoriaRepository.findById(categoriaInvasora.getId()))
                .thenReturn(Optional.of(categoriaInvasora));

        assertThrows(ResponseStatusException.class, () -> {
            transacaoService.criar(transacaoProprietario, usuarioProprietario);
        }, "Deve lançar 403 Forbidden");

        verify(transacaoRepository, never()).save(any(Transacao.class));
    }

    @Test
    void atualizarTransacaoComSucesso() {
        when(transacaoRepository.findById(transacaoProprietario.getId()))
                .thenReturn(Optional.of(transacaoProprietario));
        when(transacaoRepository.save(any(Transacao.class)))
                .thenReturn(transacaoProprietario);

        Transacao detalhesAtualizados = new Transacao();
        detalhesAtualizados.setDescricao("Descrição Atualizada");
        detalhesAtualizados.setValor(new BigDecimal("1500.00"));
        detalhesAtualizados.setCategoria(categoriaProprietario);

        Transacao resultado = transacaoService.atualizar(transacaoProprietario.getId(), detalhesAtualizados,
                usuarioProprietario);

        assertEquals("Descrição Atualizada", resultado.getDescricao());
        assertEquals(new BigDecimal("1500.00"), resultado.getValor());
    }

    @Test
    void atualizarTransacaoFalhaSeNaoPertenceAoUsuario() {
        when(transacaoRepository.findById(transacaoProprietario.getId()))
                .thenReturn(Optional.of(transacaoProprietario));

        Transacao detalhesAtualizados = new Transacao();

        assertThrows(ResponseStatusException.class, () -> {
            transacaoService.atualizar(transacaoProprietario.getId(), detalhesAtualizados, usuarioInvasor);
        }, "Deve lançar 403 Forbidden");

        verify(transacaoRepository, never()).save(any(Transacao.class));
    }

    @Test
    void deletarTransacaoComSucesso() {
        when(transacaoRepository.findById(transacaoProprietario.getId()))
                .thenReturn(Optional.of(transacaoProprietario));

        transacaoService.deletar(transacaoProprietario.getId(), usuarioProprietario);

        verify(transacaoRepository, times(1)).delete(transacaoProprietario);
    }

    @Test
    void deletarTransacaoFalhaSeNaoPertenceAoUsuario() {
        when(transacaoRepository.findById(transacaoProprietario.getId()))
                .thenReturn(Optional.of(transacaoProprietario));

        assertThrows(ResponseStatusException.class, () -> {
            transacaoService.deletar(transacaoProprietario.getId(), usuarioInvasor);
        }, "Deve lançar 403 Forbidden");

        verify(transacaoRepository, never()).delete(any(Transacao.class));
    }

    @Test
    void deletarTransacaoFalhaSeNaoEncontrada() {
        when(transacaoRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            transacaoService.deletar(999L, usuarioProprietario);
        });

        verify(transacaoRepository, never()).delete(any(Transacao.class));
    }

}