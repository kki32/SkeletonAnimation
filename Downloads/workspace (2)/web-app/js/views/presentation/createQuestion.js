/**
 * All the js for creating questions, used in presentation/show for presenter and presentation/edit
 */

define(["jquery","mediator", "notificationHandler"], function($, mediator){
	
	/** The presentation id, set when requiring this js file */
	var createQuestion = { presid:null };
	/** 
	 * Display a list of template answers to select from which will pre-populate
	 * the answer text boxes. String must be split by '/'. 
	 */
	function addTemplateAnswers(item) {
		var form = $(item).closest('form');
		var selection = $(item).val();
		if (selection != "Pick a template") {
			var answers = selection.split('/');
			form.find('.choiceListDiv').empty();
			
			for (var i = answers.length -1; i >= 0; i--) {
				form.find('.choiceTextEntry').val(answers[i]);
				addChoiceFromText(form);
			}
		}
	}
	
	
	/** 
	 * Add functionality to change the question type displayed
	 */
	function swapQuestionTypes(item, showType) {
		$(item).parent().children().removeClass('selected');
		$(item).addClass('selected');
		form = $(item).parent().parent().parent();
		form.find('.questionTypes').hide();
		form.find('.questionType').val(showType);
		form.find('.'+showType).show();
	}
	/**
	 * Confirms and then hides the question, to be removed on next save.
	 */
	function deleteQuestion(event) {
		if (confirm("Are you sure you want to remove this question?")) {
			form = $(event.target).closest('form');
			form.parent().hide('fast');
			var id = form.attr('id');
			if (id) {
				$.getJSON('/presentation/remove_question/'+id+'?presid='+createQuestion.presid+'&ignorecache='+myTimestamp());
			}
			form.parent().remove();
		}
		event.stopPropagation();
	}
	
	/**
	 * Checks a question form for any changes to the inputs
	 * If showPage is true, it's on pres/show so don't toggle dropdown div
	 * @return the action url to submit to or false if no changes
	 */
	function checkForChanges(form, showPage) {
		var id = form.attr('id');
		
		if (form.hasClass('questionLocked')) {
			return false;
		}
		if (!validate(form)) {
			if (!showPage) { toggleExpandQuestion(form, true); }
			return false;
		}

		var anythingChanged = false;
		form.find('.choiceTextField').each ( function(i, ans) {
	        if (ans.value != ans.defaultValue) {
	        	ans.defaultValue = ans.value;
	        	anythingChanged = true;
	        }
		});
		var ansCounter = form.find('.answerCount')
		var count = form.find('.choiceTextField').length.toString()
		if (ansCounter.html() != count) {
			ansCounter.html(count)
			anythingChanged = true;
		}
		var qtField = form.find('.questionText');
		if (qtField.val() != qtField[0].defaultValue) {
			qtField[0].defaultValue = qtField.val(); 
			anythingChanged = true;
		}
		var qtypeOrig = form.find('.questionTypeOrig')
		var qtype = form.find('.questionType').val()
		if (qtype != qtypeOrig.val()) {
			anythingChanged = true;
			qtypeOrig.val(qtype);
		}		
		var multiselect = form.find('.multiselectChk');
		var initialState = form.find('.multiselectOrig').html() == "true";
		if (multiselect[0].checked != initialState) {
			anythingChanged = true;
			form.find('.multiselectOrig').html(multiselect[0].checked.toString());
		}
    	if (!anythingChanged) {
    		return false; 
    	}
		
		if (id) {
			var action = '/question/update_JSON/'+id;
		}
		else {
			var action = '/question/save_JSON/';
		}
		
		return action;
	}

	/**
	 * Toggles the showing of the question. If show==true then it will show.
	 */
	function toggleExpandQuestion(qForm, show) {
		if (show) {
			qForm.find('.imgUp').hide();
			qForm.find('.imgDown').show();
			qForm.find('.dropdownDiv').show(300);
		}
		else {
			qForm.find('.imgDown').toggle();
			qForm.find('.imgUp').toggle();
			qForm.find('.dropdownDiv').toggle(300);
			qForm.find('.questionText').focus();
		}
	}
	/**
	 * Checks and tidies question form inputs. 
	 */
	function validate(form) {
		if (form.find('.questionText').val().trim().length == 0) {
			showQuestionError(form.find('.questionError'), "Please enter a question");
			return false;
		}
		
		if (form.find('.questionType').val()!="Multi-Choice") {
			return true;
		}
		
		addChoiceFromText(form.find('.choiceTextAddButton').parent());
		var seen = {};
		var answers = form.find('.choiceTextField');
		answers.each(function (i,ans) {
			ans.value = ans.value.trim(); // Trim whitespace
			if (ans.value.length>0) {
				if (seen[ans.value]) {	// Remove dupicates
			        $(ans).parent().remove();
				}
			    else {
			        seen[ans.value] = true;
			    }
			}
			else {	// Remove empty answers
				$(ans).parent().remove();
			}
		});
		if (answers.size()<2) { // show error message
			showQuestionError(form.find('.questionError'), "Please provide at least two answers");
			return false;// Don't submit if <2 answers
		}
		return true
	}
	/**
	 * Displays the error above the question.
	 */
	function showQuestionError(errDiv, errText) {
		errDiv.css('color', 'red');
		errDiv.html(errText);
		errDiv.show();
	}
	
	function removeChoice(event){
		$(event.currentTarget).parent().remove();
	}
	
	/**
	 * Creates an answer from the textbox for entering answers.
	 */
	function addChoiceFromText(form){
		var choiceText = form.find('.choiceTextEntry').val();
		if (choiceText !== ''){
			var choice = $("<div class='listedChoice'></div>");
			var node = $("<input class='choiceTextField disableEnterTextField width-90' type='text' name='choice' />");
			node.val(choiceText);
			choice.append(node);
			
			var removeButton = $("<span class='removeChoiceButton button_normal'></span>");
			var img = $("<img src='/images/ui/delete.png' />");
			removeButton.append(img)
			choice.append("&nbsp;");
			choice.append(removeButton);
			
			form.find('.choiceListDiv').prepend(choice);

			form.find('.choiceTextEntry').val("").focus();
		}
		
	}
	
	/**
	 * Adds a new empty question by ajax requesting the html from the server.
	 */
	function create1NewQuestion() {
		if ($('#questionList :visible').children().length > 0) {
			var err = $('#questionList .questionError');
			err.css('color', 'red');
			err.html("Please save this question first");
			err.show();
			return;
		}
		$.get('/question/createQuestion/', function(data) {
			var newQ = $(data).first();
			newQ.hide();
			newQ.find('.dropdownImages img').css("visibility", "hidden");
			newQ.on('click', '.saveButton', function(event) {
				saveQuestion($(event.currentTarget).closest('form'));
			});
			$('#questionList').prepend(newQ);
			newQ.show('slow');
		});
	}
	/**
	 * Saves a question (if changes have been made)
	 */
	function saveQuestion(form) {
		var action = checkForChanges(form, true);
		if (action) {
			$.post(action+'?presid='+createQuestion.presid+'&ignorecache='+myTimestamp(), form.serialize(), function(respData){
				if (respData.success) {
					location.reload();
				} else {				
					var err = form.find('.questionError');
					err.css('color', respData.colour);
					err.html(respData.message);
					err.show();
				}
			}, "json");
		}
		
	}
	
	/**
	 * Add all the action listeners
	 */
	// Create question on the presentation/show page
	$("#createQuestion").on('click', create1NewQuestion);
	
	$('#questionList').on('blur','.questionText', function(event){
		var input = $(event.target);
		input.closest('form').find('.qLabel')
		.html($('<div/>').text(input.val()).html());
	});
	
	$('#questionList').on('click', '.deleteButton', deleteQuestion);
	
	$('#questionList').on('click', '.mcType', function(event) { 
		swapQuestionTypes(event.currentTarget, 'Multi-Choice');
	});
	$('#questionList').on('click', '.ftType', function(event) { swapQuestionTypes(event.currentTarget, 'FreeText') });
	
	$('#questionList').on('change', '.templatePicker', function(event) { addTemplateAnswers(event.currentTarget) });
	
	$('#questionList').on('click','.choiceTextAddButton', function(event) { addChoiceFromText($(event.currentTarget).closest('form')); });
	
	$('#questionList').on('click', '.removeChoiceButton', removeChoice);
	
	$('#questionList').on('keypress', '.choiceTextEntry', function(event){
		if(event.keyCode == 13){
			addChoiceFromText($(this).closest('form'));
			event.preventDefault();
		}
	});
	
	$('.wrapper').on('keypress', '.disableEnterTextField', function(event){
		if(event.keyCode == 13){
			event.preventDefault();
		}
	});
	
	createQuestion.checkForChanges = checkForChanges;
	createQuestion.toggleExpandQuestion = toggleExpandQuestion;
	return createQuestion;
	
});
