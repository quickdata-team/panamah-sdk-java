package br.com.quickdata.panamah.sdk.model;

import br.com.quickdata.panamah.sdk.IPanamahModel;

public class PanamahAssinante implements IPanamahModel {

    private String id;
    private String nome;
    private String fantasia;
    private String ramo;
    private String uf;
    private String cidade;
    private String revendaId;
    private String bairro;
    private java.util.List<PanamahAssinanteSoftwaresAtivos> softwaresAtivos;
    private java.util.List<PanamahAssinanteSoftwaresEmContratosDeManutencao> softwaresEmContratosDeManutencao;
    private java.util.List<String> series;
    private Boolean ativo;

    public PanamahAssinante() {
    }

    public PanamahAssinante(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFantasia() {
        return fantasia;
    }

    public void setFantasia(String fantasia) {
        this.fantasia = fantasia;
    }

    public String getRamo() {
        return ramo;
    }

    public void setRamo(String ramo) {
        this.ramo = ramo;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getRevendaId() {
        return revendaId;
    }

    public void setRevendaId(String revendaId) {
        this.revendaId = revendaId;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public java.util.List<PanamahAssinanteSoftwaresAtivos> getSoftwaresAtivos() {
        return softwaresAtivos;
    }

    public void setSoftwaresAtivos(java.util.List<PanamahAssinanteSoftwaresAtivos> softwaresAtivos) {
        this.softwaresAtivos = softwaresAtivos;
    }

    public java.util.List<PanamahAssinanteSoftwaresEmContratosDeManutencao> getSoftwaresEmContratosDeManutencao() {
        return softwaresEmContratosDeManutencao;
    }

    public void setSoftwaresEmContratosDeManutencao(java.util.List<PanamahAssinanteSoftwaresEmContratosDeManutencao> softwaresEmContratosDeManutencao) {
        this.softwaresEmContratosDeManutencao = softwaresEmContratosDeManutencao;
    }

    public java.util.List<String> getSeries() {
        return series;
    }

    public void setSeries(java.util.List<String> series) {
        this.series = series;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }


    public void validate() throws Exception {
        if (this.id == null) throw new Exception("id não pode ser nulo!");
        if (this.nome == null) throw new Exception("nome não pode ser nulo!");
        if (this.fantasia == null) throw new Exception("fantasia não pode ser nulo!");
        if (this.ramo == null) throw new Exception("ramo não pode ser nulo!");
        if (this.uf == null) throw new Exception("uf não pode ser nulo!");
        if (this.cidade == null) throw new Exception("cidade não pode ser nulo!");
        if (this.bairro == null) throw new Exception("bairro não pode ser nulo!");
        if (this.softwaresAtivos == null) throw new Exception("softwaresAtivos não pode ser nulo!");
        if (this.softwaresEmContratosDeManutencao == null)
            throw new Exception("softwaresEmContratosDeManutencao não pode ser nulo!");
        if (this.ativo == null) throw new Exception("ativo não pode ser nulo!");
    }
}
  