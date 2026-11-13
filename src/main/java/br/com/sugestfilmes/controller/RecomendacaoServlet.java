package br.com.sugestfilmes.controller;

import br.com.sugestfilmes.api.ApiException;
import br.com.sugestfilmes.service.CategoriaService;
import br.com.sugestfilmes.service.RecomendacaoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/recomendacoes")
public class RecomendacaoServlet extends BaseServlet {
    private final CategoriaService categorias = new CategoriaService();
    private final RecomendacaoService recomendacoes = new RecomendacaoService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Long> selected = longList(req.getParameterValues("categoria"));
            req.setAttribute("categorias", categorias.listar(token(req)));
            req.setAttribute("selecionadas", selected);
            req.setAttribute("filmes", selected.isEmpty() ? List.of() : recomendacoes.recomendar(selected, token(req)));
        } catch (ApiException e) { if (apiError(req, resp, e)) return; }
        forward(req, resp, "/WEB-INF/jsp/recomendacoes/lista.jsp");
    }
}
