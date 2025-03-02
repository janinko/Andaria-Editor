package eu.janinko.andaria.editor.misc;

import eu.janinko.andaria.ultimasdk.graphics.Color;

public class HueHelper {
    public static javafx.scene.paint.Color toPaint(Color color) {
        double red = color.getRed() / 255.0;
        double green = color.getGreen() / 255.0;
        double blue = color.getBlue() / 255.0;
        return javafx.scene.paint.Color.color(red, green, blue);
    }
}
