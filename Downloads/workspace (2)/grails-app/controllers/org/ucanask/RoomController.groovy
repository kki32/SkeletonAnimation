package org.ucanask

import org.springframework.dao.DataIntegrityViolationException
import grails.converters.JSON
import grails.plugins.springsecurity.Secured


class RoomController  {

	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	def springSecurityService
	def ldapSearch
	def userService

	def index() {
		redirect(action: "list", params: params)
	}

	def goToPresentation() {
		def p = Presentation.find {
			accessKey == params["presentationCode"]
		}
		if(!p) {
			flash.message = "Presentation is not found or not available to guests, try logging in."
			redirect(action: "list")
		}
		else if (p.open) {
			params["id"] = params["presentationCode"]
			redirect(controller: "presentation", action: "show", id: p.id, params: params)
		} else {		
			flash.message = "Presentation is not available to guests, please login."
			redirect(action: "list")
		}
	}

	/**
	 * Lockdown the list to only display the relevant rooms to the user
	 * Admins Can see the rooms they control, invited too and are open. They are also given a list of ALL rooms.
	 * @return all of the necessary rooms already sorted into the correct collections
	 */
	def list() {
		if (!loggedIn) {
			if (flash.message) {
				flash.message = flash.message
			}
			else {
				flash.message = "Please login"
			}
			redirect(controller: "home")
		}
		params.max = Math.min(params.max ? params.int('max') : 10, 100)

		def myRooms = [], invitedRooms = [], openRooms = [], allRooms = []
		if(loggedIn && principal.authorities*.authority.contains("ROLE_ADMIN")) {
			allRooms = Room.list()
		}
		def user = springSecurityService.currentUser
		Room.list().each {room ->
			switch(room.userAccess(user?.id)) {
				case AccessLevel.Presenter: myRooms << room; break;
				case AccessLevel.AuthenticatedAudience: invitedRooms << room; break;
				//				case AccessLevel.UnauthenticatedAudience: openRooms << room; break;
			}
		}
		if (!loggedIn) {
			myRooms = null
			invitedRooms = null
		}
		else if (!principal.authorities*.authority.contains("ROLE_PR")) {
			myRooms = null
		}
		[isAdmin: loggedIn && principal?.authorities*.authority?.contains("ROLE_ADMIN"), allRooms: allRooms, invitedRooms: invitedRooms, myRooms: myRooms]
	}

	/**
	 * Only administrators can create new rooms. Everyone else is locked out.
	 */
	@Secured(['ROLE_ADMIN'])
	def create() {
		[roomInstance: new Room()]
	}

	
	@Secured(['ROLE_ADMIN'])
	def save() {
		def roomInstance = new Room(params)
		def map
		if (!roomInstance.save(flush: true)) {
			flash.message = message(code: 'default.button.save.error',
						args: [message(code: 'room.label', default: 'Room')])
			redirect(action: "create")
		} else {
			redirect(action: "edit", id: roomInstance.id)
		}
		
		
	}
	

	@Secured(["ROLE_PR"])
	def invite_list() {
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
        def roomInstance = Room.get(params.id)
        if (!roomInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'room.label', default: 'Room'), params.id])
            redirect(action: "list")
            return
        }
		switch(roomInstance.userAccess(springSecurityService.currentUser?.id)) {
			case AccessLevel.Admin:
			case AccessLevel.Presenter:
				render template: "inviteList", model: [userInstanceList: User.list(params), roomInstance: roomInstance, offset: params.offset]
				return
			default:
				flash.message = message(code: 'default.user.not.authorized')
				redirect(action: "show", id: roomInstance.id)
		}
	}
	
	/** Add a new owner / presenter to a room **/
	@Secured(['ROLE_PR'])
	def add_owner() {
        def roomInstance = Room.get(params.id)
        if (!roomInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'room.label', default: 'Room'), params.id])
            redirect(action: "list")
            return
        }
		switch(roomInstance.userAccess(springSecurityService.currentUser?.id)) {
			case AccessLevel.Admin:
			case AccessLevel.Presenter:
				def user = User.find {
					username == params.user
				}
				def pr = Role.findByAuthority('ROLE_PR')
				if(user) {
					def isPr = user.getAuthorities().contains(pr)
					def map;
					if(!roomInstance.owners.contains(user.id)) {
						if(isPr) {
							roomInstance.addToOwners(user.id)
						}
						map = [user:user, success:true, allowed: isPr]
					} else {					
						map = [success:true]
					}
					render map as JSON
					return
				} 
			default:
				return makeJSONResponse(false)
		}
	}
	
	/**
	 * Remove a presenter from this room
	 * @return
	 */
	@Secured(['ROLE_PR'])
	def remove_owner() {
        def roomInstance = Room.get(params.id)
        if (!roomInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'room.label', default: 'Room'), params.id])
            redirect(action: "list")
            return
        }
		switch(roomInstance.userAccess(springSecurityService.currentUser?.id)) {
			case AccessLevel.Admin:
			case AccessLevel.Presenter:
				def map
				if(roomInstance.owners.contains(Long.parseLong(params.userId))) {
					roomInstance.removeFromOwners(Long.parseLong(params.userId))
					render map = [success:true] as JSON
					return
				} else {
					render map = [success:false] as JSON
				}
			default:
				return makeJSONResponse(false)
		}
		
	}
	
	/** 
	 * Add a new course to a room
	**/
	@Secured(['ROLE_PR'])
	def add_course() {
        def roomInstance = Room.get(params.id)
        if (!roomInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'room.label', default: 'Room'), params.id])
            redirect(action: "list")
            return
        }
		def map
		switch(roomInstance.userAccess(springSecurityService.currentUser?.id)) {
			case AccessLevel.Admin:
			case AccessLevel.Presenter:
				// test whether a course exists, if so set exists.
				def validCourse = true
				def add = !roomInstance.courses.contains(params.course)
				if(validCourse && add) {
					roomInstance.addToCourses(params.course)
				}
				// Get students from this course and add them
				//TODO: Wrap this in a try/catch in case things go wrong
				def audienceMembers = ldapSearch.lookup('uccourse', params.course + '*', 'uid')
				// Wipe the existing invited members and add the automatically found ones
				//roomInstance.automaticEnrolled.clear()
				roomInstance.automaticEnrolled.addAll(userService.addUsers(audienceMembers))
				roomInstance.automaticUpdated++
				// println(roomInstance.invited)
				render map = [success:validCourse, added:add, updated: roomInstance.automaticUpdated] as JSON	
				return
		}		
		render map = [success:false, added:false] as JSON
	}
	
	/**
	 * Remove a courses from this room
	 * @return
	 */
	@Secured(['ROLE_PR'])
	def remove_course() {
        def roomInstance = Room.get(params.id)
        if (!roomInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'room.label', default: 'Room'), params.id])
            redirect(action: "list")
            return
        }
		def map
		switch(roomInstance.userAccess(springSecurityService.currentUser?.id)) {
			case AccessLevel.Admin:
			case AccessLevel.Presenter:
				if(roomInstance.courses.contains(params.course)) {
					roomInstance.removeFromCourses(params.course)
					userService.rebuildEnrollments(roomInstance)
					render map = [success:true] as JSON
					return
				}
		}
		render map = [success:false] as JSON		
	}
	
	
	@Secured(['ROLE_PR'])
	def students_manage() {
        def roomInstance = Room.get(params.id)
        if (!roomInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'room.label', default: 'Room'), params.id])
            redirect(action: "list")
            return
        }	
	}
	
	/**
	 * Action to import a presentation
	 * @return
	 */
	@Secured(['ROLE_PR'])
	def add_presentation() {
        def roomInstance = Room.get(params.id)
        if (!roomInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'room.label', default: 'Room'), params.id])
            redirect(action: "list")
            return
        }
		def map
		switch(roomInstance.userAccess(springSecurityService.currentUser?.id)) {
			case AccessLevel.Admin:
			case AccessLevel.Presenter:
				println 'here'
				def oldP = Presentation.get(params.presentationId)
				if (oldP) { 
					def newP = new Presentation(name: oldP.name, owner: springSecurityService.currentUser)
					oldP.questions.each { occurrence ->
						newP.addToQuestions(new QuestionOccurrence(askedQuestion: occurrence.askedQuestion))
					}
					newP.room = roomInstance
					newP.save()
					if(newP) {						
						render map = [success:true] as JSON
						return
					} else {
						render map = [success:false] as JSON
					}
				}
		}
		render map = [success:false] as JSON
		
	}

	/**
	 * Determines what the current user can view and if the room exists shows it
	 * @return
	 */
	def show() {
		def roomInstance = Room.get(params.id)
		if (!roomInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'room.label', default: 'Room'), params.id])
			redirect(action: "list")
			return
		}

		def view = roomInstance.userAccess(springSecurityService.currentUser?.id)
		switch(view) {
			// Use the show only if the user is a presenter or admin (will have the same view)
			case AccessLevel.Admin:
				case AccessLevel.Presenter: return [roomInstance: roomInstance, view: AccessLevel.Presenter.toString()]
			// Audience members will be redirected to the active presentations
			case AccessLevel.AuthenticatedAudience:
			case AccessLevel.UnauthenticatedAudience:
			// There is one or more active presentations. Lets show the audience
				def activePresentations = roomInstance.presentations?.findAll() {
					it.active
				}
				if(activePresentations) {
					return [roomInstance: roomInstance,
						view: AccessLevel.AuthenticatedAudience.toString(),
						activePresentations: activePresentations]
				} else {
					// No active presentations, redirect to the list of rooms
					flash.message = message(code: 'default.inactive.presentation', args: [roomInstance.name])
					redirect(action: "list")
					return
				}
			// If the user has no access to this room they will be redirected to the list of all their rooms
			case AccessLevel.None:
			default:
				flash.message = message(code: 'default.user.not.authorized')
				redirect(action: "list")
				return
		}
	}

	/**
	 * Get all of the active presentations within a room
	 * @return
	 */
	public def activePresetationsAsJSON() {
		def roomInstance = Room.get(params.id)
		def activePresentations = roomInstance.presentations?.findAll(sort:"name") {
			it.active
		}
		return activePresentations as JSON
	}

	/**
	 * Need to be an admin or PR to not be redirected to the login page or told you don't have access
	 * If you are a PR but not to this Room you will be redirected to the list
	 * People will access will be shown the edit page
	 * @return
	 */
	@Secured(["ROLE_ADMIN", "ROLE_PR"])
	def edit() {
		def roomInstance = Room.get(params.id)
		if (!roomInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'room.label', default: 'Room'), params.id])
			redirect(action: "list")
			return
		}
		// Will test if they can edit THIS room
		def access = roomInstance.userAccess(springSecurityService.currentUser?.id)
		switch(access) {
			case AccessLevel.Admin:
			case AccessLevel.Presenter:
				def owners = []
				def allCourses = ldapSearch.allCourses()
				roomInstance.owners.each { userId ->
					owners.add(User.get(userId))
				}
				params.sort = "name"
				def presentations = Presentation.findAll(params) {
					owner == springSecurityService.currentUser
				}
				def coursesJson = allCourses as JSON
				def automatic = userService.getUsercodes(roomInstance.automaticEnrolled)
				def invited = userService.getUsercodes(roomInstance.invited)
				def blocked = userService.getUsercodes(roomInstance.blocked)
				//[roomInstance: roomInstance, automatic: automatic, invited: invited, blocked: blocked]	
				return [
					roomInstance: roomInstance, 
					owners: owners, 
					myPresentations: presentations,
					coursesJson: coursesJson,
					automatic: automatic,
					invited: invited,
					blockedList: blocked]
			default:
				flash.message = message(code: 'default.user.not.authorized')
				redirect(action: "list")
				return

		}
	}
	
	@Secured(['ROLE_PR'])
	def toggleAutoUser() {
		def roomInstance = Room.get(params.id)
		if (!roomInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'room.label', default: 'Room'), params.id])
			redirect(action: "list")
			return
		}
		def map
		def access = roomInstance.userAccess(springSecurityService.currentUser?.id)
		switch(access) {
			case AccessLevel.Admin:
			case AccessLevel.Presenter:
				def user = User.findByUsername(params.user)
				def blocked = false
				if(roomInstance.blocked.contains(user.id)) {
					roomInstance.removeFromBlocked(user.id)
				} else {
					roomInstance.addToBlocked(user.id)	
					blocked = true			
				}
				render map = [success:true, 
							  blocked:blocked, 
							  enrolledCount: roomInstance.automaticEnrolled.size(),
							  blockedCount: roomInstance.blocked.size()] as JSON
				return
		}		
		render map = [success:false] as JSON
	}
	
	@Secured(['ROLE_PR'])
	def add_invited() {
		def roomInstance = Room.get(params.id)
		if (!roomInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'room.label', default: 'Room'), params.id])
			redirect(action: "list")
			return
		}
		def map
		def access = roomInstance.userAccess(springSecurityService.currentUser?.id)
		switch(access) {
			case AccessLevel.Admin:
			case AccessLevel.Presenter:
				def user = User.findByUsername(params.user)
				// If the user doesn't exist locally, but they are in the LDAP system, add them
				if (!user && ldapSearch.lookup("uid", params.user, "uid").contains(params.user)) {
					userService.addUser(params.user, "")
					user = User.findByUsername(params.user)
				}
				// Now check if they are in the local system
				if(user && !roomInstance.invited.contains(user.id)) {
					roomInstance.addToInvited(user.id)	
					render map = [success:true, total: roomInstance.invited.size()] as JSON
					return
				} else {
					// TODO: Currently the same message if user already in list, or user doesn't exist
					render map = [success:false] as JSON
					return
				}
				
		}
		render map = [success:false] as JSON		
	}
	
	
	@Secured(['ROLE_PR'])
	def remove_invited() {
		def roomInstance = Room.get(params.id)
		if (!roomInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'room.label', default: 'Room'), params.id])
			redirect(action: "list")
			return
		}
		def map
		def access = roomInstance.userAccess(springSecurityService.currentUser?.id)
		switch(access) {
			case AccessLevel.Admin:
			case AccessLevel.Presenter:
				def user = User.findByUsername(params.user)
				if(user && roomInstance.invited.contains(user.id)) {
					println 'remove'
					roomInstance.removeFromInvited(user.id)	
					render map = [success:true, total: roomInstance.invited.size()] as JSON
					return
				} else {
					println 'not'
					render map = [success:false] as JSON
					return
				}
				
		}
		render map = [success:false] as JSON		
	}
	
	
	
	@Secured(['ROLE_PR'])
	def update_name() {
		def roomInstance = Room.get(params.id)
		if (!roomInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'room.label', default: 'Room'), params.id])
			redirect(action: "list")
			return
		}
		def map
		def access = roomInstance.userAccess(springSecurityService.currentUser?.id)
		switch(access) {
			case AccessLevel.Admin:
			case AccessLevel.Presenter:
				roomInstance.name = params.name
				render map = [success:true] as JSON
				return
		}		
		render map = [success:false] as JSON
	}

	/**
	 * Only admins can delete the room. Automatically secure.
	 * @return
	 */
	@Secured(["ROLE_ADMIN"])
	def delete() {
		def roomInstance = Room.get(params.id)
		if (!roomInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'room.label', default: 'Room'), params.id])
			redirect(action: "list")
			return
		}

		try {
			roomInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'room.label', default: 'Room'), params.id])
			redirect(action: "list")
		} catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'room.label', default: 'Room'), params.id])
			redirect(action: "show", id: params.id)
		}
	}
	
	/**
	 * Get whether the student list has been updated.
	 * @return
	 */
	def auto_student_list_updated() {
		def roomInstance = Room.get(params.id)
		def map = [autoStudent: roomInstance.automaticUpdated]
		render map as JSON
	}
	
	/**
	 * Get the student list for a room
	 * @return
	 */
	def get_auto_student_list() {
		def roomInstance = Room.get(params.id)
		def automatic = userService.getUsercodes(roomInstance.automaticEnrolled)
		def blocked = userService.getUsercodes(roomInstance.blocked)
		def template = g.render(template: "automatic_enrolled", model: [automatic: automatic, blockedList: blocked])
		def map = [template: template, enrolledCount: roomInstance.automaticEnrolled.size(),
							  blockedCount: roomInstance.blocked.size()]
		render map as JSON
	}
	
	
	/**
	* Renders a JSON response, with a message and a colour for the message.
	* @param success true will display in green, false red.
	* @return A JSON response message.
	*/
	private def makeJSONResponse(boolean success) {
		def map = ["message": flash.message]
		map['success'] = success
		map['colour'] = success ? "green" : "red" 
		render map as JSON
	}
}
