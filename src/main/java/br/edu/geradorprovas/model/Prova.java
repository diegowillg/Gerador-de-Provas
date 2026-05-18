package br.edu.geradorprovas.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Model que representa uma prova montada pelo sistema.
 * Uma prova possui dados gerais e uma lista de questoes.
 */
public class Prova {

    // Dados salvos na tabela provas.
    private Integer id;
    private String titulo;
    private String descricao;
    private String instrucoes;
    private LocalDateTime criadoEm;

    // Questoes selecionadas para compor a prova.
    private List<Questao> questoes = new ArrayList<>();

    public Prova() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getInstrucoes() {
        return instrucoes;
    }

    public void setInstrucoes(String instrucoes) {
        this.instrucoes = instrucoes;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<Questao> getQuestoes() {
        return questoes;
    }

    public void setQuestoes(List<Questao> questoes) {
        this.questoes = questoes;
    }
}
