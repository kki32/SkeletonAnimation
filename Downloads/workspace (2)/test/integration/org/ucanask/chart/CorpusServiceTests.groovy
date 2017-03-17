package org.ucanask.chart


import org.ucanask.chart.Corpus
import java.io.File
import java.util.Random

import grails.test.mixin.*

import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * tfidf = Term Frequency Inverse Document Frequency
 * TF = Term Frequency - the frequency with which a term occured within a test document (the set of audience responses)
 * DF = Document Frequency - How many documents with the corpus contained a given term
 * IDF = Inverse Document Frequency 
 * 
 * 
 * 
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(CorpusService)
class CorpusServiceTests  {
	Logger log = LoggerFactory.getLogger(CorpusServiceTests)
	CorpusService corpusService
	
	
	void testSetUp() { // screw you grails!
		assert corpusService != null
		assert corpusService.corpus != null
	}
	
	/**
	 * Terms with high DF should have very little chance of getting a significant weighting
	 */
    void testVeryCommonTerm() {
		double termFrequency = 1.0 / 25.0
        assert corpusService.getTfidfWeight("for", termFrequency) < 0.001
    }
	
	/**
	 * A term with low DF should get a higher weighting than a one with high DF, if TF's are equal
	 */
	void testRareTerm() {
		assert corpusService.corpus.wordCount("splendid") < corpusService.corpus.wordCount("the")
		def termFrequency = 0.01
		assert corpusService.getTfidfWeight("splendid", termFrequency) > corpusService.getTfidfWeight("the", termFrequency)
	}
	
	void testHighFrequencyTerm() {
		assert corpusService.getTfidfWeight("rubbish", 0.5) > corpusService.getTfidfWeight("trash", 0.01)
	}
	
	/**
	 * A term which does not occur in the corpus is given the minimal DF (0 + 1), and therefore should return a lower
	 * weight (assuming TF is the same) than a term with DF = (1 + 1)
	 */
	void testNonExistantTerm() {
		assert corpusService.corpus.wordCount("scholasticus") == 1
		def termFrequency = 0.01
		assert corpusService.getTfidfWeight("yabadabadooooooooooooo", termFrequency) > corpusService.getTfidfWeight("scholasticus", termFrequency)
		assert corpusService.getTfidfWeight("yabadabadooooooooooooo", termFrequency) == corpusService.getTfidfWeight("anotherMadeUpTerm", termFrequency)
	}
	
	void testBashIt() {		
		def numThreads = 20, numActions = 10000
		for (int i = 0; i < numThreads; i++) {
			new Thread() {
				Random random = new Random()
				@Override
				public void run() {
					for (int j = 0; j < numActions; j++) {
						def result = corpusService.getTfidfWeight("frankly", random.nextDouble())
						assert result != null && result instanceof Double
					}
				}
			}.run()
		}	
	}
	
	void testGrailsTestingGayness() {
		def grailsTesting = true, gay = true
		assert (grailsTesting == gay)
	}
}





