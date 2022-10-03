package com.adventnet.beans.xtable;

import java.awt.event.ActionEvent;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

public class ToggleMenuItem extends JMenuItem
{
    private boolean state;
    
    public ToggleMenuItem(final String s) {
        this(s, false);
    }
    
    public ToggleMenuItem(final String s, final boolean state) {
        super(s);
        this.setState(state);
    }
    
    public void setState(final boolean state) {
        this.state = state;
        if (state) {
            this.setIcon(new ImageIcon(this.getClass().getResource("/com/adventnet/beans/xtable/tickmark.png")));
            this.setText(super.getText().trim());
        }
        else {
            this.setIcon(null);
            this.setText("    " + super.getText().trim());
        }
    }
    
    public String getLabel() {
        return this.getText().trim();
    }
    
    public boolean getState() {
        return this.state;
    }
    
    public void actionPerformed(final ActionEvent actionEvent) {
        this.setState(!this.getState());
    }
    
    protected void fireActionPerformed(final ActionEvent actionEvent) {
        this.actionPerformed(actionEvent);
        super.fireActionPerformed(actionEvent);
    }
}
