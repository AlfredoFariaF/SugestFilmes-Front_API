package br.com.sugestfilmes.controller;

import br.com.sugestfilmes.api.ApiException;
import br.com.sugestfilmes.model.Filme;
import br.com.sugestfilmes.service.CategoriaService;
import br.com.sugestfilmes.service.FilmeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends BaseServlet {
    private final CategoriaService categorias = new CategoriaService();
    private final FilmeService filmes = new FilmeService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("categorias", categorias.listar(token(req)));
            List<Filme> all = filmes.listar(null, token(req));
            req.setAttribute("filmes", all.subList(0, Math.min(4, all.size())));
        } catch (ApiException e) { if (apiError(req, resp, e)) return; }
        forward(req, resp, "/WEB-INF/jsp/home.jsp");
    }
}
