package java.awt;

import java.io.Serializable;

public class BorderLayout implements LayoutManager2, Serializable
{
    int hgap;
    int vgap;
    Component north;
    Component west;
    Component east;
    Component south;
    Component center;
    Component firstLine;
    Component lastLine;
    Component firstItem;
    Component lastItem;
    public static final String NORTH = "North";
    public static final String SOUTH = "South";
    public static final String EAST = "East";
    public static final String WEST = "West";
    public static final String CENTER = "Center";
    public static final String BEFORE_FIRST_LINE = "First";
    public static final String AFTER_LAST_LINE = "Last";
    public static final String BEFORE_LINE_BEGINS = "Before";
    public static final String AFTER_LINE_ENDS = "After";
    public static final String PAGE_START = "First";
    public static final String PAGE_END = "Last";
    public static final String LINE_START = "Before";
    public static final String LINE_END = "After";
    private static final long serialVersionUID = -8658291919501921765L;
    
    public BorderLayout() {
        this(0, 0);
    }
    
    public BorderLayout(final int hgap, final int vgap) {
        this.hgap = hgap;
        this.vgap = vgap;
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
    public void addLayoutComponent(final Component component, final Object o) {
        synchronized (component.getTreeLock()) {
            if (o != null && !(o instanceof String)) {
                throw new IllegalArgumentException("cannot add to layout: constraint must be a string (or null)");
            }
            this.addLayoutComponent((String)o, component);
        }
    }
    
    @Deprecated
    @Override
    public void addLayoutComponent(String s, final Component lastItem) {
        synchronized (lastItem.getTreeLock()) {
            if (s == null) {
                s = "Center";
            }
            if ("Center".equals(s)) {
                this.center = lastItem;
            }
            else if ("North".equals(s)) {
                this.north = lastItem;
            }
            else if ("South".equals(s)) {
                this.south = lastItem;
            }
            else if ("East".equals(s)) {
                this.east = lastItem;
            }
            else if ("West".equals(s)) {
                this.west = lastItem;
            }
            else if ("First".equals(s)) {
                this.firstLine = lastItem;
            }
            else if ("Last".equals(s)) {
                this.lastLine = lastItem;
            }
            else if ("Before".equals(s)) {
                this.firstItem = lastItem;
            }
            else {
                if (!"After".equals(s)) {
                    throw new IllegalArgumentException("cannot add to layout: unknown constraint: " + s);
                }
                this.lastItem = lastItem;
            }
        }
    }
    
    @Override
    public void removeLayoutComponent(final Component component) {
        synchronized (component.getTreeLock()) {
            if (component == this.center) {
                this.center = null;
            }
            else if (component == this.north) {
                this.north = null;
            }
            else if (component == this.south) {
                this.south = null;
            }
            else if (component == this.east) {
                this.east = null;
            }
            else if (component == this.west) {
                this.west = null;
            }
            if (component == this.firstLine) {
                this.firstLine = null;
            }
            else if (component == this.lastLine) {
                this.lastLine = null;
            }
            else if (component == this.firstItem) {
                this.firstItem = null;
            }
            else if (component == this.lastItem) {
                this.lastItem = null;
            }
        }
    }
    
    public Component getLayoutComponent(final Object o) {
        if ("Center".equals(o)) {
            return this.center;
        }
        if ("North".equals(o)) {
            return this.north;
        }
        if ("South".equals(o)) {
            return this.south;
        }
        if ("West".equals(o)) {
            return this.west;
        }
        if ("East".equals(o)) {
            return this.east;
        }
        if ("First".equals(o)) {
            return this.firstLine;
        }
        if ("Last".equals(o)) {
            return this.lastLine;
        }
        if ("Before".equals(o)) {
            return this.firstItem;
        }
        if ("After".equals(o)) {
            return this.lastItem;
        }
        throw new IllegalArgumentException("cannot get component: unknown constraint: " + o);
    }
    
    public Component getLayoutComponent(final Container container, final Object o) {
        final boolean leftToRight = container.getComponentOrientation().isLeftToRight();
        Component component;
        if ("North".equals(o)) {
            component = ((this.firstLine != null) ? this.firstLine : this.north);
        }
        else if ("South".equals(o)) {
            component = ((this.lastLine != null) ? this.lastLine : this.south);
        }
        else if ("West".equals(o)) {
            component = (leftToRight ? this.firstItem : this.lastItem);
            if (component == null) {
                component = this.west;
            }
        }
        else if ("East".equals(o)) {
            component = (leftToRight ? this.lastItem : this.firstItem);
            if (component == null) {
                component = this.east;
            }
        }
        else {
            if (!"Center".equals(o)) {
                throw new IllegalArgumentException("cannot get component: invalid constraint: " + o);
            }
            component = this.center;
        }
        return component;
    }
    
    public Object getConstraints(final Component component) {
        if (component == null) {
            return null;
        }
        if (component == this.center) {
            return "Center";
        }
        if (component == this.north) {
            return "North";
        }
        if (component == this.south) {
            return "South";
        }
        if (component == this.west) {
            return "West";
        }
        if (component == this.east) {
            return "East";
        }
        if (component == this.firstLine) {
            return "First";
        }
        if (component == this.lastLine) {
            return "Last";
        }
        if (component == this.firstItem) {
            return "Before";
        }
        if (component == this.lastItem) {
            return "After";
        }
        return null;
    }
    
    @Override
    public Dimension minimumLayoutSize(final Container container) {
        synchronized (container.getTreeLock()) {
            final Dimension dimension = new Dimension(0, 0);
            final boolean leftToRight = container.getComponentOrientation().isLeftToRight();
            final Component child;
            if ((child = this.getChild("East", leftToRight)) != null) {
                final Dimension minimumSize = child.getMinimumSize();
                final Dimension dimension2 = dimension;
                dimension2.width += minimumSize.width + this.hgap;
                dimension.height = Math.max(minimumSize.height, dimension.height);
            }
            final Component child2;
            if ((child2 = this.getChild("West", leftToRight)) != null) {
                final Dimension minimumSize2 = child2.getMinimumSize();
                final Dimension dimension3 = dimension;
                dimension3.width += minimumSize2.width + this.hgap;
                dimension.height = Math.max(minimumSize2.height, dimension.height);
            }
            final Component child3;
            if ((child3 = this.getChild("Center", leftToRight)) != null) {
                final Dimension minimumSize3 = child3.getMinimumSize();
                final Dimension dimension4 = dimension;
                dimension4.width += minimumSize3.width;
                dimension.height = Math.max(minimumSize3.height, dimension.height);
            }
            final Component child4;
            if ((child4 = this.getChild("North", leftToRight)) != null) {
                final Dimension minimumSize4 = child4.getMinimumSize();
                dimension.width = Math.max(minimumSize4.width, dimension.width);
                final Dimension dimension5 = dimension;
                dimension5.height += minimumSize4.height + this.vgap;
            }
            final Component child5;
            if ((child5 = this.getChild("South", leftToRight)) != null) {
                final Dimension minimumSize5 = child5.getMinimumSize();
                dimension.width = Math.max(minimumSize5.width, dimension.width);
                final Dimension dimension6 = dimension;
                dimension6.height += minimumSize5.height + this.vgap;
            }
            final Insets insets = container.getInsets();
            final Dimension dimension7 = dimension;
            dimension7.width += insets.left + insets.right;
            final Dimension dimension8 = dimension;
            dimension8.height += insets.top + insets.bottom;
            return dimension;
        }
    }
    
    @Override
    public Dimension preferredLayoutSize(final Container container) {
        synchronized (container.getTreeLock()) {
            final Dimension dimension = new Dimension(0, 0);
            final boolean leftToRight = container.getComponentOrientation().isLeftToRight();
            final Component child;
            if ((child = this.getChild("East", leftToRight)) != null) {
                final Dimension preferredSize = child.getPreferredSize();
                final Dimension dimension2 = dimension;
                dimension2.width += preferredSize.width + this.hgap;
                dimension.height = Math.max(preferredSize.height, dimension.height);
            }
            final Component child2;
            if ((child2 = this.getChild("West", leftToRight)) != null) {
                final Dimension preferredSize2 = child2.getPreferredSize();
                final Dimension dimension3 = dimension;
                dimension3.width += preferredSize2.width + this.hgap;
                dimension.height = Math.max(preferredSize2.height, dimension.height);
            }
            final Component child3;
            if ((child3 = this.getChild("Center", leftToRight)) != null) {
                final Dimension preferredSize3 = child3.getPreferredSize();
                final Dimension dimension4 = dimension;
                dimension4.width += preferredSize3.width;
                dimension.height = Math.max(preferredSize3.height, dimension.height);
            }
            final Component child4;
            if ((child4 = this.getChild("North", leftToRight)) != null) {
                final Dimension preferredSize4 = child4.getPreferredSize();
                dimension.width = Math.max(preferredSize4.width, dimension.width);
                final Dimension dimension5 = dimension;
                dimension5.height += preferredSize4.height + this.vgap;
            }
            final Component child5;
            if ((child5 = this.getChild("South", leftToRight)) != null) {
                final Dimension preferredSize5 = child5.getPreferredSize();
                dimension.width = Math.max(preferredSize5.width, dimension.width);
                final Dimension dimension6 = dimension;
                dimension6.height += preferredSize5.height + this.vgap;
            }
            final Insets insets = container.getInsets();
            final Dimension dimension7 = dimension;
            dimension7.width += insets.left + insets.right;
            final Dimension dimension8 = dimension;
            dimension8.height += insets.top + insets.bottom;
            return dimension;
        }
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
        synchronized (container.getTreeLock()) {
            final Insets insets = container.getInsets();
            int top = insets.top;
            int n = container.height - insets.bottom;
            int left = insets.left;
            int n2 = container.width - insets.right;
            final boolean leftToRight = container.getComponentOrientation().isLeftToRight();
            final Component child;
            if ((child = this.getChild("North", leftToRight)) != null) {
                child.setSize(n2 - left, child.height);
                final Dimension preferredSize = child.getPreferredSize();
                child.setBounds(left, top, n2 - left, preferredSize.height);
                top += preferredSize.height + this.vgap;
            }
            final Component child2;
            if ((child2 = this.getChild("South", leftToRight)) != null) {
                child2.setSize(n2 - left, child2.height);
                final Dimension preferredSize2 = child2.getPreferredSize();
                child2.setBounds(left, n - preferredSize2.height, n2 - left, preferredSize2.height);
                n -= preferredSize2.height + this.vgap;
            }
            final Component child3;
            if ((child3 = this.getChild("East", leftToRight)) != null) {
                child3.setSize(child3.width, n - top);
                final Dimension preferredSize3 = child3.getPreferredSize();
                child3.setBounds(n2 - preferredSize3.width, top, preferredSize3.width, n - top);
                n2 -= preferredSize3.width + this.hgap;
            }
            final Component child4;
            if ((child4 = this.getChild("West", leftToRight)) != null) {
                child4.setSize(child4.width, n - top);
                final Dimension preferredSize4 = child4.getPreferredSize();
                child4.setBounds(left, top, preferredSize4.width, n - top);
                left += preferredSize4.width + this.hgap;
            }
            final Component child5;
            if ((child5 = this.getChild("Center", leftToRight)) != null) {
                child5.setBounds(left, top, n2 - left, n - top);
            }
        }
    }
    
    private Component getChild(final String s, final boolean b) {
        Component component = null;
        if (s == "North") {
            component = ((this.firstLine != null) ? this.firstLine : this.north);
        }
        else if (s == "South") {
            component = ((this.lastLine != null) ? this.lastLine : this.south);
        }
        else if (s == "West") {
            component = (b ? this.firstItem : this.lastItem);
            if (component == null) {
                component = this.west;
            }
        }
        else if (s == "East") {
            component = (b ? this.lastItem : this.firstItem);
            if (component == null) {
                component = this.east;
            }
        }
        else if (s == "Center") {
            component = this.center;
        }
        if (component != null && !component.visible) {
            component = null;
        }
        return component;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[hgap=" + this.hgap + ",vgap=" + this.vgap + "]";
    }
}
