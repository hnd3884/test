package javax.swing.plaf.synth;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.SwingUtilities;
import java.awt.Graphics;
import javax.swing.text.PasswordView;
import javax.swing.text.View;
import javax.swing.text.Element;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;

public class SynthPasswordFieldUI extends SynthTextFieldUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new SynthPasswordFieldUI();
    }
    
    @Override
    protected String getPropertyPrefix() {
        return "PasswordField";
    }
    
    @Override
    public View create(final Element element) {
        return new PasswordView(element);
    }
    
    @Override
    void paintBackground(final SynthContext synthContext, final Graphics graphics, final JComponent component) {
        synthContext.getPainter().paintPasswordFieldBackground(synthContext, graphics, 0, 0, component.getWidth(), component.getHeight());
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintPasswordFieldBorder(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    protected void installKeyboardActions() {
        super.installKeyboardActions();
        final ActionMap uiActionMap = SwingUtilities.getUIActionMap(this.getComponent());
        if (uiActionMap != null && uiActionMap.get("select-word") != null) {
            final Action value = uiActionMap.get("select-line");
            if (value != null) {
                uiActionMap.put("select-word", value);
            }
        }
    }
}
