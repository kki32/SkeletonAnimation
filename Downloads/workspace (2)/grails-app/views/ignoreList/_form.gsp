<div>
	<div class="group">
		<div class="label filterWordText">
        

			<span class="bold"> ${listInstance.title() }
			</span> <br />
			<g:textField id='choiceTextEntry-${listInstance.id }'
				class='choiceTextEntry' placeholder="Enter word"
				name='choiceTextEntry' />
			<div class="right">
				<span id='choiceTextAddButton-${listInstance.id }'
					class="button_normal choiceTextAddButton">&nbsp;<img alt="Add New Word Filter"
					src="/images/ui/add_white.png">&nbsp;
				</span>
			</div>
		</div>

		<div id="ignoreListDiv-${listInstance.id}" class="listed">

				<g:each in="${listInstance?.ignoredWords.sort() }">
					<span class='ignoredWords'>
					   <nobr>
							<span class="ignoredWord">${it}</span>&nbsp;<span class='removeChoiceButton-${listInstance.id } removeChoiceButton button_normal'><img
								src="/images/ui/delete.png" /></span></nobr>&nbsp;</span>
                        


				</g:each>


		</div>



	</div>

</div>