package org.ucanask

import grails.test.mixin.*
import org.junit.*
import org.ucanask.AnswerStrategy.*
import org.ucanask.Responses.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} 
 * for usage instructions
 */
@TestFor(QuestionOccurrence)
@Mock([Question, Room, Presentation, User, Role, UserRole, TextResource, MultiChoiceStrategy, MultiChoiceResponse])
class QuestionOccurrenceTests {

	
	Room r
	Presentation p
	User admin
	User am
	
	void setUp() {
		admin = new User(username: "usr12345", password: "password", displayName:"presenter1", email: "ab2@xyq.com").save(failOnError:true)
		assert admin != null
		def adminRole = Role.findByAuthority('ROLE_ADMIN') ?:new Role(authority: 'ROLE_ADMIN').save(failOnError: true)
		UserRole.create admin, adminRole
		
		am = new User(username: "am123", password: "password", displayName:"am1", email: "ab1@xyq.com").save(failOnError:true)
		assert am != null
		def amRole = Role.findByAuthority('ROLE_AM') ?:new Role(authority: 'ROLE_AM').save(failOnError: true)
		UserRole.create am, amRole
		
		r = new Room(name: "roomz", owners: [admin], invited:[], open:true).save(failOnError:true)
		assert r != null
		
		p = new Presentation(name: "Presentation1", active: true, room: r, owner: admin).save(failOnError:true)
		assert p.save(failOnError:true) != null
		
//		SpringSecurityService.metaClass.getCurrentUser { -> return admin}
//		ResponseService.metaClass.addResponse { -> return true}
	}
	
	
	
	
	
	
	
	/**
	 *
	 */
	void testConstraints() {
		mockForConstraintsTests(QuestionOccurrence)
		def qo = new QuestionOccurrence()
		assert !qo.validate()
		assert "nullable" == qo.errors["askedQuestion"]
		
		qo.askedQuestion = new Question()		
		assert !qo.validate()		
		assert "nullable" == qo.errors["presentation"]
		
		qo.presentation = p
		assert qo.validate()
	}

	/**
	 *
	 */
	void testAddResponse() {
		def choice1 = new TextResource(text:"Yes")
		assert choice1.save(failOnError:true) != null
		def choice2 = new TextResource(text:"No")
		assert choice2.save(failOnError:true) != null
		def ans = new MultiChoiceStrategy(multiselect: true, choices: [choice1, choice2])
		assert ans.save(failOnError:true) != null
		def question = new Question(resources: new TextResource(text:"What?"), answerStrategy: ans, room: r, owner: admin)
		assert question.save(failOnError:true) != null
		// Adding null
		def qo = new QuestionOccurrence(askedQuestion: question, active: true)
		qo.addResponse(null)
		assert qo.responses == null
		// A dict but no "choice"
		qo.addResponse([notChoice:1])
		assert qo.responses == null
		// Valid
		qo.addResponse([userid: 1, choice:[
				choice1.id.toString(),
				choice2.id.toString()
			]])
		assert qo.responses.size() == 1
		assert qo.responses.first().choices[0].id == choice1.id
		assert qo.responses[0].choices.size() == 2
		// Replace response from same user
		qo.addResponse([userid: 1, choice:[
				choice1.id.toString()
			]])
		assert qo.responses.size() == 1
		assert qo.responses[0].choices.size() == 1
		// Non-active
		def qo2 = new QuestionOccurrence(askedQuestion: question)
		qo2.addResponse([userid: 2, choice:[
			choice1.id.toString()
		]])
		assert qo2.responses == null
	}
}
