package eu.janinko.andaria.editor.components;

import eu.janinko.andaria.editor.misc.Holder;
import eu.janinko.andaria.ultimasdk.files.Anims;
import eu.janinko.andaria.ultimasdk.files.anims.Body;
import eu.janinko.andaria.ultimasdk.files.hues.Hue;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class AnimBox extends HBox {

    @FXML
    private ToggleButton animateButton;
    @FXML
    private ChoiceBox<Body.Action> actionChoice;
    @FXML
    private ChoiceBox<Body.Direction> directionChoice;
    @FXML
    private AnimView canvas;

    public AnimBox() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AnimBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private void initialize() {
        canvas.playingProperty().bind(animateButton.selectedProperty());

        directionChoice.getItems().addAll(Body.Direction.values());
        directionChoice.getSelectionModel().select(Body.Direction.LEFT);
        canvas.directionProperty().bind(directionChoice.getSelectionModel().selectedItemProperty());

        actionChoice.setItems(canvas.availableActions());
        canvas.availableActions().addListener((ListChangeListener.Change<? extends Body.Action> c) ->
                actionChoice.getSelectionModel().select(0));
        canvas.actionProperty().bind(actionChoice.getSelectionModel().selectedItemProperty());
    }

    public void setup(Holder holder) {
        canvas.setup(holder);
    }

    public void setup(Anims.AnimFile animFile, Holder holder) {
        canvas.setAnimFile(animFile);
        canvas.setup(holder);
    }

    public int getAnimID() {
        return canvas.getImageID();
    }

    public void setAnimID(Integer id) {
        canvas.setImageID(id);
    }

    public ObjectProperty<Integer> animIDProperty() {
        return canvas.imageIDProperty();
    }

    public void invalidate() {
        canvas.invalidate();
    }

    public void redraw(){
        canvas.redraw();
    }

    public void setHue(Hue hue){
        canvas.partialHueProperty().unbind();
        canvas.partialHueProperty().set(false);
        canvas.hueProperty().unbind();
        canvas.hueProperty().set(hue);
    }
}
