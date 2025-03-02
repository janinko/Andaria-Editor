package eu.janinko.andaria.editor.components;

import eu.janinko.andaria.editor.misc.FilesHolder;
import eu.janinko.andaria.editor.misc.RenderStyle;
import eu.janinko.andaria.ultimasdk.files.gumps.Gump;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.EnumSet;

@Slf4j
public class GumpView extends BitmapView {
    private final ObjectProperty<Gump> gump = new SimpleObjectProperty<>();

    public GumpView() {
        super(EnumSet.of(RenderStyle.BG_BLACK, RenderStyle.BG_WHITE, RenderStyle.BG_WHITESMOKE));
        gump.bind(Bindings.createObjectBinding(this::computeGump, filesHolder, imageID, redrawTrigger));
        gump.addListener((o) -> invalidate());
    }

    @Override
    public void invalidate() {
        drawImage();
    }

    private void drawImage() {
        Gump art1 = gump.get();
        if(art1 == null){
            blank();
            return;
        }
        imageView.setImage(painter.drawTopLeftImage(art1.getBitmap()));
    }

    private Gump computeGump() {
        FilesHolder files = filesHolder.get();
        if (files == null) {
            return null;
        }
        if (imageID.get() == null) {
            return null;
        }
        try {
            return files.getGumps().get(imageID.get());
        } catch (IOException e) {
            log.error("Chyba načítání animace {}", imageID.get(), e);
            return null;
        }
    }
}
