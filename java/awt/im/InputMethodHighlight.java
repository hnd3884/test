package java.awt.im;

import java.awt.font.TextAttribute;
import java.util.Map;

public class InputMethodHighlight
{
    public static final int RAW_TEXT = 0;
    public static final int CONVERTED_TEXT = 1;
    public static final InputMethodHighlight UNSELECTED_RAW_TEXT_HIGHLIGHT;
    public static final InputMethodHighlight SELECTED_RAW_TEXT_HIGHLIGHT;
    public static final InputMethodHighlight UNSELECTED_CONVERTED_TEXT_HIGHLIGHT;
    public static final InputMethodHighlight SELECTED_CONVERTED_TEXT_HIGHLIGHT;
    private boolean selected;
    private int state;
    private int variation;
    private Map<TextAttribute, ?> style;
    
    public InputMethodHighlight(final boolean b, final int n) {
        this(b, n, 0, null);
    }
    
    public InputMethodHighlight(final boolean b, final int n, final int n2) {
        this(b, n, n2, null);
    }
    
    public InputMethodHighlight(final boolean selected, final int state, final int variation, final Map<TextAttribute, ?> style) {
        this.selected = selected;
        if (state != 0 && state != 1) {
            throw new IllegalArgumentException("unknown input method highlight state");
        }
        this.state = state;
        this.variation = variation;
        this.style = style;
    }
    
    public boolean isSelected() {
        return this.selected;
    }
    
    public int getState() {
        return this.state;
    }
    
    public int getVariation() {
        return this.variation;
    }
    
    public Map<TextAttribute, ?> getStyle() {
        return this.style;
    }
    
    static {
        UNSELECTED_RAW_TEXT_HIGHLIGHT = new InputMethodHighlight(false, 0);
        SELECTED_RAW_TEXT_HIGHLIGHT = new InputMethodHighlight(true, 0);
        UNSELECTED_CONVERTED_TEXT_HIGHLIGHT = new InputMethodHighlight(false, 1);
        SELECTED_CONVERTED_TEXT_HIGHLIGHT = new InputMethodHighlight(true, 1);
    }
}
