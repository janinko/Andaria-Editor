package eu.janinko.andaria.editor.ui;

import eu.janinko.andaria.editor.components.Item;
import eu.janinko.andaria.editor.misc.DeferredAction;
import eu.janinko.andaria.editor.misc.FilesHolder;
import eu.janinko.andaria.editor.misc.Holder;
import eu.janinko.andaria.editor.misc.RenderStyle;
import eu.janinko.andaria.ultimasdk.graphics.Bitmap;
import eu.janinko.andaria.ultimasdk.graphics.Image;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public abstract class SceneController {

    protected Logger log = Logger.getLogger(getClass().getName());

    protected static File lastSelectDir;
    private static File lastOutDir;

    protected Stage stage;
    protected Holder holder;

    protected void setup(Stage stage, Holder holder, ObservableValue<? extends Item> selectedItem) {
        this.stage = stage;
        this.holder = holder;

        selectedItem.addListener((p, o, n) -> {
            if (n == null) {
                unselected();
            } else {
                selectionChanged(n);
            }
        });

        holder.getFiles().addListener((_x, _y, newValue) -> {
            if (newValue != null) {
                onFileChange(newValue);
            }
        });

    }

    protected abstract void unselected();

    protected abstract void selectionChanged(Item item);

    protected abstract void onFileChange(FilesHolder newValue);

    protected BufferedImage loadFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Soubor obrázku");
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("PNG obrázek (*.png)", "*.png"));
        if (lastSelectDir != null) fileChooser.setInitialDirectory(lastSelectDir);

        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return null;
        } else {
            BufferedImage img = ImageIO.read(file);
            lastSelectDir = file.getParentFile();
            return img;
        }
    }

    protected void exportFile(RenderedImage image, int id) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Soubor obrázku");
        fileChooser.setInitialFileName(id + ".png");
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("PNG obrázek (*.png)", "*.png"));
        if (lastOutDir != null) fileChooser.setInitialDirectory(lastOutDir);

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            lastOutDir = file.getParentFile();
            ImageIO.write(image, "png", file);
        }
    }

    protected void error(IOException ex) {
        log.log(Level.SEVERE, null, ex);
        Alert alert = new Alert(Alert.AlertType.ERROR, "Error: " + ex.getLocalizedMessage(), ButtonType.OK);
        alert.show();
    }

}
