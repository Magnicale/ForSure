<%@ tag body-content="empty" %>
<%@ attribute name="capsule" required="true" type="ru.itis.timecapsule.model.Capsule" %>
<%@ attribute name="currentUserId" required="true" type="java.lang.Long" %>
<%@ attribute name="isAdmin" required="true" type="java.lang.Boolean" %>
<%@ attribute name="contextPath" required="true" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tc" tagdir="/WEB-INF/tags" %>
<article class="capsule-card ${capsule.unlocked ? 'is-open' : 'is-sealed'}"
         id="capsule-${capsule.id}"
         data-api-url="${contextPath}/api/unlock?id=${capsule.id}">
    <div class="capsule-card__topline">
        <c:if test="${capsule.unlocked}"><span class="eyebrow"><c:out value="${capsule.categoryName}" /></span></c:if>
        <tc:countdown capsuleId="${capsule.id}" unlockAtMillis="${capsule.unlockAtMillis}" />
    </div>
    <h2 class="capsule-card__title"><c:out value="${capsule.title}" /></h2>
    <c:if test="${capsule.unlocked}"><p class="muted">Автор: <c:out value="${capsule.ownerName}" /></p></c:if>

    <c:choose>
        <c:when test="${capsule.unlocked}">
            <div class="sealed-content" id="content-${capsule.id}">
                <img class="capsule-photo" src="${contextPath}/media?id=${capsule.mediaFileId}" alt="Фото капсулы" loading="lazy">
                <p class="capsule-note"><c:out value="${capsule.note}" /></p>
                <ul class="tag-list"><c:forEach items="${capsule.tags}" var="tag"><li><c:out value="${tag.name}" /></li></c:forEach></ul>
            </div>
        </c:when>
        <c:otherwise><div class="sealed-content is-hidden" id="content-${capsule.id}"></div></c:otherwise>
    </c:choose>

    <div class="capsule-actions">
        <c:if test="${capsule.unlocked && (isAdmin || capsule.ownerId == currentUserId)}">
            <a class="button button--quiet" href="${pageContext.request.contextPath}/capsules/edit?id=${capsule.id}">Изменить</a>
        </c:if>
        <c:if test="${isAdmin || capsule.ownerId == currentUserId}">
            <form method="post" action="${contextPath}/capsules/delete" class="inline-form"
                  onsubmit="return confirm('Удалить эту капсулу?')">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="id" value="${capsule.id}">
                <button class="button button--danger" type="submit">Удалить</button>
            </form>
        </c:if>
    </div>
</article>
