package com.sun.java.swing.plaf.motif;

import javax.swing.text.Caret;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicEditorPaneUI;

public class MotifEditorPaneUI extends BasicEditorPaneUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new MotifEditorPaneUI();
    }
    
    @Override
    protected Caret createCaret() {
        return MotifTextUI.createCaret();
    }
}
