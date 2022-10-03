package com.sun.java.swing.plaf.motif;

import javax.swing.text.Caret;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicPasswordFieldUI;

public class MotifPasswordFieldUI extends BasicPasswordFieldUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new MotifPasswordFieldUI();
    }
    
    @Override
    protected Caret createCaret() {
        return MotifTextUI.createCaret();
    }
}
