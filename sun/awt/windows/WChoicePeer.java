package sun.awt.windows;

import java.awt.AWTEvent;
import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import sun.awt.SunToolkit;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Choice;
import java.awt.Dimension;
import java.awt.event.WindowListener;
import java.awt.peer.ChoicePeer;

final class WChoicePeer extends WComponentPeer implements ChoicePeer
{
    private WindowListener windowListener;
    
    @Override
    public Dimension getMinimumSize() {
        final FontMetrics fontMetrics = this.getFontMetrics(((Choice)this.target).getFont());
        final Choice choice = (Choice)this.target;
        int max = 0;
        int itemCount = choice.getItemCount();
        while (itemCount-- > 0) {
            max = Math.max(fontMetrics.stringWidth(choice.getItem(itemCount)), max);
        }
        return new Dimension(28 + max, Math.max(fontMetrics.getHeight() + 6, 15));
    }
    
    @Override
    public boolean isFocusable() {
        return true;
    }
    
    @Override
    public native void select(final int p0);
    
    @Override
    public void add(final String s, final int n) {
        this.addItem(s, n);
    }
    
    @Override
    public boolean shouldClearRectBeforePaint() {
        return false;
    }
    
    @Override
    public native void removeAll();
    
    @Override
    public native void remove(final int p0);
    
    public void addItem(final String s, final int n) {
        this.addItems(new String[] { s }, n);
    }
    
    public native void addItems(final String[] p0, final int p1);
    
    @Override
    public synchronized native void reshape(final int p0, final int p1, final int p2, final int p3);
    
    WChoicePeer(final Choice choice) {
        super(choice);
    }
    
    @Override
    native void create(final WComponentPeer p0);
    
    @Override
    void initialize() {
        final Choice choice = (Choice)this.target;
        final int itemCount = choice.getItemCount();
        if (itemCount > 0) {
            final String[] array = new String[itemCount];
            for (int i = 0; i < itemCount; ++i) {
                array[i] = choice.getItem(i);
            }
            this.addItems(array, 0);
            if (choice.getSelectedIndex() >= 0) {
                this.select(choice.getSelectedIndex());
            }
        }
        final Window containingWindow = SunToolkit.getContainingWindow((Component)this.target);
        if (containingWindow != null) {
            final WWindowPeer wWindowPeer = (WWindowPeer)containingWindow.getPeer();
            if (wWindowPeer != null) {
                wWindowPeer.addWindowListener(this.windowListener = new WindowAdapter() {
                    @Override
                    public void windowIconified(final WindowEvent windowEvent) {
                        WChoicePeer.this.closeList();
                    }
                    
                    @Override
                    public void windowClosing(final WindowEvent windowEvent) {
                        WChoicePeer.this.closeList();
                    }
                });
            }
        }
        super.initialize();
    }
    
    @Override
    protected void disposeImpl() {
        final Window containingWindow = SunToolkit.getContainingWindow((Component)this.target);
        if (containingWindow != null) {
            final WWindowPeer wWindowPeer = (WWindowPeer)containingWindow.getPeer();
            if (wWindowPeer != null) {
                wWindowPeer.removeWindowListener(this.windowListener);
            }
        }
        super.disposeImpl();
    }
    
    void handleAction(final int n) {
        final Choice choice = (Choice)this.target;
        SunToolkit.executeOnEventHandlerThread(choice, new Runnable() {
            @Override
            public void run() {
                choice.select(n);
                WChoicePeer.this.postEvent(new ItemEvent(choice, 701, choice.getItem(n), 1));
            }
        });
    }
    
    int getDropDownHeight() {
        final Choice choice = (Choice)this.target;
        return this.getFontMetrics(choice.getFont()).getHeight() * Math.min(choice.getItemCount(), 8);
    }
    
    native void closeList();
}
