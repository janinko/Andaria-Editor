package eu.janinko.andaria.editor.cp;

import javax.swing.JTextField;

import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.text.AttributedCharacterIterator;

/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class JLimitedTextField extends JTextField implements InputMethodListener{
    private int limit;

    public JLimitedTextField(int limit) {
        super(limit);
        this.limit = limit;
        addInputMethodListener(this);
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public void inputMethodTextChanged(InputMethodEvent ime) {
        AttributedCharacterIterator text = ime.getText();
        if(text != null && text.getEndIndex() > limit){
            ime.consume();
        }
    }

    @Override
    public void caretPositionChanged(InputMethodEvent ime) {
    }
    
    
    
}
