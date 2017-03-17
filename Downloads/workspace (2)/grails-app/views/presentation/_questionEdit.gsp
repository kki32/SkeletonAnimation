<g:set var="id" value="${qo?.id}" />
<g:set var="locked" value="${qo!=null && qo.askedQuestion.isLocked()}" />
<div class="toggleDiv">
	<g:if test="${qo != null}"> 
		<g:set var="mcquestion" value="${qo.askedQuestion.answerStrategy?.instanceOf(org.ucanask.AnswerStrategy.MultiChoiceStrategy)}" />
		<g:set var="ftquestion" value="${qo.askedQuestion.answerStrategy?.instanceOf(org.ucanask.AnswerStrategy.FreeTextStrategy)}" />
	</g:if>
	<form class="resetForm qForm${locked ? ' questionLocked' : ''}" id="${id}">
		<div class="label expandOnClick">
			<span class="dropdownImages">
				<g:img class="imgDown link" file="ui/arrow_dropdown.png" />
				<g:img class="imgUp link" file="ui/arrow_dropup.png" style="display:none;"/>
			</span>
			<label for="thisDoesntExist" class="qLabel bold">${qo!=null ? qo.toString():''}</label>
			<span class="right"><span class="saveButton button_normal">Save</span>
			<span class="deleteButton button_normal"><img src="/images/ui/delete_white_small.png" /></span></span>
	    </div>
	    
	    
        <div class="dropdownDiv">
        	<g:if test="${locked}">
	    		<span class="questionLocked offset-1"><g:message code="default.question.not.editable" args="[qo.askedQuestion.whyLocked]" default="This question cannot be edited." />
	    		 <span class="cloneQuestion link">Click here to <b>clone this question</b>.</span></span>
	    	</g:if>
	    	<div class="questionEditable" <g:if test="${locked}">style='display: none;'</g:if>>
				<div class="grids">
				
				<div class="grid-10 offset-1">
	       			<div class="questionError" style="display:none;"></div>
					<input type="text" placeholder="Enter question" class='questionText margin-b-10' name="questionText" style="width:100%;" value="${qo!=null ? qo.toString():''}" />
	        		
				</div>
				</div>
				<div class="grids">
						<div class="grid-5 offset-1">
							<div class="qType">
								<div class="left mcType link <g:if test="${mcquestion || !ftquestion}">selected</g:if>"><div><g:img class="type-icon" file="ui/icon_multichoice.png" /></div><div>Multichoice</div></div>
								<div class="left ftType link <g:if test="${ftquestion}">selected</g:if>"><div><g:img class="type-icon" file="ui/icon_openended.png" /></div><div>Text responses</div></div>
							</div>
							<div class="clear">
								<input type="text" name="questionType" class="questionType hidden" value="${mcquestion || !ftquestion ? 'Multi-Choice':'FreeText'}"/>
								<input type="text" class="questionTypeOrig hidden" value="${mcquestion || !ftquestion ? 'Multi-Choice':'FreeText'}"/>
							</div>
							<div style="display: ${mcquestion || qo?.askedQuestion?.answerStrategy == null ? '' : 'none'}" class="questionTypes Multi-Choice">
								<div>
									<span><select class="templatePicker margin-b-5 margin-t-10">
												<option>Pick a template</option>
						                        <option>True/False</option>
						                        <option>Yes/No</option>
						                        <option>Yes/No/Maybe</option>
						                        <option>Yes/No/Don't know</option>
						                    </select></span>
						    	</div>
								<div>
									Allow multiple selections <input type="checkbox" class="multiselectChk" name="multiselect" <% (mcquestion && qo.askedQuestion.answerStrategy.multiselect) ? out<<'checked' : '' %> />
									<span class="hidden multiselectOrig">${ (mcquestion && qo.askedQuestion.answerStrategy.multiselect) ? 'true' : 'false' }</span>
								</div>
							</div> 
						</div>
					<div class="grid-10 questionTypes Multi-Choice" style="display: ${mcquestion || qo?.askedQuestion?.answerStrategy == null ? '' : 'none'}">
		        	
						<div class="show-on-ie"><g:message code="default.label.answerChoices" default="Answers"/>:</div>
						<g:textField class='choiceTextEntry width-90' placeholder="Enter answers" name='choiceTextEntry'/>
						<span class="choiceTextAddButton button_normal">Add</span>
						<span class="answerCount hidden">${mcquestion ? qo.askedQuestion.answerStrategy.choices?.size() : 0}</span>
						<div class="choiceListDiv">
					
							<g:if test="${mcquestion}">
								<% def i = 0 %> 
							<g:while test="${i < qo.askedQuestion.answerStrategy.choices?.size()}">
						    	<%i++%> 
						    	<div class="listedChoice">
								<input type='text' class='choiceTextField disableEnterTextField width-90' name='choice' value="${qo.askedQuestion.answerStrategy.choices.size() >= i ? qo.askedQuestion.answerStrategy.choices[i-1] : ""}"/>
								<span class='removeChoiceButton button_normal'><img src="/images/ui/delete.png" /></span>
								</div>
							</g:while>
							</g:if>
						</div>
					</div>
				</div>
			</div>
		</div>
    </form>
</div>