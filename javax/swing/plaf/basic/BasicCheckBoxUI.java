package javax.swing.plaf.basic;

import sun.awt.AppContext;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;

public class BasicCheckBoxUI extends BasicRadioButtonUI
{
    private static final Object BASIC_CHECK_BOX_UI_KEY;
    private static final String propertyPrefix = "CheckBox.";
    
    public static ComponentUI createUI(final JComponent component) {
        final AppContext appContext = AppContext.getAppContext();
        BasicCheckBoxUI basicCheckBoxUI = (BasicCheckBoxUI)appContext.get(BasicCheckBoxUI.BASIC_CHECK_BOX_UI_KEY);
        if (basicCheckBoxUI == null) {
            basicCheckBoxUI = new BasicCheckBoxUI();
            appContext.put(BasicCheckBoxUI.BASIC_CHECK_BOX_UI_KEY, basicCheckBoxUI);
        }
        return basicCheckBoxUI;
    }
    
    public String getPropertyPrefix() {
        return "CheckBox.";
    }
    
    static {
        BASIC_CHECK_BOX_UI_KEY = new Object();
    }
}
