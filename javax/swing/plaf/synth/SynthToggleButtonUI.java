package javax.swing.plaf.synth;

import javax.swing.AbstractButton;
import java.awt.Graphics;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;

public class SynthToggleButtonUI extends SynthButtonUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new SynthToggleButtonUI();
    }
    
    @Override
    protected String getPropertyPrefix() {
        return "ToggleButton.";
    }
    
    @Override
    void paintBackground(final SynthContext synthContext, final Graphics graphics, final JComponent component) {
        if (((AbstractButton)component).isContentAreaFilled()) {
            synthContext.getPainter().paintToggleButtonBackground(synthContext, graphics, 0, 0, component.getWidth(), component.getHeight());
        }
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintToggleButtonBorder(synthContext, graphics, n, n2, n3, n4);
    }
}
