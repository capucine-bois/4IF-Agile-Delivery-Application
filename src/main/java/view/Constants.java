package view;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;

public class Constants {

    public static final Color COLOR_1 = new Color(241, 241, 241); // App background
    public static final Color COLOR_2 = new Color(215, 215, 215); // Buttons background
    public static final Color COLOR_3 = new Color(65, 65, 65); // Text color
    public static final Color COLOR_4 = new Color(228, 228, 228); // Textual view background
    public static final Color COLOR_5 = new Color(238, 240, 242); // Graphical view background

    public static Font getFont(String fileName, int size) throws IOException, FontFormatException {
        return Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("src/main/resources/fonts/" + fileName)).deriveFont(Font.PLAIN, size);
    }
}