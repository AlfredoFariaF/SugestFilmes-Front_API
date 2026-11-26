
package br.com.sugestfilmes.dao;

import br.com.sugestfilmes.api.ApiClient;

import com.google.gson.JsonObject;

public class AuthDAO {

    private final ApiClient api = ApiClient.getInstance();

    public JsonObject login(JsonObject payload) {

        return api.post(
                "/auth/login",
                null,
                payload).getAsJsonObject();
    }
}
