package eu.janinko.andaria.editor;

import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory;
import eu.janinko.andaria.editor.windows.DirectorySelector;
import eu.janinko.andaria.editor.misc.Holder;
import eu.janinko.andaria.editor.misc.FilesHolder;
import eu.janinko.andaria.editor.ui.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Slf4j
public class EditorApp extends Application {

    private Parent editor;
    private ImageEditorController editorCtl;
    @Getter
    private Parent artScene;
    @Getter
    private ArtSceneController artCtl;
    @Getter
    private Parent gumpScene;
    @Getter
    private GumpSceneController gumpCtl;
    @Getter
    private Parent paperdollScene;
    @Getter
    private PaperdollSceneController paperdollCtl;
    @Getter
    private Parent huesScene;
    @Getter
    private HueSceneController huesCtl;
    @Getter
    private Parent animDefsScene;
    @Getter
    private AnimDefsSceneController animDefsCtl;
    @Getter
    private Parent clilocScene;
    @Getter
    private ClilocSceneController clilocCtl;

    private Holder holder = new Holder();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SvgImageLoaderFactory.install();

        Desktop.getDesktop();
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader editorLoader = new FXMLLoader(getClass().getResource("ImageEditor.fxml"));
        editor = editorLoader.load();
        editorCtl = editorLoader.getController();

        FXMLLoader artLoader = new FXMLLoader(getClass().getResource("ArtScene.fxml"));
        artScene = artLoader.load();
        artCtl = artLoader.getController();

        FXMLLoader gumpLoader = new FXMLLoader(getClass().getResource("GumpScene.fxml"));
        gumpScene = gumpLoader.load();
        gumpCtl = gumpLoader.getController();

        FXMLLoader paperdollLoader = new FXMLLoader(getClass().getResource("PaperdollScene.fxml"));
        paperdollScene = paperdollLoader.load();
        paperdollCtl = paperdollLoader.getController();

        FXMLLoader huesLoader = new FXMLLoader(getClass().getResource("HueScene.fxml"));
        huesScene = huesLoader.load();
        huesCtl = huesLoader.getController();

        FXMLLoader animDefsLoader = new FXMLLoader(getClass().getResource("AnimDefsScene.fxml"));
        animDefsScene = animDefsLoader.load();
        animDefsCtl = animDefsLoader.getController();

        FXMLLoader clilocLoader = new FXMLLoader(getClass().getResource("ClilocScene.fxml"));
        clilocScene = clilocLoader.load();
        clilocCtl = clilocLoader.getController();

        Scene scene = new Scene(editor);


        primaryStage.setTitle("Andaria Image Editor 0.9");
        //primaryStage.getIcons().add(new Image("img/icon.svg"));
        primaryStage.getIcons().add(new Image("img/icon_256.png"));
        primaryStage.setResizable(true);
        primaryStage.setScene(scene);
        primaryStage.show();

        /*
        primaryStage.setResizable(false); // hack-fix
        primaryStage.setResizable(true); // hack-fix
*/
        artCtl.setup(primaryStage, holder);
        gumpCtl.setup(primaryStage, holder);
        paperdollCtl.setup(primaryStage, holder);
        huesCtl.setup(primaryStage, holder);
        animDefsCtl.setup(primaryStage, holder);
        clilocCtl.setup(primaryStage, holder);

        editorCtl.setup(holder, this);

        selectFiles(primaryStage);
        //editorCtl.selectArts();
    }

    private void selectFiles(Stage primaryStage) {

        Optional<Path> path = DirectorySelector.storedInputPath();
        if (path.isPresent()) {
            try {
                holder.getFiles().setValue(FilesHolder.loadFiles(path.get(), primaryStage));
                return;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        boolean done = false;
        do {
            path = DirectorySelector.inputPath(primaryStage);
            if (path.isEmpty()) {
                Platform.exit();
                done = true;
            } else {
                try {
                    System.out.println("Loading files");
                    holder.getFiles().setValue(FilesHolder.loadFiles(path.get(), primaryStage));
                    done = true;
                } catch (RuntimeException ex) {
                    showError("Chyba při načítání souborů.", ex);
                } catch (InterruptedException e) {
                    log.error("Application interrupted", e);
                    Platform.exit();
                    done = true;
                }
            }
        } while (!done);
    }

    public static void showError(String message, Throwable t) {
        log.error(message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(message);
        alert.setContentText(t.getLocalizedMessage());
        alert.showAndWait();
    }


}