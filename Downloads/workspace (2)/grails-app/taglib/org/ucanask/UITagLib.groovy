
package org.ucanask


/**
 * This will render different partial views depending on the user.
 */
class UITagLib {
	static namespace = "ucanask"
	
	/**
	* Renders a toggle button
	* @attr active REQUIRED true to start "on"
	* @attr name REQUIRED the class of button to add the event handler to
	* @attr id OPTIONAL an id for the js to pass on click
	*/
	def toggle = { attrs -> 
		def idattr = attrs.id ? "id='${attrs.id}' ":''
        out << "<span ${idattr} class='hide-on-ie ${attrs.name} toggle-button ${attrs.active ? 'state-on' : 'state-off'}' ><img src='/images/ui/transparent.gif' /></span>"
		out << "&nbsp;<span class='right show-on-ie ${attrs.name}-ie button_normal' id='${attrs.id}-ie'>${attrs.active ? 'Disable' : 'Enable'}</span>"
	}
	def bluebutton = { attrs, body ->
		attrs.class = "button_normal " + (attrs.class ? attrs.class : '')
		out << g.link(attrs, body)
	}
	def redbutton = { attrs, body ->
		attrs.class = "button red " + (attrs.class ? attrs.class : '')
		out << g.link(attrs, body)
	}
	/**
	 * Renders the one and only tree nav thing.
	 * @body links separated by <sep/>
	 * @attrs none bro
	 */
	def tree = { attrs, body ->
		def links = body().split("<sep/>");
		
		out << "<div class='tree-house'>"
		out << "<ol class='tree'>"
		
		for(def i = 0; i < links.size(); i++){
			def arrow = "<span class='arrow'></span>"
			if(i%2 == 0 && i == links.size()-1){
				arrow = ''
			}
			def hidden = i < links.size()-2 ? 'class="hide-mobile"' : '';
			def borderRadius = i == links.size()-2 ? " border-mobile-bl" : ''; 
			out << "<li ${hidden} ><span class='node${i%2 == 0 ? '' : ' odd'}${borderRadius}'> ${links[i]}${arrow}</span></li>"
		}
		out << "</ol>"
//		if(links.size() > 1){
//			out << "<ol class='tree show-mobile'> <li><span class='node odd'><span class='back-arrow'></span> ${links[links.size()-2]}</span></li></ol>"
//		}
		out << "</div>"
	}
	
}
