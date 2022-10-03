package org.jfree.ui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;

public class ApplicationFrame extends JFrame implements WindowListener
{
    public ApplicationFrame(final String title) {
        super(title);
        this.addWindowListener(this);
    }
    
    public void windowActivated(final WindowEvent event) {
    }
    
    public void windowClosed(final WindowEvent event) {
    }
    
    public void windowClosing(final WindowEvent event) {
        if (event.getWindow() == this) {
            this.dispose();
            System.exit(0);
        }
    }
    
    public void windowDeactivated(final WindowEvent event) {
    }
    
    public void windowDeiconified(final WindowEvent event) {
    }
    
    public void windowIconified(final WindowEvent event) {
    }
    
    public void windowOpened(final WindowEvent event) {
    }
}
