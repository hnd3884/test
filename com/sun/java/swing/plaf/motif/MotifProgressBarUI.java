package com.sun.java.swing.plaf.motif;

import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class MotifProgressBarUI extends BasicProgressBarUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new MotifProgressBarUI();
    }
}
