package java.awt;

import java.io.Serializable;

public class GridLayout implements LayoutManager, Serializable
{
    private static final long serialVersionUID = -7411804673224730901L;
    int hgap;
    int vgap;
    int rows;
    int cols;
    
    public GridLayout() {
        this(1, 0, 0, 0);
    }
    
    public GridLayout(final int n, final int n2) {
        this(n, n2, 0, 0);
    }
    
    public GridLayout(final int rows, final int cols, final int hgap, final int vgap) {
        if (rows == 0 && cols == 0) {
            throw new IllegalArgumentException("rows and cols cannot both be zero");
        }
        this.rows = rows;
        this.cols = cols;
        this.hgap = hgap;
        this.vgap = vgap;
    }
    
    public int getRows() {
        return this.rows;
    }
    
    public void setRows(final int rows) {
        if (rows == 0 && this.cols == 0) {
            throw new IllegalArgumentException("rows and cols cannot both be zero");
        }
        this.rows = rows;
    }
    
    public int getColumns() {
        return this.cols;
    }
    
    public void setColumns(final int cols) {
        if (cols == 0 && this.rows == 0) {
            throw new IllegalArgumentException("rows and cols cannot both be zero");
        }
        this.cols = cols;
    }
    
    public int getHgap() {
        return this.hgap;
    }
    
    public void setHgap(final int hgap) {
        this.hgap = hgap;
    }
    
    public int getVgap() {
        return this.vgap;
    }
    
    public void setVgap(final int vgap) {
        this.vgap = vgap;
    }
    
    @Override
    public void addLayoutComponent(final String s, final Component component) {
    }
    
    @Override
    public void removeLayoutComponent(final Component component) {
    }
    
    @Override
    public Dimension preferredLayoutSize(final Container container) {
        synchronized (container.getTreeLock()) {
            final Insets insets = container.getInsets();
            final int componentCount = container.getComponentCount();
            int rows = this.rows;
            int cols = this.cols;
            if (rows > 0) {
                cols = (componentCount + rows - 1) / rows;
            }
            else {
                rows = (componentCount + cols - 1) / cols;
            }
            int width = 0;
            int height = 0;
            for (int i = 0; i < componentCount; ++i) {
                final Dimension preferredSize = container.getComponent(i).getPreferredSize();
                if (width < preferredSize.width) {
                    width = preferredSize.width;
                }
                if (height < preferredSize.height) {
                    height = preferredSize.height;
                }
            }
            return new Dimension(insets.left + insets.right + cols * width + (cols - 1) * this.hgap, insets.top + insets.bottom + rows * height + (rows - 1) * this.vgap);
        }
    }
    
    @Override
    public Dimension minimumLayoutSize(final Container container) {
        synchronized (container.getTreeLock()) {
            final Insets insets = container.getInsets();
            final int componentCount = container.getComponentCount();
            int rows = this.rows;
            int cols = this.cols;
            if (rows > 0) {
                cols = (componentCount + rows - 1) / rows;
            }
            else {
                rows = (componentCount + cols - 1) / cols;
            }
            int width = 0;
            int height = 0;
            for (int i = 0; i < componentCount; ++i) {
                final Dimension minimumSize = container.getComponent(i).getMinimumSize();
                if (width < minimumSize.width) {
                    width = minimumSize.width;
                }
                if (height < minimumSize.height) {
                    height = minimumSize.height;
                }
            }
            return new Dimension(insets.left + insets.right + cols * width + (cols - 1) * this.hgap, insets.top + insets.bottom + rows * height + (rows - 1) * this.vgap);
        }
    }
    
    @Override
    public void layoutContainer(final Container container) {
        synchronized (container.getTreeLock()) {
            final Insets insets = container.getInsets();
            final int componentCount = container.getComponentCount();
            int rows = this.rows;
            int cols = this.cols;
            final boolean leftToRight = container.getComponentOrientation().isLeftToRight();
            if (componentCount == 0) {
                return;
            }
            if (rows > 0) {
                cols = (componentCount + rows - 1) / rows;
            }
            else {
                rows = (componentCount + cols - 1) / cols;
            }
            final int n = (cols - 1) * this.hgap;
            final int n2 = container.width - (insets.left + insets.right);
            final int n3 = (n2 - n) / cols;
            final int n4 = (n2 - (n3 * cols + n)) / 2;
            final int n5 = (rows - 1) * this.vgap;
            final int n6 = container.height - (insets.top + insets.bottom);
            final int n7 = (n6 - n5) / rows;
            final int n8 = (n6 - (n7 * rows + n5)) / 2;
            if (leftToRight) {
                for (int i = 0, n9 = insets.left + n4; i < cols; ++i, n9 += n3 + this.hgap) {
                    for (int j = 0, n10 = insets.top + n8; j < rows; ++j, n10 += n7 + this.vgap) {
                        final int n11 = j * cols + i;
                        if (n11 < componentCount) {
                            container.getComponent(n11).setBounds(n9, n10, n3, n7);
                        }
                    }
                }
            }
            else {
                for (int k = 0, n12 = container.width - insets.right - n3 - n4; k < cols; ++k, n12 -= n3 + this.hgap) {
                    for (int l = 0, n13 = insets.top + n8; l < rows; ++l, n13 += n7 + this.vgap) {
                        final int n14 = l * cols + k;
                        if (n14 < componentCount) {
                            container.getComponent(n14).setBounds(n12, n13, n3, n7);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[hgap=" + this.hgap + ",vgap=" + this.vgap + ",rows=" + this.rows + ",cols=" + this.cols + "]";
    }
}
