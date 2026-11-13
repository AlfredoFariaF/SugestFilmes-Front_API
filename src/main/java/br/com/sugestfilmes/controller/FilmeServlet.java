package br.com.sugestfilmes.controller;

import br.com.sugestfilmes.api.ApiException;
import br.com.sugestfilmes.model.*;
import br.com.sugestfilmes.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/filmes")
public class FilmeServlet extends BaseServlet {
    private static final String LISTA = "/WEB-INF/jsp/filmes/lista.jsp";
    private static final String FORM = "/WEB-INF/jsp/filmes/form.jsp";
    private static final String DETALHES = "/WEB-INF/jsp/filmes/detalhes.jsp";
    private final FilmeService filmes = new FilmeService();
    private final CategoriaService categorias = new CategoriaService();
    private final AvaliacaoService avaliacoes = new AvaliacaoService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            switch (acao(req)) {
                case "novo" -> { if (!requireAdmin(req, resp)) return; mostrarFormulario(req, resp, new Filme(), List.of()); }
                case "editar" -> {
                    if (!requireAdmin(req, resp)) return;
                    Filme filme = filmes.buscar(longParam(req, "id"), token(req));
                    if (filme == null) { resp.sendError(404, "Filme não encontrado."); return; }
                    List<Long> selected = filme.getCategorias() == null ? List.of() : filme.getCategorias().stream().map(Categoria::getId).toList();
                    mostrarFormulario(req, resp, filme, selected);
                }
                case "detalhes" -> detalhes(req, resp);
                default -> {
                    String busca = param(req, "busca");
                    req.setAttribute("busca", busca);
                    req.setAttribute("filmes", filmes.listar(busca, token(req)));
                    forward(req, resp, LISTA);
                }
            }
        } catch (ApiException e) {
            if (apiError(req, resp, e)) return;
            forward(req, resp, LISTA);
        }
    }

    private void detalhes(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = longParam(req, "id");
        Filme filme = filmes.buscar(id, token(req));
        if (filme == null) { resp.sendError(404, "Filme não encontrado."); return; }
        List<Avaliacao> reviews = avaliacoes.porFilme(id, token(req));
        Avaliacao minha = reviews.stream().filter(a -> Objects.equals(a.getUsuarioId(), usuario(req).getId())).findFirst().orElse(null);
        req.setAttribute("filme", filme);
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("flashErro") != null) {
            req.setAttribute("erro", session.getAttribute("flashErro"));
            session.removeAttribute("flashErro");
        }
        req.setAttribute("minhaAvaliacao", minha);
        req.setAttribute("avaliacoes", reviews);
        forward(req, resp, DETALHES);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!requireAdmin(req, resp)) return;
        String action = acao(req);
        try {
            if ("excluir".equals(action)) {
                filmes.excluir(longParam(req, "id"), token(req));
                redirect(req, resp, "/filmes");
                return;
            }
            Filme filme = new Filme();
            filme.setId(longParam(req, "id"));
            filme.setTitulo(param(req, "titulo"));
            filme.setDescricao(param(req, "descricao"));
            filme.setGenero(param(req, "genero"));
            filme.setAno(intParam(req, "ano"));
            filme.setDiretor(param(req, "diretor"));
            List<Long> ids = longList(req.getParameterValues("categoria"));
            filmes.salvar(filme, ids, token(req));
            redirect(req, resp, "/filmes");
        } catch (ApiException e) {
            if (apiError(req, resp, e)) return;
            Filme filme = fromRequest(req);
            List<Long> ids = longList(req.getParameterValues("categoria"));
            try { mostrarFormulario(req, resp, filme, ids); }
            catch (ApiException ignored) { forward(req, resp, FORM); }
        }
    }

    private Filme fromRequest(HttpServletRequest req) {
        Filme f = new Filme(); f.setId(longParam(req, "id")); f.setTitulo(param(req, "titulo"));
        f.setDescricao(param(req, "descricao")); f.setGenero(param(req, "genero")); f.setAno(intParam(req, "ano"));
        f.setDiretor(param(req, "diretor")); return f;
    }

    private void mostrarFormulario(HttpServletRequest req, HttpServletResponse resp, Filme filme, List<Long> selected)
            throws ServletException, IOException {
        req.setAttribute("filme", filme);
        req.setAttribute("categorias", categorias.listar(token(req)));
        req.setAttribute("categoriasSelecionadas", new HashSet<>(selected));
        forward(req, resp, FORM);
    }
}
