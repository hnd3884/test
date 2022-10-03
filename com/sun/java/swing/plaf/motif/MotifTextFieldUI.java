package com.sun.java.swing.plaf.motif;

import javax.swing.text.Caret;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTextFieldUI;

public class MotifTextFieldUI extends BasicTextFieldUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new MotifTextFieldUI();
    }
    
    @Override
    protected Caret createCaret() {
        return MotifTextUI.createCaret();
    }
}
