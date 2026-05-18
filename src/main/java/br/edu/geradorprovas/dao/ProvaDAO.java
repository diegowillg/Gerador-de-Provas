package br.edu.geradorprovas.dao;

import br.edu.geradorprovas.config.DBConnection;
import br.edu.geradorprovas.model.Prova;
import br.edu.geradorprovas.model.Questao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * DAO de provas.
 * Faz as operacoes no banco para a tabela provas e para a tabela de ligacao prova_questao.
 */
public class ProvaDAO {

    // Lista todas as provas cadastradas.
    public List<Prova> listarTodas() throws SQLException {
        String sql = "SELECT * FROM provas ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<Prova> provas = new ArrayList<>();
            while (rs.next()) {
                provas.add(mapearProva(rs));
            }
            return provas;
        }
    }

    // Busca uma prova e tambem carrega as questoes vinculadas a ela.
    public Optional<Prova> buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM provas WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                Prova prova = mapearProva(rs);
                prova.setQuestoes(buscarQuestoesDaProva(id));
                return Optional.of(prova);
            }
        }
    }

    // Insere a prova e vincula as questoes escolhidas em uma transacao.
    public int inserir(Prova prova, List<Integer> questaoIds) throws SQLException {
        String sql = "INSERT INTO provas (titulo, descricao, instrucoes) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            // A transacao garante que prova e questoes sejam salvas juntas.
            conn.setAutoCommit(false);
            try {
                int provaId;
                try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, prova.getTitulo());
                    stmt.setString(2, prova.getDescricao());
                    stmt.setString(3, prova.getInstrucoes());
                    stmt.executeUpdate();
                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Não foi possível obter o ID da prova");
                        }
                        provaId = keys.getInt(1);
                    }
                }
                vincularQuestoes(conn, provaId, questaoIds);
                conn.commit();
                return provaId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // Atualiza a prova e recria os vinculos com as questoes selecionadas.
    public void atualizar(Prova prova, List<Integer> questaoIds) throws SQLException {
        String sql = "UPDATE provas SET titulo = ?, descricao = ?, instrucoes = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, prova.getTitulo());
                    stmt.setString(2, prova.getDescricao());
                    stmt.setString(3, prova.getInstrucoes());
                    stmt.setInt(4, prova.getId());
                    stmt.executeUpdate();
                }
                try (PreparedStatement delete = conn.prepareStatement("DELETE FROM prova_questao WHERE prova_id = ?")) {
                    delete.setInt(1, prova.getId());
                    delete.executeUpdate();
                }
                vincularQuestoes(conn, prova.getId(), questaoIds);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // Exclui a prova; os vinculos sao apagados por ON DELETE CASCADE no banco.
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM provas WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Grava a relacao N:N entre prova e questoes.
    private void vincularQuestoes(Connection conn, int provaId, List<Integer> questaoIds) throws SQLException {
        String sql = "INSERT INTO prova_questao (prova_id, questao_id, ordem) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int ordem = 1;
            for (Integer questaoId : questaoIds) {
                stmt.setInt(1, provaId);
                stmt.setInt(2, questaoId);
                stmt.setInt(3, ordem++);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    // Carrega as questoes da prova na ordem em que foram vinculadas.
    private List<Questao> buscarQuestoesDaProva(int provaId) throws SQLException {
        String sql = """
                SELECT q.* FROM questoes q
                INNER JOIN prova_questao pq ON pq.questao_id = q.id
                WHERE pq.prova_id = ?
                ORDER BY pq.ordem
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, provaId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Questao> questoes = new ArrayList<>();
                while (rs.next()) {
                    questoes.add(mapearQuestao(rs));
                }
                return questoes;
            }
        }
    }

    public Map<Integer, List<Integer>> listarVinculos() throws SQLException {
        String sql = "SELECT prova_id, questao_id FROM prova_questao ORDER BY prova_id, ordem";
        Map<Integer, List<Integer>> vinculos = new LinkedHashMap<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int provaId = rs.getInt("prova_id");
                vinculos.computeIfAbsent(provaId, k -> new ArrayList<>()).add(rs.getInt("questao_id"));
            }
        }
        return vinculos;
    }

    // Transforma uma linha da tabela provas em objeto Prova.
    private Prova mapearProva(ResultSet rs) throws SQLException {
        Prova prova = new Prova();
        prova.setId(rs.getInt("id"));
        prova.setTitulo(rs.getString("titulo"));
        prova.setDescricao(rs.getString("descricao"));
        prova.setInstrucoes(rs.getString("instrucoes"));
        Timestamp criadoEm = rs.getTimestamp("criado_em");
        if (criadoEm != null) {
            prova.setCriadoEm(criadoEm.toLocalDateTime());
        }
        return prova;
    }

    // Transforma uma linha da tabela questoes em objeto Questao.
    private Questao mapearQuestao(ResultSet rs) throws SQLException {
        Questao questao = new Questao();
        questao.setId(rs.getInt("id"));
        questao.setEnunciado(rs.getString("enunciado"));
        questao.setAlternativaA(rs.getString("alternativa_a"));
        questao.setAlternativaB(rs.getString("alternativa_b"));
        questao.setAlternativaC(rs.getString("alternativa_c"));
        questao.setAlternativaD(rs.getString("alternativa_d"));
        questao.setAlternativaE(rs.getString("alternativa_e"));
        questao.setRespostaCorreta(rs.getString("resposta_correta"));
        questao.setDisciplina(rs.getString("disciplina"));
        questao.setDificuldade(rs.getString("dificuldade"));
        return questao;
    }
}
