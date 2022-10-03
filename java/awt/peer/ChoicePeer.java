package java.awt.peer;

public interface ChoicePeer extends ComponentPeer
{
    void add(final String p0, final int p1);
    
    void remove(final int p0);
    
    void removeAll();
    
    void select(final int p0);
}
