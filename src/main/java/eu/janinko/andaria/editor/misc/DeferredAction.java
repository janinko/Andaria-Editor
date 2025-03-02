package eu.janinko.andaria.editor.misc;

import javafx.application.Platform;

import java.util.concurrent.atomic.AtomicBoolean;

public class DeferredAction {

    private final AtomicBoolean pendingInvalidation = new AtomicBoolean(false);
    private final Runnable action;

    public DeferredAction(Runnable action) {
        this.action = action;
    }

    public void invalidate() {
        if (!pendingInvalidation.getAndSet(true)) {
            Platform.runLater(() -> {
                // Signal that the UI is processing the pending invalidation, so any additional invalidations must schedule another update.
                pendingInvalidation.set(false);
                action.run();
            });
        }
    }
}