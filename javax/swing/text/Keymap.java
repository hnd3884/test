package javax.swing.text;

import javax.swing.KeyStroke;
import javax.swing.Action;

public interface Keymap
{
    String getName();
    
    Action getDefaultAction();
    
    void setDefaultAction(final Action p0);
    
    Action getAction(final KeyStroke p0);
    
    KeyStroke[] getBoundKeyStrokes();
    
    Action[] getBoundActions();
    
    KeyStroke[] getKeyStrokesForAction(final Action p0);
    
    boolean isLocallyDefined(final KeyStroke p0);
    
    void addActionForKeyStroke(final KeyStroke p0, final Action p1);
    
    void removeKeyStrokeBinding(final KeyStroke p0);
    
    void removeBindings();
    
    Keymap getResolveParent();
    
    void setResolveParent(final Keymap p0);
}
