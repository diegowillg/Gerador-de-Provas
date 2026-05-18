<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="../layout/header.jsp">
    <jsp:param name="titulo" value="Provas"/>
    <jsp:param name="menu" value="provas"/>
</jsp:include>

<div class="page-header">
    <div>
        <h1>Provas Geradas</h1>
        <p class="subtitle">Monte avalia&#231;&#245;es selecionando quest&#245;es do banco</p>
    </div>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/provas?acao=novo">Nova prova</a>
</div>

<%-- Lista as provas ja criadas ou mostra um aviso quando ainda nao existe prova. --%>
<c:choose>
    <c:when test="${empty provas}">
        <div class="empty-state">
            <h3>Nenhuma prova cadastrada</h3>
            <p>Crie sua primeira prova selecionando quest&#245;es do banco.</p>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/provas?acao=novo">Criar prova</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="cards-grid">
            <c:forEach var="p" items="${provas}">
                <article class="card prova-card">
                    <div class="prova-card-header">
                        <h3>${p.titulo}</h3>
                    </div>
                    <%-- Se a descricao estiver vazia, mostra um texto padrao para manter o card preenchido. --%>
                    <p class="muted">${empty p.descricao ? 'Sem descri&#231;&#227;o' : p.descricao}</p>
                    <c:if test="${p.criadoEm != null}">
                        <p class="meta-small">Criada em: ${p.criadoEm}</p>
                    </c:if>
                    <%-- Acoes disponiveis para cada prova. --%>
                    <div class="card-actions">
                        <a class="btn btn-sm btn-primary" href="${pageContext.request.contextPath}/provas?acao=visualizar&id=${p.id}">Visualizar</a>
                        <a class="btn btn-sm btn-ghost" href="${pageContext.request.contextPath}/provas?acao=imprimir&id=${p.id}" target="_blank">Imprimir</a>
                        <a class="btn btn-sm btn-ghost" href="${pageContext.request.contextPath}/provas?acao=editar&id=${p.id}">Editar</a>
                        <form method="post" action="${pageContext.request.contextPath}/provas" class="inline-form"
                              onsubmit="return confirmarExclusao('esta prova');">
                            <input type="hidden" name="acao" value="excluir">
                            <input type="hidden" name="id" value="${p.id}">
                            <button type="submit" class="btn btn-sm btn-danger-ghost">Excluir</button>
                        </form>
                    </div>
                </article>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="../layout/footer.jsp"/>
