package com.example.financas.service;

import com.example.financas.dto.SaldoDTO;
import com.example.financas.exception.ResourceNotFoundException;
import com.example.financas.model.Categoria;
import com.example.financas.model.TipoTransacao;
import com.example.financas.model.Transacao;
import com.example.financas.model.User;
import com.example.financas.repository.CategoriaRepository;
import com.example.financas.repository.TransacaoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final CategoriaRepository categoriaRepository;

    public TransacaoService(TransacaoRepository transacaoRepository, CategoriaRepository categoriaRepository) {
        this.transacaoRepository = transacaoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<Transacao> listarPorUsuario(User userLogado) {
        return transacaoRepository.findByUser(userLogado);
    }

    public Transacao criar(Transacao transacao, User userLogado) {
        Categoria categoria = categoriaRepository.findById(transacao.getCategoria().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoria não encontrada com ID: " + transacao.getCategoria().getId()));

        if (!categoria.getUser().getId().equals(userLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "A categoria informada não pertence ao usuário logado.");
        }

        transacao.setUser(userLogado);
        transacao.setCategoria(categoria);

        return transacaoRepository.save(transacao);
    }

    public Transacao atualizar(Long id, Transacao transacaoDetails, User userLogado) {

        Transacao transacaoExistente = transacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada com id: " + id));

        // 1. 🚨 REGRA DE SEGURANÇA: A transação pertence ao User logado?
        if (!transacaoExistente.getUser().getId().equals(userLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Você não tem permissão para atualizar esta transação.");
        }

        // 2. Lógica de Atualização de Categoria (se houver mudança)
        if (transacaoDetails.getCategoria() != null
                && !transacaoDetails.getCategoria().getId().equals(transacaoExistente.getCategoria().getId())) {

            Categoria novaCategoria = categoriaRepository.findById(transacaoDetails.getCategoria().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Nova Categoria não encontrada com ID: " + transacaoDetails.getCategoria().getId()));

            // 🚨 REGRA DE SEGURANÇA: A nova Categoria pertence ao User logado?
            if (!novaCategoria.getUser().getId().equals(userLogado.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "A nova categoria informada não pertence ao usuário logado.");
            }
            transacaoExistente.setCategoria(novaCategoria);
        }

        // 3. Aplicar outras atualizações (Lógica de Negócio)
        transacaoExistente.setDescricao(transacaoDetails.getDescricao());
        transacaoExistente.setValor(transacaoDetails.getValor());
        transacaoExistente.setData(transacaoDetails.getData());
        transacaoExistente.setTipo(transacaoDetails.getTipo());

        return transacaoRepository.save(transacaoExistente);
    }

    public void deletar(Long id, User userLogado) {

        Transacao transacaoExistente = transacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada com id: " + id));

        // 1. 🚨 REGRA DE SEGURANÇA: A transação pertence ao User logado?
        if (!transacaoExistente.getUser().getId().equals(userLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Você não tem permissão para deletar esta transação.");
        }

        // 2. Deleção
        transacaoRepository.delete(transacaoExistente);
    }

    public SaldoDTO getSaldoPorUsuario(User userLogado) {
        // Busca a soma das RECEITAS. Se não houver, retorna 0.00
        BigDecimal receitas = transacaoRepository.sumValorByTipoAndUser(TipoTransacao.RECEITA, userLogado)
                .orElse(BigDecimal.ZERO);

        // Busca a soma das DESPESAS. Se não houver, retorna 0.00
        BigDecimal despesas = transacaoRepository.sumValorByTipoAndUser(TipoTransacao.DESPESA, userLogado)
                .orElse(BigDecimal.ZERO);

        // Calcula o saldo total: Receitas - Despesas
        BigDecimal saldoTotal = receitas.subtract(despesas);

        return new SaldoDTO(receitas, despesas, saldoTotal);
    }
}