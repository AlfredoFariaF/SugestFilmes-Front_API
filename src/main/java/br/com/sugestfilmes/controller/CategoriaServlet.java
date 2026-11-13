package br.com.sugestfilmes.controller;

import br.com.sugestfilmes.api.ApiException;
import br.com.sugestfilmes.model.Categoria;
import br.com.sugestfilmes.service.CategoriaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/categorias")
public class CategoriaServlet extends BaseServlet {
    private static final String LISTA = "/WEB-INF/jsp/categorias/lista.jsp";
    private static final String FORM = "/WEB-INF/jsp/categorias/form.jsp";
    private final CategoriaService service = new CategoriaService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            switch (acao(req)) {
                case "novo" -> { if (!requireAdmin(req, resp)) return; form(req, resp, new Categoria()); }
                case "editar" -> {
                    if (!requireAdmin(req, resp)) return;
                    Categoria c = service.buscar(longParam(req, "id"), token(req));
                    if (c == null) { resp.sendError(404, "Categoria não encontrada."); return; }
                    form(req, resp, c);
                }
                default -> { req.setAttribute("categorias", service.listar(token(req))); forward(req, resp, LISTA); }
            }
        } catch (ApiException e) { if (apiError(req, resp, e)) return; forward(req, resp, LISTA); }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!requireAdmin(req, resp)) return;
        try {
            if ("excluir".equals(acao(req))) service.excluir(longParam(req, "id"), token(req));
            else { Categoria c = new Categoria(); c.setId(longParam(req, "id")); c.setNome(param(req, "nome")); service.salvar(c, token(req)); }
            redirect(req, resp, "/categorias");
        } catch (ApiException e) {
            if (apiError(req, resp, e)) return;
            if (longParam(req, "id") != null && !"excluir".equals(acao(req))) {
                Categoria c = new Categoria(); c.setId(longParam(req, "id")); c.setNome(param(req, "nome")); form(req, resp, c);
            } else { req.setAttribute("categorias", java.util.List.of()); forward(req, resp, LISTA); }
        }
    }
    private void form(HttpServletRequest req, HttpServletResponse resp, Categoria c) throws ServletException, IOException {
        req.setAttribute("categoria", c); forward(req, resp, FORM);
    }
}
