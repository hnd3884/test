package com.sun.java.swing.plaf.windows;

import javax.swing.UIManager;
import javax.swing.AbstractButton;
import sun.awt.AppContext;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;

public class WindowsCheckBoxUI extends WindowsRadioButtonUI
{
    private static final Object WINDOWS_CHECK_BOX_UI_KEY;
    private static final String propertyPrefix = "CheckBox.";
    private boolean defaults_initialized;
    
    public WindowsCheckBoxUI() {
        this.defaults_initialized = false;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        final AppContext appContext = AppContext.getAppContext();
        WindowsCheckBoxUI windowsCheckBoxUI = (WindowsCheckBoxUI)appContext.get(WindowsCheckBoxUI.WINDOWS_CHECK_BOX_UI_KEY);
        if (windowsCheckBoxUI == null) {
            windowsCheckBoxUI = new WindowsCheckBoxUI();
            appContext.put(WindowsCheckBoxUI.WINDOWS_CHECK_BOX_UI_KEY, windowsCheckBoxUI);
        }
        return windowsCheckBoxUI;
    }
    
    public String getPropertyPrefix() {
        return "CheckBox.";
    }
    
    @Override
    public void installDefaults(final AbstractButton abstractButton) {
        super.installDefaults(abstractButton);
        if (!this.defaults_initialized) {
            this.icon = UIManager.getIcon(this.getPropertyPrefix() + "icon");
            this.defaults_initialized = true;
        }
    }
    
    public void uninstallDefaults(final AbstractButton abstractButton) {
        super.uninstallDefaults(abstractButton);
        this.defaults_initialized = false;
    }
    
    static {
        WINDOWS_CHECK_BOX_UI_KEY = new Object();
    }
}
