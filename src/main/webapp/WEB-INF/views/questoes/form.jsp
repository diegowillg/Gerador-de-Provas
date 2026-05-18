<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="../layout/header.jsp">
    <jsp:param name="titulo" value="${questao.id == null ? 'Nova Questao' : 'Editar Questao'}"/>
    <jsp:param name="menu" value="questoes"/>
</jsp:include>

<div class="page-header">
    <div>
        <h1>${questao.id == null ? 'Cadastrar Quest&#227;o' : 'Editar Quest&#227;o'}</h1>
        <p class="subtitle">Preencha os campos da quest&#227;o objetiva</p>
    </div>
    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/questoes?acao=listar">Voltar</a>
</div>

<c:if test="${not empty erro}">
    <div class="alert alert-erro">${erro}</div>
</c:if>

<%-- Formulario usado tanto para cadastrar quanto para editar uma questao. --%>
<form class="form-card" method="post"
      action="${pageContext.request.contextPath}/questoes?acao=${questao.id == null ? 'salvar' : 'atualizar'}">
    <c:if test="${questao.id != null}">
        <input type="hidden" name="id" value="${questao.id}">
    </c:if>

    <p class="form-section-title">Dados da quest&#227;o</p>

    <div class="form-group">
        <label for="enunciado">Enunciado *</label>
        <textarea id="enunciado" name="enunciado" rows="4" required>${questao.enunciado}</textarea>
    </div>

    <div class="form-grid">
        <div class="form-group">
            <label for="disciplina">Disciplina *</label>
            <input type="text" id="disciplina" name="disciplina" value="${questao.disciplina}" required
                   placeholder="Ex: Matematica">
        </div>
        <div class="form-group">
            <label for="dificuldade">Dificuldade *</label>
            <select id="dificuldade" name="dificuldade" required>
                <option value="FACIL" ${questao.dificuldade == 'FACIL' ? 'selected' : ''}>F&#225;cil</option>
                <option value="MEDIA" ${questao.dificuldade == null || questao.dificuldade == 'MEDIA' ? 'selected' : ''}>M&#233;dia</option>
                <option value="DIFICIL" ${questao.dificuldade == 'DIFICIL' ? 'selected' : ''}>Dif&#237;cil</option>
            </select>
        </div>
    </div>

    <%-- Alternativas e gabarito da questao objetiva. --%>
    <p class="form-section-title">Alternativas</p>

    <div class="form-grid alt-grid">
        <div class="form-group">
            <label for="alternativaA">Alternativa A *</label>
            <input type="text" id="alternativaA" name="alternativaA" value="${questao.alternativaA}" required>
        </div>
        <div class="form-group">
            <label for="alternativaB">Alternativa B *</label>
            <input type="text" id="alternativaB" name="alternativaB" value="${questao.alternativaB}" required>
        </div>
        <div class="form-group">
            <label for="alternativaC">Alternativa C *</label>
            <input type="text" id="alternativaC" name="alternativaC" value="${questao.alternativaC}" required>
        </div>
        <div class="form-group">
            <label for="alternativaD">Alternativa D *</label>
            <input type="text" id="alternativaD" name="alternativaD" value="${questao.alternativaD}" required>
        </div>
        <div class="form-group">
            <label for="alternativaE">Alternativa E (opcional)</label>
            <input type="text" id="alternativaE" name="alternativaE" value="${questao.alternativaE}">
        </div>
        <div class="form-group">
            <label for="respostaCorreta">Resposta correta *</label>
            <select id="respostaCorreta" name="respostaCorreta" required>
                <option value="A" ${questao.respostaCorreta == 'A' ? 'selected' : ''}>A</option>
                <option value="B" ${questao.respostaCorreta == 'B' ? 'selected' : ''}>B</option>
                <option value="C" ${questao.respostaCorreta == 'C' ? 'selected' : ''}>C</option>
                <option value="D" ${questao.respostaCorreta == 'D' ? 'selected' : ''}>D</option>
                <option value="E" ${questao.respostaCorreta == 'E' ? 'selected' : ''}>E</option>
            </select>
        </div>
    </div>

    <div class="form-actions">
        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/questoes?acao=listar">Cancelar</a>
        <button type="submit" class="btn btn-primary">Salvar quest&#227;o</button>
    </div>
</form>

<jsp:include page="../layout/footer.jsp"/>
