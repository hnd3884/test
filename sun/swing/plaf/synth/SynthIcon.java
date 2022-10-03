package sun.swing.plaf.synth;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.Icon;

public abstract class SynthIcon implements Icon
{
    public static int getIconWidth(final Icon icon, final SynthContext synthContext) {
        if (icon == null) {
            return 0;
        }
        if (icon instanceof SynthIcon) {
            return ((SynthIcon)icon).getIconWidth(synthContext);
        }
        return icon.getIconWidth();
    }
    
    public static int getIconHeight(final Icon icon, final SynthContext synthContext) {
        if (icon == null) {
            return 0;
        }
        if (icon instanceof SynthIcon) {
            return ((SynthIcon)icon).getIconHeight(synthContext);
        }
        return icon.getIconHeight();
    }
    
    public static void paintIcon(final Icon icon, final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        if (icon instanceof SynthIcon) {
            ((SynthIcon)icon).paintIcon(synthContext, graphics, n, n2, n3, n4);
        }
        else if (icon != null) {
            icon.paintIcon(synthContext.getComponent(), graphics, n, n2);
        }
    }
    
    public abstract void paintIcon(final SynthContext p0, final Graphics p1, final int p2, final int p3, final int p4, final int p5);
    
    public abstract int getIconWidth(final SynthContext p0);
    
    public abstract int getIconHeight(final SynthContext p0);
    
    @Override
    public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
        this.paintIcon(null, graphics, n, n2, 0, 0);
    }
    
    @Override
    public int getIconWidth() {
        return this.getIconWidth(null);
    }
    
    @Override
    public int getIconHeight() {
        return this.getIconHeight(null);
    }
}
