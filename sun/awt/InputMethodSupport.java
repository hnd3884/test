package sun.awt;

import java.util.Locale;
import java.awt.Window;
import sun.awt.im.InputContext;
import java.awt.AWTException;
import java.awt.im.spi.InputMethodDescriptor;

public interface InputMethodSupport
{
    InputMethodDescriptor getInputMethodAdapterDescriptor() throws AWTException;
    
    Window createInputMethodWindow(final String p0, final InputContext p1);
    
    boolean enableInputMethodsForTextComponent();
    
    Locale getDefaultKeyboardLocale();
}
