package br.com.sugestfilmes.controller;

import br.com.sugestfilmes.api.ApiException;
import br.com.sugestfilmes.model.Avaliacao;
import br.com.sugestfilmes.model.Filme;
import br.com.sugestfilmes.service.AvaliacaoService;
import br.com.sugestfilmes.service.FilmeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@WebServlet("/avaliacoes")
public class AvaliacaoServlet extends BaseServlet {
    private final AvaliacaoService service = new AvaliacaoService();
    private final FilmeService filmes = new FilmeService();
    private static final String LISTA = "/WEB-INF/jsp/avaliacoes/lista.jsp";
    private static final String FORM = "/WEB-INF/jsp/avaliacoes/form.jsp";

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            if ("editar".equals(acao(req))) {
                Long id = longParam(req, "id");
                Avaliacao avaliacao = service.minhas(token(req)).stream()
                        .filter(a -> id != null && id.equals(a.getId())).findFirst().orElse(null);
                if (avaliacao == null) { resp.sendError(404, "Avaliação não encontrada."); return; }
                Filme filme = filmes.buscar(avaliacao.getFilmeId(), token(req));
                req.setAttribute("avaliacao", avaliacao);
                req.setAttribute("filme", filme);
                forward(req, resp, FORM);
                return;
            }
            if ("excluir".equals(acao(req)) || "atualizar".equals(acao(req))) {
                resp.sendError(405, "Esta operação deve ser enviada por POST.");
                return;
            }
            Long filmeId = longParam(req, "filmeId");
            List<Avaliacao> lista = filmeId == null ? service.minhas(token(req)) : service.porFilme(filmeId, token(req));
            req.setAttribute("avaliacoes", lista);
            req.setAttribute("filmeId", filmeId);
        } catch (ApiException e) { if (apiError(req, resp, e)) return; }
        forward(req, resp, LISTA);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = acao(req);
        Long id = longParam(req, "id");
        Long filmeId = longParam(req, "filmeId");
        try {
            if ("excluir".equals(action)) {
                service.excluir(id, token(req));
            } else if ("atualizar".equals(action)) {
                Integer nota = intParam(req, "nota");
                service.atualizar(id, nota == null ? 0 : nota, param(req, "comentario"), token(req));
            } else {
                Integer nota = intParam(req, "nota");
                service.salvar(filmeId, nota == null ? 0 : nota, param(req, "comentario"), token(req));
            }

            if (filmeId != null) redirect(req, resp, "/avaliacoes?filmeId=" + filmeId);
            else redirect(req, resp, "/avaliacoes");
        } catch (ApiException e) {
            if (apiError(req, resp, e)) return;
            if ("atualizar".equals(action)) {
                req.setAttribute("erro", e.getMessage());
                Avaliacao atual = new Avaliacao();
                atual.setId(id);
                atual.setFilmeId(filmeId);
                atual.setNota(intParam(req, "nota") == null ? 0 : intParam(req, "nota"));
                atual.setComentario(param(req, "comentario"));
                req.setAttribute("avaliacao", atual);
                try { req.setAttribute("filme", filmeId == null ? null : filmes.buscar(filmeId, token(req))); }
                catch (ApiException ignored) { }
                forward(req, resp, FORM);
            } else if ("excluir".equals(action)) {
                req.setAttribute("erro", e.getMessage());
                req.setAttribute("avaliacoes", List.of());
                forward(req, resp, LISTA);
            } else {
                req.getSession(true).setAttribute("flashErro", e.getMessage());
                redirect(req, resp, "/filmes?acao=detalhes&id=" + filmeId);
            }
        }
    }

}
