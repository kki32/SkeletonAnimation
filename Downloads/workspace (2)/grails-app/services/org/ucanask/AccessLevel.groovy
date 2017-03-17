package org.ucanask

enum AccessLevel {
	Admin("Admin"),
	Presenter("Presenter"),
	AuthenticatedAudience("Audience"),
	UnauthenticatedAudience("Audience"),
	None("None")
	
	final String value
	
	AccessLevel(String value) { this.value = value }
	
	String toString() { value }
	String getKey() { name() }
}
