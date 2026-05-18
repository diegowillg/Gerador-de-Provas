<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="layout/header.jsp">
    <jsp:param name="titulo" value="In&#237;cio"/>
    <jsp:param name="menu" value="home"/>
</jsp:include>

<%-- Bloco inicial com resumo e atalhos principais. --%>
<section class="hero">
    <h1>Gerador de Provas</h1>
    <p>Cadastre quest&#245;es, monte avalia&#231;&#245;es e gere provas prontas para impress&#227;o em poucos cliques.</p>
    <div class="hero-actions">
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/questoes?acao=novo">Nova quest&#227;o</a>
        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/provas?acao=novo">Montar prova</a>
    </div>
</section>

<%-- Cards com dados carregados pelo HomeController. --%>
<section class="stats-grid">
    <article class="stat-card">
        <h3>${totalQuestoes}</h3>
        <p>Quest&#245;es no banco</p>
    </article>
    <article class="stat-card">
        <h3>${totalProvas}</h3>
        <p>Provas geradas</p>
    </article>
    <article class="stat-card">
        <h3>${totalDisciplinas}</h3>
        <p>Disciplinas</p>
    </article>
</section>

<%-- Explicacao simples do CRUD para facilitar a apresentacao. --%>
<section class="card" style="margin-bottom: 1.5rem;">
    <h2>Opera&#231;&#245;es do sistema (CRUD)</h2>
    <ul class="crud-list">
        <li><strong>Create</strong> - Cadastre quest&#245;es com enunciado e alternativas.</li>
        <li><strong>Read</strong> - Liste, filtre por disciplina e visualize detalhes.</li>
        <li><strong>Update</strong> - Edite quest&#245;es e provas j&#225; existentes.</li>
        <li><strong>Delete</strong> - Remova registros que n&#227;o ser&#227;o mais usados.</li>
    </ul>
</section>

<jsp:include page="layout/footer.jsp"/>
