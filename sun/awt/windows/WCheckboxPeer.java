package sun.awt.windows;

import sun.awt.SunToolkit;
import java.awt.AWTEvent;
import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Checkbox;
import java.awt.Dimension;
import java.awt.CheckboxGroup;
import java.awt.peer.CheckboxPeer;

final class WCheckboxPeer extends WComponentPeer implements CheckboxPeer
{
    @Override
    public native void setState(final boolean p0);
    
    @Override
    public native void setCheckboxGroup(final CheckboxGroup p0);
    
    @Override
    public native void setLabel(final String p0);
    
    private static native int getCheckMarkSize();
    
    @Override
    public Dimension getMinimumSize() {
        String label = ((Checkbox)this.target).getLabel();
        final int checkMarkSize = getCheckMarkSize();
        if (label == null) {
            label = "";
        }
        final FontMetrics fontMetrics = this.getFontMetrics(((Checkbox)this.target).getFont());
        return new Dimension(fontMetrics.stringWidth(label) + checkMarkSize / 2 + checkMarkSize, Math.max(fontMetrics.getHeight() + 8, checkMarkSize));
    }
    
    @Override
    public boolean isFocusable() {
        return true;
    }
    
    WCheckboxPeer(final Checkbox checkbox) {
        super(checkbox);
    }
    
    @Override
    native void create(final WComponentPeer p0);
    
    @Override
    void initialize() {
        final Checkbox checkbox = (Checkbox)this.target;
        this.setState(checkbox.getState());
        this.setCheckboxGroup(checkbox.getCheckboxGroup());
        final Color background = ((Component)this.target).getBackground();
        if (background != null) {
            this.setBackground(background);
        }
        super.initialize();
    }
    
    @Override
    public boolean shouldClearRectBeforePaint() {
        return false;
    }
    
    void handleAction(final boolean b) {
        final Checkbox checkbox = (Checkbox)this.target;
        SunToolkit.executeOnEventHandlerThread(checkbox, new Runnable() {
            @Override
            public void run() {
                final CheckboxGroup checkboxGroup = checkbox.getCheckboxGroup();
                if (checkboxGroup != null && checkbox == checkboxGroup.getSelectedCheckbox() && checkbox.getState()) {
                    return;
                }
                checkbox.setState(b);
                WCheckboxPeer.this.postEvent(new ItemEvent(checkbox, 701, checkbox.getLabel(), b ? 1 : 2));
            }
        });
    }
}
