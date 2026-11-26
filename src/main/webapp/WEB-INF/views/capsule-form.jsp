<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Новая капсула · Капсула времени" />
<%@ include file="fragments/header.jspf" %>
<section class="form-panel form-panel--wide">
    <p class="eyebrow">ЗАПЕЧАТАЙ МОМЕНТ</p>
    <h1><c:choose><c:when test="${not empty capsule}">Измени капсулу</c:when><c:otherwise>Новая капсула</c:otherwise></c:choose></h1>
    <p class="lead">Заголовок и таймер останутся видимыми. Фото и заметка откроются после выбранной даты.</p>
    <c:if test="${not empty error}"><p class="error-message"><c:out value="${error}" /></p></c:if>
    <form method="post" enctype="multipart/form-data"
          action="${pageContext.request.contextPath}/capsules/${not empty capsule ? 'edit' : 'new'}" class="form-stack">
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
        <c:if test="${empty capsule}"><input type="hidden" name="requestKey" value="${requestKey}"></c:if>
        <c:if test="${not empty capsule}"><input type="hidden" name="id" value="${capsule.id}"></c:if>
        <label>Заголовок
            <input type="text" name="title" required maxlength="120" value="${fn:escapeXml(capsule.title)}" placeholder="Например, открыть в день выпуска">
        </label>
        <label>Заметка
            <textarea name="note" rows="7" required maxlength="4000" placeholder="Что ты хочешь прочитать позже?"><c:out value="${capsule.note}" /></textarea>
        </label>
        <div class="form-row">
            <label>Дата и время открытия
                <input type="datetime-local" name="unlockAt" required value="${fn:escapeXml(capsule.unlockAtLocal)}">
            </label>
            <label>Категория
                <select name="categoryId" required>
                    <option value="">Выбери категорию</option>
                    <c:forEach items="${categories}" var="category">
                        <option value="${category.id}" ${capsule.categoryId == category.id ? 'selected' : ''}><c:out value="${category.name}" /></option>
                    </c:forEach>
                </select>
            </label>
        </div>
        <fieldset>
            <legend>Теги</legend>
            <div class="tag-options">
                <c:forEach items="${tags}" var="tag">
                    <label class="check-option"><input type="checkbox" name="tagIds" value="${tag.id}"
                        ${fn:contains(selectedTagIds, fn:concat(fn:concat(',', tag.id), ',')) ? 'checked' : ''}>
                        <span><c:out value="${tag.name}" /></span>
                    </label>
                </c:forEach>
            </div>
        </fieldset>
        <label>Фотография <span class="muted">JPG, PNG или GIF, до 5 МБ<c:if test="${not empty capsule}">. Не выбирай файл, чтобы оставить текущее фото</c:if></span>
            <input type="file" name="photo" accept="image/jpeg,image/png,image/gif" ${empty capsule ? 'required' : ''}>
        </label>
        <div class="form-actions">
            <button class="button button--primary" type="submit">Сохранить капсулу</button>
            <a class="button button--quiet" href="${pageContext.request.contextPath}/capsules">Отмена</a>
        </div>
    </form>
</section>
<%@ include file="fragments/footer.jspf" %>
