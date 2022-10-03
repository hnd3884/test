package com.sun.java.swing.plaf.motif;

import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicSeparatorUI;

public class MotifSeparatorUI extends BasicSeparatorUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new MotifSeparatorUI();
    }
}
