package org.jfree.ui;

import java.awt.LayoutManager;
import javax.swing.JPanel;

public abstract class WizardPanel extends JPanel
{
    private WizardDialog owner;
    
    protected WizardPanel(final LayoutManager layout) {
        super(layout);
    }
    
    public abstract boolean canFinish();
    
    public abstract boolean canRedisplayNextPanel();
    
    public abstract WizardPanel getNextPanel();
    
    public WizardDialog getOwner() {
        return this.owner;
    }
    
    public Object getResult() {
        return null;
    }
    
    public abstract boolean hasNextPanel();
    
    public abstract void returnFromLaterStep();
    
    public void setOwner(final WizardDialog owner) {
        this.owner = owner;
    }
}
