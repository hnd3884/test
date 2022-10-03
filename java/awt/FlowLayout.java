package java.awt;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class FlowLayout implements LayoutManager, Serializable
{
    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;
    public static final int LEADING = 3;
    public static final int TRAILING = 4;
    int align;
    int newAlign;
    int hgap;
    int vgap;
    private boolean alignOnBaseline;
    private static final long serialVersionUID = -7262534875583282631L;
    private static final int currentSerialVersion = 1;
    private int serialVersionOnStream;
    
    public FlowLayout() {
        this(1, 5, 5);
    }
    
    public FlowLayout(final int n) {
        this(n, 5, 5);
    }
    
    public FlowLayout(final int alignment, final int hgap, final int vgap) {
        this.serialVersionOnStream = 1;
        this.hgap = hgap;
        this.vgap = vgap;
        this.setAlignment(alignment);
    }
    
    public int getAlignment() {
        return this.newAlign;
    }
    
    public void setAlignment(final int n) {
        switch (this.newAlign = n) {
            case 3: {
                this.align = 0;
                break;
            }
            case 4: {
                this.align = 2;
                break;
            }
            default: {
                this.align = n;
                break;
            }
        }
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
    
    public void setAlignOnBaseline(final boolean alignOnBaseline) {
        this.alignOnBaseline = alignOnBaseline;
    }
    
    public boolean getAlignOnBaseline() {
        return this.alignOnBaseline;
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
            final Dimension dimension = new Dimension(0, 0);
            final int componentCount = container.getComponentCount();
            int n = 1;
            final boolean alignOnBaseline = this.getAlignOnBaseline();
            int max = 0;
            int max2 = 0;
            for (int i = 0; i < componentCount; ++i) {
                final Component component = container.getComponent(i);
                if (component.isVisible()) {
                    final Dimension preferredSize = component.getPreferredSize();
                    dimension.height = Math.max(dimension.height, preferredSize.height);
                    if (n != 0) {
                        n = 0;
                    }
                    else {
                        final Dimension dimension2 = dimension;
                        dimension2.width += this.hgap;
                    }
                    final Dimension dimension3 = dimension;
                    dimension3.width += preferredSize.width;
                    if (alignOnBaseline) {
                        final int baseline = component.getBaseline(preferredSize.width, preferredSize.height);
                        if (baseline >= 0) {
                            max = Math.max(max, baseline);
                            max2 = Math.max(max2, preferredSize.height - baseline);
                        }
                    }
                }
            }
            if (alignOnBaseline) {
                dimension.height = Math.max(max + max2, dimension.height);
            }
            final Insets insets = container.getInsets();
            final Dimension dimension4 = dimension;
            dimension4.width += insets.left + insets.right + this.hgap * 2;
            final Dimension dimension5 = dimension;
            dimension5.height += insets.top + insets.bottom + this.vgap * 2;
            return dimension;
        }
    }
    
    @Override
    public Dimension minimumLayoutSize(final Container container) {
        synchronized (container.getTreeLock()) {
            final boolean alignOnBaseline = this.getAlignOnBaseline();
            final Dimension dimension = new Dimension(0, 0);
            final int componentCount = container.getComponentCount();
            int max = 0;
            int max2 = 0;
            int n = 1;
            for (int i = 0; i < componentCount; ++i) {
                final Component component = container.getComponent(i);
                if (component.visible) {
                    final Dimension minimumSize = component.getMinimumSize();
                    dimension.height = Math.max(dimension.height, minimumSize.height);
                    if (n != 0) {
                        n = 0;
                    }
                    else {
                        final Dimension dimension2 = dimension;
                        dimension2.width += this.hgap;
                    }
                    final Dimension dimension3 = dimension;
                    dimension3.width += minimumSize.width;
                    if (alignOnBaseline) {
                        final int baseline = component.getBaseline(minimumSize.width, minimumSize.height);
                        if (baseline >= 0) {
                            max = Math.max(max, baseline);
                            max2 = Math.max(max2, dimension.height - baseline);
                        }
                    }
                }
            }
            if (alignOnBaseline) {
                dimension.height = Math.max(max + max2, dimension.height);
            }
            final Insets insets = container.getInsets();
            final Dimension dimension4 = dimension;
            dimension4.width += insets.left + insets.right + this.hgap * 2;
            final Dimension dimension5 = dimension;
            dimension5.height += insets.top + insets.bottom + this.vgap * 2;
            return dimension;
        }
    }
    
    private int moveComponents(final Container container, int n, final int n2, final int n3, int max, final int n4, final int n5, final boolean b, final boolean b2, final int[] array, final int[] array2) {
        switch (this.newAlign) {
            case 0: {
                n += (b ? 0 : n3);
                break;
            }
            case 1: {
                n += n3 / 2;
                break;
            }
            case 2: {
                n += (b ? n3 : 0);
            }
            case 4: {
                n += n3;
                break;
            }
        }
        int max2 = 0;
        int max3 = 0;
        int n6 = 0;
        if (b2) {
            int max4 = 0;
            for (int i = n4; i < n5; ++i) {
                final Component component = container.getComponent(i);
                if (component.visible) {
                    if (array[i] >= 0) {
                        max2 = Math.max(max2, array[i]);
                        max4 = Math.max(max4, array2[i]);
                    }
                    else {
                        max3 = Math.max(component.getHeight(), max3);
                    }
                }
            }
            max = Math.max(max2 + max4, max3);
            n6 = (max - max2 - max4) / 2;
        }
        for (int j = n4; j < n5; ++j) {
            final Component component2 = container.getComponent(j);
            if (component2.isVisible()) {
                int n7;
                if (b2 && array[j] >= 0) {
                    n7 = n2 + n6 + max2 - array[j];
                }
                else {
                    n7 = n2 + (max - component2.height) / 2;
                }
                if (b) {
                    component2.setLocation(n, n7);
                }
                else {
                    component2.setLocation(container.width - n - component2.width, n7);
                }
                n += component2.width + this.hgap;
            }
        }
        return max;
    }
    
    @Override
    public void layoutContainer(final Container container) {
        synchronized (container.getTreeLock()) {
            final Insets insets = container.getInsets();
            final int n = container.width - (insets.left + insets.right + this.hgap * 2);
            final int componentCount = container.getComponentCount();
            int width = 0;
            int n2 = insets.top + this.vgap;
            int n3 = 0;
            int n4 = 0;
            final boolean leftToRight = container.getComponentOrientation().isLeftToRight();
            final boolean alignOnBaseline = this.getAlignOnBaseline();
            int[] array = null;
            int[] array2 = null;
            if (alignOnBaseline) {
                array = new int[componentCount];
                array2 = new int[componentCount];
            }
            for (int i = 0; i < componentCount; ++i) {
                final Component component = container.getComponent(i);
                if (component.isVisible()) {
                    final Dimension preferredSize = component.getPreferredSize();
                    component.setSize(preferredSize.width, preferredSize.height);
                    if (alignOnBaseline) {
                        final int baseline = component.getBaseline(preferredSize.width, preferredSize.height);
                        if (baseline >= 0) {
                            array[i] = baseline;
                            array2[i] = preferredSize.height - baseline;
                        }
                        else {
                            array[i] = -1;
                        }
                    }
                    if (width == 0 || width + preferredSize.width <= n) {
                        if (width > 0) {
                            width += this.hgap;
                        }
                        width += preferredSize.width;
                        n3 = Math.max(n3, preferredSize.height);
                    }
                    else {
                        final int moveComponents = this.moveComponents(container, insets.left + this.hgap, n2, n - width, n3, n4, i, leftToRight, alignOnBaseline, array, array2);
                        width = preferredSize.width;
                        n2 += this.vgap + moveComponents;
                        n3 = preferredSize.height;
                        n4 = i;
                    }
                }
            }
            this.moveComponents(container, insets.left + this.hgap, n2, n - width, n3, n4, componentCount, leftToRight, alignOnBaseline, array, array2);
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        if (this.serialVersionOnStream < 1) {
            this.setAlignment(this.align);
        }
        this.serialVersionOnStream = 1;
    }
    
    @Override
    public String toString() {
        String s = "";
        switch (this.align) {
            case 0: {
                s = ",align=left";
                break;
            }
            case 1: {
                s = ",align=center";
                break;
            }
            case 2: {
                s = ",align=right";
                break;
            }
            case 3: {
                s = ",align=leading";
                break;
            }
            case 4: {
                s = ",align=trailing";
                break;
            }
        }
        return this.getClass().getName() + "[hgap=" + this.hgap + ",vgap=" + this.vgap + s + "]";
    }
}
