package br.com.sugestfilmes.service;

import br.com.sugestfilmes.dao.FilmeDAO;
import br.com.sugestfilmes.model.Filme;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FilmeService {

    private final FilmeDAO dao = new FilmeDAO();

    public List<Filme> listar(String busca, String token) {

        String query = busca == null || busca.isBlank()
                ? ""
                : "?q=" + URLEncoder.encode(
                        busca.trim(), StandardCharsets.UTF_8);

        return dao.listar(query, token);
    }

    public Filme buscar(Long id, String token) {
        return dao.buscar(id, token);
    }

    public void salvar(
            Filme filme,
            List<Long> categoriasIds,
            String token) {

        JsonObject body = new JsonObject();

        body.addProperty("titulo", filme.getTitulo());
        body.addProperty("descricao", filme.getDescricao());
        body.addProperty("genero", filme.getGenero());
        body.addProperty("ano", filme.getAno());
        body.addProperty("diretor", filme.getDiretor());

        JsonArray ids = new JsonArray();

        if (categoriasIds != null) {
            categoriasIds.forEach(ids::add);
        }

        body.add("categoriasIds", ids);

        if (filme.getId() == null) {
            dao.cadastrar(body, token);
        } else {
            dao.atualizar(filme.getId(), body, token);
        }
    }

    public void excluir(Long id, String token) {
        dao.excluir(id, token);
    }
}