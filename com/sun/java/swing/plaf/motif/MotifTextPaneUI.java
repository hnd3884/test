package com.sun.java.swing.plaf.motif;

import javax.swing.text.Caret;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTextPaneUI;

public class MotifTextPaneUI extends BasicTextPaneUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new MotifTextPaneUI();
    }
    
    @Override
    protected Caret createCaret() {
        return MotifTextUI.createCaret();
    }
}
