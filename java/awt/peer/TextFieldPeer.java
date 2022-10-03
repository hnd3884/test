package java.awt.peer;

import java.awt.Dimension;

public interface TextFieldPeer extends TextComponentPeer
{
    void setEchoChar(final char p0);
    
    Dimension getPreferredSize(final int p0);
    
    Dimension getMinimumSize(final int p0);
}
