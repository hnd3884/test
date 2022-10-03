package javax.swing.event;

import java.util.EventListener;

public interface DocumentListener extends EventListener
{
    void insertUpdate(final DocumentEvent p0);
    
    void removeUpdate(final DocumentEvent p0);
    
    void changedUpdate(final DocumentEvent p0);
}
