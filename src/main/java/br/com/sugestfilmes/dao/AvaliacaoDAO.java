package br.com.sugestfilmes.dao;

import br.com.sugestfilmes.api.ApiClient;
import br.com.sugestfilmes.model.Avaliacao;
import com.google.gson.JsonObject;

import java.util.List;

public class AvaliacaoDAO {

    private final ApiClient api = ApiClient.getInstance();

    // GET - Avaliações do usuário autenticado
    public List<Avaliacao> minhas(String token) {
        return api.getList("/avaliacoes", token, Avaliacao.class);
    }

    // GET - Avaliações de um filme
    public List<Avaliacao> porFilme(String query, String token) {
        return api.getList("/avaliacoes" + query, token, Avaliacao.class);
    }

    // POST - Registrar avaliação
    public void cadastrar(JsonObject body, String token) {
        api.post("/avaliacoes", token, body);
    }

    // PUT - Atualizar avaliação
    public void atualizar(Long id, JsonObject body, String token) {
        api.put("/avaliacoes/" + id, token, body);
    }

    // DELETE - Excluir avaliação
    public void excluir(Long id, String token) {
        api.delete("/avaliacoes/" + id, token);
    }
}