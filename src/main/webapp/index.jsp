<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:choose>
    <c:when test="${not empty sessionScope.userId}"><c:redirect url="${pageContext.request.contextPath}/capsules" /></c:when>
    <c:otherwise><c:redirect url="${pageContext.request.contextPath}/login" /></c:otherwise>
</c:choose>
