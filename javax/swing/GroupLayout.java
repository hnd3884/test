package javax.swing;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.awt.Insets;
import java.awt.Dimension;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.awt.Container;
import java.awt.Component;
import java.util.Map;
import java.awt.LayoutManager2;

public class GroupLayout implements LayoutManager2
{
    private static final int MIN_SIZE = 0;
    private static final int PREF_SIZE = 1;
    private static final int MAX_SIZE = 2;
    private static final int SPECIFIC_SIZE = 3;
    private static final int UNSET = Integer.MIN_VALUE;
    public static final int DEFAULT_SIZE = -1;
    public static final int PREFERRED_SIZE = -2;
    private boolean autocreatePadding;
    private boolean autocreateContainerPadding;
    private Group horizontalGroup;
    private Group verticalGroup;
    private Map<Component, ComponentInfo> componentInfos;
    private Container host;
    private Set<Spring> tmpParallelSet;
    private boolean springsChanged;
    private boolean isValid;
    private boolean hasPreferredPaddingSprings;
    private LayoutStyle layoutStyle;
    private boolean honorsVisibility;
    
    private static void checkSize(final int n, final int n2, final int n3, final boolean b) {
        checkResizeType(n, b);
        if (!b && n2 < 0) {
            throw new IllegalArgumentException("Pref must be >= 0");
        }
        if (b) {
            checkResizeType(n2, true);
        }
        checkResizeType(n3, b);
        checkLessThan(n, n2);
        checkLessThan(n2, n3);
    }
    
    private static void checkResizeType(final int n, final boolean b) {
        if (n < 0 && ((b && n != -1 && n != -2) || (!b && n != -2))) {
            throw new IllegalArgumentException("Invalid size");
        }
    }
    
    private static void checkLessThan(final int n, final int n2) {
        if (n >= 0 && n2 >= 0 && n > n2) {
            throw new IllegalArgumentException("Following is not met: min<=pref<=max");
        }
    }
    
    public GroupLayout(final Container host) {
        if (host == null) {
            throw new IllegalArgumentException("Container must be non-null");
        }
        this.honorsVisibility = true;
        this.host = host;
        this.setHorizontalGroup(this.createParallelGroup(Alignment.LEADING, true));
        this.setVerticalGroup(this.createParallelGroup(Alignment.LEADING, true));
        this.componentInfos = new HashMap<Component, ComponentInfo>();
        this.tmpParallelSet = new HashSet<Spring>();
    }
    
    public void setHonorsVisibility(final boolean honorsVisibility) {
        if (this.honorsVisibility != honorsVisibility) {
            this.honorsVisibility = honorsVisibility;
            this.springsChanged = true;
            this.isValid = false;
            this.invalidateHost();
        }
    }
    
    public boolean getHonorsVisibility() {
        return this.honorsVisibility;
    }
    
    public void setHonorsVisibility(final Component component, final Boolean honorsVisibility) {
        if (component == null) {
            throw new IllegalArgumentException("Component must be non-null");
        }
        this.getComponentInfo(component).setHonorsVisibility(honorsVisibility);
        this.springsChanged = true;
        this.isValid = false;
        this.invalidateHost();
    }
    
    public void setAutoCreateGaps(final boolean autocreatePadding) {
        if (this.autocreatePadding != autocreatePadding) {
            this.autocreatePadding = autocreatePadding;
            this.invalidateHost();
        }
    }
    
    public boolean getAutoCreateGaps() {
        return this.autocreatePadding;
    }
    
    public void setAutoCreateContainerGaps(final boolean autocreateContainerPadding) {
        if (this.autocreateContainerPadding != autocreateContainerPadding) {
            this.autocreateContainerPadding = autocreateContainerPadding;
            this.horizontalGroup = this.createTopLevelGroup(this.getHorizontalGroup());
            this.verticalGroup = this.createTopLevelGroup(this.getVerticalGroup());
            this.invalidateHost();
        }
    }
    
    public boolean getAutoCreateContainerGaps() {
        return this.autocreateContainerPadding;
    }
    
    public void setHorizontalGroup(final Group group) {
        if (group == null) {
            throw new IllegalArgumentException("Group must be non-null");
        }
        this.horizontalGroup = this.createTopLevelGroup(group);
        this.invalidateHost();
    }
    
    private Group getHorizontalGroup() {
        int n = 0;
        if (this.horizontalGroup.springs.size() > 1) {
            n = 1;
        }
        return (Group)this.horizontalGroup.springs.get(n);
    }
    
    public void setVerticalGroup(final Group group) {
        if (group == null) {
            throw new IllegalArgumentException("Group must be non-null");
        }
        this.verticalGroup = this.createTopLevelGroup(group);
        this.invalidateHost();
    }
    
    private Group getVerticalGroup() {
        int n = 0;
        if (this.verticalGroup.springs.size() > 1) {
            n = 1;
        }
        return (Group)this.verticalGroup.springs.get(n);
    }
    
    private Group createTopLevelGroup(final Group group) {
        final SequentialGroup sequentialGroup = this.createSequentialGroup();
        if (this.getAutoCreateContainerGaps()) {
            sequentialGroup.addSpring(new ContainerAutoPreferredGapSpring());
            sequentialGroup.addGroup(group);
            sequentialGroup.addSpring(new ContainerAutoPreferredGapSpring());
        }
        else {
            sequentialGroup.addGroup(group);
        }
        return sequentialGroup;
    }
    
    public SequentialGroup createSequentialGroup() {
        return new SequentialGroup();
    }
    
    public ParallelGroup createParallelGroup() {
        return this.createParallelGroup(Alignment.LEADING);
    }
    
    public ParallelGroup createParallelGroup(final Alignment alignment) {
        return this.createParallelGroup(alignment, true);
    }
    
    public ParallelGroup createParallelGroup(final Alignment alignment, final boolean b) {
        if (alignment == null) {
            throw new IllegalArgumentException("alignment must be non null");
        }
        if (alignment == Alignment.BASELINE) {
            return new BaselineGroup(b);
        }
        return new ParallelGroup(alignment, b);
    }
    
    public ParallelGroup createBaselineGroup(final boolean b, final boolean b2) {
        return new BaselineGroup(b, b2);
    }
    
    public void linkSize(final Component... array) {
        this.linkSize(0, array);
        this.linkSize(1, array);
    }
    
    public void linkSize(final int n, final Component... array) {
        if (array == null) {
            throw new IllegalArgumentException("Components must be non-null");
        }
        for (int i = array.length - 1; i >= 0; --i) {
            final Component component = array[i];
            if (array[i] == null) {
                throw new IllegalArgumentException("Components must be non-null");
            }
            this.getComponentInfo(component);
        }
        int n2;
        if (n == 0) {
            n2 = 0;
        }
        else {
            if (n != 1) {
                throw new IllegalArgumentException("Axis must be one of SwingConstants.HORIZONTAL or SwingConstants.VERTICAL");
            }
            n2 = 1;
        }
        final LinkInfo linkInfo = this.getComponentInfo(array[array.length - 1]).getLinkInfo(n2);
        for (int j = array.length - 2; j >= 0; --j) {
            linkInfo.add(this.getComponentInfo(array[j]));
        }
        this.invalidateHost();
    }
    
    public void replace(final Component component, final Component component2) {
        if (component == null || component2 == null) {
            throw new IllegalArgumentException("Components must be non-null");
        }
        if (this.springsChanged) {
            this.registerComponents(this.horizontalGroup, 0);
            this.registerComponents(this.verticalGroup, 1);
        }
        final ComponentInfo componentInfo = this.componentInfos.remove(component);
        if (componentInfo == null) {
            throw new IllegalArgumentException("Component must already exist");
        }
        this.host.remove(component);
        if (component2.getParent() != this.host) {
            this.host.add(component2);
        }
        componentInfo.setComponent(component2);
        this.componentInfos.put(component2, componentInfo);
        this.invalidateHost();
    }
    
    public void setLayoutStyle(final LayoutStyle layoutStyle) {
        this.layoutStyle = layoutStyle;
        this.invalidateHost();
    }
    
    public LayoutStyle getLayoutStyle() {
        return this.layoutStyle;
    }
    
    private LayoutStyle getLayoutStyle0() {
        LayoutStyle layoutStyle = this.getLayoutStyle();
        if (layoutStyle == null) {
            layoutStyle = LayoutStyle.getInstance();
        }
        return layoutStyle;
    }
    
    private void invalidateHost() {
        if (this.host instanceof JComponent) {
            ((JComponent)this.host).revalidate();
        }
        else {
            this.host.invalidate();
        }
        this.host.repaint();
    }
    
    @Override
    public void addLayoutComponent(final String s, final Component component) {
    }
    
    @Override
    public void removeLayoutComponent(final Component component) {
        final ComponentInfo componentInfo = this.componentInfos.remove(component);
        if (componentInfo != null) {
            componentInfo.dispose();
            this.springsChanged = true;
            this.isValid = false;
        }
    }
    
    @Override
    public Dimension preferredLayoutSize(final Container container) {
        this.checkParent(container);
        this.prepare(1);
        return this.adjustSize(this.horizontalGroup.getPreferredSize(0), this.verticalGroup.getPreferredSize(1));
    }
    
    @Override
    public Dimension minimumLayoutSize(final Container container) {
        this.checkParent(container);
        this.prepare(0);
        return this.adjustSize(this.horizontalGroup.getMinimumSize(0), this.verticalGroup.getMinimumSize(1));
    }
    
    @Override
    public void layoutContainer(final Container container) {
        this.prepare(3);
        final Insets insets = container.getInsets();
        final int n = container.getWidth() - insets.left - insets.right;
        final int n2 = container.getHeight() - insets.top - insets.bottom;
        final boolean leftToRight = this.isLeftToRight();
        if (this.getAutoCreateGaps() || this.getAutoCreateContainerGaps() || this.hasPreferredPaddingSprings) {
            this.calculateAutopadding(this.horizontalGroup, 0, 3, 0, n);
            this.calculateAutopadding(this.verticalGroup, 1, 3, 0, n2);
        }
        this.horizontalGroup.setSize(0, 0, n);
        this.verticalGroup.setSize(1, 0, n2);
        final Iterator<ComponentInfo> iterator = this.componentInfos.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().setBounds(insets, n, leftToRight);
        }
    }
    
    @Override
    public void addLayoutComponent(final Component component, final Object o) {
    }
    
    @Override
    public Dimension maximumLayoutSize(final Container container) {
        this.checkParent(container);
        this.prepare(2);
        return this.adjustSize(this.horizontalGroup.getMaximumSize(0), this.verticalGroup.getMaximumSize(1));
    }
    
    @Override
    public float getLayoutAlignmentX(final Container container) {
        this.checkParent(container);
        return 0.5f;
    }
    
    @Override
    public float getLayoutAlignmentY(final Container container) {
        this.checkParent(container);
        return 0.5f;
    }
    
    @Override
    public void invalidateLayout(final Container container) {
        this.checkParent(container);
        synchronized (container.getTreeLock()) {
            this.isValid = false;
        }
    }
    
    private void prepare(final int n) {
        boolean b = false;
        if (!this.isValid) {
            this.isValid = true;
            this.horizontalGroup.setSize(0, Integer.MIN_VALUE, Integer.MIN_VALUE);
            this.verticalGroup.setSize(1, Integer.MIN_VALUE, Integer.MIN_VALUE);
            for (final ComponentInfo componentInfo : this.componentInfos.values()) {
                if (componentInfo.updateVisibility()) {
                    b = true;
                }
                componentInfo.clearCachedSize();
            }
        }
        if (this.springsChanged) {
            this.registerComponents(this.horizontalGroup, 0);
            this.registerComponents(this.verticalGroup, 1);
        }
        if (this.springsChanged || b) {
            this.checkComponents();
            this.horizontalGroup.removeAutopadding();
            this.verticalGroup.removeAutopadding();
            if (this.getAutoCreateGaps()) {
                this.insertAutopadding(true);
            }
            else if (this.hasPreferredPaddingSprings || this.getAutoCreateContainerGaps()) {
                this.insertAutopadding(false);
            }
            this.springsChanged = false;
        }
        if (n != 3 && (this.getAutoCreateGaps() || this.getAutoCreateContainerGaps() || this.hasPreferredPaddingSprings)) {
            this.calculateAutopadding(this.horizontalGroup, 0, n, 0, 0);
            this.calculateAutopadding(this.verticalGroup, 1, n, 0, 0);
        }
    }
    
    private void calculateAutopadding(final Group group, final int n, final int n2, final int n3, int n4) {
        group.unsetAutopadding();
        switch (n2) {
            case 0: {
                n4 = group.getMinimumSize(n);
                break;
            }
            case 1: {
                n4 = group.getPreferredSize(n);
                break;
            }
            case 2: {
                n4 = group.getMaximumSize(n);
                break;
            }
        }
        group.setSize(n, n3, n4);
        group.calculateAutopadding(n);
    }
    
    private void checkComponents() {
        for (final ComponentInfo componentInfo : this.componentInfos.values()) {
            if (componentInfo.horizontalSpring == null) {
                throw new IllegalStateException(componentInfo.component + " is not attached to a horizontal group");
            }
            if (componentInfo.verticalSpring == null) {
                throw new IllegalStateException(componentInfo.component + " is not attached to a vertical group");
            }
        }
    }
    
    private void registerComponents(final Group group, final int n) {
        final List<Spring> springs = group.springs;
        for (int i = springs.size() - 1; i >= 0; --i) {
            final Spring spring = springs.get(i);
            if (spring instanceof ComponentSpring) {
                ((ComponentSpring)spring).installIfNecessary(n);
            }
            else if (spring instanceof Group) {
                this.registerComponents((Group)spring, n);
            }
        }
    }
    
    private Dimension adjustSize(final int n, final int n2) {
        final Insets insets = this.host.getInsets();
        return new Dimension(n + insets.left + insets.right, n2 + insets.top + insets.bottom);
    }
    
    private void checkParent(final Container container) {
        if (container != this.host) {
            throw new IllegalArgumentException("GroupLayout can only be used with one Container at a time");
        }
    }
    
    private ComponentInfo getComponentInfo(final Component component) {
        ComponentInfo componentInfo = this.componentInfos.get(component);
        if (componentInfo == null) {
            componentInfo = new ComponentInfo(component);
            this.componentInfos.put(component, componentInfo);
            if (component.getParent() != this.host) {
                this.host.add(component);
            }
        }
        return componentInfo;
    }
    
    private void insertAutopadding(final boolean b) {
        this.horizontalGroup.insertAutopadding(0, new ArrayList<AutoPreferredGapSpring>(1), new ArrayList<AutoPreferredGapSpring>(1), new ArrayList<ComponentSpring>(1), new ArrayList<ComponentSpring>(1), b);
        this.verticalGroup.insertAutopadding(1, new ArrayList<AutoPreferredGapSpring>(1), new ArrayList<AutoPreferredGapSpring>(1), new ArrayList<ComponentSpring>(1), new ArrayList<ComponentSpring>(1), b);
    }
    
    private boolean areParallelSiblings(final Component component, final Component component2, final int n) {
        final ComponentInfo componentInfo = this.getComponentInfo(component);
        final ComponentInfo componentInfo2 = this.getComponentInfo(component2);
        ComponentSpring componentSpring;
        ComponentSpring componentSpring2;
        if (n == 0) {
            componentSpring = componentInfo.horizontalSpring;
            componentSpring2 = componentInfo2.horizontalSpring;
        }
        else {
            componentSpring = componentInfo.verticalSpring;
            componentSpring2 = componentInfo2.verticalSpring;
        }
        final Set<Spring> tmpParallelSet = this.tmpParallelSet;
        tmpParallelSet.clear();
        for (Spring spring = componentSpring.getParent(); spring != null; spring = spring.getParent()) {
            tmpParallelSet.add(spring);
        }
        for (Spring spring2 = componentSpring2.getParent(); spring2 != null; spring2 = spring2.getParent()) {
            if (tmpParallelSet.contains(spring2)) {
                tmpParallelSet.clear();
                while (spring2 != null) {
                    if (spring2 instanceof ParallelGroup) {
                        return true;
                    }
                    spring2 = spring2.getParent();
                }
                return false;
            }
        }
        tmpParallelSet.clear();
        return false;
    }
    
    private boolean isLeftToRight() {
        return this.host.getComponentOrientation().isLeftToRight();
    }
    
    @Override
    public String toString() {
        if (this.springsChanged) {
            this.registerComponents(this.horizontalGroup, 0);
            this.registerComponents(this.verticalGroup, 1);
        }
        final StringBuffer sb = new StringBuffer();
        sb.append("HORIZONTAL\n");
        this.createSpringDescription(sb, this.horizontalGroup, "  ", 0);
        sb.append("\nVERTICAL\n");
        this.createSpringDescription(sb, this.verticalGroup, "  ", 1);
        return sb.toString();
    }
    
    private void createSpringDescription(final StringBuffer sb, final Spring spring, String string, final int n) {
        String s = "";
        String string2 = "";
        if (spring instanceof ComponentSpring) {
            final ComponentSpring componentSpring = (ComponentSpring)spring;
            s = Integer.toString(componentSpring.getOrigin()) + " ";
            final String name = componentSpring.getComponent().getName();
            if (name != null) {
                s = "name=" + name + ", ";
            }
        }
        if (spring instanceof AutoPreferredGapSpring) {
            final AutoPreferredGapSpring autoPreferredGapSpring = (AutoPreferredGapSpring)spring;
            string2 = ", userCreated=" + autoPreferredGapSpring.getUserCreated() + ", matches=" + autoPreferredGapSpring.getMatchDescription();
        }
        sb.append(string + spring.getClass().getName() + " " + Integer.toHexString(spring.hashCode()) + " " + s + ", size=" + spring.getSize() + ", alignment=" + spring.getAlignment() + " prefs=[" + spring.getMinimumSize(n) + " " + spring.getPreferredSize(n) + " " + spring.getMaximumSize(n) + string2 + "]\n");
        if (spring instanceof Group) {
            final List<Spring> springs = ((Group)spring).springs;
            string += "  ";
            for (int i = 0; i < springs.size(); ++i) {
                this.createSpringDescription(sb, (Spring)springs.get(i), string, n);
            }
        }
    }
    
    public enum Alignment
    {
        LEADING, 
        TRAILING, 
        CENTER, 
        BASELINE;
    }
    
    private abstract class Spring
    {
        private int size;
        private int min;
        private int max;
        private int pref;
        private Spring parent;
        private Alignment alignment;
        
        Spring() {
            final int min = Integer.MIN_VALUE;
            this.max = min;
            this.pref = min;
            this.min = min;
        }
        
        abstract int calculateMinimumSize(final int p0);
        
        abstract int calculatePreferredSize(final int p0);
        
        abstract int calculateMaximumSize(final int p0);
        
        void setParent(final Spring parent) {
            this.parent = parent;
        }
        
        Spring getParent() {
            return this.parent;
        }
        
        void setAlignment(final Alignment alignment) {
            this.alignment = alignment;
        }
        
        Alignment getAlignment() {
            return this.alignment;
        }
        
        final int getMinimumSize(final int n) {
            if (this.min == Integer.MIN_VALUE) {
                this.min = this.constrain(this.calculateMinimumSize(n));
            }
            return this.min;
        }
        
        final int getPreferredSize(final int n) {
            if (this.pref == Integer.MIN_VALUE) {
                this.pref = this.constrain(this.calculatePreferredSize(n));
            }
            return this.pref;
        }
        
        final int getMaximumSize(final int n) {
            if (this.max == Integer.MIN_VALUE) {
                this.max = this.constrain(this.calculateMaximumSize(n));
            }
            return this.max;
        }
        
        void setSize(final int n, final int n2, final int size) {
            this.size = size;
            if (size == Integer.MIN_VALUE) {
                this.unset();
            }
        }
        
        void unset() {
            final int n = Integer.MIN_VALUE;
            this.max = n;
            this.pref = n;
            this.min = n;
            this.size = n;
        }
        
        int getSize() {
            return this.size;
        }
        
        int constrain(final int n) {
            return Math.min(n, 32767);
        }
        
        int getBaseline() {
            return -1;
        }
        
        Component.BaselineResizeBehavior getBaselineResizeBehavior() {
            return Component.BaselineResizeBehavior.OTHER;
        }
        
        final boolean isResizable(final int n) {
            final int minimumSize = this.getMinimumSize(n);
            final int preferredSize = this.getPreferredSize(n);
            return minimumSize != preferredSize || preferredSize != this.getMaximumSize(n);
        }
        
        abstract boolean willHaveZeroSize(final boolean p0);
    }
    
    public abstract class Group extends Spring
    {
        List<Spring> springs;
        
        Group() {
            this.springs = new ArrayList<Spring>();
        }
        
        public Group addGroup(final Group group) {
            return this.addSpring(group);
        }
        
        public Group addComponent(final Component component) {
            return this.addComponent(component, -1, -1, -1);
        }
        
        public Group addComponent(final Component component, final int n, final int n2, final int n3) {
            return this.addSpring(new ComponentSpring(component, n, n2, n3));
        }
        
        public Group addGap(final int n) {
            return this.addGap(n, n, n);
        }
        
        public Group addGap(final int n, final int n2, final int n3) {
            return this.addSpring(new GapSpring(n, n2, n3));
        }
        
        Spring getSpring(final int n) {
            return this.springs.get(n);
        }
        
        int indexOf(final Spring spring) {
            return this.springs.indexOf(spring);
        }
        
        Group addSpring(final Spring spring) {
            this.springs.add(spring);
            spring.setParent(this);
            if (!(spring instanceof AutoPreferredGapSpring) || !((AutoPreferredGapSpring)spring).getUserCreated()) {
                GroupLayout.this.springsChanged = true;
            }
            return this;
        }
        
        @Override
        void setSize(final int n, final int n2, final int n3) {
            super.setSize(n, n2, n3);
            if (n3 == Integer.MIN_VALUE) {
                for (int i = this.springs.size() - 1; i >= 0; --i) {
                    this.getSpring(i).setSize(n, n2, n3);
                }
            }
            else {
                this.setValidSize(n, n2, n3);
            }
        }
        
        abstract void setValidSize(final int p0, final int p1, final int p2);
        
        @Override
        int calculateMinimumSize(final int n) {
            return this.calculateSize(n, 0);
        }
        
        @Override
        int calculatePreferredSize(final int n) {
            return this.calculateSize(n, 1);
        }
        
        @Override
        int calculateMaximumSize(final int n) {
            return this.calculateSize(n, 2);
        }
        
        int calculateSize(final int n, final int n2) {
            final int size = this.springs.size();
            if (size == 0) {
                return 0;
            }
            if (size == 1) {
                return this.getSpringSize(this.getSpring(0), n, n2);
            }
            int n3 = this.constrain(this.operator(this.getSpringSize(this.getSpring(0), n, n2), this.getSpringSize(this.getSpring(1), n, n2)));
            for (int i = 2; i < size; ++i) {
                n3 = this.constrain(this.operator(n3, this.getSpringSize(this.getSpring(i), n, n2)));
            }
            return n3;
        }
        
        int getSpringSize(final Spring spring, final int n, final int n2) {
            switch (n2) {
                case 0: {
                    return spring.getMinimumSize(n);
                }
                case 1: {
                    return spring.getPreferredSize(n);
                }
                case 2: {
                    return spring.getMaximumSize(n);
                }
                default: {
                    assert false;
                    return 0;
                }
            }
        }
        
        abstract int operator(final int p0, final int p1);
        
        abstract void insertAutopadding(final int p0, final List<AutoPreferredGapSpring> p1, final List<AutoPreferredGapSpring> p2, final List<ComponentSpring> p3, final List<ComponentSpring> p4, final boolean p5);
        
        void removeAutopadding() {
            this.unset();
            for (int i = this.springs.size() - 1; i >= 0; --i) {
                final Spring spring = this.springs.get(i);
                if (spring instanceof AutoPreferredGapSpring) {
                    if (((AutoPreferredGapSpring)spring).getUserCreated()) {
                        ((AutoPreferredGapSpring)spring).reset();
                    }
                    else {
                        this.springs.remove(i);
                    }
                }
                else if (spring instanceof Group) {
                    ((Group)spring).removeAutopadding();
                }
            }
        }
        
        void unsetAutopadding() {
            this.unset();
            for (int i = this.springs.size() - 1; i >= 0; --i) {
                final Spring spring = this.springs.get(i);
                if (spring instanceof AutoPreferredGapSpring) {
                    spring.unset();
                }
                else if (spring instanceof Group) {
                    ((Group)spring).unsetAutopadding();
                }
            }
        }
        
        void calculateAutopadding(final int n) {
            for (int i = this.springs.size() - 1; i >= 0; --i) {
                final Spring spring = this.springs.get(i);
                if (spring instanceof AutoPreferredGapSpring) {
                    spring.unset();
                    ((AutoPreferredGapSpring)spring).calculatePadding(n);
                }
                else if (spring instanceof Group) {
                    ((Group)spring).calculateAutopadding(n);
                }
            }
            this.unset();
        }
        
        @Override
        boolean willHaveZeroSize(final boolean b) {
            for (int i = this.springs.size() - 1; i >= 0; --i) {
                if (!this.springs.get(i).willHaveZeroSize(b)) {
                    return false;
                }
            }
            return true;
        }
    }
    
    public class SequentialGroup extends Group
    {
        private Spring baselineSpring;
        
        SequentialGroup() {
        }
        
        @Override
        public SequentialGroup addGroup(final Group group) {
            return (SequentialGroup)super.addGroup(group);
        }
        
        public SequentialGroup addGroup(final boolean b, final Group baselineSpring) {
            super.addGroup(baselineSpring);
            if (b) {
                this.baselineSpring = baselineSpring;
            }
            return this;
        }
        
        @Override
        public SequentialGroup addComponent(final Component component) {
            return (SequentialGroup)super.addComponent(component);
        }
        
        public SequentialGroup addComponent(final boolean b, final Component component) {
            super.addComponent(component);
            if (b) {
                this.baselineSpring = this.springs.get(this.springs.size() - 1);
            }
            return this;
        }
        
        @Override
        public SequentialGroup addComponent(final Component component, final int n, final int n2, final int n3) {
            return (SequentialGroup)super.addComponent(component, n, n2, n3);
        }
        
        public SequentialGroup addComponent(final boolean b, final Component component, final int n, final int n2, final int n3) {
            super.addComponent(component, n, n2, n3);
            if (b) {
                this.baselineSpring = this.springs.get(this.springs.size() - 1);
            }
            return this;
        }
        
        @Override
        public SequentialGroup addGap(final int n) {
            return (SequentialGroup)super.addGap(n);
        }
        
        @Override
        public SequentialGroup addGap(final int n, final int n2, final int n3) {
            return (SequentialGroup)super.addGap(n, n2, n3);
        }
        
        public SequentialGroup addPreferredGap(final JComponent component, final JComponent component2, final LayoutStyle.ComponentPlacement componentPlacement) {
            return this.addPreferredGap(component, component2, componentPlacement, -1, -2);
        }
        
        public SequentialGroup addPreferredGap(final JComponent component, final JComponent component2, final LayoutStyle.ComponentPlacement componentPlacement, final int n, final int n2) {
            if (componentPlacement == null) {
                throw new IllegalArgumentException("Type must be non-null");
            }
            if (component == null || component2 == null) {
                throw new IllegalArgumentException("Components must be non-null");
            }
            this.checkPreferredGapValues(n, n2);
            return (SequentialGroup)this.addSpring(new PreferredGapSpring(component, component2, componentPlacement, n, n2));
        }
        
        public SequentialGroup addPreferredGap(final LayoutStyle.ComponentPlacement componentPlacement) {
            return this.addPreferredGap(componentPlacement, -1, -1);
        }
        
        public SequentialGroup addPreferredGap(final LayoutStyle.ComponentPlacement componentPlacement, final int n, final int n2) {
            if (componentPlacement != LayoutStyle.ComponentPlacement.RELATED && componentPlacement != LayoutStyle.ComponentPlacement.UNRELATED) {
                throw new IllegalArgumentException("Type must be one of LayoutStyle.ComponentPlacement.RELATED or LayoutStyle.ComponentPlacement.UNRELATED");
            }
            this.checkPreferredGapValues(n, n2);
            GroupLayout.this.hasPreferredPaddingSprings = true;
            return (SequentialGroup)this.addSpring(new AutoPreferredGapSpring(componentPlacement, n, n2));
        }
        
        public SequentialGroup addContainerGap() {
            return this.addContainerGap(-1, -1);
        }
        
        public SequentialGroup addContainerGap(final int n, final int n2) {
            if ((n < 0 && n != -1) || (n2 < 0 && n2 != -1 && n2 != -2) || (n >= 0 && n2 >= 0 && n > n2)) {
                throw new IllegalArgumentException("Pref and max must be either DEFAULT_VALUE or >= 0 and pref <= max");
            }
            GroupLayout.this.hasPreferredPaddingSprings = true;
            return (SequentialGroup)this.addSpring(new ContainerAutoPreferredGapSpring(n, n2));
        }
        
        @Override
        int operator(final int n, final int n2) {
            return this.constrain(n) + this.constrain(n2);
        }
        
        @Override
        void setValidSize(final int n, int n2, final int n3) {
            if (n3 == this.getPreferredSize(n)) {
                for (final Spring spring : this.springs) {
                    final int preferredSize = spring.getPreferredSize(n);
                    spring.setSize(n, n2, preferredSize);
                    n2 += preferredSize;
                }
            }
            else if (this.springs.size() == 1) {
                final Spring spring2 = this.getSpring(0);
                spring2.setSize(n, n2, Math.min(Math.max(n3, spring2.getMinimumSize(n)), spring2.getMaximumSize(n)));
            }
            else if (this.springs.size() > 1) {
                this.setValidSizeNotPreferred(n, n2, n3);
            }
        }
        
        private void setValidSizeNotPreferred(final int n, int n2, final int n3) {
            int n4 = n3 - this.getPreferredSize(n);
            assert n4 != 0;
            final boolean b = n4 < 0;
            final int size = this.springs.size();
            if (b) {
                n4 *= -1;
            }
            final List<SpringDelta> buildResizableList = this.buildResizableList(n, b);
            final int size2 = buildResizableList.size();
            if (size2 > 0) {
                int n5 = n4 / size2;
                int n6 = n4 - n5 * size2;
                final int[] array = new int[size];
                final int n7 = b ? -1 : 1;
                for (int i = 0; i < size2; ++i) {
                    final SpringDelta springDelta = buildResizableList.get(i);
                    if (i + 1 == size2) {
                        n5 += n6;
                    }
                    springDelta.delta = Math.min(n5, springDelta.delta);
                    n4 -= springDelta.delta;
                    if (springDelta.delta != n5 && i + 1 < size2) {
                        n5 = n4 / (size2 - i - 1);
                        n6 = n4 - n5 * (size2 - i - 1);
                    }
                    array[springDelta.index] = n7 * springDelta.delta;
                }
                for (int j = 0; j < size; ++j) {
                    final Spring spring = this.getSpring(j);
                    final int n8 = spring.getPreferredSize(n) + array[j];
                    spring.setSize(n, n2, n8);
                    n2 += n8;
                }
            }
            else {
                for (int k = 0; k < size; ++k) {
                    final Spring spring2 = this.getSpring(k);
                    int n9;
                    if (b) {
                        n9 = spring2.getMinimumSize(n);
                    }
                    else {
                        n9 = spring2.getMaximumSize(n);
                    }
                    spring2.setSize(n, n2, n9);
                    n2 += n9;
                }
            }
        }
        
        private List<SpringDelta> buildResizableList(final int n, final boolean b) {
            final int size = this.springs.size();
            final ArrayList list = new ArrayList(size);
            for (int i = 0; i < size; ++i) {
                final Spring spring = this.getSpring(i);
                int n2;
                if (b) {
                    n2 = spring.getPreferredSize(n) - spring.getMinimumSize(n);
                }
                else {
                    n2 = spring.getMaximumSize(n) - spring.getPreferredSize(n);
                }
                if (n2 > 0) {
                    list.add((Object)new SpringDelta(i, n2));
                }
            }
            Collections.sort((List<Comparable>)list);
            return (List<SpringDelta>)list;
        }
        
        private int indexOfNextNonZeroSpring(int i, final boolean b) {
            while (i < this.springs.size()) {
                if (!this.springs.get(i).willHaveZeroSize(b)) {
                    return i;
                }
                ++i;
            }
            return i;
        }
        
        @Override
        void insertAutopadding(final int n, final List<AutoPreferredGapSpring> list, final List<AutoPreferredGapSpring> list2, final List<ComponentSpring> list3, final List<ComponentSpring> list4, final boolean b) {
            final ArrayList list5 = new ArrayList((Collection<? extends E>)list);
            final ArrayList list6 = new ArrayList(1);
            final ArrayList sources = new ArrayList((Collection<? extends E>)list3);
            List list7 = null;
            int i = 0;
            while (i < this.springs.size()) {
                final Spring spring = this.getSpring(i);
                if (spring instanceof AutoPreferredGapSpring) {
                    if (list5.size() == 0) {
                        final AutoPreferredGapSpring autoPreferredGapSpring = (AutoPreferredGapSpring)spring;
                        autoPreferredGapSpring.setSources(sources);
                        sources.clear();
                        i = this.indexOfNextNonZeroSpring(i + 1, true);
                        if (i == this.springs.size()) {
                            if (autoPreferredGapSpring instanceof ContainerAutoPreferredGapSpring) {
                                continue;
                            }
                            list2.add(autoPreferredGapSpring);
                        }
                        else {
                            list5.clear();
                            list5.add(autoPreferredGapSpring);
                        }
                    }
                    else {
                        i = this.indexOfNextNonZeroSpring(i + 1, true);
                    }
                }
                else if (sources.size() > 0 && b) {
                    this.springs.add(i, new AutoPreferredGapSpring());
                }
                else if (spring instanceof ComponentSpring) {
                    final ComponentSpring componentSpring = (ComponentSpring)spring;
                    if (!componentSpring.isVisible()) {
                        ++i;
                    }
                    else {
                        final Iterator iterator = list5.iterator();
                        while (iterator.hasNext()) {
                            ((AutoPreferredGapSpring)iterator.next()).addTarget(componentSpring, n);
                        }
                        sources.clear();
                        list5.clear();
                        i = this.indexOfNextNonZeroSpring(i + 1, false);
                        if (i == this.springs.size()) {
                            list4.add(componentSpring);
                        }
                        else {
                            sources.add(componentSpring);
                        }
                    }
                }
                else if (spring instanceof Group) {
                    if (list7 == null) {
                        list7 = new ArrayList<ComponentSpring>(1);
                    }
                    else {
                        list7.clear();
                    }
                    list6.clear();
                    ((Group)spring).insertAutopadding(n, list5, list6, sources, list7, b);
                    sources.clear();
                    list5.clear();
                    i = this.indexOfNextNonZeroSpring(i + 1, list7.size() == 0);
                    if (i == this.springs.size()) {
                        list4.addAll(list7);
                        list2.addAll(list6);
                    }
                    else {
                        sources.addAll(list7);
                        list5.addAll(list6);
                    }
                }
                else {
                    list5.clear();
                    sources.clear();
                    ++i;
                }
            }
        }
        
        @Override
        int getBaseline() {
            if (this.baselineSpring != null) {
                final int baseline = this.baselineSpring.getBaseline();
                if (baseline >= 0) {
                    int n = 0;
                    for (final Spring spring : this.springs) {
                        if (spring == this.baselineSpring) {
                            return n + baseline;
                        }
                        n += spring.getPreferredSize(1);
                    }
                }
            }
            return -1;
        }
        
        @Override
        Component.BaselineResizeBehavior getBaselineResizeBehavior() {
            if (this.isResizable(1)) {
                if (!this.baselineSpring.isResizable(1)) {
                    boolean b = false;
                    for (final Spring spring : this.springs) {
                        if (spring == this.baselineSpring) {
                            break;
                        }
                        if (spring.isResizable(1)) {
                            b = true;
                            break;
                        }
                    }
                    boolean b2 = false;
                    for (int i = this.springs.size() - 1; i >= 0; --i) {
                        final Spring spring2 = this.springs.get(i);
                        if (spring2 == this.baselineSpring) {
                            break;
                        }
                        if (spring2.isResizable(1)) {
                            b2 = true;
                            break;
                        }
                    }
                    if (b && !b2) {
                        return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
                    }
                    if (!b && b2) {
                        return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
                    }
                }
                else {
                    final Component.BaselineResizeBehavior baselineResizeBehavior = this.baselineSpring.getBaselineResizeBehavior();
                    if (baselineResizeBehavior == Component.BaselineResizeBehavior.CONSTANT_ASCENT) {
                        for (final Spring spring3 : this.springs) {
                            if (spring3 == this.baselineSpring) {
                                return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
                            }
                            if (spring3.isResizable(1)) {
                                return Component.BaselineResizeBehavior.OTHER;
                            }
                        }
                    }
                    else if (baselineResizeBehavior == Component.BaselineResizeBehavior.CONSTANT_DESCENT) {
                        for (int j = this.springs.size() - 1; j >= 0; --j) {
                            final Spring spring4 = this.springs.get(j);
                            if (spring4 == this.baselineSpring) {
                                return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
                            }
                            if (spring4.isResizable(1)) {
                                return Component.BaselineResizeBehavior.OTHER;
                            }
                        }
                    }
                }
                return Component.BaselineResizeBehavior.OTHER;
            }
            return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
        }
        
        private void checkPreferredGapValues(final int n, final int n2) {
            if ((n < 0 && n != -1 && n != -2) || (n2 < 0 && n2 != -1 && n2 != -2) || (n >= 0 && n2 >= 0 && n > n2)) {
                throw new IllegalArgumentException("Pref and max must be either DEFAULT_SIZE, PREFERRED_SIZE, or >= 0 and pref <= max");
            }
        }
    }
    
    private static final class SpringDelta implements Comparable<SpringDelta>
    {
        public final int index;
        public int delta;
        
        public SpringDelta(final int index, final int delta) {
            this.index = index;
            this.delta = delta;
        }
        
        @Override
        public int compareTo(final SpringDelta springDelta) {
            return this.delta - springDelta.delta;
        }
        
        @Override
        public String toString() {
            return super.toString() + "[index=" + this.index + ", delta=" + this.delta + "]";
        }
    }
    
    public class ParallelGroup extends Group
    {
        private final Alignment childAlignment;
        private final boolean resizable;
        
        ParallelGroup(final Alignment childAlignment, final boolean resizable) {
            this.childAlignment = childAlignment;
            this.resizable = resizable;
        }
        
        @Override
        public ParallelGroup addGroup(final Group group) {
            return (ParallelGroup)super.addGroup(group);
        }
        
        @Override
        public ParallelGroup addComponent(final Component component) {
            return (ParallelGroup)super.addComponent(component);
        }
        
        @Override
        public ParallelGroup addComponent(final Component component, final int n, final int n2, final int n3) {
            return (ParallelGroup)super.addComponent(component, n, n2, n3);
        }
        
        @Override
        public ParallelGroup addGap(final int n) {
            return (ParallelGroup)super.addGap(n);
        }
        
        @Override
        public ParallelGroup addGap(final int n, final int n2, final int n3) {
            return (ParallelGroup)super.addGap(n, n2, n3);
        }
        
        public ParallelGroup addGroup(final Alignment alignment, final Group group) {
            this.checkChildAlignment(alignment);
            group.setAlignment(alignment);
            return (ParallelGroup)this.addSpring(group);
        }
        
        public ParallelGroup addComponent(final Component component, final Alignment alignment) {
            return this.addComponent(component, alignment, -1, -1, -1);
        }
        
        public ParallelGroup addComponent(final Component component, final Alignment alignment, final int n, final int n2, final int n3) {
            this.checkChildAlignment(alignment);
            final ComponentSpring componentSpring = new ComponentSpring(component, n, n2, n3);
            componentSpring.setAlignment(alignment);
            return (ParallelGroup)this.addSpring(componentSpring);
        }
        
        boolean isResizable() {
            return this.resizable;
        }
        
        @Override
        int operator(final int n, final int n2) {
            return Math.max(n, n2);
        }
        
        @Override
        int calculateMinimumSize(final int n) {
            if (!this.isResizable()) {
                return this.getPreferredSize(n);
            }
            return super.calculateMinimumSize(n);
        }
        
        @Override
        int calculateMaximumSize(final int n) {
            if (!this.isResizable()) {
                return this.getPreferredSize(n);
            }
            return super.calculateMaximumSize(n);
        }
        
        @Override
        void setValidSize(final int n, final int n2, final int n3) {
            final Iterator<Spring> iterator = this.springs.iterator();
            while (iterator.hasNext()) {
                this.setChildSize(iterator.next(), n, n2, n3);
            }
        }
        
        void setChildSize(final Spring spring, final int n, final int n2, final int n3) {
            Alignment alignment = spring.getAlignment();
            final int min = Math.min(Math.max(spring.getMinimumSize(n), n3), spring.getMaximumSize(n));
            if (alignment == null) {
                alignment = this.childAlignment;
            }
            switch (alignment) {
                case TRAILING: {
                    spring.setSize(n, n2 + n3 - min, min);
                    break;
                }
                case CENTER: {
                    spring.setSize(n, n2 + (n3 - min) / 2, min);
                    break;
                }
                default: {
                    spring.setSize(n, n2, min);
                    break;
                }
            }
        }
        
        @Override
        void insertAutopadding(final int n, final List<AutoPreferredGapSpring> list, final List<AutoPreferredGapSpring> list2, final List<ComponentSpring> sources, final List<ComponentSpring> list3, final boolean b) {
            for (final Spring spring : this.springs) {
                if (spring instanceof ComponentSpring) {
                    if (!((ComponentSpring)spring).isVisible()) {
                        continue;
                    }
                    final Iterator<AutoPreferredGapSpring> iterator2 = list.iterator();
                    while (iterator2.hasNext()) {
                        iterator2.next().addTarget((ComponentSpring)spring, n);
                    }
                    list3.add((ComponentSpring)spring);
                }
                else if (spring instanceof Group) {
                    ((Group)spring).insertAutopadding(n, list, list2, sources, list3, b);
                }
                else {
                    if (!(spring instanceof AutoPreferredGapSpring)) {
                        continue;
                    }
                    ((AutoPreferredGapSpring)spring).setSources(sources);
                    list2.add((AutoPreferredGapSpring)spring);
                }
            }
        }
        
        private void checkChildAlignment(final Alignment alignment) {
            this.checkChildAlignment(alignment, this instanceof BaselineGroup);
        }
        
        private void checkChildAlignment(final Alignment alignment, final boolean b) {
            if (alignment == null) {
                throw new IllegalArgumentException("Alignment must be non-null");
            }
            if (!b && alignment == Alignment.BASELINE) {
                throw new IllegalArgumentException("Alignment must be one of:LEADING, TRAILING or CENTER");
            }
        }
    }
    
    private class BaselineGroup extends ParallelGroup
    {
        private boolean allSpringsHaveBaseline;
        private int prefAscent;
        private int prefDescent;
        private boolean baselineAnchorSet;
        private boolean baselineAnchoredToTop;
        private boolean calcedBaseline;
        
        BaselineGroup(final boolean b) {
            super(Alignment.LEADING, b);
            final int n = -1;
            this.prefDescent = n;
            this.prefAscent = n;
            this.calcedBaseline = false;
        }
        
        BaselineGroup(final GroupLayout groupLayout, final boolean b, final boolean baselineAnchoredToTop) {
            this(groupLayout, b);
            this.baselineAnchoredToTop = baselineAnchoredToTop;
            this.baselineAnchorSet = true;
        }
        
        @Override
        void unset() {
            super.unset();
            final int n = -1;
            this.prefDescent = n;
            this.prefAscent = n;
            this.calcedBaseline = false;
        }
        
        @Override
        void setValidSize(final int n, final int n2, final int n3) {
            this.checkAxis(n);
            if (this.prefAscent == -1) {
                super.setValidSize(n, n2, n3);
            }
            else {
                this.baselineLayout(n2, n3);
            }
        }
        
        @Override
        int calculateSize(final int n, final int n2) {
            this.checkAxis(n);
            if (!this.calcedBaseline) {
                this.calculateBaselineAndResizeBehavior();
            }
            if (n2 == 0) {
                return this.calculateMinSize();
            }
            if (n2 == 2) {
                return this.calculateMaxSize();
            }
            if (this.allSpringsHaveBaseline) {
                return this.prefAscent + this.prefDescent;
            }
            return Math.max(this.prefAscent + this.prefDescent, super.calculateSize(n, n2));
        }
        
        private void calculateBaselineAndResizeBehavior() {
            this.prefAscent = 0;
            this.prefDescent = 0;
            int n = 0;
            Component.BaselineResizeBehavior constant_ASCENT = null;
            for (final Spring spring : this.springs) {
                if (spring.getAlignment() == null || spring.getAlignment() == Alignment.BASELINE) {
                    final int baseline = spring.getBaseline();
                    if (baseline < 0) {
                        continue;
                    }
                    if (spring.isResizable(1)) {
                        final Component.BaselineResizeBehavior baselineResizeBehavior = spring.getBaselineResizeBehavior();
                        if (constant_ASCENT == null) {
                            constant_ASCENT = baselineResizeBehavior;
                        }
                        else if (baselineResizeBehavior != constant_ASCENT) {
                            constant_ASCENT = Component.BaselineResizeBehavior.CONSTANT_ASCENT;
                        }
                    }
                    this.prefAscent = Math.max(this.prefAscent, baseline);
                    this.prefDescent = Math.max(this.prefDescent, spring.getPreferredSize(1) - baseline);
                    ++n;
                }
            }
            if (!this.baselineAnchorSet) {
                if (constant_ASCENT == Component.BaselineResizeBehavior.CONSTANT_DESCENT) {
                    this.baselineAnchoredToTop = false;
                }
                else {
                    this.baselineAnchoredToTop = true;
                }
            }
            this.allSpringsHaveBaseline = (n == this.springs.size());
            this.calcedBaseline = true;
        }
        
        private int calculateMaxSize() {
            int n = this.prefAscent;
            int n2 = this.prefDescent;
            int max = 0;
            for (final Spring spring : this.springs) {
                final int maximumSize = spring.getMaximumSize(1);
                final int baseline;
                if ((spring.getAlignment() == null || spring.getAlignment() == Alignment.BASELINE) && (baseline = spring.getBaseline()) >= 0) {
                    final int preferredSize = spring.getPreferredSize(1);
                    if (preferredSize == maximumSize) {
                        continue;
                    }
                    switch (spring.getBaselineResizeBehavior()) {
                        case CONSTANT_ASCENT: {
                            if (this.baselineAnchoredToTop) {
                                n2 = Math.max(n2, maximumSize - baseline);
                                continue;
                            }
                            continue;
                        }
                        case CONSTANT_DESCENT: {
                            if (!this.baselineAnchoredToTop) {
                                n = Math.max(n, maximumSize - preferredSize + baseline);
                                continue;
                            }
                            continue;
                        }
                    }
                }
                else {
                    max = Math.max(max, maximumSize);
                }
            }
            return Math.max(max, n + n2);
        }
        
        private int calculateMinSize() {
            int n = 0;
            int n2 = 0;
            int max = 0;
            if (this.baselineAnchoredToTop) {
                n = this.prefAscent;
            }
            else {
                n2 = this.prefDescent;
            }
            for (final Spring spring : this.springs) {
                final int minimumSize = spring.getMinimumSize(1);
                final int baseline;
                if ((spring.getAlignment() == null || spring.getAlignment() == Alignment.BASELINE) && (baseline = spring.getBaseline()) >= 0) {
                    final int preferredSize = spring.getPreferredSize(1);
                    switch (spring.getBaselineResizeBehavior()) {
                        case CONSTANT_ASCENT: {
                            if (this.baselineAnchoredToTop) {
                                n2 = Math.max(minimumSize - baseline, n2);
                                continue;
                            }
                            n = Math.max(baseline, n);
                            continue;
                        }
                        case CONSTANT_DESCENT: {
                            if (!this.baselineAnchoredToTop) {
                                n = Math.max(baseline - (preferredSize - minimumSize), n);
                                continue;
                            }
                            n2 = Math.max(preferredSize - baseline, n2);
                            continue;
                        }
                        default: {
                            n = Math.max(baseline, n);
                            n2 = Math.max(preferredSize - baseline, n2);
                            continue;
                        }
                    }
                }
                else {
                    max = Math.max(max, minimumSize);
                }
            }
            return Math.max(max, n + n2);
        }
        
        private void baselineLayout(final int n, final int n2) {
            int prefAscent;
            int prefDescent;
            if (this.baselineAnchoredToTop) {
                prefAscent = this.prefAscent;
                prefDescent = n2 - prefAscent;
            }
            else {
                prefAscent = n2 - this.prefDescent;
                prefDescent = this.prefDescent;
            }
            for (final Spring spring : this.springs) {
                final Alignment alignment = spring.getAlignment();
                if (alignment == null || alignment == Alignment.BASELINE) {
                    final int baseline = spring.getBaseline();
                    if (baseline >= 0) {
                        final int maximumSize = spring.getMaximumSize(1);
                        int preferredSize;
                        final int n3 = preferredSize = spring.getPreferredSize(1);
                        int n4 = 0;
                        switch (spring.getBaselineResizeBehavior()) {
                            case CONSTANT_ASCENT: {
                                n4 = n + prefAscent - baseline;
                                preferredSize = Math.min(prefDescent, maximumSize - baseline) + baseline;
                                break;
                            }
                            case CONSTANT_DESCENT: {
                                preferredSize = Math.min(prefAscent, maximumSize - n3 + baseline) + (n3 - baseline);
                                n4 = n + prefAscent + (n3 - baseline) - preferredSize;
                                break;
                            }
                            default: {
                                n4 = n + prefAscent - baseline;
                                break;
                            }
                        }
                        spring.setSize(1, n4, preferredSize);
                    }
                    else {
                        this.setChildSize(spring, 1, n, n2);
                    }
                }
                else {
                    this.setChildSize(spring, 1, n, n2);
                }
            }
        }
        
        @Override
        int getBaseline() {
            if (this.springs.size() > 1) {
                this.getPreferredSize(1);
                return this.prefAscent;
            }
            if (this.springs.size() == 1) {
                return this.springs.get(0).getBaseline();
            }
            return -1;
        }
        
        @Override
        Component.BaselineResizeBehavior getBaselineResizeBehavior() {
            if (this.springs.size() == 1) {
                return this.springs.get(0).getBaselineResizeBehavior();
            }
            if (this.baselineAnchoredToTop) {
                return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
            }
            return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
        }
        
        private void checkAxis(final int n) {
            if (n == 0) {
                throw new IllegalStateException("Baseline must be used along vertical axis");
            }
        }
    }
    
    private final class ComponentSpring extends Spring
    {
        private Component component;
        private int origin;
        private final int min;
        private final int pref;
        private final int max;
        private int baseline;
        private boolean installed;
        
        private ComponentSpring(final Component component, final int min, final int pref, final int max) {
            this.baseline = -1;
            this.component = component;
            if (component == null) {
                throw new IllegalArgumentException("Component must be non-null");
            }
            checkSize(min, pref, max, true);
            this.min = min;
            this.max = max;
            this.pref = pref;
            GroupLayout.this.getComponentInfo(component);
        }
        
        @Override
        int calculateMinimumSize(final int n) {
            if (this.isLinked(n)) {
                return this.getLinkSize(n, 0);
            }
            return this.calculateNonlinkedMinimumSize(n);
        }
        
        @Override
        int calculatePreferredSize(final int n) {
            if (this.isLinked(n)) {
                return this.getLinkSize(n, 1);
            }
            return Math.min(this.getMaximumSize(n), Math.max(this.getMinimumSize(n), this.calculateNonlinkedPreferredSize(n)));
        }
        
        @Override
        int calculateMaximumSize(final int n) {
            if (this.isLinked(n)) {
                return this.getLinkSize(n, 2);
            }
            return Math.max(this.getMinimumSize(n), this.calculateNonlinkedMaximumSize(n));
        }
        
        boolean isVisible() {
            return GroupLayout.this.getComponentInfo(this.getComponent()).isVisible();
        }
        
        int calculateNonlinkedMinimumSize(final int n) {
            if (!this.isVisible()) {
                return 0;
            }
            if (this.min >= 0) {
                return this.min;
            }
            if (this.min == -2) {
                return this.calculateNonlinkedPreferredSize(n);
            }
            assert this.min == -1;
            return this.getSizeAlongAxis(n, this.component.getMinimumSize());
        }
        
        int calculateNonlinkedPreferredSize(final int n) {
            if (!this.isVisible()) {
                return 0;
            }
            if (this.pref >= 0) {
                return this.pref;
            }
            assert this.pref == -2;
            return this.getSizeAlongAxis(n, this.component.getPreferredSize());
        }
        
        int calculateNonlinkedMaximumSize(final int n) {
            if (!this.isVisible()) {
                return 0;
            }
            if (this.max >= 0) {
                return this.max;
            }
            if (this.max == -2) {
                return this.calculateNonlinkedPreferredSize(n);
            }
            assert this.max == -1;
            return this.getSizeAlongAxis(n, this.component.getMaximumSize());
        }
        
        private int getSizeAlongAxis(final int n, final Dimension dimension) {
            return (n == 0) ? dimension.width : dimension.height;
        }
        
        private int getLinkSize(final int n, final int n2) {
            if (!this.isVisible()) {
                return 0;
            }
            return GroupLayout.this.getComponentInfo(this.component).getLinkSize(n, n2);
        }
        
        @Override
        void setSize(final int n, final int origin, final int n2) {
            super.setSize(n, origin, n2);
            this.origin = origin;
            if (n2 == Integer.MIN_VALUE) {
                this.baseline = -1;
            }
        }
        
        int getOrigin() {
            return this.origin;
        }
        
        void setComponent(final Component component) {
            this.component = component;
        }
        
        Component getComponent() {
            return this.component;
        }
        
        @Override
        int getBaseline() {
            if (this.baseline == -1) {
                final int preferredSize = GroupLayout.this.getComponentInfo(this.component).horizontalSpring.getPreferredSize(0);
                final int preferredSize2 = this.getPreferredSize(1);
                if (preferredSize > 0 && preferredSize2 > 0) {
                    this.baseline = this.component.getBaseline(preferredSize, preferredSize2);
                }
            }
            return this.baseline;
        }
        
        @Override
        Component.BaselineResizeBehavior getBaselineResizeBehavior() {
            return this.getComponent().getBaselineResizeBehavior();
        }
        
        private boolean isLinked(final int n) {
            return GroupLayout.this.getComponentInfo(this.component).isLinked(n);
        }
        
        void installIfNecessary(final int n) {
            if (!this.installed) {
                this.installed = true;
                if (n == 0) {
                    GroupLayout.this.getComponentInfo(this.component).horizontalSpring = this;
                }
                else {
                    GroupLayout.this.getComponentInfo(this.component).verticalSpring = this;
                }
            }
        }
        
        @Override
        boolean willHaveZeroSize(final boolean b) {
            return !this.isVisible();
        }
    }
    
    private class PreferredGapSpring extends Spring
    {
        private final JComponent source;
        private final JComponent target;
        private final LayoutStyle.ComponentPlacement type;
        private final int pref;
        private final int max;
        
        PreferredGapSpring(final JComponent source, final JComponent target, final LayoutStyle.ComponentPlacement type, final int pref, final int max) {
            this.source = source;
            this.target = target;
            this.type = type;
            this.pref = pref;
            this.max = max;
        }
        
        @Override
        int calculateMinimumSize(final int n) {
            return this.getPadding(n);
        }
        
        @Override
        int calculatePreferredSize(final int n) {
            if (this.pref == -1 || this.pref == -2) {
                return this.getMinimumSize(n);
            }
            return Math.min(this.getMaximumSize(n), Math.max(this.getMinimumSize(n), this.pref));
        }
        
        @Override
        int calculateMaximumSize(final int n) {
            if (this.max == -2 || this.max == -1) {
                return this.getPadding(n);
            }
            return Math.max(this.getMinimumSize(n), this.max);
        }
        
        private int getPadding(final int n) {
            int n2;
            if (n == 0) {
                n2 = 3;
            }
            else {
                n2 = 5;
            }
            return GroupLayout.this.getLayoutStyle0().getPreferredGap(this.source, this.target, this.type, n2, GroupLayout.this.host);
        }
        
        @Override
        boolean willHaveZeroSize(final boolean b) {
            return false;
        }
    }
    
    private class GapSpring extends Spring
    {
        private final int min;
        private final int pref;
        private final int max;
        
        GapSpring(final int min, final int pref, final int max) {
            checkSize(min, pref, max, false);
            this.min = min;
            this.pref = pref;
            this.max = max;
        }
        
        @Override
        int calculateMinimumSize(final int n) {
            if (this.min == -2) {
                return this.getPreferredSize(n);
            }
            return this.min;
        }
        
        @Override
        int calculatePreferredSize(final int n) {
            return this.pref;
        }
        
        @Override
        int calculateMaximumSize(final int n) {
            if (this.max == -2) {
                return this.getPreferredSize(n);
            }
            return this.max;
        }
        
        @Override
        boolean willHaveZeroSize(final boolean b) {
            return false;
        }
    }
    
    private class AutoPreferredGapSpring extends Spring
    {
        List<ComponentSpring> sources;
        ComponentSpring source;
        private List<AutoPreferredGapMatch> matches;
        int size;
        int lastSize;
        private final int pref;
        private final int max;
        private LayoutStyle.ComponentPlacement type;
        private boolean userCreated;
        
        private AutoPreferredGapSpring() {
            this.pref = -2;
            this.max = -2;
            this.type = LayoutStyle.ComponentPlacement.RELATED;
        }
        
        AutoPreferredGapSpring(final int pref, final int max) {
            this.pref = pref;
            this.max = max;
        }
        
        AutoPreferredGapSpring(final LayoutStyle.ComponentPlacement type, final int pref, final int max) {
            this.type = type;
            this.pref = pref;
            this.max = max;
            this.userCreated = true;
        }
        
        public void setSource(final ComponentSpring source) {
            this.source = source;
        }
        
        public void setSources(final List<ComponentSpring> list) {
            this.sources = new ArrayList<ComponentSpring>(list);
        }
        
        public void setUserCreated(final boolean userCreated) {
            this.userCreated = userCreated;
        }
        
        public boolean getUserCreated() {
            return this.userCreated;
        }
        
        @Override
        void unset() {
            this.lastSize = this.getSize();
            super.unset();
            this.size = 0;
        }
        
        public void reset() {
            this.size = 0;
            this.sources = null;
            this.source = null;
            this.matches = null;
        }
        
        public void calculatePadding(final int n) {
            this.size = Integer.MIN_VALUE;
            int max = Integer.MIN_VALUE;
            if (this.matches != null) {
                final LayoutStyle access$800 = GroupLayout.this.getLayoutStyle0();
                int n2;
                if (n == 0) {
                    if (GroupLayout.this.isLeftToRight()) {
                        n2 = 3;
                    }
                    else {
                        n2 = 7;
                    }
                }
                else {
                    n2 = 5;
                }
                for (int i = this.matches.size() - 1; i >= 0; --i) {
                    final AutoPreferredGapMatch autoPreferredGapMatch = this.matches.get(i);
                    max = Math.max(max, this.calculatePadding(access$800, n2, autoPreferredGapMatch.source, autoPreferredGapMatch.target));
                }
            }
            if (this.size == Integer.MIN_VALUE) {
                this.size = 0;
            }
            if (max == Integer.MIN_VALUE) {
                max = 0;
            }
            if (this.lastSize != Integer.MIN_VALUE) {
                this.size += Math.min(max, this.lastSize);
            }
        }
        
        private int calculatePadding(final LayoutStyle layoutStyle, final int n, final ComponentSpring componentSpring, final ComponentSpring componentSpring2) {
            final int n2 = componentSpring2.getOrigin() - (componentSpring.getOrigin() + componentSpring.getSize());
            if (n2 >= 0) {
                int preferredGap;
                if (componentSpring.getComponent() instanceof JComponent && componentSpring2.getComponent() instanceof JComponent) {
                    preferredGap = layoutStyle.getPreferredGap((JComponent)componentSpring.getComponent(), (JComponent)componentSpring2.getComponent(), this.type, n, GroupLayout.this.host);
                }
                else {
                    preferredGap = 10;
                }
                if (preferredGap > n2) {
                    this.size = Math.max(this.size, preferredGap - n2);
                }
                return preferredGap;
            }
            return 0;
        }
        
        public void addTarget(final ComponentSpring componentSpring, final int n) {
            final int n2 = (n == 0) ? 1 : 0;
            if (this.source != null) {
                if (GroupLayout.this.areParallelSiblings(this.source.getComponent(), componentSpring.getComponent(), n2)) {
                    this.addValidTarget(this.source, componentSpring);
                }
            }
            else {
                final Component component = componentSpring.getComponent();
                for (int i = this.sources.size() - 1; i >= 0; --i) {
                    final ComponentSpring componentSpring2 = this.sources.get(i);
                    if (GroupLayout.this.areParallelSiblings(componentSpring2.getComponent(), component, n2)) {
                        this.addValidTarget(componentSpring2, componentSpring);
                    }
                }
            }
        }
        
        private void addValidTarget(final ComponentSpring componentSpring, final ComponentSpring componentSpring2) {
            if (this.matches == null) {
                this.matches = new ArrayList<AutoPreferredGapMatch>(1);
            }
            this.matches.add(new AutoPreferredGapMatch(componentSpring, componentSpring2));
        }
        
        @Override
        int calculateMinimumSize(final int n) {
            return this.size;
        }
        
        @Override
        int calculatePreferredSize(final int n) {
            if (this.pref == -2 || this.pref == -1) {
                return this.size;
            }
            return Math.max(this.size, this.pref);
        }
        
        @Override
        int calculateMaximumSize(final int n) {
            if (this.max >= 0) {
                return Math.max(this.getPreferredSize(n), this.max);
            }
            return this.size;
        }
        
        String getMatchDescription() {
            return (this.matches == null) ? "" : this.matches.toString();
        }
        
        @Override
        public String toString() {
            return super.toString() + this.getMatchDescription();
        }
        
        @Override
        boolean willHaveZeroSize(final boolean b) {
            return b;
        }
    }
    
    private static final class AutoPreferredGapMatch
    {
        public final ComponentSpring source;
        public final ComponentSpring target;
        
        AutoPreferredGapMatch(final ComponentSpring source, final ComponentSpring target) {
            this.source = source;
            this.target = target;
        }
        
        private String toString(final ComponentSpring componentSpring) {
            return componentSpring.getComponent().getName();
        }
        
        @Override
        public String toString() {
            return "[" + this.toString(this.source) + "-" + this.toString(this.target) + "]";
        }
    }
    
    private class ContainerAutoPreferredGapSpring extends AutoPreferredGapSpring
    {
        private List<ComponentSpring> targets;
        
        ContainerAutoPreferredGapSpring() {
            this.setUserCreated(true);
        }
        
        ContainerAutoPreferredGapSpring(final int n, final int n2) {
            super(n, n2);
            this.setUserCreated(true);
        }
        
        @Override
        public void addTarget(final ComponentSpring componentSpring, final int n) {
            if (this.targets == null) {
                this.targets = new ArrayList<ComponentSpring>(1);
            }
            this.targets.add(componentSpring);
        }
        
        @Override
        public void calculatePadding(final int n) {
            final LayoutStyle access$800 = GroupLayout.this.getLayoutStyle0();
            int n2 = 0;
            this.size = 0;
            if (this.targets != null) {
                int n3;
                if (n == 0) {
                    if (GroupLayout.this.isLeftToRight()) {
                        n3 = 7;
                    }
                    else {
                        n3 = 3;
                    }
                }
                else {
                    n3 = 5;
                }
                for (int i = this.targets.size() - 1; i >= 0; --i) {
                    final ComponentSpring componentSpring = this.targets.get(i);
                    int n4 = 10;
                    if (componentSpring.getComponent() instanceof JComponent) {
                        final int containerGap = access$800.getContainerGap((JComponent)componentSpring.getComponent(), n3, GroupLayout.this.host);
                        n2 = Math.max(containerGap, n2);
                        n4 = containerGap - componentSpring.getOrigin();
                    }
                    else {
                        n2 = Math.max(n4, n2);
                    }
                    this.size = Math.max(this.size, n4);
                }
            }
            else {
                int n5;
                if (n == 0) {
                    if (GroupLayout.this.isLeftToRight()) {
                        n5 = 3;
                    }
                    else {
                        n5 = 7;
                    }
                }
                else {
                    n5 = 5;
                }
                if (this.sources != null) {
                    for (int j = this.sources.size() - 1; j >= 0; --j) {
                        n2 = Math.max(n2, this.updateSize(access$800, this.sources.get(j), n5));
                    }
                }
                else if (this.source != null) {
                    n2 = this.updateSize(access$800, this.source, n5);
                }
            }
            if (this.lastSize != Integer.MIN_VALUE) {
                this.size += Math.min(n2, this.lastSize);
            }
        }
        
        private int updateSize(final LayoutStyle layoutStyle, final ComponentSpring componentSpring, final int n) {
            int containerGap = 10;
            if (componentSpring.getComponent() instanceof JComponent) {
                containerGap = layoutStyle.getContainerGap((JComponent)componentSpring.getComponent(), n, GroupLayout.this.host);
            }
            this.size = Math.max(this.size, containerGap - Math.max(0, this.getParent().getSize() - componentSpring.getSize() - componentSpring.getOrigin()));
            return containerGap;
        }
        
        @Override
        String getMatchDescription() {
            if (this.targets != null) {
                return "leading: " + this.targets.toString();
            }
            if (this.sources != null) {
                return "trailing: " + this.sources.toString();
            }
            return "--";
        }
    }
    
    private static class LinkInfo
    {
        private final int axis;
        private final List<ComponentInfo> linked;
        private int size;
        
        LinkInfo(final int axis) {
            this.linked = new ArrayList<ComponentInfo>();
            this.size = Integer.MIN_VALUE;
            this.axis = axis;
        }
        
        public void add(final ComponentInfo componentInfo) {
            final LinkInfo access$1100 = componentInfo.getLinkInfo(this.axis, false);
            if (access$1100 == null) {
                this.linked.add(componentInfo);
                componentInfo.setLinkInfo(this.axis, this);
            }
            else if (access$1100 != this) {
                this.linked.addAll(access$1100.linked);
                final Iterator<ComponentInfo> iterator = access$1100.linked.iterator();
                while (iterator.hasNext()) {
                    iterator.next().setLinkInfo(this.axis, this);
                }
            }
            this.clearCachedSize();
        }
        
        public void remove(final ComponentInfo componentInfo) {
            this.linked.remove(componentInfo);
            componentInfo.setLinkInfo(this.axis, null);
            if (this.linked.size() == 1) {
                this.linked.get(0).setLinkInfo(this.axis, null);
            }
            this.clearCachedSize();
        }
        
        public void clearCachedSize() {
            this.size = Integer.MIN_VALUE;
        }
        
        public int getSize(final int n) {
            if (this.size == Integer.MIN_VALUE) {
                this.size = this.calculateLinkedSize(n);
            }
            return this.size;
        }
        
        private int calculateLinkedSize(final int n) {
            int max = 0;
            for (final ComponentInfo componentInfo : this.linked) {
                ComponentSpring componentSpring;
                if (n == 0) {
                    componentSpring = componentInfo.horizontalSpring;
                }
                else {
                    assert n == 1;
                    componentSpring = componentInfo.verticalSpring;
                }
                max = Math.max(max, componentSpring.calculateNonlinkedPreferredSize(n));
            }
            return max;
        }
    }
    
    private class ComponentInfo
    {
        private Component component;
        ComponentSpring horizontalSpring;
        ComponentSpring verticalSpring;
        private LinkInfo horizontalMaster;
        private LinkInfo verticalMaster;
        private boolean visible;
        private Boolean honorsVisibility;
        
        ComponentInfo(final Component component) {
            this.component = component;
            this.updateVisibility();
        }
        
        public void dispose() {
            this.removeSpring(this.horizontalSpring);
            this.horizontalSpring = null;
            this.removeSpring(this.verticalSpring);
            this.verticalSpring = null;
            if (this.horizontalMaster != null) {
                this.horizontalMaster.remove(this);
            }
            if (this.verticalMaster != null) {
                this.verticalMaster.remove(this);
            }
        }
        
        void setHonorsVisibility(final Boolean honorsVisibility) {
            this.honorsVisibility = honorsVisibility;
        }
        
        private void removeSpring(final Spring spring) {
            if (spring != null) {
                ((Group)spring.getParent()).springs.remove(spring);
            }
        }
        
        public boolean isVisible() {
            return this.visible;
        }
        
        boolean updateVisibility() {
            boolean b;
            if (this.honorsVisibility == null) {
                b = GroupLayout.this.getHonorsVisibility();
            }
            else {
                b = this.honorsVisibility;
            }
            final boolean visible = !b || this.component.isVisible();
            if (this.visible != visible) {
                this.visible = visible;
                return true;
            }
            return false;
        }
        
        public void setBounds(final Insets insets, final int n, final boolean b) {
            int origin = this.horizontalSpring.getOrigin();
            final int size = this.horizontalSpring.getSize();
            final int origin2 = this.verticalSpring.getOrigin();
            final int size2 = this.verticalSpring.getSize();
            if (!b) {
                origin = n - origin - size;
            }
            this.component.setBounds(origin + insets.left, origin2 + insets.top, size, size2);
        }
        
        public void setComponent(final Component component) {
            this.component = component;
            if (this.horizontalSpring != null) {
                this.horizontalSpring.setComponent(component);
            }
            if (this.verticalSpring != null) {
                this.verticalSpring.setComponent(component);
            }
        }
        
        public Component getComponent() {
            return this.component;
        }
        
        public boolean isLinked(final int n) {
            if (n == 0) {
                return this.horizontalMaster != null;
            }
            assert n == 1;
            return this.verticalMaster != null;
        }
        
        private void setLinkInfo(final int n, final LinkInfo linkInfo) {
            if (n == 0) {
                this.horizontalMaster = linkInfo;
            }
            else {
                assert n == 1;
                this.verticalMaster = linkInfo;
            }
        }
        
        public LinkInfo getLinkInfo(final int n) {
            return this.getLinkInfo(n, true);
        }
        
        private LinkInfo getLinkInfo(final int n, final boolean b) {
            if (n == 0) {
                if (this.horizontalMaster == null && b) {
                    new LinkInfo(0).add(this);
                }
                return this.horizontalMaster;
            }
            assert n == 1;
            if (this.verticalMaster == null && b) {
                new LinkInfo(1).add(this);
            }
            return this.verticalMaster;
        }
        
        public void clearCachedSize() {
            if (this.horizontalMaster != null) {
                this.horizontalMaster.clearCachedSize();
            }
            if (this.verticalMaster != null) {
                this.verticalMaster.clearCachedSize();
            }
        }
        
        int getLinkSize(final int n, final int n2) {
            if (n == 0) {
                return this.horizontalMaster.getSize(n);
            }
            assert n == 1;
            return this.verticalMaster.getSize(n);
        }
    }
}
