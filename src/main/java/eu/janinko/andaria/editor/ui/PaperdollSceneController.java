package eu.janinko.andaria.editor.ui;


import eu.janinko.andaria.editor.components.*;
import eu.janinko.andaria.editor.misc.FilesHolder;
import eu.janinko.andaria.editor.misc.Holder;
import eu.janinko.andaria.editor.misc.RenderStyle;
import eu.janinko.andaria.ultimasdk.files.TileData;
import eu.janinko.andaria.ultimasdk.files.gumps.Gump;
import eu.janinko.andaria.ultimasdk.files.tiledata.ItemData;
import eu.janinko.andaria.ultimasdk.graphics.impl.WritableBitmap;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import lombok.Value;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * FXML Controller class
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class PaperdollSceneController extends SceneController {

    public static final int PAPERDOLL_START = 50000;

    @FXML
    private GumpView canvasM;

    @FXML
    private GumpView canvasF;

    @FXML
    private ItemSelector<PaperdollItem> idList;

    @FXML
    private ChoiceBox<Integer> artSelector;

    @FXML
    private ArtView artCanvas;

    @FXML
    private TiledataSidebar tiledataSidebar;
    @FXML
    private AnimBox animBox;

    @FXML
    private void initialize() {
        artSelector.disableProperty().bind(Bindings.createBooleanBinding(() -> artSelector.getItems().size() < 2, artSelector.getItems()));
        canvasM.setAvailableRenderStyles(EnumSet.of(RenderStyle.BG_BLACK, RenderStyle.BG_WHITE, RenderStyle.BG_WHITESMOKE, RenderStyle.MALE_GUMP));
        canvasM.renderStyleProperty().set(RenderStyle.MALE_GUMP);
        canvasF.setAvailableRenderStyles(EnumSet.of(RenderStyle.BG_BLACK, RenderStyle.BG_WHITE, RenderStyle.BG_WHITESMOKE, RenderStyle.FEMALE_GUMP));
        canvasF.renderStyleProperty().set(RenderStyle.FEMALE_GUMP);
    }

    public void setup(Stage stage, Holder holder) {
        super.setup(stage, holder, idList.selectedItemProperty());
        holder.getPaperdoll().bind(idList.selectedItemProperty());
        animBox.setup(holder);
        artCanvas.setup(holder);
        canvasM.setup(holder);
        canvasF.setup(holder);

        ReadOnlyObjectProperty<Integer> selected = artSelector.getSelectionModel().selectedItemProperty();
        ObjectBinding<ItemData> itemBinding = Bindings.createObjectBinding(() -> {
            Integer i = selected.get();
            if (i == null) {
                return null;
            }
            FilesHolder filesHolder = holder.getFiles().get();
            if (filesHolder == null) {
                return null;
            }
            return filesHolder.getTileData().getItem(i);

        }, selected, holder.getFiles());
        artCanvas.imageIDProperty().bind(selected);
        tiledataSidebar.itemProperty().bind(itemBinding);
    }

    @Override
    protected void unselected() {
        canvasM.setImageID(null);
        canvasF.setImageID(null);
        animBox.setAnimID(null);
        noItemsToSelect();
    }

    @Override
    protected void selectionChanged(Item item) {
        canvasM.setImageID(item.getId());
        canvasF.setImageID(holder.paperdollFemaleId());
        PaperdollItem paperdollItem = (PaperdollItem) item;
        animBox.setAnimID(paperdollItem.getAnim());
        prepareItemsForAnim(paperdollItem.getAnim());
    }

    public void onFileChange(FilesHolder newFiles) {
        final List<PaperdollItem> items = new ArrayList<>();
        if (newFiles != null) {
            Map<Short, String> nameMap = holder.getFiles().get().getTileData().getItems().stream().filter(i -> i.getAnimation() != 0).collect(Collectors.toMap(ItemData::getAnimation, ItemData::getName, (first, second) -> {
                if (first.contains(second)) return first;
                else return first + ", " + second;
            }));
            for (short i = 0; i < 1000; i++) {
                items.add(new PaperdollItem(i, nameMap.get(i)));
            }
        }
        idList.setItems(items);
    }

    private void prepareItemsForAnim(int anim) {
        FilesHolder filesHolder = holder.getFiles().get();
        if (filesHolder == null || anim == 0) {
            noItemsToSelect();
            return;
        }

        TileData tileData = filesHolder.getTileData();
        List<Integer> arts = new ArrayList<>();
        for (ItemData item : tileData.getItems()) {
            if (item.getAnimation() == anim) {
                arts.add(item.getId());
            }
        }

        artSelector.getItems().clear();
        System.out.println("Addding arts " + arts.size());
        artSelector.getItems().addAll(arts);
        System.out.println("Arts added");
        artSelector.getSelectionModel().select(0);
    }

    private void noItemsToSelect() {
        artSelector.getItems().clear();
    }

    @FXML
    private void loadM() {
        try {
            BufferedImage newImg = loadFile();
            if (newImg != null) {
                Gump newArt = new Gump(new WritableBitmap(newImg));
                holder.getFiles().get().getGumps().setGump(holder.getPaperdoll().get().getId(), newArt);
                canvasM.redraw();
            }
        } catch (IOException ex) {
            error(ex);
        }
    }

    @FXML
    private void exportM() {
        try {
            exportFile(holder.getFiles().get().getGumps().get(holder.getPaperdoll().get().getId()).getImage(), holder.getItem().get().getId());
        } catch (IOException ex) {
            error(ex);
        }
    }

    @FXML
    private void loadF() {
        try {
            BufferedImage newImg = loadFile();
            if (newImg != null) {
                Gump newArt = new Gump(new WritableBitmap(newImg));
                PaperdollItem paperdollItem = holder.getPaperdoll().get();
                holder.getFiles().get().getGumps().setGump(paperdollItem.getId() + 10000, newArt);
                selectionChanged(paperdollItem);
                canvasF.redraw();
            }
        } catch (IOException ex) {
            error(ex);
        }
    }

    @FXML
    private void exportF() {
        try {
            int gump;
            if (holder.getFiles().get().getGumps().isGump(holder.getPaperdoll().get().getId() + 10000)) {
                gump = holder.getPaperdoll().get().getId() + 10000;
            } else {
                gump = holder.getPaperdoll().get().getId();
            }
            exportFile(holder.getFiles().get().getGumps().get(gump).getImage(), gump);
        } catch (IOException ex) {
            error(ex);
        }
    }

    @Value
    public class PaperdollItem implements Item {
        int id;
        boolean present;
        String text;

        public PaperdollItem(int anim, String name) {
            this.id = anim + PAPERDOLL_START;
            this.present = holder.getFiles().get().getGumps().isGump(anim + PAPERDOLL_START);
            if (name == null) {
                name = "";
            } else {
                name = " " + name;
            }
            this.text = anim + " (0x" + Integer.toHexString(anim) + ")" + name;
        }

        public int getAnim() {
            return this.id - PAPERDOLL_START;
        }
    }
}
