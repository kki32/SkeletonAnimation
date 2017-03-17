package org.ucanask

import grails.plugins.springsecurity.SpringSecurityService;
import org.ucanask.User

class LoginTagLib {

	static namespace = "ucanask"
	def springSecurityService

	def userDisplayName = { attrs ->
		def name = (springSecurityService.principal != null) ?
				(User.get(springSecurityService.principal.id).displayName) : "User"
		out << name
	}
	
	def getUsernames = { attrs ->
		User.list() // Ensure its all ready to use from the database
		Room roomInstance = attrs?.room
		String usernameList = ""
		// Create a valid list of student id's
		Set adjustedList = [] as Set
		adjustedList.addAll(roomInstance.automaticEnrolled)
		adjustedList.addAll(roomInstance.invited)
		adjustedList.removeAll(roomInstance.blocked)
		adjustedList.each {
			usernameList += User?.get(it).toString()
			usernameList += ", "
		}
		def lastIndex = usernameList.lastIndexOf(",")
		usernameList = (lastIndex == -1)? "" : usernameList.substring(0, lastIndex)
		out << usernameList
	}
	
	def getAutomatic = { attrs ->
		User.list() // Ensure its all ready to use from the database
		Room roomInstance = attrs?.room
		String usernameList = ""
		// Create a valid list of student id's
		Set adjustedList = [] as Set
		adjustedList.addAll(roomInstance.automaticEnrolled)
		adjustedList.each {
			usernameList += User?.get(it).toString()
			usernameList += ", "
		}
		def lastIndex = usernameList.lastIndexOf(",")
		usernameList = (lastIndex == -1)? "" : usernameList.substring(0, lastIndex)
		out << usernameList
		
	}
}