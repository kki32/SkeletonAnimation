package org.ucanask.AnswerStrategy

import java.util.Map;
import org.ucanask.*
import org.ucanask.Responses.MultiChoiceResponse

/**
 *
 */
class MultiChoiceStrategy extends AnswerStrategy {

	List choices
	boolean multiselect

	static hasMany = [choices : Resource]

	/**
	 *
	 */
	static constraints = {
		choices(minSize:2, nullable:false)
		multiselect(nullable:false)
	}

	/**
	 * Factory method to create a response to this multichoice question.
	 * Using a dictionary as this will need to tie in a user too.
	 * @param dict the dictionary with "choice" being a list of resource ids.
	 * @return a MultiChoiceResponse that knows the resource(s) selected.
	 */
	MultiChoiceResponse makeResponse ( dict ) {
		if (dict?.choice != null) {
			def mcr = new MultiChoiceResponse()
			// For each multi-select choice
			dict.choice.each { selectionId ->
				// Find the resource with that choice id
				choices.find { resource ->
					if (resource.id.toString() == selectionId) {
						mcr.addToChoices(resource)
					}
				}
			}
			// Stops responding multiple to a single select question
			if (mcr.choices != null && !(!multiselect && mcr.choices.size()>1)) {
				return mcr
			}
		}
		return null
	}

	/**
	 * Make a map with all of the responses from a specific question occurrence for this strategy
	 * For this questionOccurrence, get a map of possible choices and number of responses for each.
	 * @param questionOccurrence to retrieve responses from
	 * @return a map collection of choices and their cardinality
	 */
	@Override
	public def responseCollection(dict) {
		QuestionOccurrence questionOccurrence = dict?.occurrence
		def displayType = dict?.displayType
		// Ensure this is the same answer strategy before proceeding
		if(questionOccurrence?.askedQuestion?.answerStrategy.instanceOf(MultiChoiceStrategy)) {

			// Get the responses for the display selected
			switch(displayType) {
				case "Grouped":
					return groupedResponses(questionOccurrence);
				case "":
					return individualResponses(questionOccurrence);
				default:
					return [:]					
			}
		}
		return [:]
	}

	/**
	 * Gets the individual responses (i.e. each choice and there count)
	 * @param questionOccurrence the occurrence of the question
	 * @return a map of choices and their response count
	 */
	private def individualResponses(QuestionOccurrence questionOccurrence) {
		def responseMap = [:]
		questionOccurrence?.responses?.each { resp ->
			resp.choices.each{
				def val = responseMap.get(it.toString())
				responseMap.put(it.toString(), val ? val + 1 : 1)
			}
		}
		responseMap
	}
	
	/**
	 * Gets the grouped repsonse (i.e. count of all combination of responses for each user)
	 * When the multichoice question is a multiselect
	 * @param questionOccurrence the occurrence of the question
	 * @return
	 */
	private def groupedResponses(QuestionOccurrence questionOccurrence) {
		TreeMap tempMap = new TreeMap()
		
		questionOccurrence?.responses?.each { resp ->
			ResponseCombinations r = new ResponseCombinations(resp.toString())
			
			if(tempMap.get(r) != null) {
				int val = tempMap.get(r);
				val++;
				r.setCount(val);
				tempMap.put(r, val)
			}
			else {
				tempMap.put(r, 1)
			}
		}
		
		def responseMap = [:]
		if(tempMap.size() <= 8) {
			return tempMap;
		} else{
			int otherCount = 0
			Iterator iter = tempMap.iterator()
			while(iter.hasNext()) {
				def entry = iter.next()
				if(responseMap.size() < 7) {
					responseMap.put(entry.key, entry.value)
				} else {
					otherCount += entry.value;
				}
			}
			responseMap.put(new ResponseCombinations("Other", otherCount), otherCount)
		}
		return responseMap
	}
	
	/**
	 * The name for this strategy
	 */
	String typeName() {
		"Multi-Choice"
	}
}
