package eu.janinko.andaria.editor.ui;

import eu.janinko.andaria.editor.EditorApp;
import eu.janinko.andaria.editor.FxUtils;
import eu.janinko.andaria.editor.components.ItemSelector;
import eu.janinko.andaria.editor.components.TaskProgressBar;
import eu.janinko.andaria.editor.misc.Holder;
import eu.janinko.andaria.editor.misc.FilesHolder;
import eu.janinko.andaria.editor.windows.HueSelectorPopup;
import eu.janinko.andaria.editor.windows.DirectorySelector;
import eu.janinko.andaria.ultimasdk.UOFiles;
import eu.janinko.andaria.ultimasdk.files.*;
import eu.janinko.andaria.ultimasdk.files.hues.Hue;
import eu.janinko.andaria.uotools.diff.*;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * FXML Controller class
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
@Slf4j
public class ImageEditorController {

    @FXML
    public HBox mainBox;
    @FXML
    public ToggleButton partialHueToggle;

    @FXML
    private ToggleGroup sceneGroup;

    @FXML
    private Pane scene;

    private EditorApp editor;

    private Holder holder;

    @FXML
    private void initialize() {
        sceneGroup.selectedToggleProperty().addListener(this::sceneSelected);
        FxUtils.overrideSelectingEvents(sceneGroup);
    }

    public void setup(Holder holder, EditorApp editor) {
        this.editor = editor;
        this.holder = holder;
        this.holder.getPartial().bindBidirectional(partialHueToggle.selectedProperty());

        mainBox.disableProperty().bind(holder.getFiles().isNull());

        scene.getChildren().add(editor.getArtScene());
    }

    private void sceneSelected(ObservableValue<? extends Toggle> observable, Toggle oldVal, Toggle newVal) {
        if (newVal == null) { // disallow scene deselection
            oldVal.setSelected(true);
            return;
        }
        switch (newVal.getUserData().toString()) {
            case "arts" -> setScene(editor.getArtScene(), editor.getArtCtl());
            case "gumps" -> setScene(editor.getGumpScene(), editor.getGumpCtl());
            case "paperdolls" -> setScene(editor.getPaperdollScene(), editor.getPaperdollCtl());
            case "hues" -> setScene(editor.getHuesScene(), editor.getHuesCtl());
            case "animDefs" -> setScene(editor.getAnimDefsScene(), editor.getAnimDefsCtl());
            case "cliloc" -> setScene(editor.getClilocScene(), editor.getClilocCtl());
        }
    }

    @FXML
    private void save() {
        String fileType = sceneGroup.getSelectedToggle().getUserData().toString();
        try {
            switch (fileType) {
                case "arts" -> save(EnumSet.of(FilesHolder.FileType.ARTS, FilesHolder.FileType.TILEDATA));
                case "gumps" -> save(EnumSet.of(FilesHolder.FileType.GUMPS));
                case "paperdolls" -> save(EnumSet.of(FilesHolder.FileType.GUMPS, FilesHolder.FileType.TILEDATA));
                case "hues" -> save(EnumSet.of(FilesHolder.FileType.HUES));
                case "animDefs" -> save(EnumSet.noneOf(FilesHolder.FileType.class));
                case "cliloc" -> save(EnumSet.of(FilesHolder.FileType.CLILOC));
            }
        } catch (Exception ex) {
            EditorApp.showError("Chyba při ukládání souborů.", ex);
        }
    }

    private void save(EnumSet<FilesHolder.FileType> fileTypes) throws IOException {
        FilesHolder filesHolder = holder.getFiles().get();
        String list = fileTypes.stream().map(FilesHolder.FileType::getName).collect(Collectors.joining(", "));
        Optional<Path> outputPath = DirectorySelector.outputPath(scene.getScene().getWindow(), "Uloží se " + list + ".");
        if (outputPath.isEmpty()) {
            return;
        }
        filesHolder.save(fileTypes, outputPath.get());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("Uloženo");
        alert.showAndWait();
    }

    @FXML
    private void reload() {
        FilesHolder filesHolder = holder.getFiles().get();
        holder.getFiles().setValue(null);
        FilesHolder newFiles = filesHolder.reload();
        holder.getFiles().setValue(newFiles);
    }

    @FXML
    private void open() {
        Optional<Path> path = DirectorySelector.inputPath(scene.getScene().getWindow());
        if(path.isPresent()){
            try {
                FilesHolder newHolder = FilesHolder.loadFiles(path.get(), (Stage) scene.getScene().getWindow());
                holder.getFiles().setValue(newHolder);
            } catch (InterruptedException e) {
                EditorApp.showError("Chyba při načítání souborů", e);
            }
        }
    }

    private void setScene(Parent scn, SceneController ctl) {
        scene.getChildren().clear();
        scene.getChildren().add(scn);
    }

    @FXML
    private void pickHue(ActionEvent actionEvent) {
        Hue hue = holder.hue();
        HueSelectorPopup hsp = new HueSelectorPopup(holder.getFiles().get().getHues(), hue == null ? null : hue.getId(), mainBox.getScene().getWindow());
        hsp.showAndWait();
        if (hsp.selectedHue() == null) {
            holder.getHue().set(null);
        } else {
            holder.getHue().set(new ItemSelector.DefaultItem(hsp.selectedHue().getId(), true));
        }
    }

    @FXML
    private void diff(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Provedené změny v souborech");
        alert.setHeaderText(null);

        Stage window = (Stage) alert.getDialogPane().getScene().getWindow();
        System.out.println("S min: " + window.getMinHeight() + " x " + window.getMinWidth());
        System.out.println("S max: " + window.getMaxHeight() + " x " + window.getMaxWidth());
        alert.setResizable(true);

        System.out.println("alert: " + alert.getHeight() + " x " + alert.getWidth());

        window.setMinWidth(600);
        window.setMinHeight(600);



        Node node = alert.getDialogPane().getChildren().get(1);
        if(node instanceof Label l){
            System.out.println("Lab: " + l.getText());
            l.setPrefWidth(Region.USE_COMPUTED_SIZE);
        }

        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
        try {
            TextArea ta = new TextArea(computeDiff());
            ta.setEditable(false);
            alert.getDialogPane().setContent(ta);
        } catch (Exception e) {
            e.printStackTrace();
            Platform.exit();
            return;
        }
        alert.showAndWait();
    }


    private static class DiffTask extends Task<String> {

        private final FilesHolder holder;

        private DiffTask(FilesHolder filesHolder) {
            this.holder = filesHolder;
        }

        @Override
        protected String call() throws Exception {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try (PrintStream ps = new PrintStream(baos); UOFiles uoFiles = new UOFiles(holder.getFilesPath())) {
                ps.println("Arty: ");
                updateProgress(50, 1000);
                Arts arts = uoFiles.getArts();
                updateProgress(150, 1000);
                ArtsDiff artsDiff = new ArtsDiff(ps);
                artsDiff.diff(arts, holder.getArts());
                updateProgress(250, 1000);
                arts.close();
                ps.println();
                ps.println("-------------------");

                ps.println("Gumpy: ");
                Gumps gumps = uoFiles.getGumps();
                updateProgress(350, 1000);
                GumpsDiff gumpsDiff = new GumpsDiff(ps);
                gumpsDiff.diff(gumps, holder.getGumps());
                updateProgress(450, 1000);
                gumps.close();
                ps.println();
                ps.println("-------------------");

                ps.println("Tiledata: ");
                TileData tileData = uoFiles.getTileData();
                updateProgress(550, 1000);
                TiledataDiff tiledataDiff = new TiledataDiff(ps);
                tiledataDiff.diff(tileData, holder.getTileData());
                updateProgress(650, 1000);
                ps.println();
                ps.println("-------------------");

                ps.println("Hues: ");
                Hues hues = uoFiles.getHues();
                updateProgress(750, 1000);
                HuesDiff huesDiff = new HuesDiff(ps);
                huesDiff.diff(hues, holder.getHues());
                updateProgress(850, 1000);
                ps.println();
                ps.println("-------------------");

                ps.println("Cliloc: ");
                CliLocs cliLocs = uoFiles.getCliLocs();
                updateProgress(900, 1000);
                CliLocsDiff cliLocsDiff = new CliLocsDiff(ps);
                cliLocsDiff.diff(cliLocs, holder.getCliLocs());
                updateProgress(950, 1000);
            } catch (Exception e) {
                e.printStackTrace();
                return "Nastala chyba: " + e.getLocalizedMessage();
            }

            String data = baos.toString();
            updateProgress(1000, 1000);
            return data;
        }
    }

    private String computeDiff() throws InterruptedException {
        FilesHolder filesHolder = holder.getFiles().get();
        if(filesHolder == null){
            return "Soubory nejsou načtené!";
        }

        final DiffTask task = new DiffTask(filesHolder);
        TaskProgressBar<String> tpb = new TaskProgressBar<>(task, null);

        tpb.setTitle("Výpočet rozdílů");

        try {
            return tpb.startAndWait();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return "Nastala chyba: " + e.getLocalizedMessage();
        }
    }
}
