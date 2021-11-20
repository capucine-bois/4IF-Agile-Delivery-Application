package view;

import java.awt.*;
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
    public static final Color COLOR_5 = new Color(238, 240, 242); // Graphical view background

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
}