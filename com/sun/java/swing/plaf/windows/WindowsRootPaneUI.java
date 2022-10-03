package com.sun.java.swing.plaf.windows;

import java.awt.Component;
import javax.swing.SwingUtilities;
import javax.swing.JMenu;
import sun.awt.AWTAccessor;
import sun.awt.SunToolkit;
import java.awt.Toolkit;
import javax.swing.JMenuBar;
import javax.swing.MenuElement;
import javax.swing.JFrame;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.MenuSelectionManager;
import java.awt.event.KeyEvent;
import java.awt.Window;
import javax.swing.JRootPane;
import java.awt.KeyEventPostProcessor;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicRootPaneUI;

public class WindowsRootPaneUI extends BasicRootPaneUI
{
    private static final WindowsRootPaneUI windowsRootPaneUI;
    static final AltProcessor altProcessor;
    
    public static ComponentUI createUI(final JComponent component) {
        return WindowsRootPaneUI.windowsRootPaneUI;
    }
    
    static {
        windowsRootPaneUI = new WindowsRootPaneUI();
        altProcessor = new AltProcessor();
    }
    
    static class AltProcessor implements KeyEventPostProcessor
    {
        static boolean altKeyPressed;
        static boolean menuCanceledOnPress;
        static JRootPane root;
        static Window winAncestor;
        
        void altPressed(final KeyEvent keyEvent) {
            final MenuSelectionManager defaultManager = MenuSelectionManager.defaultManager();
            final MenuElement[] selectedPath = defaultManager.getSelectedPath();
            if (selectedPath.length > 0 && !(selectedPath[0] instanceof ComboPopup)) {
                defaultManager.clearSelectedPath();
                AltProcessor.menuCanceledOnPress = true;
                keyEvent.consume();
            }
            else if (selectedPath.length > 0) {
                WindowsLookAndFeel.setMnemonicHidden(AltProcessor.menuCanceledOnPress = false);
                WindowsGraphicsUtils.repaintMnemonicsInWindow(AltProcessor.winAncestor);
                keyEvent.consume();
            }
            else {
                WindowsLookAndFeel.setMnemonicHidden(AltProcessor.menuCanceledOnPress = false);
                WindowsGraphicsUtils.repaintMnemonicsInWindow(AltProcessor.winAncestor);
                JMenuBar jMenuBar = (AltProcessor.root != null) ? AltProcessor.root.getJMenuBar() : null;
                if (jMenuBar == null && AltProcessor.winAncestor instanceof JFrame) {
                    jMenuBar = ((JFrame)AltProcessor.winAncestor).getJMenuBar();
                }
                if (((jMenuBar != null) ? jMenuBar.getMenu(0) : null) != null) {
                    keyEvent.consume();
                }
            }
        }
        
        void altReleased(final KeyEvent keyEvent) {
            if (AltProcessor.menuCanceledOnPress) {
                WindowsLookAndFeel.setMnemonicHidden(true);
                WindowsGraphicsUtils.repaintMnemonicsInWindow(AltProcessor.winAncestor);
                return;
            }
            final MenuSelectionManager defaultManager = MenuSelectionManager.defaultManager();
            if (defaultManager.getSelectedPath().length == 0) {
                JMenuBar jMenuBar = (AltProcessor.root != null) ? AltProcessor.root.getJMenuBar() : null;
                if (jMenuBar == null && AltProcessor.winAncestor instanceof JFrame) {
                    jMenuBar = ((JFrame)AltProcessor.winAncestor).getJMenuBar();
                }
                final JMenu menu = (jMenuBar != null) ? jMenuBar.getMenu(0) : null;
                boolean b = false;
                final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
                if (defaultToolkit instanceof SunToolkit) {
                    b = (SunToolkit.getContainingWindow(AWTAccessor.getKeyEventAccessor().getOriginalSource(keyEvent)) != AltProcessor.winAncestor || keyEvent.getWhen() <= ((SunToolkit)defaultToolkit).getWindowDeactivationTime(AltProcessor.winAncestor));
                }
                if (menu != null && !b) {
                    defaultManager.setSelectedPath(new MenuElement[] { jMenuBar, menu });
                }
                else if (!WindowsLookAndFeel.isMnemonicHidden()) {
                    WindowsLookAndFeel.setMnemonicHidden(true);
                    WindowsGraphicsUtils.repaintMnemonicsInWindow(AltProcessor.winAncestor);
                }
            }
            else if (defaultManager.getSelectedPath()[0] instanceof ComboPopup) {
                WindowsLookAndFeel.setMnemonicHidden(true);
                WindowsGraphicsUtils.repaintMnemonicsInWindow(AltProcessor.winAncestor);
            }
        }
        
        @Override
        public boolean postProcessKeyEvent(final KeyEvent keyEvent) {
            if (keyEvent.isConsumed() && keyEvent.getKeyCode() != 18) {
                return AltProcessor.altKeyPressed = false;
            }
            if (keyEvent.getKeyCode() == 18) {
                AltProcessor.root = SwingUtilities.getRootPane(keyEvent.getComponent());
                AltProcessor.winAncestor = ((AltProcessor.root == null) ? null : SwingUtilities.getWindowAncestor(AltProcessor.root));
                if (keyEvent.getID() == 401) {
                    if (!AltProcessor.altKeyPressed) {
                        this.altPressed(keyEvent);
                    }
                    return AltProcessor.altKeyPressed = true;
                }
                if (keyEvent.getID() == 402) {
                    if (AltProcessor.altKeyPressed) {
                        this.altReleased(keyEvent);
                    }
                    else if (MenuSelectionManager.defaultManager().getSelectedPath().length <= 0) {
                        WindowsLookAndFeel.setMnemonicHidden(true);
                        WindowsGraphicsUtils.repaintMnemonicsInWindow(AltProcessor.winAncestor);
                    }
                    AltProcessor.altKeyPressed = false;
                }
                AltProcessor.root = null;
                AltProcessor.winAncestor = null;
            }
            else {
                AltProcessor.altKeyPressed = false;
            }
            return false;
        }
        
        static {
            AltProcessor.altKeyPressed = false;
            AltProcessor.menuCanceledOnPress = false;
            AltProcessor.root = null;
            AltProcessor.winAncestor = null;
        }
    }
}
