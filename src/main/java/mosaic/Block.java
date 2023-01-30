package mosaic;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import javax.imageio.ImageIO;

public class Block {

	private File path; // path of Block
	private String name;
	private BufferedImage image;
	private Color avgRGBColors; // average of all of the RGB values for each pixel in Block
	private int count;

	protected final static Map<Block, Color> block2Color = new HashMap<>();
	protected final static Map<Color, BufferedImage> color2image = new HashMap<>();
	// TreeMap<>();

	public Block(File path) throws IOException {
		this.path = path;
		this.name = path.getName();
		this.image = ImageIO.read(this.path);
		this.avgRGBColors = this.calculateAvgRGBColors(this.image);

		color2image.put(this.avgRGBColors, this.image);
		// block2Color.put(this, this.avgRGBColors);
		// counts.put(this, counts.get(this) + 1);
	}

	// Get the average R,G,B integer value for all pixels in each Block
	public Color calculateAvgRGBColors(BufferedImage image) {
		int r = 0;
		int g = 0;
		int b = 0;

		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int pixel = image.getRGB(x, y);
				var pixelColor = new Color(pixel, true);
				// https://stackoverflow.com/questions/12408431/how-can-i-get-the-average-color-of-an-image
				// https://stackoverflow.com/questions/649454/what-is-the-best-way-to-average-two-colors-that-define-a-linear-gradient
				int tempR = (int) Math.pow(pixelColor.getRed(), 2);
				int tempG = (int) Math.pow(pixelColor.getGreen(), 2);
				int tempB = (int) Math.pow(pixelColor.getBlue(), 2);
				r += tempR;
				g += tempG;
				b += tempB;
			}
		}
		var imageSize = image.getHeight() * image.getWidth();
		var avgR = (int) Math.sqrt(r / imageSize);
		var avgG = (int) Math.sqrt(g / imageSize);
		var avgB = (int) Math.sqrt(b / imageSize);
		return new Color(avgR, avgG, avgB);
	}

	public BufferedImage getImage() {
		return this.image;
	}

	public Color getAvgRGBColors() {
		return this.avgRGBColors;
	}

	public void incrementCount(String name) {
		this.count++;
	}

}
