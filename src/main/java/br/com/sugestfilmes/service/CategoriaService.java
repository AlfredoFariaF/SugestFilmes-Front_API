package br.com.sugestfilmes.service;

import br.com.sugestfilmes.api.ApiClient;
import br.com.sugestfilmes.model.Categoria;
import com.google.gson.JsonObject;
import java.util.List;

public class CategoriaService {
    private final ApiClient api = ApiClient.getInstance();
    public List<Categoria> listar(String token) { return api.getList("/categorias", token, Categoria.class); }
    public Categoria buscar(Long id, String token) { return api.getObject("/categorias/" + id, token, Categoria.class); }
    public void salvar(Categoria categoria, String token) {
        JsonObject body = new JsonObject(); body.addProperty("nome", categoria.getNome());
        if (categoria.getId() == null) api.post("/categorias", token, body);
        else api.put("/categorias/" + categoria.getId(), token, body);
    }
    public void excluir(Long id, String token) { api.delete("/categorias/" + id, token); }
}
