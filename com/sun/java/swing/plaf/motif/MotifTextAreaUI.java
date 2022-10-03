package com.sun.java.swing.plaf.motif;

import javax.swing.text.Caret;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTextAreaUI;

public class MotifTextAreaUI extends BasicTextAreaUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new MotifTextAreaUI();
    }
    
    @Override
    protected Caret createCaret() {
        return MotifTextUI.createCaret();
    }
}
