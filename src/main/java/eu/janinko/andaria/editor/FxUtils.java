package eu.janinko.andaria.editor;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class FxUtils {

    /**
     * Overrides events to ignore selection events on already selected buttons to disable unselect.
     */
    public static void overrideSelectingEvents(ToggleGroup group) {
        group.getToggles().forEach(t -> FxUtils.overrideSelectingEvents((ToggleButton) t));
    }

    private static void overrideSelectingEvents(ToggleButton button) {
        button.addEventFilter(KeyEvent.ANY, (e) -> {
            if (e.getCode().equals(KeyCode.SPACE) || e.getCode().equals(KeyCode.ENTER) || button.isSelected()) {
                e.consume();
            }
        });
        button.addEventFilter(MouseEvent.ANY, (e) -> {
            if (button.isSelected()) {
                e.consume();
            }
        });
    }
}
