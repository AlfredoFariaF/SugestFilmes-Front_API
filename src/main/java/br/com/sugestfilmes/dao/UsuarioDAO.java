
package br.com.sugestfilmes.dao;

import br.com.sugestfilmes.api.ApiClient;
import br.com.sugestfilmes.model.Usuario;

import com.google.gson.JsonObject;

import java.util.List;

public class UsuarioDAO {

    private final ApiClient api = ApiClient.getInstance();

    public List<Usuario> listar(String token) {
        return api.getList("/usuarios", token, Usuario.class);
    }

    public Usuario buscar(Long id, String token) {
        return api.getObject(
                "/usuarios/" + id,
                token,
                Usuario.class);
    }

    public void cadastrar(JsonObject body, String token) {
        api.post("/usuarios", token, body);
    }

    public void atualizar(
            Long id,
            JsonObject body,
            String token) {

        api.put("/usuarios/" + id, token, body);
    }

    public void excluir(Long id, String token) {
        api.delete("/usuarios/" + id, token);
    }
}
