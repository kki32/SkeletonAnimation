package org.ucanask;

public class ResponseCombinations implements Comparable<ResponseCombinations> {

	private String choices;
	private int count;
	
	public ResponseCombinations(String choices) {
		this.choices = choices;
		this.count = 1;
	}
	
	public ResponseCombinations(String choices, int count) {
		this.choices = choices;
		this.count = count;
	}
	
	public boolean equals(Object o) {
		if(o == null) return false;
		if(o.getClass() != getClass()) return false;
		ResponseCombinations r = (ResponseCombinations) o;
		return choices.equals(r.getChoices());
	}
	
	public int hashCode() {
		return choices.hashCode();
	}
	
	public String getChoices() {
		return choices;
	}
	
	public Integer getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public int compareTo(ResponseCombinations o) {
		if(o.toString().equals(toString())) {
			return 0;
		} else {
			int compare = o.getCount().compareTo(getCount());
			if(compare == 0) {
				compare--;
			}
			return compare;
		}
	}
	
	public String toString() {
		return choices;
	}
}
