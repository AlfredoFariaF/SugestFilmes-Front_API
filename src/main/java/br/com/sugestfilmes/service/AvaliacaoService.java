package br.com.sugestfilmes.service;

import br.com.sugestfilmes.dao.AvaliacaoDAO;
import br.com.sugestfilmes.model.Avaliacao;

import com.google.gson.JsonObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AvaliacaoService {

    private final AvaliacaoDAO dao = new AvaliacaoDAO();

    // Consultar minhas avaliações
    public List<Avaliacao> minhas(String token) {
        return dao.minhas(token);
    }

    // Consultar avaliações de um filme
    public List<Avaliacao> porFilme(Long filmeId, String token) {

        String query = "?filmeId=" + URLEncoder.encode(
                String.valueOf(filmeId),
                StandardCharsets.UTF_8
        );

        return dao.porFilme(query, token);
    }

    // Registrar avaliação
    public void salvar(
            Long filmeId,
            int nota,
            String comentario,
            String token) {

        JsonObject body = new JsonObject();

        body.addProperty("filmeId", filmeId);
        body.addProperty("nota", nota);
        body.addProperty("comentario", comentario);

        dao.cadastrar(body, token);
    }

    // Atualizar avaliação
    public void atualizar(
            Long id,
            int nota,
            String comentario,
            String token) {

        JsonObject body = new JsonObject();

        body.addProperty("nota", nota);
        body.addProperty("comentario", comentario);

        dao.atualizar(id, body, token);
    }

    // Excluir avaliação
    public void excluir(Long id, String token) {
        dao.excluir(id, token);
    }
}