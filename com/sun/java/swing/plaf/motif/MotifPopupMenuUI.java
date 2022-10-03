package com.sun.java.swing.plaf.motif;

import java.awt.event.MouseEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Insets;
import java.awt.FontMetrics;
import java.awt.Component;
import sun.swing.SwingUtilities2;
import javax.swing.UIManager;
import javax.swing.JPopupMenu;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Font;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicPopupMenuUI;

public class MotifPopupMenuUI extends BasicPopupMenuUI
{
    private static Border border;
    private Font titleFont;
    
    public MotifPopupMenuUI() {
        this.titleFont = null;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new MotifPopupMenuUI();
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        final Dimension preferredLayoutSize = component.getLayout().preferredLayoutSize(component);
        final String label = ((JPopupMenu)component).getLabel();
        if (this.titleFont == null) {
            this.titleFont = UIManager.getLookAndFeelDefaults().getFont("PopupMenu.font");
        }
        final FontMetrics fontMetrics = component.getFontMetrics(this.titleFont);
        int n = 0;
        if (label != null) {
            n += SwingUtilities2.stringWidth(component, fontMetrics, label);
        }
        if (preferredLayoutSize.width < n) {
            preferredLayoutSize.width = n + 8;
            final Insets insets = component.getInsets();
            if (insets != null) {
                final Dimension dimension = preferredLayoutSize;
                dimension.width += insets.left + insets.right;
            }
            if (MotifPopupMenuUI.border != null) {
                final Insets borderInsets = MotifPopupMenuUI.border.getBorderInsets(component);
                final Dimension dimension2 = preferredLayoutSize;
                dimension2.width += borderInsets.left + borderInsets.right;
            }
            return preferredLayoutSize;
        }
        return null;
    }
    
    protected ChangeListener createChangeListener(final JPopupMenu popupMenu) {
        return new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent changeEvent) {
            }
        };
    }
    
    @Override
    public boolean isPopupTrigger(final MouseEvent mouseEvent) {
        return mouseEvent.getID() == 501 && (mouseEvent.getModifiers() & 0x4) != 0x0;
    }
    
    static {
        MotifPopupMenuUI.border = null;
    }
}
