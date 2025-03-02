package eu.janinko.andaria.editor.misc;

import eu.janinko.andaria.ultimasdk.files.anims.Frame;
import eu.janinko.andaria.ultimasdk.files.hues.Hue;
import eu.janinko.andaria.ultimasdk.graphics.Bitmap;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class Painter {
    private static final Image alignGrid = new Image("img/art_align_grid.png");
    private static final Image maleGump = new Image("img/male.png");
    private static final Image femaleGump = new Image("img/female.png");

    private final ObjectProperty<RenderStyle> renderStyle = new SimpleObjectProperty<>(RenderStyle.BG_BLACK);
    private final ObjectProperty<Hue> hue = new SimpleObjectProperty<>();
    private final BooleanProperty partialHue = new SimpleBooleanProperty(false);

    protected int minWidth = 200;
    protected int minHeight = 200;
    protected int padding = 4;

    public ObjectProperty<Hue> hueProperty() {
        return hue;
    }

    public BooleanProperty partialHueProperty() {
        return partialHue;
    }

    public ObjectProperty<RenderStyle> renderStyleProperty() {
        return renderStyle;
    }

    public Image drawFrame(Frame frame, int width, int biasUp, int biasDown) {
        Bitmap bitmap = frame.getBitmap();

        width += 2 * padding;
        if (width < minWidth) {
            width = minWidth;
        }
        biasUp += padding;
        if (biasDown < 21) {
            biasDown = 21;
        }
        if (biasUp < minHeight) {
            biasUp = minHeight;
        }
        int height = biasDown + biasUp;

        return getWritableImage(bitmap, width, height, frame.leftPadding(width), frame.topPadding(biasUp), biasUp);
    }

    public WritableImage drawBottomCenteredImage(Bitmap bitmap){
        int width = bitmap.getWidth() + 2 * padding;
        if (width < minWidth) {
            width = minWidth;
        }
        int height = bitmap.getHeight() + 2 * padding;
        if (height < minHeight) {
            height = minHeight;
        }
        int paddingLeft = width / 2 - bitmap.getWidth() / 2;
        int paddingTop = height - bitmap.getHeight();
        int biasUp = height - padding;

        return getWritableImage(bitmap, width, height, paddingLeft, paddingTop, biasUp);
    }

    public WritableImage drawTopLeftImage(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int biasUp = height - padding;

        return getWritableImage(bitmap, width, height, 0, 0, biasUp);
    }

    private WritableImage getWritableImage(Bitmap bitmap, int width, int height, int paddingLeft, int paddingTop, int biasUp) {
        bitmap.hue(hue.get(), partialHue.get());
        BitmapPixelReader bpr = new BitmapPixelReader(bitmap, width, height, paddingLeft, paddingTop);

        switch (renderStyle.getValue()) {
            case BG_WHITESMOKE -> bpr.setBackgroundColor(javafx.scene.paint.Color.WHITESMOKE);
            case BG_WHITE -> bpr.setBackgroundColor(javafx.scene.paint.Color.WHITE);
            case BG_BLACK -> bpr.setBackgroundColor(javafx.scene.paint.Color.BLACK);
            case ALIGN_GRID -> {
                int biasLeft = width / 2;
                bpr.setBackgroundImage(alignGrid, 242 - biasLeft, 462 - biasUp);
            }
            case FEMALE_GUMP -> bpr.setBackgroundImage(femaleGump, 0, 0);
            case MALE_GUMP -> bpr.setBackgroundImage(maleGump, 0, 0);
        }
        return new WritableImage(bpr, width, height);
    }
}
