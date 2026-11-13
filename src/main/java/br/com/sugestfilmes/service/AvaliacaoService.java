package br.com.sugestfilmes.service;

import br.com.sugestfilmes.api.ApiClient;
import br.com.sugestfilmes.model.Avaliacao;
import com.google.gson.JsonObject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AvaliacaoService {
    private final ApiClient api = ApiClient.getInstance();
    public List<Avaliacao> minhas(String token) { return api.getList("/avaliacoes", token, Avaliacao.class); }
    public List<Avaliacao> porFilme(Long filmeId, String token) {
        return api.getList("/avaliacoes?filmeId=" + URLEncoder.encode(String.valueOf(filmeId), StandardCharsets.UTF_8), token, Avaliacao.class);
    }
    public void salvar(Long filmeId, int nota, String comentario, String token) {
        JsonObject body = new JsonObject(); body.addProperty("filmeId", filmeId); body.addProperty("nota", nota); body.addProperty("comentario", comentario);
        api.post("/avaliacoes", token, body);
    }
    public void atualizar(Long id, int nota, String comentario, String token) {
        JsonObject body = new JsonObject(); body.addProperty("nota", nota); body.addProperty("comentario", comentario);
        api.put("/avaliacoes/" + id, token, body);
    }
    public void excluir(Long id, String token) { api.delete("/avaliacoes/" + id, token); }
}
