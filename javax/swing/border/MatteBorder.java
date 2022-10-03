package javax.swing.border;

import java.awt.Graphics;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.Icon;
import java.awt.Color;

public class MatteBorder extends EmptyBorder
{
    protected Color color;
    protected Icon tileIcon;
    
    public MatteBorder(final int n, final int n2, final int n3, final int n4, final Color color) {
        super(n, n2, n3, n4);
        this.color = color;
    }
    
    public MatteBorder(final Insets insets, final Color color) {
        super(insets);
        this.color = color;
    }
    
    public MatteBorder(final int n, final int n2, final int n3, final int n4, final Icon tileIcon) {
        super(n, n2, n3, n4);
        this.tileIcon = tileIcon;
    }
    
    public MatteBorder(final Insets insets, final Icon tileIcon) {
        super(insets);
        this.tileIcon = tileIcon;
    }
    
    public MatteBorder(final Icon icon) {
        this(-1, -1, -1, -1, icon);
    }
    
    @Override
    public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        final Insets borderInsets = this.getBorderInsets(component);
        final Color color = graphics.getColor();
        graphics.translate(n, n2);
        if (this.tileIcon != null) {
            this.color = ((this.tileIcon.getIconWidth() == -1) ? Color.gray : null);
        }
        if (this.color != null) {
            graphics.setColor(this.color);
            graphics.fillRect(0, 0, n3 - borderInsets.right, borderInsets.top);
            graphics.fillRect(0, borderInsets.top, borderInsets.left, n4 - borderInsets.top);
            graphics.fillRect(borderInsets.left, n4 - borderInsets.bottom, n3 - borderInsets.left, borderInsets.bottom);
            graphics.fillRect(n3 - borderInsets.right, 0, borderInsets.right, n4 - borderInsets.bottom);
        }
        else if (this.tileIcon != null) {
            final int iconWidth = this.tileIcon.getIconWidth();
            final int iconHeight = this.tileIcon.getIconHeight();
            this.paintEdge(component, graphics, 0, 0, n3 - borderInsets.right, borderInsets.top, iconWidth, iconHeight);
            this.paintEdge(component, graphics, 0, borderInsets.top, borderInsets.left, n4 - borderInsets.top, iconWidth, iconHeight);
            this.paintEdge(component, graphics, borderInsets.left, n4 - borderInsets.bottom, n3 - borderInsets.left, borderInsets.bottom, iconWidth, iconHeight);
            this.paintEdge(component, graphics, n3 - borderInsets.right, 0, borderInsets.right, n4 - borderInsets.bottom, iconWidth, iconHeight);
        }
        graphics.translate(-n, -n2);
        graphics.setColor(color);
    }
    
    private void paintEdge(final Component component, Graphics create, int i, int j, final int n, final int n2, final int n3, final int n4) {
        create = create.create(i, j, n, n2);
        final int n5 = -(j % n4);
        for (i = -(i % n3); i < n; i += n3) {
            for (j = n5; j < n2; j += n4) {
                this.tileIcon.paintIcon(component, create, i, j);
            }
        }
        create.dispose();
    }
    
    @Override
    public Insets getBorderInsets(final Component component, final Insets insets) {
        return this.computeInsets(insets);
    }
    
    @Override
    public Insets getBorderInsets() {
        return this.computeInsets(new Insets(0, 0, 0, 0));
    }
    
    private Insets computeInsets(final Insets insets) {
        if (this.tileIcon != null && this.top == -1 && this.bottom == -1 && this.left == -1 && this.right == -1) {
            final int iconWidth = this.tileIcon.getIconWidth();
            final int iconHeight = this.tileIcon.getIconHeight();
            insets.top = iconHeight;
            insets.right = iconWidth;
            insets.bottom = iconHeight;
            insets.left = iconWidth;
        }
        else {
            insets.left = this.left;
            insets.top = this.top;
            insets.right = this.right;
            insets.bottom = this.bottom;
        }
        return insets;
    }
    
    public Color getMatteColor() {
        return this.color;
    }
    
    public Icon getTileIcon() {
        return this.tileIcon;
    }
    
    @Override
    public boolean isBorderOpaque() {
        return this.color != null;
    }
}
