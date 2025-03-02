package eu.janinko.andaria.editor.components;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class BindableSelection extends HBox {
    private static Image bindedIcon = new Image("img/binded.png");
    private static Image unbindedIcon = new Image("img/unbinded.png");

    @FXML
    private ToggleButton bindButton;

    @FXML
    private ImageView bindIcon;

    @FXML
    private TextField selectionField;

    private ObjectProperty<? extends Item> bindable = null;

    private final IntegerProperty selected = new SimpleIntegerProperty();

    public BindableSelection() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("BindableSelection.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        selectionField.onActionProperty();
    }

    public void setBindable(ObjectProperty<? extends Item> bindable){
        this.bindable = bindable;
        bind();
    }

    public IntegerProperty selectedProperty() {
        return selected;
    }

    private void bind() {
        System.out.println("Binding " + bindable);
        if(bindable == null){
            return;
        }
        StringBinding stringBinding = Bindings.createStringBinding(() -> bindable.get() == null ? "" : bindable.get().getText(), bindable);
        selectionField.textProperty().bind(stringBinding);

        IntegerBinding integerBinding = Bindings.createIntegerBinding(() -> bindable.get() == null ? 0 : bindable.get().getId(), bindable);
        selected.bind(integerBinding);

        bindIcon.setImage(bindedIcon);
        selectionField.setDisable(true);
        bindButton.setSelected(true);
    }

    private void unbind() {
        System.out.println("Unbinding " + bindable);
        selected.unbind();
        selectionField.textProperty().unbind();
        selectionField.setText(Integer.toString(selected.get()));
        bindIcon.setImage(unbindedIcon);
        selectionField.setDisable(false);
        bindButton.setSelected(false);
    }

    @FXML
    private void selectionEntered() {
        if (bindButton.isSelected()) {
            return;
        }
        String text = selectionField.getText().trim();

        int selectedNumber;
        try {
            if (text.startsWith("0x")) {
                selectedNumber = Integer.parseInt(text.substring(2), 16);
            } else if (text.startsWith("0")) {
                selectedNumber = Integer.parseInt(text, 16);
            } else {
                selectedNumber = Integer.parseInt(text, 10);
            }
        } catch (NumberFormatException ex) {
            return;
        }
        selected.set(selectedNumber);
    }

    @FXML
    private void popupPressed() {

    }

    @FXML
    private void bindPressed() {
        System.out.println("bindPressed");
        if (bindable != null && bindButton.isSelected()) {
            bind();
        } else {
            unbind();
        }
    }
}
