package com.example.financas.dto;

import java.math.BigDecimal;

public class SaldoDTO {
    private BigDecimal receitas;
    private BigDecimal despesas;
    private BigDecimal saldoTotal;

    public SaldoDTO(BigDecimal receitas, BigDecimal despesas, BigDecimal saldoTotal) {
        this.receitas = receitas;
        this.despesas = despesas;
        this.saldoTotal = saldoTotal;
    }

    public BigDecimal getReceitas() {
        return receitas;
    }

    public void setReceitas(BigDecimal receitas) {
        this.receitas = receitas;
    }

    public BigDecimal getDespesas() {
        return despesas;
    }

    public void setDespesas(BigDecimal despesas) {
        this.despesas = despesas;
    }

    public BigDecimal getSaldoTotal() {
        return saldoTotal;
    }

    public void setSaldoTotal(BigDecimal saldoTotal) {
        this.saldoTotal = saldoTotal;
    }
}