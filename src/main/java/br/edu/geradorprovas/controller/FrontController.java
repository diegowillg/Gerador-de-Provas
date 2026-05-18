package br.edu.geradorprovas.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

/**
 * Servlet principal do projeto.
 * Ele funciona como Front Controller: recebe as URLs e encaminha cada uma
 * para o controller correto.
 */
@WebServlet(name = "FrontController", urlPatterns = {"", "/", "/home", "/questoes", "/provas"})
public class FrontController extends HttpServlet {

    // Mapa simples de rotas do sistema.
    private final Map<String, BaseController> rotas = Map.of(
            "home", new HomeController(),
            "questoes", new QuestaoController(),
            "provas", new ProvaController()
    );

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processar(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processar(request, response);
    }

    private void processar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if (path == null || path.isBlank() || "/".equals(path)) {
            path = "/home";
        }

        // Remove a barra inicial para localizar o controller no mapa de rotas.
        String recurso = path.startsWith("/") ? path.substring(1) : path;
        BaseController controller = rotas.getOrDefault(recurso, rotas.get("home"));
        String acao = request.getParameter("acao");

        try {
            controller.executar(request, response, acao);
        } catch (IllegalArgumentException ex) {
            // Erros de validacao aparecem como mensagem amigavel na tela.
            request.getSession().setAttribute("flashTipo", "erro");
            request.getSession().setAttribute("flashMensagem", ex.getMessage());
            response.sendRedirect(request.getContextPath() + "/" + recurso + "?acao=listar");
        } catch (SQLException ex) {
            throw new ServletException("Erro ao acessar o banco de dados.", ex);
        }
    }
}
