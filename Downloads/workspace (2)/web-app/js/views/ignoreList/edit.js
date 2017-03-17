define(['jquery','mediator','ajax'],function($,mediator){
	

function removeChoice(event){
			var word = $(event.currentTarget).parent().find('.ignoredWord').html().trim();
			
			var id = $(event.currentTarget).attr('class').split(' ')[0].split('-')[1];
			
			
	         var params = {
	              url: '/ignoreList/removeWord',
	              onSuccess: null,
	              data: {
	                  id: id,
	                  word: word
	              }
	          };
	          
	          mediator.publish("ajax-json",params);
	
			$(event.currentTarget).parent().parent().remove();
		}
		
	/**
	 * Creates an answer from the textbox for entering answers.
	 */
	function addChoiceFromText(id,word){
	
		
		if (word !== ''){
			
			var image = $('<img alt="Remove" src="/images/ui/delete.png" />');
			var button = $('<span class="removeChoiceButton-' + id + ' removeChoiceButton button_normal"></span>');
			button.append(image);
			button.on('click',removeChoice);
			
			var wordSpan = $('<span class="ignoredWord">'+ word + '</span>');
			var noBreak = $('<nobr></nobr>');
			
			noBreak.append(wordSpan);
			noBreak.append('&nbsp;');
			noBreak.append(button);
			noBreak.append('&nbsp;');
			
			var outerSpan = $('<span class="ignoredWords"></span>');
			
			outerSpan.append(noBreak);
			
			$('#ignoreListDiv-'+id).append(outerSpan);
			
			 var params = {
		              url: '/ignoreList/addWord',
		              onSuccess: null,
		              data: {
		                  id: id,
		                  word: word
		              }
		          };
		          
			mediator.publish("ajax-json",params);
	
		}
		
	}
	
	$('.choiceTextAddButton').on('click',function(){
		var id = $(event.currentTarget).attr('id').split('-')[1];
		var word = $('#choiceTextEntry-'+id).val();
		addChoiceFromText(id,word);
		$('#choiceTextEntry-'+id).val('').focus();
		
	});
	
	$('.removeChoiceButton').on('click',removeChoice);
	
	$('.choiceTextEntry').on('keypress', function(){
		if(event.keyCode == 13){
			event.preventDefault();
			
			var id = $(event.currentTarget).attr('id').split('-')[1];
			var word = $(event.currentTarget).val().trim();
			addChoiceFromText(id,word)
			$(event.currentTarget).val('').focus();
		}
	});
	
});