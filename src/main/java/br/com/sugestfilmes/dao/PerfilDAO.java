
package br.com.sugestfilmes.dao;

import br.com.sugestfilmes.api.ApiClient;
import br.com.sugestfilmes.model.Perfil;

import com.google.gson.JsonObject;

import java.util.List;

public class PerfilDAO {

    private final ApiClient api = ApiClient.getInstance();

    public List<Perfil> listar(String token) {
        return api.getList("/perfis", token, Perfil.class);
    }

    public void cadastrar(JsonObject body, String token) {
        api.post("/perfis", token, body);
    }

    public void atualizar(
            Long id,
            JsonObject body,
            String token) {

        api.put("/perfis/" + id, token, body);
    }

    public void excluir(Long id, String token) {
        api.delete("/perfis/" + id, token);
    }
}
