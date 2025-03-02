package eu.janinko.andaria.editor.misc;

import javafx.util.converter.IntegerStringConverter;

public class Utils {
    public static IntegerStringConverter unsignedStringConverter(int limit) {
        return new IntegerStringConverter() {
            @Override
            public Integer fromString(String s) {
                Integer i = super.fromString(s);
                if (i == null || i < 0 || i > limit) {
                    return null;
                } else return i;
            }
        };
    }
}
