package java.awt.peer;

import java.awt.Dimension;

public interface TextAreaPeer extends TextComponentPeer
{
    void insert(final String p0, final int p1);
    
    void replaceRange(final String p0, final int p1, final int p2);
    
    Dimension getPreferredSize(final int p0, final int p1);
    
    Dimension getMinimumSize(final int p0, final int p1);
}
