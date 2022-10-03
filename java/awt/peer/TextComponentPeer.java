package java.awt.peer;

import java.awt.im.InputMethodRequests;

public interface TextComponentPeer extends ComponentPeer
{
    void setEditable(final boolean p0);
    
    String getText();
    
    void setText(final String p0);
    
    int getSelectionStart();
    
    int getSelectionEnd();
    
    void select(final int p0, final int p1);
    
    void setCaretPosition(final int p0);
    
    int getCaretPosition();
    
    InputMethodRequests getInputMethodRequests();
}
