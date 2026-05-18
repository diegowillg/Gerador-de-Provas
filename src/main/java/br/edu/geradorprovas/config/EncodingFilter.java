package br.edu.geradorprovas.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Filtro executado em todas as requisicoes da aplicacao.
 * Ele define UTF-8 para evitar problemas com acentos em formularios e paginas JSP.
 */
@WebFilter(filterName = "EncodingFilter", urlPatterns = "/*")
public class EncodingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        res.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // Arquivos estaticos nao recebem contentType de HTML.
        String uri = req.getRequestURI();
        boolean recursoEstatico = uri.endsWith(".css") || uri.endsWith(".js")
                || uri.endsWith(".ico") || uri.endsWith(".png") || uri.endsWith(".jpg");
        if (!recursoEstatico) {
            res.setContentType("text/html; charset=UTF-8");
        }

        chain.doFilter(request, response);
    }
}
