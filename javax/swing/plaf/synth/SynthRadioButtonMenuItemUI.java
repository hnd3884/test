package javax.swing.plaf.synth;

import java.awt.Graphics;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;

public class SynthRadioButtonMenuItemUI extends SynthMenuItemUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new SynthRadioButtonMenuItemUI();
    }
    
    @Override
    protected String getPropertyPrefix() {
        return "RadioButtonMenuItem";
    }
    
    @Override
    void paintBackground(final SynthContext synthContext, final Graphics graphics, final JComponent component) {
        synthContext.getPainter().paintRadioButtonMenuItemBackground(synthContext, graphics, 0, 0, component.getWidth(), component.getHeight());
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintRadioButtonMenuItemBorder(synthContext, graphics, n, n2, n3, n4);
    }
}
