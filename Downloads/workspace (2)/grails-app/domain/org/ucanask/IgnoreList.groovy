package org.ucanask

class IgnoreList {

	User owner
	Set ignoredWords
	boolean global  // applies across all Users/Questions eg "it", "the"
	Map <QuestionOccurrence, Set<String>> occurrences
	
	static hasMany = [ignoredWords: String]
    static belongsTo = [User]
	
	static constraints = {
		owner nullable:false
		global nullable:false
		ignoredWords nullable:false
    }
	
	static fetchMode = [ignoredWords: "eager", occurrences: "eager"]
}


