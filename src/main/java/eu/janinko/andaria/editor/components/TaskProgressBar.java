package eu.janinko.andaria.editor.components;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class TaskProgressBar<R> extends Stage {

    private final Task<R> task;

    @FXML
    public ProgressBar progressBar;

    public TaskProgressBar(Task<R> task, Window owner) {
        try {
            this.task = task;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TaskProgressBar.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();

            initModality(Modality.APPLICATION_MODAL);
            initOwner(owner);

            setOnCloseRequest(this::onCloseRequest);

            progressBar.progressProperty().bind(task.progressProperty());

            task.setOnCancelled(this::onFinished);
            task.setOnFailed(this::onFinished);
            task.setOnSucceeded(this::onFinished);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public R startAndWait() throws ExecutionException, InterruptedException {
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
        showAndWait();
        if(!task.isDone()) {
            throw new IllegalStateException("Stopped waiting while task is not done!");
        }
        return task.get();
    }

    private void onCloseRequest(WindowEvent e) {
        task.cancel(true);
        e.consume();
    }

    private void onFinished(WorkerStateEvent h) {
        close();
    }
}
