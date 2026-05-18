<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="layout/header.jsp">
    <jsp:param name="titulo" value="Erro"/>
</jsp:include>

<%-- Tela generica usada para 404 e excecoes nao tratadas. --%>
<div class="empty-state">
    <h1>Ops! Algo deu errado.</h1>
    <p>Não foi possível processar sua solicitação. Verifique a conexão com o banco de dados e tente novamente.</p>
    <%-- Em ambiente de apresentacao, a mensagem ajuda a identificar falhas de banco ou rota. --%>
    <c:if test="${not empty exception}">
        <pre class="error-detail">${exception.message}</pre>
    </c:if>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/home">Voltar ao início</a>
</div>

<jsp:include page="layout/footer.jsp"/>
