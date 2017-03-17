package org.ucanask


import javax.naming.directory.Attributes
import org.springframework.ldap.CommunicationException
import org.springframework.ldap.NamingException
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate

/**
 * Return the found item, or list of found items
 * @author admin
 * @return set of search results, or an empty set
 */
class LdapSearchService {
	
	LdapTemplate myLdapTemplate
	
	def lookup(String filterField, String filterAttr, String searchAttr) {
		def appContext = new User().domainClass.grailsApplication.mainContext
		myLdapTemplate = appContext.ldapTemplate
		
		def search = null
		
		try {
			search = myLdapTemplate.search("", "($filterField=$filterAttr*)", new AttributesMapper() {
				@Override
				public Object mapFromAttributes(Attributes attrs) throws NamingException {
					return attrs.get(searchAttr)?.get()
				}
			})
		} catch (CommunicationException e) {
			// Nothing to do here, but LDAP has failed and there will be a delay
			// for the punters waiting for it to time out.  If they have local
			// accounts, they will still be logged in using those - if not, they just
			// won't be able to log in.  They could still access open rooms using a
			// access code if available
		}
		
		//return the search result, or an empty linked list if search fails
		List nothing = new LinkedList()
		nothing.add("")
		return (search == null)? nothing : search
	}
	
	
	/**
	 * This returns a Sorted Set of all available courses currently available in the LDAP tree.
	 * 
	 * It should probably be called nightly at most as its quite probably a big ask for the LDAP
	 * database, but is currently called every time someone views the edit room (course) page to
	 * populate the dropdown for linking courses to a room
	 *  
	 * @return SortedSet of available (current) course codes from LDAP
	 */
	def allCourses() {
		def appContext = new User().domainClass.grailsApplication.mainContext
		myLdapTemplate = appContext.ldapTemplate
		
		def search = []
		
		try {
			search = myLdapTemplate.search("", "(&(uid=*)(uccourse=*))", new AttributesMapper() {
				@Override
				public Object mapFromAttributes(Attributes attrs) throws NamingException {
					return attrs.get("uccourse")?.get()
				}
			})
		} catch (CommunicationException e) {
			// Nothing to do here, but LDAP has failed and there will be a delay
			// for the punters waiting for it to time out.  If they have local
			// accounts, they will still be logged in using those.
		}
		
		//return the search result, or an empty linked list if search fails
		def courses = [] as SortedSet
		courses.addAll(search)
		return courses
	}
}
