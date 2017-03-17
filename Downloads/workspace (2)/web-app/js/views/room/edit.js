define(["jquery", "mediator", "ajax",  "jquery-ui"], function($,mediator){
	var edit = {
			courses: []
	};
	function setup(id) {
		function addPresenter(id) {
			
			var usercode = $('#presenterUsercode').val()
			$('#oresenterUsercode').val('')
			
			var params = {
					url:'/room/add_owner/' + id,
					data:{user: usercode}, 
					onSuccess: function(data) {
						if(data.success) {
							$('#presenterError').html('')
							if(data.user) {
								if(data.allowed) {
									$('#presenterList').append("<tr><td>" + data.user.username + "</td>" +	
												   "<td>" + data.user.name + "</td>" +
												   "<td>" + data.user.email + "</td>" +
												   "<td id=" + data.user.id + " ><img class='deletePresenter'src='/images/ui/delete_black.png'/></tr>")
								} else {
									$('#presenterError').html(data.user.username + ' does not have access to be a lecturer.')
								}
							}
						} else {
							$('#presenterError').html(usercode + ' does not exist.')
						}
					}
			};
			mediator.publish('ajax-json', params);

		};
		
		$("#presenterAdd").on("click", function(){
			addPresenter(id);
		});
		
		/** Delete presenter **/
		$("#presenterList").on("click", ".deletePresenter", function(event){
			var element = event.currentTarget;
			var userId = element.parentNode.id
			var params = {
					url:'/room/remove_owner/' + id,
					data:{userId: userId}, 
					onSuccess: function(data) {
						if(data.success) {
							$(element).parent().parent().remove()
						}
					}
			};
			mediator.publish('ajax-json', params);
		});
		
		function addPresentation(id, element) {
			var presentationId = element.parentNode.id
			var params = {
					url:'/room/add_presentation/' + id,
					data:{presentationId: presentationId}, 
					onSuccess: function(data) {
						if(data.success) {
							$(element).html('tick');
							$(element).removeClass();
						}
					}
			};
			mediator.publish('ajax-json', params);
			
		}
		
		$("#presentationList").on("click", ".addPresentation", function(event){
			addPresentation(id, event.currentTarget);
		});
		
		function addCourse(id) {
			
			var courseCode = $('#courseCode').val()
			$('#courseCode').val('')
			if(courseCode == null) {
				return;
			}
			var params = {
					url:'/room/add_course/' + id,
					data:{course: courseCode}, 
					onSuccess: function(data) {
						if(data.success) {
							$('#courseError').html('')
							if(data.added) {
								$('#courseList').append("<tr><td>" + courseCode + "</td>" +	
											  		"<td id=" + courseCode + " ><img class='deleteCourse'src='/images/ui/delete_black.png'/></tr>");
										
							}
						} else {
							$('#courseError').html(courseCode + ' does not exist.')
						}
					}
			};
			mediator.publish('ajax-json', params);

		};
		
		$("#courseAdd").on("click", function(){
			addCourse(id);
		});
		
		function removeCourse(id, element) {
			var courseCode = element.parentNode.id
			var params = {
					url:'/room/remove_course/' + id,
					data:{course: courseCode}, 
					onSuccess: function(data) {
						if(data.success) {
							$(element).parent().parent().remove()
						}
					}
			};
			mediator.publish('ajax-json', params);
		}
		
		$("#courseList").on("click", ".deleteCourse", function(event){
			removeCourse(id, event.currentTarget);
		});
		
		function saveChanges(id) {
			var name = $('#name').val();
			var params = {
					url:'/room/update_name/' + id,
					data:{name: name}, 
					onSuccess: function(data) {
						window.location = '/room/show/' + id
					}
			};
			mediator.publish('ajax-json', params);			
		}
		
		$("#save").on("click", function(){
			saveChanges(id);
		});
		
		
		// Auto complete linked course codes input box
		$("#courseCode").autocomplete({
			source: function(request, response) {
				// limit the results to ten
		        var results = $.ui.autocomplete.filter(edit.courses, request.term);

		        response(results.slice(0, 10));
		    },
			appendTo:"#courseCodeWrapper"
		});
		
		
		function updateRoomAM(roomId, userId) {
			if($('input:radio[name='+userId+']:checked').val()) {
				$.post('/room/managementMemebers/' + roomId + '?add=true&userId=' + userId);
			} else {
				$.post('/room/managementMemebers/' + roomId + '?add=true&userId=' + userId);		
			}
		}

		function removePresenter(event) {	
			$(event.target).parent().remove()	
		}

		function addPresenterFromText() {
			
			var choiceText = $('#ownerTextEntry').val();
			if (choiceText !== ''){
				var choice = $("<div class='listedChoice'></div>");
				var node = $("<input class='owner disableEnterTextField' type='text' name='owner' />");
				node.val(choiceText);
				choice.append(node);
				
				var removeButton = $("<div class='nice small blue radius button removeOwnerButton'>X</div>");
				
				choice.append(removeButton);
				
				$('#ownerListDiv').append(choice);

				$('#ownerTextEntry').val("");
			}
			
		}

		$('#ownerTextAddButton').live('click', addPresenterFromText);

		$('.removeOwnerButton').live('click', removePresenter);

		$('#ownerTextEntry').keypress(function(event){
			if(event.keyCode == 13)
				{
					addChoiceFromText()
					return false;
				}
		})


		function setupUpdateToInviteList(roomId, initialOffset) {
			var currentOffset = 0;
			var numNodes =  $("#inviteList").children().length;
			$('#moreInvited').on('click', function() {
				$('#lessInvited').attr("disabled", false);
				$.get('/room/invite_list/' + roomId + "?offset=" + (currentOffset + numNodes), function(html){
					
					if( $(html).length > 0) {
						$('#inviteList').html(html);
						currentOffset += numNodes;
						numNodes = $("#inviteList").children().length;	
					} else {
						$('#moreInvited').attr("disabled", true);
					}
				});
			});
			$('#lessInvited').on('click', function() {
				$('#moreInvited').attr("disabled", false);
				if(currentOffset > 0) {
					$.get('/room/invite_list/' + roomId + "?offset=" + (currentOffset - initialOffset),function(html){
						$('#inviteList').html(html);
						numNodes = $("#inviteList").children().length;
						currentOffset -= numNodes;
					});			
				} else {
					$('#lessInvited').attr("disabled", true);
				}
			});
		}

		/** Expand and hide **/
		function toggleExpand(event) {
			var qForm = $(event.currentTarget).parent()
			qForm.find('.imgDown').toggle();
			qForm.find('.imgUp').toggle();
			qForm.find('.dropdownDiv').toggle(300);
		}
		
		$(".toggleDiv .label").on("click", toggleExpand);

		function toggleAutoUser(event) {	
			var element = $(event.target).parent();
			var user = $.trim(element.text());
			var params = {
					url:'/room/toggleAutoUser/' + id,
					data:{user: user}, 
					onSuccess: function(data) {
						if(data.success) {
							if(data.blocked) {
								$("> span", element).addClass('blockedUser');
							} else {
								$("> span", element).removeClass('blockedUser');
							}	
							$("#studentTotal").html(data.enrolledCount + ' Automatically Enrolled Students, ' + data.blockedCount + 
									' Blocked');
						}
					}
			};
			mediator.publish('ajax-json', params);	
		}
		
		$('.autoUser').live('click', toggleAutoUser);
		
		var currentVersion = 0
		function getStudentListAjax(){
			
			var params = {
				url: '/room/auto_student_list_updated/' + id,
				onComplete: function() {
					setTimeout(getStudentListAjax, 4000);
				},
				onSuccess: function(data){
					if(data.autoStudent != currentVersion.autoStudent) {
						updateStudentList();
						currentVersion = data;
					}
				}
			};

			mediator.publish("ajax-json", params);
		}
		
		function initialiseStudentListAjax(){
			setTimeout(getStudentListAjax,4000);
		}
		
		initialiseStudentListAjax();
		
		
		function updateStudentList() {
			var params = {
				url: '/room/get_auto_student_list/' + id,
				onSuccess: function(data){
					$("#studentList").html('');
					$("#studentList").html(data.template);
					$("#studentTotal").html(data.enrolledCount + ' Automatically Enrolled Students, ' + data.blockedCount + ' Blocked');
				}
			};

			mediator.publish("ajax-json", params);			
		}
		

		function addInvited(id) {
			
			var user = $('#inviteUsercode').val()
			$('#inviteUsercode').val('')
			if(user == null) {
				return;
			}
			var params = {
					url:'/room/add_invited/' + id,
					data:{user: user}, 
					onSuccess: function(data) {
						if(data.success) {
							$('#inviteError').html('')
							$('#emptyInvited').html('')
							$('#invitedList').append("<li class='listColumn'><span class='deleteInvited'>" +
									"<img src='/images/ui/delete_red.png'></span>" + user + "</li>");
							$("#inviteTotal").html('');
							$("#inviteTotal").html(data.total + ' Invited Students');							
						} else {
							$('#inviteError').html(user + ' does not exist.')
						}
					}
			};
			mediator.publish('ajax-json', params);

		};
		
		$("#addInvited").on("click", function(){
			addInvited(id);
		});

		function removeInvited(event) {	
			var element = $(event.target).parent().parent();
			var user = $.trim(element.text());
			var params = {
					url:'/room/remove_invited/' + id,
					data:{user: user}, 
					onSuccess: function(data) {
						if(data.success) {
							element.remove();
							$("#inviteTotal").html('');
							$("#inviteTotal").html(data.total + ' Invited Students');
						}
					}
			};
			mediator.publish('ajax-json', params);	
		}
		
		$('.deleteInvited').live('click', removeInvited);
		
		
	}
	edit.setup = setup;
	return edit;
	

});
