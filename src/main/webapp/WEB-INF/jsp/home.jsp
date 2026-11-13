<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="pt-BR"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>SugestFilmes</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css"></head><body>
<c:set var="activePage" value="home"/><%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>
<main class="container page-space">
    <section class="hero"><div class="hero-copy"><span class="eyebrow">SISTEMA DE RECOMENDAÇÃO</span><h1>Encontre seu próximo filme.</h1>
        <p>Escolha as categorias que combinam com você e descubra sugestões do catálogo do SugestFilmes.</p>
        <a class="btn" href="#categorias">Escolher categorias</a></div></section>
    <c:if test="${not empty erro}"><div class="alert alert-erro"><c:out value="${erro}"/></div></c:if>
    <section id="categorias" class="section-block"><div class="section-heading"><div><h2>Escolha suas categorias</h2><p>Você pode selecionar mais de uma categoria.</p></div></div>
        <form method="get" action="${pageContext.request.contextPath}/recomendacoes">
            <div class="category-grid"><c:forEach var="categoria" items="${categorias}"><label class="category-option"><input type="checkbox" name="categoria" value="${categoria.id}"><span><c:out value="${categoria.nome}"/></span></label></c:forEach></div>
            <div class="center-actions"><button class="btn" type="submit">Sugerir filmes</button></div>
        </form>
    </section>
    <section class="section-block"><div class="section-heading"><div><h2>Filmes em destaque</h2><p>Alguns títulos do catálogo.</p></div><a href="${pageContext.request.contextPath}/filmes">Ver todos</a></div>
        <div class="movie-grid"><c:forEach var="filme" items="${filmes}"><article class="movie-card"><div class="movie-poster"><span>🎬</span><span class="year-badge"><c:out value="${filme.ano}"/></span></div>
            <div class="movie-body"><h3><c:out value="${filme.titulo}"/></h3><p class="movie-meta"><c:out value="${filme.genero}"/> · <c:out value="${filme.diretor}"/></p><div class="rating">⭐ <strong><c:choose><c:when test="${filme.totalAvaliacoes > 0}"><c:out value="${filme.mediaAvaliacao}"/></c:when><c:otherwise>Sem avaliações</c:otherwise></c:choose></strong></div>
                <a class="btn btn-secondary btn-small" href="${pageContext.request.contextPath}/filmes?acao=detalhes&id=${filme.id}">Ver detalhes</a></div></article></c:forEach></div>
        <c:if test="${empty filmes}"><div class="empty">Nenhum filme cadastrado no momento.</div></c:if>
    </section>
</main><footer class="footer"><div class="container">SugestFilmes · Interface MVC em JSP integrada à API REST</div></footer>
</body></html>
