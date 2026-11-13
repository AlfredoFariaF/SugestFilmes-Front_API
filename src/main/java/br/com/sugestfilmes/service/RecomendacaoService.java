package br.com.sugestfilmes.service;

import br.com.sugestfilmes.api.ApiClient;
import br.com.sugestfilmes.model.Filme;
import java.util.List;
import java.util.stream.Collectors;

public class RecomendacaoService {
    private final ApiClient api = ApiClient.getInstance();
    public List<Filme> recomendar(List<Long> categorias, String token) {
        String ids = categorias.stream().map(String::valueOf).collect(Collectors.joining(","));
        return api.getList("/recomendacoes?categorias=" + ids, token, Filme.class);
    }
}
