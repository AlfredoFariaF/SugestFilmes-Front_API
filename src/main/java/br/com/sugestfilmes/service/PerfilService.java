
package br.com.sugestfilmes.service;

import br.com.sugestfilmes.dao.PerfilDAO;
import br.com.sugestfilmes.model.Perfil;

import com.google.gson.JsonObject;

import java.util.List;

public class PerfilService {

    private final PerfilDAO dao = new PerfilDAO();

    public List<Perfil> listar(String token) {
        return dao.listar(token);
    }

    public void salvar(Perfil perfil, String token) {

        JsonObject body = new JsonObject();
        body.addProperty("nome", perfil.getNome());

        if (perfil.getId() == null) {
            dao.cadastrar(body, token);
        } else {
            dao.atualizar(perfil.getId(), body, token);
        }
    }

    public void excluir(Long id, String token) {
        dao.excluir(id, token);
    }
}
