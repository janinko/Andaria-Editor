package eu.janinko.andaria.editor.misc;

import java.util.EnumSet;
import java.util.Iterator;

/**
 *
 * @author janinko
 */
public enum RenderStyle {
    BG_WHITESMOKE,
    BG_WHITE,
    BG_BLACK,
    ALIGN_GRID,
    MALE_GUMP,
    FEMALE_GUMP;


    public RenderStyle next(EnumSet<RenderStyle> available) {
        if (available.isEmpty()) {
            throw new IllegalArgumentException("Cannot select Render Style from empty set");
        }
        Iterator<RenderStyle> it = available.iterator();
        RenderStyle first = it.next();
        RenderStyle next = first;
        while (it.hasNext() && next.ordinal() <= this.ordinal()) {
            next = it.next();
        }
        if (next.ordinal() > this.ordinal()) {
            return next;
        } else {
            return first;
        }
    }
}
