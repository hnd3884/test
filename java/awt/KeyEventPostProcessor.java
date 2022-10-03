package java.awt;

import java.awt.event.KeyEvent;

@FunctionalInterface
public interface KeyEventPostProcessor
{
    boolean postProcessKeyEvent(final KeyEvent p0);
}
