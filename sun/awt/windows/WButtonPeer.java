package sun.awt.windows;

import java.awt.event.KeyEvent;
import sun.awt.SunToolkit;
import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.peer.ButtonPeer;

final class WButtonPeer extends WComponentPeer implements ButtonPeer
{
    @Override
    public Dimension getMinimumSize() {
        final FontMetrics fontMetrics = this.getFontMetrics(((Button)this.target).getFont());
        String label = ((Button)this.target).getLabel();
        if (label == null) {
            label = "";
        }
        return new Dimension(fontMetrics.stringWidth(label) + 14, fontMetrics.getHeight() + 8);
    }
    
    @Override
    public boolean isFocusable() {
        return true;
    }
    
    @Override
    public native void setLabel(final String p0);
    
    WButtonPeer(final Button button) {
        super(button);
    }
    
    @Override
    native void create(final WComponentPeer p0);
    
    public void handleAction(final long n, final int n2) {
        SunToolkit.executeOnEventHandlerThread(this.target, new Runnable() {
            @Override
            public void run() {
                WButtonPeer.this.postEvent(new ActionEvent(WButtonPeer.this.target, 1001, ((Button)WButtonPeer.this.target).getActionCommand(), n, n2));
            }
        }, n);
    }
    
    @Override
    public boolean shouldClearRectBeforePaint() {
        return false;
    }
    
    private static native void initIDs();
    
    @Override
    public boolean handleJavaKeyEvent(final KeyEvent keyEvent) {
        switch (keyEvent.getID()) {
            case 402: {
                if (keyEvent.getKeyCode() == 32) {
                    this.handleAction(keyEvent.getWhen(), keyEvent.getModifiers());
                    break;
                }
                break;
            }
        }
        return false;
    }
    
    static {
        initIDs();
    }
}
