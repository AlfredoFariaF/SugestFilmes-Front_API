<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="pt-BR"><head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Entrar - SugestFilmes</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head><body class="login-body">
<div class="login-page"><div class="login-card card">
    <div class="brand-center">SugestFilmes</div><h1>Bem-vindo</h1>
    <p>Entre para receber recomendações de filmes.</p>
    <c:if test="${not empty erro}"><div class="alert alert-erro"><c:out value="${erro}"/></div></c:if>
    <form method="post" action="${pageContext.request.contextPath}/login">
        <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
        <div class="form-group"><label for="login">Login</label><input type="text" id="login" name="login" autocomplete="username" required autofocus></div>
        <div class="form-group"><label for="senha">Senha</label><input type="password" id="senha" name="senha" autocomplete="current-password" required></div>
        <button type="submit" class="btn btn-wide">Entrar</button>
    </form>
</div></div></body></html>
