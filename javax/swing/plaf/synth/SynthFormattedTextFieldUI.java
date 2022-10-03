package javax.swing.plaf.synth;

import java.awt.Graphics;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;

public class SynthFormattedTextFieldUI extends SynthTextFieldUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new SynthFormattedTextFieldUI();
    }
    
    @Override
    protected String getPropertyPrefix() {
        return "FormattedTextField";
    }
    
    @Override
    void paintBackground(final SynthContext synthContext, final Graphics graphics, final JComponent component) {
        synthContext.getPainter().paintFormattedTextFieldBackground(synthContext, graphics, 0, 0, component.getWidth(), component.getHeight());
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintFormattedTextFieldBorder(synthContext, graphics, n, n2, n3, n4);
    }
}
