
package br.com.sugestfilmes.service;

import br.com.sugestfilmes.dao.UsuarioDAO;
import br.com.sugestfilmes.model.Usuario;

import com.google.gson.JsonObject;

import java.util.List;

public class UsuarioService {

    private final UsuarioDAO dao = new UsuarioDAO();

    public List<Usuario> listar(String token) {
        return dao.listar(token);
    }

    public Usuario buscar(Long id, String token) {
        return dao.buscar(id, token);
    }

    public void salvar(
            Usuario usuario,
            String senha,
            String token) {

        JsonObject body = new JsonObject();

        body.addProperty("nome", usuario.getNome());
        body.addProperty("login", usuario.getLogin());
        body.addProperty("senha", senha);
        body.addProperty("perfilId", usuario.getPerfilId());

        if (usuario.getId() == null) {
            dao.cadastrar(body, token);
        } else {
            dao.atualizar(usuario.getId(), body, token);
        }
    }

    public void excluir(Long id, String token) {
        dao.excluir(id, token);
    }
}
