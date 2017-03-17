package org.ucanask

import grails.test.mixin.*
import org.junit.*
import org.ucanask.AnswerStrategy.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} 
 * for usage instructions
 */
@TestFor(Question)
@Mock([MultiChoiceStrategy, QuestionOccurrence, Room, Presentation, Role, User, UserRole])
class QuestionTests {
	
	Room r
	Presentation p
	User admin
	User am
	
	void setUp() {
		mockCodec(org.codehaus.groovy.grails.plugins.codecs.HTMLCodec)		
	
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
		mockForConstraintsTests(Question)
		def q = new Question()
		assert !q.validate()
		assert "nullable" == q.errors["resources"]
		assert "nullable" == q.errors["answerStrategy"]
		def ans = new MultiChoiceStrategy(choices: [
			new TextResource(text:"Yes"),
			new TextResource(text:"No")
		])
		q = new Question(resources: [], answerStrategy: ans)
		assert !q.validate()
		assert "minSize" == q.errors["resources"]
		def presentation = new Presentation(currentQuestions: q)
		def txt = new TextResource(text:"Does it pass?")
		q = new Question(resources: txt, answerStrategy: ans, owner: admin, room: r)
		assert q.validate()
	}

	/**
	 *
	 */
	void testToString() {
		def q = new Question()
		assert null == q.toString()
		q = new Question(resources: [])
		assert null == q.toString()
		def aString = "Does it pass?"
		def txt = new TextResource(text:aString)
		q = new Question(resources: txt)
		assert aString == q.toString()
		def txt2 = new TextResource(text:"Another string")
		q = new Question(resources: [txt, txt2])
		assert aString == q.toString()
	}
}
