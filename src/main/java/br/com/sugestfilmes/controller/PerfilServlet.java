package br.com.sugestfilmes.controller;

import br.com.sugestfilmes.api.ApiException;
import br.com.sugestfilmes.model.Perfil;
import br.com.sugestfilmes.service.PerfilService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/perfis")
public class PerfilServlet extends BaseServlet {
    private static final String LISTA = "/WEB-INF/jsp/perfis/lista.jsp";
    private static final String FORM = "/WEB-INF/jsp/perfis/form.jsp";
    private final PerfilService service = new PerfilService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!requireAdmin(req, resp)) return;
        try {
            if ("novo".equals(acao(req))) { form(req, resp, new Perfil()); return; }
            if ("editar".equals(acao(req))) {
                Long id = longParam(req, "id");
                Perfil perfil = service.listar(token(req)).stream().filter(p -> id != null && id.equals(p.getId())).findFirst().orElse(null);
                if (perfil == null) { resp.sendError(404, "Perfil não encontrado."); return; }
                form(req, resp, perfil); return;
            }
            req.setAttribute("perfis", service.listar(token(req)));
            forward(req, resp, LISTA);
        } catch (ApiException e) { if (apiError(req, resp, e)) return; forward(req, resp, LISTA); }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!requireAdmin(req, resp)) return;
        try {
            if ("excluir".equals(acao(req))) service.excluir(longParam(req, "id"), token(req));
            else { Perfil p = new Perfil(); p.setId(longParam(req, "id")); p.setNome(param(req, "nome")); service.salvar(p, token(req)); }
            redirect(req, resp, "/perfis");
        } catch (ApiException e) {
            if (apiError(req, resp, e)) return;
            if ("excluir".equals(acao(req))) { req.setAttribute("perfis", java.util.List.of()); forward(req, resp, LISTA); }
            else { Perfil p = new Perfil(); p.setId(longParam(req, "id")); p.setNome(param(req, "nome")); form(req, resp, p); }
        }
    }
    private void form(HttpServletRequest req, HttpServletResponse resp, Perfil p) throws ServletException, IOException {
        req.setAttribute("perfil", p); forward(req, resp, FORM);
    }
}
