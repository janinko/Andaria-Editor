package eu.janinko.andaria.editor.ui;

import eu.janinko.andaria.editor.components.*;
import eu.janinko.andaria.editor.misc.*;
import eu.janinko.andaria.ultimasdk.files.arts.Art;
import eu.janinko.andaria.ultimasdk.files.hues.Hue;
import eu.janinko.andaria.ultimasdk.graphics.Color;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

import static eu.janinko.andaria.ultimasdk.utils.Utils.chs;

/**
 * FXML Controller class
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class HueSceneController extends SceneController {

    @FXML
    private TextField nameField;
    @FXML
    private BindableSelection artSelection;
    @FXML
    private BindableSelection paperdollSelection;
    @FXML
    private BindableSelection animSelection;
    @FXML
    private ImageView colorStrip;
    @FXML
    private ArtView artCanvas;
    @FXML
    private GumpView paperdollCanvas;
    @FXML
    private AnimBox animBox;

    @FXML
    private UOColorPicker colorPicker;

    private IntegerProperty selectedColor = new SimpleIntegerProperty(0);

    private final WritableImage strip = new WritableImage(768, 56);


    private final DeferredAction redrawStrip = new DeferredAction(this::drawSelected);

    @FXML
    private void initialize() {
        colorStrip.setImage(strip);
        nameField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().getBytes(chs).length > 19) {
                return null;
            } else {
                return change;
            }
        }));

        artCanvas.imageIDProperty().bind(artSelection.selectedProperty().asObject());
        paperdollCanvas.imageIDProperty().bind(paperdollSelection.selectedProperty().asObject());
        paperdollCanvas.setAvailableRenderStyles(EnumSet.of(RenderStyle.BG_BLACK, RenderStyle.BG_WHITE, RenderStyle.BG_WHITESMOKE, RenderStyle.MALE_GUMP, RenderStyle.FEMALE_GUMP));
        paperdollCanvas.renderStyleProperty().set(RenderStyle.MALE_GUMP);
        animBox.animIDProperty().bind(animSelection.selectedProperty().asObject());
    }

    public void setup(Stage stage, Holder holder) {
        super.setup(stage, holder, holder.getHue());
        //holder.getItem().bind(idList.selectedItemProperty());
        artCanvas.setup(holder);
        paperdollCanvas.setup(holder);
        animBox.setup(holder);

        artSelection.setBindable(holder.getItem());
        paperdollSelection.setBindable(holder.getPaperdoll());
        animSelection.setBindable(holder.getAnimDef());

        selectedColor.addListener((p, o, n) -> {
            Hue hue = holder.hue();
            System.out.println("SelectedColor changed " + hue + " [" + n+ "]");
            if(hue != null) {
                colorPicker.setColor(hue.getColor(n.intValue()));
                redrawStrip.invalidate();
            }
        });

    }

    @Override
    protected void unselected() {
        selectedColor.set(0);
        colorPicker.setColor(Color.BLACK);
        nameField.setText("");
        redrawStrip.invalidate();
    }

    @Override
    protected void selectionChanged(Item item) {
        Hue hue = holder.hue();
        System.out.println("Hue updated " + item);
        selectedColor.set(0);
        colorPicker.setColor(hue.getColor(0));
        nameField.setText(hue.getName());
        redrawStrip.invalidate();
    }

    @Override
    protected void onFileChange(FilesHolder newValue) {
    }

    public void drawSelected(){
        PixelWriter pw = strip.getPixelWriter();

        Hue hue = holder.hue();
        if (hue == null) {
            for (int x = 0; x < 768; x++) {
                for (int y = 0; y < 56; y++) {
                    pw.setColor(x, y, javafx.scene.paint.Color.TRANSPARENT);
                }
            }
        } else {
            int xOffset = 0;
            Color[] colors = hue.getColors();
            for (int i = 0; i < colors.length; i++) {
                Color clr = colors[i];
                var color = javafx.scene.paint.Color.rgb(clr.getRed(), clr.getGreen(), clr.getBlue());

                for (int x = 0; x < 24; x++) {
                    javafx.scene.paint.Color markerColorTop = javafx.scene.paint.Color.TRANSPARENT;
                    javafx.scene.paint.Color markerColorBot = javafx.scene.paint.Color.TRANSPARENT;
                    if (x == 0 || x == 23) {
                        markerColorTop = javafx.scene.paint.Color.WHITE;
                        markerColorBot = javafx.scene.paint.Color.BLACK;
                    } else if (selectedColor.get() == i) {
                        markerColorTop = javafx.scene.paint.Color.WHITE;
                        markerColorBot = javafx.scene.paint.Color.BLACK;
                    }
                    for (int y = 0; y < 4; y++) {
                        pw.setColor(xOffset + x, y, markerColorTop);
                    }
                    for (int y = 4; y < 52; y++) {
                        pw.setColor(xOffset + x, y, color);
                    }
                    for (int y = 52; y < 56; y++) {
                        pw.setColor(xOffset + x, y, markerColorBot);
                    }
                }
                xOffset += 24;
            }
        }
    }

    @FXML
    private void stripClicked(MouseEvent e) {
        int num = (int) (32 * e.getX() / colorStrip.getFitWidth());
        System.out.println("clicked strip " + e.getX() + " -> " + num);
        selectedColor.set(num);
    }

    @FXML
    private void apply(ActionEvent actionEvent) {
        Hue hue = holder.hue();
        if(hue == null){
            return;
        }

        hue.getColors()[selectedColor.get()] = colorPicker.color().getValue();
        redrawStrip.invalidate();
        artCanvas.invalidate();
        paperdollCanvas.invalidate();
        animBox.invalidate();
    }
}
