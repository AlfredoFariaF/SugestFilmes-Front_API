package br.com.sugestfilmes.filter;

import br.com.sugestfilmes.api.ApiClient;
import br.com.sugestfilmes.model.Usuario;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Set;

/** Protege páginas autenticadas e formulários contra CSRF. O JWT fica no servidor, na sessão. */
@WebFilter("/*")
public class SecurityFilter implements Filter {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Set<String> PUBLIC_PATHS = Set.of("/", "/index.jsp", "/login", "/login.jsp", "/favicon.ico");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // Define UTF-8 antes de qualquer Servlet/JSP escrever a resposta.
        // Isso evita que o Tomcat envie texto UTF-8 com charset incorreto.
        req.setCharacterEncoding(java.nio.charset.StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(java.nio.charset.StandardCharsets.UTF_8.name());

        String path = req.getRequestURI().substring(req.getContextPath().length());
        boolean publicPath = PUBLIC_PATHS.contains(path)
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/");

        HttpSession session = req.getSession(true);
        ensureCsrfToken(session);

        if (!publicPath) {
            // Páginas autenticadas não devem ser reutilizadas pelo cache do navegador.
            disableCaching(resp);

            boolean authenticated = session.getAttribute("jwtToken") != null
                    && session.getAttribute("usuarioLogado") != null;
            if (!authenticated) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            // A sessão do frontend é independente da API. Portanto, verificamos a API
            // em cada requisição protegida antes de permitir que o Controller execute.
            if (!ApiClient.getInstance().isAvailable()) {
                session.invalidate();
                HttpSession newSession = req.getSession(true);
                ensureCsrfToken(newSession);
                newSession.setAttribute("flashErro",
                        "A API está indisponível. A sessão foi encerrada. Verifique se o backend está ativo e entre novamente.");
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
        }

        if ("POST".equalsIgnoreCase(req.getMethod())) {
            String expected = (String) session.getAttribute("csrfToken");
            String supplied = req.getParameter("_csrf");
            if (expected == null || supplied == null || !constantTimeEquals(expected, supplied)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Token CSRF inválido. Atualize a página e tente novamente.");
                return;
            }
        }

        Usuario user = (Usuario) session.getAttribute("usuarioLogado");
        req.setAttribute("ehAdmin", user != null && user.getPerfil() != null
                && "Administrador".equalsIgnoreCase(user.getPerfil().getNome()));
        chain.doFilter(request, response);
    }

    private void ensureCsrfToken(HttpSession session) {
        if (session.getAttribute("csrfToken") == null) {
            byte[] bytes = new byte[32];
            RANDOM.nextBytes(bytes);
            session.setAttribute("csrfToken",
                    Base64.getUrlEncoder().withoutPadding().encodeToString(bytes));
        }
    }

    private void disableCaching(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        return java.security.MessageDigest.isEqual(a.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                b.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
