package br.com.casamagalhaes.panamah.sdk.model;

public class PanamahVendaPagamentos {

    private String formaPagamentoId;
    private String sequencial;
    private java.math.BigDecimal valor;
    private String tipoPagamentoSefaz;

    public PanamahVendaPagamentos() {
    }

    public String getFormaPagamentoId() {
        return formaPagamentoId;
    }

    public void setFormaPagamentoId(String formaPagamentoId) {
        this.formaPagamentoId = formaPagamentoId;
    }

    public String getSequencial() {
        return sequencial;
    }

    public void setSequencial(String sequencial) {
        this.sequencial = sequencial;
    }

    public java.math.BigDecimal getValor() {
        return valor;
    }

    public void setValor(java.math.BigDecimal valor) {
        this.valor = valor;
    }

    public String getTipoPagamentoSefaz() {
        return tipoPagamentoSefaz;
    }

    public void setTipoPagamentoSefaz(String tipoPagamentoSefaz) {
        this.tipoPagamentoSefaz = tipoPagamentoSefaz;
    }

    public void validate() throws Exception {
        if (this.formaPagamentoId == null)
            throw new Exception("formaPagamentoId não pode ser nulo!");
        if (this.sequencial == null)
            throw new Exception("sequencial não pode ser nulo!");
        if (this.valor == null)
            throw new Exception("valor não pode ser nulo!");
    }
}
