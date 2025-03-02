package eu.janinko.andaria.editor.windows;

import eu.janinko.andaria.ultimasdk.files.Hues;
import eu.janinko.andaria.ultimasdk.files.hues.Hue;
import eu.janinko.andaria.ultimasdk.graphics.Color;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class HueSelectorPopup extends Stage {

    private final Hues hues;
    @FXML
    public TextField filterField;

    @FXML
    public ImageView canvas;
    @FXML
    public ImageView selectedStrip;

    @FXML
    public TextField idField;
    @FXML
    public Label nameLabel;

    @FXML
    public Button selectButton;

    private final Tooltip tooltip = new Tooltip("Toto je tooltip");

    public ObjectProperty<Hue> selected = new SimpleObjectProperty<>();

    private final WritableImage img = new WritableImage(1600, 960);
    private final WritableImage strip = new WritableImage(512, 32);

    public Hue selectedHue(){
        return selected.get();
    }


    public HueSelectorPopup(Hues hues, Integer preselected, Window owner) {
        this.hues = hues;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("HueSelectorPopup.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();

            initModality(Modality.WINDOW_MODAL);
            initOwner(owner);

            canvas.setImage(img);
            selectedStrip.setImage(strip);

            canvas.setOnMouseExited(e -> tooltip.hide());

            draw();
            drawSelected();

            filterField.textProperty().addListener((x) -> draw());
            selected.addListener((x) -> drawSelected());

            idField.textProperty().addListener(this::idChanged);
            selectButton.disableProperty().bind(selected.isNull());
            if (preselected != null) {
                idField.setText(Integer.toString(preselected));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onMouseMove(MouseEvent e){
        int x = (int) (e.getX() / 32);
        int y = (int) (e.getY() / 16);
        int SKIPPED = 1;
        int id = x * 60 + y + SKIPPED + 1;
        if (!validId(id)) {
            tooltip.hide();
            return;
        }
        Hue hue = hues.get(id);

        String text = filterField.getText().trim().toLowerCase();
        boolean keep = shouldKeep(hue, text);
        if (keep) {
            tooltip.setText("Hue: " + id + " " + hue.getName());
            tooltip.show(canvas, e.getScreenX() + 20, e.getScreenY());
        } else {
            tooltip.hide();
        }
    }

    @FXML
    private void onMouseClicked(MouseEvent e) {
        int x = (int) (e.getX() / 32);
        int y = (int) (e.getY() / 16);
        int SKIPPED = 1;
        int id = x * 60 + y + SKIPPED + 1;
        if (!validId(id)) {
            return;
        }
        idField.setText(Integer.toString(id));
    }

    public void drawSelected(){
        PixelWriter pw = strip.getPixelWriter();
        Hue hue = selected.get();
        if (hue == null) {
            nameLabel.setText("");
            for (int x = 0; x < 512; x++) {
                for (int y = 0; y < 32; y++) {
                    pw.setColor(x, y, javafx.scene.paint.Color.TRANSPARENT);
                }
            }
        } else {
            nameLabel.setText(hue.getName());
            int xOffset = 0;
            for (Color clr : hue.getColors()) {
                var color = javafx.scene.paint.Color.rgb(clr.getRed(), clr.getGreen(), clr.getBlue());

                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 32; y++) {
                        pw.setColor(xOffset + x, y, color);
                    }
                }
                xOffset += 16;
            }
        }
    }

    public void draw() {
        int imageHeight = 60 * 16;

        PixelWriter pw = img.getPixelWriter();

        int xOffset = 0;
        int yOffset = 0;

        String text = filterField.getText().trim().toLowerCase();
        for (Hue hue : hues.getHues()) {
            if (hue.getId() == 1 || hue.getId() == 3000) {
                continue;
            }
            boolean keep = shouldKeep(hue, text);
            Color[] colors = hue.getColors();

            for (int i = 0; i < 32; i++) {
                Color clr = colors[i];

                javafx.scene.paint.Color color;
                if(keep){
                    color = javafx.scene.paint.Color.rgb(clr.getRed(), clr.getGreen(), clr.getBlue());
                } else {
                    color = javafx.scene.paint.Color.GREY;
                }

                for (int j = 0; j < 16; j++) {
                    pw.setColor(xOffset + i, yOffset + j, color);
                }
            }

            yOffset += 16;
            if (yOffset >= imageHeight) {
                yOffset = 0;
                xOffset += 32;
            }
        }
    }

    private static boolean shouldKeep(Hue hue, String text) {
        return text.isBlank() || hue.getName().toLowerCase().contains(text);
    }

    private void idChanged(ObservableValue<? extends String> p, String o, String n) {
        if (n == null || n.isBlank()) {
            selected.setValue(null);
            return;
        }
        int id;
        try {
            id = Integer.parseInt(n);
        } catch (NumberFormatException ex) {
            selected.setValue(null);
            return;
        }
        if (!validId(id)) {
            selected.setValue(null);
            return;
        }
        selected.setValue(hues.get(id));
    }

    private static boolean validId(int id) {
        return 2 <= id && id <= 2999;
    }

    @FXML
    private void deselect(){
        selected.set(null);
        close();
    }
}
