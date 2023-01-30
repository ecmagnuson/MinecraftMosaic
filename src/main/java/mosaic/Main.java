package mosaic;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class Main {

	// return a List of all Block objects in /blocks dir
	public static List<Block> getBlocks() throws IOException {
		var blockFile = new File(System.getProperty("user.dir") + "/blocks/");
		var blocks = new ArrayList<Block>();
		for (File block : blockFile.listFiles()) {
			blocks.add(new Block(block.getAbsoluteFile()));
		}
		return blocks;
	}

	// return a List of all UserImage objects in /inputimages dir
	public static List<UserImage> getImages() throws IOException {
		var imageFile = new File(System.getProperty("user.dir") + "/inputimages/");
		var images = new ArrayList<UserImage>();
		for (File f : imageFile.listFiles()) {
			if (f.getName().equals(".gitignore")) {
				continue;
			}
			var name = f.getName();
			var image = ImageIO.read(f);
			images.add(new UserImage(name, image));
		}
		return images;
	}

	// get the difference in RGB values for a Color
	public static int calculateDiff2Colors(Color color1, Color color2) {
		int r1 = color1.getRed();
		int g1 = color1.getGreen();
		int b1 = color1.getBlue();
		int r2 = color2.getRed();
		int g2 = color2.getGreen();
		int b2 = color2.getBlue();
		return (int) Math.sqrt(Math.pow((r1 - r2), 2) + Math.pow((g1 - g2), 2) + Math.pow((b1 - b2), 2));
	}

	// return the Color of the Block that has the closest RGB value to a Color pixel
	public static Color matchPixelToBlock(Color pixel, List<Block> blocks) {
		var match = new Color(0, 0, 0, 255);
		int minDist = calculateDiff2Colors(pixel, match);
		// for each block see how close it is to this color, return that color
		for (var block : blocks) {
			int dist = calculateDiff2Colors(pixel, block.getAvgRGBColors());
			if (dist < minDist) {
				minDist = dist;
				match = block.getAvgRGBColors();
			}
		}
		return match;
	}

	// Create a white BufferedImage with a specified width and height,
	public static BufferedImage createCanvas(int width, int height) throws IOException {
		var image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = image.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		g.dispose();
		return image;
	}

	// save the newly created mosaic to the /output directory
	public static void saveMinecraftImage(BufferedImage minecraftMosaic, String name) throws IOException {
		Files.createDirectories(Paths.get(System.getProperty("user.dir") + "/output"));
		var output = new File(System.getProperty("user.dir") + "/output/" + "Minecraft" + name);
		ImageIO.write(minecraftMosaic, "png", output);
	}

	public static void transform(List<UserImage> userImages, List<Block> blocks) throws IOException {
		for (var ui : userImages) {
			BufferedImage minecraftMosaic = createCanvas(16 * ui.image().getWidth(), 16 * ui.image().getHeight());
			for (int x = 0; x < ui.image().getWidth(); x++) {
				for (int y = 0; y < ui.image().getHeight(); y++) {
					int pixel = ui.image().getRGB(x, y);
					var pixelColor = new Color(pixel, true);
					// Find the color that matches a Block value that is closest to the image Pixel
					Color match = matchPixelToBlock(pixelColor, blocks);
					var blockImage = Block.color2image.get(match);
					if (blockImage == null) {
						// could def make this better
						// Color of obsidian.png is r=24,g=20,b=35
						blockImage = Block.color2image.get(new Color(24, 20, 35));
					}
					var blockToPaste = (Graphics2D) minecraftMosaic.getGraphics();
					// blocks are 16 x 16 images
					blockToPaste.drawImage(blockImage, null, x * 16, y * 16);
				}
			}
			saveMinecraftImage(minecraftMosaic, ui.name());
		}
	}

	public static void main(String[] args) throws IOException {
		List<UserImage> images = getImages();
		List<Block> blocks = getBlocks();
		transform(images, blocks);

		System.out.println("Done :)");
		System.out.printf("all images (%d) are in /output", images.size());
	}
}
