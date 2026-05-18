<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${param.titulo} | Gerador de Provas</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%-- Cabecalho reaproveitado em todas as paginas do sistema. --%>
<header class="topbar">
    <a href="${pageContext.request.contextPath}/home" class="brand-link">
        <span class="brand-text">
            <span class="brand-title">Gerador de Provas</span>
            <span class="brand-sub">Sistema acad&#234;mico</span>
        </span>
    </a>
    <%-- Menu principal de navegacao entre as telas do MVC. --%>
    <nav class="nav-main" aria-label="Menu principal">
        <a href="${pageContext.request.contextPath}/home" class="nav-link ${param.menu == 'home' ? 'active' : ''}">In&#237;cio</a>
        <a href="${pageContext.request.contextPath}/questoes?acao=listar" class="nav-link ${param.menu == 'questoes' ? 'active' : ''}">Quest&#245;es</a>
        <a href="${pageContext.request.contextPath}/provas?acao=listar" class="nav-link ${param.menu == 'provas' ? 'active' : ''}">Provas</a>
    </nav>
</header>

<main class="container">

<%-- Mensagem temporaria usada para avisos de sucesso ou erro. --%>
<c:if test="${not empty sessionScope.flashMensagem}">
    <div class="toast alert-${sessionScope.flashTipo}" role="alert">
        ${sessionScope.flashMensagem}
    </div>
    <c:remove var="flashMensagem" scope="session"/>
    <c:remove var="flashTipo" scope="session"/>
</c:if>
