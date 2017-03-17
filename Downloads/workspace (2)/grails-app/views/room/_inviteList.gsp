<g:each in="${userInstanceList}" var="user">
	<tr><td>
	<g:set var="userInvited" value="${roomInstance.invited.contains(user)}"/>  ${user.toString()}
		<input id="addAM" onChange="updateRoomAM(${roomInstance.id}, ${user.id})" type="radio" name="${user.id}" value="true" <% userInvited ? out<< 'checked' : '' %>/>Invited
		<input id="addAM" type="radio" name="${user.id}" value="false" <% !userInvited ? out<< 'checked' : '' %> />Uninvited
	</td></tr>					
</g:each>
