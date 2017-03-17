package org.ucanask.chart;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;


public class WordCloud {
	
	//Display Options
	private int maxFontSize;  
	private int minFontSize;
	private String defaultFontFamily; // = "SansSerif";    // one of Dialog, Serif, SansSerif, Monospaced, DialogInput
	private Rectangle2D imageSize;
	private Color fill;
	private double dRadius;
	private int dDeg;
	private static final int PADDING = 5; 
	private int width = 800;
	private int height = 500;
	
	private Random rand;
	private List<Word> words;
	private BufferedImage image;
	
	
	public WordCloud(List<Word> words) {	
		minFontSize = 15;
		maxFontSize = 80;
		defaultFontFamily = "SansSerif";
		dDeg = 20;
		dRadius = 40f;
		this.words = words;
		rand = new Random();
		image = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
		
		if (!words.isEmpty()) {
			formatWords();
			layoutWords();
			drawCloud();
		}
	}

	
	/**
	 * Set the appearance of each word according to its settings.
	 * If any settings are null, default values are used.
	 */
	private void formatWords() {
		double high = words.get(0).getWeight();
		double low = words.get(words.size()-1).getWeight();
		FontRenderContext frc = new FontRenderContext(null, true, false);
		for(Word word : words) {
			int fontSize = (int)(((word.getWeight() - low)/(high - low)) * (maxFontSize - minFontSize)) + minFontSize;
			Font font = new Font(defaultFontFamily, Font.BOLD, fontSize);
			TextLayout textLayout = new TextLayout(word.toString(), font, frc);
			Shape shape = textLayout.getOutline(null);	
			Rectangle2D bounds = shape.getBounds2D();
			AffineTransform centerTr = AffineTransform.getTranslateInstance(-bounds.getCenterX(),-bounds.getCenterY());
			word.setShape(centerTr.createTransformedShape(shape));
		}		
	}
	
	/**
	 * Lay the words out relative to each other, without overlap.
	 */
	private void layoutWords() {
		Point2D.Double center = new Point2D.Double(0,0);
		Word first = words.get(0);
		for(int i = 1; i < words.size(); ++i) {
			Word current = words.get(i);
			center.x = 0;
			center.y = 0;
			double totalWeight = 0.0;
			for(int prev = 0; prev < i; ++prev) {
				Word wPrev = words.get(prev);
				center.x += (wPrev.getBounds().getCenterX()) * wPrev.getWeight();
				center.y += (wPrev.getBounds().getCenterY()) * wPrev.getWeight();
				totalWeight += wPrev.getWeight();
			}
			center.x /= totalWeight;
			center.y /= totalWeight;
			
			boolean done = false;
			double radius = 0.5 * Math.min(first.getBounds().getWidth(), first.getBounds().getHeight());

			while(!done) {
				int startDeg = rand.nextInt(360);
				//loop over spiral
				int prev_x = -1;
				int prev_y =- 1;
				for(int deg = startDeg; deg < startDeg + 360; deg += dDeg) {
					double rad = ((double)deg/Math.PI) * 180.0;
					int cx = (int)(center.x + radius * Math.cos(rad));
					int cy = (int)(center.y + radius * Math.sin(rad));
					if (prev_x == cx && prev_y == cy) continue;
					prev_x = cx;
					prev_y = cy;

					AffineTransform moveTo = AffineTransform.getTranslateInstance(cx, cy);
					Shape candidate = moveTo.createTransformedShape(current.getShape());
					Rectangle2D bound1 = new Rectangle2D.Double(current.getBounds().getX() + cx,
							current.getBounds().getY() + cy,
							current.getBounds().getWidth() + PADDING,
							current.getBounds().getHeight() + PADDING);
					//any collision ?
					int prev = 0;
					for (prev = 0; prev < i; ++prev) {
						if(bound1.intersects(words.get(prev).getBounds())) {
							break;
						}
					}
					//no collision: we're done
					if (prev == i) {
						current.setShape(candidate);
						done = true;
						break;
					}
				}
				radius += this.dRadius;
			}
		}
		
		double minX = Integer.MAX_VALUE;
		double minY = Integer.MAX_VALUE;
		double maxX =- Integer.MAX_VALUE;
		double maxY =- Integer.MAX_VALUE;
		
		for(Word word : words) {
			minX = Math.min(minX, word.getBounds().getMinX());
			minY = Math.min(minY, word.getBounds().getMinY());
			maxX = Math.max(maxX, word.getBounds().getMaxX());
			maxY = Math.max(maxY, word.getBounds().getMaxY());
		}
		
		AffineTransform shiftTr = AffineTransform.getTranslateInstance(-minX, -minY);
		
		for(Word word : words) {
			word.setShape(shiftTr.createTransformedShape(word.getShape()));
			if (words.get(0) == word) {
				fill = new Color(255, 0, 0, 255);
			} 
			else {
				int adj = 255 / words.size();
				fill = new Color(fill.getRed()-adj, 0, fill.getBlue()+adj, fill.getAlpha()-(adj/2));
			} 
			word.setFill(fill);
		}		
		imageSize = new Rectangle2D.Double(0, 0, (maxX-minX + 5), (maxY - minY + 5));		
	}
	
	public void drawCloud() {
		double scaleFactor;
		if (1.0 * width / imageSize.getWidth() < 1.0 * height / imageSize.getHeight()) {
			scaleFactor = 1.0 * width / imageSize.getWidth();
		}
		else {
			scaleFactor = 1.0 * height / imageSize.getHeight();
		}
		scaleFactor *= 0.85;
		AffineTransform	scale = AffineTransform.getScaleInstance(scaleFactor, scaleFactor);
		image = new BufferedImage((int)(imageSize.getWidth() * scaleFactor), 
				(int)(imageSize.getHeight() * scaleFactor), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();
		g2d.setBackground(new Color(255,255,255, 255));
		g2d.clearRect(0, 0, width, height);

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setTransform(scale);
		for(Word w : this.words) {
			g2d.setColor(w.getFill());
			g2d.fill(w.getShape());
		}
		g2d.dispose();
	}
	
	public BufferedImage getImage() {		
		return image;
	}
}

	
	