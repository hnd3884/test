package java.awt.im.spi;

import javax.swing.JFrame;
import java.awt.Window;
import java.awt.font.TextHitInfo;
import java.text.AttributedCharacterIterator;
import java.awt.im.InputMethodRequests;

public interface InputMethodContext extends InputMethodRequests
{
    void dispatchInputMethodEvent(final int p0, final AttributedCharacterIterator p1, final int p2, final TextHitInfo p3, final TextHitInfo p4);
    
    Window createInputMethodWindow(final String p0, final boolean p1);
    
    JFrame createInputMethodJFrame(final String p0, final boolean p1);
    
    void enableClientWindowNotification(final InputMethod p0, final boolean p1);
}
