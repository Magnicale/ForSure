<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Админка · Категории" />
<%@ include file="fragments/header.jspf" %>
<section class="hero hero--compact">
    <div><p class="eyebrow">УПРАВЛЕНИЕ САЙТОМ</p><h1>Категории капсул</h1><p class="lead">Создавай, переименовывай и удаляй категории.</p></div>
    <a class="button button--quiet" href="${pageContext.request.contextPath}/capsules">Вернуться в ленту</a>
</section>
<c:if test="${not empty error}"><p class="error-message"><c:out value="${error}" /></p></c:if>
<c:if test="${param.updated == '1'}"><p class="notice">Изменения сохранены.</p></c:if>
<section class="admin-panel">
    <h2>Добавить категорию</h2>
    <form method="post" action="${pageContext.request.contextPath}/admin/categories" class="admin-create">
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
        <input type="hidden" name="action" value="create">
        <label class="sr-only" for="new-category">Название</label>
        <input id="new-category" name="name" required maxlength="60" placeholder="Например, Учёба">
        <button class="button button--primary" type="submit">Добавить</button>
    </form>
</section>
<section class="admin-list">
    <c:forEach items="${categories}" var="category">
        <article class="admin-row">
            <form method="post" action="${pageContext.request.contextPath}/admin/categories" class="admin-edit">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="id" value="${category.id}">
                <input name="name" maxlength="60" required value="${fn:escapeXml(category.name)}" aria-label="Новое название категории">
                <button class="button button--quiet" type="submit">Сохранить</button>
            </form>
            <form method="post" action="${pageContext.request.contextPath}/admin/categories" class="inline-form">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="id" value="${category.id}">
                <button class="button button--danger" type="submit">Удалить</button>
            </form>
        </article>
    </c:forEach>
</section>
<%@ include file="fragments/footer.jspf" %>
