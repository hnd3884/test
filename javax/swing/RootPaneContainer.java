package javax.swing;

import java.awt.Component;
import java.awt.Container;

public interface RootPaneContainer
{
    JRootPane getRootPane();
    
    void setContentPane(final Container p0);
    
    Container getContentPane();
    
    void setLayeredPane(final JLayeredPane p0);
    
    JLayeredPane getLayeredPane();
    
    void setGlassPane(final Component p0);
    
    Component getGlassPane();
}
