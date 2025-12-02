package com.example.financas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.example.financas.model.TipoTransacao;
import com.example.financas.model.Transacao;
import com.example.financas.repository.TransacaoRepository;

import jakarta.persistence.EntityManager;

@Service
@Transactional
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;

    @Autowired
    private EntityManager entityManager;

    public TransacaoService(TransacaoRepository transacaoRepository) {
        this.transacaoRepository = transacaoRepository;
    }

    public List<Transacao> listar() {
        return transacaoRepository.findAll();
    }

    public Optional<Transacao> buscarPorId(Long id) {
        return transacaoRepository.findById(id);
    }

    public Transacao salvar(Transacao transacao) {

        if (transacao.getValor() == null || transacao.getValor().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da transação não pode ser nulo e deve ser maior que zero.");
        }

        if (transacao.getCategoria() == null) {
            throw new IllegalArgumentException("A categoria da transação não pode ser nula.");
        }

        if (transacao.getTipo() == null) {
            throw new IllegalArgumentException("O tipo da transação não pode ser nulo.");
        }

        if (transacao.getData() == null) {
            transacao.setData(java.time.LocalDate.now());
        }

        transacaoRepository.save(transacao);
        entityManager.refresh(transacao);
        return transacao;
    }

    public void deletar(Long id) {
        transacaoRepository.deleteById(id);
    }

    public BigDecimal calcularSaldo() {
        return transacaoRepository.findAll()
                .stream()
                .map(transacao -> transacao.getTipo().equals(TipoTransacao.DESPESA) ? transacao.getValor().negate()
                        : transacao.getValor())
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }
}
