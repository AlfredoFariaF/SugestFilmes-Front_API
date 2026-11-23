package br.com.sugestfilmes.service;

import br.com.sugestfilmes.dao.CategoriaDAO;
import br.com.sugestfilmes.model.Categoria;
import com.google.gson.JsonObject;

import java.util.List;

public class CategoriaService {

    private final CategoriaDAO dao = new CategoriaDAO();

    public List<Categoria> listar(String token) {
        return dao.listar(token);
    }

    public Categoria buscar(Long id, String token) {
        return dao.buscar(id, token);
    }

    public void salvar(Categoria categoria, String token) {

        JsonObject body = new JsonObject();
        body.addProperty("nome", categoria.getNome());

        if (categoria.getId() == null) {
            dao.cadastrar(body, token);
        } else {
            dao.atualizar(categoria.getId(), body, token);
        }
    }

    public void excluir(Long id, String token) {
        dao.excluir(id, token);
    }
}