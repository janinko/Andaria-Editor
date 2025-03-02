package eu.janinko.andaria.editor.misc;

import eu.janinko.andaria.editor.components.Item;
import eu.janinko.andaria.editor.ui.AnimDefsSceneController;
import eu.janinko.andaria.editor.ui.AnimDefsSceneController.AnimDefItem;
import eu.janinko.andaria.editor.ui.ArtSceneController.ArtItem;
import eu.janinko.andaria.editor.ui.PaperdollSceneController.PaperdollItem;
import eu.janinko.andaria.ultimasdk.files.arts.Art;
import eu.janinko.andaria.ultimasdk.files.gumps.Gump;
import eu.janinko.andaria.ultimasdk.files.hues.Hue;
import eu.janinko.andaria.ultimasdk.files.tiledata.ItemData;
import eu.janinko.andaria.ultimasdk.files.tiledata.LandData;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
@Getter
@Slf4j
public class Holder {
    private ObjectProperty<AnimDefItem> animDef = new SimpleObjectProperty<>(null);

    private ObjectProperty<Item> hue = new SimpleObjectProperty<>(null);

    private BooleanProperty partial = new SimpleBooleanProperty(false);

    private ObjectProperty<ArtItem> item = new SimpleObjectProperty<>(null);

    private ObjectProperty<Item> gump = new SimpleObjectProperty<>(null);

    private ObjectProperty<PaperdollItem> paperdoll = new SimpleObjectProperty<>(null);

    private ObjectProperty<FilesHolder> files = new SimpleObjectProperty<>(null);

    public Art item() throws IOException {
        ArtItem artItem = item.get();
        if (artItem == null) {
            return null;
        }
        FilesHolder filesHolder = files.get();
        if (files == null) {
            return null;
        }
        int id = artItem.getId();

        return filesHolder.getArts().getStatic(id);
    }

    public ItemData itemData() {
        ArtItem artItem = item.get();
        if (artItem == null) {
            return null;
        }
        FilesHolder filesHolder = files.get();
        if (files == null) {
            return null;
        }
        int id = artItem.getId();

        return filesHolder.getTileData().getItem(id);
    }

    public Gump gump() throws IOException {
        Item gumpItem = gump.get();
        if (gumpItem == null) {
            return null;
        }
        FilesHolder filesHolder = files.get();
        if (files == null) {
            return null;
        }
        int id = gumpItem.getId();

        return filesHolder.getGumps().get(id);
    }

    public Gump paperdoll() throws IOException {
        Item gumpItem = paperdoll.get();
        if (gumpItem == null) {
            return null;
        }
        FilesHolder filesHolder = files.get();
        if (files == null) {
            return null;
        }
        int id = gumpItem.getId();

        return filesHolder.getGumps().get(id);
    }

    public int paperdollFemaleId() {
        Item gumpItem = paperdoll.get();
        if (gumpItem == null) {
            return 0;
        }
        FilesHolder filesHolder = files.get();
        if (files == null) {
            return 0;
        }
        int id = gumpItem.getId();
        try {
            Gump gump = filesHolder.getGumps().get(id + 10000);
            if (gump == null) {
                return id;
            } else {
                return id + 10000;
            }
        } catch (IOException e) {
            log.error("Could not read female gump", e);
            return id;
        }
    }

    public Gump paperdollFemale() throws IOException {
        Item gumpItem = paperdoll.get();
        if (gumpItem == null) {
            return null;
        }
        FilesHolder filesHolder = files.get();
        if (files == null) {
            return null;
        }
        int id = gumpItem.getId();

        return filesHolder.getGumps().get(id + 10000);
    }

    public Hue hue(){
        Item hueItem = hue.get();
        if (hueItem == null) {
            return null;
        }
        FilesHolder filesHolder = files.get();
        if (files == null) {
            return null;
        }
        int id = hueItem.getId();

        return filesHolder.getHues().get(id);

    }

}
