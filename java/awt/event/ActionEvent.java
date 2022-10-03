package java.awt.event;

import java.awt.AWTEvent;

public class ActionEvent extends AWTEvent
{
    public static final int SHIFT_MASK = 1;
    public static final int CTRL_MASK = 2;
    public static final int META_MASK = 4;
    public static final int ALT_MASK = 8;
    public static final int ACTION_FIRST = 1001;
    public static final int ACTION_LAST = 1001;
    public static final int ACTION_PERFORMED = 1001;
    String actionCommand;
    long when;
    int modifiers;
    private static final long serialVersionUID = -7671078796273832149L;
    
    public ActionEvent(final Object o, final int n, final String s) {
        this(o, n, s, 0);
    }
    
    public ActionEvent(final Object o, final int n, final String s, final int n2) {
        this(o, n, s, 0L, n2);
    }
    
    public ActionEvent(final Object o, final int n, final String actionCommand, final long when, final int modifiers) {
        super(o, n);
        this.actionCommand = actionCommand;
        this.when = when;
        this.modifiers = modifiers;
    }
    
    public String getActionCommand() {
        return this.actionCommand;
    }
    
    public long getWhen() {
        return this.when;
    }
    
    public int getModifiers() {
        return this.modifiers;
    }
    
    @Override
    public String paramString() {
        String s = null;
        switch (this.id) {
            case 1001: {
                s = "ACTION_PERFORMED";
                break;
            }
            default: {
                s = "unknown type";
                break;
            }
        }
        return s + ",cmd=" + this.actionCommand + ",when=" + this.when + ",modifiers=" + KeyEvent.getKeyModifiersText(this.modifiers);
    }
}
