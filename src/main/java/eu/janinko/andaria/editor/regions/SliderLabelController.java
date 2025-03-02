package eu.janinko.andaria.editor.regions;



import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

/**
 * SliderLabel Controller class
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class SliderLabelController {

    @FXML
    private CheckBox checkbox;

    @FXML
    private Slider slider;

    @FXML
    private Label label;

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        slider.valueProperty().addListener((o, ov, nv) -> {
            if (checkbox.isSelected()) label.setText(Integer.toString(nv.intValue()));
        });
        checkbox.selectedProperty().addListener((o, ov, nv) ->checkboxChange());
    }
    
    @FXML
    public void checkboxChange(){
        if(checkbox.isSelected()){
            slider.setDisable(false);
            label.setText(Integer.toString((int) slider.getValue()));
        }else{
            slider.setDisable(true);
            label.setText("---");
        }
    }
    
    public DoubleProperty value(){
        return slider.valueProperty();
    }

    public BooleanProperty selected(){
        return checkbox.selectedProperty();
    }
}
