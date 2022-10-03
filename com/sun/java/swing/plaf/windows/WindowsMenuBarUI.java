package com.sun.java.swing.plaf.windows;

import javax.swing.JMenu;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.ActionMap;
import javax.swing.Action;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.SwingUtilities;
import java.awt.event.HierarchyEvent;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Window;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowListener;
import javax.swing.plaf.basic.BasicMenuBarUI;

public class WindowsMenuBarUI extends BasicMenuBarUI
{
    private WindowListener windowListener;
    private HierarchyListener hierarchyListener;
    private Window window;
    
    public WindowsMenuBarUI() {
        this.windowListener = null;
        this.hierarchyListener = null;
        this.window = null;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new WindowsMenuBarUI();
    }
    
    @Override
    protected void uninstallListeners() {
        this.uninstallWindowListener();
        if (this.hierarchyListener != null) {
            this.menuBar.removeHierarchyListener(this.hierarchyListener);
            this.hierarchyListener = null;
        }
        super.uninstallListeners();
    }
    
    private void installWindowListener() {
        if (this.windowListener == null) {
            final Container topLevelAncestor = this.menuBar.getTopLevelAncestor();
            if (topLevelAncestor instanceof Window) {
                this.window = (Window)topLevelAncestor;
                this.windowListener = new WindowAdapter() {
                    @Override
                    public void windowActivated(final WindowEvent windowEvent) {
                        WindowsMenuBarUI.this.menuBar.repaint();
                    }
                    
                    @Override
                    public void windowDeactivated(final WindowEvent windowEvent) {
                        WindowsMenuBarUI.this.menuBar.repaint();
                    }
                };
                ((Window)topLevelAncestor).addWindowListener(this.windowListener);
            }
        }
    }
    
    private void uninstallWindowListener() {
        if (this.windowListener != null && this.window != null) {
            this.window.removeWindowListener(this.windowListener);
        }
        this.window = null;
        this.windowListener = null;
    }
    
    @Override
    protected void installListeners() {
        if (WindowsLookAndFeel.isOnVista()) {
            this.installWindowListener();
            this.hierarchyListener = new HierarchyListener() {
                @Override
                public void hierarchyChanged(final HierarchyEvent hierarchyEvent) {
                    if ((hierarchyEvent.getChangeFlags() & 0x2L) != 0x0L) {
                        if (WindowsMenuBarUI.this.menuBar.isDisplayable()) {
                            WindowsMenuBarUI.this.installWindowListener();
                        }
                        else {
                            WindowsMenuBarUI.this.uninstallWindowListener();
                        }
                    }
                }
            };
            this.menuBar.addHierarchyListener(this.hierarchyListener);
        }
        super.installListeners();
    }
    
    @Override
    protected void installKeyboardActions() {
        super.installKeyboardActions();
        ActionMap uiActionMap = SwingUtilities.getUIActionMap(this.menuBar);
        if (uiActionMap == null) {
            uiActionMap = new ActionMapUIResource();
            SwingUtilities.replaceUIActionMap(this.menuBar, uiActionMap);
        }
        uiActionMap.put("takeFocus", new TakeFocus());
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final XPStyle xp = XPStyle.getXP();
        if (WindowsMenuItemUI.isVistaPainting(xp)) {
            xp.getSkin(component, TMSchema.Part.MP_BARBACKGROUND).paintSkin(graphics, 0, 0, component.getWidth(), component.getHeight(), isActive(component) ? TMSchema.State.ACTIVE : TMSchema.State.INACTIVE);
        }
        else {
            super.paint(graphics, component);
        }
    }
    
    static boolean isActive(final JComponent component) {
        final JRootPane rootPane = component.getRootPane();
        if (rootPane != null) {
            final Container parent = rootPane.getParent();
            if (parent instanceof Window) {
                return ((Window)parent).isActive();
            }
        }
        return true;
    }
    
    private static class TakeFocus extends AbstractAction
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JMenuBar menuBar = (JMenuBar)actionEvent.getSource();
            final JMenu menu = menuBar.getMenu(0);
            if (menu != null) {
                MenuSelectionManager.defaultManager().setSelectedPath(new MenuElement[] { menuBar, menu });
                WindowsLookAndFeel.setMnemonicHidden(false);
                WindowsLookAndFeel.repaintRootPane(menuBar);
            }
        }
    }
}
