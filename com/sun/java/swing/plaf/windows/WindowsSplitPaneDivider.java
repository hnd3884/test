package com.sun.java.swing.plaf.windows;

import java.awt.Dimension;
import java.awt.Color;
import javax.swing.UIManager;
import java.awt.Graphics;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;

public class WindowsSplitPaneDivider extends BasicSplitPaneDivider
{
    public WindowsSplitPaneDivider(final BasicSplitPaneUI basicSplitPaneUI) {
        super(basicSplitPaneUI);
    }
    
    @Override
    public void paint(final Graphics graphics) {
        final Color color = this.splitPane.hasFocus() ? UIManager.getColor("SplitPane.shadow") : this.getBackground();
        final Dimension size = this.getSize();
        if (color != null) {
            graphics.setColor(color);
            graphics.fillRect(0, 0, size.width, size.height);
        }
        super.paint(graphics);
    }
}
