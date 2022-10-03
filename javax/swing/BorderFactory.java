package javax.swing;

import java.awt.Paint;
import javax.swing.border.StrokeBorder;
import java.awt.BasicStroke;
import javax.swing.border.MatteBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.Border;

public class BorderFactory
{
    static final Border sharedRaisedBevel;
    static final Border sharedLoweredBevel;
    private static Border sharedSoftRaisedBevel;
    private static Border sharedSoftLoweredBevel;
    static final Border sharedEtchedBorder;
    private static Border sharedRaisedEtchedBorder;
    static final Border emptyBorder;
    private static Border sharedDashedBorder;
    
    private BorderFactory() {
    }
    
    public static Border createLineBorder(final Color color) {
        return new LineBorder(color, 1);
    }
    
    public static Border createLineBorder(final Color color, final int n) {
        return new LineBorder(color, n);
    }
    
    public static Border createLineBorder(final Color color, final int n, final boolean b) {
        return new LineBorder(color, n, b);
    }
    
    public static Border createRaisedBevelBorder() {
        return createSharedBevel(0);
    }
    
    public static Border createLoweredBevelBorder() {
        return createSharedBevel(1);
    }
    
    public static Border createBevelBorder(final int n) {
        return createSharedBevel(n);
    }
    
    public static Border createBevelBorder(final int n, final Color color, final Color color2) {
        return new BevelBorder(n, color, color2);
    }
    
    public static Border createBevelBorder(final int n, final Color color, final Color color2, final Color color3, final Color color4) {
        return new BevelBorder(n, color, color2, color3, color4);
    }
    
    static Border createSharedBevel(final int n) {
        if (n == 0) {
            return BorderFactory.sharedRaisedBevel;
        }
        if (n == 1) {
            return BorderFactory.sharedLoweredBevel;
        }
        return null;
    }
    
    public static Border createRaisedSoftBevelBorder() {
        if (BorderFactory.sharedSoftRaisedBevel == null) {
            BorderFactory.sharedSoftRaisedBevel = new SoftBevelBorder(0);
        }
        return BorderFactory.sharedSoftRaisedBevel;
    }
    
    public static Border createLoweredSoftBevelBorder() {
        if (BorderFactory.sharedSoftLoweredBevel == null) {
            BorderFactory.sharedSoftLoweredBevel = new SoftBevelBorder(1);
        }
        return BorderFactory.sharedSoftLoweredBevel;
    }
    
    public static Border createSoftBevelBorder(final int n) {
        if (n == 0) {
            return createRaisedSoftBevelBorder();
        }
        if (n == 1) {
            return createLoweredSoftBevelBorder();
        }
        return null;
    }
    
    public static Border createSoftBevelBorder(final int n, final Color color, final Color color2) {
        return new SoftBevelBorder(n, color, color2);
    }
    
    public static Border createSoftBevelBorder(final int n, final Color color, final Color color2, final Color color3, final Color color4) {
        return new SoftBevelBorder(n, color, color2, color3, color4);
    }
    
    public static Border createEtchedBorder() {
        return BorderFactory.sharedEtchedBorder;
    }
    
    public static Border createEtchedBorder(final Color color, final Color color2) {
        return new EtchedBorder(color, color2);
    }
    
    public static Border createEtchedBorder(final int n) {
        switch (n) {
            case 0: {
                if (BorderFactory.sharedRaisedEtchedBorder == null) {
                    BorderFactory.sharedRaisedEtchedBorder = new EtchedBorder(0);
                }
                return BorderFactory.sharedRaisedEtchedBorder;
            }
            case 1: {
                return BorderFactory.sharedEtchedBorder;
            }
            default: {
                throw new IllegalArgumentException("type must be one of EtchedBorder.RAISED or EtchedBorder.LOWERED");
            }
        }
    }
    
    public static Border createEtchedBorder(final int n, final Color color, final Color color2) {
        return new EtchedBorder(n, color, color2);
    }
    
    public static TitledBorder createTitledBorder(final String s) {
        return new TitledBorder(s);
    }
    
    public static TitledBorder createTitledBorder(final Border border) {
        return new TitledBorder(border);
    }
    
    public static TitledBorder createTitledBorder(final Border border, final String s) {
        return new TitledBorder(border, s);
    }
    
    public static TitledBorder createTitledBorder(final Border border, final String s, final int n, final int n2) {
        return new TitledBorder(border, s, n, n2);
    }
    
    public static TitledBorder createTitledBorder(final Border border, final String s, final int n, final int n2, final Font font) {
        return new TitledBorder(border, s, n, n2, font);
    }
    
    public static TitledBorder createTitledBorder(final Border border, final String s, final int n, final int n2, final Font font, final Color color) {
        return new TitledBorder(border, s, n, n2, font, color);
    }
    
    public static Border createEmptyBorder() {
        return BorderFactory.emptyBorder;
    }
    
    public static Border createEmptyBorder(final int n, final int n2, final int n3, final int n4) {
        return new EmptyBorder(n, n2, n3, n4);
    }
    
    public static CompoundBorder createCompoundBorder() {
        return new CompoundBorder();
    }
    
    public static CompoundBorder createCompoundBorder(final Border border, final Border border2) {
        return new CompoundBorder(border, border2);
    }
    
    public static MatteBorder createMatteBorder(final int n, final int n2, final int n3, final int n4, final Color color) {
        return new MatteBorder(n, n2, n3, n4, color);
    }
    
    public static MatteBorder createMatteBorder(final int n, final int n2, final int n3, final int n4, final Icon icon) {
        return new MatteBorder(n, n2, n3, n4, icon);
    }
    
    public static Border createStrokeBorder(final BasicStroke basicStroke) {
        return new StrokeBorder(basicStroke);
    }
    
    public static Border createStrokeBorder(final BasicStroke basicStroke, final Paint paint) {
        return new StrokeBorder(basicStroke, paint);
    }
    
    public static Border createDashedBorder(final Paint paint) {
        return createDashedBorder(paint, 1.0f, 1.0f, 1.0f, false);
    }
    
    public static Border createDashedBorder(final Paint paint, final float n, final float n2) {
        return createDashedBorder(paint, 1.0f, n, n2, false);
    }
    
    public static Border createDashedBorder(final Paint paint, final float n, final float n2, final float n3, final boolean b) {
        final boolean b2 = !b && paint == null && n == 1.0f && n2 == 1.0f && n3 == 1.0f;
        if (b2 && BorderFactory.sharedDashedBorder != null) {
            return BorderFactory.sharedDashedBorder;
        }
        if (n < 1.0f) {
            throw new IllegalArgumentException("thickness is less than 1");
        }
        if (n2 < 1.0f) {
            throw new IllegalArgumentException("length is less than 1");
        }
        if (n3 < 0.0f) {
            throw new IllegalArgumentException("spacing is less than 0");
        }
        final Border strokeBorder = createStrokeBorder(new BasicStroke(n, b ? 1 : 2, (int)(b ? 1 : 0), n * 2.0f, new float[] { n * (n2 - 1.0f), n * (n3 + 1.0f) }, 0.0f), paint);
        if (b2) {
            BorderFactory.sharedDashedBorder = strokeBorder;
        }
        return strokeBorder;
    }
    
    static {
        sharedRaisedBevel = new BevelBorder(0);
        sharedLoweredBevel = new BevelBorder(1);
        sharedEtchedBorder = new EtchedBorder();
        emptyBorder = new EmptyBorder(0, 0, 0, 0);
    }
}
