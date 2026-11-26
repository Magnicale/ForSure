<%@ tag body-content="empty" %>
<%@ attribute name="capsuleId" required="true" type="java.lang.Long" %>
<%@ attribute name="unlockAtMillis" required="true" type="java.lang.Long" %>
<time class="countdown" data-capsule-id="${capsuleId}" data-unlock-at="${unlockAtMillis}">Считаем время…</time>
