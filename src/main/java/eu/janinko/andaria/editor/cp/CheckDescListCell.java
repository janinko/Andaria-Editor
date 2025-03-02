package eu.janinko.andaria.editor.cp;

import eu.janinko.andaria.editor.cp.CheckDescListCell.CheckDescListItem;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import lombok.Data;

/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class CheckDescListCell extends ListCell<CheckDescListItem> {

    private final GridPane grid = new GridPane();

    private final CheckBox check = new CheckBox();
    private final Label label = new Label();
    private final Label description = new Label();

    public CheckDescListCell() {
        grid.setHgap(10);
        grid.setVgap(4);
        grid.setPadding(new Insets(0, 10, 0, 10));
        description.setFont(Font.font(10));

        grid.add(check, 0, 0, 1, 2);
        grid.add(label, 1, 0);
        grid.add(description, 1, 1);
    }

    @Override
    public void updateItem(CheckDescListItem item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            clearContent();
        } else {
            addContent(item);
        }
    }

    private void clearContent() {
        setText(null);
        setGraphic(null);
    }

    private void addContent(CheckDescListItem item) {
        setText(null);
        check.setSelected(item.selected);
        label.setText(item.label);
        description.setText(item.description);
        setGraphic(grid);
    }

    @Data
    public static class CheckDescListItem {
        private boolean selected;
        private String label;
        private String description;
    }
}
