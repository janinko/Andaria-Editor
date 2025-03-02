package eu.janinko.andaria.editor.regions;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import eu.janinko.andaria.editor.cp.CheckDescListCell;
import eu.janinko.andaria.editor.cp.CheckDescListCell.CheckDescListItem;
import eu.janinko.andaria.editor.regions.RegionsTreeView.RegionItem;
import eu.janinko.andaria.spherescript.sphere.objects.Areadef;
import static eu.janinko.andaria.spherescript.sphere.objects.Areadef.Flag.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Data;

/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class EditBoxCTL {
    private static final String STYLE_INVALID = "-fx-control-inner-background: #FFB6C1";
    private static final String STYLE_VALID = "-fx-control-inner-background: #90EE90";

    @FXML
    private TextField defnameInput;

    @FXML
    private TextField nameInput;

    @FXML
    private TextField positionInput;

    @FXML
    private SliderLabelController rainchanceController;

    @FXML
    private SliderLabelController coldchanceController;

    @FXML
    private ListView events;

    @FXML
    private ComboBox eventsCombo;

    @FXML
    private Button eventsButton;

    @FXML
    private TableView<Tag> tags;
    
    @FXML
    private TableColumn<Tag, String> tagNameCol;
    
    @FXML
    private TableColumn<Tag, String> tagValueCol;

    @FXML
    private ListView<CheckDescListCell.CheckDescListItem> flags;

    @FXML
    private ListView locations;
    
    private Areadef selectedArea;
    
    private Mapa map;
    private BooleanProperty mapValid;
    
    @FXML
    public void initialize(){
        defnameInput.focusedProperty().addListener((o, ov, nv) -> {if(!nv) defnameDone();});
        defnameInput.setOnAction(t -> this.defnameDone());
        
        nameInput.focusedProperty().addListener((o, ov, nv) -> {if(!nv) nameDone();});
        nameInput.setOnAction(t -> this.nameDone());
        
        rainchanceController.value().addListener((o, ov, nv) -> rainchanceDone());
        rainchanceController.selected().addListener((o, ov, nv) -> rainchanceDone());
        
        
        coldchanceController.value().addListener((o, ov, nv) -> coldchanceDone());
        coldchanceController.selected().addListener((o, ov, nv) -> coldchanceDone());
    }

    void init(Mapa map, BooleanProperty mapValid) {
        this.map = map;
        this.mapValid = mapValid;
    }
    
    public Areadef getArea(){
        return selectedArea;
    }
    
    public void treeSelected(ObservableValue<? extends TreeItem<RegionItem>> observable, TreeItem<RegionItem> oldValue, TreeItem<RegionItem> newValue) {
        RegionItem value = newValue.getValue();
        System.out.println("Selected: " + value.getName() + " G?" + value.isGroup());
        if(!value.isGroup()){
            areaSelected(value.getArea());
        }
    }

    private void areaSelected(Areadef area) {
        if(area == selectedArea){
            return;
        }
        selectedArea = area;
        
        defnameInput.setStyle("");
        defnameInput.setText(area.getDefname());
        
        nameInput.setStyle("");
        nameInput.setText(area.getName());
        
        positionInput.setStyle("");
        positionInput.setText(positionString(area));
        
        setChance(rainchanceController, area.getRainchance());
        
        setChance(coldchanceController, area.getColdchance());
        
        ObservableList<Areadef.Rect> locs = FXCollections.observableArrayList(area.getRects());
        locations.setItems(locs);
        
        ObservableList<String> evs = FXCollections.observableArrayList(area.getEvents());
        events.setItems(evs);
        
        ObservableList<Tag> tagList = FXCollections.observableArrayList();
        for (Map.Entry<String, String> e : area.getTags().entrySet()) {
            tagList.add(new Tag(e.getKey(), e.getValue()));
        }
        tagNameCol.setCellValueFactory(new PropertyValueFactory("name"));
        tagValueCol.setCellValueFactory(new PropertyValueFactory("value"));
        tags.setItems(tagList);
        setFlags(area.getFlags());
        
        map.setX(area.getX());
        map.setY(area.getY());
        mapValid.setValue(false);
    }

    private void setChance(SliderLabelController chanceController, Long chance) {
        if(chance == null){
            chanceController.selected().set(false);
        }else{
            chanceController.selected().set(true);
            chanceController.value().setValue(chance);
        }
    }
    
    private void setFlags(EnumSet<Areadef.Flag> f){
        
        ObservableList<CheckDescListItem> flagList = FXCollections.observableArrayList();
        flags.setCellFactory((p) -> new CheckDescListCell());
        for (Areadef.Flag flag : Areadef.Flag.values()) {
            CheckDescListItem li = new CheckDescListItem();
            li.setSelected(f.contains(flag));
            li.setLabel(flag.name());
            li.setDescription(flagDesc.get(flag));
            flagList.add(li);
        }
        flags.setItems(flagList);
    }

    private static String positionString(Areadef a){
        return a.getX() + "," + a.getY() + (a.getZ() == 0? "" : "," + a.getZ() );
    }

    private void defnameDone() {
        String text = defnameInput.getText();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @FXML
    private void selectPosition() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void nameDone() {
        String text = nameInput.getText();
        if(text.trim().isEmpty()){
            nameInput.setStyle(STYLE_INVALID);
            return;
        }
        selectedArea.setName(text.trim());
        nameInput.setStyle(STYLE_VALID);
    }

    private void rainchanceDone() {
        if(rainchanceController.selected().get()){
            selectedArea.setRainchance((long) rainchanceController.value().get());
        }else{
            selectedArea.setRainchance(null);
        }
    }

    private void coldchanceDone() {
        if(coldchanceController.selected().get()){
            selectedArea.setColdchance((long) coldchanceController.value().get());
        }else{
            selectedArea.setColdchance(null);
        }
    }
    
    @Data
    public static class Tag{
        private final String name;
        private String value;

        public Tag(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
    
    private static final Map<Areadef.Flag, String> flagDesc = new HashMap<>();
    static{
        flagDesc.put(region_antimagic_all, "Magie je zakázána");
        flagDesc.put(region_antimagic_recall_in, "Nelze se sem přemístit");
        flagDesc.put(region_antimagic_recall_out, "Nelze se odtud přemístit");
        flagDesc.put(region_antimagic_gate, "Nelze vytvořit portál");
        flagDesc.put(region_antimagic_teleport, "Nelze použít teleportaci");
        flagDesc.put(region_antimagic_damage, "Nelze seslat zlou magii");

        flagDesc.put(region_flag_ship, "Region pro lodě");
        flagDesc.put(region_flag_nobuilding, "Žádné stavění");
        flagDesc.put(region_flag_announce, "Oznámení při vtupu");
        flagDesc.put(region_flag_insta_logout, "Okamžitý logout");
        flagDesc.put(region_flag_underground, "Podzemí (žádné počasí)");
        flagDesc.put(region_flag_nodecay, "Věci na zemi nemizí");

        flagDesc.put(region_flag_safe, "Nejde nikoho zranit");
        flagDesc.put(region_flag_guarded, "Stráženo");
        flagDesc.put(region_flag_no_pvp, "Hráči se nemohou zraňovat");
        flagDesc.put(region_flag_arena, "Nepřičítají se killy");

        flagDesc.put(region_flag_no_fire_spells, "Nelze seslat ohnivá kouzla");
        flagDesc.put(region_flag_no_cold_spells, "Nelze seslat ledová kouzla");
        flagDesc.put(region_flag_no_acid_spells, "Nelze seslatt kyselinová kouzla");
        flagDesc.put(region_flag_no_energy_spells, "Nelze seslat energetická kouzla");
        flagDesc.put(region_antimagic_mark, "Nelze tu označit runa");
    }
}
