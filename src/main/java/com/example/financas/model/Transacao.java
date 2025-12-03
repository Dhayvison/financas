package com.example.financas.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDate data;

    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Transacao() {
    }

    public Transacao(BigDecimal valor, LocalDate data, Categoria categoria) {
        this.valor = valor;
        this.data = data;
        this.categoria = categoria;
    }

    public Transacao(String string, BigDecimal bigDecimal, LocalDate now, TipoTransacao despesa,
            User usuarioProprietario, Categoria categoriaProprietario) {
        this.descricao = string;
        this.valor = bigDecimal;
        this.data = now;
        this.tipo = despesa;
        this.user = usuarioProprietario;
        this.categoria = categoriaProprietario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransacao tipo) {
        this.tipo = tipo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}