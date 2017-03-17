<ul>
<g:each in="${automatic}" var="user">
	<li class="listColumn">
		<g:set value="${!blockedList.contains(user)}" var="blocked"/>
		<input type="checkbox" class="autoUser" value="${user}" <% blocked ?  out<<'checked' : '' %>/>
		<g:if test="${!blocked}">
			<span class="blockedUser">${user.toString()}</span>
		</g:if>
		<g:else>
			<span>${user.toString()}</span>
		</g:else>
	</li>
</g:each>	
</ul>