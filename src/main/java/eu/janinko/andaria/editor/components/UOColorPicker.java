package eu.janinko.andaria.editor.components;

import eu.janinko.andaria.editor.misc.DeferredAction;
import eu.janinko.andaria.ultimasdk.graphics.Color;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import lombok.Getter;

import java.io.IOException;
import java.util.*;

/**
 * FXML Controller class
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class UOColorPicker extends HBox {

    @FXML
    private Slider redSlider;
    @FXML
    private Spinner<Integer> redSpinner;
    @FXML
    private Slider greenSlider;
    @FXML
    private Spinner<Integer> greenSpinner;
    @FXML
    private Slider blueSlider;
    @FXML
    private Spinner<Integer> blueSpinner;

    @FXML
    private Canvas hueBar;
    @FXML
    private Canvas colorPicker;

    @FXML
    private GridPane previousColors;

    @FXML
    private Canvas currentCanvas;
    @FXML
    private Canvas previousCanvas;

    private final ColorRepresentation color = new ColorRepresentation();
    private final DeferredAction redraw = new DeferredAction(this::redraw);
    private final ObservableValue<Color> currentColor;
    private final ObjectProperty<Color> previousColor = new SimpleObjectProperty<>(Color.BLACK);
    private final ArrayList<Button> pastColorButtons = new ArrayList<>(20);
    private final ArrayDeque<Color> pastColors = new ArrayDeque<>(20);

    public UOColorPicker() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("UOColorPicker.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        currentColor = Bindings.createObjectBinding(color::getColor, color);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        initComponent(redSpinner, redSlider);
        initComponent(greenSpinner, greenSlider);
        initComponent(blueSpinner, blueSlider);

        redSpinner.valueProperty().addListener((p, o, n) -> color.setRed(n.shortValue()));
        redSlider.valueProperty().addListener((p, o, n) -> color.setRed(n.shortValue()));
        greenSpinner.valueProperty().addListener((p, o, n) -> color.setGreen(n.shortValue()));
        greenSlider.valueProperty().addListener((p, o, n) -> color.setGreen(n.shortValue()));
        blueSpinner.valueProperty().addListener((p, o, n) -> color.setBlue(n.shortValue()));
        blueSlider.valueProperty().addListener((p, o, n) -> color.setBlue(n.shortValue()));

        initPastColors();

        drawColorPicker();
        drawHueBar();
        drawCurrent();
        color.addListener((a) -> redraw.invalidate());
    }

    private void initComponent(Spinner<Integer> componentSpinner, Slider componentSlider) {
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 31);

        componentSpinner.setValueFactory(valueFactory);
        componentSpinner.increment();

        BidirectionalIntDoubleBinding bidirectionalIntDoubleBinding = new BidirectionalIntDoubleBinding(valueFactory.valueProperty(), componentSlider.valueProperty());
        valueFactory.valueProperty().addListener(bidirectionalIntDoubleBinding);
        componentSlider.valueProperty().addListener(bidirectionalIntDoubleBinding);
    }

    private void initPastColors() {
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 8; c++) {
                Button button = createPastColorButton();
                pastColorButtons.add(button);
                pastColors.add(Color.BLACK);
                button.setOnAction(this::pastColorSelected);

                previousColors.add(button, c, r);
                GridPane.setMargin(button, new Insets(1));
            }
        }
        updatePastColors();
    }

    public void setColor(Color color) {
        this.color.set(color);
        updateSlider();
        this.previousColor.set(color);

        if (!pastColors.getFirst().equals(color)) {
            if (!pastColors.removeFirstOccurrence(color)) {
                pastColors.removeLast();
            }
            pastColors.addFirst(color);
            updatePastColors();
        }
    }

    public ObservableValue<Color> color() {
        return currentColor;
    }

    private void updatePastColors() {
        Iterator<Color> colorIterator = pastColors.iterator();
        for (Button button : pastColorButtons) {
            Color color = colorIterator.next();

            var paint = getPaint(color);

            ImageView iv = (ImageView) button.getGraphic();
            WritableImage image = (WritableImage) iv.getImage();

            PixelWriter pw = image.getPixelWriter();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    pw.setColor(x, y, paint);
                }
            }
            button.setUserData(color);
        }
    }

    private void pastColorSelected(ActionEvent e) {
        Button source = (Button) e.getSource();
        Color color = (Color) source.getUserData();

        redSlider.setValue(color.get5Red());
        greenSlider.setValue(color.get5Green());
        blueSlider.setValue(color.get5Blue());
    }

    private Button createPastColorButton() {
        Button button = new Button();
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setStyle("-fx-padding: 3;");
        button.setUserData(Color.BLACK);
        WritableImage writableImage = new WritableImage(20, 20);
        button.setGraphic(new ImageView(writableImage));
        return button;
    }

    private void redraw() {
        drawColorPicker();
        drawHueBar();
        drawCurrent();
        drawPrevious();
    }

    private void drawHueBar() {
        System.out.println("Drawing Hue Bar " + color.getHue());
        GraphicsContext gc = hueBar.getGraphicsContext2D();

        int height = (int) colorPicker.getHeight();
        int hp = (int) (color.getHue() / 360.0 * height);

        for (int l = 0; l < height; l++) {
            Paint paint;
            if (l == hp) {
                paint = javafx.scene.paint.Color.BLACK;
            } else if ((l - 1) == hp || (l + 1) == hp) {
                paint = javafx.scene.paint.Color.WHITE;
            } else {
                Color color = Color.hsv2rgb(new Color.Hsv(l * 360.0 / height, 1, 1));
                paint = getPaint(color);
            }
            gc.setFill(paint);
            gc.fillRect(0, l, hueBar.getWidth(), 1);
        }
    }

    private void drawColorPicker() {
        System.out.println("Drawing Color Picker " + color.getHue());
        GraphicsContext gc = colorPicker.getGraphicsContext2D();
        int width = (int) colorPicker.getWidth();
        int height = (int) colorPicker.getHeight();

        double h = color.getHue();

        int sp = (int) (color.getSaturation() * width);
        int vp = (int) (color.getValue() * height);

        for (int s = 0; s < width; s++) {
            for (int v = 0; v < height; v++) {
                Paint paint;
                if (s == sp || v == vp) {
                    paint = javafx.scene.paint.Color.BLACK;
                } else if ((s - 1) == sp || (s + 1) == sp || (v - 1) == vp || (v + 1) == vp) {
                    paint = javafx.scene.paint.Color.WHITE;
                } else {
                    Color color = Color.hsv2rgb(new Color.Hsv(h, (double) s / width, (double) v / height));
                    paint = getPaint(color);
                }
                gc.setFill(paint);

                gc.fillRect(s, height - v - 1, 1, 1);
            }
        }
    }

    private void drawCurrent() {
        System.out.println("Drawing current");
        GraphicsContext gc = currentCanvas.getGraphicsContext2D();

        gc.setFill(getPaint(color.getColor()));
        gc.fillRect(0, 0, currentCanvas.getWidth(), currentCanvas.getHeight());
    }

    private void drawPrevious() {
        System.out.println("Drawing previous");
        GraphicsContext gc = previousCanvas.getGraphicsContext2D();

        gc.setFill(getPaint(previousColor.get()));
        gc.fillRect(0, 0, previousCanvas.getWidth(), previousCanvas.getHeight());
    }

    private static javafx.scene.paint.Color getPaint(Color color) {
        double red = color.getRed() / 255.0;
        double green = color.getGreen() / 255.0;
        double blue = color.getBlue() / 255.0;
        return javafx.scene.paint.Color.color(red, green, blue);
    }

    @FXML
    private void colorClicked(MouseEvent e) {
        System.out.println("Color clicked -> " + e.getX() + ", " + e.getY());
        color.setHSV(color.getHue(), e.getX() / colorPicker.getWidth(), 1 - e.getY() / colorPicker.getHeight());
        updateSlider();
    }

    @FXML
    private void hueClicked(MouseEvent e) {
        System.out.println("Hue clicked -> " + e.getX() + ", " + e.getY());
        color.setHSV(360.0 * e.getY() / hueBar.getHeight(), color.getSaturation(), color.getValue());
        updateSlider();
    }

    private void updateSlider() {
        redSlider.setValue(color.getRed());
        greenSlider.setValue(color.getGreen());
        blueSlider.setValue(color.getBlue());
    }

    private static class BidirectionalIntDoubleBinding implements InvalidationListener {
        private final ObjectProperty<Integer> intProperty;
        private final DoubleProperty doubleProper;
        private int oldValue;
        private boolean updating = false;

        private BidirectionalIntDoubleBinding(ObjectProperty<Integer> intProperty, DoubleProperty doubleProper) {
            oldValue = intProperty.get();
            this.intProperty = Objects.requireNonNull(intProperty);
            this.doubleProper = Objects.requireNonNull(doubleProper);
        }

        @Override
        public void invalidated(Observable sourceProperty) {
            if (!updating) {
                try {
                    updating = true;
                    if (intProperty == sourceProperty) {
                        int newValue = intProperty.get();
                        doubleProper.set(newValue);
                        oldValue = newValue;
                    } else {
                        int newValue = (int) Math.floor(doubleProper.get());
                        intProperty.set(newValue);
                        oldValue = newValue;
                    }
                } catch (RuntimeException e) {
                    try {
                        if (intProperty == sourceProperty) {
                            intProperty.set(oldValue);
                        } else {
                            doubleProper.set(oldValue);
                        }
                    } catch (Exception e2) {
                        e2.addSuppressed(e);
                        throw new RuntimeException("Bidirectional binding failed together with an attempt" + " to restore the source property to the previous value.", e2);
                    }
                    throw new RuntimeException("Bidirectional binding failed, setting to the previous value", e);
                } finally {
                    updating = false;
                }
            }
        }
    }

    @Getter
    private static class ColorRepresentation implements Observable {
        private short red;
        private short green;
        private short blue;

        private double hue;
        private double saturation;
        private double value;


        private Set<InvalidationListener> listeners = new LinkedHashSet<>();

        public void setRed(short red) {
            if (this.red != red) {
                this.red = red;
                updateHSV();
                invalidate();
            }
        }

        public void setGreen(short green) {
            if (this.green != green) {
                this.green = green;
                updateHSV();
                invalidate();
            }
        }

        public void setBlue(short blue) {
            if (this.blue != blue) {
                this.blue = blue;
                updateHSV();
                invalidate();
            }
        }

        private void invalidate() {
            listeners.forEach(a -> a.invalidated(this));
        }

        public void setHSV(double h, double s, double v) {
            if (h == hue && s == saturation && v == value) {
                return;
            }

            this.hue = h;
            this.saturation = s;
            this.value = v;

            double r, g, b;
            if (h >= 360.0) {
                h -= 360.0;
            }

            double hh = h / 60;
            int i = ((int) hh) % 6;

            double f = hh - i;
            double p = v * (1 - s);
            double q = v * (1 - f * s);
            double t = v * (1 - (1 - f) * s);

            switch (i) {
                case 0:
                    r = v;
                    g = t;
                    b = p;
                    break;
                case 1:
                    r = q;
                    g = v;
                    b = p;
                    break;
                case 2:
                    r = p;
                    g = v;
                    b = t;
                    break;
                case 3:
                    r = p;
                    g = q;
                    b = v;
                    break;
                case 4:
                    r = t;
                    g = p;
                    b = v;
                    break;
                case 5:
                    r = v;
                    g = p;
                    b = q;
                    break;
                default:
                    throw new IllegalStateException();
            }

            red = (short) Math.round(r * 31);
            green = (short) Math.round(g * 31);
            blue = (short) Math.round(b * 31);
            invalidate();
        }

        private void updateHSV() {
            short min = red, max = red;
            if (green > max) max = green;
            if (blue > max) max = blue;
            if (green < min) min = green;
            if (blue < min) min = blue;
            int delta = max - min;

            this.value = max / 31.0;
            if (delta == 0) {
                this.hue = 0;
                this.saturation = 0;
                return;
            }

            double h;
            if (max == red) {
                h = (double) (green - blue) / delta % 6;
            } else if (max == green) {
                h = (double) (blue - red) / delta + 2;
            } else {
                h = (double) (red - green) / delta + 4;
            }
            h *= 60;

            if (h < 0) {
                h = 360 + h;
            }
            this.hue = h;

            if (max == 0) {
                this.saturation = 0;
            } else {
                this.saturation = (double) delta / max;
            }
        }

        @Override
        public void addListener(InvalidationListener listener) {
            listeners.add(listener);
        }

        @Override
        public void removeListener(InvalidationListener listener) {
            listeners.remove(listener);
        }

        private Color getColor() {
            return Color.getInstance(red, green, blue);
        }

        public void set(Color color) {
            boolean valid = true;
            if (this.red != color.get5Red()) {
                this.red = color.get5Red();
                valid = false;
            }
            if (this.green != color.get5Green()) {
                this.green = color.get5Green();
                valid = false;
            }
            if (this.blue != color.get5Blue()) {
                this.blue = color.get5Blue();
                valid = false;
            }
            if (!valid) {
                updateHSV();
                invalidate();
            }
        }
    }
}
