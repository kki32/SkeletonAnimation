package presentation

import org.ucanask.Presentation
import org.ucanask.QuestionOccurrence
import org.ucanask.Responses.FreeTextResponse

class ResponseService {

    static synchronized boolean addResponse(dict) {
	    def questionOcc = QuestionOccurrence.get(dict.quesid)		
	    if (!questionOcc) {
	      return false
	    }	
	    questionOcc.addResponse(dict)
		
		return questionOcc.save(flush:true)	!= null	
    }
	
	static synchronized boolean addAudienceQuestion(dict) {		
		def presentationInstance = Presentation.get(dict.id)
		presentationInstance.addToAudienceQuestions(new FreeTextResponse(dict))
		presentationInstance.audienceQuestionVersion++
		return true
	}
}
