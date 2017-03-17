package org.ucanask

import grails.converters.*

import org.springframework.context.ApplicationContext
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.ldap.core.AttributesMapper
import org.springframework.ldap.core.DirContextOperations
import org.springframework.ldap.core.support.AbstractContextMapper
import org.springframework.security.access.annotation.Secured

import presentation.ResponseService
import org.ucanask.AnswerStrategy.FreeTextStrategy
import org.ucanask.QrCodeService.QrCode
import org.ucanask.Responses.*

import grails.plugins.springsecurity.Secured
import java.rmi.server.UID
import javax.naming.NamingException
import javax.naming.directory.Attributes
import javax.servlet.http.Cookie
import org.apache.commons.lang.RandomStringUtils
import org.codehaus.groovy.grails.web.binding.ListOrderedSet


/**
 * 
 */
class PresentationController {

	def springSecurityService
	def ldapTemplate

	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	/**
	 * Presentations landing page - redirects to list at this point
	 */
	def index() {
		redirect(controller: "room", action: "list", params: params)
	}
	
	/** 
	 * Demo of how to return all the students from a given course
	 * Uses AttributesMapper
	 * 
	 * Usage example:
	 * http://<sitename>/presentation/showStudentsOnCourse?courseId=cosc121
	 * 
	 * @return a list of full names of the users on the course (cn == complete name)
	 */
	@Secured(['ROLE_ADMIN'])
	def showStudentsOnCourse() {
		def course = params.getProperty("courseId")
		def students = ldapTemplate.search("", "(uccourse=" + course + "*)", new AttributesMapper() {
			@Override
			public Object mapFromAttributes(Attributes attrs) throws NamingException {
				return attrs.get("cn").get()
			}
		})
		render "<b>Students on $course:</b><br>"
		render "count: $students.size<br />"
		render "-------<br />"
		students.each {
			render it.toString()
			render "<br>"
		}
	}

	/**
	* Demo of how to return all the courses for a given student
	* Uses ContextMapper
	*
	* Usage example:
	* http://<sitename>/presentation/showCoursesForStudent?userId=abc123
	*
	* @return a list of courses the user provided is enrolled in
	*/
	@Secured(['ROLE_ADMIN'])
	def showCoursesForStudent() {
		def uid = params.getProperty("userId")
		def courses = ldapTemplate.search("", "uid=" + uid + "*", new MyContextMapperCourses())
		render "<b>Courses for $uid:</b><br>"
		courses[0].each {
			render it.toString()
			render "<br>"
		}
		
	}
	
	/**
	* Demo of how to return an alphabetically ordered list of every course that is being taken
	* by a student at the moment
	* Uses AttributeMapper
	*
	* Usage example:
	* http://<sitename>/presentation/getUniqueCourses
	*
	* @return an alphabetically ascending sorted list of courses
	*/
	@Secured(['ROLE_ADMIN'])
	def getUniqueCourses() {
		def courses = ldapTemplate.search("", "(&(uid=*)(uccourse=*))", new AttributesMapper() {
			@Override
			public Object mapFromAttributes(Attributes attrs) throws NamingException {
				return attrs.get("uccourse").get()
			}
		})
		render "<b>Unique Course List:</b><br>"
		def courseSet = [] as SortedSet
		courseSet.addAll(courses)
		render courseSet
	}
	
	/**
	 * Creates a QR Code that will allow guests to connect directly to a presentation page
	 */
	def qrCode () {
		// Pass these params in (presentation id and access code)
		String pres = params.id
		String accessKey = params.presentationCode
		
		// Get the full url
		def scheme = request.scheme
		def serverName = request.serverName
		def serverPort = request.serverPort // Not using this, port num only for local development
		def urlString = scheme + "://" + serverName + ":" + serverPort

		def qrCode  = new QrCode()
		qrCode.data = urlString + "/presentation/show/" + pres + "?presentationCode=" + accessKey
		// println urlString + "/presentation/show/" + pres + "?presentationCode=" + accessKey
		byte[] qr = qrCode.generateCode()
		response.contentType="image/png"
		response.outputStream << qr
		response.outputStream.flush()
	}
	
/*
	*//**
	 * Get a list of all of the presentations available to the current presenter.
	 * TODO: fix pagination
	 *//*
	@Secured(['ROLE_PR'])
	def list() {
		def myPresentations = []
		if (principal.authorities*.authority.contains("ROLE_ADMIN")) {
			myPresentations = Presentation.list()
		}
		else {
			myPresentations = Presentation.findAllWhere(
					owner: springSecurityService.getCurrentUser())
		}
		[presentationInstanceList: myPresentations, presentationInstanceTotal:
					myPresentations.size()]
	}*/
	
	/** Everyone has access to **/

	/**
	 * Displays a list of planned and current questions in the presentation
	 * given by the id and for the users credentials for that presentation
	 */
	def show() {
		// Get the current user (will be null if not logged in)
		def user = springSecurityService.currentUser		
		def presentationInstance = Presentation.get(params.id.toLong())
		if (!presentationInstance) {
			flash.message = message(code: 'default.not.found.message',
				args: [message(code: 'default.label.presentation', default: 'Presentation'), params.id])
			
			def room = Room.get(params.roomId);
			if (!room) {
				return redirect(controller: "room", action: "list")
			}
			else {
				return redirect(controller: "room", action: "show", id: room.id)
			}
		}
		def isGuest = false
		// Redirect the user to the right show that is appropriate for their access to THIS presentation
		switch(presentationInstance.room.userAccess(user?.id, presentationInstance)) {
			case AccessLevel.Admin:
			case AccessLevel.Presenter: 
				def accessCode = presentationInstance.accessKey
				while (accessCode == null) {	// Generates a unique 6 number code for this presentation
					presentationInstance.accessKey = RandomStringUtils.randomNumeric(6).toUpperCase()
					// println presentationInstance.accessKey
					if (!presentationInstance.save(flush: true)) {
						presentationInstance.accessKey = null
					}
					accessCode = presentationInstance.accessKey
				}
				return [presentationInstance: presentationInstance, view: AccessLevel.Presenter.toString(), accesskey: accessCode]				
			
			case AccessLevel.UnauthenticatedAudience:
				if (params.presentationCode == null || presentationInstance.accessKey != params.presentationCode) {
					flash.message = message(code: 'default.not.found.message',
						args: [message(code: 'default.label.presentation', default: 'Presentation'), params.id])
					return redirect(controller: "room", action: "list")
				}
				isGuest = true
			case AccessLevel.AuthenticatedAudience:
				if (!presentationInstance.active) {
					flash.message = message(code: 'default.no.active.presentation.message')
					return redirect(controller: "room", action: "show", id:params.roomId)
				}
				return audienceView(presentationInstance, user, isGuest)
				
			case AccessLevel.None:
			default:	
					// We redirect to the list as they shouldn't be in the room show either
				flash.message = message(code: 'default.not.found.message',
					args: [message(code: 'room.label', default: 'Room'), params.roomId])
				redirect(controller: "room", action: "list")
				return
		}
	}
	
	/** Only the current presenter or administrators have access to do **/
	
	/**
	 * Create a new presentation.
	 * Ensure only presenters with presenter access to the room can create a presentation
	 * Else redirect to the room show
	 */	
	@Secured(['ROLE_PR'])
	def create() {
		Room roomInstance = Room.get(params.roomId)
		if (!roomInstance) {
			flash.message = message(code: 'default.not.found.message',
					args: [message(code: 'room.label', default: 'Room'), params.roomId])
			redirect(controller: "room", action: "list")
			return
		}
		switch(roomInstance.userAccess(springSecurityService.currentUser?.id)) {
			case AccessLevel.Admin:
			case AccessLevel.Presenter: 
				def presentationInstance = new Presentation()
				presentationInstance.properties = params
				return ['presentationInstance':presentationInstance, roomId: params.roomId, roomName: roomInstance.toString()]
			default:
				flash.message = message(code: 'default.user.not.authorized')
				redirect(controller: "room", action: "show", params: [id : roomInstance.id])			
		}
	}
	
	/**
	 * Save the presentation if the user is an admin or the presentation of the room that they are trying to save to
	 * Redirect everyone to show regardless of whether the save was attempted.
	 */
	@Secured(['ROLE_PR'])
	def save() {
		Room roomInstance = Room.get(params.roomId)
		if (!roomInstance) {
			flash.message = message(code: 'default.not.found.message',
					args: [message(code: 'room.label', default: 'Room'), params.roomId])
			redirect(controller: "room", action: "list")
			return
		}
		def user = springSecurityService.currentUser
		switch(roomInstance.userAccess(user?.id)) {
			case AccessLevel.Admin:
			case AccessLevel.Presenter: 			
			// Create the room and set the correct room as its owner
				def presentationInstance = new Presentation(params)
				presentationInstance.room = roomInstance
				presentationInstance.owner = user
				if (!presentationInstance.save(flush: true)) {
					render(view: "create", model: [presentationInstance: presentationInstance, roomId: params.roomId, roomName: roomInstance.toString()])
					return
				}
				// At this point, if its all good we can carry on : )
				redirect(action: "edit", id: presentationInstance.id, 
					params: [roomId: params.roomId, roomName: roomInstance.toString()])
				return
			default: 
				flash.message = message(code: 'default.user.not.authorized')
		}
		redirect(controller: "room", action: "show", params: [id : roomInstance.id])
	}

	
	/**
	 * Editing an existing presentation can only be done by an admin or an owner for the room that the
	 * presentation belongs to
	 * @return
	 */
	@Secured(['ROLE_PR'])
	def edit() {
			// Find the presentation to edit
		def presentationInstance = Presentation.get(params.id)
		if (!presentationInstance) {
			flash.message = message(code: 'default.not.found.message',
				args: [message(code: 'default.label.presentation', default: 'Presentation'), params.id])
			redirect(controller: "room", action: "list")
			return
		}
		Room room = presentationInstance.room
			// Determine the users access
		switch(room.userAccess(springSecurityService.currentUser?.id)) {
			case AccessLevel.Admin:
			case AccessLevel.Presenter: 
					// Give the edit view the presentation to edit
				return [presentationInstance: presentationInstance]
			default:
					// Else not authorized and redirect to the presentations show
				flash.message = message(code: 'default.user.not.authorized')
				redirect(action: "show", id: presentationInstance.id, params: [roomId : room.id])
		}
	}

	/**
	 * Update an existing presentation if authorized to do so
	 */
	@Secured(['ROLE_PR'])
	def update() {
		def presentationInstance = Presentation.get(params.id)
		if (!presentationInstance) {
			flash.message  = message(code: 'default.not.found.message', 
				args: [message(code: 'default.label.presentation', default: 'Presentation'), params.id])
			return makeJSONResponse(false)
		}
			// Determine the users access
		switch(presentationInstance.room.userAccess(springSecurityService.currentUser?.id)) {
			case AccessLevel.Admin:
			case AccessLevel.Presenter: 
					// Authorized to update the presentation
				presentationInstance.name = params.name
				if (!presentationInstance.save(flush: true)) {
					flash.message = message(code: 'default.invalid.name', 
						args: [message(code: 'default.label.presentation', default: 'Presentation'), presentationInstance.id])
					return makeJSONResponse(false)
				}
				flash.message = message(code: 'default.updated.name.message', 
					args: [message(code: 'default.label.presentation', default: 'Presentation'), presentationInstance.id])
				return makeJSONResponse(true)
			 default:
			 		// Not authorized
				flash.message = message(code: 'default.user.not.authorized')				
		}
		return makeJSONResponse(false)
	}

	/**
	 * Delete the specified question from the database.
	 */
	@Secured(['ROLE_PR'])
	def delete() {
		println 'start'
		def presentationInstance = Presentation.get(params.id)
		if (!presentationInstance) {
			flash.message = message(code: 'default.not.found.message',
					args: [message(code: 'default.label.presentation', default: 'Presentation'), params.id])
			redirect(controller: "room", action: "list")
			return
		}
		def map
		switch(presentationInstance.room.userAccess(springSecurityService.currentUser?.id)) {
			case AccessLevel.Admin:
			case AccessLevel.Presenter:
				// Authorized to delete the presentation
				try {
					def roomId = presentationInstance.room.id
					presentationInstance.questions.removeAll()
					presentationInstance.delete(flush: true)
					println'here'
					render map = [success:true, roomId: roomId] as JSON
					return
				} catch (DataIntegrityViolationException e) {
					render map = [success:false] as JSON
					return 
				}
			default:
		 		// Not authorized
					render map = [success:false] as JSON 					
		}
		render map = [success:false] as JSON
		return
	}	
	
	/**
	 * Enable or disable guest users to participate in the presentation
	 * @return
	 */
	@Secured(['ROLE_PR'])
	def allow_guests() {
		def presentationInstance = Presentation.get(params.id)
		if (!presentationInstance) {
			flash.message = message(code: 'default.not.found.message',
				args: [message(code: 'default.label.presentation', default: 'Presentation'), params.presid])
			return makeJSONResponse(false)
		}
		switch(presentationInstance.room.userAccess(springSecurityService.currentUser?.id)) {
			case AccessLevel.Admin:
			case AccessLevel.Presenter:
				if(params.change == "true") {
					presentationInstance.toggleOpen()
				}
				def state = presentationInstance.open ? "state-on" : "state-off"
				def map = ['success': true, 'state': state]
				render map as JSON
			default:
				return makeJSONResponse(false)
		}
	}
	
	/** 
	 * Update the index of a question
	 */
	@Secured(["ROLE_PR"])
	def sort_questions() {
		def presentationInstance = Presentation.get(params.id)
		if (!presentationInstance) {
			flash.message = message(code: 'default.not.found.message',
				args: [message(code: 'default.label.presentation', default: 'Presentation'), params.presid])
			return makeJSONResponse(false)
		}
		switch(presentationInstance.room.userAccess(springSecurityService.currentUser?.id)) {
			case AccessLevel.Admin:
			case AccessLevel.Presenter:	
				def question = QuestionOccurrence.get(params.questionId)
				def oldIndex = presentationInstance.questions.indexOf(question)
				def newIndex = Integer.parseInt(params.index)
				if(oldIndex > newIndex) {
					presentationInstance.questions.remove(question)
					presentationInstance.questions.add(newIndex, question)
				} else {
					presentationInstance.questions.add(newIndex+1, question)	
					presentationInstance.questions.remove(question)			
				}
				return makeJSONResponse(true)
			default:
				redirect(action: "show", id: params.id, params:[roomId: presentationInstance.room.id])				
		}
		
	}
		
	/**
	 * Presenter calls this to make a question toggle a question and make it either visible if current invisible or
	 * invisible if currently visible.
	 * We check to ensure the user has control over THIS current presentation
	 */
	@Secured(["ROLE_PR"])
	def toggle_question() {   // TODO flash messages 
		def map = ['success': false]
		def presentationInstance = Presentation.get(params.presid)
		if (!presentationInstance) {
//			flash.message = message(code: 'default.not.found.message',
//					args: [ message(code: 'default.label.presentation', default: 'Presentation'), params.presid])
//			redirect(controller: "room", action: "list")
//			return
			render map as JSON
			return
		}
		def questionOcc = QuestionOccurrence.get(params.id)
		//if (!questionOcc) {
//			flash.message = message(code: 'default.not.found.message',
//					args: [message(code: 'question.label', default: 'Question'), params.id])
		//} else {
		if (questionOcc) {
			// Can only ask or end the question if an admin or a presenter for this presentation
			switch(presentationInstance.room.userAccess(springSecurityService.currentUser?.id)) {
				case AccessLevel.Admin:
				case AccessLevel.Presenter:
					if (params.change == "true") {
						presentationInstance.toggleQuestion(questionOcc)
					}
					def state = questionOcc.active ? "state-on" : "state-off"
					map = ['success': true, 'state': state]
					render map as JSON
					return
			}
		}
			// Everyone is redirected to the show and shown their appropriate view
		//redirect(action: "show", id: params.presid, params:[roomId: presentationInstance.room.id])
		render map as JSON
		return
	}
		
	/**
	 * The controller for handling the creation of the add_questions view for adding new questions to a presentation
	 * Only allow admins or the presenter for this presentation to add questions to this presentation
	 * @return Presentation instance and a list of all possible questions
	 */
	@Secured(["ROLE_PR"])
	def add_questions() {
		def presentationInstance = Presentation.get(params.id)
		if (!presentationInstance) {
			flash.message = message(code: 'default.not.found.message',
					args: [message(code: 'default.label.presentation', default: 'Presentation'), params.id])
			redirect(controller: "room", action: "list")
			return
		}
		// Can only add a question if an admin or a presenter for this presentation
		switch(presentationInstance.room.userAccess(springSecurityService.currentUser?.id)) {
			case AccessLevel.Admin:
			case AccessLevel.Presenter:
				def questions = Question.findAllByOwner(springSecurityService.currentUser)
				return [presentationInstance: presentationInstance, allQuestions: questions, roomInstance: presentationInstance.room]
			default:
				redirect(action: "show", id: params.id, params:[roomId: presentationInstance.room.id])				
		}
	}
	
	/**
	 * Add new planned questions to the presentation
	 * @return the show_presenter view
	 */
	@Secured(["ROLE_PR"])
	def addPlannedQuestions() {
		def presentationInstance = Presentation.get(params.id)
		if (!presentationInstance) {
			flash.message = message(code: 'default.not.found.message',
					args: [message(code: 'default.label.presentation', default: 'Presentation'), params.id])
			redirect(controller: "room", action: "list")
			return
		}
		// Can only add a question if an admin or a presenter for this presentation
		switch(presentationInstance.room.userAccess(springSecurityService.currentUser?.id)) {
			case AccessLevel.Admin:
			case AccessLevel.Presenter:
				params.list("questionsToAdd").each {
					def ques = Question.get(it)
					presentationInstance.addToQuestions(new QuestionOccurrence(askedQuestion: ques, presentation: presentationInstance))
				}
				if (!presentationInstance.save(flush: true)) {
					render(view: "add_questions", model: [presentationInstance: presentationInstance])
					return
				}
				flash.message = message(code: 'default.created.message',
						args: [message(code: 'default.label.presentation', default: 'Presentation'), params.id])
				break
			default:
		 		// Not authorized
				flash.message = message(code: 'default.user.not.authorized')	
		}
		redirect(action: "show", id: params.id, params:[roomId: presentationInstance.room.id])	
	}
	
	
	/**
	* Sets a presentation to active.
	* @param params .id: presentation id
	* @return Refreshes the list of presentations
	*/
	@Secured(["ROLE_PR"])
	def togglePresentationStatus() {
		def presentationInstance = Presentation.get(params.id)
		if (!presentationInstance) {
			flash.message = message(code: 'default.not.found.message',
					args: [message(code: 'default.label.presentation', default: 'Presentation'), params.id])
			//redirect(controller:"Room", action: "show", id: params.id)
			return makeJSONResponse(false)
		}
		// Can enable/disable presentation if an admin or a presenter for this presentation
		switch(presentationInstance.room.userAccess(springSecurityService.currentUser?.id)) {
			case AccessLevel.Admin:
			case AccessLevel.Presenter:
				if (params.change == "true") {
					if (presentationInstance.active) { presentationInstance.endPresentation() }
					else { presentationInstance.active = true }
				}
				def state = presentationInstance.active ? "state-on" : "state-off"
				def map = ['success': true, 'state': state]
				render map as JSON
				return
			default:
		 		// Not authorized
				flash.message = message(code: 'default.user.not.authorized')	
		}
		//redirect(controller:"Room", action: "show", id: params.id)
		return makeJSONResponse(false)
	}
	
	/**
	 * Enable audience members to ask questions
	 * @return
	 */
	@Secured(['ROLE_PR'])
	def enable_am_questions() {
		def presentationInstance = Presentation.get(params.id)
		if (!presentationInstance) {
			flash.message = message(code: 'default.not.found.message',
				args: [message(code: 'default.label.presentation', default: 'Presentation'), params.presid])
			return makeJSONResponse(false)
		}
		switch(presentationInstance.room.userAccess(springSecurityService.currentUser?.id)) {
			case AccessLevel.Admin:
			case AccessLevel.Presenter:
				if (params.change == "true") {
					presentationInstance.toggleAudienceQuestion()
				}
				def state = presentationInstance.allowUserQuestions ? "state-on" : "state-off"
				def map = ['success': true, 'state': state]
				render map as JSON
				return
		}
		//redirect(action: "show", id: params.id, params:[roomId: presentationInstance.room.id])		
		return makeJSONResponse(false)	
	}
	
	/**
	 * Show questions from the audience members
	 * @return
	 */
	@Secured(['ROLE_PR'])
	def show_am_questions() {
		def presentationInstance = Presentation.get(params.id)
		if (!presentationInstance) {
			flash.message = message(code: 'default.not.found.message',
				args: [message(code: 'default.label.presentation', default: 'Presentation'), params.presid])
			return makeJSONResponse(false)
		}
		switch(presentationInstance.room.userAccess(springSecurityService.currentUser?.id)) {
			case AccessLevel.Admin:
			case AccessLevel.Presenter:
				[presentationInstance: presentationInstance]
				break					
			default:
				redirect(action: "show", id: params.id, params:[roomId: presentationInstance.room.id])					
		}
	}
	
	/** Audience Members for this presentation have access to these **/

	/**
	 * Displays the current questions of the presentation given by the id, in an answerable format.
	 * Remove any questions that have already been answers
	 * (Private method, no security needed) 
	 */
	private def audienceView(presentationInstance, User user, isGuest) {
		def responses = [:]
		List questionsToShow = []
		presentationInstance.questions.each { ques ->
			if (ques.active){
				questionsToShow.add(ques)
			}
		}
		if (user != null) { // Shows which questions this user has responded to already 
			if (questionsToShow.size() > 0) { 
				presentationInstance.questions.each { quesOcc ->
					responses[quesOcc.id] = quesOcc.responses.find { it.userId == user.id }
				}
			}
		} else {
			// Creates an array of lists of questions answered in this user's session
			if (!session.answeredQuestions) {
				session.answeredQuestions = []
			}
			if (!session.answeredQuestions[presentationInstance.id as int]) {
				session.answeredQuestions[presentationInstance.id as int] = []
			}
			// Removes questions they have answered so they can only answer once
			session.answeredQuestions[presentationInstance.id as int].each { qoid ->
				questionsToShow.remove(QuestionOccurrence.get(qoid))
			}
		}
		
		[presentationInstance: presentationInstance, questionsToShow: questionsToShow, responses: responses, view: "Audience", userExists: loggedIn, isGuest: isGuest]
	}
	
	/**
	* Renders a JSON response, with a message and a colour for the message.
	* @param success true will display in green, false red.
	* @return A JSON response message.
	*/
	private def makeJSONResponse(boolean success) {
		def map = ["message": flash.message]
		map['success'] = success
		map['notification'] = success ? 'notification-alert' : 'notification-warning'
		map['colour'] = success ? "green" : "red" 
		render map as JSON
	}

	/**
	 * Post method sending in an audience member's selected choice/response.
	 * @param params .presid: presentation id, .quesid: question id, 
	 * 			.choice#: (where # is the quesid) selected resource id(s).
	 * @return A JSON response message.
	 * 
	 * Untested. Haven't added invite users yet so all rooms are currently open and anyone can answer
	 */
	def respond() {
		def presentationInstance = Presentation.get(params.presid)
		if (!presentationInstance) {
			flash.message = message(code: 'default.not.found.message',
				args: [message(code: 'default.label.presentation', default: 'Presentation'), params.presid])
			return makeJSONResponse(false)
		}
		def questionOcc = QuestionOccurrence.get(params.quesid)
		if (!questionOcc) {
			flash.message = message(code: 'default.not.found.message',
			 	args: [message(code:'questionOccurence.label', default: 'Question Occurrence'),params.quesid])
			return makeJSONResponse(false)
		}
		if (!questionOcc.active) {
			flash.message = message(code: 'default.question.inactive.message', args: [questionOcc.toString()])
			return makeJSONResponse(false)
		}
		// Creates a list if there's only one choice
		def res = null
		
		def user = springSecurityService.currentUser
		if (!user) {
			session.answeredQuestions[presentationInstance.id as int].add(params.quesid)
		}
		
		switch(presentationInstance.room.userAccess(user?.id, presentationInstance)) {
			case AccessLevel.AuthenticatedAudience:
			case AccessLevel.UnauthenticatedAudience:
				params.choice = params.list('choice'+questionOcc.id)
				params.userid = springSecurityService?.currentUser?.id
				res = ResponseService.addResponse(params)
				break;
		}
		// Handles submitting no checked boxes/radio button
		if (res) {
			flash.message = message(code: 'default.respond.message')
		}
		else {	
			flash.message = message(code: 'default.respond.failed.message')
			return makeJSONResponse(false)
		}
		makeJSONResponse(true)
	}	
	
	/**
	 * Recored questions asked to the presenter from the audience members
	 * @return
	 */
	def ask_presenter_question() {
		def presentationInstance = Presentation.get(params.id)		
		if (!presentationInstance) {
			flash.message = message(code: 'default.not.found.message',
				args: [message(code: 'default.label.presentation', default: 'Presentation'), params.presid])
			return makeJSONResponse(false)
		}
		if (!presentationInstance.allowUserQuestions) {
			flash.message = message(code: 'default.questions.not.allowed')
			return makeJSONResponse(false)
		}
		if(params.textResponse && params.textResponse.trim().length()>0) {
			params.userId = springSecurityService.currentUser?.id
			ResponseService.addAudienceQuestion(params)
			flash.message = message(code: 'default.ask.message.amQuestion')
			makeJSONResponse(true)
		} else {
			flash.message = message(code: 'default.respond.invalid.question')
		}
		makeJSONResponse(false)
		//redirect(action: "show", id: params.id, params:[roomId: presentationInstance.room.id])	
	}
	
	

	/**
	 * Get the total number of responses for all current question occurrences within this presentation
	 * @return the total number of responses within this presentation
	 * No danger in not locking this down
	 */
	def get_total_responses_json() {
		def presentationInstance = Presentation.get(params.id)
		def countResponses = 0
		
		def responses = [:]
		
		presentationInstance?.questions?.each{ quesOcc ->
				if(quesOcc.responses != null)
					responses[quesOcc.id.toString()] = quesOcc.responses.size()
		}
		def map = ["responseCounts": responses, "feedbackCount": presentationInstance?.audienceQuestions?.size()]
		render map as JSON
	}
	
	/**
	* Gets the html of a question to display to the audience for them to answer.
	* No danger in not locking this down
	*/
	def get_question_by_id() {
		def presentation = Presentation.get(params.id)
		def question = QuestionOccurrence.get(params.quesid)
		def userid = springSecurityService?.currentUser?.id
		def responses = [:]
		responses[question.id] = null
		if (userid!=null) {
			responses[question.id] = question.responses.find { it.userId == userid }
		}
		render(template: "questionView", model:[questionOccurrence: question, presentationInstance: presentation, responses: responses])
	}
	/**
	* Gets a list of current question ids for presentation of given id.
	* and if user questions is allowed
	* For audience view to update when questions change.
	* No danger in not locking this down.
	*/
	def update_audience_view() {
		def presentation = Presentation.get(params.id)
		def ids = []
		presentation.questions.each{ quesOcc ->
			if (quesOcc.active) {
				ids << quesOcc.id
			}
		}
		ids.sort()
		def map = ['ids': ids, 'allowUserQuestions': presentation?.allowUserQuestions]
		
		render map as JSON
	}
	
	def get_audience_questions_json() {
		def presentation = Presentation.get(params.id)
		if (!presentation) {
			flash.message = message(code: 'default.not.presentation.message')
			return redirect(controller: "room", action: "list")
		}
		render presentation?.audienceQuestions as JSON
	}
	
	def get_audience_question_version() {
		def presentation = Presentation.get(params.id)
		if (!presentation) {
			flash.message = message(code: 'default.not.presentation.message')
			return redirect(controller: "room", action: "list")
		}
		render presentation?.audienceQuestionVersion
	}
	
	def presentation_open_version() {
		def presentation = Presentation.get(params.id)
		if (!presentation) {
			flash.message = message(code: 'default.not.presentation.message')
			return redirect(controller: "room", action: "list")
		}
		render presentation?.open		
	}

	def remove_question() {
		/* This method sometimes removes, sometimes not. I have no idea why. */
		def presentationInstance = Presentation.get(params.presid)
		def qo = QuestionOccurrence.get(params.id)
		if (presentationInstance != null && qo) {
			def access = presentationInstance?.room.userAccess(springSecurityService.getCurrentUser()?.id)
			if (access == AccessLevel.Admin || access == AccessLevel.Presenter) {
				Presentation.withTransaction { status ->
					presentationInstance.questions.remove(qo)
				}
				presentationInstance.save(flush: true)
				if (!presentationInstance.questions.contains(qo)) {
					flash.message = "Question removed"
					return makeJSONResponse(true)
				}
			}
		}
		flash.message = "Failed to remove question"
		makeJSONResponse(false)
	}
	
	def update_presentation_list() {
		def room = Room.get(params.id)
		def ids = []
		room?.presentations?.each{ pres ->
			if (pres.active) {
				ids << pres.id
			}
		}
		ids.sort()
		def map = ['ids': ids]
		render map as JSON
	}
	
	/**
	* Gets the html of a listed presentation to display to the audience member
	*/
	def get_listed_presentation() {
		def presentation = Presentation.get(params.presid)
		def room = Room.get(params.id)
		def access = presentation?.room.userAccess(springSecurityService?.currentUser?.id)
		if (access == AccessLevel.AuthenticatedAudience || access == AccessLevel.UnauthenticatedAudience) {
			render(template: "/room/AMpresentationItem", model:[p: presentation, roomId: room.id])
			return
		}
	}
	protected class MyContextMapperCourses extends AbstractContextMapper {
		protected Object doMapFromContext(DirContextOperations context) {
			return context.getStringAttributes("uccourse")
		}
	}
	
}
