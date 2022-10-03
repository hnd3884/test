package java.awt;

import java.util.Arrays;
import java.util.Hashtable;
import java.io.Serializable;

public class GridBagLayout implements LayoutManager2, Serializable
{
    static final int EMPIRICMULTIPLIER = 2;
    protected static final int MAXGRIDSIZE = 512;
    protected static final int MINSIZE = 1;
    protected static final int PREFERREDSIZE = 2;
    protected Hashtable<Component, GridBagConstraints> comptable;
    protected GridBagConstraints defaultConstraints;
    protected GridBagLayoutInfo layoutInfo;
    public int[] columnWidths;
    public int[] rowHeights;
    public double[] columnWeights;
    public double[] rowWeights;
    private Component componentAdjusting;
    transient boolean rightToLeft;
    static final long serialVersionUID = 8838754796412211005L;
    
    public GridBagLayout() {
        this.rightToLeft = false;
        this.comptable = new Hashtable<Component, GridBagConstraints>();
        this.defaultConstraints = new GridBagConstraints();
    }
    
    public void setConstraints(final Component component, final GridBagConstraints gridBagConstraints) {
        this.comptable.put(component, (GridBagConstraints)gridBagConstraints.clone());
    }
    
    public GridBagConstraints getConstraints(final Component component) {
        GridBagConstraints gridBagConstraints = this.comptable.get(component);
        if (gridBagConstraints == null) {
            this.setConstraints(component, this.defaultConstraints);
            gridBagConstraints = this.comptable.get(component);
        }
        return (GridBagConstraints)gridBagConstraints.clone();
    }
    
    protected GridBagConstraints lookupConstraints(final Component component) {
        GridBagConstraints gridBagConstraints = this.comptable.get(component);
        if (gridBagConstraints == null) {
            this.setConstraints(component, this.defaultConstraints);
            gridBagConstraints = this.comptable.get(component);
        }
        return gridBagConstraints;
    }
    
    private void removeConstraints(final Component component) {
        this.comptable.remove(component);
    }
    
    public Point getLayoutOrigin() {
        final Point point = new Point(0, 0);
        if (this.layoutInfo != null) {
            point.x = this.layoutInfo.startx;
            point.y = this.layoutInfo.starty;
        }
        return point;
    }
    
    public int[][] getLayoutDimensions() {
        if (this.layoutInfo == null) {
            return new int[2][0];
        }
        final int[][] array = { new int[this.layoutInfo.width], new int[this.layoutInfo.height] };
        System.arraycopy(this.layoutInfo.minWidth, 0, array[0], 0, this.layoutInfo.width);
        System.arraycopy(this.layoutInfo.minHeight, 0, array[1], 0, this.layoutInfo.height);
        return array;
    }
    
    public double[][] getLayoutWeights() {
        if (this.layoutInfo == null) {
            return new double[2][0];
        }
        final double[][] array = { new double[this.layoutInfo.width], new double[this.layoutInfo.height] };
        System.arraycopy(this.layoutInfo.weightX, 0, array[0], 0, this.layoutInfo.width);
        System.arraycopy(this.layoutInfo.weightY, 0, array[1], 0, this.layoutInfo.height);
        return array;
    }
    
    public Point location(final int n, final int n2) {
        final Point point = new Point(0, 0);
        if (this.layoutInfo == null) {
            return point;
        }
        int startx = this.layoutInfo.startx;
        int i;
        if (!this.rightToLeft) {
            for (i = 0; i < this.layoutInfo.width; ++i) {
                startx += this.layoutInfo.minWidth[i];
                if (startx > n) {
                    break;
                }
            }
        }
        else {
            for (i = this.layoutInfo.width - 1; i >= 0 && startx <= n; startx += this.layoutInfo.minWidth[i], --i) {}
            ++i;
        }
        point.x = i;
        int starty = this.layoutInfo.starty;
        int j;
        for (j = 0; j < this.layoutInfo.height; ++j) {
            starty += this.layoutInfo.minHeight[j];
            if (starty > n2) {
                break;
            }
        }
        point.y = j;
        return point;
    }
    
    @Override
    public void addLayoutComponent(final String s, final Component component) {
    }
    
    @Override
    public void addLayoutComponent(final Component component, final Object o) {
        if (o instanceof GridBagConstraints) {
            this.setConstraints(component, (GridBagConstraints)o);
        }
        else if (o != null) {
            throw new IllegalArgumentException("cannot add to layout: constraints must be a GridBagConstraint");
        }
    }
    
    @Override
    public void removeLayoutComponent(final Component component) {
        this.removeConstraints(component);
    }
    
    @Override
    public Dimension preferredLayoutSize(final Container container) {
        return this.getMinSize(container, this.getLayoutInfo(container, 2));
    }
    
    @Override
    public Dimension minimumLayoutSize(final Container container) {
        return this.getMinSize(container, this.getLayoutInfo(container, 1));
    }
    
    @Override
    public Dimension maximumLayoutSize(final Container container) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    @Override
    public float getLayoutAlignmentX(final Container container) {
        return 0.5f;
    }
    
    @Override
    public float getLayoutAlignmentY(final Container container) {
        return 0.5f;
    }
    
    @Override
    public void invalidateLayout(final Container container) {
    }
    
    @Override
    public void layoutContainer(final Container container) {
        this.arrangeGrid(container);
    }
    
    @Override
    public String toString() {
        return this.getClass().getName();
    }
    
    protected GridBagLayoutInfo getLayoutInfo(final Container container, final int n) {
        return this.GetLayoutInfo(container, n);
    }
    
    private long[] preInitMaximumArraySizes(final Container container) {
        final Component[] components = container.getComponents();
        int max = 0;
        int max2 = 0;
        final long[] array = new long[2];
        for (int i = 0; i < components.length; ++i) {
            final Component component = components[i];
            if (component.isVisible()) {
                final GridBagConstraints lookupConstraints = this.lookupConstraints(component);
                int gridx = lookupConstraints.gridx;
                int gridy = lookupConstraints.gridy;
                int gridwidth = lookupConstraints.gridwidth;
                int gridheight = lookupConstraints.gridheight;
                if (gridx < 0) {
                    gridx = ++max2;
                }
                if (gridy < 0) {
                    gridy = ++max;
                }
                if (gridwidth <= 0) {
                    gridwidth = 1;
                }
                if (gridheight <= 0) {
                    gridheight = 1;
                }
                max = Math.max(gridy + gridheight, max);
                max2 = Math.max(gridx + gridwidth, max2);
            }
        }
        array[0] = max;
        array[1] = max2;
        return array;
    }
    
    protected GridBagLayoutInfo GetLayoutInfo(final Container container, final int n) {
        synchronized (container.getTreeLock()) {
            final Component[] components = container.getComponents();
            int length2;
            int length = length2 = 0;
            int n3;
            int n2 = n3 = -1;
            final long[] preInitMaximumArraySizes = this.preInitMaximumArraySizes(container);
            int max = (2L * preInitMaximumArraySizes[0] > 2147483647L) ? Integer.MAX_VALUE : (2 * (int)preInitMaximumArraySizes[0]);
            int max2 = (2L * preInitMaximumArraySizes[1] > 2147483647L) ? Integer.MAX_VALUE : (2 * (int)preInitMaximumArraySizes[1]);
            if (this.rowHeights != null) {
                max = Math.max(max, this.rowHeights.length);
            }
            if (this.columnWidths != null) {
                max2 = Math.max(max2, this.columnWidths.length);
            }
            final int[] array = new int[max];
            final int[] array2 = new int[max2];
            boolean b = false;
            for (int i = 0; i < components.length; ++i) {
                final Component component = components[i];
                if (component.isVisible()) {
                    final GridBagConstraints lookupConstraints = this.lookupConstraints(component);
                    int gridx = lookupConstraints.gridx;
                    int gridy = lookupConstraints.gridy;
                    int gridwidth = lookupConstraints.gridwidth;
                    if (gridwidth <= 0) {
                        gridwidth = 1;
                    }
                    int gridheight = lookupConstraints.gridheight;
                    if (gridheight <= 0) {
                        gridheight = 1;
                    }
                    if (gridx < 0 && gridy < 0) {
                        if (n3 >= 0) {
                            gridy = n3;
                        }
                        else if (n2 >= 0) {
                            gridx = n2;
                        }
                        else {
                            gridy = 0;
                        }
                    }
                    if (gridx < 0) {
                        int max3 = 0;
                        for (int j = gridy; j < gridy + gridheight; ++j) {
                            max3 = Math.max(max3, array[j]);
                        }
                        gridx = max3 - gridx - 1;
                        if (gridx < 0) {
                            gridx = 0;
                        }
                    }
                    else if (gridy < 0) {
                        int max4 = 0;
                        for (int k = gridx; k < gridx + gridwidth; ++k) {
                            max4 = Math.max(max4, array2[k]);
                        }
                        gridy = max4 - gridy - 1;
                        if (gridy < 0) {
                            gridy = 0;
                        }
                    }
                    final int n4 = gridx + gridwidth;
                    if (length2 < n4) {
                        length2 = n4;
                    }
                    final int n5 = gridy + gridheight;
                    if (length < n5) {
                        length = n5;
                    }
                    for (int l = gridx; l < gridx + gridwidth; ++l) {
                        array2[l] = n5;
                    }
                    for (int n6 = gridy; n6 < gridy + gridheight; ++n6) {
                        array[n6] = n4;
                    }
                    Dimension dimension;
                    if (n == 2) {
                        dimension = component.getPreferredSize();
                    }
                    else {
                        dimension = component.getMinimumSize();
                    }
                    lookupConstraints.minWidth = dimension.width;
                    lookupConstraints.minHeight = dimension.height;
                    if (this.calculateBaseline(component, lookupConstraints, dimension)) {
                        b = true;
                    }
                    if (lookupConstraints.gridheight == 0 && lookupConstraints.gridwidth == 0) {
                        n2 = (n3 = -1);
                    }
                    if (lookupConstraints.gridheight == 0 && n3 < 0) {
                        n2 = gridx + gridwidth;
                    }
                    else if (lookupConstraints.gridwidth == 0 && n2 < 0) {
                        n3 = gridy + gridheight;
                    }
                }
            }
            if (this.columnWidths != null && length2 < this.columnWidths.length) {
                length2 = this.columnWidths.length;
            }
            if (this.rowHeights != null && length < this.rowHeights.length) {
                length = this.rowHeights.length;
            }
            final GridBagLayoutInfo gridBagLayoutInfo = new GridBagLayoutInfo(length2, length);
            int n8;
            int n7 = n8 = -1;
            Arrays.fill(array, 0);
            Arrays.fill(array2, 0);
            int[] array3 = null;
            int[] array4 = null;
            short[] array5 = null;
            if (b) {
                array3 = (gridBagLayoutInfo.maxAscent = new int[length]);
                array4 = (gridBagLayoutInfo.maxDescent = new int[length]);
                array5 = (gridBagLayoutInfo.baselineType = new short[length]);
                gridBagLayoutInfo.hasBaseline = true;
            }
            for (int n9 = 0; n9 < components.length; ++n9) {
                final Component component2 = components[n9];
                if (component2.isVisible()) {
                    final GridBagConstraints lookupConstraints2 = this.lookupConstraints(component2);
                    int gridx2 = lookupConstraints2.gridx;
                    int gridy2 = lookupConstraints2.gridy;
                    int gridwidth2 = lookupConstraints2.gridwidth;
                    int gridheight2 = lookupConstraints2.gridheight;
                    if (gridx2 < 0 && gridy2 < 0) {
                        if (n8 >= 0) {
                            gridy2 = n8;
                        }
                        else if (n7 >= 0) {
                            gridx2 = n7;
                        }
                        else {
                            gridy2 = 0;
                        }
                    }
                    if (gridx2 < 0) {
                        if (gridheight2 <= 0) {
                            gridheight2 += gridBagLayoutInfo.height - gridy2;
                            if (gridheight2 < 1) {
                                gridheight2 = 1;
                            }
                        }
                        int max5 = 0;
                        for (int n10 = gridy2; n10 < gridy2 + gridheight2; ++n10) {
                            max5 = Math.max(max5, array[n10]);
                        }
                        gridx2 = max5 - gridx2 - 1;
                        if (gridx2 < 0) {
                            gridx2 = 0;
                        }
                    }
                    else if (gridy2 < 0) {
                        if (gridwidth2 <= 0) {
                            gridwidth2 += gridBagLayoutInfo.width - gridx2;
                            if (gridwidth2 < 1) {
                                gridwidth2 = 1;
                            }
                        }
                        int max6 = 0;
                        for (int n11 = gridx2; n11 < gridx2 + gridwidth2; ++n11) {
                            max6 = Math.max(max6, array2[n11]);
                        }
                        gridy2 = max6 - gridy2 - 1;
                        if (gridy2 < 0) {
                            gridy2 = 0;
                        }
                    }
                    if (gridwidth2 <= 0) {
                        gridwidth2 += gridBagLayoutInfo.width - gridx2;
                        if (gridwidth2 < 1) {
                            gridwidth2 = 1;
                        }
                    }
                    if (gridheight2 <= 0) {
                        gridheight2 += gridBagLayoutInfo.height - gridy2;
                        if (gridheight2 < 1) {
                            gridheight2 = 1;
                        }
                    }
                    final int n12 = gridx2 + gridwidth2;
                    final int n13 = gridy2 + gridheight2;
                    for (int n14 = gridx2; n14 < gridx2 + gridwidth2; ++n14) {
                        array2[n14] = n13;
                    }
                    for (int n15 = gridy2; n15 < gridy2 + gridheight2; ++n15) {
                        array[n15] = n12;
                    }
                    if (lookupConstraints2.gridheight == 0 && lookupConstraints2.gridwidth == 0) {
                        n7 = (n8 = -1);
                    }
                    if (lookupConstraints2.gridheight == 0 && n8 < 0) {
                        n7 = gridx2 + gridwidth2;
                    }
                    else if (lookupConstraints2.gridwidth == 0 && n7 < 0) {
                        n8 = gridy2 + gridheight2;
                    }
                    lookupConstraints2.tempX = gridx2;
                    lookupConstraints2.tempY = gridy2;
                    lookupConstraints2.tempWidth = gridwidth2;
                    lookupConstraints2.tempHeight = gridheight2;
                    final int anchor = lookupConstraints2.anchor;
                    if (b) {
                        switch (anchor) {
                            case 256:
                            case 512:
                            case 768: {
                                if (lookupConstraints2.ascent < 0) {
                                    break;
                                }
                                if (gridheight2 == 1) {
                                    array3[gridy2] = Math.max(array3[gridy2], lookupConstraints2.ascent);
                                    array4[gridy2] = Math.max(array4[gridy2], lookupConstraints2.descent);
                                }
                                else if (lookupConstraints2.baselineResizeBehavior == Component.BaselineResizeBehavior.CONSTANT_DESCENT) {
                                    array4[gridy2 + gridheight2 - 1] = Math.max(array4[gridy2 + gridheight2 - 1], lookupConstraints2.descent);
                                }
                                else {
                                    array3[gridy2] = Math.max(array3[gridy2], lookupConstraints2.ascent);
                                }
                                if (lookupConstraints2.baselineResizeBehavior == Component.BaselineResizeBehavior.CONSTANT_DESCENT) {
                                    final short[] array6 = array5;
                                    final int n16 = gridy2 + gridheight2 - 1;
                                    array6[n16] |= (short)(1 << lookupConstraints2.baselineResizeBehavior.ordinal());
                                    break;
                                }
                                final short[] array7 = array5;
                                final int n17 = gridy2;
                                array7[n17] |= (short)(1 << lookupConstraints2.baselineResizeBehavior.ordinal());
                                break;
                            }
                            case 1024:
                            case 1280:
                            case 1536: {
                                array3[gridy2] = Math.max(array3[gridy2], lookupConstraints2.minHeight + lookupConstraints2.insets.top + lookupConstraints2.ipady);
                                array4[gridy2] = Math.max(array4[gridy2], lookupConstraints2.insets.bottom);
                                break;
                            }
                            case 1792:
                            case 2048:
                            case 2304: {
                                array4[gridy2] = Math.max(array4[gridy2], lookupConstraints2.minHeight + lookupConstraints2.insets.bottom + lookupConstraints2.ipady);
                                array3[gridy2] = Math.max(array3[gridy2], lookupConstraints2.insets.top);
                                break;
                            }
                        }
                    }
                }
            }
            gridBagLayoutInfo.weightX = new double[max2];
            gridBagLayoutInfo.weightY = new double[max];
            gridBagLayoutInfo.minWidth = new int[max2];
            gridBagLayoutInfo.minHeight = new int[max];
            if (this.columnWidths != null) {
                System.arraycopy(this.columnWidths, 0, gridBagLayoutInfo.minWidth, 0, this.columnWidths.length);
            }
            if (this.rowHeights != null) {
                System.arraycopy(this.rowHeights, 0, gridBagLayoutInfo.minHeight, 0, this.rowHeights.length);
            }
            if (this.columnWeights != null) {
                System.arraycopy(this.columnWeights, 0, gridBagLayoutInfo.weightX, 0, Math.min(gridBagLayoutInfo.weightX.length, this.columnWeights.length));
            }
            if (this.rowWeights != null) {
                System.arraycopy(this.rowWeights, 0, gridBagLayoutInfo.weightY, 0, Math.min(gridBagLayoutInfo.weightY.length, this.rowWeights.length));
            }
            for (int n18 = Integer.MAX_VALUE, n19 = 1; n19 != Integer.MAX_VALUE; n19 = n18, n18 = Integer.MAX_VALUE) {
                for (int n20 = 0; n20 < components.length; ++n20) {
                    final Component component3 = components[n20];
                    if (component3.isVisible()) {
                        final GridBagConstraints lookupConstraints3 = this.lookupConstraints(component3);
                        if (lookupConstraints3.tempWidth == n19) {
                            final int n21 = lookupConstraints3.tempX + lookupConstraints3.tempWidth;
                            double weightx = lookupConstraints3.weightx;
                            for (int tempX = lookupConstraints3.tempX; tempX < n21; ++tempX) {
                                weightx -= gridBagLayoutInfo.weightX[tempX];
                            }
                            if (weightx > 0.0) {
                                double n22 = 0.0;
                                for (int tempX2 = lookupConstraints3.tempX; tempX2 < n21; ++tempX2) {
                                    n22 += gridBagLayoutInfo.weightX[tempX2];
                                }
                                double n23;
                                for (int tempX3 = lookupConstraints3.tempX; n22 > 0.0 && tempX3 < n21; n22 -= n23, ++tempX3) {
                                    n23 = gridBagLayoutInfo.weightX[tempX3];
                                    final double n24 = n23 * weightx / n22;
                                    final double[] weightX = gridBagLayoutInfo.weightX;
                                    final int n25 = tempX3;
                                    weightX[n25] += n24;
                                    weightx -= n24;
                                }
                                final double[] weightX2 = gridBagLayoutInfo.weightX;
                                final int n26 = n21 - 1;
                                weightX2[n26] += weightx;
                            }
                            int n27 = lookupConstraints3.minWidth + lookupConstraints3.ipadx + lookupConstraints3.insets.left + lookupConstraints3.insets.right;
                            for (int tempX4 = lookupConstraints3.tempX; tempX4 < n21; ++tempX4) {
                                n27 -= gridBagLayoutInfo.minWidth[tempX4];
                            }
                            if (n27 > 0) {
                                double n28 = 0.0;
                                for (int tempX5 = lookupConstraints3.tempX; tempX5 < n21; ++tempX5) {
                                    n28 += gridBagLayoutInfo.weightX[tempX5];
                                }
                                double n29;
                                for (int tempX6 = lookupConstraints3.tempX; n28 > 0.0 && tempX6 < n21; n28 -= n29, ++tempX6) {
                                    n29 = gridBagLayoutInfo.weightX[tempX6];
                                    final int n30 = (int)(n29 * n27 / n28);
                                    final int[] minWidth = gridBagLayoutInfo.minWidth;
                                    final int n31 = tempX6;
                                    minWidth[n31] += n30;
                                    n27 -= n30;
                                }
                                final int[] minWidth2 = gridBagLayoutInfo.minWidth;
                                final int n32 = n21 - 1;
                                minWidth2[n32] += n27;
                            }
                        }
                        else if (lookupConstraints3.tempWidth > n19 && lookupConstraints3.tempWidth < n18) {
                            n18 = lookupConstraints3.tempWidth;
                        }
                        if (lookupConstraints3.tempHeight == n19) {
                            final int n33 = lookupConstraints3.tempY + lookupConstraints3.tempHeight;
                            double weighty = lookupConstraints3.weighty;
                            for (int tempY = lookupConstraints3.tempY; tempY < n33; ++tempY) {
                                weighty -= gridBagLayoutInfo.weightY[tempY];
                            }
                            if (weighty > 0.0) {
                                double n34 = 0.0;
                                for (int tempY2 = lookupConstraints3.tempY; tempY2 < n33; ++tempY2) {
                                    n34 += gridBagLayoutInfo.weightY[tempY2];
                                }
                                double n35;
                                for (int tempY3 = lookupConstraints3.tempY; n34 > 0.0 && tempY3 < n33; n34 -= n35, ++tempY3) {
                                    n35 = gridBagLayoutInfo.weightY[tempY3];
                                    final double n36 = n35 * weighty / n34;
                                    final double[] weightY = gridBagLayoutInfo.weightY;
                                    final int n37 = tempY3;
                                    weightY[n37] += n36;
                                    weighty -= n36;
                                }
                                final double[] weightY2 = gridBagLayoutInfo.weightY;
                                final int n38 = n33 - 1;
                                weightY2[n38] += weighty;
                            }
                            int n39 = -1;
                            if (b) {
                                switch (lookupConstraints3.anchor) {
                                    case 256:
                                    case 512:
                                    case 768: {
                                        if (lookupConstraints3.ascent < 0) {
                                            break;
                                        }
                                        if (lookupConstraints3.tempHeight == 1) {
                                            n39 = array3[lookupConstraints3.tempY] + array4[lookupConstraints3.tempY];
                                            break;
                                        }
                                        if (lookupConstraints3.baselineResizeBehavior != Component.BaselineResizeBehavior.CONSTANT_DESCENT) {
                                            n39 = array3[lookupConstraints3.tempY] + lookupConstraints3.descent;
                                            break;
                                        }
                                        n39 = lookupConstraints3.ascent + array4[lookupConstraints3.tempY + lookupConstraints3.tempHeight - 1];
                                        break;
                                    }
                                    case 1024:
                                    case 1280:
                                    case 1536: {
                                        n39 = lookupConstraints3.insets.top + lookupConstraints3.minHeight + lookupConstraints3.ipady + array4[lookupConstraints3.tempY];
                                        break;
                                    }
                                    case 1792:
                                    case 2048:
                                    case 2304: {
                                        n39 = array3[lookupConstraints3.tempY] + lookupConstraints3.minHeight + lookupConstraints3.insets.bottom + lookupConstraints3.ipady;
                                        break;
                                    }
                                }
                            }
                            if (n39 == -1) {
                                n39 = lookupConstraints3.minHeight + lookupConstraints3.ipady + lookupConstraints3.insets.top + lookupConstraints3.insets.bottom;
                            }
                            for (int tempY4 = lookupConstraints3.tempY; tempY4 < n33; ++tempY4) {
                                n39 -= gridBagLayoutInfo.minHeight[tempY4];
                            }
                            if (n39 > 0) {
                                double n40 = 0.0;
                                for (int tempY5 = lookupConstraints3.tempY; tempY5 < n33; ++tempY5) {
                                    n40 += gridBagLayoutInfo.weightY[tempY5];
                                }
                                double n41;
                                for (int tempY6 = lookupConstraints3.tempY; n40 > 0.0 && tempY6 < n33; n40 -= n41, ++tempY6) {
                                    n41 = gridBagLayoutInfo.weightY[tempY6];
                                    final int n42 = (int)(n41 * n39 / n40);
                                    final int[] minHeight = gridBagLayoutInfo.minHeight;
                                    final int n43 = tempY6;
                                    minHeight[n43] += n42;
                                    n39 -= n42;
                                }
                                final int[] minHeight2 = gridBagLayoutInfo.minHeight;
                                final int n44 = n33 - 1;
                                minHeight2[n44] += n39;
                            }
                        }
                        else if (lookupConstraints3.tempHeight > n19 && lookupConstraints3.tempHeight < n18) {
                            n18 = lookupConstraints3.tempHeight;
                        }
                    }
                }
            }
            return gridBagLayoutInfo;
        }
    }
    
    private boolean calculateBaseline(final Component component, final GridBagConstraints gridBagConstraints, final Dimension dimension) {
        final int anchor = gridBagConstraints.anchor;
        if (anchor == 256 || anchor == 512 || anchor == 768) {
            final int n = dimension.width + gridBagConstraints.ipadx;
            final int n2 = dimension.height + gridBagConstraints.ipady;
            gridBagConstraints.ascent = component.getBaseline(n, n2);
            if (gridBagConstraints.ascent >= 0) {
                final int ascent = gridBagConstraints.ascent;
                gridBagConstraints.descent = n2 - gridBagConstraints.ascent + gridBagConstraints.insets.bottom;
                gridBagConstraints.ascent += gridBagConstraints.insets.top;
                gridBagConstraints.baselineResizeBehavior = component.getBaselineResizeBehavior();
                gridBagConstraints.centerPadding = 0;
                if (gridBagConstraints.baselineResizeBehavior == Component.BaselineResizeBehavior.CENTER_OFFSET) {
                    final int baseline = component.getBaseline(n, n2 + 1);
                    gridBagConstraints.centerOffset = ascent - n2 / 2;
                    if (n2 % 2 == 0) {
                        if (ascent != baseline) {
                            gridBagConstraints.centerPadding = 1;
                        }
                    }
                    else if (ascent == baseline) {
                        --gridBagConstraints.centerOffset;
                        gridBagConstraints.centerPadding = 1;
                    }
                }
            }
            return true;
        }
        gridBagConstraints.ascent = -1;
        return false;
    }
    
    protected void adjustForGravity(final GridBagConstraints gridBagConstraints, final Rectangle rectangle) {
        this.AdjustForGravity(gridBagConstraints, rectangle);
    }
    
    protected void AdjustForGravity(final GridBagConstraints gridBagConstraints, final Rectangle rectangle) {
        final int y = rectangle.y;
        final int height = rectangle.height;
        if (!this.rightToLeft) {
            rectangle.x += gridBagConstraints.insets.left;
        }
        else {
            rectangle.x -= rectangle.width - gridBagConstraints.insets.right;
        }
        rectangle.width -= gridBagConstraints.insets.left + gridBagConstraints.insets.right;
        rectangle.y += gridBagConstraints.insets.top;
        rectangle.height -= gridBagConstraints.insets.top + gridBagConstraints.insets.bottom;
        int n = 0;
        if (gridBagConstraints.fill != 2 && gridBagConstraints.fill != 1 && rectangle.width > gridBagConstraints.minWidth + gridBagConstraints.ipadx) {
            n = rectangle.width - (gridBagConstraints.minWidth + gridBagConstraints.ipadx);
            rectangle.width = gridBagConstraints.minWidth + gridBagConstraints.ipadx;
        }
        int n2 = 0;
        if (gridBagConstraints.fill != 3 && gridBagConstraints.fill != 1 && rectangle.height > gridBagConstraints.minHeight + gridBagConstraints.ipady) {
            n2 = rectangle.height - (gridBagConstraints.minHeight + gridBagConstraints.ipady);
            rectangle.height = gridBagConstraints.minHeight + gridBagConstraints.ipady;
        }
        switch (gridBagConstraints.anchor) {
            case 256: {
                rectangle.x += n / 2;
                this.alignOnBaseline(gridBagConstraints, rectangle, y, height);
                break;
            }
            case 512: {
                if (this.rightToLeft) {
                    rectangle.x += n;
                }
                this.alignOnBaseline(gridBagConstraints, rectangle, y, height);
                break;
            }
            case 768: {
                if (!this.rightToLeft) {
                    rectangle.x += n;
                }
                this.alignOnBaseline(gridBagConstraints, rectangle, y, height);
                break;
            }
            case 1024: {
                rectangle.x += n / 2;
                this.alignAboveBaseline(gridBagConstraints, rectangle, y, height);
                break;
            }
            case 1280: {
                if (this.rightToLeft) {
                    rectangle.x += n;
                }
                this.alignAboveBaseline(gridBagConstraints, rectangle, y, height);
                break;
            }
            case 1536: {
                if (!this.rightToLeft) {
                    rectangle.x += n;
                }
                this.alignAboveBaseline(gridBagConstraints, rectangle, y, height);
                break;
            }
            case 1792: {
                rectangle.x += n / 2;
                this.alignBelowBaseline(gridBagConstraints, rectangle, y, height);
                break;
            }
            case 2048: {
                if (this.rightToLeft) {
                    rectangle.x += n;
                }
                this.alignBelowBaseline(gridBagConstraints, rectangle, y, height);
                break;
            }
            case 2304: {
                if (!this.rightToLeft) {
                    rectangle.x += n;
                }
                this.alignBelowBaseline(gridBagConstraints, rectangle, y, height);
                break;
            }
            case 10: {
                rectangle.x += n / 2;
                rectangle.y += n2 / 2;
                break;
            }
            case 11:
            case 19: {
                rectangle.x += n / 2;
                break;
            }
            case 12: {
                rectangle.x += n;
                break;
            }
            case 13: {
                rectangle.x += n;
                rectangle.y += n2 / 2;
                break;
            }
            case 14: {
                rectangle.x += n;
                rectangle.y += n2;
                break;
            }
            case 15:
            case 20: {
                rectangle.x += n / 2;
                rectangle.y += n2;
                break;
            }
            case 16: {
                rectangle.y += n2;
                break;
            }
            case 17: {
                rectangle.y += n2 / 2;
                break;
            }
            case 18: {
                break;
            }
            case 21: {
                if (this.rightToLeft) {
                    rectangle.x += n;
                }
                rectangle.y += n2 / 2;
                break;
            }
            case 22: {
                if (!this.rightToLeft) {
                    rectangle.x += n;
                }
                rectangle.y += n2 / 2;
                break;
            }
            case 23: {
                if (this.rightToLeft) {
                    rectangle.x += n;
                    break;
                }
                break;
            }
            case 24: {
                if (!this.rightToLeft) {
                    rectangle.x += n;
                    break;
                }
                break;
            }
            case 25: {
                if (this.rightToLeft) {
                    rectangle.x += n;
                }
                rectangle.y += n2;
                break;
            }
            case 26: {
                if (!this.rightToLeft) {
                    rectangle.x += n;
                }
                rectangle.y += n2;
                break;
            }
            default: {
                throw new IllegalArgumentException("illegal anchor value");
            }
        }
    }
    
    private void alignOnBaseline(final GridBagConstraints gridBagConstraints, final Rectangle rectangle, final int n, final int n2) {
        if (gridBagConstraints.ascent >= 0) {
            if (gridBagConstraints.baselineResizeBehavior == Component.BaselineResizeBehavior.CONSTANT_DESCENT) {
                final int n3 = n + n2 - this.layoutInfo.maxDescent[gridBagConstraints.tempY + gridBagConstraints.tempHeight - 1] + gridBagConstraints.descent - gridBagConstraints.insets.bottom;
                if (!gridBagConstraints.isVerticallyResizable()) {
                    rectangle.y = n3 - gridBagConstraints.minHeight;
                    rectangle.height = gridBagConstraints.minHeight;
                }
                else {
                    rectangle.height = n3 - n - gridBagConstraints.insets.top;
                }
            }
            else {
                int n4 = gridBagConstraints.ascent;
                int n5;
                if (this.layoutInfo.hasConstantDescent(gridBagConstraints.tempY)) {
                    n5 = n2 - this.layoutInfo.maxDescent[gridBagConstraints.tempY];
                }
                else {
                    n5 = this.layoutInfo.maxAscent[gridBagConstraints.tempY];
                }
                if (gridBagConstraints.baselineResizeBehavior == Component.BaselineResizeBehavior.OTHER) {
                    boolean b = false;
                    n4 = this.componentAdjusting.getBaseline(rectangle.width, rectangle.height);
                    if (n4 >= 0) {
                        n4 += gridBagConstraints.insets.top;
                    }
                    if (n4 >= 0 && n4 <= n5) {
                        if (n5 + (rectangle.height - n4 - gridBagConstraints.insets.top) <= n2 - gridBagConstraints.insets.bottom) {
                            b = true;
                        }
                        else if (gridBagConstraints.isVerticallyResizable()) {
                            int baseline = this.componentAdjusting.getBaseline(rectangle.width, n2 - gridBagConstraints.insets.bottom - n5 + n4);
                            if (baseline >= 0) {
                                baseline += gridBagConstraints.insets.top;
                            }
                            if (baseline >= 0 && baseline <= n4) {
                                rectangle.height = n2 - gridBagConstraints.insets.bottom - n5 + n4;
                                n4 = baseline;
                                b = true;
                            }
                        }
                    }
                    if (!b) {
                        n4 = gridBagConstraints.ascent;
                        rectangle.width = gridBagConstraints.minWidth;
                        rectangle.height = gridBagConstraints.minHeight;
                    }
                }
                rectangle.y = n + n5 - n4 + gridBagConstraints.insets.top;
                if (gridBagConstraints.isVerticallyResizable()) {
                    switch (gridBagConstraints.baselineResizeBehavior) {
                        case CONSTANT_ASCENT: {
                            rectangle.height = Math.max(gridBagConstraints.minHeight, n + n2 - rectangle.y - gridBagConstraints.insets.bottom);
                            break;
                        }
                        case CENTER_OFFSET: {
                            final int min = Math.min(rectangle.y - n - gridBagConstraints.insets.top, n + n2 - rectangle.y - gridBagConstraints.minHeight - gridBagConstraints.insets.bottom);
                            int n6 = min + min;
                            if (n6 > 0 && (gridBagConstraints.minHeight + gridBagConstraints.centerPadding + n6) / 2 + gridBagConstraints.centerOffset != n5) {
                                --n6;
                            }
                            rectangle.height = gridBagConstraints.minHeight + n6;
                            rectangle.y = n + n5 - (rectangle.height + gridBagConstraints.centerPadding) / 2 - gridBagConstraints.centerOffset;
                        }
                    }
                }
            }
        }
        else {
            this.centerVertically(gridBagConstraints, rectangle, n2);
        }
    }
    
    private void alignAboveBaseline(final GridBagConstraints gridBagConstraints, final Rectangle rectangle, final int n, final int n2) {
        if (this.layoutInfo.hasBaseline(gridBagConstraints.tempY)) {
            int n3;
            if (this.layoutInfo.hasConstantDescent(gridBagConstraints.tempY)) {
                n3 = n + n2 - this.layoutInfo.maxDescent[gridBagConstraints.tempY];
            }
            else {
                n3 = n + this.layoutInfo.maxAscent[gridBagConstraints.tempY];
            }
            if (gridBagConstraints.isVerticallyResizable()) {
                rectangle.y = n + gridBagConstraints.insets.top;
                rectangle.height = n3 - rectangle.y;
            }
            else {
                rectangle.height = gridBagConstraints.minHeight + gridBagConstraints.ipady;
                rectangle.y = n3 - rectangle.height;
            }
        }
        else {
            this.centerVertically(gridBagConstraints, rectangle, n2);
        }
    }
    
    private void alignBelowBaseline(final GridBagConstraints gridBagConstraints, final Rectangle rectangle, final int n, final int n2) {
        if (this.layoutInfo.hasBaseline(gridBagConstraints.tempY)) {
            if (this.layoutInfo.hasConstantDescent(gridBagConstraints.tempY)) {
                rectangle.y = n + n2 - this.layoutInfo.maxDescent[gridBagConstraints.tempY];
            }
            else {
                rectangle.y = n + this.layoutInfo.maxAscent[gridBagConstraints.tempY];
            }
            if (gridBagConstraints.isVerticallyResizable()) {
                rectangle.height = n + n2 - rectangle.y - gridBagConstraints.insets.bottom;
            }
        }
        else {
            this.centerVertically(gridBagConstraints, rectangle, n2);
        }
    }
    
    private void centerVertically(final GridBagConstraints gridBagConstraints, final Rectangle rectangle, final int n) {
        if (!gridBagConstraints.isVerticallyResizable()) {
            rectangle.y += Math.max(0, (n - gridBagConstraints.insets.top - gridBagConstraints.insets.bottom - gridBagConstraints.minHeight - gridBagConstraints.ipady) / 2);
        }
    }
    
    protected Dimension getMinSize(final Container container, final GridBagLayoutInfo gridBagLayoutInfo) {
        return this.GetMinSize(container, gridBagLayoutInfo);
    }
    
    protected Dimension GetMinSize(final Container container, final GridBagLayoutInfo gridBagLayoutInfo) {
        final Dimension dimension = new Dimension();
        final Insets insets = container.getInsets();
        int n = 0;
        for (int i = 0; i < gridBagLayoutInfo.width; ++i) {
            n += gridBagLayoutInfo.minWidth[i];
        }
        dimension.width = n + insets.left + insets.right;
        int n2 = 0;
        for (int j = 0; j < gridBagLayoutInfo.height; ++j) {
            n2 += gridBagLayoutInfo.minHeight[j];
        }
        dimension.height = n2 + insets.top + insets.bottom;
        return dimension;
    }
    
    protected void arrangeGrid(final Container container) {
        this.ArrangeGrid(container);
    }
    
    protected void ArrangeGrid(final Container container) {
        final Insets insets = container.getInsets();
        final Component[] components = container.getComponents();
        final Rectangle rectangle = new Rectangle();
        this.rightToLeft = !container.getComponentOrientation().isLeftToRight();
        if (components.length == 0 && (this.columnWidths == null || this.columnWidths.length == 0) && (this.rowHeights == null || this.rowHeights.length == 0)) {
            return;
        }
        GridBagLayoutInfo layoutInfo = this.getLayoutInfo(container, 2);
        Dimension dimension = this.getMinSize(container, layoutInfo);
        if (container.width < dimension.width || container.height < dimension.height) {
            layoutInfo = this.getLayoutInfo(container, 1);
            dimension = this.getMinSize(container, layoutInfo);
        }
        this.layoutInfo = layoutInfo;
        rectangle.width = dimension.width;
        rectangle.height = dimension.height;
        final int n = container.width - rectangle.width;
        int n5;
        if (n != 0) {
            double n2 = 0.0;
            for (int i = 0; i < layoutInfo.width; ++i) {
                n2 += layoutInfo.weightX[i];
            }
            if (n2 > 0.0) {
                for (int j = 0; j < layoutInfo.width; ++j) {
                    final int n3 = (int)(n * layoutInfo.weightX[j] / n2);
                    final int[] minWidth = layoutInfo.minWidth;
                    final int n4 = j;
                    minWidth[n4] += n3;
                    final Rectangle rectangle2 = rectangle;
                    rectangle2.width += n3;
                    if (layoutInfo.minWidth[j] < 0) {
                        final Rectangle rectangle3 = rectangle;
                        rectangle3.width -= layoutInfo.minWidth[j];
                        layoutInfo.minWidth[j] = 0;
                    }
                }
            }
            n5 = container.width - rectangle.width;
        }
        else {
            n5 = 0;
        }
        final int n6 = container.height - rectangle.height;
        int n10;
        if (n6 != 0) {
            double n7 = 0.0;
            for (int k = 0; k < layoutInfo.height; ++k) {
                n7 += layoutInfo.weightY[k];
            }
            if (n7 > 0.0) {
                for (int l = 0; l < layoutInfo.height; ++l) {
                    final int n8 = (int)(n6 * layoutInfo.weightY[l] / n7);
                    final int[] minHeight = layoutInfo.minHeight;
                    final int n9 = l;
                    minHeight[n9] += n8;
                    final Rectangle rectangle4 = rectangle;
                    rectangle4.height += n8;
                    if (layoutInfo.minHeight[l] < 0) {
                        final Rectangle rectangle5 = rectangle;
                        rectangle5.height -= layoutInfo.minHeight[l];
                        layoutInfo.minHeight[l] = 0;
                    }
                }
            }
            n10 = container.height - rectangle.height;
        }
        else {
            n10 = 0;
        }
        layoutInfo.startx = n5 / 2 + insets.left;
        layoutInfo.starty = n10 / 2 + insets.top;
        for (int n11 = 0; n11 < components.length; ++n11) {
            final Component componentAdjusting = components[n11];
            if (componentAdjusting.isVisible()) {
                final GridBagConstraints lookupConstraints = this.lookupConstraints(componentAdjusting);
                if (!this.rightToLeft) {
                    rectangle.x = layoutInfo.startx;
                    for (int n12 = 0; n12 < lookupConstraints.tempX; ++n12) {
                        final Rectangle rectangle6 = rectangle;
                        rectangle6.x += layoutInfo.minWidth[n12];
                    }
                }
                else {
                    rectangle.x = container.width - (n5 / 2 + insets.right);
                    for (int n13 = 0; n13 < lookupConstraints.tempX; ++n13) {
                        final Rectangle rectangle7 = rectangle;
                        rectangle7.x -= layoutInfo.minWidth[n13];
                    }
                }
                rectangle.y = layoutInfo.starty;
                for (int n14 = 0; n14 < lookupConstraints.tempY; ++n14) {
                    final Rectangle rectangle8 = rectangle;
                    rectangle8.y += layoutInfo.minHeight[n14];
                }
                rectangle.width = 0;
                for (int tempX = lookupConstraints.tempX; tempX < lookupConstraints.tempX + lookupConstraints.tempWidth; ++tempX) {
                    final Rectangle rectangle9 = rectangle;
                    rectangle9.width += layoutInfo.minWidth[tempX];
                }
                rectangle.height = 0;
                for (int tempY = lookupConstraints.tempY; tempY < lookupConstraints.tempY + lookupConstraints.tempHeight; ++tempY) {
                    final Rectangle rectangle10 = rectangle;
                    rectangle10.height += layoutInfo.minHeight[tempY];
                }
                this.componentAdjusting = componentAdjusting;
                this.adjustForGravity(lookupConstraints, rectangle);
                if (rectangle.x < 0) {
                    final Rectangle rectangle11 = rectangle;
                    rectangle11.width += rectangle.x;
                    rectangle.x = 0;
                }
                if (rectangle.y < 0) {
                    final Rectangle rectangle12 = rectangle;
                    rectangle12.height += rectangle.y;
                    rectangle.y = 0;
                }
                if (rectangle.width <= 0 || rectangle.height <= 0) {
                    componentAdjusting.setBounds(0, 0, 0, 0);
                }
                else if (componentAdjusting.x != rectangle.x || componentAdjusting.y != rectangle.y || componentAdjusting.width != rectangle.width || componentAdjusting.height != rectangle.height) {
                    componentAdjusting.setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                }
            }
        }
    }
}
