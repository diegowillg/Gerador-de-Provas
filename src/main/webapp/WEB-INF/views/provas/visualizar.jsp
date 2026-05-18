<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="tituloPagina" value="${modoImpressao ? 'Imprimir Prova' : 'Visualizar Prova'}"/>
<jsp:include page="../layout/header.jsp">
    <jsp:param name="titulo" value="${tituloPagina}"/>
    <jsp:param name="menu" value="provas"/>
</jsp:include>

<%-- Quando a acao e imprimir, escondo menu e botoes para deixar a prova limpa. --%>
<c:if test="${modoImpressao}">
    <style>
        .topbar,
        .nav-main,
        .page-header,
        .no-print {
            display: none !important;
        }

        .container {
            max-width: 100%;
            padding: 0;
        }

        body {
            background: #fff;
        }
    </style>
    <script>
        window.addEventListener('load', function () {
            setTimeout(function () {
                window.print();
            }, 300);
        });
    </script>
</c:if>

<div class="page-header no-print">
    <h1>${prova.titulo}</h1>
    <div class="header-actions">
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/provas?acao=imprimir&id=${prova.id}">Imprimir</a>
        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/provas?acao=listar">Voltar</a>
    </div>
</div>

<%-- Modelo visual da prova que sera exibida ou impressa. --%>
<article class="exam-sheet ${modoImpressao ? 'print-mode' : ''}">
    <header class="exam-header">
        <h1>${prova.titulo}</h1>
        <c:if test="${not empty prova.descricao}">
            <p>${prova.descricao}</p>
        </c:if>
        <c:if test="${not empty prova.instrucoes}">
            <p class="instructions"><strong>Instruções:</strong> ${prova.instrucoes}</p>
        </c:if>
        <div class="student-fields">
            <span>Nome: _________________________________________</span>
            <span>Turma: _______________ Data: ____/____/______</span>
        </div>
    </header>

    <c:forEach var="q" items="${prova.questoes}" varStatus="st">
        <section class="exam-question">
            <h3>Questão ${st.count}</h3>
            <p>${q.enunciado}</p>
            <ul class="exam-options">
                <li><span class="bubble">A</span> ${q.alternativaA}</li>
                <li><span class="bubble">B</span> ${q.alternativaB}</li>
                <li><span class="bubble">C</span> ${q.alternativaC}</li>
                <li><span class="bubble">D</span> ${q.alternativaD}</li>
                <c:if test="${not empty q.alternativaE}">
                    <li><span class="bubble">E</span> ${q.alternativaE}</li>
                </c:if>
            </ul>
        </section>
    </c:forEach>
</article>

<jsp:include page="../layout/footer.jsp"/>
