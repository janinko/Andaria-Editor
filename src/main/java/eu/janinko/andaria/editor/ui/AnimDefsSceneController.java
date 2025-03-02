package eu.janinko.andaria.editor.ui;

import eu.janinko.andaria.editor.components.AnimBox;
import eu.janinko.andaria.editor.components.Item;
import eu.janinko.andaria.editor.components.ItemSelector;
import eu.janinko.andaria.editor.misc.FilesHolder;
import eu.janinko.andaria.editor.misc.Holder;
import eu.janinko.andaria.editor.misc.Utils;
import eu.janinko.andaria.editor.windows.AnimSelectorPopup;
import eu.janinko.andaria.ultimasdk.files.Anims;
import eu.janinko.andaria.ultimasdk.files.Arts;
import eu.janinko.andaria.ultimasdk.files.Body;
import eu.janinko.andaria.ultimasdk.files.BodyConv;
import eu.janinko.andaria.ultimasdk.files.defs.BodyConvEntry;
import eu.janinko.andaria.ultimasdk.files.defs.BodyEntry;
import eu.janinko.andaria.ultimasdk.files.hues.Hue;
import eu.janinko.andaria.ultimasdk.utils.AnimResolver;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * FXML Controller class
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class AnimDefsSceneController extends SceneController {

    public static final int MAX_ANIMATION_COUNT = 2048;
    @FXML
    private Button jumpButton;
    @FXML
    private TextField bodyDefField;
    @FXML
    private TextField colorField;

    @FXML
    private Button selectAnimButton;
    @FXML
    private ChoiceBox<Anims.AnimFile> animFileChoice;
    @FXML
    private TextField animIndexField;
    @FXML
    private AnimBox animBox;

    @FXML
    private ItemSelector<AnimDefItem> idList;


    private final ObjectProperty<Integer> replacement = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> color = new SimpleObjectProperty<>();
    private final ObjectProperty<Anims.AnimFile> animFile = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> idxInFile = new SimpleObjectProperty<>();


    @FXML
    private void initialize() {
        ObservableList<Anims.AnimFile> choices = FXCollections.observableArrayList();
        choices.add(null);
        choices.addAll(Anims.AnimFile.values());
        choices.remove(Anims.AnimFile.ANIM1);
        animFileChoice.setItems(choices);

        TextFormatter<Integer> replacementFormatter = new TextFormatter<>(Utils.unsignedStringConverter(MAX_ANIMATION_COUNT));
        bodyDefField.setTextFormatter(replacementFormatter);
        TextFormatter<Integer> colorFormatter = new TextFormatter<>(Utils.unsignedStringConverter(3000));
        colorField.setTextFormatter(colorFormatter);
        TextFormatter<Integer> animIndexFormatter = new TextFormatter<>(Utils.unsignedStringConverter(MAX_ANIMATION_COUNT));
        animIndexField.setTextFormatter(animIndexFormatter);

        replacement.bind(replacementFormatter.valueProperty());
        color.bind(colorFormatter.valueProperty());
        animFile.bind(animFileChoice.valueProperty());
        idxInFile.bind(animIndexFormatter.valueProperty());

        replacement.addListener(this::replacementChanged);
        color.addListener(this::colorChanged);
        animFile.addListener(this::animFileChanged);
        idxInFile.addListener(this::idxInFileChanged);

        bodyDefField.disableProperty().bind(animFile.isNotNull());
        colorField.disableProperty().bind(replacement.isNull());
        animFileChoice.disableProperty().bind(replacement.isNotNull());
        animIndexField.disableProperty().bind(Bindings.or(replacement.isNotNull(), animFile.isNull()));
        selectAnimButton.disableProperty().bind(Bindings.or(replacement.isNotNull(), animFile.isNull()));
    }

    private void replacementChanged(ObservableValue<? extends Integer> p, Integer o, Integer n) {
        if (idList.selectedItemProperty().get() == null) {
            return;
        }
        int id = idList.selectedItemProperty().get().getId();
        Body bodyDef = holder.getFiles().get().getBodyDef();
        if (n == null) {
            colorField.setText("");
            if (bodyDef.isEntry(id)) {
                bodyDef.clear(id);
                redraw();
            }
        } else {
            BodyEntry bodyEntry = bodyDef.get(id);
            if (bodyEntry == null) {
                bodyEntry = new BodyEntry(id, n, color.get() == null ? 0 : color.get());
                bodyDef.set(bodyEntry);
                redraw();
            } else {
                if(bodyEntry.getReplacementAnimID() != n) {
                    bodyEntry.setReplacementAnimID(n);
                    redraw();
                }
            }
        }
    }

    private void colorChanged(ObservableValue<? extends Integer> p, Integer o, Integer n) {
        if (idList.selectedItemProperty().get() == null) {
            return;
        }
        int id = idList.selectedItemProperty().get().getId();
        Body bodyDef = holder.getFiles().get().getBodyDef();
        BodyEntry bodyEntry = bodyDef.get(id);
        if (n == null) {
            if(bodyEntry != null) {
                if(bodyEntry.getColor() != 0) {
                    bodyEntry.setColor(0);
                    redraw();
                }
            }
        } else {
            if(bodyEntry.getColor() != n) {
                bodyEntry.setColor(n);
                redraw();
            }
        }
    }

    private void animFileChanged(ObservableValue<? extends Anims.AnimFile> p, Anims.AnimFile o, Anims.AnimFile n) {
        if (idList.selectedItemProperty().get() == null) {
            return;
        }
        int id = idList.selectedItemProperty().get().getId();

        BodyConv bodyConv = holder.getFiles().get().getBodyConv();
        if (n == null) {
            animIndexField.setText("");
            if(bodyConv.isEntry(id)) {
                bodyConv.clear(id);
                redraw();
            }
        } else {
            BodyConvEntry bodyConvEntry = bodyConv.get(id);
            if (bodyConvEntry == null) {
                bodyConvEntry = new BodyConvEntry(id, n, color.get() == null ? 0 : color.get());
                bodyConv.set(bodyConvEntry);
                redraw();
            } else {
                if(bodyConvEntry.getAnimFile() != n) {
                    bodyConvEntry.setAnimFile(n);
                    redraw();
                }
            }
        }
    }

    private void idxInFileChanged(ObservableValue<? extends Integer> p, Integer o, Integer n) {
        if (idList.selectedItemProperty().get() == null) {
            return;
        }
        int id = idList.selectedItemProperty().get().getId();
        BodyConv bodyConv = holder.getFiles().get().getBodyConv();
        BodyConvEntry bodyConvEntry = bodyConv.get(id);
        if (n == null) {
            if (bodyConvEntry != null) {
                if(bodyConvEntry.getIndexInFile() != 0) {
                    bodyConvEntry.setIndexInFile(0);
                    redraw();
                }
            }
        } else {
            if(bodyConvEntry.getIndexInFile() != n) {
                bodyConvEntry.setIndexInFile(n);
                redraw();
            }
        }
    }

    private void redraw() {
        System.out.println("Called redraw");
        animBox.redraw();
        if (idList.selectedItemProperty().get() == null) {
            return;
        }
        int id = idList.selectedItemProperty().get().getId();
        idList.updateItem(new AnimDefItem(id));
    }


    public void setup(Stage stage, Holder holder) {
        super.setup(stage, holder, idList.selectedItemProperty());
        holder.getAnimDef().bind(idList.selectedItemProperty());
        animBox.setup(holder);
        animBox.setHue(null);
    }

    @Override
    protected void unselected() {
        animBox.setAnimID(null);
        bodyDefField.setText("");
        colorField.setText("");
        animFileChoice.getSelectionModel().select(0);
        animIndexField.setText("");
    }

    @Override
    protected void selectionChanged(Item item) {
        animBox.setAnimID(item.getId());

        FilesHolder filesHolder = holder.getFiles().get();

        Body bodyDef = filesHolder.getBodyDef();
        BodyConv bodyConv = filesHolder.getBodyConv();

        int id = item.getId();
        BodyEntry bodyEntry = bodyDef.get(id);
        if (bodyEntry != null) {
            id = bodyEntry.getReplacementAnimID();
            bodyDefField.setText(String.valueOf(id));
            int color = bodyEntry.getColor();
            if (color == 0) {
                animBox.setHue(null);
            } else {
                Hue hue = filesHolder.getHues().get(color);
                animBox.setHue(hue);
            }
            colorField.setText(String.valueOf(color));
            jumpButton.setDisable(false);
        } else {
            bodyDefField.setText("");
            colorField.setText("");
            animBox.setHue(null);
            jumpButton.setDisable(true);
        }
        BodyConvEntry entry = bodyConv.get(id);
        if (entry != null) {
            animFileChoice.getSelectionModel().select(entry.getAnimFile());
            animIndexField.setText(String.valueOf(entry.getIndexInFile()));
        } else {
            animFileChoice.getSelectionModel().select(0);
            animIndexField.setText("");
        }
    }

    @Override
    protected void onFileChange(FilesHolder newValue) {
        List<AnimDefItem> items = new ArrayList<>();
        int available = 0;
        for (int i = 0; i < 1000; i++) {
            AnimDefItem e = new AnimDefItem(i);
            items.add(e);
            available += e.isPresent() ? 0 : 1;
        }
        System.err.println("Anim sloty: " + available + " / " + 1000);

        idList.setItems(items);
    }

    public void popupPressed(ActionEvent actionEvent) {
        AnimSelectorPopup asp = new AnimSelectorPopup(animFileChoice.getValue(), holder, null, animBox.getScene().getWindow());
        asp.showAndWait();
        if (asp.selectedItem() != null) {
            animIndexField.setText(asp.selectedItem().getId() + "");
        }
    }

    public void jumpto(ActionEvent actionEvent) {
        idList.selectId(replacement.get());
    }

    @Value
    public class AnimDefItem implements Item {
        int id;
        boolean present;
        String text;

        public AnimDefItem(int id) {
            this.id = id;
            FilesHolder filesHolder = holder.getFiles().get();

            Body bodyDef = filesHolder.getBodyDef();
            BodyConv bodyConv = filesHolder.getBodyConv();
            AnimResolver.AnimationInfo ai = AnimResolver.resolve(id, bodyDef, bodyConv);

            Anims anims = filesHolder.getAnims(ai.animFile());
            this.present = anims.isBody(ai.idInFile());


            String text = id + "";
            BodyEntry bodyEntry = bodyDef.get(id);
            if (bodyEntry != null) {
                id = bodyEntry.getReplacementAnimID();
                text += " >> " + id;
            }
            BodyConvEntry entry = bodyConv.get(id);
            if (entry != null) {
                int fileNum = switch (entry.getAnimFile()) {
                    case ANIM1 -> 1;
                    case ANIM2 -> 2;
                    case ANIM3 -> 3;
                    case ANIM4 -> 4;
                    case ANIM5 -> 5;
                };
                text += " >> " + fileNum + "#" + entry.getIndexInFile();
            }

            this.text = text;
        }
    }

}
