package org.ucanask.chart;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Built from the British Academic Written English Corpus (BAWE), for the purpose of finding 
 * the Term Frequency Inverse Document Frequency (TFIDF) weighting of terms (ie English words)
 * within a document. 
 * 
 * Tfidf is useful for finding out how important/relevant a term is to a document. 
 *  
 * @author jrr
 *
 */
public class Corpus implements Serializable {
	private static final long serialVersionUID = -4268842474358026332L;
	private Map<String, Integer> words;
	private Integer numDocuments;

	/**
	 * Constructor 1
	 * @param corpus - a map containing each word in the corpus, and how many of the corpus' documents it occurred in
	 * @param numDocuments - number of documents in the corpus
	 */
	public Corpus(Map<String, Integer> words, Integer numDocuments) {
		this.words = words;
		this.numDocuments = numDocuments;
	}
	
	/**
	 * Rebuild a Corpus object from a File containing its serialised form.
	 * Assumes standard Java serialization. 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Corpus deserialize(File path) {
		HashMap<String, Integer> words = null;
		try {
			ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(path)));
			words = (HashMap<String, Integer>) in.readObject();
			in.close();
		} catch (Exception e) {
			System.out.println(e.getCause() + " " + e.getMessage() + " " + e.getClass());
			e.printStackTrace();
		}
		return new Corpus(words, words.get("<NUM_FILES>"));
	}
	
	public static String deserialize() {
		return "Shit bro " + System.getProperty("user.dir");
	}
	
	/**
	 * Get the tfidf weighting of a term.
	 * 
	 * @param term - the term to get the weighting of
	 * @param termFrequency - the frequency of term within its enclosing document (eg 100 words in document, a term which occurs 5 times will = 0.05)
	 * @return the tfidf weighting of term
	 */
	public double getTfidfWeight(String term, double termFrequency) {
		double idf = Math.log(numDocuments / new Double(1.0 + wordCount(term))); 
		return termFrequency * idf;
	}
	
	/**
	 * Get how many documents of the corpus contain the given word
	 * @param word - the word to check the corpus for
	 * @return - how many documents word occurs in within the corpus
	 */
	private Integer wordCount(String word) {
		Integer freq = words.get(word);
		return freq != null ? freq : 0;
	}
			
	public String toString() {
		return "Number of documents in corpus: " + numDocuments + "\n" +
				   "Number of unique terms: " + words.size();
	}
}



