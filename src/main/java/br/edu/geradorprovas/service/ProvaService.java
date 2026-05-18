package br.edu.geradorprovas.service;

import br.edu.geradorprovas.dao.ProvaDAO;
import br.edu.geradorprovas.dao.QuestaoDAO;
import br.edu.geradorprovas.model.Prova;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Camada de regra de negocio das provas.
 * Valida a prova e confere se as questoes selecionadas existem antes de salvar.
 */
public class ProvaService {

    private final ProvaDAO provaDAO = new ProvaDAO();
    private final QuestaoDAO questaoDAO = new QuestaoDAO();

    public List<Prova> listarTodas() throws SQLException {
        return provaDAO.listarTodas();
    }

    public Optional<Prova> buscarPorId(int id) throws SQLException {
        return provaDAO.buscarPorId(id);
    }

    // Salva uma nova prova depois de validar os campos.
    public int salvar(Prova prova, List<Integer> questaoIds) throws SQLException {
        validar(prova, questaoIds);
        return provaDAO.inserir(prova, questaoIds);
    }

    // Atualiza uma prova existente e seus vinculos com questoes.
    public void atualizar(Prova prova, List<Integer> questaoIds) throws SQLException {
        if (prova.getId() == null) {
            throw new IllegalArgumentException("ID da prova é obrigatório para atualização.");
        }
        validar(prova, questaoIds);
        provaDAO.atualizar(prova, questaoIds);
    }

    public void excluir(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }
        provaDAO.excluir(id);
    }

    // Regras para uma prova ser considerada valida.
    private void validar(Prova prova, List<Integer> questaoIds) throws SQLException {
        if (prova.getTitulo() == null || prova.getTitulo().trim().length() < 3) {
            throw new IllegalArgumentException("O título da prova deve ter pelo menos 3 caracteres.");
        }
        if (questaoIds == null || questaoIds.isEmpty()) {
            throw new IllegalArgumentException("Selecione ao menos uma questão para a prova.");
        }
        prova.setTitulo(prova.getTitulo().trim());
        if (prova.getDescricao() != null) {
            prova.setDescricao(prova.getDescricao().trim());
        }
        if (prova.getInstrucoes() != null) {
            prova.setInstrucoes(prova.getInstrucoes().trim());
        }

        // Confere no banco se todas as questoes selecionadas realmente existem.
        long encontradas = questaoDAO.buscarPorIds(questaoIds).size();
        if (encontradas != questaoIds.size()) {
            throw new IllegalArgumentException("Uma ou mais questões selecionadas não existem.");
        }
    }
}
