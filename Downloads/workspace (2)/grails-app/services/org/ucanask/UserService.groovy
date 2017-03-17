package org.ucanask


import java.util.LinkedList
import org.apache.commons.lang.RandomStringUtils


/**
 * Things to do with users...
 * 
 * @author sjd150
 *
 */
class UserService {
	
	def ldapSearchService
	def randomPasswordLength = 128
	
	/**
	 * Add a collection of users as local disabled users (When they log in for the first time they
	 * will be enabled)
	 * 
	 * @param users - a collection of usernames
	 * @return a collection of user id's of those that were added
	 */
	def addUsers(LinkedList users) {
		def userIds = [] as Set
		User.withTransaction { status ->
			users.each {
				def user = User.findByUsername(it)
				// User doesn't exist, create and add the id
				if (!user) {
					
					def userId = this.addUser(it, this.getPassword(randomPasswordLength))
					userIds.add(userId)
				}
				// User does exist so just add the id
				else userIds.add(user.id)
			}
		}
		return userIds
	}
	
	/**
	 * Updates the users details from LDAP, if they exist there.
	 * @param username - the uid of the user to be updated
	 * @return true if the users details were updated, false otherwise
	 */
	def updateUserDetailsFromLdap(String username) {
		def user = User.findByUsername(username)
		if (user) {
			user.displayName = (user.displayName = username)?
			ldapSearchService.lookup("uid", username, "givenName")[0] ?: "$username" : user.displayName
			user.email = ldapSearchService.lookup("uid", username, "mail")[0] ?: ""
			user.enabled = true // For users who had an account auto-generated in room/edit
			user.merge(flush: true) // Update the user
			return true
		}
		else {
			return false
		}
	}
	
	/**
	 * Rebuild the enrollment database for a room
	 * @param roomInstance
	 * @return
	 */
	def rebuildEnrollments(roomInstance) {		
		roomInstance.automaticEnrolled.clear()
		def audienceMembers = new LinkedList()
		roomInstance.courses.each {
			audienceMembers.addAll(ldapSearchService.lookup('uccourse', it + '*', 'uid'))
		}
		roomInstance.automaticEnrolled.addAll(addUsers(audienceMembers))
		roomInstance.automaticUpdated++
	}
	
	/**
	 * Build a list of user ids
	 * @param userIds
	 * @return
	 */
	def getUsercodes(Set userIds) {
		SortedSet users = new TreeSet()
		userIds.each {
			users.add(User.get(it).getUsername())
		}
		return users
	}
	
	/**
	 * Creates a new DISABLED user with audience member rights
	 * 
	 * @param uid the username
	 * @param password their password
	 * @return the id of the user
	 */
	protected Long addUser(String uid, String password) {
		def amRole = Role.findByAuthority('ROLE_AM') ?:
				new Role(authority: 'ROLE_AM').save(failOnError: true)
		password = (password == "")? getPassword(randomPasswordLength) : password
				
		def user = new User(
			username: uid,
			password: password,
			displayName: uid, //ldapSearchService.lookup("uid", uid, "givenName")[0],
			email: "", //ldapSearchService.lookup("uid", uid, "mail")[0],
			enabled: false).save(failOnError: true)
		// And make the user an audience member. For higher privileges, see an admin
		UserRole.create user, amRole
		return user.id
	}
	
	// 
	/**
	 * Generate a random alphanumeric string, 'length' long to be used as a temp password 
	 * 
	 * @param password (if null or empty returns new password, otherwise just returns itself
	 * @return the password
	 */
	private String getPassword (int length) {
		return RandomStringUtils.randomAlphanumeric(length)
	}
	
	
}
