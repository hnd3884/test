package javax.swing.plaf.synth;

import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.text.JTextComponent;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.plaf.UIResource;
import javax.swing.border.AbstractBorder;

class SynthBorder extends AbstractBorder implements UIResource
{
    private SynthUI ui;
    private Insets insets;
    
    SynthBorder(final SynthUI ui, final Insets insets) {
        this.ui = ui;
        this.insets = insets;
    }
    
    SynthBorder(final SynthUI synthUI) {
        this(synthUI, null);
    }
    
    @Override
    public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        final SynthContext context = this.ui.getContext((JComponent)component);
        if (context.getStyle() != null) {
            this.ui.paintBorder(context, graphics, n, n2, n3, n4);
            context.dispose();
            return;
        }
        assert false : "SynthBorder is being used outside after the UI has been uninstalled";
    }
    
    @Override
    public Insets getBorderInsets(final Component component, Insets insets) {
        if (this.insets != null) {
            if (insets == null) {
                insets = new Insets(this.insets.top, this.insets.left, this.insets.bottom, this.insets.right);
            }
            else {
                insets.top = this.insets.top;
                insets.bottom = this.insets.bottom;
                insets.left = this.insets.left;
                insets.right = this.insets.right;
            }
        }
        else if (insets == null) {
            insets = new Insets(0, 0, 0, 0);
        }
        else {
            final Insets insets2 = insets;
            final Insets insets3 = insets;
            final Insets insets4 = insets;
            final Insets insets5 = insets;
            final int n = 0;
            insets5.right = n;
            insets4.left = n;
            insets3.bottom = n;
            insets2.top = n;
        }
        if (component instanceof JComponent) {
            final Region region = Region.getRegion((JComponent)component);
            Insets insets6 = null;
            if ((region == Region.ARROW_BUTTON || region == Region.BUTTON || region == Region.CHECK_BOX || region == Region.CHECK_BOX_MENU_ITEM || region == Region.MENU || region == Region.MENU_ITEM || region == Region.RADIO_BUTTON || region == Region.RADIO_BUTTON_MENU_ITEM || region == Region.TOGGLE_BUTTON) && component instanceof AbstractButton) {
                insets6 = ((AbstractButton)component).getMargin();
            }
            else if ((region == Region.EDITOR_PANE || region == Region.FORMATTED_TEXT_FIELD || region == Region.PASSWORD_FIELD || region == Region.TEXT_AREA || region == Region.TEXT_FIELD || region == Region.TEXT_PANE) && component instanceof JTextComponent) {
                insets6 = ((JTextComponent)component).getMargin();
            }
            else if (region == Region.TOOL_BAR && component instanceof JToolBar) {
                insets6 = ((JToolBar)component).getMargin();
            }
            else if (region == Region.MENU_BAR && component instanceof JMenuBar) {
                insets6 = ((JMenuBar)component).getMargin();
            }
            if (insets6 != null) {
                final Insets insets7 = insets;
                insets7.top += insets6.top;
                final Insets insets8 = insets;
                insets8.bottom += insets6.bottom;
                final Insets insets9 = insets;
                insets9.left += insets6.left;
                final Insets insets10 = insets;
                insets10.right += insets6.right;
            }
        }
        return insets;
    }
    
    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
