package eu.janinko.andaria.editor.regions;

import java.awt.image.BufferedImage;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import eu.janinko.andaria.ultimasdk.files.Map;
import eu.janinko.andaria.ultimasdk.files.Radarcol;
import eu.janinko.andaria.ultimasdk.files.Statics;
import eu.janinko.andaria.ultimasdk.files.TileData;
import eu.janinko.andaria.ultimasdk.graphics.impl.BasicBitmap;
import eu.janinko.andaria.ultimasdk.graphics.Color;
import eu.janinko.andaria.ultimasdk.tools.MiniMap;
import eu.janinko.andaria.spherescript.sphere.parsers.SphereParser;
import eu.janinko.andaria.spherescript.sphere.objects.Areadef;
import eu.janinko.andaria.ultimasdk.UOFiles;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.image.Image;

/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class Regions {
    private UOFiles files;
    private Path script;
    private Image image;
    private SphereParser parser = new SphereParser();

    public void initRegions(Path script) {
        if(!Files.isRegularFile(script) || !Files.isReadable(script))
            throw new IllegalArgumentException("Nemohu přečíst soubor "+script);
        this.script = script;
    }

    public void initFiles(Path uoPath) {
        files = new UOFiles(uoPath);
    }
   
    public ReadOnlyDoubleProperty load(EventHandler<WorkerStateEvent> onFinish){
        final LoadTask task = new LoadTask();
        
        task.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, onFinish);
        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, onFinish);
        
        new Thread(task).start();
        return task.progressProperty();
    }
    
    public Image getImage(){
        return image;
    }
    
    public Set<Areadef> getAreas(){
        return parser.getAreadefs();
    }

    private class LoadTask extends Task<Void>{

        private LoadTask() {
        }

        @Override
        protected Void call() throws Exception {
            try {
                parser.parse(script, Charset.forName("Cp1250"));
                updateProgress(20, 1000);

                Map map = files.getMap();
                updateProgress(27, 1000);
                Statics statics = files.getStatics();
                updateProgress(27 + 376, 1000);
                Radarcol radarcol = files.getRadarcol();
                updateProgress(27 + 376 + 7, 1000);
                TileData tiledata = files.getTileData();
                updateProgress(27 + 376 + 7 + 109, 1000);
                MiniMap mm = new MiniMap(map, statics, radarcol, tiledata);
                Color[][] render = mm.getMap(6144, 4096);
                updateProgress(940, 1000);
                BasicBitmap bmp = new BasicBitmap(render);
                updateProgress(960, 1000);
                BufferedImage bfimage = bmp.getImage();
                updateProgress(980, 1000);
                image = SwingFXUtils.toFXImage(bfimage, null);
                updateProgress(1000, 1000);
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                throw ex;
            }
            return null;
        }
        
    }
}
