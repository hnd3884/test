package javax.swing.plaf;

import java.beans.ConstructorProperties;
import java.awt.Color;

public class ColorUIResource extends Color implements UIResource
{
    @ConstructorProperties({ "red", "green", "blue" })
    public ColorUIResource(final int n, final int n2, final int n3) {
        super(n, n2, n3);
    }
    
    public ColorUIResource(final int n) {
        super(n);
    }
    
    public ColorUIResource(final float n, final float n2, final float n3) {
        super(n, n2, n3);
    }
    
    public ColorUIResource(final Color color) {
        super(color.getRGB(), (color.getRGB() & 0xFF000000) != 0xFF000000);
    }
}
