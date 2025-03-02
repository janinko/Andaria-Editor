package eu.janinko.andaria.editor.regions;

import java.io.File;
import java.nio.file.Paths;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class SelectorCTL {
    
    @FXML
    private TextField filesInput;

    @FXML
    private TextField scriptInput;
    
    private Stage stage;
    
    private MainControll mainControll;
    

    void setup(Stage secondStage, MainControll mainControll) {
        this.stage = secondStage;
        this.mainControll = mainControll;
        
        filesInput.textProperty().setValue(mainControll.getProps().getProperty("files", ""));
        scriptInput.textProperty().setValue(mainControll.getProps().getProperty("script", ""));
    }
    
    private boolean validateScript(String script){
        if(script == null || script.isEmpty()){
            error("Musíš vybrat soubor s regiony.");
            return false;
        }
        try{
            mainControll.getRegions().initRegions(Paths.get(script));
        }catch(IllegalArgumentException ex){
            error("Chyba při načítání regionů: " +ex.getLocalizedMessage());
            return false;
        }catch(RuntimeException ex){
            ex.printStackTrace();
            error("Chyba při načítání regionů: " +ex.getLocalizedMessage());
            return false;
        }
        return true;
    }
    
    private boolean validateFiles(String files){
        if(files == null || files.isEmpty()){
            error("Musíš vybrat složku s UO soubory.");
            return false;
        }
        try{
            mainControll.getRegions().initFiles(Paths.get(files));
        }catch(IllegalArgumentException ex){
            error("Chyba při načítání UO souborů: " +ex.getLocalizedMessage());
            return false;
        }catch(RuntimeException ex){
            ex.printStackTrace();
            error("Chyba při načítání UO souborů: " +ex.getLocalizedMessage());
            return false;
        }
        return true;
    }
    
    @FXML
    public void loadFiles(){
        String files = filesInput.textProperty().get();
        String script = scriptInput.textProperty().get();
        if(!validateScript(script))
            return;
        if(!validateFiles(files))
            return;

        mainControll.getProps().put("files", files);
        mainControll.getProps().put("script", script);
        
        stage.close();
    }

    @FXML
    public void selectFiles(){
        final StringProperty filesProperty = filesInput.textProperty();
        
        DirectoryChooser directoryChoser = new DirectoryChooser();
        directoryChoser.setTitle("Složka s UO soubory");
        
        String files = filesProperty.getValue();
        if(files != null && !files.isEmpty()){
            File pdir = new File(files);
            if(pdir.isDirectory())
                directoryChoser.setInitialDirectory(pdir);
        }
        
        File file = directoryChoser.showDialog(stage);
        if(file != null){
            filesProperty.setValue(file.getAbsolutePath());
        }
    }
    
    @FXML
    public void selectScript(){
        final StringProperty scriptProperty = scriptInput.textProperty();
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Soubor s regiony");
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Sphere script (*.scp)", "*.scp"));

        String files = scriptProperty.getValue();
        if(files != null && !files.isEmpty()){
            File pdir = new File(files).getParentFile();
            if(pdir.isDirectory())
                fileChooser.setInitialDirectory(pdir);
        }
        
        File file = fileChooser.showOpenDialog(stage);
        if(file != null){
            scriptProperty.setValue(file.getAbsolutePath());
        }
    }

    private void error(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
