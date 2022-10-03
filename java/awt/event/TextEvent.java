package java.awt.event;

import java.awt.AWTEvent;

public class TextEvent extends AWTEvent
{
    public static final int TEXT_FIRST = 900;
    public static final int TEXT_LAST = 900;
    public static final int TEXT_VALUE_CHANGED = 900;
    private static final long serialVersionUID = 6269902291250941179L;
    
    public TextEvent(final Object o, final int n) {
        super(o, n);
    }
    
    @Override
    public String paramString() {
        String s = null;
        switch (this.id) {
            case 900: {
                s = "TEXT_VALUE_CHANGED";
                break;
            }
            default: {
                s = "unknown type";
                break;
            }
        }
        return s;
    }
}
