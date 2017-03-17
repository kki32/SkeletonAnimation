/**
 * Some js for making the presentation edit page work, uses createQuestion.js also
 */

define(["jquery","mediator", "views/presentation/createQuestion", "notificationHandler"], function($, mediator, createQuestion){
	
	/**
	 * Resets the forms. This fixes issues with the back button and
	 * browsers retaining form information.
	 */
	$(document).ready(function() {
		$('.resetForm').each(function(){ this.reset(); });
		if($('#questionList :visible').children().length===0) {
			createNewQuestion()
		}
	});
	
	/**
	 * Uses JSON to post presentation name and each question form.
	 * Checks question forms for simple errors first.
	 * Only submits questions with changes.
	 */
	function saveChanges(event) {
		event.stopPropagation();
		
		var tbox = $('#name');
		if (tbox.val() != tbox[0].defaultValue && tbox.val().trim().length != 0) {
			$.post('/presentation/update/'+'?ignorecache='+myTimestamp(), $('#presName').serialize(), function(data){
				$('#presNameError').css('color', data.colour);
				$('#presNameError').html(data.message);
				$('#presNameError').show();
				tbox[0].defaultValue = tbox.val();
			}, "json");
		}
		
		$('#questionList form').each(function() {
			saveQuestion($(this));
		});
	}
	/**
	 * Saves a question (if changes have been made)
	 */
	function saveQuestion(form) {
		var action = createQuestion.checkForChanges(form, false);
		if (action) {
			$.post(action+'?presid='+createQuestion.presid+'&ignorecache='+myTimestamp(), form.serialize(), function(respData){
				var err = form.find('.questionError');
				err.css('color', respData.colour);
				err.html(respData.message);
				err.show();
				if (respData.success) {
					form.attr('id', respData.id);
				}
			}, "json");
		}
	}
	/**
	 * Adds a new empty question by ajax requesting the html from the server.
	 */
	function createNewQuestion() {
		if ($('#questionList :visible').children().length!==0) {
			var form = $('#questionList :visible').children().first();
			if (!form.attr('id')) {
				var err = form.find('.questionError');
				err.css('color', 'red');
				err.html("Please save this question first");
				err.show();
				return;
			}
		}
		$.get('/question/createQuestion/', function(data) {
			var newQ = $(data).first();
			newQ.hide();
			$('#questionList').prepend(newQ);
			newQ.show('slow');
		});
	}
	/**
	 * Add all the action listeners
	 */
	$("#create").on('click', createNewQuestion);

	$('#saveAll').on('click', saveChanges);
	
	$('#questionList').on('click', '.dropdownDiv', function(event){
		event.stopPropagation();
		$(event.currentTarget).find('.questionError').hide();
	});
	
	$('#questionList').on('click', '.expandOnClick', function(event) {
		createQuestion.toggleExpandQuestion($(event.currentTarget).parent());
	});
	
	$('#questionList').on('click', '.cloneQuestion', function(event){
		var form = $(event.currentTarget).closest('form');
		form.find('div.questionEditable').show('fast');
		form.removeClass('questionLocked');
		form.find('span.questionLocked').remove();
	});

	$('#questionList').on('click', '.saveButton', function(event) {
		event.stopPropagation();
		saveQuestion($(event.currentTarget).closest('form'));
	});
	return createQuestion;
	
});
