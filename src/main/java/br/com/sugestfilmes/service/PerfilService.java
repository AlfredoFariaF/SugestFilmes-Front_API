package br.com.sugestfilmes.service;

import br.com.sugestfilmes.api.ApiClient;
import br.com.sugestfilmes.model.Perfil;
import com.google.gson.JsonObject;
import java.util.List;

public class PerfilService {
    private final ApiClient api = ApiClient.getInstance();
    public List<Perfil> listar(String token) { return api.getList("/perfis", token, Perfil.class); }
    public void salvar(Perfil perfil, String token) {
        JsonObject body = new JsonObject(); body.addProperty("nome", perfil.getNome());
        if (perfil.getId() == null) api.post("/perfis", token, body);
        else api.put("/perfis/" + perfil.getId(), token, body);
    }
    public void excluir(Long id, String token) { api.delete("/perfis/" + id, token); }
}
