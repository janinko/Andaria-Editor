package eu.janinko.andaria.editor.components;

import eu.janinko.andaria.editor.misc.FilesHolder;
import eu.janinko.andaria.editor.misc.RenderStyle;
import eu.janinko.andaria.ultimasdk.files.Anims;
import eu.janinko.andaria.ultimasdk.files.anims.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

@Slf4j
public class AnimView extends BitmapView {
    private final ObjectProperty<Body> body = new SimpleObjectProperty<>();

    private final ObjectProperty<Body.Direction> direction = new SimpleObjectProperty<>(Body.Direction.LEFT);
    private final ObjectProperty<Body.Action> action = new SimpleObjectProperty<>();
    private final BooleanProperty playing = new SimpleBooleanProperty(true);


    private final Timeline animTimeline = new Timeline();
    private final ObservableList<Image> frames = FXCollections.observableArrayList();
    private final IntegerProperty currentFrame = new SimpleIntegerProperty(0);

    private final BooleanProperty timelinePlaying = new SimpleBooleanProperty(false);

    private final ObservableList<Body.Action> availableActions = FXCollections.observableArrayList();

    @Setter
    private Anims.AnimFile animFile;

    public AnimView() {
        super(EnumSet.of(RenderStyle.BG_BLACK, RenderStyle.BG_WHITE, RenderStyle.BG_WHITESMOKE, RenderStyle.ALIGN_GRID));
        painter.renderStyleProperty().set(RenderStyle.ALIGN_GRID);
        animTimeline.setCycleCount(Timeline.INDEFINITE);
        animTimeline.setAutoReverse(false);

        currentFrame.addListener((o) -> drawImage());

        body.bind(Bindings.createObjectBinding(this::computeBody, filesHolder, imageID, redrawTrigger));

        body.addListener(this::updateAvailableActions);
        body.addListener((o) -> invalidate());
        direction.addListener((o) -> invalidate());
        action.addListener((o) -> invalidate());

        timelinePlaying.addListener((p, o, n) -> {
            if (n) animTimeline.play();
            else animTimeline.stop();
        });
        timelinePlaying.bind(Bindings.createBooleanBinding(() -> playing.get() && !frames.isEmpty(), playing, frames));
        setStyle("--fx-background-color: orange");
    }

    private void drawImage() {
        int idx = currentFrame.intValue();
        if (idx < frames.size()) {
            imageView.setImage(frames.get(idx));
        } else {
            blank();
        }
    }

    public Body.Direction getDirection() {
        return direction.get();
    }

    public void setDirection(Body.Direction direction) {
        this.direction.set(direction);
    }

    public ObjectProperty<Body.Direction> directionProperty() {
        return direction;
    }

    public Body.Action getAction() {
        return action.get();
    }

    public void setAction(Body.Action action) {
        this.action.set(action);
    }

    public ObjectProperty<Body.Action> actionProperty() {
        return action;
    }

    public boolean isPlaying() {
        return playing.get();
    }

    public void setPlaying(boolean id) {
        playing.set(id);
    }

    public BooleanProperty playingProperty() {
        return playing;
    }

    public void invalidate() {
        frames.clear();
        List<Image> frms = computeFrames();
        if (frms.isEmpty()) {
            System.out.println("Frames empty");
            drawImage();
            return;
        }

        KeyFrame keyFrame = new KeyFrame(Duration.millis(100), (e) -> {
            int count = frames.size();
            if (count == 0) {
                return;
            }
            int nextFrame = currentFrame.get() + 1;
            currentFrame.set(nextFrame % count);
        });
        animTimeline.getKeyFrames().clear();
        animTimeline.getKeyFrames().add(keyFrame);

        frames.addAll(frms);
        currentFrame.set(0);
        drawImage(); // in case it was 0 already
    }

    private Body computeBody() {
        FilesHolder files = filesHolder.get();
        if (files == null) {
            return null;
        }
        if (imageID.get() == null) {
            return null;
        }
        try {
            if (animFile == null) {
                return files.getBody(imageID.get());
            } else {
                return files.getAnims(animFile).getBody(imageID.get());
            }
        } catch (IOException e) {
            log.error("Chyba načítání animace {}", imageID.get(), e);
            return null;
        }
    }

    private List<Image> computeFrames() {
        Body b = body.get();
        if (b == null) {
            return List.of();
        }
        Anim anim = b.getAnim(action(), direction());
        if (anim == null) {
            return List.of();
        }

        int width = 0;
        int biasUp = 0;
        int biasDown = 0;
        for (int i = 0; i < anim.frameCount(); i++) {
            Frame frame = anim.getFrame(i);
            int w = frame.getViewWidth();
            if (w > width) {
                width = w;
            }

            int bu = frame.biasUp();
            if (bu > biasUp) {
                biasUp = bu;
            }

            int bd = frame.biasDown();
            if (bd > biasDown) {
                biasDown = bd;
            }
        }

        Image[] frms = new Image[anim.frameCount()];
        for (int i = 0; i < anim.frameCount(); i++) {
            Frame frame = anim.getFrame(i);
            frms[i] = painter.drawFrame(frame, width, biasUp, biasDown);
        }
        return List.of(frms);
    }

    private Body.Direction direction() {
        Body.Direction dir = direction.get();
        if (dir == null) {
            dir = Body.Direction.LEFT;
        }
        return dir;
    }

    public ObservableList<Body.Action> availableActions() {
        return availableActions;
    }

    private void updateAvailableActions(ObservableValue<? extends Body> p, Body oldBody, Body newBody) {
        if (newBody == null) {
            availableActions.clear();
        } else {
            if (oldBody == null || !oldBody.getClass().equals(newBody.getClass())) {
                availableActions.clear();
                availableActions.addAll(newBody.getActions());
            }
        }
    }

    private Body.Action action() {
        Body b = body.get();
        if (b == null) {
            return BodyChar.ActionChar.WALK_UNARMED;
        }
        Body.Action act = action.get();
        if (act == null) {
            return BodyChar.ActionChar.WALK_UNARMED;
        }

        for (Body.Action bAction : b.getActions()) {
            if (bAction == act) {
                return act;
            }
        }
        return availableActions.get(0);
    }


}
