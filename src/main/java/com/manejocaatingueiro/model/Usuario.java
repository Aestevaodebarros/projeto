package com.manejocaatingueiro.model;

import java.time.Instant;

public class Usuario {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String senhaHash;
    private PerfilUsuario perfil;
    private FonteAgua abastecimento;
    private Integer localidadeId;
    private PerfilProdutivo perfilProdutivo;
    private boolean emailVerificado;
    private Instant createdAt;
    private Instant updatedAt;

    public Usuario() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    public PerfilUsuario getPerfil() { return perfil; }
    public void setPerfil(PerfilUsuario perfil) { this.perfil = perfil; }
    public FonteAgua getAbastecimento() { return abastecimento; }
    public void setAbastecimento(FonteAgua abastecimento) { this.abastecimento = abastecimento; }
    public Integer getLocalidadeId() { return localidadeId; }
    public void setLocalidadeId(Integer localidadeId) { this.localidadeId = localidadeId; }
    public PerfilProdutivo getPerfilProdutivo() { return perfilProdutivo; }
    public void setPerfilProdutivo(PerfilProdutivo perfilProdutivo) { this.perfilProdutivo = perfilProdutivo; }
    public boolean isEmailVerificado() { return emailVerificado; }
    public void setEmailVerificado(boolean emailVerificado) { this.emailVerificado = emailVerificado; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
