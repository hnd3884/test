package java.awt;

import java.awt.event.KeyEvent;

@FunctionalInterface
public interface KeyEventDispatcher
{
    boolean dispatchKeyEvent(final KeyEvent p0);
}
