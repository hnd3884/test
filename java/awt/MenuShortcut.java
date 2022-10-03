package java.awt;

import java.awt.event.KeyEvent;
import java.io.Serializable;

public class MenuShortcut implements Serializable
{
    int key;
    boolean usesShift;
    private static final long serialVersionUID = 143448358473180225L;
    
    public MenuShortcut(final int n) {
        this(n, false);
    }
    
    public MenuShortcut(final int key, final boolean usesShift) {
        this.key = key;
        this.usesShift = usesShift;
    }
    
    public int getKey() {
        return this.key;
    }
    
    public boolean usesShiftModifier() {
        return this.usesShift;
    }
    
    public boolean equals(final MenuShortcut menuShortcut) {
        return menuShortcut != null && menuShortcut.getKey() == this.key && menuShortcut.usesShiftModifier() == this.usesShift;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof MenuShortcut && this.equals((MenuShortcut)o);
    }
    
    @Override
    public int hashCode() {
        return this.usesShift ? (~this.key) : this.key;
    }
    
    @Override
    public String toString() {
        int menuShortcutKeyMask = 0;
        if (!GraphicsEnvironment.isHeadless()) {
            menuShortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        }
        if (this.usesShiftModifier()) {
            menuShortcutKeyMask |= 0x1;
        }
        return KeyEvent.getKeyModifiersText(menuShortcutKeyMask) + "+" + KeyEvent.getKeyText(this.key);
    }
    
    protected String paramString() {
        String s = "key=" + this.key;
        if (this.usesShiftModifier()) {
            s += ",usesShiftModifier";
        }
        return s;
    }
}
