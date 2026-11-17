package com.manejocaatingueiro.model;

import java.time.Instant;

/** Operação local aguardando replicação para um serviço remoto. */
public class PendenciaSincronizacao {
    private String id;
    private String entidade;
    private String operacao;
    private String referenciaId;
    private String status = "PENDENTE";
    private Instant criadaEm = Instant.now();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEntidade() { return entidade; }
    public void setEntidade(String entidade) { this.entidade = entidade; }
    public String getOperacao() { return operacao; }
    public void setOperacao(String operacao) { this.operacao = operacao; }
    public String getReferenciaId() { return referenciaId; }
    public void setReferenciaId(String referenciaId) { this.referenciaId = referenciaId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCriadaEm() { return criadaEm; }
    public void setCriadaEm(Instant criadaEm) { this.criadaEm = criadaEm; }
}
