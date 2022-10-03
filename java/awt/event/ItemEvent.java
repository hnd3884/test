package java.awt.event;

import java.awt.ItemSelectable;
import java.awt.AWTEvent;

public class ItemEvent extends AWTEvent
{
    public static final int ITEM_FIRST = 701;
    public static final int ITEM_LAST = 701;
    public static final int ITEM_STATE_CHANGED = 701;
    public static final int SELECTED = 1;
    public static final int DESELECTED = 2;
    Object item;
    int stateChange;
    private static final long serialVersionUID = -608708132447206933L;
    
    public ItemEvent(final ItemSelectable itemSelectable, final int n, final Object item, final int stateChange) {
        super(itemSelectable, n);
        this.item = item;
        this.stateChange = stateChange;
    }
    
    public ItemSelectable getItemSelectable() {
        return (ItemSelectable)this.source;
    }
    
    public Object getItem() {
        return this.item;
    }
    
    public int getStateChange() {
        return this.stateChange;
    }
    
    @Override
    public String paramString() {
        String s = null;
        switch (this.id) {
            case 701: {
                s = "ITEM_STATE_CHANGED";
                break;
            }
            default: {
                s = "unknown type";
                break;
            }
        }
        String s2 = null;
        switch (this.stateChange) {
            case 1: {
                s2 = "SELECTED";
                break;
            }
            case 2: {
                s2 = "DESELECTED";
                break;
            }
            default: {
                s2 = "unknown type";
                break;
            }
        }
        return s + ",item=" + this.item + ",stateChange=" + s2;
    }
}
