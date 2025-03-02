package eu.janinko.andaria.editor.components;

import eu.janinko.andaria.editor.misc.RenderStyle;
import javafx.beans.DefaultProperty;
import javafx.beans.property.*;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

@DefaultProperty("mainImage")
public class BackgroundedImageView extends StackPane {
    private final ObjectProperty<ImageView> mainImageProperty = new SimpleObjectProperty<>();
    private static Image alignGrid = new Image("img/art_align_grid.png");
    private static Image maleGump = new Image("img/male.png");
    private static Image femaleGump = new Image("img/female.png");
    private ImageView bgImageView = new ImageView(alignGrid);
    private ObjectProperty<Image> bgImage = bgImageView.imageProperty();

    private final DoubleProperty width = new SimpleDoubleProperty(1);
    private final DoubleProperty height = new SimpleDoubleProperty(1);

    private final double minWidth = 48;
    private final double minHeight = 48;
    private final double padding = 4;

    public BackgroundedImageView() {
        setAlignment(Pos.CENTER);
        getChildren().add(bgImageView);
        mainImageProperty.addListener((p, o, n) -> {
            if (o != null) {
                getChildren().remove(o);
            }
            if (n != null) {
                setImage(n);
            }
        });

        //bgImageView.setSmooth(false);
        bgImageView.setPreserveRatio(false);

        width.addListener((i) -> resize());
        height.addListener((i) -> resize());
    }

    public void setRenderStyle(RenderStyle renderStyle){
        switch (renderStyle){
            case BG_WHITESMOKE -> bgImage.set(generateImage(Color.WHITESMOKE));
            case BG_WHITE -> bgImage.set(generateImage(Color.WHITE));
            case BG_BLACK -> bgImage.set(generateImage(Color.BLACK));
            case ALIGN_GRID -> bgImage.set(alignGrid);
            case MALE_GUMP -> bgImage.set(maleGump);
            case FEMALE_GUMP -> bgImage.set(femaleGump);
        }
    }

    public ImageView getMainImage() {
        return mainImageProperty.get();
    }

    public void setMainImage(ImageView image) {
        mainImageProperty.set(image);
    }

    public ObjectProperty<ImageView> mainImageProperty() {
        return mainImageProperty;
    }

    private void resize() {
        double newWidth = Math.max(minWidth, width.get() + padding * 2);
        double newHeight = Math.max(minHeight, height.get() + padding * 2);

        bgImageView.setFitWidth(newWidth);
        bgImageView.setFitHeight(newHeight);
    }

    private void setImage(ImageView n) {
        getChildren().add(n);
        n.imageProperty().addListener((p, o, nn) -> {
            if (nn == null) {
                width.set(1);
                height.set(1);
            } else {
                width.set(nn.getWidth());
                height.set(nn.getHeight());
            }
        });
    }

    private static Image generateImage(Color color) {
        WritableImage img = new WritableImage(2, 2);
        PixelWriter pw = img.getPixelWriter();

        pw.setColor(0, 0, color);
        pw.setColor(1, 0, color);
        pw.setColor(0, 1, color);
        pw.setColor(1, 1, color);
        return img;
    }
}
