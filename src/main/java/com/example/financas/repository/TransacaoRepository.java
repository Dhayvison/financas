package com.example.financas.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.financas.model.TipoTransacao;
import com.example.financas.model.Transacao;
import com.example.financas.model.User;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    List<Transacao> findByTipo(TipoTransacao tipo);

    List<Transacao> findByUser(User user);

    Transacao findByIdAndUser(Long id, User user);

    /**
     * Calcula a soma de 'valor' para um determinado 'tipo' (RECEITA ou DESPESA)
     * e para um 'user' específico.
     */
    @Query("SELECT SUM(t.valor) FROM Transacao t WHERE t.tipo = :tipo AND t.user = :user")
    Optional<BigDecimal> sumValorByTipoAndUser(TipoTransacao tipo, User user);
}
