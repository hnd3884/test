package java.awt.peer;

import java.awt.Dimension;

public interface ListPeer extends ComponentPeer
{
    int[] getSelectedIndexes();
    
    void add(final String p0, final int p1);
    
    void delItems(final int p0, final int p1);
    
    void removeAll();
    
    void select(final int p0);
    
    void deselect(final int p0);
    
    void makeVisible(final int p0);
    
    void setMultipleMode(final boolean p0);
    
    Dimension getPreferredSize(final int p0);
    
    Dimension getMinimumSize(final int p0);
}
