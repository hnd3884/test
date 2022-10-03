package sun.security.tools.policytool;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

class ChildWindowListener implements WindowListener
{
    private ToolDialog td;
    
    ChildWindowListener(final ToolDialog td) {
        this.td = td;
    }
    
    @Override
    public void windowOpened(final WindowEvent windowEvent) {
    }
    
    @Override
    public void windowClosing(final WindowEvent windowEvent) {
        this.td.setVisible(false);
        this.td.dispose();
    }
    
    @Override
    public void windowClosed(final WindowEvent windowEvent) {
    }
    
    @Override
    public void windowIconified(final WindowEvent windowEvent) {
    }
    
    @Override
    public void windowDeiconified(final WindowEvent windowEvent) {
    }
    
    @Override
    public void windowActivated(final WindowEvent windowEvent) {
    }
    
    @Override
    public void windowDeactivated(final WindowEvent windowEvent) {
    }
}
