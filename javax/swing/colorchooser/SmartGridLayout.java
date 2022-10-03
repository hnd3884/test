package javax.swing.colorchooser;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Container;
import java.awt.Component;
import java.io.Serializable;
import java.awt.LayoutManager;

class SmartGridLayout implements LayoutManager, Serializable
{
    int rows;
    int columns;
    int xGap;
    int yGap;
    int componentCount;
    Component[][] layoutGrid;
    
    public SmartGridLayout(final int columns, final int rows) {
        this.rows = 2;
        this.columns = 2;
        this.xGap = 2;
        this.yGap = 2;
        this.componentCount = 0;
        this.rows = rows;
        this.columns = columns;
        this.layoutGrid = new Component[columns][rows];
    }
    
    @Override
    public void layoutContainer(final Container container) {
        this.buildLayoutGrid(container);
        final int[] array = new int[this.rows];
        final int[] array2 = new int[this.columns];
        for (int i = 0; i < this.rows; ++i) {
            array[i] = this.computeRowHeight(i);
        }
        for (int j = 0; j < this.columns; ++j) {
            array2[j] = this.computeColumnWidth(j);
        }
        final Insets insets = container.getInsets();
        if (container.getComponentOrientation().isLeftToRight()) {
            int left = insets.left;
            for (int k = 0; k < this.columns; ++k) {
                int top = insets.top;
                for (int l = 0; l < this.rows; ++l) {
                    this.layoutGrid[k][l].setBounds(left, top, array2[k], array[l]);
                    top += array[l] + this.yGap;
                }
                left += array2[k] + this.xGap;
            }
        }
        else {
            int n = container.getWidth() - insets.right;
            for (int n2 = 0; n2 < this.columns; ++n2) {
                int top2 = insets.top;
                final int n3 = n - array2[n2];
                for (int n4 = 0; n4 < this.rows; ++n4) {
                    this.layoutGrid[n2][n4].setBounds(n3, top2, array2[n2], array[n4]);
                    top2 += array[n4] + this.yGap;
                }
                n = n3 - this.xGap;
            }
        }
    }
    
    @Override
    public Dimension minimumLayoutSize(final Container container) {
        this.buildLayoutGrid(container);
        final Insets insets = container.getInsets();
        int n = 0;
        int n2 = 0;
        for (int i = 0; i < this.rows; ++i) {
            n += this.computeRowHeight(i);
        }
        for (int j = 0; j < this.columns; ++j) {
            n2 += this.computeColumnWidth(j);
        }
        return new Dimension(n2 + (this.xGap * (this.columns - 1) + insets.right + insets.left), n + (this.yGap * (this.rows - 1) + insets.top + insets.bottom));
    }
    
    @Override
    public Dimension preferredLayoutSize(final Container container) {
        return this.minimumLayoutSize(container);
    }
    
    @Override
    public void addLayoutComponent(final String s, final Component component) {
    }
    
    @Override
    public void removeLayoutComponent(final Component component) {
    }
    
    private void buildLayoutGrid(final Container container) {
        final Component[] components = container.getComponents();
        for (int i = 0; i < components.length; ++i) {
            int n = 0;
            int n2 = 0;
            if (i != 0) {
                n2 = i % this.columns;
                n = (i - n2) / this.columns;
            }
            this.layoutGrid[n2][n] = components[i];
        }
    }
    
    private int computeColumnWidth(final int n) {
        int n2 = 1;
        for (int i = 0; i < this.rows; ++i) {
            final int width = this.layoutGrid[n][i].getPreferredSize().width;
            if (width > n2) {
                n2 = width;
            }
        }
        return n2;
    }
    
    private int computeRowHeight(final int n) {
        int n2 = 1;
        for (int i = 0; i < this.columns; ++i) {
            final int height = this.layoutGrid[i][n].getPreferredSize().height;
            if (height > n2) {
                n2 = height;
            }
        }
        return n2;
    }
}
