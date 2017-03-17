package org.ucanask

//import org.springframework.dao.DataIntegrityViolationException

import grails.converters.JSON
import grails.plugins.springsecurity.Secured

class IgnoreListController {

	def springSecurityService
	
    static allowedMethods = [update: "POST"]


	@Secured(["ROLE_PR"])
    def edit() {
		def presId = springSecurityService.getCurrentUser().id
		User pres = User.get(presId)
		
        def ignoreListInstance = IgnoreList.findByOwnerAndGlobal(pres, false)
        if (!ignoreListInstance) {
			ignoreListInstance = new IgnoreList(owner: pres, global: false, ignoredWords: [])
			ignoreListInstance.save(flush: true)
			if (!ignoreListInstance){
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'ignoreList.label', default: 'IgnoreList'), params.id])
	            redirect(controller: "room", action: "list")
	            return
			}
			
        }
		
		//Database is considered 'finalised' right now... forgive me.
		if(ignoreListInstance)
			ignoreListInstance.metaClass.title = { "My List" }
		
		def adminListInstance
		if(pres.getAuthorities().contains(Role.findByAuthority("ROLE_ADMIN"))){
			adminListInstance = IgnoreList.findByGlobal(true)
			if(adminListInstance)
				adminListInstance.metaClass.title = { "Global List" }
		}
		
		
        [ignoreListInstance: ignoreListInstance, adminListInstance: adminListInstance]
    }

//	@Secured(["ROLE_PR"])
//    def update() {
//        def ignoreListInstance = IgnoreList.get(params.id)
//        if (!ignoreListInstance) {
//			
//            flash.message = message(code: 'default.not.found.message', args: [message(code: 'ignoreList.label', default: 'IgnoreList'), params.id])
//            redirect(controller: "room", action: "list")
//            return
//        }
//
//        if (params.version) {
//            def version = params.version.toLong()
//            if (ignoreListInstance.version > version) {
//                ignoreListInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
//                          [message(code: 'ignoreList.label', default: 'IgnoreList')] as Object[],
//                          "Another user has updated this IgnoreList while you were editing")
//                render(view: "edit", model: [ignoreListInstance: ignoreListInstance])
//                return
//            }
//        }
//
//        ignoreListInstance.properties = params
//
//        if (!ignoreListInstance.save(flush: true)) {
//            render(view: "edit", model: [ignoreListInstance: ignoreListInstance])
//            return
//        }
//
//		flash.message = message(code: 'default.updated.message', args: [message(code: 'ignoreList.label', default: 'IgnoreList'), ignoreListInstance.id])
//        redirect(controller: "room", action: "list")
//    }

	@Secured(["ROLE_PR"])
	def addWord() {
		def ignoreListInstance = IgnoreList.get(params.id)
        if (!ignoreListInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'ignoreList.label', default: 'IgnoreList'), params.id])
            redirect(controller: "room", action: "list")
            return
        }
		
		ignoreListInstance.addToIgnoredWords(params.word)
		ignoreListInstance.save(flush:true)
		
		def ajaxResponse = [success:false]
		
		if(ignoreListInstance.ignoredWords.contains(params.word)) {
			ajaxResponse = [success:true]
		}
		
		render ajaxResponse as JSON
		
	}
	
	@Secured(["ROLE_PR"])
	def removeWord() {
		
		def ignoreListInstance = IgnoreList.get(params.id)
		
		if (!ignoreListInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'ignoreList.label', default: 'IgnoreList'), params.id])
			redirect(controller: "room", action: "list")
			return
		}
		
		ignoreListInstance.removeFromIgnoredWords(params.word)
		
		
		def ajaxResponse = [success:true]
		
		if(ignoreListInstance?.ignoredWords.contains(params.word)){
			ajaxResponse = [success:false]
		}
		
		render ajaxResponse as JSON
	}
    
}
