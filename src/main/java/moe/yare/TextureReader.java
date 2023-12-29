package moe.yare;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TextureReader {

    public static Texture loadTexture(String filepath) {
        try {
            BufferedImage image = ImageIO.read(new File(filepath));
            int[] rgb = new int[image.getWidth() * image.getHeight()];
            image.getRGB(0, 0, image.getWidth(), image.getHeight(), rgb, 0, image.getWidth());

            return new Texture(image.getWidth(), image.getHeight(), rgb);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
