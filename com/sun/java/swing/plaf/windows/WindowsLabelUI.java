package com.sun.java.swing.plaf.windows;

import javax.swing.UIManager;
import java.awt.Color;
import sun.swing.SwingUtilities2;
import java.awt.Graphics;
import javax.swing.JLabel;
import sun.awt.AppContext;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicLabelUI;

public class WindowsLabelUI extends BasicLabelUI
{
    private static final Object WINDOWS_LABEL_UI_KEY;
    
    public static ComponentUI createUI(final JComponent component) {
        final AppContext appContext = AppContext.getAppContext();
        WindowsLabelUI windowsLabelUI = (WindowsLabelUI)appContext.get(WindowsLabelUI.WINDOWS_LABEL_UI_KEY);
        if (windowsLabelUI == null) {
            windowsLabelUI = new WindowsLabelUI();
            appContext.put(WindowsLabelUI.WINDOWS_LABEL_UI_KEY, windowsLabelUI);
        }
        return windowsLabelUI;
    }
    
    @Override
    protected void paintEnabledText(final JLabel label, final Graphics graphics, final String s, final int n, final int n2) {
        int displayedMnemonicIndex = label.getDisplayedMnemonicIndex();
        if (WindowsLookAndFeel.isMnemonicHidden()) {
            displayedMnemonicIndex = -1;
        }
        graphics.setColor(label.getForeground());
        SwingUtilities2.drawStringUnderlineCharAt(label, graphics, s, displayedMnemonicIndex, n, n2);
    }
    
    @Override
    protected void paintDisabledText(final JLabel label, final Graphics graphics, final String s, final int n, final int n2) {
        int displayedMnemonicIndex = label.getDisplayedMnemonicIndex();
        if (WindowsLookAndFeel.isMnemonicHidden()) {
            displayedMnemonicIndex = -1;
        }
        if (UIManager.getColor("Label.disabledForeground") instanceof Color && UIManager.getColor("Label.disabledShadow") instanceof Color) {
            graphics.setColor(UIManager.getColor("Label.disabledShadow"));
            SwingUtilities2.drawStringUnderlineCharAt(label, graphics, s, displayedMnemonicIndex, n + 1, n2 + 1);
            graphics.setColor(UIManager.getColor("Label.disabledForeground"));
            SwingUtilities2.drawStringUnderlineCharAt(label, graphics, s, displayedMnemonicIndex, n, n2);
        }
        else {
            final Color background = label.getBackground();
            graphics.setColor(background.brighter());
            SwingUtilities2.drawStringUnderlineCharAt(label, graphics, s, displayedMnemonicIndex, n + 1, n2 + 1);
            graphics.setColor(background.darker());
            SwingUtilities2.drawStringUnderlineCharAt(label, graphics, s, displayedMnemonicIndex, n, n2);
        }
    }
    
    static {
        WINDOWS_LABEL_UI_KEY = new Object();
    }
}
