package com.sun.java.swing.plaf.windows;

import javax.swing.MenuElement;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.JRootPane;
import sun.swing.StringUIClientPropertyKey;
import java.awt.Insets;
import java.awt.Graphics;
import sun.swing.SwingUtilities2;
import java.awt.Component;
import javax.swing.PopupFactory;
import javax.swing.Popup;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeListener;
import javax.swing.MenuSelectionManager;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicPopupMenuUI;

public class WindowsPopupMenuUI extends BasicPopupMenuUI
{
    static MnemonicListener mnemonicListener;
    static final Object GUTTER_OFFSET_KEY;
    
    public static ComponentUI createUI(final JComponent component) {
        return new WindowsPopupMenuUI();
    }
    
    public void installListeners() {
        super.installListeners();
        if (!UIManager.getBoolean("Button.showMnemonics") && WindowsPopupMenuUI.mnemonicListener == null) {
            WindowsPopupMenuUI.mnemonicListener = new MnemonicListener();
            MenuSelectionManager.defaultManager().addChangeListener(WindowsPopupMenuUI.mnemonicListener);
        }
    }
    
    @Override
    public Popup getPopup(final JPopupMenu popupMenu, final int n, final int n2) {
        return PopupFactory.getSharedInstance().getPopup(popupMenu.getInvoker(), popupMenu, n, n2);
    }
    
    static int getTextOffset(final JComponent component) {
        int n = -1;
        final Object clientProperty = component.getClientProperty(SwingUtilities2.BASICMENUITEMUI_MAX_TEXT_OFFSET);
        if (clientProperty instanceof Integer) {
            final int intValue = (int)clientProperty;
            int x = 0;
            final Component component2 = component.getComponent(0);
            if (component2 != null) {
                x = component2.getX();
            }
            n = intValue + x;
        }
        return n;
    }
    
    static int getSpanBeforeGutter() {
        return 3;
    }
    
    static int getSpanAfterGutter() {
        return 3;
    }
    
    static int getGutterWidth() {
        int width = 2;
        final XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            width = xp.getSkin(null, TMSchema.Part.MP_POPUPGUTTER).getWidth();
        }
        return width;
    }
    
    private static boolean isLeftToRight(final JComponent component) {
        boolean leftToRight = true;
        for (int n = component.getComponentCount() - 1; n >= 0 && leftToRight; leftToRight = component.getComponent(n).getComponentOrientation().isLeftToRight(), --n) {}
        return leftToRight;
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final XPStyle xp = XPStyle.getXP();
        if (WindowsMenuItemUI.isVistaPainting(xp)) {
            xp.getSkin(component, TMSchema.Part.MP_POPUPBACKGROUND).paintSkin(graphics, 0, 0, component.getWidth(), component.getHeight(), TMSchema.State.NORMAL);
            final int textOffset = getTextOffset(component);
            if (textOffset >= 0 && isLeftToRight(component)) {
                final XPStyle.Skin skin = xp.getSkin(component, TMSchema.Part.MP_POPUPGUTTER);
                final int gutterWidth = getGutterWidth();
                final int n = textOffset - getSpanAfterGutter() - gutterWidth;
                component.putClientProperty(WindowsPopupMenuUI.GUTTER_OFFSET_KEY, n);
                final Insets insets = component.getInsets();
                skin.paintSkin(graphics, n, insets.top, gutterWidth, component.getHeight() - insets.bottom - insets.top, TMSchema.State.NORMAL);
            }
            else if (component.getClientProperty(WindowsPopupMenuUI.GUTTER_OFFSET_KEY) != null) {
                component.putClientProperty(WindowsPopupMenuUI.GUTTER_OFFSET_KEY, null);
            }
        }
        else {
            super.paint(graphics, component);
        }
    }
    
    static {
        WindowsPopupMenuUI.mnemonicListener = null;
        GUTTER_OFFSET_KEY = new StringUIClientPropertyKey("GUTTER_OFFSET_KEY");
    }
    
    static class MnemonicListener implements ChangeListener
    {
        JRootPane repaintRoot;
        
        MnemonicListener() {
            this.repaintRoot = null;
        }
        
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            final MenuElement[] selectedPath = ((MenuSelectionManager)changeEvent.getSource()).getSelectedPath();
            if (selectedPath.length == 0) {
                if (!WindowsLookAndFeel.isMnemonicHidden()) {
                    WindowsLookAndFeel.setMnemonicHidden(true);
                    if (this.repaintRoot != null) {
                        WindowsGraphicsUtils.repaintMnemonicsInWindow(SwingUtilities.getWindowAncestor(this.repaintRoot));
                    }
                }
            }
            else {
                Component invoker = (Component)selectedPath[0];
                if (invoker instanceof JPopupMenu) {
                    invoker = ((JPopupMenu)invoker).getInvoker();
                }
                this.repaintRoot = SwingUtilities.getRootPane(invoker);
            }
        }
    }
}
