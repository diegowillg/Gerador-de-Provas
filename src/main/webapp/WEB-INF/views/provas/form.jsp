<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="../layout/header.jsp">
    <jsp:param name="titulo" value="${prova.id == null ? 'Nova Prova' : 'Editar Prova'}"/>
    <jsp:param name="menu" value="provas"/>
</jsp:include>

<div class="page-header">
    <div>
        <h1>${prova.id == null ? 'Montar Prova' : 'Editar Prova'}</h1>
        <p class="subtitle">Defina os dados e selecione as quest&#245;es</p>
    </div>
    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/provas?acao=listar">Voltar</a>
</div>

<c:if test="${not empty erro}">
    <div class="alert alert-erro">${erro}</div>
</c:if>

<%-- Formulario para montar ou editar uma prova. --%>
<form class="form-card" method="post"
      action="${pageContext.request.contextPath}/provas?acao=${prova.id == null ? 'salvar' : 'atualizar'}">
    <c:if test="${prova.id != null}">
        <input type="hidden" name="id" value="${prova.id}">
    </c:if>

    <p class="form-section-title">Informa&#231;&#245;es da prova</p>

    <div class="form-group">
        <label for="titulo">T&#237;tulo da prova *</label>
        <input type="text" id="titulo" name="titulo" value="${prova.titulo}" required
               placeholder="Ex: Avaliacao Bimestral 1">
    </div>

    <div class="form-group">
        <label for="descricao">Descri&#231;&#227;o</label>
        <textarea id="descricao" name="descricao" rows="2">${prova.descricao}</textarea>
    </div>

    <div class="form-group">
        <label for="instrucoes">Instru&#231;&#245;es para o aluno</label>
        <textarea id="instrucoes" name="instrucoes" rows="3"
                  placeholder="Ex: Marque apenas uma alternativa por questao.">${prova.instrucoes}</textarea>
    </div>

    <%-- As questoes aparecem como checkboxes para compor a prova. --%>
    <p class="form-section-title">Quest&#245;es da prova</p>

    <div class="form-group">
        <label>Selecione as quest&#245;es *</label>
        <p class="hint">Marque as quest&#245;es que far&#227;o parte da prova.</p>
        <c:choose>
            <c:when test="${empty questoesDisponiveis}">
                <p class="empty-inline">Cadastre quest&#245;es antes de criar uma prova.</p>
            </c:when>
            <c:otherwise>
                <div class="question-picker" id="questionPicker">
                    <c:forEach var="q" items="${questoesDisponiveis}">
                        <c:set var="marcada" value="false"/>
                        <c:forEach var="sel" items="${prova.questoes}">
                            <c:if test="${sel.id == q.id}">
                                <c:set var="marcada" value="true"/>
                            </c:if>
                        </c:forEach>
                        <label class="picker-item">
                            <input type="checkbox" name="questaoIds" value="${q.id}" ${marcada ? 'checked' : ''}>
                            <span class="picker-content">
                                <strong class="picker-title">#${q.id} - ${q.enunciado}</strong>
                                <small>${q.disciplina} &middot; ${q.dificuldade}</small>
                            </span>
                        </label>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="form-actions">
        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/provas?acao=listar">Cancelar</a>
        <button type="submit" class="btn btn-primary" ${empty questoesDisponiveis ? 'disabled' : ''}>Salvar prova</button>
    </div>
</form>

<jsp:include page="../layout/footer.jsp"/>
