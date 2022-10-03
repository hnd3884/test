package java.awt.im;

import java.awt.Component;
import java.awt.AWTEvent;
import java.beans.Transient;
import java.util.Locale;
import sun.awt.im.InputMethodContext;

public class InputContext
{
    protected InputContext() {
    }
    
    public static InputContext getInstance() {
        return new InputMethodContext();
    }
    
    public boolean selectInputMethod(final Locale locale) {
        return false;
    }
    
    public Locale getLocale() {
        return null;
    }
    
    public void setCharacterSubsets(final Character.Subset[] array) {
    }
    
    public void setCompositionEnabled(final boolean b) {
    }
    
    @Transient
    public boolean isCompositionEnabled() {
        return false;
    }
    
    public void reconvert() {
    }
    
    public void dispatchEvent(final AWTEvent awtEvent) {
    }
    
    public void removeNotify(final Component component) {
    }
    
    public void endComposition() {
    }
    
    public void dispose() {
    }
    
    public Object getInputMethodControlObject() {
        return null;
    }
}
