package br.edu.geradorprovas.controller;

import br.edu.geradorprovas.model.Prova;
import br.edu.geradorprovas.service.ProvaService;
import br.edu.geradorprovas.service.QuestaoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller responsavel pelas provas.
 * Ele permite listar, criar, editar, excluir, visualizar e imprimir uma prova.
 */
public class ProvaController extends BaseController {

    private final ProvaService provaService = new ProvaService();
    private final QuestaoService questaoService = new QuestaoService();

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response, String acao)
            throws ServletException, IOException, SQLException {
        // Decide qual metodo executar de acordo com a acao recebida na URL.
        switch (acao == null ? "listar" : acao) {
            case "listar" -> listar(request, response);
            case "novo" -> formulario(request, response, new Prova());
            case "editar" -> editar(request, response);
            case "salvar" -> salvar(request, response);
            case "atualizar" -> atualizar(request, response);
            case "excluir" -> excluir(request, response);
            case "visualizar", "imprimir" -> visualizar(request, response, "imprimir".equals(acao));
            default -> redirecionar(request, response, "/provas?acao=listar");
        }
    }

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        request.setAttribute("provas", provaService.listarTodas());
        encaminhar(request, response, "provas/listar.jsp");
    }

    private void formulario(HttpServletRequest request, HttpServletResponse response, Prova prova)
            throws ServletException, IOException, SQLException {
        // Envia a prova e todas as questoes disponiveis para o formulario JSP.
        request.setAttribute("prova", prova);
        request.setAttribute("questoesDisponiveis", questaoService.listarTodas());
        encaminhar(request, response, "provas/form.jsp");
    }

    private void editar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        int id = obterId(request);
        Prova prova = provaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Prova não encontrada."));
        formulario(request, response, prova);
    }

    private void visualizar(HttpServletRequest request, HttpServletResponse response, boolean modoImpressao)
            throws ServletException, IOException, SQLException {
        int id = obterId(request);
        Prova prova = provaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Prova não encontrada."));
        request.setAttribute("prova", prova);
        request.setAttribute("modoImpressao", modoImpressao);
        encaminhar(request, response, "provas/visualizar.jsp");
    }

    private void salvar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        try {
            // Monta a prova e recupera as questoes marcadas no formulario.
            Prova prova = montarProva(request, null);
            List<Integer> questaoIds = obterQuestoesSelecionadas(request);
            provaService.salvar(prova, questaoIds);
            definirMensagem(request, "sucesso", "Prova criada com sucesso!");
            redirecionar(request, response, "/provas?acao=listar");
        } catch (IllegalArgumentException ex) {
            tratarErroFormulario(request, response, ex, null);
        }
    }

    private void atualizar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        try {
            int id = obterId(request);
            Prova prova = montarProva(request, id);
            provaService.atualizar(prova, obterQuestoesSelecionadas(request));
            definirMensagem(request, "sucesso", "Prova atualizada com sucesso!");
            redirecionar(request, response, "/provas?acao=listar");
        } catch (IllegalArgumentException ex) {
            tratarErroFormulario(request, response, ex, obterId(request));
        }
    }

    private void excluir(HttpServletRequest request, HttpServletResponse response)
            throws IOException, SQLException {
        try {
            provaService.excluir(obterId(request));
            definirMensagem(request, "sucesso", "Prova excluída com sucesso!");
        } catch (IllegalArgumentException ex) {
            definirMensagem(request, "erro", ex.getMessage());
        }
        redirecionar(request, response, "/provas?acao=listar");
    }

    /**
     * Converte os campos do formulario em um objeto Prova.
     */
    private Prova montarProva(HttpServletRequest request, Integer id) {
        Prova prova = new Prova();
        prova.setId(id);
        prova.setTitulo(request.getParameter("titulo"));
        prova.setDescricao(request.getParameter("descricao"));
        prova.setInstrucoes(request.getParameter("instrucoes"));
        return prova;
    }

    /**
     * Le os checkboxes marcados no formulario e transforma em lista de IDs.
     */
    private List<Integer> obterQuestoesSelecionadas(HttpServletRequest request) {
        String[] ids = request.getParameterValues("questaoIds");
        if (ids == null) {
            return List.of();
        }
        return Arrays.stream(ids)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    private void tratarErroFormulario(HttpServletRequest request, HttpServletResponse response,
                                      IllegalArgumentException ex, Integer id)
            throws ServletException, IOException {
        request.setAttribute("erro", ex.getMessage());
        try {
            formulario(request, response, montarProva(request, id));
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
