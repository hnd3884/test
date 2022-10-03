package com.sun.java.swing.plaf.motif;

import javax.swing.AbstractButton;
import javax.swing.plaf.basic.BasicButtonListener;

public class MotifButtonListener extends BasicButtonListener
{
    public MotifButtonListener(final AbstractButton abstractButton) {
        super(abstractButton);
    }
    
    @Override
    protected void checkOpacity(final AbstractButton abstractButton) {
        abstractButton.setOpaque(false);
    }
}
