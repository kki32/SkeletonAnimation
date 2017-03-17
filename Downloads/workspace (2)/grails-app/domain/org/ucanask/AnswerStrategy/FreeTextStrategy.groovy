

package org.ucanask.AnswerStrategy



import javax.imageio.ImageIO

import org.ucanask.*
import org.ucanask.Responses.FreeTextResponse
import org.ucanask.chart.Corpus
import org.ucanask.chart.Word
import org.ucanask.chart.WordCloud
import java.util.regex.Pattern;

class FreeTextStrategy extends AnswerStrategy {

	def corpusService
	
	/** 
	 * Factory method to return a response of the correct type when
	 * given the user (eventually) and the required params
     */
	FreeTextResponse makeResponse(dict) {
		if (dict?.text != null && dict.text != "") {
			return new FreeTextResponse(textResponse: dict?.text)
		}
		return null
	}
	
	/**
	 * Create a list of all of the responses for this specific question occurrence.
	 * The if statement ensures the answerStrategy is in fact a FreeTextStrategy before continuing.
	 * @param dict containing a questionOccurrence and chartType
	 * @return a list collection of textual responses
	 */
	@Override
	public def responseCollection(dict) {
		if(dict?.occurrence?.askedQuestion?.answerStrategy?.instanceOf(FreeTextStrategy)) {
			def presId = dict?.presId
//			boolean globalFilter = dict?.globalFilter?.equals("on")
//			boolean presFilter = dict?.presFilter?.equals("on")
//			List qOccFilter = dict?.qOccFilter ? dict.qOccFilter.split(" +") : []
			switch (dict?.displayType) {
				case "rawFT":
					return getResponsesRaw(dict?.occurrence)			
				case "cloud":
					return getStaticCloud(dict?.occurrence, presId)
				case "animCloud":
					return getResponsesCloud(dict?.occurrence, presId)	
				default:
					return []		
			}
		}
		return []
	}
	
	/**
	 * Get responses formatted for display in a plain list
	 * @param questionOccurrence - to get responses from
	 * @return a list of raw text responses
	 */
	private def getResponsesRaw(QuestionOccurrence questionOccurrence) {
		Map responses = [:]	
		questionOccurrence?.responses?.each {
			if(it.enabled)
				responses.put(it.id, it.toString())
		}
		return responses;
	}
	
	/**
	 * Get responses formatted for display in a word cloud
	 * @param questionOccurrence - to get responses from
	 * @return a map containing each word and its frequency
	 */
	private def getResponsesCloud(QuestionOccurrence questionOccurrence, presId) {
		Pattern p = Pattern.compile('(^-+)|(-+$)|[^a-zA-Z0-9-]');
		
		List ignoredWords = getIgnoredWords(questionOccurrence, presId)
		double totalWords = 0;
		def words = [:].asSynchronized()	
		questionOccurrence?.responses?.each {resp ->
			if (resp.enabled) {
				def tokens = resp.toString().tokenize()
				totalWords += tokens.size()
				tokens.each() {token ->
					token = p.matcher(token).replaceAll(" ").toLowerCase();
					if (!ignoredWords.contains(token) && !token.isEmpty()) {
						Word w = new Word(token)
						Integer freq = words.get(w)
						freq == null ? freq = 1 : ++freq
						w.setWeight(freq)
						words.remove(w)  // grails...!
						words.put(w, freq)
					}
				}
			}	
		}
		
		def wordsList = words.keySet() as List
		
//		def test = true
//		if (test) {
//			wordsList.sort()
//			println("Pre-tfidf: " + wordsList.subList(0, 20))
//		}
		
		def applyTfidf = true   // TODO
		if (applyTfidf) {
			applyWeighting(wordsList, totalWords)
		}

		wordsList.sort()
//		if (test) {
//			println("Post-tfidf: " + wordsList.subList(0, 20))
//		}
//		
		
		def maxWords = 20  // TODO config option?
		return wordsList.size() <= maxWords ? wordsList : wordsList.subList(0, maxWords)
	}	
	
	private def getStaticCloud(QuestionOccurrence questionOccurrence, presId) {
		def words = getResponsesCloud(questionOccurrence, presId)
		def wc = new WordCloud(words)		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(wc.getImage(), "PNG", baos)
		return baos.toByteArray()
	}	
	
	private List<String> getIgnoredWords(qOcc, presId) {
	def ignoredWords = []
		if (qOcc.globalIgnoreList) {			
			def globalIgnored = IgnoreList.findByGlobal(true)?.ignoredWords
			if (globalIgnored) {
				ignoredWords.addAll(globalIgnored)
			}
		}
		if (qOcc.presIgnoreList) {
			User pres = User.get(presId)
			def presIgnored = IgnoreList.findByOwnerAndGlobal(pres, false)?.ignoredWords
			if (presIgnored) {
				ignoredWords.addAll(presIgnored)
				
			}
		}		
		ignoredWords.addAll(qOcc.ignoredWords)
		//println ignoredWords
		return ignoredWords
	}
	
	private void applyWeighting(List<Word> words, double totalWords) {
		words.each { word ->
			double wordFrequency = word.getWeight() / totalWords
			word.setWeight(corpusService.getTfidfWeight(word.toString(), wordFrequency))
		}
	}
	
	String typeName() {
		"FreeText"
	}
}
