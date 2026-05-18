<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="../layout/header.jsp">
    <jsp:param name="titulo" value="Detalhe da Questão"/>
    <jsp:param name="menu" value="questoes"/>
</jsp:include>

<%-- Cabecalho da pagina com acoes de editar ou voltar para a listagem. --%>
<div class="page-header">
    <h1>Questão #${questao.id}</h1>
    <div class="header-actions">
        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/questoes?acao=editar&id=${questao.id}">Editar</a>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/questoes?acao=listar">Voltar</a>
    </div>
</div>

<%-- Exibe todos os dados da questao e destaca a alternativa correta. --%>
<article class="card question-detail">
    <p class="meta">
        <span class="badge">${questao.disciplina}</span>
        <span class="badge badge-${questao.dificuldade.toLowerCase()}">${questao.dificuldade}</span>
        <span>Gabarito: <strong>${questao.respostaCorreta}</strong></span>
    </p>
    <h2>Enunciado</h2>
    <p>${questao.enunciado}</p>
    <%-- A classe "correct" aplica o destaque verde na resposta cadastrada como gabarito. --%>
    <ul class="alternatives-list">
        <li class="${questao.respostaCorreta == 'A' ? 'correct' : ''}"><strong>A)</strong> ${questao.alternativaA}</li>
        <li class="${questao.respostaCorreta == 'B' ? 'correct' : ''}"><strong>B)</strong> ${questao.alternativaB}</li>
        <li class="${questao.respostaCorreta == 'C' ? 'correct' : ''}"><strong>C)</strong> ${questao.alternativaC}</li>
        <li class="${questao.respostaCorreta == 'D' ? 'correct' : ''}"><strong>D)</strong> ${questao.alternativaD}</li>
        <c:if test="${not empty questao.alternativaE}">
            <li class="${questao.respostaCorreta == 'E' ? 'correct' : ''}"><strong>E)</strong> ${questao.alternativaE}</li>
        </c:if>
    </ul>
</article>

<jsp:include page="../layout/footer.jsp"/>
