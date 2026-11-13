package br.com.sugestfilmes.api;

import br.com.sugestfilmes.config.ApiConfig;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/** Cliente HTTP único para integração servidor-servidor com a SugestFilmes-API. */
public final class ApiClient {
    private static final ApiClient INSTANCE = new ApiClient();
    private final HttpClient http;
    private final Gson gson = new GsonBuilder().create();

    private ApiClient() {
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    public static ApiClient getInstance() { return INSTANCE; }

    public JsonElement get(String path, String token) { return send("GET", path, token, null); }
    public JsonElement post(String path, String token, Object body) { return send("POST", path, token, body); }
    public JsonElement put(String path, String token, Object body) { return send("PUT", path, token, body); }
    public JsonElement delete(String path, String token) { return send("DELETE", path, token, null); }

    public <T> T getObject(String path, String token, Class<T> type) {
        JsonElement json = get(path, token);
        return json == null || json.isJsonNull() ? null : gson.fromJson(json, type);
    }

    public <T> List<T> getList(String path, String token, Class<T> itemType) {
        JsonElement json = get(path, token);
        if (json == null || !json.isJsonArray()) return List.of();
        Type type = TypeToken.getParameterized(List.class, itemType).getType();
        return gson.fromJson(json, type);
    }

    private JsonElement send(String method, String path, String token, Object body) {
        String url = ApiConfig.baseUrl() + (path.startsWith("/") ? path : "/" + path);
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json")
                .header("X-Requested-With", "SugestFilmes-Front-JSP");
        if (token != null && !token.isBlank()) builder.header("Authorization", "Bearer " + token);

        if (body == null) {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        } else {
            builder.header("Content-Type", "application/json; charset=UTF-8")
                    .method(method, HttpRequest.BodyPublishers.ofString(gson.toJson(body)));
        }

        try {
            HttpResponse<String> response = http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body() == null ? "" : response.body();
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ApiException(response.statusCode(), readError(responseBody, response.statusCode()));
            }
            if (responseBody.isBlank()) return JsonNull.INSTANCE;
            try { return JsonParser.parseString(responseBody); }
            catch (JsonParseException e) { throw new ApiException(response.statusCode(), "A API retornou uma resposta inválida.", e); }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(0, "A comunicação com a API foi interrompida.", e);
        } catch (IOException | IllegalArgumentException e) {
            throw new ApiException(0, "Não foi possível conectar à API em " + ApiConfig.baseUrl() + ". Verifique se ela está ativa e se API_BASE_URL está correto.", e);
        }
    }

    private String readError(String body, int status) {
        if (body != null && !body.isBlank()) {
            try {
                JsonObject object = JsonParser.parseString(body).getAsJsonObject();
                if (object.has("erro") && !object.get("erro").isJsonNull()) return object.get("erro").getAsString();
                if (object.has("mensagem") && !object.get("mensagem").isJsonNull()) return object.get("mensagem").getAsString();
            } catch (RuntimeException ignored) { }
        }
        return switch (status) {
            case 401 -> "Sessão expirada ou token inválido. Entre novamente.";
            case 403 -> "Você não tem permissão para realizar esta operação.";
            case 404 -> "O registro solicitado não foi encontrado.";
            default -> "A API retornou um erro (HTTP " + status + ").";
        };
    }
}
