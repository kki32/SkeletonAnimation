package org.ucanask

class Room {

	String name
	SortedSet<Presentation> presentations
	Set owners
	Set invited = [] // Lecturers can specifically add users to the course, this should not over-ride blocked.
	Set blocked // Lecturers can add students to this list to exclude them, even if auto added
	Set automaticEnrolled // Students added by linking UC courses to UCanAsk Courses
	Set course // course will be used to automatically get users from LDAP
	
	int automaticUpdated = 0 // Count of when the automatic count has been updated

	static hasMany = [presentations: Presentation,
					  owners: Long,  
					  blocked: Long, 
					  invited: Long,
					  automaticEnrolled: Long,
					  courses: String ]	

	static fetchMode = [owners: "eager"]

	static mapping = {
		owners lazy: false
	}

	static constraints = {
		name nullable: false, blank: false, unique: true
		presentations nullable: true
		owners nullable: true // nullable: false is default, adding it explicitly creates an error for some dumb reason
		invited nullable: true
		automaticEnrolled nullable: true
		course nullable:true
	}

	boolean isInvited(user) {
		return !invited.contains(user)
	}

	boolean addOwner(name) {
		def user  = User.find {username == name }
		if(user) {
			println 'here'
			addToOwners(user.id)
			return true
		} else {
			return false
		}
	}

	/**
	 * Get the users access level for this room
	 * User will be null if not logged in, test whether the presentation is open first.
	 * @param user the user to check for
	 * @return the access level for this room for this specific user
	 */
	AccessLevel userAccess(Long userId) {
		if(userId == null) {
			return AccessLevel.None
		} else if(User.get(userId).getAuthorities().contains(Role.findByAuthority("ROLE_ADMIN"))) {
			return AccessLevel.Admin
		} else if(owners.contains(userId)) {
			return AccessLevel.Presenter
		} else if(automaticEnrolled.contains(userId) && !blocked.contains(userId)) {
			return AccessLevel.AuthenticatedAudience		
		} else if(invited.contains(userId)) {
			return AccessLevel.AuthenticatedAudience
		} 
		AccessLevel.None
	}

	/**
	 * To deal with open presentations
	 * @param userId
	 * @param presentation
	 * @return
	 */
	AccessLevel userAccess(Long userId, Presentation presentation) {
		def access = userAccess(userId)
		if(access == AccessLevel.None) {
			return presentation.open ?  AccessLevel.UnauthenticatedAudience : access
		}
		access
	}

	def openPresentationCount() {
		return presentations.findAll {
			it.open == true
		}.size()
	}

	String toString() {
		return name
	}
}

