package eu.janinko.andaria.editor.misc;

import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import eu.janinko.andaria.editor.EditorApp;
import eu.janinko.andaria.editor.components.TaskProgressBar;
import eu.janinko.andaria.ultimasdk.UOFileSizes;
import eu.janinko.andaria.ultimasdk.UOFiles;
import eu.janinko.andaria.ultimasdk.files.*;
import eu.janinko.andaria.ultimasdk.files.anims.Body;
import eu.janinko.andaria.ultimasdk.utils.AnimResolver;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import lombok.Getter;

/**
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class FilesHolder {
    private UOFiles files;

    public Path getFilesPath() {
        return files.getFilePath();
    }

    public Body getBody(int i) throws IOException {
        AnimResolver.AnimationInfo ai = AnimResolver.resolve(i, files.getBody(), files.getBodyConv());

        Anims anims = getAnims(ai.animFile());
        return anims.getBody(ai.idInFile());
    }

    public eu.janinko.andaria.ultimasdk.files.Body getBodyDef() {
        try {
            return files.getBody();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BodyConv getBodyConv() {
        try {
            return files.getBodyConv();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Anims getAnims(Anims.AnimFile animFile) {
        try {
            return files.getAnims(animFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Arts getArts() {
        try {
            return files.getArts();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Gumps getGumps() {
        try {
            return files.getGumps();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Hues getHues() {
        try {
            return files.getHues();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TileData getTileData() {
        try {
            return files.getTileData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CliLocs getCliLocs() {
        try {
            return files.getCliLocs();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FilesHolder(UOFiles files) {
        this.files = files;
    }


    public static FilesHolder loadFiles(Path files, Stage primaryStage) throws InterruptedException {
        final LoadTask task = new LoadTask(files);
        TaskProgressBar<FilesHolder> tpb = new TaskProgressBar<>(task, primaryStage);

        tpb.setTitle("Načítání souborů");

        try {
            return tpb.startAndWait();
        } catch (ExecutionException e) {
            throw new RuntimeException("Chyba při načítání souborů: " + e.getLocalizedMessage(), e);
        }
    }

    private static void prepareOutputDirectory(Path output) {
        if (!java.nio.file.Files.exists(output)) {
            try {
                java.nio.file.Files.createDirectories(output);
            } catch (IOException ex) {
                throw new IllegalArgumentException("Nejde vytvořit složka do složky '" + output + "'.", ex);
            }
        }
        if (!java.nio.file.Files.isDirectory(output) || !java.nio.file.Files.isWritable(output)) {
            throw new IllegalArgumentException("Nejde zapisovat do složky '" + output + "'.");
        }
    }

    public FilesHolder reload() {
        try {
            FilesHolder filesHolder = loadFiles(files.getFilePath(), null);
            files.close();
            return filesHolder;
        } catch (Exception e) {
            EditorApp.showError("Chyba při načítání souborů", e);
            Platform.exit();
            return null;
        }
    }

    public void save(EnumSet<FileType> filesTypes, Path outputPath) {
        if (getFilesPath().equals(outputPath)) {
            throw new IllegalArgumentException("Složka pro uložení souborů se musí lišit od složky se načtenými soubory.");
        }
        prepareOutputDirectory(outputPath);

        final SaveTask task = new SaveTask(files, outputPath, filesTypes);
        TaskProgressBar<Void> tpb = new TaskProgressBar<>(task, null);

        tpb.setTitle("Ukládání souborů");

        try {
            tpb.startAndWait();
        } catch (ExecutionException e) {
            throw new RuntimeException("Chyba při načítání souborů: " + e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            Platform.exit();
            throw new RuntimeException(e);
        }
    }

    public enum FileType{
        ARTS("arty", UOFiles::getArts, UOFiles::saveArts, UOFileSizes::loadSizeOfArts),
        CLILOC("cliloc", UOFiles::getCliLocs, UOFiles::saveCliLocs, UOFileSizes::loadSizeOfCliLocs),
        GUMPS("gumpy", UOFiles::getGumps, UOFiles::saveGumps, UOFileSizes::loadSizeOfGumps),
        TILEDATA("tiledata", UOFiles::getTileData, UOFiles::saveTileData, UOFileSizes::loadSizeOfTileData),
        HUES("hues", UOFiles::getHues, UOFiles::saveHues, UOFileSizes::loadSizeOfHues),
        ANIM("anims", UOFiles::getAnims, UOFiles::saveAnims, UOFileSizes::loadSizeOfAnims),
        ANIM2("anims2", UOFiles::getAnims2, UOFiles::saveAnims2, UOFileSizes::loadSizeOfAnims2),
        ANIM3("anims3", UOFiles::getAnims3, UOFiles::saveAnims3, UOFileSizes::loadSizeOfAnims3),
        ANIM4("anims4", UOFiles::getAnims4, UOFiles::saveAnims4, UOFileSizes::loadSizeOfAnims4),
        ANIM5("anims5", UOFiles::getAnims5, UOFiles::saveAnims5, UOFileSizes::loadSizeOfAnims5),
        BODY("body", UOFiles::getBody, UOFiles::saveBody, UOFileSizes::loadSizeOfBody),
        BODY_CONV("body conv", UOFiles::getBodyConv, UOFiles::saveBodyConv, UOFileSizes::loadSizeOfBodyConv),;

        @Getter
        private final String name;
        private final CheckedBiConsumer<UOFiles, Path> save;
        private final CheckedFunction<UOFileSizes, Long> size;
        private final CheckedFunction<UOFiles, ?> load;

        FileType(String name, CheckedFunction<UOFiles, ?> load, CheckedBiConsumer<UOFiles, Path> save, CheckedFunction<UOFileSizes, Long> size) {
            this.name = name;
            this.load = load;
            this.save = save;
            this.size = size;
        }
    }

    @FunctionalInterface
    public interface CheckedFunction<T, R> {
        R apply(T t) throws IOException;
    }
    @FunctionalInterface
    public interface CheckedBiConsumer<T, U> {
        void accept(T t, U u) throws IOException;
    }

    private static class SaveTask extends Task<Void> {
        private final EnumSet<FileType> toSave;
        private final UOFiles uoFiles;
        private final Path outPath;

        private SaveTask(UOFiles uoFiles, Path outPath, EnumSet<FileType> toSave) {
            this.toSave = toSave;
            this.uoFiles = uoFiles;
            this.outPath = outPath;
        }

        @Override
        protected Void call() throws Exception {
            UOFileSizes sizes = new UOFileSizes(uoFiles.getFilePath());

            long totalSize = 0;
            for (FileType fileType : toSave) {
                totalSize += fileType.size.apply(sizes);
            }

            long progress = 0;
            for (FileType fileType : toSave) {
                fileType.save.accept(uoFiles, outPath);
                progress += fileType.size.apply(sizes);
                updateProgress(progress, totalSize);
            }

            return null;
        }
    }

    private static class LoadTask extends Task<FilesHolder> {

        private final Path files;

        private LoadTask(Path files) {
            this.files = files;
        }

        @Override
        protected FilesHolder call() throws Exception {
            UOFiles uoFiles = new UOFiles(files);
            UOFileSizes sizes = new UOFileSizes(files);

            long totalSize = 0;
            for (FileType value : FileType.values()) {
                totalSize += value.size.apply(sizes);
            }

            long progress = 0;
            for (FileType value : FileType.values()) {
                value.load.apply(uoFiles);
                progress += value.size.apply(sizes);
                updateProgress(progress, totalSize);
            }

            return new FilesHolder(uoFiles);
        }

    }
}
