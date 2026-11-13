package br.com.sugestfilmes.service;

import br.com.sugestfilmes.api.ApiClient;
import com.google.gson.JsonObject;

public class AuthService {
    private final ApiClient api = ApiClient.getInstance();

    public JsonObject login(String login, String senha) {
        JsonObject payload = new JsonObject();
        payload.addProperty("login", login == null ? "" : login.trim());
        payload.addProperty("senha", senha == null ? "" : senha);
        return api.post("/auth/login", null, payload).getAsJsonObject();
    }
}
