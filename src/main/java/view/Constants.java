package view;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Constants for GUI.
 */
public class Constants {

    // COLORS
    public static final Color COLOR_1 = new Color(241, 241, 241); // App background
    public static final Color COLOR_2 = new Color(215, 215, 215); // Buttons background
    public static final Color COLOR_3 = new Color(65, 65, 65); // Text color
    public static final Color COLOR_4 = new Color(228, 228, 228); // Textual view background
    public static final Color COLOR_5 = new Color(244, 246, 247); // Graphical view background
    public static final Color COLOR_6 = new Color(220, 222, 226); // Segments stroke
    public static final Color COLOR_7 = new Color(255, 255, 255); // Segments background

    /**
     * Get Font instance for a given font name and a given font size
     * @param fileName name of the font file
     * @param size font size
     * @return instance of desired font
     * @throws IOException raised if font file can't be found
     * @throws FontFormatException raised if font can't be loaded
     */
    public static Font getFont(String fileName, int size) throws IOException, FontFormatException {
        return Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("src/main/resources/fonts/" + fileName)).deriveFont(Font.PLAIN, size);
    }

    /**
     * Get image for a given image name
     * @param filename name of the image file
     * @return buffered image linked to the image
     * @throws IOException raised if the file can not be read
     */
    public static BufferedImage getImage(String filename) throws IOException {
        return ImageIO.read(new File("src/main/resources/img/" + filename));
    }
}