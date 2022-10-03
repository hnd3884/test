package javax.swing.plaf.metal;

import javax.swing.UIManager;
import javax.swing.AbstractButton;
import sun.awt.AppContext;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;

public class MetalCheckBoxUI extends MetalRadioButtonUI
{
    private static final Object METAL_CHECK_BOX_UI_KEY;
    private static final String propertyPrefix = "CheckBox.";
    private boolean defaults_initialized;
    
    public MetalCheckBoxUI() {
        this.defaults_initialized = false;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        final AppContext appContext = AppContext.getAppContext();
        MetalCheckBoxUI metalCheckBoxUI = (MetalCheckBoxUI)appContext.get(MetalCheckBoxUI.METAL_CHECK_BOX_UI_KEY);
        if (metalCheckBoxUI == null) {
            metalCheckBoxUI = new MetalCheckBoxUI();
            appContext.put(MetalCheckBoxUI.METAL_CHECK_BOX_UI_KEY, metalCheckBoxUI);
        }
        return metalCheckBoxUI;
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
        METAL_CHECK_BOX_UI_KEY = new Object();
    }
}
