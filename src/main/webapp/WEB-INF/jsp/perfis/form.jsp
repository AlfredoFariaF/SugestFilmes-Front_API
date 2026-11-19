<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
            <!DOCTYPE html>
            <html lang="pt-BR">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <title>Perfil - SugestFilmes</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
            </head>

            <body>
                <header class="topbar">
                    <div class="container topbar-content">
                        <a class="logo" href="${pageContext.request.contextPath}/home"> SugestFilmes</a>
                        <nav class="nav">
                            <a class="active" href="${pageContext.request.contextPath}/home">Início</a>
                            <a href="${pageContext.request.contextPath}/filmes">Filmes</a>
                            <a href="${pageContext.request.contextPath}/categorias">Categorias</a>
                            <a href="${pageContext.request.contextPath}/recomendacoes">Recomendações</a>
                            <a href="${pageContext.request.contextPath}/avaliacoes?minhas=true">Minhas avaliações</a>
                        </nav>
                        <div class="user-nav"><span>${usuarioLogado.nome}</span><a
                                href="${pageContext.request.contextPath}/logout">Sair</a></div>
                    </div>
                </header>
                <main class="container page-space">
                    <div class="page-header">
                        <h1>
                            <c:choose>
                                <c:when test="${empty perfil.id}">Novo perfil</c:when>
                                <c:otherwise>Editar perfil</c:otherwise>
                            </c:choose>
                        </h1>
                    </div>
                    <div class="card">
                        <c:if test="${not empty erro}">
                            <div class="alert alert-erro">
                                <c:out value="${erro}" />
                            </div>
                        </c:if>
                        <form method="post" action="${pageContext.request.contextPath}/perfis"><input type="hidden"
                                name="_csrf" value="${sessionScope.csrfToken}"><input type="hidden" name="id"
                                value="${perfil.id}">
                            <div class="form-group"><label for="nome">Nome</label><input type="text" id="nome"
                                    name="nome" value="${fn:escapeXml(perfil.nome)}" required maxlength="80"></div>
                            <div class="actions"><button class="btn" type="submit">Salvar</button><a
                                    class="btn btn-secondary"
                                    href="${pageContext.request.contextPath}/perfis">Cancelar</a></div>
                        </form>
                    </div>
                </main>
            </body>

            </html>