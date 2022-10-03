package sun.swing.icon;

import javax.swing.UIManager;
import java.awt.Graphics;
import java.awt.Component;
import java.awt.Color;
import java.io.Serializable;
import javax.swing.plaf.UIResource;
import javax.swing.Icon;

public class SortArrowIcon implements Icon, UIResource, Serializable
{
    private static final int ARROW_HEIGHT = 5;
    private static final int X_PADDING = 7;
    private boolean ascending;
    private Color color;
    private String colorKey;
    
    public SortArrowIcon(final boolean ascending, final Color color) {
        this.ascending = ascending;
        this.color = color;
        if (color == null) {
            throw new IllegalArgumentException();
        }
    }
    
    public SortArrowIcon(final boolean ascending, final String colorKey) {
        this.ascending = ascending;
        this.colorKey = colorKey;
        if (colorKey == null) {
            throw new IllegalArgumentException();
        }
    }
    
    @Override
    public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
        graphics.setColor(this.getColor());
        final int n3 = 7 + n + 2;
        if (this.ascending) {
            graphics.fillRect(n3, n2, 1, 1);
            for (int i = 1; i < 5; ++i) {
                graphics.fillRect(n3 - i, n2 + i, i + i + 1, 1);
            }
        }
        else {
            final int n4 = n2 + 5 - 1;
            graphics.fillRect(n3, n4, 1, 1);
            for (int j = 1; j < 5; ++j) {
                graphics.fillRect(n3 - j, n4 - j, j + j + 1, 1);
            }
        }
    }
    
    @Override
    public int getIconWidth() {
        return 17;
    }
    
    @Override
    public int getIconHeight() {
        return 7;
    }
    
    private Color getColor() {
        if (this.color != null) {
            return this.color;
        }
        return UIManager.getColor(this.colorKey);
    }
}
