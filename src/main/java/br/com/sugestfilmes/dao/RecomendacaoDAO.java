
package br.com.sugestfilmes.dao;

import br.com.sugestfilmes.api.ApiClient;
import br.com.sugestfilmes.model.Filme;

import java.util.List;

public class RecomendacaoDAO {

    private final ApiClient api = ApiClient.getInstance();

    public List<Filme> recomendar(String categorias, String token) {
        return api.getList(
                "/recomendacoes?categorias=" + categorias,
                token,
                Filme.class);
    }
}
