
package br.com.sugestfilmes.service;

import br.com.sugestfilmes.dao.RecomendacaoDAO;
import br.com.sugestfilmes.model.Filme;

import java.util.List;
import java.util.stream.Collectors;

public class RecomendacaoService {

    private final RecomendacaoDAO dao = new RecomendacaoDAO();

    public List<Filme> recomendar(
            List<Long> categorias,
            String token) {

        String ids = categorias.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        return dao.recomendar(ids, token);
    }
}
    