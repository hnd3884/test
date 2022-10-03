package java.awt.event;

import sun.awt.SunToolkit;
import sun.awt.AWTAccessor;
import java.io.IOException;
import java.awt.EventQueue;
import java.io.ObjectInputStream;
import java.awt.Component;
import java.awt.font.TextHitInfo;
import java.text.AttributedCharacterIterator;
import java.awt.AWTEvent;

public class InputMethodEvent extends AWTEvent
{
    private static final long serialVersionUID = 4727190874778922661L;
    public static final int INPUT_METHOD_FIRST = 1100;
    public static final int INPUT_METHOD_TEXT_CHANGED = 1100;
    public static final int CARET_POSITION_CHANGED = 1101;
    public static final int INPUT_METHOD_LAST = 1101;
    long when;
    private transient AttributedCharacterIterator text;
    private transient int committedCharacterCount;
    private transient TextHitInfo caret;
    private transient TextHitInfo visiblePosition;
    
    public InputMethodEvent(final Component component, final int n, final long when, final AttributedCharacterIterator text, final int committedCharacterCount, final TextHitInfo caret, final TextHitInfo visiblePosition) {
        super(component, n);
        if (n < 1100 || n > 1101) {
            throw new IllegalArgumentException("id outside of valid range");
        }
        if (n == 1101 && text != null) {
            throw new IllegalArgumentException("text must be null for CARET_POSITION_CHANGED");
        }
        this.when = when;
        this.text = text;
        int n2 = 0;
        if (text != null) {
            n2 = text.getEndIndex() - text.getBeginIndex();
        }
        if (committedCharacterCount < 0 || committedCharacterCount > n2) {
            throw new IllegalArgumentException("committedCharacterCount outside of valid range");
        }
        this.committedCharacterCount = committedCharacterCount;
        this.caret = caret;
        this.visiblePosition = visiblePosition;
    }
    
    public InputMethodEvent(final Component component, final int n, final AttributedCharacterIterator attributedCharacterIterator, final int n2, final TextHitInfo textHitInfo, final TextHitInfo textHitInfo2) {
        this(component, n, getMostRecentEventTimeForSource(component), attributedCharacterIterator, n2, textHitInfo, textHitInfo2);
    }
    
    public InputMethodEvent(final Component component, final int n, final TextHitInfo textHitInfo, final TextHitInfo textHitInfo2) {
        this(component, n, getMostRecentEventTimeForSource(component), null, 0, textHitInfo, textHitInfo2);
    }
    
    public AttributedCharacterIterator getText() {
        return this.text;
    }
    
    public int getCommittedCharacterCount() {
        return this.committedCharacterCount;
    }
    
    public TextHitInfo getCaret() {
        return this.caret;
    }
    
    public TextHitInfo getVisiblePosition() {
        return this.visiblePosition;
    }
    
    public void consume() {
        this.consumed = true;
    }
    
    public boolean isConsumed() {
        return this.consumed;
    }
    
    public long getWhen() {
        return this.when;
    }
    
    @Override
    public String paramString() {
        String s = null;
        switch (this.id) {
            case 1100: {
                s = "INPUT_METHOD_TEXT_CHANGED";
                break;
            }
            case 1101: {
                s = "CARET_POSITION_CHANGED";
                break;
            }
            default: {
                s = "unknown type";
                break;
            }
        }
        String string;
        if (this.text == null) {
            string = "no text";
        }
        else {
            final StringBuilder sb = new StringBuilder("\"");
            int committedCharacterCount = this.committedCharacterCount;
            char c = this.text.first();
            while (committedCharacterCount-- > 0) {
                sb.append(c);
                c = this.text.next();
            }
            sb.append("\" + \"");
            while (c != '\uffff') {
                sb.append(c);
                c = this.text.next();
            }
            sb.append("\"");
            string = sb.toString();
        }
        final String string2 = this.committedCharacterCount + " characters committed";
        String string3;
        if (this.caret == null) {
            string3 = "no caret";
        }
        else {
            string3 = "caret: " + this.caret.toString();
        }
        String string4;
        if (this.visiblePosition == null) {
            string4 = "no visible position";
        }
        else {
            string4 = "visible position: " + this.visiblePosition.toString();
        }
        return s + ", " + string + ", " + string2 + ", " + string3 + ", " + string4;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        if (this.when == 0L) {
            this.when = EventQueue.getMostRecentEventTime();
        }
    }
    
    private static long getMostRecentEventTimeForSource(final Object o) {
        if (o == null) {
            throw new IllegalArgumentException("null source");
        }
        return AWTAccessor.getEventQueueAccessor().getMostRecentEventTime(SunToolkit.getSystemEventQueueImplPP(SunToolkit.targetToAppContext(o)));
    }
}
