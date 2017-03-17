
<g:set var="id" value="${questionOccurrence.id}" />
<g:set var="answered" value="${responses[id] != null}" />
<div id="questionDiv${id}" class="toggleDiv ${ answered ? 'disabledQuestion':'' }">
    <div id="label${id}" onclick="toggleExpandItem(${id})" class="am label bold">
    	<g:img id="imgDown${id}" file="ui/arrow_dropdown.png"/>
    	<g:img id="imgUp${id}" file="ui/arrow_dropup.png" style="display:none"/>
    	${questionOccurrence.toString()}
    </div>
    <div id="dropdownDiv${id}">
		<div style="display: none;" id="messageDiv${id}"></div>
		<g:form action="respond" name="form${id}">
			<g:if test="${questionOccurrence.askedQuestion.answerStrategy.instanceOf(
					org.ucanask.AnswerStrategy.MultiChoiceStrategy)}">
<!-- Multi-Choice -->
			 	
				<g:each in="${questionOccurrence.askedQuestion.answerStrategy.choices}" var="choice">
				
			    <div class="grids listed updateSelectionStyles" >
			    	<div class="grid-16">
			   			
			   			<g:if test="${questionOccurrence.askedQuestion.answerStrategy.multiselect}">
	<!-- multi-select -->	<g:checkBox name="choice${id}" id="select${choice.id}" checked="${answered && responses[id].choices.contains(choice)? 'true':''}" value="${choice.id}" />
		            	</g:if>
		            	<g:else>
	<!-- single-select -->	<g:radio name="choice${id}" id="select${choice.id}" checked="${answered && responses[id].choices.contains(choice)? 'true':''}" value="${choice.id}" />
		            	</g:else>
		            	<label for="select${choice.id}" id="chkLabel${choice.id}" >
		            	${choice}</label>
        			</div>
       		 	</div>
			 	</g:each>
			 	<div class="grids">
			    	<div class="grid-16">
			 			<div class="button_normal margin-top-5 right sendSelection" id="sub${id}">Submit Answer</div>
			 		</div>
			 	</div>
			</g:if>
			<g:elseif test="${questionOccurrence.askedQuestion.answerStrategy.instanceOf(
					org.ucanask.AnswerStrategy.FreeTextStrategy)}">
<!--  FreeText -->
				<div class="grids">
			  		<div class="grid-16">
			  		<g:if test="${answered}">
			  			<g:textArea value="${responses[id].toString()}" name="text" required="" rows="4" id="text${id}" class="am" />
			  		</g:if>
			  		<g:else>
						<g:textArea placeholder="Answer here..." name="text" required="" rows="4"  id="text${id}" class="am ftTextArea" />
					</g:else>
        			</div>
       		 	</div>
			 	<div class="grids">
			    	<div class="grid-16">
						<div id="sub${id}" class="button_normal right ftSendSelection">Submit Answer</div>
			 		</div>
			 	</div>
			</g:elseif>
			<input type="hidden" name=presid value="${presentationInstance.id}">
			<input type="hidden" name="quesid" value="${id}">
		</g:form>    
	</div> 
</div>