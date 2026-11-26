<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Вход · Капсула времени" />
<%@ include file="fragments/header.jspf" %>
<section class="auth-panel">
    <p class="eyebrow">ТВОЁ ВРЕМЯ</p>
    <h1>С возвращением</h1>
    <p class="lead">Войди, чтобы создать новую капсулу или дождаться открытия старой.</p>
    <c:if test="${param.registered == '1'}"><p class="notice">Аккаунт создан. Теперь можно войти.</p></c:if>
    <c:if test="${not empty error}"><p class="error-message"><c:out value="${error}" /></p></c:if>
    <form method="post" action="${pageContext.request.contextPath}/login" class="form-stack">
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
        <label>Email<input type="email" name="email" required maxlength="255" autocomplete="email"></label>
        <label>Пароль<input type="password" name="password" required autocomplete="current-password"></label>
        <button class="button button--primary" type="submit">Войти</button>
    </form>
    <p class="muted">Нет аккаунта? <a href="${pageContext.request.contextPath}/register">Зарегистрироваться</a></p>
</section>
<%@ include file="fragments/footer.jspf" %>
