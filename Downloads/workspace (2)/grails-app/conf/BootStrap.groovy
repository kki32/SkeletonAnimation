import grails.converters.JSON
import grails.util.GrailsUtil

import org.ucanask.*
import org.ucanask.AnswerStrategy.*
import org.ucanask.chart.Word


class BootStrap {

	def springSecurityService
	def userDetailsService

	def init = { servletContext ->
		
		switch (GrailsUtil.environment) {
			case "development":
			case "test":		
				log.info "Info: In development/test mode"
				setupUsers()		
				setupIgnoredWordList()
				createInvitedRoom()
				registerWordJsonHandler()
				registerFreeTextResponseJsonHandler()
				registerUserJsonHandler()
				break;
			case "production":
				log.info "Info: In production mode"
				setupAdminUser()
				registerWordJsonHandler()
				registerFreeTextResponseJsonHandler()
				registerUserJsonHandler()
				break;
			
		}
		log.info "Info: Bootstrap done"
	}
	
	def registerWordJsonHandler() {
		JSON.registerObjectMarshaller(Word) {
			def returnArray = [:]
			returnArray['text'] = it.toString()
			returnArray['weight'] = it.getWeight()
			return returnArray
		}
	}
	
	def registerFreeTextResponseJsonHandler() {
		JSON.registerObjectMarshaller(org.ucanask.Responses.FreeTextResponse) {
			def returnArray = [:]
			returnArray['username'] = it.userId ? User.get(it.userId).displayName : ""
			returnArray['userid'] = it.userId
			returnArray['text'] = it.toString()
			returnArray['anonymous'] = it.anonymous
			return returnArray
		}
	}
	
	def registerUserJsonHandler() {
		JSON.registerObjectMarshaller(org.ucanask.User) {
			def returnArray = [:]
			returnArray['username'] = it.username 
			returnArray['id'] = it.id
			returnArray['name'] = it.toString()
			returnArray['email'] = it.email
			return returnArray
		}
	}
	
	private setupAdminUser() {
		if (!User.find {username == "admin"}) {	
			def amRole    = Role.findByAuthority('ROLE_AM') ?:	new Role(authority: 'ROLE_AM').save(failOnError: true)
			def prRole    = Role.findByAuthority('ROLE_PR') ?:	new Role(authority: 'ROLE_PR').save(failOnError: true)
			def adminRole = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority: 'ROLE_ADMIN').save(failOnError: true)
			def user = new User(
				username:    'admin',
				password:      "IWontAnswer!",
				displayName: 'Admin',
				email:       "ucanaskteam@gmail.com",
				enabled:       true
			).save(failOnError: true)
			UserRole.create user, adminRole
			UserRole.create user, prRole
			UserRole.create user, amRole
			setupIgnoredWordList()
		}
	}
	
	/*
	 * Create users and setup roles for each one
	 * @return
	 */
	private setupUsers() {
		// Sample users for development
		def sampleUsers = [
			['admin',     'Admin', "ucanaskteam@gmail.com",        'admin'],
			['tbell', 		'Tim', 'tim.bell@canterbury.ac.nz',  	  'pr'],
			['mbrosnahan',  'Max', "msb67@uclive.ac.nz",              'pr'],
			['lchristie', 'Lewis', "lewis.christie@gmail.com",        'pr'],
			['jrutherford', 'Jon', "jrr66@uclive.ac.nz",              'pr'],
			['sdunford',  'Steve', "sjd150@uclive.ac.nz",             'pr'],
			['dwhite',     'Dave', "whitynz@orcon.net.nz",            'pr'],
			['mlang',      'Matt', "matt.lang.42@gmail.com",          'pr'],
			['skent',    'Stuart', "stuartnkent@gmail.com",           'pr'],
			['mmathews',  'Moff', "moffat.mathews@canterbury.ac.nz", 'pr'],
			['mquigley', 'Mark Quigley', 'mark.quigley@canterbury.ac.nz', 'pr'],
			['test',     'Testie', "ucanaskteam@gmail.com",           'am'] ]

		// If the following roles (Audience Member, Presenter and Admin) do not exist, create them
		def amRole    = Role.findByAuthority('ROLE_AM') ?:
			new Role(authority: 'ROLE_AM').save(failOnError: true)
		def prRole    = Role.findByAuthority('ROLE_PR') ?:
			new Role(authority: 'ROLE_PR').save(failOnError: true)
		def adminRole = Role.findByAuthority('ROLE_ADMIN') ?:
			new Role(authority: 'ROLE_ADMIN').save(failOnError: true)
			
			// Add the sample users into the database if there are no saved users
		if (!User.list()) {
			sampleUsers.each { userAttributes ->
				// println userAttributes
				def user = new User(
						username:    userAttributes[0],
						password:      "IWontAnswer!",
						displayName: userAttributes[1],
						email:       userAttributes[2],
						enabled:       true
					).save(failOnError: true)

				// Add the user to their specified role
				switch (userAttributes[3]) {
					case 'admin': UserRole.create user, adminRole
					case 'pr':    UserRole.create user, prRole
					default:      UserRole.create user, amRole
				}
				if (user.validate()) {
//					println "Creating new user: ${user.displayName} (${user.username})"
				}
			}
		}
		else {
			User.list().each {
				//println(User.getPrincipal.id.getAttribute("displayName"))
			}
		}
	}
	
	def setupIgnoredWordList() {
		def ignored = ["a", "able", "about", "after", "again", "all", "also", "am", "an", "and", "any", 
			"are", "as", "at", "back", "be", "because", "been", "before", "being", "between", "but", "by", "came", "can", "cause", 
			"change", "come", "could", "did", "do","does", "don", "down", "each", 
			"end", "even", "every", "far", "few", "for", "found", "four", "from", "get", "give",
			"go", "got", "good", "had", "has", "have", "he", "her", "here", "him", "his", "how","i","if", "im", "in", "is", "it","into", 
			"its", "just", "keep", "let", "like", "look", "make", "many", "may", "me", "might", "more", "most", "much", "must", "my",
			"near", "need", "never", "next", "no", "not", "now", "of", "off", "on","one", "only", "or", "other", 
			"our", "out", "over", "part", "put", "said", "same", "say", "see", "seem", "set", "she", "should", 
			"side", "so", "some", "still", "such", "take", "than", "that", "the", "their", "them", "then", 
			"there", "these", "they", "thing", "this", "three", "through", "to", "too", "up", "upon", "us",
			"use", "want", "was", "way", "we", "well", "went", "were", "what", "when", "where", "which", "while", 
			"who", "will", "with", "would", "you", "your"]
//		def customList = [
//			"python", "java", "c++"
//			]
		User admin = User.find {username == "admin"}
		IgnoreList l = new IgnoreList(owner: admin, global:true, ignoredWords: ignored as Set).save(failOnError: true)
		
//		User matty = User.find {username == "mlang"}
//		IgnoreList l2 = new IgnoreList(owner: matty, global:false, ignoredWords: customList as Set).save(failOnError: true)
	}

	private createInvitedRoom() {
		Room r1 = new Room(name:"Earthquake Lecture Series", owners: [User.find {username == "mquigley"}.id])
		.save(failOnError:true)
		earthquake1(r1)
		earthquake2(r1)
		
		Room r2 = new Room(name:"ERSK341", owners: [User.find {username == "mlang"}.id, User.find {username == "skent"}.id])
		.save(failOnError:true)
		geekyPresentation(r2)
		beerPresentation(r2)
		
		Room r = new Room(name:"COSC427", owners: [User.find {username == "mmathews"}.id])		
		r.addToInvited(User.find {username == "dwhite"}.id)
		r.addToInvited(User.find {username == "sdunford"}.id)
		r.addToInvited(User.find {username == "jrutherford"}.id)
		r.addToInvited(User.find {username == "mlang"}.id)
		r.addToInvited(User.find {username == "lchristie"}.id)
		r.addToInvited(User.find {username == "mbrosnahan"}.id)
		r.save(failOnError:true)
		
		UMLDiagrams(r)
		GangOfFourPrincipals(r)
	}
	
	private earthquake1(room) {
		Presentation presentation = new Presentation(name:"Ground Force Acceleration", active: true)
		presentation.owner = User.find {username == "mquigley"}
		presentation.room = room		
		
		def questionResource = new TextResource(text: "What was the ground force acceleration on February 22nd?")
		def answers = new MultiChoiceStrategy(choices:
				[new TextResource(text: "2.2g"), 
				 new TextResource(text: "2.8g"), 
				 new TextResource(text: "0.4g"), 
				 new TextResource(text: "5.0g"), 
			     new TextResource(text: "1.0g")], multiselect: false)
		def q = new Question(resources: questionResource, owner: User.find {username == "mquigley"},
			answerStrategy: answers, qLocked:false, room: room).save(failOnError:true)
		presentation.addToQuestions(new QuestionOccurrence(askedQuestion:q, active: false))
		presentation.save(failOnError:true)
			
			
		questionResource = new TextResource(text: "How would you describe the ground force acceleration that you experienced")
		answers = new FreeTextStrategy()
		q = new Question(resources: questionResource, owner: User.find {username == "mquigley"},
			answerStrategy: answers, qLocked:false, room: room).save(failOnError:true)
		presentation.addToQuestions(new QuestionOccurrence(askedQuestion:q, active: false))
		presentation.save(failOnError:true)
			
			
		questionResource = new TextResource(text: "What best describes peak ground force acceleration?")
		answers = new MultiChoiceStrategy(choices:
				[new TextResource(text: "Measure of earthquake acceleration on the ground and an important input parameter for earthquake engineering."),
				 new TextResource(text: "Measure of how quickly an earthquake moves through the soil."),
				 new TextResource(text: "Measure of the stress placed on a building during an earthquake event."),
				 new TextResource(text: "Measure of the instensity of the earth shaking."),
				 new TextResource(text: "Measure of how large the earthquake is."),
				 new TextResource(text: "1.0g")], multiselect: false)
		q = new Question(resources: questionResource, owner: User.find {username == "mquigley"},
			answerStrategy: answers, qLocked:false, room: room).save(failOnError:true)		
		presentation.addToQuestions(new QuestionOccurrence(askedQuestion:q, active: false))
		presentation.save(failOnError:true)
	}
	
	private earthquake2(room) {
		Presentation presentation = new Presentation(name:"The Christchurch Event", active: true)
		presentation.owner = User.find {username == "mquigley"}
		presentation.room = room
			
		def questionResource = new TextResource(text: "How do you think Christchurch is now recovering?")
		def answers = new FreeTextStrategy()
		User u = User.find {username == "mquigley"}
		def q = new Question(resources: questionResource, owner: u,
			answerStrategy: answers, qLocked:false, room: room).save(failOnError:true)
		presentation.addToQuestions(new QuestionOccurrence(askedQuestion:q, active: false))
		presentation.save(failOnError:true)
		
	}
	
	
	private UMLDiagrams(room) {
		Presentation presentation = new Presentation(name:"UML Refreshers", active: true)
		presentation.owner = User.find {username == "mmathews"}
		presentation.room = room
		
		def questionResource = new TextResource(text: "What do Use Case Diagrams Include?")
		def answers = new MultiChoiceStrategy(choices:
				[new TextResource(text: "Actors"),
				 new TextResource(text: "Scenarios"),
				 new TextResource(text: "Pre-conditions"),
				 new TextResource(text: "Code"),
				 new TextResource(text: "Relationships"),
				 new TextResource(text: "Classes"),
				 new TextResource(text: "Methods")], multiselect: true)
		def q = new Question(resources: questionResource, owner: User.find {username == "mmathews"},
			answerStrategy: answers, qLocked:false, room: room).save(failOnError:true)
		presentation.addToQuestions(new QuestionOccurrence(askedQuestion:q, active: false))
		presentation.save(failOnError:true)
			
			
		questionResource = new TextResource(text: "Can you explain a use case diagram?")
		answers = new FreeTextStrategy()
		q = new Question(resources: questionResource, owner: User.find {username == "mmathews"},
			answerStrategy: answers, qLocked:false, room: room).save(failOnError:true)
		presentation.addToQuestions(new QuestionOccurrence(askedQuestion:q, active: false))
		presentation.save(failOnError:true)
			
			
		questionResource = new TextResource(text: "What is the difference between public, private and protected?")
		answers = new FreeTextStrategy()
		q = new Question(resources: questionResource, owner: User.find {username == "mmathews"},
			answerStrategy: answers, qLocked:false, room: room).save(failOnError:true)
		presentation.addToQuestions(new QuestionOccurrence(askedQuestion:q, active: false))
		presentation.save(failOnError:true)
		
		questionResource = new TextResource(text: "Class diagrams at conceptual level should include?")
		answers = new MultiChoiceStrategy(choices:
				[new TextResource(text: "Attributes Only"),
				 new TextResource(text: "Methods Only"),
				 new TextResource(text: "Both attributes and methods")], multiselect: false)
		q = new Question(resources: questionResource, owner: User.find {username == "mmathews"},
			answerStrategy: answers, qLocked:false, room: room).save(failOnError:true)
		presentation.addToQuestions(new QuestionOccurrence(askedQuestion:q, active: false))
		presentation.save(failOnError:true)
	}
	
	
	private GangOfFourPrincipals(room) {
		Presentation presentation = new Presentation(name:"Design Patterns", active: true)
		presentation.owner = User.find {username == "mmathews"}
		presentation.room = room
		
		def questionResource = new TextResource(text: "What one of these is not a design pattern?")
		def answers = new MultiChoiceStrategy(choices:
				[new TextResource(text: "Composite"),
				 new TextResource(text: "Singleton"),
				 new TextResource(text: "Iterator"),
				 new TextResource(text: "Ace"),
				 new TextResource(text: "Factory Method")], multiselect: false)
		def q = new Question(resources: questionResource, owner: User.find {username == "mmathews"},
			answerStrategy: answers, qLocked:false, room: room).save(failOnError:true)
		def qo = new QuestionOccurrence(askedQuestion:q, active: true)
		presentation.addToQuestions(qo)			
		14.times { i ->
			qo.addResponse(['choice':[answers.choices[i%5].id.toString()]])
		}
		6.times { 
			qo.addResponse(['choice':[answers.choices[3].id.toString()]])
		}
			
		questionResource = new TextResource(text: "What is a design pattern?")
		answers = new FreeTextStrategy()
		q = new Question(resources: questionResource, owner: User.find {username == "mmathews"},
			answerStrategy: answers, qLocked:false, room: room).save(failOnError:true)
		qo = new QuestionOccurrence(askedQuestion:q, active: true)
		presentation.addToQuestions(qo)
		
		def design = "In software engineering, a design pattern is a general reusable solution to a commonly occurring problem within a given context in software design. A design pattern is not a finished design that can be transformed directly into code. It is a description or template for how to solve a problem that can be used in many different situations. Object-oriented design patterns typically show relationships and interactions between classes or objects, without specifying the final application classes or objects that are involved. Many patterns imply object-orientation or more generally mutable state, and so may not be as applicable in functional programming languages, in which data is immutable or treated as such. Design patterns reside in the domain of modules and interconnections. At a higher level there are architectural patterns that are larger in scope, usually describing an overall pattern followed by an entire system. There are many types of design patterns, like Algorithm strategy patterns addressing concerns related to high-level strategies describing how to exploit application characteristic on a computing platform. Computational design patterns addressing concerns related to key computation identification. Execution patterns that address concerns related to supporting application execution, including strategies in executing streams of tasks and building blocks to support task synchronization.	Implementation strategy patterns addressing concerns related to implementing source code to support	program organization, and the common data structures specific to parallel programming. Structural design patterns addressing concerns related to high-level structures of applications being developed."
		def split = design.tokenize(".")
		split.each { str ->
			qo.addResponse(['text': str])
		}
		
		presentation.save(failOnError:true)
	}
	/**
	 * A presentation with questions for the four lecture of COSC122
	 * @return
	 */
	/*private cosc122L4Presentation(room) {
		Presentation presentation = new Presentation(name:"Lecture 4", active: false)
		presentation.owner = User.find {username == "admin"}
		presentation.room = room
		def questionResource = new TextResource(text: "Is a set of objects ordered?")
		def answers = new MultiChoiceStrategy(choices:
				[new TextResource(text: "Yes"), new TextResource(text: "No")], multiselect: false)
		def q = new Question(resources: questionResource, owner: User.find {username == "admin"},
			answerStrategy: answers, qLocked:false, room: room).save(failOnError:true)
		presentation.addToQuestions(new QuestionOccurrence(askedQuestion:q, active: false))

		questionResource = new TextResource(text: "A set of objects is unique?")
		answers = new MultiChoiceStrategy(choices:
				[new TextResource(text: "True"), new TextResource(text: "False")], multiselect: false)
		q = new Question(resources: questionResource, owner: User.find {username == "admin"},
			answerStrategy: answers, qLocked:false, room: room).save(failOnError:true)
		presentation.addToQuestions(new QuestionOccurrence(askedQuestion:q, active: false))

		presentation.save(failOnError:true)
		
		presentation
	}*/
	
	/**
	 * A presentation that has geeky questions with no right answer
	 */
	private geekyPresentation(room) {
		Presentation presentation = new Presentation(name:"The Geek Survey", active: true)
		presentation.owner = User.find {username == "admin"}
		presentation.room = room
		def questionResource = new TextResource(text: "Do you prefer a PC or a Mac?")
		def answers = new MultiChoiceStrategy(multiselect: false, choices:
				[new TextResource(text: "PC"), new TextResource(text: "Mac")])
		def q = new Question(resources: questionResource, owner: User.find {username == "admin"},
			answerStrategy: answers, qLocked:false, room: room).save(failOnError:true)
		presentation.addToQuestions(new QuestionOccurrence(askedQuestion:q, active: true))

		questionResource = new TextResource(text: "What are your preferred programming languages?")
		answers = new MultiChoiceStrategy(multiselect: true, choices:
				[new TextResource(text: "Steve++"),
					new TextResource(text: "Java"),
					new TextResource(text: "C/C++"),
					new TextResource(text: "Python"),
					new TextResource(text: "Ruby"),
					new TextResource(text: "C#"),
					new TextResource(text: "A Magical Web Framework")])
		q = new Question(resources: questionResource, owner: User.find {username == "admin"},
			answerStrategy: answers, qLocked:false, room: room).save(failOnError:true)
		presentation.addToQuestions(new QuestionOccurrence(askedQuestion:q, active: true))

		questionResource = new TextResource(text: "Who is the greatest geek of all time?")
		answers = new MultiChoiceStrategy(multiselect: false, choices:
				[new TextResource(text: "Linus Torvalds"),
					new TextResource(text: "Steve Wozniak"),
					new TextResource(text: "Sir Tim Berners-Lee"),
					new TextResource(text: "Seymour Cray"),
					new TextResource(text: "Richard Stallman"),
					new TextResource(text: "Paul Allen")])
		q = new Question(resources: questionResource, owner: User.find {username == "admin"},
			answerStrategy: answers, qLocked:false, room: room).save(failOnError:true)
		presentation.addToQuestions(new QuestionOccurrence(askedQuestion:q, active: true))
		presentation.save(failOnError:true)
		
		presentation
	}

	private beerPresentation(room) {
		Presentation presentation = new Presentation(name:"Beer Essentials", active: true)
		presentation.owner = User.find {username == "admin"}
		presentation.room = room
		def questionResource = new TextResource(text: "Which of these is an essential ingredient in beer?")
		def answers = new MultiChoiceStrategy(multiselect: false, choices:
				[new TextResource(text: "Furry Animals"), new TextResource(text: "Lava"), new TextResource(text: "Water")])
		def q = new Question(resources: questionResource, owner: User.find {username == "admin"},
			answerStrategy: answers, qLocked:false, room: room).save(failOnError:true)
		presentation.addToQuestions(new QuestionOccurrence(askedQuestion:q, active: true))

		questionResource = new TextResource(text: "Which of these beers do you like")
		answers = new MultiChoiceStrategy(multiselect: true, choices:
				[new TextResource(text: "Emersons Pilsner"),
					new TextResource(text: "8 Wired Tall Poppy"),
					new TextResource(text: "Renaissance Stonecutter"),
					new TextResource(text: "Heineken"),
					new TextResource(text: "Speights Old Dark"), // Love ya work Jon
					new TextResource(text: "Floor spillage"),
					new TextResource(text: "Tasman Bitter")])
		q = new Question(resources: questionResource, owner: User.find {username == "admin"},
			answerStrategy: answers, qLocked:false, room: room).save(failOnError:true)
		presentation.addToQuestions(new QuestionOccurrence(askedQuestion:q, active: true))

		questionResource = new TextResource(text: "What is the world record for sculling 1 litre of Beer?")
		answers = new MultiChoiceStrategy(multiselect: false, choices:
				[new TextResource(text: "2mins 49.4secs"),
					new TextResource(text: "2mins 18.6secs"),
					new TextResource(text: "1mins 22.9secs"),
					new TextResource(text: "0min 39.3secs"),
					new TextResource(text: "0min 12.4secs"),
					new TextResource(text: "0min 1.3secs")])
		q = new Question(resources: questionResource, owner: User.find {username == "admin"},
			answerStrategy: answers, qLocked:false, room: room).save(failOnError:true)
		presentation.addToQuestions(new QuestionOccurrence(askedQuestion:q, active: true))
		
		questionResource = new TextResource(text: "Which country makes the best beer?")
		q = new Question(resources: questionResource, owner: User.find {username == "admin"},
			answerStrategy: new FreeTextStrategy(), qLocked:false, room: room).save(failOnError:true)
		presentation.addToQuestions(new QuestionOccurrence(askedQuestion:q, active: true))
		
		presentation.save(failOnError:true)
		
		presentation
	}
	
	private textQPres(room) {
		Presentation presentation = new Presentation(name:"A pres with txt questions!", active: false)
		presentation.owner = User.find {username == "mlang"}
		presentation.room = room
		def questionResource = new TextResource(text: "Tell me about yourself")
		def answers = new FreeTextStrategy()
		def q = new Question(resources: questionResource, owner: User.find {username == "mlang"},
			answerStrategy: answers, qLocked:false, room: room).save(failOnError:true)
		presentation.addToQuestions(new QuestionOccurrence(askedQuestion:q, active: false))		

		presentation.save(failOnError:true)
		
		presentation
	}
	
	def destroy = {

	}

}
