<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tc" tagdir="/WEB-INF/tags" %>
<c:set var="pageTitle" value="Капсула · Капсула времени" />
<%@ include file="fragments/header.jspf" %>
<article class="capsule-detail ${capsule.unlocked ? 'is-open' : 'is-sealed'}">
    <a class="back-link" href="${pageContext.request.contextPath}/capsules">← Ко всем капсулам</a>
    <c:if test="${capsule.unlocked}"><p class="eyebrow"><c:out value="${capsule.categoryName}" /> · <c:out value="${capsule.ownerName}" /></p></c:if>
    <h1><c:out value="${capsule.title}" /></h1>
    <div class="detail-timer"><tc:countdown capsuleId="${capsule.id}" unlockAtMillis="${capsule.unlockAtMillis}" /></div>
    <c:choose>
        <c:when test="${capsule.unlocked}">
            <img class="detail-photo" src="${pageContext.request.contextPath}/media?id=${capsule.mediaFileId}" alt="Фото капсулы">
            <div class="capsule-note capsule-note--large"><c:out value="${capsule.note}" /></div>
            <ul class="tag-list"><c:forEach items="${capsule.tags}" var="tag"><li><c:out value="${tag.name}" /></li></c:forEach></ul>
        </c:when>
        <c:otherwise></c:otherwise>
    </c:choose>
</article>
<%@ include file="fragments/footer.jspf" %>
