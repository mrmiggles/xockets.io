package com.tc.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

public class ImageUtils {
	
	private static Logger LOG = Logger.getLogger(ImageUtils.class.getName());
	
	
	public static File resize(InputStream in, int width, int height) throws IOException{
		BufferedImage source = ImageIO.read(in);
		BufferedImage scaledImg = Scalr.resize(source, Method.QUALITY, height, width, Scalr.OP_ANTIALIAS);
		return writeAsJpeg(scaledImg, 100);
	}
	
	public static File resize(File file, int width, int height) {
		File resized = null;
		BufferedImage source = readFile(file);
		BufferedImage scaledImg = Scalr.resize(source, Method.QUALITY, height, width, Scalr.OP_ANTIALIAS);
		resized = writeAsJpeg(scaledImg, 100);
		return resized;
	}

	private static BufferedImage readFile(File file){
		BufferedImage source = null;
		try {
			source = ImageIO.read(file);
		} catch (IOException ex) {
			LOG.log(Level.SEVERE,null, ex);
		}
		return source;
	}

	public static File writeAsJpeg(BufferedImage image, int quality) {
		File targetFile = null;


		try {
			targetFile = File.createTempFile("image", ".jpg");
			BufferedImage newBufferedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
			newBufferedImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);

			// write to jpeg file
			ImageIO.write(newBufferedImage, "jpg", targetFile);

		} catch (Exception e) {
			LOG.log(Level.SEVERE,null, e);

		} finally {

		}
		return targetFile;
	}
	

	

}
