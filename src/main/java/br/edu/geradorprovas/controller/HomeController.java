package br.edu.geradorprovas.controller;

import br.edu.geradorprovas.service.ProvaService;
import br.edu.geradorprovas.service.QuestaoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller da pagina inicial.
 * Busca alguns totais no sistema para mostrar um resumo na home.
 */
public class HomeController extends BaseController {

    private final QuestaoService questaoService = new QuestaoService();
    private final ProvaService provaService = new ProvaService();

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response, String acao)
            throws ServletException, IOException, SQLException {
        // Os atributos definidos aqui sao usados no arquivo home.jsp.
        request.setAttribute("totalQuestoes", questaoService.listarTodas().size());
        request.setAttribute("totalProvas", provaService.listarTodas().size());
        var disciplinas = questaoService.listarDisciplinas();
        request.setAttribute("disciplinas", disciplinas);
        request.setAttribute("totalDisciplinas", disciplinas.size());
        encaminhar(request, response, "home.jsp");
    }
}
