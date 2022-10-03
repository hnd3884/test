package java.awt.im.spi;

import java.awt.Image;
import java.awt.AWTException;
import java.util.Locale;

public interface InputMethodDescriptor
{
    Locale[] getAvailableLocales() throws AWTException;
    
    boolean hasDynamicLocaleList();
    
    String getInputMethodDisplayName(final Locale p0, final Locale p1);
    
    Image getInputMethodIcon(final Locale p0);
    
    InputMethod createInputMethod() throws Exception;
}
