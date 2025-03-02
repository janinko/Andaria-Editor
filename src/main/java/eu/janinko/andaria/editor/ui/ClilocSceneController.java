package eu.janinko.andaria.editor.ui;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import eu.janinko.andaria.editor.components.Item;
import eu.janinko.andaria.editor.components.TextAreaTableCell;
import eu.janinko.andaria.editor.misc.FilesHolder;
import eu.janinko.andaria.editor.misc.Holder;
import eu.janinko.andaria.ultimasdk.files.CliLocs;
import eu.janinko.andaria.ultimasdk.files.cliloc.CliLoc;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Predicate;

/**
 * FXML Controller class
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class ClilocSceneController extends SceneController {

    @FXML
    private TextField idTextField;
    @FXML
    private TextField searchBar;
    @FXML
    private TableColumn<CliLoc, Integer> idColumn;
    @FXML
    private TableColumn<CliLoc, Byte> flagColumn;
    @FXML
    private TableColumn<CliLoc, String> textColumn;
    @FXML
    private TableView<CliLoc> clilocTable;

    private final ObservableList<CliLoc> items = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        flagColumn.setCellValueFactory(new PropertyValueFactory<>("flag"));
        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        textColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        textColumn.setOnEditCommit(event -> {
            event.getRowValue().setText(event.getNewValue());
        });
        idTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));

        FilteredList<CliLoc> filteredList = new FilteredList<>(items);
        filteredList.predicateProperty().bind(Bindings.createObjectBinding(
                this::getIDSelectionPredicate,
                searchBar.textProperty()));
        clilocTable.setItems(filteredList);

        clilocTable.setRowFactory(
                tableView -> {
                    final TableRow<CliLoc> row = new TableRow<>();
                    final ContextMenu rowMenu = new ContextMenu();
                    MenuItem removeItem = new MenuItem("Odstranit");
                    removeItem.setOnAction(event -> {

                        FilesHolder filesHolder = holder.getFiles().get();
                        if (filesHolder == null) {
                            return;
                        }
                        CliLoc item = row.getItem();
                        filesHolder.getCliLocs().getEntries().remove(item.getId());
                        items.remove(item);
                    });
                    rowMenu.getItems().addAll(removeItem);

                    // only display context menu for non-empty rows:
                    row.contextMenuProperty().bind(
                            Bindings.when(row.emptyProperty())
                                    .then((ContextMenu) null)
                                    .otherwise(rowMenu));
                    return row;
                });
    }


    private Predicate<CliLoc> getIDSelectionPredicate() {
        System.out.println("getIDSelectionPredicate");
        String searchText = searchBar.getText().toLowerCase();

        Predicate<CliLoc> predicate = _i -> true;

        if (!searchText.isEmpty()) {
            predicate = predicate.and(i -> i.getText().toLowerCase().contains(searchText));
        }
        return predicate;
    }

    public void setup(Stage stage, Holder holder) {
        super.setup(stage, holder, new SimpleObjectProperty<>(null));
    }

    @Override
    protected void unselected() {

    }

    @Override
    protected void selectionChanged(Item item) {

    }

    @Override
    protected void onFileChange(FilesHolder newValue) {
        items.clear();
        CliLocs cliLocs = newValue.getCliLocs();
        items.addAll(cliLocs.getEntries().values());
    }

    @FXML
    private void loadFromFile(ActionEvent actionEvent) {
        FilesHolder filesHolder = holder.getFiles().get();
        if (filesHolder == null) {
            return;
        }
        List<String[]> content = openFile();
        if (content == null) {
            return;
        }

        CliLocs cliLocs = filesHolder.getCliLocs();
        for (String[] row : content) {
            int id = Integer.parseInt(row[0]);
            String text = row[1];
            System.out.println("Načetl jsem: " + id + " => " + text);

            CliLoc cliLoc = cliLocs.get(id);
            if (cliLoc == null) {
                cliLoc = new CliLoc(id, text);
                cliLocs.getEntries().put(id, cliLoc);
            } else {
                cliLoc.setText(text);
            }
        }
        onFileChange(filesHolder);
    }

    private List<String[]> openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Soubor s lokalizací");
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("CSV soubor (*.csv)", "*.csv"));

        if (lastSelectDir != null) fileChooser.setInitialDirectory(lastSelectDir);

        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return null;
        } else {
            try (Reader reader = Files.newBufferedReader(file.toPath())) {
                CSVReaderBuilder builder = new CSVReaderBuilder(reader);
                builder.withMultilineLimit(-1);
                try (CSVReader csvReader = builder.build()) {
                    return csvReader.readAll();
                } catch (IOException | CsvException e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addEntry(ActionEvent actionEvent) {
        FilesHolder filesHolder = holder.getFiles().get();
        if (filesHolder == null) {
            return;
        }
        if (idTextField.getText().isBlank()) {
            return;
        }
        int id = Integer.parseInt(idTextField.getText());
        CliLoc cliLoc = filesHolder.getCliLocs().get(id);
        if (cliLoc == null) {
            cliLoc = new CliLoc(id, "");
            filesHolder.getCliLocs().getEntries().put(id, cliLoc);
            onFileChange(filesHolder);
        }
        clilocTable.scrollTo(cliLoc);
    }
}
