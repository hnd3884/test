package java.awt.event;

import java.util.EventListener;

public interface KeyListener extends EventListener
{
    void keyTyped(final KeyEvent p0);
    
    void keyPressed(final KeyEvent p0);
    
    void keyReleased(final KeyEvent p0);
}
