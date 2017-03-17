package org.ucanask.chart;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Random;


public class Word implements Comparable<Word> {
	private double weight;
	private String text;
	private Shape shape;
	private Color fill;
	private float lineHeight;
	
	public Word(String text) {		
		this.text = text.trim();
		if (this.text.isEmpty()) {
			throw new IllegalArgumentException("Word text length must be > 0");
		}		
		weight = 0.0;
		lineHeight = 0.5f;
	}
	
	public String toStringVerbose() {
		return text + " " + weight;
	}	
	
	public double getWeight() {
		return weight;
	}
	
	public double setWeight(double weight) {
		this.weight = weight;
		return this.weight;
	}
	
	public String toString() {		
		return text;
	}
	
	public void setFill(Color fill) {
		this.fill = fill;
	}

	public Color getFill() {
		return fill;
	}
	
	public float getLineHeight() {
		return lineHeight;
	}

	public void setLineHeight(float lineHeight) {
		this.lineHeight = lineHeight;
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public void setShape(Shape shape) {
		this.shape = shape;
		//this.bounds = (shape == null) ? null : shape.getBounds2D();
	}
	
	public Rectangle2D getBounds() {
		return (shape == null) ? null : shape.getBounds2D();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.toString().equals(this.toString());		
	}

	@Override
	public int compareTo(Word o) {
		if (this.equals(o) || this.getWeight() == o.getWeight()) {
			return 0;
		}
		return o.getWeight() - this.weight > 0 ? 1 : -1; 
	}
	
	@Override
	public int hashCode() {
		return this.text.hashCode();
	}
}
