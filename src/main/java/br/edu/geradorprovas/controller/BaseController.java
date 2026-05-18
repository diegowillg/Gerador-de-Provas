package br.edu.geradorprovas.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Classe base dos controllers.
 * Reune metodos comuns, como redirecionar, encaminhar para JSP e exibir mensagens.
 */
public abstract class BaseController {

  public abstract void executar(HttpServletRequest request, HttpServletResponse response, String acao)
      throws ServletException, IOException, SQLException;

  // Redireciona o navegador para outra rota do sistema.
  protected void redirecionar(HttpServletRequest request, HttpServletResponse response, String caminho)
      throws IOException {
    if (!caminho.startsWith("/")) {
      caminho = "/" + caminho;
    }
    response.sendRedirect(request.getContextPath() + caminho);
  }

  // Encaminha a requisicao para uma view dentro de WEB-INF/views.
  protected void encaminhar(HttpServletRequest request, HttpServletResponse response, String view)
      throws ServletException, IOException {
    request.getRequestDispatcher("/WEB-INF/views/" + view).forward(request, response);
  }

  // Salva uma mensagem temporaria na sessao para aparecer apos redirecionamentos.
  protected void definirMensagem(HttpServletRequest request, String tipo, String texto) {
    request.getSession().setAttribute("flashTipo", tipo);
    request.getSession().setAttribute("flashMensagem", texto);
  }

  // Recupera e valida o parametro id usado em editar, excluir e visualizar.
  protected int obterId(HttpServletRequest request) {
    String idParam = request.getParameter("id");
    if (idParam == null || idParam.isBlank()) {
      throw new IllegalArgumentException("ID não informado.");
    }
    return Integer.parseInt(idParam);
  }
}
