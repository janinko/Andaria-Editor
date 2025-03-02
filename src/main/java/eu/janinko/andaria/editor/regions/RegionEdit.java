package eu.janinko.andaria.editor.regions;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import static javafx.concurrent.WorkerStateEvent.WORKER_STATE_FAILED;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class RegionEdit extends Application {
    
    private Stage primaryStage;
    MainControll mainControll;
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        mainControll = new MainControll();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RegionEdit.fxml"));
        Parent root = loader.load();
        
        RegionsCTL regionsCTL = loader.getController();
        regionsCTL.setup(mainControll);
        
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("Andaria Region Editor");
        primaryStage.setResizable(true);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        this.primaryStage = primaryStage;
        
        if(!mainControll.checkFiles()){
            selectFiles();
        }

        loadFiles();
        primaryStage.setResizable(false); // hack-fix
        primaryStage.setResizable(true); // hack-fix
        regionsCTL.init();        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void selectFiles() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Selector.fxml"));
        
        Parent selector = loader.load();
        Scene selectScene = new Scene(selector);
        Stage selectStage = new Stage();
        
        SelectorCTL selectorCTL = loader.getController();
        selectorCTL.setup(selectStage, mainControll);
        
        selectStage.setResizable(false);
        selectStage.setTitle("Výběr souborů");
        selectStage.setScene(selectScene);
        selectStage.setOnCloseRequest(t -> Platform.exit());
        selectStage.initOwner(primaryStage);
        selectStage.initModality(Modality.WINDOW_MODAL);
        selectStage.showAndWait();
    }
    
    private void loadFiles(){
        final ProgressBar pb = new ProgressBar(0);
        pb.setPrefSize(300, 30);
        
        AnchorPane pane = new AnchorPane();
        pane.getChildren().add(pb);
        Scene scene = new Scene(pane);
        
        Stage progressStage = new Stage();

        pb.progressProperty().bind(mainControll.getRegions().load((WorkerStateEvent t) -> {
            if(t.getEventType() == WORKER_STATE_FAILED){
                t.consume();
                
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Chyba při načítání souborů");
                alert.setContentText(t.getSource().getException().getLocalizedMessage());
                alert.showAndWait();
                Platform.exit();
                return;
            }
            progressStage.close();
        }));
        
        progressStage.setResizable(false);
        progressStage.setTitle("Načítání souborů");
        progressStage.setScene(scene);
        progressStage.setOnCloseRequest(WindowEvent::consume);
        progressStage.initOwner(primaryStage);
        progressStage.initModality(Modality.WINDOW_MODAL);
        progressStage.showAndWait();
    }
    
    
}
