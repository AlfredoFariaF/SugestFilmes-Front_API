package br.com.sugestfilmes.dao;

import br.com.sugestfilmes.api.ApiClient;
import br.com.sugestfilmes.model.Filme;

import com.google.gson.JsonObject;

import java.util.List;

public class FilmeDAO {

    private final ApiClient api = ApiClient.getInstance();

    public List<Filme> listar(String query, String token) {
        return api.getList("/filmes" + query, token, Filme.class);
    }

    public Filme buscar(Long id, String token) {
        return api.getObject("/filmes/" + id, token, Filme.class);
    }

    public void cadastrar(JsonObject body, String token) {
        api.post("/filmes", token, body);
    }

    public void atualizar(Long id, JsonObject body, String token) {
        api.put("/filmes/" + id, token, body);
    }

    public void excluir(Long id, String token) {
        api.delete("/filmes/" + id, token);
    }
}