<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tc" tagdir="/WEB-INF/tags" %>
<c:set var="pageTitle" value="Лента капсул · Капсула времени" />
<%@ include file="fragments/header.jspf" %>
<section class="hero hero--compact">
    <div>
        <p class="eyebrow">ЛИЧНЫЕ ПОСЛАНИЯ ИЗ ПРОШЛОГО</p>
        <h1>Открой момент, когда придёт время</h1>
        <p class="lead">До указанной даты видны только заголовок и таймер. Заметка и фото откроются автоматически.</p>
    </div>
    <a class="button button--primary" href="${pageContext.request.contextPath}/capsules/new">Запечатать момент</a>
</section>
<c:if test="${param.created == '1'}"><p class="notice">Капсула сохранена и запечатана.</p></c:if>
<c:if test="${param.duplicate == '1'}"><p class="notice">Эта форма уже была отправлена. Повторная капсула не создана.</p></c:if>
<c:if test="${param.updated == '1'}"><p class="notice">Изменения сохранены.</p></c:if>
<c:if test="${param.deleted == '1'}"><p class="notice">Капсула удалена.</p></c:if>
<section class="capsule-grid" aria-label="Капсулы времени">
    <c:choose>
        <c:when test="${empty capsules}">
            <div class="empty-state"><span class="empty-icon">✳</span><h2>Здесь пока тихо</h2><p>Создай первую капсулу и назначь ей дату открытия.</p></div>
        </c:when>
        <c:otherwise>
            <c:forEach items="${capsules}" var="capsule">
                <tc:capsule-card capsule="${capsule}" currentUserId="${currentUserId}" isAdmin="${isAdmin}"
                                 contextPath="${pageContext.request.contextPath}" />
            </c:forEach>
        </c:otherwise>
    </c:choose>
</section>
<%@ include file="fragments/footer.jspf" %>
