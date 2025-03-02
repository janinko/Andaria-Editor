package eu.janinko.andaria.editor.regions;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class RegionsCTL {

    @FXML
    private Canvas mapCanvas;

    @FXML
    private Label zoomLabel;
    
    @FXML
    private Label positionLabel;

    @FXML
    private Pane canvasPane;
    
    @FXML
    private RegionsTreeView regionsTree;
    
    @FXML
    private EditBoxCTL editBoxController;

    private IntegerProperty zoom = new SimpleIntegerProperty(1);
    
    private BooleanProperty mapValid = new SimpleBooleanProperty(true);
    
    private MainControll mainControll;
    
    private Mapa map;

    void setup(MainControll mainControll) {
        this.mainControll = mainControll;
    }

    @FXML
    public void initialize() {
        zoom.addListener((o, ov, nv) -> zoomLabel.textProperty().setValue(nv + "x"));
        zoom.addListener((o, ov, nv) -> mapValid.setValue(false));
        canvasPane.widthProperty().addListener(this::canvasChange);
        canvasPane.heightProperty().addListener(this::canvasChange);
        mapCanvas.widthProperty().bind(canvasPane.widthProperty());
        mapCanvas.heightProperty().bind(canvasPane.heightProperty());
        mapValid.addListener((o, ov, nv) -> {if(!nv) redrawMap();});
    }

    public void init() {
        System.out.println("Initializing...");
        map = new Mapa(mainControll.getRegions().getImage());
        regionsTree.init(mainControll.getRegions().getAreas());
        editBoxController.init(map, mapValid);
        regionsTree.getSelectionModel().selectedItemProperty().addListener(editBoxController::treeSelected);
        mapValid.setValue(false);
    }

    final ThreadFactory tf = (Runnable r) -> {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        return thread;
    };
    final ScheduledExecutorService ses = new ScheduledThreadPoolExecutor(1, tf);
    ScheduledFuture<?> task = null;

    private void canvasChange(Observable observable) {
        if (task != null) {
            task.cancel(true);
        }
        task = ses.schedule(() -> mapValid.setValue(false), 200, TimeUnit.MILLISECONDS);
    }

    @FXML
    public void zoomIn() {
        System.out.println("zooming in");
        if (zoom.get() < 16) {
            zoom.setValue(zoom.get() * 2);
        }
    }

    @FXML
    public void zoomOut() {
        System.out.println("zooming out");
        if (zoom.get() > 1) {
            zoom.setValue(zoom.get() / 2);
        }
    }
    
    @FXML
    public void mapClick(MouseEvent event) {
        final int width = (int) mapCanvas.getWidth();
        final int height = (int) mapCanvas.getHeight();

        final int x = (int) event.getX();
        final int y = (int) event.getY();
        map.setPos(x, y, width, height, zoom.get());
        mapValid.setValue(false);
    }

    private void redrawMap() {
        System.out.println("Redrawing map");
        if(mapValid.get()){
            System.out.println("  Map is valid");
            return;
        }
        if (map == null) {
            mapValid.setValue(true);
            return;
        }
        try {
            mapValid.setValue(true);
            final double width = mapCanvas.getWidth();
            final double height = mapCanvas.getHeight();
            Image imgB = map.getImage((int) width, (int) height, zoom.get());
            Image imgO = map.getOverlay((int) width, (int) height, zoom.get(), mainControll.getRegions().getAreas(), editBoxController.getArea());
            final GraphicsContext gc = mapCanvas.getGraphicsContext2D();

            gc.clearRect(0, 0, width, height);
            gc.drawImage(imgB, 0, 0);
            gc.drawImage(imgO, 0, 0);
            
            positionLabel.setText(map.getX()+","+map.getY());
            
            System.out.println("  Map is redrawn");
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public Image scale(Image source, int zoom) {
        ImageView imageView = new ImageView(source);
        imageView.setFitWidth(source.getWidth() * zoom);
        imageView.setFitHeight(source.getHeight() * zoom);
        return imageView.snapshot(null, null);
    }

}
