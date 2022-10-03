package sun.swing.plaf.windows;

import javax.swing.UIManager;
import java.awt.Graphics;
import java.awt.Component;
import java.io.Serializable;
import javax.swing.plaf.UIResource;
import javax.swing.Icon;

public class ClassicSortArrowIcon implements Icon, UIResource, Serializable
{
    private static final int X_OFFSET = 9;
    private boolean ascending;
    
    public ClassicSortArrowIcon(final boolean ascending) {
        this.ascending = ascending;
    }
    
    @Override
    public void paintIcon(final Component component, final Graphics graphics, int n, final int n2) {
        n += 9;
        if (this.ascending) {
            graphics.setColor(UIManager.getColor("Table.sortIconHighlight"));
            this.drawSide(graphics, n + 3, n2, -1);
            graphics.setColor(UIManager.getColor("Table.sortIconLight"));
            this.drawSide(graphics, n + 4, n2, 1);
            graphics.fillRect(n + 1, n2 + 6, 6, 1);
        }
        else {
            graphics.setColor(UIManager.getColor("Table.sortIconHighlight"));
            this.drawSide(graphics, n + 3, n2 + 6, -1);
            graphics.fillRect(n + 1, n2, 6, 1);
            graphics.setColor(UIManager.getColor("Table.sortIconLight"));
            this.drawSide(graphics, n + 4, n2 + 6, 1);
        }
    }
    
    private void drawSide(final Graphics graphics, int n, int n2, final int n3) {
        int n4 = 2;
        if (this.ascending) {
            graphics.fillRect(n, n2, 1, 2);
            ++n2;
        }
        else {
            graphics.fillRect(n, --n2, 1, 2);
            n4 = -2;
            n2 -= 2;
        }
        n += n3;
        for (int i = 0; i < 2; ++i) {
            graphics.fillRect(n, n2, 1, 3);
            n += n3;
            n2 += n4;
        }
        if (!this.ascending) {
            ++n2;
        }
        graphics.fillRect(n, n2, 1, 2);
    }
    
    @Override
    public int getIconWidth() {
        return 17;
    }
    
    @Override
    public int getIconHeight() {
        return 9;
    }
}
