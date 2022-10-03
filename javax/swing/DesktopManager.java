package javax.swing;

public interface DesktopManager
{
    void openFrame(final JInternalFrame p0);
    
    void closeFrame(final JInternalFrame p0);
    
    void maximizeFrame(final JInternalFrame p0);
    
    void minimizeFrame(final JInternalFrame p0);
    
    void iconifyFrame(final JInternalFrame p0);
    
    void deiconifyFrame(final JInternalFrame p0);
    
    void activateFrame(final JInternalFrame p0);
    
    void deactivateFrame(final JInternalFrame p0);
    
    void beginDraggingFrame(final JComponent p0);
    
    void dragFrame(final JComponent p0, final int p1, final int p2);
    
    void endDraggingFrame(final JComponent p0);
    
    void beginResizingFrame(final JComponent p0, final int p1);
    
    void resizeFrame(final JComponent p0, final int p1, final int p2, final int p3, final int p4);
    
    void endResizingFrame(final JComponent p0);
    
    void setBoundsForFrame(final JComponent p0, final int p1, final int p2, final int p3, final int p4);
}
