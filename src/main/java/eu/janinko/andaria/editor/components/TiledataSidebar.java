package eu.janinko.andaria.editor.components;

import eu.janinko.andaria.editor.misc.Utils;
import eu.janinko.andaria.editor.ui.ArtSceneController;
import eu.janinko.andaria.ultimasdk.files.tiledata.ItemData;
import eu.janinko.andaria.ultimasdk.files.tiledata.TileFlag;
import eu.janinko.andaria.ultimasdk.files.tiledata.TileFlags;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.util.converter.IntegerStringConverter;
import lombok.Getter;

import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * FXML Controller class
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class TiledataSidebar extends GridPane {

    private static Map<TileFlag, String> FLAG_NAMES = new HashMap<>();

    static {
        FLAG_NAMES.put(TileFlag.None, "--žádný--");
        FLAG_NAMES.put(TileFlag.Background, "Pozadí");
        FLAG_NAMES.put(TileFlag.Weapon, "Zbraň");
        FLAG_NAMES.put(TileFlag.Transparent, "Průhledný");
        FLAG_NAMES.put(TileFlag.Translucent, "Průsvitný");
        FLAG_NAMES.put(TileFlag.Wall, "Zeď");
        FLAG_NAMES.put(TileFlag.Damaing, "Poškozující");
        FLAG_NAMES.put(TileFlag.Impassable, "Neprůchozí");
        FLAG_NAMES.put(TileFlag.Liquid, "Tekutý");
        FLAG_NAMES.put(TileFlag.Unknown1, "Neznámý 1");
        FLAG_NAMES.put(TileFlag.Surface, "Povrch");
        FLAG_NAMES.put(TileFlag.Bridge, "Most");
        FLAG_NAMES.put(TileFlag.Generic, "Stackovatelný");
        FLAG_NAMES.put(TileFlag.Window, "Okno");
        FLAG_NAMES.put(TileFlag.NoShoot, "Blokuje střelbu");
        FLAG_NAMES.put(TileFlag.ArticleA, "Člen a");
        FLAG_NAMES.put(TileFlag.ArticleAn, "Člen an");
        FLAG_NAMES.put(TileFlag.Internal, "interní");
        FLAG_NAMES.put(TileFlag.Foliage, "Listí");
        FLAG_NAMES.put(TileFlag.PartialHue, "Částečně barvitelné");
        FLAG_NAMES.put(TileFlag.Unknown2, "Neznámý 2");
        FLAG_NAMES.put(TileFlag.Map, "Mapa");
        FLAG_NAMES.put(TileFlag.Container, "Nádoba");
        FLAG_NAMES.put(TileFlag.Wearable, "Nositelné");
        FLAG_NAMES.put(TileFlag.LightSource, "Zdroj světla");
        FLAG_NAMES.put(TileFlag.Animation, "Animace");
        FLAG_NAMES.put(TileFlag.NoDiagonal, "Blokuje diagonálu");
        FLAG_NAMES.put(TileFlag.Unknown3, "Neznámé 3");
        FLAG_NAMES.put(TileFlag.Armor, "Zbroj");
        FLAG_NAMES.put(TileFlag.Roof, "Střecha");
        FLAG_NAMES.put(TileFlag.Door, "Dveře");
        FLAG_NAMES.put(TileFlag.StairBack, "Schody zezadu");
        FLAG_NAMES.put(TileFlag.StairRight, "Schody vpravo");
    }

    private final ObjectProperty<ItemData> item = new SimpleObjectProperty<>();

    private final TileDatumProperty tileDatumProperty = new TileDatumProperty();

    @FXML
    private TextField nameField;
    @FXML
    private TextField weightField;
    @FXML
    private TextField qualityField;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField animationField;
    @FXML
    private TextField hueField;
    @FXML
    private TextField valueField;
    @FXML
    private TextField heightField;
    @FXML
    private TextField unknown1Field;
    @FXML
    private TextField unknown2Field;
    @FXML
    private TextField unknown3Field;
    @FXML
    private TextField unknown4Field;

    @FXML
    private Label flagsLabel;

    private List<Object> thisIsNotGarbage = new ArrayList<>(); // Prevent garbage collection

    @Getter
    private final List<ArtSceneController.ArtItem> items = new ArrayList<>();

    public TiledataSidebar() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TiledataSidebar.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private void initialize() {
        this.disableProperty().bind(item.isNull());

        int column = 2;
        int row = 1;
        for (TileFlag flag : TileFlag.values()) {
            if (flag == TileFlag.None) {
                continue;
            }
            CheckBox box = createFlag(flag);
            this.add(box, column, row);
            row++;
            if (row > 11) {
                column++;
                row = 1;
            }
        }

        nameField.textProperty().bindBidirectional(tileDatumProperty.name);

        IntegerStringConverter byteConverter = Utils.unsignedStringConverter(255);
        IntegerStringConverter shortConverter = Utils.unsignedStringConverter(65535);
        setFormatter(byteConverter, weightField, tileDatumProperty.weight);
        setFormatter(byteConverter, qualityField, tileDatumProperty.quality);
        setFormatter(shortConverter, unknown1Field, tileDatumProperty.unknown1);
        setFormatter(byteConverter, unknown2Field, tileDatumProperty.unknown2);
        setFormatter(byteConverter, quantityField, tileDatumProperty.quantity);
        setFormatter(shortConverter, animationField, tileDatumProperty.animation);
        setFormatter(byteConverter, unknown3Field, tileDatumProperty.unknown3);
        setFormatter(byteConverter, hueField, tileDatumProperty.hue);
        setFormatter(byteConverter, unknown4Field, tileDatumProperty.unknown4);
        setFormatter(byteConverter, valueField, tileDatumProperty.value);
        setFormatter(byteConverter, heightField, tileDatumProperty.height);

        for (BooleanProperty flagProperty : tileDatumProperty.flags.values()) {
            flagProperty.addListener((p, o, n) -> flagsLabel.setText(tileDatumProperty.td.getFlags().toHexString()));
        }

        item.addListener((p, o, n) -> {
            if (n != null) {
                tileDatumProperty.link(n);
            }
        });
    }

    private void setFormatter(IntegerStringConverter converter, TextField field, IntegerProperty tiledataProperty){
        TextFormatter<Integer> textFormatter = new TextFormatter<>(converter);
        field.setTextFormatter(textFormatter);
        ObjectProperty<Integer> objectProperty = tiledataProperty.asObject();
        thisIsNotGarbage.add(objectProperty);
        textFormatter.valueProperty().bindBidirectional(objectProperty);
    }

    public void setIem(ItemData item) {
        this.item.set(item);
    }

    public ItemData getItem() {
        return this.item.get();
    }

    public ObjectProperty<ItemData> itemProperty() {
        return this.item;
    }

    private CheckBox createFlag(TileFlag flag) {
        CheckBox checkBox = new CheckBox(FLAG_NAMES.get(flag));
        BooleanProperty flagSelected = tileDatumProperty.flags.get(flag);
        checkBox.selectedProperty().bindBidirectional(flagSelected);

        GridPane.setMargin(checkBox, new Insets(3));
        return checkBox;
    }


    @FXML
    private void search() {
    }

    private static class TileDatumProperty {
        private final Map<TileFlag, BooleanProperty> flags = new HashMap<>();
        private final StringProperty name = new SimpleStringProperty();

        private final IntegerProperty weight = new SimpleIntegerProperty();
        private final IntegerProperty quality = new SimpleIntegerProperty();
        private final IntegerProperty unknown1 = new SimpleIntegerProperty();
        private final IntegerProperty unknown2 = new SimpleIntegerProperty();
        private final IntegerProperty quantity = new SimpleIntegerProperty();
        private final IntegerProperty animation = new SimpleIntegerProperty();
        private final IntegerProperty unknown3 = new SimpleIntegerProperty();
        private final IntegerProperty hue = new SimpleIntegerProperty();
        private final IntegerProperty unknown4 = new SimpleIntegerProperty();
        private final IntegerProperty value = new SimpleIntegerProperty();
        private final IntegerProperty height = new SimpleIntegerProperty();

        private ItemData td = new ItemData(-1);

        public TileDatumProperty() {
            for (TileFlag flag : TileFlag.values()) {
                if (flag == TileFlag.None) {
                    continue;
                }
                SimpleBooleanProperty property = new SimpleBooleanProperty();
                flags.put(flag, property);
                property.addListener((p, o, n) -> td.getFlags().setFlag(flag, n));
            }
            name.addListener((p, o, n) -> td.setName(n));

            weight.addListener((p, o, n) -> td.setWeight(n.byteValue()));
            quality.addListener((p, o, n) -> td.setQuality(n.byteValue()));
            unknown1.addListener((p, o, n) -> td.setUnknown1(n.shortValue()));
            unknown2.addListener((p, o, n) -> td.setUnknown2(n.byteValue()));
            quantity.addListener((p, o, n) -> td.setQuantity(n.byteValue()));
            animation.addListener((p, o, n) -> td.setAnimation(n.shortValue()));
            unknown3.addListener((p, o, n) -> td.setUnknown3(n.byteValue()));
            hue.addListener((p, o, n) -> td.setHue(n.byteValue()));
            unknown4.addListener((p, o, n) -> td.setUnknown4(n.byteValue()));
            value.addListener((p, o, n) -> td.setValue(n.byteValue()));
            height.addListener((p, o, n) -> td.setHeight(n.byteValue()));
        }

        public void link(ItemData td) {
            this.td = td;

            linkFlags(td.getFlags());
            name.set(td.getName());
            weight.set(td.getQuality());

            weight.set(Byte.toUnsignedInt(td.getWeight()));
            quality.set(Byte.toUnsignedInt(td.getQuality()));
            unknown1.set(Short.toUnsignedInt(td.getUnknown1()));
            unknown2.set(Byte.toUnsignedInt(td.getUnknown2()));
            quantity.set(Byte.toUnsignedInt(td.getQuantity()));
            animation.set(Short.toUnsignedInt(td.getAnimation()));
            unknown3.set(Byte.toUnsignedInt(td.getUnknown3()));
            hue.set(Byte.toUnsignedInt(td.getHue()));
            unknown4.set(Byte.toUnsignedInt(td.getUnknown4()));
            value.set(Byte.toUnsignedInt(td.getValue()));
            height.set(Byte.toUnsignedInt(td.getHeight()));
        }

        private void linkFlags(TileFlags tileFlags) {

            for (TileFlag flag : TileFlag.values()) {
                if (flag == TileFlag.None) {
                    continue;
                }
                BooleanProperty booleanProperty = flags.get(flag);
                booleanProperty.setValue(tileFlags.contains(flag));
            }
        }
    }

}
