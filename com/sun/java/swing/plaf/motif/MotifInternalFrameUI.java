package com.sun.java.swing.plaf.motif;

import javax.swing.plaf.ActionMapUIResource;
import javax.swing.UIManager;
import javax.swing.Action;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.ActionMap;
import javax.swing.SwingUtilities;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.LayoutManager;
import javax.swing.LookAndFeel;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.JInternalFrame;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import java.awt.Color;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class MotifInternalFrameUI extends BasicInternalFrameUI
{
    Color color;
    Color highlight;
    Color shadow;
    MotifInternalFrameTitlePane titlePane;
    @Deprecated
    protected KeyStroke closeMenuKey;
    
    public static ComponentUI createUI(final JComponent component) {
        return new MotifInternalFrameUI((JInternalFrame)component);
    }
    
    public MotifInternalFrameUI(final JInternalFrame internalFrame) {
        super(internalFrame);
    }
    
    @Override
    public void installUI(final JComponent component) {
        super.installUI(component);
        this.setColors((JInternalFrame)component);
    }
    
    @Override
    protected void installDefaults() {
        final Border border = this.frame.getBorder();
        this.frame.setLayout(this.internalFrameLayout = this.createLayoutManager());
        if (border == null || border instanceof UIResource) {
            this.frame.setBorder(new MotifBorders.InternalFrameBorder(this.frame));
        }
    }
    
    @Override
    protected void installKeyboardActions() {
        super.installKeyboardActions();
        this.closeMenuKey = KeyStroke.getKeyStroke(27, 0);
    }
    
    @Override
    protected void uninstallDefaults() {
        LookAndFeel.uninstallBorder(this.frame);
        this.frame.setLayout(null);
        this.internalFrameLayout = null;
    }
    
    private JInternalFrame getFrame() {
        return this.frame;
    }
    
    public JComponent createNorthPane(final JInternalFrame internalFrame) {
        return this.titlePane = new MotifInternalFrameTitlePane(internalFrame);
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }
    
    @Override
    protected void uninstallKeyboardActions() {
        super.uninstallKeyboardActions();
        if (this.isKeyBindingRegistered()) {
            final JInternalFrame.JDesktopIcon desktopIcon = this.frame.getDesktopIcon();
            SwingUtilities.replaceUIActionMap(desktopIcon, null);
            SwingUtilities.replaceUIInputMap(desktopIcon, 2, null);
        }
    }
    
    @Override
    protected void setupMenuOpenKey() {
        super.setupMenuOpenKey();
        final ActionMap uiActionMap = SwingUtilities.getUIActionMap(this.frame);
        if (uiActionMap != null) {
            uiActionMap.put("showSystemMenu", new AbstractAction() {
                @Override
                public void actionPerformed(final ActionEvent actionEvent) {
                    MotifInternalFrameUI.this.titlePane.showSystemMenu();
                }
                
                @Override
                public boolean isEnabled() {
                    return MotifInternalFrameUI.this.isKeyBindingActive();
                }
            });
        }
    }
    
    @Override
    protected void setupMenuCloseKey() {
        final ActionMap uiActionMap = SwingUtilities.getUIActionMap(this.frame);
        if (uiActionMap != null) {
            uiActionMap.put("hideSystemMenu", new AbstractAction() {
                @Override
                public void actionPerformed(final ActionEvent actionEvent) {
                    MotifInternalFrameUI.this.titlePane.hideSystemMenu();
                }
                
                @Override
                public boolean isEnabled() {
                    return MotifInternalFrameUI.this.isKeyBindingActive();
                }
            });
        }
        final JInternalFrame.JDesktopIcon desktopIcon = this.frame.getDesktopIcon();
        if (SwingUtilities.getUIInputMap(desktopIcon, 2) == null) {
            final Object[] array = (Object[])UIManager.get("DesktopIcon.windowBindings");
            if (array != null) {
                SwingUtilities.replaceUIInputMap(desktopIcon, 2, LookAndFeel.makeComponentInputMap(desktopIcon, array));
            }
        }
        if (SwingUtilities.getUIActionMap(desktopIcon) == null) {
            final ActionMapUIResource actionMapUIResource = new ActionMapUIResource();
            actionMapUIResource.put("hideSystemMenu", new AbstractAction() {
                @Override
                public void actionPerformed(final ActionEvent actionEvent) {
                    ((MotifDesktopIconUI)MotifInternalFrameUI.this.getFrame().getDesktopIcon().getUI()).hideSystemMenu();
                }
                
                @Override
                public boolean isEnabled() {
                    return MotifInternalFrameUI.this.isKeyBindingActive();
                }
            });
            SwingUtilities.replaceUIActionMap(desktopIcon, actionMapUIResource);
        }
    }
    
    @Override
    protected void activateFrame(final JInternalFrame colors) {
        super.activateFrame(colors);
        this.setColors(colors);
    }
    
    @Override
    protected void deactivateFrame(final JInternalFrame colors) {
        this.setColors(colors);
        super.deactivateFrame(colors);
    }
    
    void setColors(final JInternalFrame internalFrame) {
        if (internalFrame.isSelected()) {
            this.color = UIManager.getColor("InternalFrame.activeTitleBackground");
        }
        else {
            this.color = UIManager.getColor("InternalFrame.inactiveTitleBackground");
        }
        this.highlight = this.color.brighter();
        this.shadow = this.color.darker().darker();
        this.titlePane.setColors(this.color, this.highlight, this.shadow);
    }
}
