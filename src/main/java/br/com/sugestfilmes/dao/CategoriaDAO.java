package br.com.sugestfilmes.dao;

import br.com.sugestfilmes.api.ApiClient;
import br.com.sugestfilmes.model.Categoria;
import com.google.gson.JsonObject;

import java.util.List;

public class CategoriaDAO {

    private final ApiClient api = ApiClient.getInstance();

    public List<Categoria> listar(String token) {
        return api.getList("/categorias", token, Categoria.class);
    }

    public Categoria buscar(Long id, String token) {
        return api.getObject("/categorias/" + id, token, Categoria.class);
    }

    public void cadastrar(JsonObject body, String token) {
        api.post("/categorias", token, body);
    }

    public void atualizar(Long id, JsonObject body, String token) {
        api.put("/categorias/" + id, token, body);
    }

    public void excluir(Long id, String token) {
        api.delete("/categorias/" + id, token);
    }
}