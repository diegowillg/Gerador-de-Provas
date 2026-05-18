package br.edu.geradorprovas.controller;

import br.edu.geradorprovas.model.Questao;
import br.edu.geradorprovas.service.QuestaoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller responsavel pelo CRUD de questoes.
 * Recebe os dados dos formularios, chama a camada Service e escolhe qual JSP sera exibido.
 */
public class QuestaoController extends BaseController {

    private final QuestaoService questaoService = new QuestaoService();

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response, String acao)
            throws ServletException, IOException, SQLException {
        // A acao vem pela URL, por exemplo: /questoes?acao=novo.
        switch (acao == null ? "listar" : acao) {
            case "listar" -> listar(request, response);
            case "novo" -> formulario(request, response, new Questao());
            case "editar" -> editar(request, response);
            case "salvar" -> salvar(request, response);
            case "atualizar" -> atualizar(request, response);
            case "excluir" -> excluir(request, response);
            case "detalhe" -> detalhe(request, response);
            default -> redirecionar(request, response, "/questoes?acao=listar");
        }
    }

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        // Lista as questoes e aplica filtro por disciplina quando informado.
        String disciplina = request.getParameter("disciplina");
        request.setAttribute("questoes", questaoService.listarPorDisciplina(disciplina));
        request.setAttribute("disciplinas", questaoService.listarDisciplinas());
        request.setAttribute("disciplinaFiltro", disciplina);
        encaminhar(request, response, "questoes/listar.jsp");
    }

    private void formulario(HttpServletRequest request, HttpServletResponse response, Questao questao)
            throws ServletException, IOException {
        // O mesmo JSP atende cadastro e edicao; o objeto define se ha ID ou nao.
        request.setAttribute("questao", questao);
        encaminhar(request, response, "questoes/form.jsp");
    }

    private void editar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        // Antes de abrir o formulario de edicao, carrega a questao existente.
        int id = obterId(request);
        Questao questao = questaoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Questão não encontrada."));
        formulario(request, response, questao);
    }

    private void detalhe(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        // A tela de detalhe mostra o enunciado, alternativas e gabarito.
        int id = obterId(request);
        Questao questao = questaoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Questão não encontrada."));
        request.setAttribute("questao", questao);
        encaminhar(request, response, "questoes/detalhe.jsp");
    }

    private void salvar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        try {
            // Monta o objeto com os dados enviados pelo formulario.
            Questao questao = montarQuestao(request, null);
            questaoService.salvar(questao);
            definirMensagem(request, "sucesso", "Questão cadastrada com sucesso!");
            redirecionar(request, response, "/questoes?acao=listar");
        } catch (IllegalArgumentException ex) {
            tratarErroFormulario(request, response, ex, null);
        }
    }

    private void atualizar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        try {
            int id = obterId(request);
            Questao questao = montarQuestao(request, id);
            questaoService.atualizar(questao);
            definirMensagem(request, "sucesso", "Questão atualizada com sucesso!");
            redirecionar(request, response, "/questoes?acao=listar");
        } catch (IllegalArgumentException ex) {
            tratarErroFormulario(request, response, ex, obterId(request));
        }
    }

    private void excluir(HttpServletRequest request, HttpServletResponse response)
            throws IOException, SQLException {
        try {
            questaoService.excluir(obterId(request));
            definirMensagem(request, "sucesso", "Questão excluída com sucesso!");
        } catch (IllegalArgumentException ex) {
            definirMensagem(request, "erro", ex.getMessage());
        }
        redirecionar(request, response, "/questoes?acao=listar");
    }

    /**
     * Converte os parametros do formulario HTML em um objeto Questao.
     * Esse objeto depois e validado pela camada Service.
     */
    private Questao montarQuestao(HttpServletRequest request, Integer id) {
        Questao questao = new Questao();
        questao.setId(id);
        questao.setEnunciado(request.getParameter("enunciado"));
        questao.setAlternativaA(request.getParameter("alternativaA"));
        questao.setAlternativaB(request.getParameter("alternativaB"));
        questao.setAlternativaC(request.getParameter("alternativaC"));
        questao.setAlternativaD(request.getParameter("alternativaD"));
        questao.setAlternativaE(request.getParameter("alternativaE"));
        questao.setRespostaCorreta(request.getParameter("respostaCorreta"));
        questao.setDisciplina(request.getParameter("disciplina"));
        questao.setDificuldade(request.getParameter("dificuldade"));
        return questao;
    }

    private void tratarErroFormulario(HttpServletRequest request, HttpServletResponse response,
                                      IllegalArgumentException ex, Integer id)
            throws ServletException, IOException {
        // Reexibe o formulario com os dados digitados quando a validacao falha.
        request.setAttribute("erro", ex.getMessage());
        request.setAttribute("questao", montarQuestao(request, id));
        try {
            encaminhar(request, response, "questoes/form.jsp");
        } catch (ServletException | IOException e) {
            definirMensagem(request, "erro", ex.getMessage());
            try {
                redirecionar(request, response, "/questoes?acao=listar");
            } catch (IOException ioException) {
                throw new ServletException(ioException);
            }
        }
    }
}
