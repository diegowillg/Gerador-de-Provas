package br.edu.geradorprovas.dao;

import br.edu.geradorprovas.config.DBConnection;
import br.edu.geradorprovas.model.Questao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO de questoes.
 * Esta classe concentra o acesso a tabela questoes usando JDBC e PreparedStatement.
 */
public class QuestaoDAO {

    // Busca todas as questoes cadastradas no banco.
    public List<Questao> listarTodas() throws SQLException {
        String sql = "SELECT * FROM questoes ORDER BY id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<Questao> questoes = new ArrayList<>();
            while (rs.next()) {
                questoes.add(mapear(rs));
            }
            return questoes;
        }
    }

    // Busca somente as questoes de uma disciplina.
    public List<Questao> listarPorDisciplina(String disciplina) throws SQLException {
        String sql = "SELECT * FROM questoes WHERE disciplina = ? ORDER BY id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, disciplina);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Questao> questoes = new ArrayList<>();
                while (rs.next()) {
                    questoes.add(mapear(rs));
                }
                return questoes;
            }
        }
    }

    // Optional evita retornar null quando a questao nao existe.
    public Optional<Questao> buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM questoes WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
                return Optional.empty();
            }
        }
    }

    public List<Questao> buscarPorIds(List<Integer> ids) throws SQLException {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        String placeholders = String.join(",", ids.stream().map(id -> "?").toList());
        String sql = "SELECT * FROM questoes WHERE id IN (" + placeholders + ")";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < ids.size(); i++) {
                stmt.setInt(i + 1, ids.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                List<Questao> questoes = new ArrayList<>();
                while (rs.next()) {
                    questoes.add(mapear(rs));
                }
                return questoes;
            }
        }
    }

    // Insere uma questao e devolve o ID gerado pelo MySQL.
    public int inserir(Questao questao) throws SQLException {
        String sql = """
                INSERT INTO questoes (enunciado, alternativa_a, alternativa_b, alternativa_c,
                alternativa_d, alternativa_e, resposta_correta, disciplina, dificuldade)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherParametros(stmt, questao);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Não foi possível obter o ID da questão inserida");
    }

    // Atualiza todos os campos editaveis da questao.
    public void atualizar(Questao questao) throws SQLException {
        String sql = """
                UPDATE questoes SET enunciado = ?, alternativa_a = ?, alternativa_b = ?,
                alternativa_c = ?, alternativa_d = ?, alternativa_e = ?, resposta_correta = ?,
                disciplina = ?, dificuldade = ? WHERE id = ?
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencherParametros(stmt, questao);
            stmt.setInt(10, questao.getId());
            stmt.executeUpdate();
        }
    }

    // Remove a questao pelo ID.
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM questoes WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<String> listarDisciplinas() throws SQLException {
        String sql = "SELECT DISTINCT disciplina FROM questoes ORDER BY disciplina";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<String> disciplinas = new ArrayList<>();
            while (rs.next()) {
                disciplinas.add(rs.getString("disciplina"));
            }
            return disciplinas;
        }
    }

    // Reaproveita o preenchimento dos parametros de insert e update.
    private void preencherParametros(PreparedStatement stmt, Questao questao) throws SQLException {
        stmt.setString(1, questao.getEnunciado());
        stmt.setString(2, questao.getAlternativaA());
        stmt.setString(3, questao.getAlternativaB());
        stmt.setString(4, questao.getAlternativaC());
        stmt.setString(5, questao.getAlternativaD());
        stmt.setString(6, questao.getAlternativaE());
        stmt.setString(7, questao.getRespostaCorreta());
        stmt.setString(8, questao.getDisciplina());
        stmt.setString(9, questao.getDificuldade());
    }

    // Transforma uma linha do ResultSet em objeto Questao.
    private Questao mapear(ResultSet rs) throws SQLException {
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
        Timestamp criadoEm = rs.getTimestamp("criado_em");
        if (criadoEm != null) {
            questao.setCriadoEm(criadoEm.toLocalDateTime());
        }
        return questao;
    }
}
