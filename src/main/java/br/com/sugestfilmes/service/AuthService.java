
package br.com.sugestfilmes.service;

import br.com.sugestfilmes.dao.AuthDAO;

import com.google.gson.JsonObject;

public class AuthService {

    private final AuthDAO dao = new AuthDAO();

    public JsonObject login(String login, String senha) {

        JsonObject payload = new JsonObject();

        payload.addProperty(
                "login",
                login == null ? "" : login.trim());

        payload.addProperty(
                "senha",
                senha == null ? "" : senha);

        return dao.login(payload);
    }
}
