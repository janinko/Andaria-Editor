package eu.janinko.andaria.editor.regions;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import lombok.Getter;

/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class MainControll {
    private static final Path PROPS_PATH = Paths.get("are.ini");
    
    @Getter
    private final Properties props = new Properties();
    
    @Getter
    private final Regions regions = new Regions();

    public MainControll() {
        loadProperties();
    }
    
    private void loadProperties(){
        if(Files.isReadable(PROPS_PATH) && Files.isRegularFile(PROPS_PATH)){
            try {
                props.load(Files.newBufferedReader(PROPS_PATH, Charset.forName("utf-8")));
            } catch (IOException ex) {
                System.err.println("Chyba načítání souboru properties: " +ex.getLocalizedMessage());
            }
        }
    }

    public boolean checkFiles() {
        String files = props.getProperty("files");
        String script = props.getProperty("script");
        if (files == null) return false;
        if (script == null) return false;
        try{
            regions.initFiles(Paths.get(files));
            regions.initRegions(Paths.get(script));
            return true;
        }catch(IllegalArgumentException ex){
            return false;
        }
    }
    

    
}
