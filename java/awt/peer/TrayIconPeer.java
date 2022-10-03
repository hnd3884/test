package java.awt.peer;

public interface TrayIconPeer
{
    void dispose();
    
    void setToolTip(final String p0);
    
    void updateImage();
    
    void displayMessage(final String p0, final String p1, final String p2);
    
    void showPopupMenu(final int p0, final int p1);
}
