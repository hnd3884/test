package org.jfree.ui.action;

import javax.swing.Action;
import java.util.ArrayList;

public class ActionConcentrator
{
    private final ArrayList actions;
    
    public ActionConcentrator() {
        this.actions = new ArrayList();
    }
    
    public void addAction(final Action a) {
        if (a == null) {
            throw new NullPointerException();
        }
        this.actions.add(a);
    }
    
    public boolean isEnabled() {
        for (int i = 0; i < this.actions.size(); ++i) {
            final Action a = this.actions.get(i);
            if (a.isEnabled()) {
                return true;
            }
        }
        return false;
    }
    
    public void removeAction(final Action a) {
        if (a == null) {
            throw new NullPointerException();
        }
        this.actions.remove(a);
    }
    
    public void setEnabled(final boolean b) {
        for (int i = 0; i < this.actions.size(); ++i) {
            final Action a = this.actions.get(i);
            a.setEnabled(b);
        }
    }
}
