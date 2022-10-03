package javax.swing.plaf.synth;

import java.awt.Graphics;
import javax.swing.JComponent;

public interface SynthUI extends SynthConstants
{
    SynthContext getContext(final JComponent p0);
    
    void paintBorder(final SynthContext p0, final Graphics p1, final int p2, final int p3, final int p4, final int p5);
}
