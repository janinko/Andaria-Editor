package eu.janinko.andaria.editor.components;

import eu.janinko.andaria.editor.misc.FilesHolder;
import eu.janinko.andaria.editor.misc.RenderStyle;
import eu.janinko.andaria.ultimasdk.files.arts.Art;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.EnumSet;

@Slf4j
public class ArtView extends BitmapView {
    private final ObjectProperty<Art> art = new SimpleObjectProperty<>();

    public ArtView() {
        super(EnumSet.of(RenderStyle.BG_BLACK, RenderStyle.BG_WHITE, RenderStyle.BG_WHITESMOKE, RenderStyle.ALIGN_GRID));
        painter.renderStyleProperty().set(RenderStyle.ALIGN_GRID);
        art.bind(Bindings.createObjectBinding(this::computeArt, filesHolder, imageID, redrawTrigger));
        art.addListener((o) -> invalidate());
    }

    @Override
    public void invalidate() {
        System.out.println("ArtView invalidated  " + isVisible());
        drawImage();
    }

    private void drawImage() {
        Art art1 = art.get();
        if(art1 == null){
            blank();
            return;
        }
        imageView.setImage(painter.drawBottomCenteredImage(art1.getBitmap()));
    }

    private Art computeArt() {
        FilesHolder files = filesHolder.get();
        if (files == null) {
            return null;
        }
        if (imageID.get() == null) {
            return null;
        }
        try {
            return files.getArts().getStatic(imageID.get());
        } catch (IOException e) {
            log.error("Chyba načítání animace {}", imageID.get(), e);
            return null;
        }
    }
}
