package javax.swing.plaf.metal;

import java.awt.image.ImageObserver;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import sun.awt.AppContext;
import java.awt.GraphicsConfiguration;
import java.awt.Color;
import javax.swing.Icon;

class MetalBumps implements Icon
{
    static final Color ALPHA;
    protected int xBumps;
    protected int yBumps;
    protected Color topColor;
    protected Color shadowColor;
    protected Color backColor;
    private static final Object METAL_BUMPS;
    protected BumpBuffer buffer;
    
    public MetalBumps(final int n, final int n2, final Color color, final Color color2, final Color color3) {
        this.setBumpArea(n, n2);
        this.setBumpColors(color, color2, color3);
    }
    
    private static BumpBuffer createBuffer(final GraphicsConfiguration graphicsConfiguration, final Color color, final Color color2, final Color color3) {
        final AppContext appContext = AppContext.getAppContext();
        List list = (List)appContext.get(MetalBumps.METAL_BUMPS);
        if (list == null) {
            list = new ArrayList();
            appContext.put(MetalBumps.METAL_BUMPS, list);
        }
        for (final BumpBuffer bumpBuffer : list) {
            if (bumpBuffer.hasSameConfiguration(graphicsConfiguration, color, color2, color3)) {
                return bumpBuffer;
            }
        }
        final BumpBuffer bumpBuffer2 = new BumpBuffer(graphicsConfiguration, color, color2, color3);
        list.add(bumpBuffer2);
        return bumpBuffer2;
    }
    
    public void setBumpArea(final Dimension dimension) {
        this.setBumpArea(dimension.width, dimension.height);
    }
    
    public void setBumpArea(final int n, final int n2) {
        this.xBumps = n / 2;
        this.yBumps = n2 / 2;
    }
    
    public void setBumpColors(final Color topColor, final Color shadowColor, final Color backColor) {
        this.topColor = topColor;
        this.shadowColor = shadowColor;
        if (backColor == null) {
            this.backColor = MetalBumps.ALPHA;
        }
        else {
            this.backColor = backColor;
        }
    }
    
    @Override
    public void paintIcon(final Component component, final Graphics graphics, int i, int j) {
        final GraphicsConfiguration graphicsConfiguration = (graphics instanceof Graphics2D) ? ((Graphics2D)graphics).getDeviceConfiguration() : null;
        if (this.buffer == null || !this.buffer.hasSameConfiguration(graphicsConfiguration, this.topColor, this.shadowColor, this.backColor)) {
            this.buffer = createBuffer(graphicsConfiguration, this.topColor, this.shadowColor, this.backColor);
        }
        final int n = 64;
        final int n2 = 64;
        final int iconWidth = this.getIconWidth();
        final int iconHeight = this.getIconHeight();
        final int n3 = i + iconWidth;
        final int n4 = j + iconHeight;
        final int n5 = i;
        while (j < n4) {
            final int min = Math.min(n4 - j, n2);
            int min2;
            for (i = n5; i < n3; i += n) {
                min2 = Math.min(n3 - i, n);
                graphics.drawImage(this.buffer.getImage(), i, j, i + min2, j + min, 0, 0, min2, min, null);
            }
            j += n2;
        }
    }
    
    @Override
    public int getIconWidth() {
        return this.xBumps * 2;
    }
    
    @Override
    public int getIconHeight() {
        return this.yBumps * 2;
    }
    
    static {
        ALPHA = new Color(0, 0, 0, 0);
        METAL_BUMPS = new Object();
    }
}
