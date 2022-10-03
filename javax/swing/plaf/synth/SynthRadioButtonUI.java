package javax.swing.plaf.synth;

import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.AbstractButton;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;

public class SynthRadioButtonUI extends SynthToggleButtonUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new SynthRadioButtonUI();
    }
    
    @Override
    protected String getPropertyPrefix() {
        return "RadioButton.";
    }
    
    @Override
    protected Icon getSizingIcon(final AbstractButton abstractButton) {
        return this.getIcon(abstractButton);
    }
    
    @Override
    void paintBackground(final SynthContext synthContext, final Graphics graphics, final JComponent component) {
        synthContext.getPainter().paintRadioButtonBackground(synthContext, graphics, 0, 0, component.getWidth(), component.getHeight());
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintRadioButtonBorder(synthContext, graphics, n, n2, n3, n4);
    }
}
