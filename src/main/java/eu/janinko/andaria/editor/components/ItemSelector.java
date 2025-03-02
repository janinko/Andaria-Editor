package eu.janinko.andaria.editor.components;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.Value;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ItemSelector<I extends Item> extends VBox {
    @FXML
    private ListView<I> listView;
    @FXML
    private TextField searchBar;
    @FXML
    private ToggleButton hideMissing;
    @FXML
    private ToggleButton hideExisting;
    private final ObservableList<I> items = FXCollections.observableArrayList();
    private static final PseudoClass ID_MISSING = PseudoClass.getPseudoClass("idMissing");

    private I selectedItem;

    public ItemSelector() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ItemSelector.fxml"));
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
        listView.setCellFactory(lv -> new ItemCell<>());


        FilteredList<I> filteredNames = new FilteredList<>(items);
        filteredNames.predicateProperty().bind(Bindings.createObjectBinding(
                this::getIDSelectionPredicate,
                hideMissing.selectedProperty(),
                hideExisting.selectedProperty(),
                searchBar.textProperty()));
        listView.setItems(filteredNames);
        listView.itemsProperty().addListener((p, o, n) -> {
            System.out.println("listView.itemsProperty() ");

            if (selectedItem != null && filteredNames.contains(selectedItem)) {
                listView.getSelectionModel().select(selectedItem);
            }

        });

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Selected " + oldValue + "->" + newValue);
            if (newValue == null) {
                if (selectedItem != null && filteredNames.contains(selectedItem)) {
                    listView.getSelectionModel().select(selectedItem);
                }
            } else {
                selectedItem = newValue;
            }
        });
    }

    public ReadOnlyObjectProperty<I> selectedItemProperty() {
        return listView.getSelectionModel().selectedItemProperty();
    }

    private Predicate<Item> getIDSelectionPredicate() {
        System.out.println("getIDSelectionPredicate");
        boolean hideMissingSelected = hideMissing.isSelected();
        boolean hideExistingSelected = hideExisting.isSelected();
        String searchText = searchBar.getText();

        Predicate<Item> predicate = _i -> true;

        if (!searchText.isEmpty()) {
            predicate = predicate.and(i -> i.getText().contains(searchText));
        }
        if (hideMissingSelected) {
            predicate = predicate.and(item -> item.isPresent());
        }
        if (hideExistingSelected) {
            predicate = predicate.and(item -> !item.isPresent());
        }
        return predicate;
    }

    public void setItems(List<I> newItems) {
        ObservableList<I> items1 = listView.getItems();
        items.clear();
        items.addAll(newItems);

        listView.getSelectionModel().select(0);
        listView.scrollTo(0);
    }

    private static class ItemCell<I extends Item> extends ListCell<I> {

        @Override
        protected void updateItem(I item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
                pseudoClassStateChanged(ID_MISSING, true);
            } else {
                setText(item.getText());
                if (item.isPresent()) {
                    pseudoClassStateChanged(ID_MISSING, false);
                } else {
                    pseudoClassStateChanged(ID_MISSING, true);
                }
            }
        }

    }

    @Value
    public static class DefaultItem implements Item {
        int id;
        boolean present;
        String text;

        public DefaultItem(int id, boolean present) {
            this.id = id;
            this.present = present;
            this.text = this.id + " (0" + Integer.toHexString(this.id) + ")";
        }
    }

    public void selectId(int id) {
        Optional<I> first = items.stream().filter(i -> i.getId() == id).findFirst();
        listView.getSelectionModel().select(first.orElse(null));
        listView.scrollTo(listView.selectionModelProperty().get().getSelectedIndex());
    }

    public void selectIdx(int index) {
        listView.getSelectionModel().select(index);
        listView.scrollTo(index);
    }

    public void hideMissing() {
        hideExisting.setSelected(false);
        hideMissing.setSelected(true);
    }

    public void updateItem(I item) {
        I selected = listView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        if (item.isPresent()) {
            hideExisting.setSelected(false);
        } else {
            hideMissing.setSelected(false);
        }
        selected.getId();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) == selected) {
                items.set(i, item);
                return;
            }
        }
    }
}
