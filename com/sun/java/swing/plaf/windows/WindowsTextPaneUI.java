package com.sun.java.swing.plaf.windows;

import javax.swing.text.Caret;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTextPaneUI;

public class WindowsTextPaneUI extends BasicTextPaneUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new WindowsTextPaneUI();
    }
    
    @Override
    protected Caret createCaret() {
        return new WindowsTextUI.WindowsCaret();
    }
}
