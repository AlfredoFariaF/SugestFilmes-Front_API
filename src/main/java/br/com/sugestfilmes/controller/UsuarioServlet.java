package br.com.sugestfilmes.controller;

import br.com.sugestfilmes.api.ApiException;
import br.com.sugestfilmes.model.Perfil;
import br.com.sugestfilmes.model.Usuario;
import br.com.sugestfilmes.service.PerfilService;
import br.com.sugestfilmes.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/usuarios")
public class UsuarioServlet extends BaseServlet {
    private static final String LISTA = "/WEB-INF/jsp/usuarios/lista.jsp";
    private static final String FORM = "/WEB-INF/jsp/usuarios/form.jsp";
    private final UsuarioService usuarios = new UsuarioService();
    private final PerfilService perfis = new PerfilService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!requireAdmin(req, resp)) return;
        try {
            switch (acao(req)) {
                case "novo" -> form(req, resp, new Usuario());
                case "editar" -> {
                    Usuario u = usuarios.buscar(longParam(req, "id"), token(req));
                    if (u == null) { resp.sendError(404, "Usuário não encontrado."); return; }
                    form(req, resp, u);
                }
                default -> { req.setAttribute("usuarios", usuarios.listar(token(req))); forward(req, resp, LISTA); }
            }
        } catch (ApiException e) { if (apiError(req, resp, e)) return; forward(req, resp, LISTA); }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!requireAdmin(req, resp)) return;
        try {
            if ("excluir".equals(acao(req))) usuarios.excluir(longParam(req, "id"), token(req));
            else {
                Usuario u = new Usuario(); u.setId(longParam(req, "id")); u.setNome(param(req, "nome"));
                u.setLogin(param(req, "login")); u.setPerfilId(longParam(req, "perfilId"));
                usuarios.salvar(u, req.getParameter("senha"), token(req));
            }
            redirect(req, resp, "/usuarios");
        } catch (ApiException e) {
            if (apiError(req, resp, e)) return;
            if ("excluir".equals(acao(req))) { req.setAttribute("usuarios", java.util.List.of()); forward(req, resp, LISTA); }
            else {
                Usuario u = new Usuario(); u.setId(longParam(req, "id")); u.setNome(param(req, "nome"));
                u.setLogin(param(req, "login")); u.setPerfilId(longParam(req, "perfilId")); form(req, resp, u);
            }
        }
    }

    private void form(HttpServletRequest req, HttpServletResponse resp, Usuario u) throws ServletException, IOException {
        List<Perfil> lista = perfis.listar(token(req));
        req.setAttribute("usuario", u); req.setAttribute("perfis", lista);
        forward(req, resp, FORM);
    }
}
