package br.com.sugestfilmes.controller;

import br.com.sugestfilmes.api.ApiException;
import br.com.sugestfilmes.model.Usuario;
import br.com.sugestfilmes.service.AuthService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {
    private final AuthService authService = new AuthService();
    private final Gson gson = new Gson();
    private static final String VIEW = "/WEB-INF/jsp/login.jsp";

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (token(req) != null) { redirect(req, resp, "/home"); return; }
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("flashErro") != null) {
            req.setAttribute("erro", session.getAttribute("flashErro"));
            session.removeAttribute("flashErro");
        }
        forward(req, resp, VIEW);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            JsonObject result = authService.login(param(req, "login"), req.getParameter("senha"));
            String jwt = result.get("token").getAsString();
            Usuario user = gson.fromJson(result.getAsJsonObject("usuario"), Usuario.class);
            req.changeSessionId();
            HttpSession session = req.getSession(false);
            session.setAttribute("jwtToken", jwt);
            session.setAttribute("usuarioLogado", user);
            redirect(req, resp, "/home");
        } catch (ApiException e) {
            req.setAttribute("erro", e.getMessage());
            forward(req, resp, VIEW);
        } catch (RuntimeException e) {
            req.setAttribute("erro", "Não foi possível autenticar. Verifique os dados e a disponibilidade da API.");
            forward(req, resp, VIEW);
        }
    }
}
