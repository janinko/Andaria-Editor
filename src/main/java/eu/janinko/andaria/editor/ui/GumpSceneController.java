package eu.janinko.andaria.editor.ui;

import eu.janinko.andaria.editor.components.GumpView;
import eu.janinko.andaria.editor.components.Item;
import eu.janinko.andaria.editor.components.ItemSelector;
import eu.janinko.andaria.editor.misc.FilesHolder;
import eu.janinko.andaria.editor.misc.Holder;
import eu.janinko.andaria.editor.misc.RenderStyle;
import eu.janinko.andaria.ultimasdk.files.gumps.Gump;
import eu.janinko.andaria.ultimasdk.graphics.impl.WritableBitmap;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;

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
public class GumpSceneController extends SceneController {
    
    @FXML
    private GumpView canvas;

    @FXML
    private ItemSelector<Item> idList;

    public void setup(Stage stage, Holder holder) {
        super.setup(stage, holder,idList.selectedItemProperty());
        holder.getGump().bind(idList.selectedItemProperty());
        canvas.setup(holder);
    }

    @Override
    protected void unselected() {
        canvas.setImageID(null);
    }

    @Override
    protected void selectionChanged(Item item) {
        canvas.setImageID(item.getId());
    }

    @Override
    protected void onFileChange(FilesHolder newValue) {

        List<Item> items = new ArrayList<>();
        for (int i = 0; i < 50000; i++) {
            items.add(new ItemSelector.DefaultItem(i, holder.getFiles().get().getGumps().isGump(i)));
        }

        idList.setItems(items);
    }
    
    @FXML
    private void load(){
        try {
            BufferedImage newImg = loadFile();
            if(newImg != null){
                Gump newArt = new Gump(new WritableBitmap(newImg));
                holder.getFiles().get().getGumps().setGump(holder.getGump().get().getId(), newArt);
                canvas.redraw();
            }
        } catch (IOException ex) {
            error(ex);
        }
    }
    
    @FXML
    private void export(){
        try {
            exportFile(holder.getFiles().get().getGumps().get(holder.getGump().get().getId()).getImage(), holder.getItem().get().getId());
        } catch (IOException ex) {
            error(ex);
        }
    }

}
