package mosaic;

import java.awt.image.BufferedImage;

//a UserImage has a name and its associated image
public record UserImage(String name, BufferedImage image) {
	
	//each pixel get the avgRGBValue
	//get shared common color
	//put togethor
}
