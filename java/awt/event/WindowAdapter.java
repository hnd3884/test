package java.awt.event;

public abstract class WindowAdapter implements WindowListener, WindowStateListener, WindowFocusListener
{
    @Override
    public void windowOpened(final WindowEvent windowEvent) {
    }
    
    @Override
    public void windowClosing(final WindowEvent windowEvent) {
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
    
    @Override
    public void windowStateChanged(final WindowEvent windowEvent) {
    }
    
    @Override
    public void windowGainedFocus(final WindowEvent windowEvent) {
    }
    
    @Override
    public void windowLostFocus(final WindowEvent windowEvent) {
    }
}
