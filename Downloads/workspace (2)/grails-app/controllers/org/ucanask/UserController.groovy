package org.ucanask

import org.springframework.dao.DataIntegrityViolationException
import grails.plugins.springsecurity.Secured

/**
 * User class - only available to Admin users
 */
@Secured (['ROLE_ADMIN'])
class UserController {

	def springSecurityService
	def userDetailsService
	def ldapSearchService

	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	/**
	 *
	 */
	def index() {
		redirect(action: "list", params: params)
	}

	//@Secured(['ROLE_ADMIN', 'ROLE_PR'])
	/**
	 *
	 */
	def list() {
		params.max = Math.min(params.max ? params.int('max') : 15, 100)
		[userInstanceList: User.list(params), userInstanceTotal: User.count()]
	}

	
	/**
	 *
	 */
	def create() {
		[userInstance: new User(params)]
	}

	/**
	 *
	 */
	def save() {
		def userInstance = new User(params)
		if (!userInstance.save(flush: true)) {
			render(view: "create", model: [userInstance: userInstance])
			return
		}
		// Create the AM role if it doesn't exist, and add the new user to it
		def amRole = Role.findByAuthority('ROLE_AM') ?:	new Role(authority: 'ROLE_AM').save(failOnError: true)
		UserRole.create userInstance, amRole
		
		flash.message = message(code: 'default.created.message',
				args: [message(code: 'user.label', default: 'User'), userInstance.id])
		redirect(action: "show", id: userInstance.id)
	}

	/**
	 *
	 */
	def show() {
		def userInstance = User.get(params.id)
		if (!userInstance) {
			flash.message = message(code: 'default.not.found.message',
					args: [message(code: 'user.label', default: 'User'), params.id])
			redirect(action: "list")
			return
		}
		[userInstance: userInstance]
	}

	/**
	 *
	 */
	@Secured (['ROLE_ADMIN'])
	def edit() {
		def roles = Role.list().authority
		def userInstance = User.get(params.id)
		if (!userInstance) {
			flash.message = message(code: 'default.not.found.message',
					args: [message(code: 'user.label', default: 'User'), params.id])
			redirect(action: "list")
			return
		}
		
		[userInstance: userInstance, roles: roles]
	}
	
	
	
	/**
	 *
	 */
	def update() {
		//println(params)
		
		def userInstance = User.get(params.id)
		if (!userInstance) {
			flash.message = message(code: 'default.not.found.message',
					args: [message(code: 'user.label', default: 'User'), params.id])
			redirect(action: "list")
			return
		}
		
		if (params.version) {
			def version = params.version.toLong()
			if (userInstance.version > version) {
				userInstance.errors.rejectValue("version",
						"default.optimistic.locking.failure", [message(code: 'user.label',
							default: 'User')] as Object[],
						"Another user has updated this User while you were editing")
				render(view: "edit", model: [userInstance: userInstance])
				return
			}
		}
		// Update details?
		if (params["updateDetails"].equals("on")) {
			params.displayName = ldapSearchService.lookup("uid", userInstance.username, "givenName")[0] ?: userInstance.username
			params.email = ldapSearchService.lookup("uid", userInstance.username, "mail")[0] ?: ""
		}
		// Update roles - custom code added by Steve (sjd150)
		// Done hierarchically to ensure no cock-ups
		UserRole.removeAll userInstance
		params.list("Roles").each { role ->
			switch (role) {
				case 'ROLE_ADMIN':  if (!userInstance.authorities.authority.contains(role)) 
					UserRole.create userInstance, Role.findByAuthority("ROLE_ADMIN"), true
				case 'ROLE_PR': if (!userInstance.authorities.authority.contains("ROLE_PR")) 
					UserRole.create userInstance, Role.findByAuthority("ROLE_PR"), true
				default: if (!userInstance.authorities.authority.contains("ROLE_AM")) 
					UserRole.create userInstance, Role.findByAuthority("ROLE_AM"), true
			}
		}
		
		// Warning for no roles selected, ROLE_AM is forced and user taken back to try again
		if (params.list("Roles").size() == 0) {
			flash.message = message(code: 'default.user.minimumAuthority',
				args: [message(code: 'user.label', default: 'User'), params.id])
			UserRole.create userInstance, Role.findByAuthority("ROLE_AM"), true
			def roles = Role.list().authority
			render(view: "edit", model: [userInstance: userInstance, roles: roles])
			return
		}
		
		userInstance.properties = params
		if (!userInstance.save(flush: true)) {
			render(view: "edit", model: [userInstance: userInstance])
			return
		}
		flash.message = message(code: 'default.updated.message',
				args: [message(code: 'user.label', default: 'User'), userInstance.id])
		redirect(action: "show", id: userInstance.id)
	}

	/**
	 *
	 */
	def delete() {
		def userInstance = User.get(params.id)
		if (!userInstance) {
			flash.message = message(code: 'default.not.found.message',
					args: [message(code: 'user.label', default: 'User'), params.id])
			redirect(action: "list")
			return
		}
		try {
			userInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message',
					args: [message(code: 'user.label', default: 'User'), params.id])
			redirect(action: "list")
		} catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message',
					args: [message(code: 'user.label', default: 'User'), params.id])
			redirect(action: "show", id: params.id)
		}
	}
}
