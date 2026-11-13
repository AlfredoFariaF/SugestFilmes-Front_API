package br.com.sugestfilmes.service;

import br.com.sugestfilmes.api.ApiClient;
import br.com.sugestfilmes.model.Filme;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FilmeService {
    private final ApiClient api = ApiClient.getInstance();
    public List<Filme> listar(String busca, String token) {
        String query = busca == null || busca.isBlank() ? "" : "?q=" + URLEncoder.encode(busca.trim(), StandardCharsets.UTF_8);
        return api.getList("/filmes" + query, token, Filme.class);
    }
    public Filme buscar(Long id, String token) { return api.getObject("/filmes/" + id, token, Filme.class); }
    public void salvar(Filme filme, List<Long> categoriasIds, String token) {
        JsonObject body = new JsonObject();
        body.addProperty("titulo", filme.getTitulo());
        body.addProperty("descricao", filme.getDescricao());
        body.addProperty("genero", filme.getGenero());
        body.addProperty("ano", filme.getAno());
        body.addProperty("diretor", filme.getDiretor());
        JsonArray ids = new JsonArray();
        if (categoriasIds != null) categoriasIds.forEach(ids::add);
        body.add("categoriasIds", ids);
        if (filme.getId() == null) api.post("/filmes", token, body);
        else api.put("/filmes/" + filme.getId(), token, body);
    }
    public void excluir(Long id, String token) { api.delete("/filmes/" + id, token); }
}
