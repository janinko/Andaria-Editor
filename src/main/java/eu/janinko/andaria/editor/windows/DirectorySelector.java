package eu.janinko.andaria.editor.windows;

import eu.janinko.andaria.editor.EditorApp;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.prefs.Preferences;

public class DirectorySelector extends Dialog<Path> {
    private static final String FILES_PATH = "filesPath";
    private static final String OUTPUT_PATH = "outputPath";
    private final String propsKey;
    private final String filesChooerPropmt;

    @FXML
    private Label pathLabel;

    @FXML
    private TextField pathField;

    @FXML
    private ButtonType saveButton;

    private static Preferences prefs = Preferences.userNodeForPackage(EditorApp.class);

    public static Optional<Path> storedInputPath() {
        String filesString = prefs.get(FILES_PATH, null);
        if (filesString == null) {
            return Optional.empty();
        }
        Path filesPath;
        try {
            filesPath = Paths.get(filesString);
        } catch (InvalidPathException e) {
            return Optional.empty();
        }
        if (!Files.isDirectory(filesPath)) {
            return Optional.empty();
        }
        return Optional.of(filesPath);
    }

    public static Optional<Path> inputPath(Window owner) {
        DirectorySelector directorySelector = new DirectorySelector(owner, FILES_PATH, "Složka s UO soubory");
        directorySelector.setHeaderText("Vyber složku se soubory hry.");
        directorySelector.setTitle("Vyber složku se soubory hry");
        directorySelector.pathLabel.setText("Soubory UO");
        return directorySelector.showAndWait();
    }

    public static Optional<Path> outputPath(Window owner, String message) {
        DirectorySelector directorySelector = new DirectorySelector(owner, OUTPUT_PATH, "Složka pro výstup");
        directorySelector.setHeaderText("Vyber složku pro uložení souborů. " + message);
        directorySelector.setTitle("Vyber složku pro uložení souborů");
        directorySelector.pathLabel.setText("Výstupní složka");
        return directorySelector.showAndWait();
    }

    private DirectorySelector(Window owner, String propsKey, String filesChooerPropmt) {
        this.propsKey = propsKey;
        this.filesChooerPropmt = filesChooerPropmt;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DirectorySelector.fxml"));
            loader.setController(this);

            DialogPane dialogPane = loader.load();
            dialogPane.lookupButton(saveButton).addEventFilter(ActionEvent.ANY, this::onSubmit);

            initOwner(owner);
            initModality(Modality.APPLICATION_MODAL);

            setResizable(true); // false?
            setDialogPane(dialogPane);

            setResultConverter(buttonType -> {
                if (Objects.equals(ButtonBar.ButtonData.OK_DONE, buttonType.getButtonData())) {
                    return Paths.get(pathField.getText());
                }

                return null;
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void initialize() {
        pathField.setText(prefs.get(propsKey, ""));
    }

    private void onSubmit(ActionEvent actionEvent) {
        String pathString = pathField.getText();
        if (pathString == null || pathString.isEmpty()) {
            error("Musíš vybrat složku s UO soubory.");
            actionEvent.consume();
            return;
        }
        try {
            Paths.get(pathString);
        } catch (InvalidPathException ex) {
            error("Musíš vybrat složku s UO soubory. Chyba: " + ex.getLocalizedMessage());
            actionEvent.consume();
            return;
        }
        prefs.put(propsKey, pathString);
    }

    @FXML
    public void selectDirectory() {
        StringProperty filesProperty = pathField.textProperty();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(filesChooerPropmt);

        String files = filesProperty.getValue();
        if (files != null && !files.isEmpty()) {
            File pdir = new File(files);
            if (pdir.isDirectory())
                directoryChooser.setInitialDirectory(pdir);
        }

        File file = directoryChooser.showDialog(getOwner());
        if (file != null) {
            filesProperty.setValue(file.getAbsolutePath());
        }
    }

    private void error(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}
