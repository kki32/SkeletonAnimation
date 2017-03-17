package org.ucanask.chart

class CorpusService {

	Corpus corpus;
	
    synchronized double getTfidfWeight(String term, double termFrequency) {
		return corpus.getTfidfWeight(term, termFrequency)
    }
}
