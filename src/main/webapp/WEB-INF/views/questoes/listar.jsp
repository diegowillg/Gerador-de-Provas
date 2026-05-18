<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="../layout/header.jsp">
    <jsp:param name="titulo" value="Quest&#245;es"/>
    <jsp:param name="menu" value="questoes"/>
</jsp:include>

<div class="page-header">
    <div>
        <h1>Banco de Quest&#245;es</h1>
        <p class="subtitle">Gerencie o cadastro completo de quest&#245;es objetivas</p>
    </div>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/questoes?acao=novo">Nova quest&#227;o</a>
</div>

<%-- Formulario de filtro por disciplina. --%>
<form class="filter-bar" method="get" action="${pageContext.request.contextPath}/questoes">
    <input type="hidden" name="acao" value="listar">
    <div class="form-group">
        <label for="disciplina">Disciplina</label>
        <select id="disciplina" name="disciplina">
            <option value="">Todas as disciplinas</option>
            <c:forEach var="d" items="${disciplinas}">
                <option value="${d}" ${d == disciplinaFiltro ? 'selected' : ''}>${d}</option>
            </c:forEach>
        </select>
    </div>
    <button type="submit" class="btn btn-secondary">Filtrar</button>
</form>

<%-- Se nao houver questoes, mostra aviso; caso contrario, monta a tabela. --%>
<c:choose>
    <c:when test="${empty questoes}">
        <div class="empty-state">
            <h3>Nenhuma quest&#227;o encontrada</h3>
            <p>Cadastre a primeira quest&#227;o para come&#231;ar a montar suas provas.</p>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/questoes?acao=novo">Cadastrar quest&#227;o</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="table-wrapper">
            <table class="data-table">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Enunciado</th>
                    <th>Disciplina</th>
                    <th>Dificuldade</th>
                    <th>Gabarito</th>
                    <th>A&#231;&#245;es</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="q" items="${questoes}">
                    <tr>
                        <td class="id-cell">${q.id}</td>
                        <td class="truncate" title="${q.enunciado}">${q.enunciado}</td>
                        <td><span class="badge">${q.disciplina}</span></td>
                        <td><span class="badge badge-${q.dificuldade.toLowerCase()}">${q.dificuldade}</span></td>
                        <td><span class="gabarito-cell">${q.respostaCorreta}</span></td>
                        <td class="actions">
                            <a class="btn btn-sm btn-ghost" href="${pageContext.request.contextPath}/questoes?acao=detalhe&id=${q.id}">Ver</a>
                            <a class="btn btn-sm btn-ghost" href="${pageContext.request.contextPath}/questoes?acao=editar&id=${q.id}">Editar</a>
                            <form method="post" action="${pageContext.request.contextPath}/questoes" class="inline-form"
                                  onsubmit="return confirmarExclusao('esta quest&#227;o');">
                                <input type="hidden" name="acao" value="excluir">
                                <input type="hidden" name="id" value="${q.id}">
                                <button type="submit" class="btn btn-sm btn-danger-ghost">Excluir</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="../layout/footer.jsp"/>
