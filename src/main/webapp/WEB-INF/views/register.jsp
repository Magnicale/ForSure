<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Регистрация · Капсула времени" />
<%@ include file="fragments/header.jspf" %>
<section class="auth-panel">
    <p class="eyebrow">НАЧНИ СЕЙЧАС</p>
    <h1>Создай аккаунт</h1>
    <p class="lead">Некоторые сообщения лучше прочитать позже.</p>
    <c:if test="${not empty error}"><p class="error-message"><c:out value="${error}" /></p></c:if>
    <form method="post" action="${pageContext.request.contextPath}/register" class="form-stack">
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
        <label>Как тебя называть<input type="text" name="displayName" required minlength="2" maxlength="80" autocomplete="name"></label>
        <label>Email<input type="email" name="email" required maxlength="255" autocomplete="email"></label>
        <label>Пароль<input type="password" name="password" required minlength="8" maxlength="100" autocomplete="new-password"></label>
        <button class="button button--primary" type="submit">Создать аккаунт</button>
    </form>
    <p class="muted">Уже есть аккаунт? <a href="${pageContext.request.contextPath}/login">Войти</a></p>
</section>
<%@ include file="fragments/footer.jspf" %>
