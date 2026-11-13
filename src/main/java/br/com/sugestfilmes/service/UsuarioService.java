package br.com.sugestfilmes.service;

import br.com.sugestfilmes.api.ApiClient;
import br.com.sugestfilmes.model.Usuario;
import com.google.gson.JsonObject;
import java.util.List;

public class UsuarioService {
    private final ApiClient api = ApiClient.getInstance();
    public List<Usuario> listar(String token) { return api.getList("/usuarios", token, Usuario.class); }
    public Usuario buscar(Long id, String token) { return api.getObject("/usuarios/" + id, token, Usuario.class); }
    public void salvar(Usuario usuario, String senha, String token) {
        JsonObject body = new JsonObject();
        body.addProperty("nome", usuario.getNome()); body.addProperty("login", usuario.getLogin());
        body.addProperty("senha", senha); body.addProperty("perfilId", usuario.getPerfilId());
        if (usuario.getId() == null) api.post("/usuarios", token, body);
        else api.put("/usuarios/" + usuario.getId(), token, body);
    }
    public void excluir(Long id, String token) { api.delete("/usuarios/" + id, token); }
}
