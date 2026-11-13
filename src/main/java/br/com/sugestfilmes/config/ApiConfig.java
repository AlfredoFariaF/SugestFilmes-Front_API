package br.com.sugestfilmes.config;

/** Configuração externa da URL da API. Nunca armazena credenciais ou token. */
public final class ApiConfig {
    private static final String DEFAULT_URL = "http://host.docker.internal:8081/api";
    private ApiConfig() { }

    public static String baseUrl() {
        String value = System.getenv("API_BASE_URL");
        if (value == null || value.isBlank()) value = System.getProperty("api.base.url");
        if (value == null || value.isBlank()) value = DEFAULT_URL;
        return value.trim().replaceAll("/+$", "");
    }
}
