package org.ucanask

import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.*
import com.google.zxing.common.*
import com.google.zxing.*

import java.awt.image.BufferedImage

import javax.imageio.ImageIO


class QrCodeService {
	
	/**
	 * Generate QRCode to be used to log in to UCanAsk
	 * @author Steve Dunford
	 */
	class QrCode {
		
		BitMatrix matrix // The QR Code
		ByteArrayOutputStream baos // The QR Code image
		String data // The text in the QR Code
		int width  = 1000 // QR Code image dimensions
		int height = 1000
		def baseUrl= ""
		def charset = "ISO-8859-1"
		
		
		def generateCode() {
			baos = new ByteArrayOutputStream()
			def hints = new Hashtable<EncodeHintType, String>
					([(EncodeHintType.CHARACTER_SET): charset])
			matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset), charset), 
					BarcodeFormat.QR_CODE, width, height, hints)
			BufferedImage img = MatrixToImageWriter.toBufferedImage(matrix);
			ImageIO.write(img, "PNG", baos)
			return baos.toByteArray()
		}
	}
}