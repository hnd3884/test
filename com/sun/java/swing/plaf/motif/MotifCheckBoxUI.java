package com.sun.java.swing.plaf.motif;

import javax.swing.UIManager;
import javax.swing.AbstractButton;
import sun.awt.AppContext;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;

public class MotifCheckBoxUI extends MotifRadioButtonUI
{
    private static final Object MOTIF_CHECK_BOX_UI_KEY;
    private static final String propertyPrefix = "CheckBox.";
    private boolean defaults_initialized;
    
    public MotifCheckBoxUI() {
        this.defaults_initialized = false;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        final AppContext appContext = AppContext.getAppContext();
        MotifCheckBoxUI motifCheckBoxUI = (MotifCheckBoxUI)appContext.get(MotifCheckBoxUI.MOTIF_CHECK_BOX_UI_KEY);
        if (motifCheckBoxUI == null) {
            motifCheckBoxUI = new MotifCheckBoxUI();
            appContext.put(MotifCheckBoxUI.MOTIF_CHECK_BOX_UI_KEY, motifCheckBoxUI);
        }
        return motifCheckBoxUI;
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
    
    @Override
    protected void uninstallDefaults(final AbstractButton abstractButton) {
        super.uninstallDefaults(abstractButton);
        this.defaults_initialized = false;
    }
    
    static {
        MOTIF_CHECK_BOX_UI_KEY = new Object();
    }
}
