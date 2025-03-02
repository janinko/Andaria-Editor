package eu.janinko.andaria.editor.ui;

import eu.janinko.andaria.editor.components.ArtView;
import eu.janinko.andaria.editor.components.Item;
import eu.janinko.andaria.editor.components.ItemSelector;
import eu.janinko.andaria.editor.components.TiledataSidebar;
import eu.janinko.andaria.editor.misc.FilesHolder;
import eu.janinko.andaria.editor.misc.Holder;
import eu.janinko.andaria.editor.misc.RenderStyle;
import eu.janinko.andaria.ultimasdk.files.Arts;
import eu.janinko.andaria.ultimasdk.files.arts.Art;
import eu.janinko.andaria.ultimasdk.files.tiledata.ItemData;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import lombok.Value;
import org.w3c.dom.Text;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * FXML Controller class
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class ArtSceneController extends SceneController{
    
    @FXML
    private ArtView canvas;

    @FXML
    private ItemSelector<ArtItem> idList;

    @FXML
    private TiledataSidebar tiledataSidebar;

    public void setup(Stage stage, Holder holder) {
        super.setup(stage, holder, idList.selectedItemProperty());
        holder.getItem().bind(idList.selectedItemProperty());
        canvas.setup(holder);
    }

    @Override
    protected void unselected() {
        canvas.setImageID(null);
        tiledataSidebar.setIem(null);

    }

    @Override
    protected void selectionChanged(Item item) {
        canvas.setImageID(item.getId());
        tiledataSidebar.setIem(holder.itemData());
    }


    @Override
    public void onFileChange(FilesHolder newFiles) {
        final List<ArtItem> items = new ArrayList<>();
        if (newFiles != null) {
            int available = 0;
            for (int i = 0; i < 0x3FFF; i++) {
                ArtItem e = new ArtItem(i);
                items.add(e);
                available += e.isPresent() ? 0 : 1;
            }
            System.err.println("Arty: " + available + " / " + 0x3FFF);
        }
        idList.setItems(items);
    }
    
    @FXML
    private void load(){
        try {
            BufferedImage newImg = loadFile();
            if(newImg != null){
                Art newArt = new Art(newImg);
                holder.getFiles().get().getArts().setStatic(holder.getItem().get().getId(), newArt);
                canvas.redraw();
            }
        } catch (IOException ex) {
            error(ex);
        }
    }
    
    @FXML
    private void export(){
        try {
            exportFile(holder.getFiles().get().getArts().getStatic(holder.getItem().get().getId()).getImage(), holder.getItem().get().getId());
        } catch (IOException ex) {
            error(ex);
        }
    }
    
    @FXML
    private void remove(){
        try {
            holder.getFiles().get().getArts().clearStatic(holder.getItem().get().getId());
            canvas.redraw();
        } catch (IOException ex) {
            error(ex);
        }
    }

    @Value
    public class ArtItem implements Item {
        int id;
        boolean present;
        String text;
        public ArtItem(int id) {
            this.id = id;
            this.present = holder.getFiles().get().getArts().isStatic(id);
            String name = "";
            if(present){
                name = " " + holder.getFiles().get().getTileData().getItem(id).getName();
            }
            this.text =  id + " (0x" + Integer.toHexString(id) + ")" + name;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public boolean isPresent() {
            return present;
        }

        @Override
        public String getText() {
            return text;
        }
    }
}
