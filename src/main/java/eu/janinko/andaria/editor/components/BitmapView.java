package eu.janinko.andaria.editor.components;

import eu.janinko.andaria.editor.misc.*;
import eu.janinko.andaria.ultimasdk.files.hues.Hue;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import lombok.Setter;

import java.util.EnumSet;
import java.util.Iterator;

public abstract class BitmapView extends StackPane {
    private static final Image BLANK = new Image("img/blank_image.svg", 48, 48, true, true);
    private static final Image RS = new Image("img/bgcolor.png");

    protected final ImageView imageView = new ImageView();
    protected final Button renderStyleButton = new Button();

    protected final ObjectProperty<FilesHolder> filesHolder = new SimpleObjectProperty<>();
    protected final ObjectProperty<Integer> imageID = new SimpleObjectProperty<>();

    protected final Painter painter = new Painter();
    protected Observable redrawTrigger = new SimpleBooleanProperty();


    @Setter
    private EnumSet<RenderStyle> availableRenderStyles;

    public BitmapView(EnumSet<RenderStyle> availableRenderStyles) {
        this.availableRenderStyles = availableRenderStyles;
        setAlignment(Pos.TOP_LEFT);
        getChildren().add(imageView);
        getChildren().add(renderStyleButton);
        renderStyleButton.setGraphic(new ImageView(RS));
        renderStyleButton.setVisible(false);
        renderStyleButton.setOnAction((e) -> switchRenderStyle());
        StackPane.setMargin(renderStyleButton, new Insets(3));
        setOnMouseEntered((e) -> renderStyleButton.setVisible(true));
        setOnMouseExited((e) -> renderStyleButton.setVisible(false));


        painter.hueProperty().addListener((o) -> invalidate());
        painter.partialHueProperty().addListener((o) -> invalidate());
        painter.renderStyleProperty().addListener((o) -> invalidate());
    }

    private void switchRenderStyle() {
        RenderStyle current = painter.renderStyleProperty().get();
        Iterator<RenderStyle> iterator = availableRenderStyles.iterator();
        while (iterator.hasNext()) {
            RenderStyle next = iterator.next();
            if (next == current) {
                if (iterator.hasNext()) {
                    painter.renderStyleProperty().set(iterator.next());
                    return;
                }
            }
        }
        painter.renderStyleProperty().set(availableRenderStyles.iterator().next());
    }

    public void setup(Holder holder) {
        filesHolder.bind(holder.getFiles());
        painter.hueProperty().bind(Bindings.createObjectBinding(holder::hue, holder.getHue()));
        painter.partialHueProperty().bind(holder.getPartial());
    }

    public int getImageID() {
        return imageID.get();
    }

    public void setImageID(Integer id) {
        this.imageID.set(id);
    }

    public ObjectProperty<Integer> imageIDProperty() {
        return imageID;
    }

    public ObjectProperty<Hue> hueProperty() {
        return painter.hueProperty();
    }

    public BooleanProperty partialHueProperty() {
        return painter.partialHueProperty();
    }

    public ObjectProperty<RenderStyle> renderStyleProperty() {
        return painter.renderStyleProperty();
    }

    public abstract void invalidate();

    public void redraw(){
        BooleanProperty redraw = (BooleanProperty) redrawTrigger;
        redraw.set(!redraw.get());
    }

    protected void blank(){
        imageView.setImage(BLANK);
    }

}
