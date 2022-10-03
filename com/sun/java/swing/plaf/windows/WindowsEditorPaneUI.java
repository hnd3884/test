package com.sun.java.swing.plaf.windows;

import javax.swing.text.Caret;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicEditorPaneUI;

public class WindowsEditorPaneUI extends BasicEditorPaneUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new WindowsEditorPaneUI();
    }
    
    @Override
    protected Caret createCaret() {
        return new WindowsTextUI.WindowsCaret();
    }
}
