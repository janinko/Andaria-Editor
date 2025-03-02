package eu.janinko.andaria.editor.windows;

import eu.janinko.andaria.editor.components.AnimBox;
import eu.janinko.andaria.editor.components.Item;
import eu.janinko.andaria.editor.components.ItemSelector;
import eu.janinko.andaria.editor.misc.Holder;
import eu.janinko.andaria.ultimasdk.files.Anims;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnimSelectorPopup extends Stage {


    @FXML
    private ItemSelector<Item> idList;

    @FXML
    private AnimBox canvas;
    @FXML
    private Button selectButton;

    public ObjectProperty<Item> selected = new SimpleObjectProperty<>();

    public AnimSelectorPopup(Anims.AnimFile animsType, Holder holder, Integer preselected, Window owner) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AnimSelectorPopup.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();

            initModality(Modality.WINDOW_MODAL);
            initOwner(owner);

            canvas.setup(animsType, holder);
            canvas.animIDProperty().bind(Bindings.createObjectBinding(() -> {
                Item item = idList.selectedItemProperty().get();
                if(item == null){
                    return null;
                }
                return item.getId();
            }, idList.selectedItemProperty()));

            Anims anims = holder.getFiles().get().getAnims(animsType);
            List<Item> items = new ArrayList<>();
            for(int i = 0; i < anims.numberOfBodies(); i++) {
                Item item = new ItemSelector.DefaultItem(i, anims.isBody(i));
                items.add(item);
            }
            idList.setItems(items);

            if (preselected != null) {
                idList.selectIdx(preselected);
            }
            idList.hideMissing();

            idList.selectedItemProperty().addListener((p, o, n) -> selected.set(n));
            selectButton.disableProperty().bind(selected.isNull());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Item selectedItem() {
        return selected.get();
    }

    @FXML
    private void cancel() {
        selected.set(null);
        close();
    }

}
