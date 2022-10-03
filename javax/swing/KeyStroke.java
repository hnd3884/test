package javax.swing;

import java.awt.event.KeyEvent;
import java.awt.AWTKeyStroke;

public class KeyStroke extends AWTKeyStroke
{
    private static final long serialVersionUID = -9060180771037902530L;
    
    private KeyStroke() {
    }
    
    private KeyStroke(final char c, final int n, final int n2, final boolean b) {
        super(c, n, n2, b);
    }
    
    public static KeyStroke getKeyStroke(final char c) {
        synchronized (AWTKeyStroke.class) {
            AWTKeyStroke.registerSubclass(KeyStroke.class);
            return (KeyStroke)AWTKeyStroke.getAWTKeyStroke(c);
        }
    }
    
    @Deprecated
    public static KeyStroke getKeyStroke(final char c, final boolean b) {
        return new KeyStroke(c, 0, 0, b);
    }
    
    public static KeyStroke getKeyStroke(final Character c, final int n) {
        synchronized (AWTKeyStroke.class) {
            AWTKeyStroke.registerSubclass(KeyStroke.class);
            return (KeyStroke)AWTKeyStroke.getAWTKeyStroke(c, n);
        }
    }
    
    public static KeyStroke getKeyStroke(final int n, final int n2, final boolean b) {
        synchronized (AWTKeyStroke.class) {
            AWTKeyStroke.registerSubclass(KeyStroke.class);
            return (KeyStroke)AWTKeyStroke.getAWTKeyStroke(n, n2, b);
        }
    }
    
    public static KeyStroke getKeyStroke(final int n, final int n2) {
        synchronized (AWTKeyStroke.class) {
            AWTKeyStroke.registerSubclass(KeyStroke.class);
            return (KeyStroke)AWTKeyStroke.getAWTKeyStroke(n, n2);
        }
    }
    
    public static KeyStroke getKeyStrokeForEvent(final KeyEvent keyEvent) {
        synchronized (AWTKeyStroke.class) {
            AWTKeyStroke.registerSubclass(KeyStroke.class);
            return (KeyStroke)AWTKeyStroke.getAWTKeyStrokeForEvent(keyEvent);
        }
    }
    
    public static KeyStroke getKeyStroke(final String s) {
        if (s == null || s.length() == 0) {
            return null;
        }
        synchronized (AWTKeyStroke.class) {
            AWTKeyStroke.registerSubclass(KeyStroke.class);
            try {
                return (KeyStroke)AWTKeyStroke.getAWTKeyStroke(s);
            }
            catch (final IllegalArgumentException ex) {
                return null;
            }
        }
    }
}
