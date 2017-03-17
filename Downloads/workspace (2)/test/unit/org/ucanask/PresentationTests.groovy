package org.ucanask


import grails.test.mixin.*

import org.junit.*
import org.ucanask.AnswerStrategy.MultiChoiceStrategy

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} 
 * for usage instructions
 */
@TestFor(Presentation)
@Mock([User, UserRole, Role, Room, Question, TextResource, MultiChoiceStrategy, QuestionOccurrence])
class PresentationTests {
	
	User admin
	User am
	Room r
	
	void setUp() {
		
//		java.lang.String.metaClass.encodeAsHTML = {
//			return grails.doc.internal.StringEscapeCategory.encodeAsHtml(delegate)
//		}
		
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

//		SpringSecurityService.metaClass.getCurrentUser { -> return admin}
//		ResponseService.metaClass.addResponse { -> return true}
	}

	/**
	 *
	 */
	void testConstraints() {
		mockForConstraintsTests(Presentation)
		def presentation = new Presentation()
		assert !presentation.validate()
		assert "nullable" == presentation.errors["name"]
		assert null ==  presentation.questions
		
		TextResource t1 = new TextResource(text: "Yes")
		assert t1.save(failOnError:true)
		TextResource t2 = new TextResource(text: "No")
		assert t2.save(failOnError:true)
		TextResource t3 = new TextResource(text: "Does it pass?")
		assert t3.save(failOnError:true)
		
		def ans = new MultiChoiceStrategy(choices: [
			t1,
			t2
		], multiselect: false)
		assert ans.save(failOnError:true) != null	
		def presentation2 = new Presentation(name: "Presentation2", active: false)
		presentation2.room = r
		presentation2.owner = admin
		assert presentation2.save(failOnError:true) != null
				
		def q = new QuestionOccurrence(askedQuestion: new Question(resources: t3,	answerStrategy: ans))
		q.presentation = presentation2
		assert q.save(failOnError:true) != null
		assert presentation2.validate()
		assert presentation2.questions.size() == 1

	}

	/**
	 *
	 */
	void testEndPresentation() {
		def presentation = new Presentation(name: "Presentation")
		presentation.active = true
		presentation.endPresentation()
		assert presentation.active == false
		def ans = new MultiChoiceStrategy(choices: [
			new TextResource(text:"Yes"),
			new TextResource(text:"No")
		], multiselect: false)
		def txt = new TextResource(text:"Does it pass?")
		def q = new Question(resources: txt, answerStrategy: ans)
		presentation.addToQuestions(new QuestionOccurrence(askedQuestion: q, active: true))

		def ans2 = new MultiChoiceStrategy(choices: [
			new TextResource(text:"True"),
			new TextResource(text:"False")
		], multiselect: false)
		def txt2 = new TextResource(text:"I can add more than one question?")
		def q2 = new Question(resources: txt2, answerStrategy: ans2)
		presentation.addToQuestions(new QuestionOccurrence(askedQuestion:q2, active: true))
		presentation.active = true
		presentation.endPresentation()
		assert presentation.active == false
		assert presentation.questions.size() == 2
		assert presentation.questions[0].active == false
		assert presentation.questions[1].active == false
	}

	/**
	 *
	 */
	void testToString() {
		mockCodec(org.codehaus.groovy.grails.plugins.codecs.HTMLCodec)
		def presentation = new Presentation(name: "Presentation")
		assert presentation.toString().equals("Presentation")
	}

	/**
	 *
	 */
	void testQuestionNotUsed() {
		def presentation = new Presentation(name: "Presentation")
		def ans = new MultiChoiceStrategy(choices: [
			new TextResource(text:"Yes"),
			new TextResource(text:"No")
		], multiselect: false)
		def txt = new TextResource(text:"Does it pass?")
		def q = new Question(resources: txt, answerStrategy: ans)
		assert presentation.questionNotUsed(q)
		presentation.addToQuestions(
				new QuestionOccurrence(askedQuestion: q))
		assert presentation.questionNotUsed(q) == false
	}
}
