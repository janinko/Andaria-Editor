module eu.janinko.andaria.editor {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.swing;
    requires java.logging;
    requires java.prefs;
    requires java.desktop;
    requires static lombok;
    
    requires eu.janinko.andaria.ultimasdk;
    requires eu.janinko.uo.spherescript;
    
    opens eu.janinko.andaria.editor to javafx.graphics;
    exports eu.janinko.andaria.editor.images;
}
