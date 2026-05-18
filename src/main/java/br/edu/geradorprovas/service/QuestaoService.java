package br.edu.geradorprovas.service;

import br.edu.geradorprovas.dao.QuestaoDAO;
import br.edu.geradorprovas.model.Questao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Camada de regra de negocio das questoes.
 * O controller chama esta classe antes de acessar o DAO.
 */
public class QuestaoService {

    private static final Set<String> RESPOSTAS_VALIDAS = Set.of("A", "B", "C", "D", "E");
    private static final Set<String> DIFICULDADES_VALIDAS = Set.of("FACIL", "MEDIA", "DIFICIL");

    private final QuestaoDAO questaoDAO = new QuestaoDAO();

    public List<Questao> listarTodas() throws SQLException {
        return questaoDAO.listarTodas();
    }

    public List<Questao> listarPorDisciplina(String disciplina) throws SQLException {
        if (disciplina == null || disciplina.isBlank()) {
            return listarTodas();
        }
        return questaoDAO.listarPorDisciplina(disciplina.trim());
    }

    public Optional<Questao> buscarPorId(int id) throws SQLException {
        return questaoDAO.buscarPorId(id);
    }

    public List<String> listarDisciplinas() throws SQLException {
        return questaoDAO.listarDisciplinas();
    }

    // Valida os dados antes de gravar uma nova questao no banco.
    public int salvar(Questao questao) throws SQLException {
        validar(questao, questao.getId() == null);
        return questaoDAO.inserir(questao);
    }

    // Valida os dados antes de atualizar uma questao existente.
    public void atualizar(Questao questao) throws SQLException {
        if (questao.getId() == null) {
            throw new IllegalArgumentException("ID da questão é obrigatório para atualização.");
        }
        validar(questao, false);
        questaoDAO.atualizar(questao);
    }

    public void excluir(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }
        questaoDAO.excluir(id);
    }

    // Regras basicas para manter o cadastro consistente.
    private void validar(Questao questao, boolean novo) {
        if (questao.getEnunciado() == null || questao.getEnunciado().trim().length() < 10) {
            throw new IllegalArgumentException("O enunciado deve ter pelo menos 10 caracteres.");
        }
        if (isBlank(questao.getAlternativaA()) || isBlank(questao.getAlternativaB())
                || isBlank(questao.getAlternativaC()) || isBlank(questao.getAlternativaD())) {
            throw new IllegalArgumentException("As alternativas A, B, C e D são obrigatórias.");
        }
        if (questao.getRespostaCorreta() == null
                || !RESPOSTAS_VALIDAS.contains(questao.getRespostaCorreta().toUpperCase())) {
            throw new IllegalArgumentException("Resposta correta deve ser A, B, C, D ou E.");
        }
        if (questao.getDisciplina() == null || questao.getDisciplina().trim().isBlank()) {
            throw new IllegalArgumentException("A disciplina é obrigatória.");
        }
        String dificuldade = questao.getDificuldade() == null ? "MEDIA" : questao.getDificuldade().toUpperCase();
        if (!DIFICULDADES_VALIDAS.contains(dificuldade)) {
            throw new IllegalArgumentException("Dificuldade inválida.");
        }
        questao.setRespostaCorreta(questao.getRespostaCorreta().toUpperCase());
        questao.setDificuldade(dificuldade);
        questao.setDisciplina(questao.getDisciplina().trim());
        questao.setEnunciado(questao.getEnunciado().trim());

        if ("E".equals(questao.getRespostaCorreta()) && isBlank(questao.getAlternativaE())) {
            throw new IllegalArgumentException("Informe a alternativa E quando ela for a resposta correta.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
