package java.awt.event;

import java.util.EventListener;

public interface InputMethodListener extends EventListener
{
    void inputMethodTextChanged(final InputMethodEvent p0);
    
    void caretPositionChanged(final InputMethodEvent p0);
}
