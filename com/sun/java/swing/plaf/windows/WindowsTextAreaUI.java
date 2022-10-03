package com.sun.java.swing.plaf.windows;

import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.text.Caret;
import javax.swing.plaf.basic.BasicTextAreaUI;

public class WindowsTextAreaUI extends BasicTextAreaUI
{
    @Override
    protected Caret createCaret() {
        return new WindowsTextUI.WindowsCaret();
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new WindowsTextAreaUI();
    }
}
