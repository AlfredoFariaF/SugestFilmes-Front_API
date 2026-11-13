package br.com.sugestfilmes.controller;

import br.com.sugestfilmes.api.ApiException;
import br.com.sugestfilmes.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseServlet extends HttpServlet {
    protected String token(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (String) session.getAttribute("jwtToken");
    }
    protected Usuario usuario(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (Usuario) session.getAttribute("usuarioLogado");
    }
    protected boolean isAdmin(HttpServletRequest req) {
        Usuario u = usuario(req);
        return u != null && u.getPerfil() != null && "Administrador".equalsIgnoreCase(u.getPerfil().getNome());
    }
    protected boolean requireAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (isAdmin(req)) return true;
        resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso permitido somente para administradores.");
        return false;
    }
    protected void forward(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        req.getRequestDispatcher(view).forward(req, resp);
    }
    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        resp.sendRedirect(req.getContextPath() + path);
    }
    protected String param(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        return value == null ? null : value.trim();
    }
    protected Long longParam(HttpServletRequest req, String name) {
        String value = param(req, name);
        if (value == null || value.isBlank()) return null;
        try { return Long.valueOf(value); } catch (NumberFormatException e) { return null; }
    }
    protected Integer intParam(HttpServletRequest req, String name) {
        String value = param(req, name);
        if (value == null || value.isBlank()) return null;
        try { return Integer.valueOf(value); } catch (NumberFormatException e) { return null; }
    }
    protected List<Long> longList(String[] values) {
        List<Long> result = new ArrayList<>();
        if (values == null) return result;
        for (String value : values) {
            try { result.add(Long.valueOf(value)); } catch (NumberFormatException ignored) { }
        }
        return result;
    }
    protected boolean apiError(HttpServletRequest req, HttpServletResponse resp, ApiException e) throws IOException {
        if (e.getStatusCode() == 401) {
            HttpSession session = req.getSession(false);
            if (session != null) session.invalidate();
            HttpSession newSession = req.getSession(true);
            newSession.setAttribute("flashErro", "Sua sessão expirou. Entre novamente para continuar.");
            redirect(req, resp, "/login");
            return true;
        }
        req.setAttribute("erro", e.getMessage());
        return false;
    }
    protected String acao(HttpServletRequest req) {
        String action = param(req, "acao");
        return action == null ? "" : action.toLowerCase();
    }
}
