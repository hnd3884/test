package javax.swing.plaf.metal;

import sun.swing.SwingUtilities2;
import javax.swing.UIManager;
import java.awt.Graphics;
import javax.swing.JLabel;
import sun.awt.AppContext;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicLabelUI;

public class MetalLabelUI extends BasicLabelUI
{
    protected static MetalLabelUI metalLabelUI;
    private static final Object METAL_LABEL_UI_KEY;
    
    public static ComponentUI createUI(final JComponent component) {
        if (System.getSecurityManager() != null) {
            final AppContext appContext = AppContext.getAppContext();
            MetalLabelUI metalLabelUI = (MetalLabelUI)appContext.get(MetalLabelUI.METAL_LABEL_UI_KEY);
            if (metalLabelUI == null) {
                metalLabelUI = new MetalLabelUI();
                appContext.put(MetalLabelUI.METAL_LABEL_UI_KEY, metalLabelUI);
            }
            return metalLabelUI;
        }
        return MetalLabelUI.metalLabelUI;
    }
    
    @Override
    protected void paintDisabledText(final JLabel label, final Graphics graphics, final String s, final int n, final int n2) {
        final int displayedMnemonicIndex = label.getDisplayedMnemonicIndex();
        graphics.setColor(UIManager.getColor("Label.disabledForeground"));
        SwingUtilities2.drawStringUnderlineCharAt(label, graphics, s, displayedMnemonicIndex, n, n2);
    }
    
    static {
        MetalLabelUI.metalLabelUI = new MetalLabelUI();
        METAL_LABEL_UI_KEY = new Object();
    }
}
