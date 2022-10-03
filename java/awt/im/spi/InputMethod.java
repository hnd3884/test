package java.awt.im.spi;

import java.awt.Rectangle;
import java.awt.AWTEvent;
import java.util.Locale;

public interface InputMethod
{
    void setInputMethodContext(final InputMethodContext p0);
    
    boolean setLocale(final Locale p0);
    
    Locale getLocale();
    
    void setCharacterSubsets(final Character.Subset[] p0);
    
    void setCompositionEnabled(final boolean p0);
    
    boolean isCompositionEnabled();
    
    void reconvert();
    
    void dispatchEvent(final AWTEvent p0);
    
    void notifyClientWindowChange(final Rectangle p0);
    
    void activate();
    
    void deactivate(final boolean p0);
    
    void hideWindows();
    
    void removeNotify();
    
    void endComposition();
    
    void dispose();
    
    Object getControlObject();
}
