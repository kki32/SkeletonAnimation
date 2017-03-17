package org.ucanask.chart

import static org.junit.Assert.*
import grails.test.mixin.*
import grails.test.mixin.support.*

import java.awt.image.BufferedImage

import org.junit.*

/**
 * Unable to test that what is displayed in the cloud is as intended, but
 * can check that the image dimensions are as they should be 
 */
@TestMixin(GrailsUnitTestMixin)
class WordCloudTests {

	/**
	 * Creating a wc with an empty list, then calling getImage should return a 1x1 image
	 */
    void testEmptyList() {
        WordCloud wc = new WordCloud([])
		Object img = wc.getImage()
		assert img instanceof BufferedImage
		assert img.getHeight() == 1 && img.getWidth() == 1
    }	
}