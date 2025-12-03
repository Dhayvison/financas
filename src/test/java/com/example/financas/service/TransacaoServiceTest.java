package com.example.financas.service;

import com.example.financas.dto.SaldoDTO;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
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
        void criarTransacaoFalhaSeCategoriaNaoEncontrada() {

                when(categoriaRepository.findById(categoriaProprietario.getId()))
                                .thenReturn(Optional.empty());

                assertThrows(ResourceNotFoundException.class, () -> {
                        transacaoService.criar(transacaoProprietario, usuarioProprietario);
                });

                verify(transacaoRepository, never()).save(any(Transacao.class));
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
        void atualizarTransacaoFalhaSeNovaCategoriaNaoEncontrada() {

                when(transacaoRepository.findById(transacaoProprietario.getId()))
                                .thenReturn(Optional.of(transacaoProprietario));

                when(categoriaRepository.findById(anyLong()))
                                .thenReturn(Optional.empty());

                Categoria categoriaNaoExistente = new Categoria();
                categoriaNaoExistente.setId(999L);

                Transacao detalhesAtualizados = new Transacao();
                detalhesAtualizados.setCategoria(categoriaNaoExistente);

                assertThrows(ResourceNotFoundException.class, () -> {
                        transacaoService.atualizar(transacaoProprietario.getId(), detalhesAtualizados,
                                        usuarioProprietario);
                });

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
        void atualizarTransacaoComSucessoMudandoCategoria() {

                Categoria novaCategoriaProprietario = new Categoria(50L, "Viagem", usuarioProprietario);

                when(transacaoRepository.findById(transacaoProprietario.getId()))
                                .thenReturn(Optional.of(transacaoProprietario));

                when(categoriaRepository.findById(novaCategoriaProprietario.getId()))
                                .thenReturn(Optional.of(novaCategoriaProprietario));

                when(transacaoRepository.save(any(Transacao.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                Transacao detalhesAtualizados = new Transacao();
                detalhesAtualizados.setCategoria(novaCategoriaProprietario);
                detalhesAtualizados.setDescricao("Viagem para X");

                Transacao resultado = transacaoService.atualizar(transacaoProprietario.getId(), detalhesAtualizados,
                                usuarioProprietario);

                assertNotNull(resultado);

                assertEquals(novaCategoriaProprietario.getId(), resultado.getCategoria().getId());
                assertEquals("Viagem para X", resultado.getDescricao());

                verify(transacaoRepository, times(1)).save(any(Transacao.class));
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

        @Test
        void listarPorUsuarioDeveRetornarListaDeTransacoes() {

                when(transacaoRepository.findByUser(any(User.class)))
                                .thenReturn(List.of(transacaoProprietario));

                List<Transacao> resultado = transacaoService.listarPorUsuario(usuarioProprietario);

                assertFalse(resultado.isEmpty());
                assertEquals(1, resultado.size());

                verify(transacaoRepository, times(1)).findByUser(usuarioProprietario);
        }

        @Test
        void listarPorUsuarioDeveRetornarListaVaziaSeNaoHouverTransacoes() {

                when(transacaoRepository.findByUser(any(User.class)))
                                .thenReturn(Collections.emptyList());

                List<Transacao> resultado = transacaoService.listarPorUsuario(usuarioProprietario);

                assertTrue(resultado.isEmpty());
                verify(transacaoRepository, times(1)).findByUser(usuarioProprietario);
        }

        @Test
        void getSaldoPorUsuarioDeveRetornarZeroQuandoNaoHaTransacoes() {
                // 1. PREPARAÇÃO: Simular Optional.empty() para RECEITAS
                when(transacaoRepository.sumValorByTipoAndUser(TipoTransacao.RECEITA, usuarioProprietario))
                                .thenReturn(Optional.empty());

                // 2. PREPARAÇÃO: Simular Optional.empty() para DESPESAS
                when(transacaoRepository.sumValorByTipoAndUser(TipoTransacao.DESPESA, usuarioProprietario))
                                .thenReturn(Optional.empty());

                // 3. AÇÃO
                SaldoDTO resultado = transacaoService.getSaldoPorUsuario(usuarioProprietario);

                // 4. VERIFICAÇÃO
                BigDecimal zero = BigDecimal.ZERO;

                // Verifica se todos os campos retornaram zero (o orElse(BigDecimal.ZERO) deve
                // funcionar)
                assertEquals(zero, resultado.getReceitas());
                assertEquals(zero, resultado.getDespesas());
                assertEquals(zero, resultado.getSaldoTotal());
        }

        @Test
        void getSaldoPorUsuarioDeveCalcularSaldoCorretamente() {
                // 1. PREPARAÇÃO: Simular a soma das RECEITAS
                BigDecimal receitas = new BigDecimal("7000.00");
                when(transacaoRepository.sumValorByTipoAndUser(TipoTransacao.RECEITA, usuarioProprietario))
                                .thenReturn(Optional.of(receitas));

                // 2. PREPARAÇÃO: Simular a soma das DESPESAS
                BigDecimal despesas = new BigDecimal("2500.50");
                when(transacaoRepository.sumValorByTipoAndUser(TipoTransacao.DESPESA, usuarioProprietario))
                                .thenReturn(Optional.of(despesas));

                // 3. AÇÃO
                SaldoDTO resultado = transacaoService.getSaldoPorUsuario(usuarioProprietario);

                // 4. VERIFICAÇÃO
                BigDecimal saldoEsperado = new BigDecimal("4499.50"); // 7000.00 - 2500.50

                // Verifica se os valores individuais estão corretos
                assertEquals(receitas.setScale(2, RoundingMode.HALF_UP),
                                resultado.getReceitas().setScale(2, RoundingMode.HALF_UP));
                assertEquals(despesas.setScale(2, RoundingMode.HALF_UP),
                                resultado.getDespesas().setScale(2, RoundingMode.HALF_UP));

                // Verifica se o saldo final está correto (usando compareTo para BigDecimals)
                assertEquals(0, saldoEsperado.compareTo(resultado.getSaldoTotal()));

                // Garante que os métodos do repositório foram chamados
                verify(transacaoRepository, times(1)).sumValorByTipoAndUser(TipoTransacao.RECEITA, usuarioProprietario);
                verify(transacaoRepository, times(1)).sumValorByTipoAndUser(TipoTransacao.DESPESA, usuarioProprietario);
        }

}