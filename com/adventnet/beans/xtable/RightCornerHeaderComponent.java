package com.adventnet.beans.xtable;

import com.adventnet.beans.xtable.events.RightCornerHeaderComponentActionEvent;
import com.adventnet.beans.xtable.events.RightCornerHeaderComponentActionListener;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import com.adventnet.beans.xtable.events.DefaultRightCornerHeaderComponentHandler;
import java.util.Vector;
import javax.swing.JButton;

public class RightCornerHeaderComponent extends JButton
{
    public Vector listeners;
    private DefaultRightCornerHeaderComponentHandler h;
    private Icon icon;
    
    public RightCornerHeaderComponent() {
        this.listeners = new Vector(1);
        this.setIcon(new ImageIcon(this.getClass().getResource("RCHCicon.png")));
        this.addRightCornerHeaderComponentActionListener(this.h = new DefaultRightCornerHeaderComponentHandler());
    }
    
    public void addRightCornerHeaderComponentActionListener(final RightCornerHeaderComponentActionListener rightCornerHeaderComponentActionListener) {
        this.listeners.add(rightCornerHeaderComponentActionListener);
    }
    
    public void removeRightCornerHeaderComponentActionListeher(final RightCornerHeaderComponentActionListener rightCornerHeaderComponentActionListener) {
        this.listeners.remove(rightCornerHeaderComponentActionListener);
    }
    
    public void enableDefaultHandler(final boolean b) {
        if (b) {
            this.listeners.add(this.h);
        }
        else {
            this.listeners.remove(this.h);
        }
    }
    
    public void fireRightCornerHeaderComponentActionEvent(final Object o, final int n, final int n2) {
        final RightCornerHeaderComponentActionEvent rightCornerHeaderComponentActionEvent = new RightCornerHeaderComponentActionEvent(o, n, n2);
        for (int i = 0; i < this.listeners.size(); ++i) {
            ((RightCornerHeaderComponentActionListener)this.listeners.get(i)).rightCornerHeaderComponentInvoked(rightCornerHeaderComponentActionEvent);
        }
    }
}
