package javax.swing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Container;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.awt.Component;
import java.util.Map;
import java.awt.LayoutManager2;

public class SpringLayout implements LayoutManager2
{
    private Map<Component, Constraints> componentConstraints;
    private Spring cyclicReference;
    private Set<Spring> cyclicSprings;
    private Set<Spring> acyclicSprings;
    public static final String NORTH = "North";
    public static final String SOUTH = "South";
    public static final String EAST = "East";
    public static final String WEST = "West";
    public static final String HORIZONTAL_CENTER = "HorizontalCenter";
    public static final String VERTICAL_CENTER = "VerticalCenter";
    public static final String BASELINE = "Baseline";
    public static final String WIDTH = "Width";
    public static final String HEIGHT = "Height";
    private static String[] ALL_HORIZONTAL;
    private static String[] ALL_VERTICAL;
    
    public SpringLayout() {
        this.componentConstraints = new HashMap<Component, Constraints>();
        this.cyclicReference = Spring.constant(Integer.MIN_VALUE);
    }
    
    private void resetCyclicStatuses() {
        this.cyclicSprings = new HashSet<Spring>();
        this.acyclicSprings = new HashSet<Spring>();
    }
    
    private void setParent(final Container container) {
        this.resetCyclicStatuses();
        final Constraints constraints = this.getConstraints(container);
        constraints.setX(Spring.constant(0));
        constraints.setY(Spring.constant(0));
        final Spring width = constraints.getWidth();
        if (width instanceof Spring.WidthSpring && ((Spring.WidthSpring)width).c == container) {
            constraints.setWidth(Spring.constant(0, 0, Integer.MAX_VALUE));
        }
        final Spring height = constraints.getHeight();
        if (height instanceof Spring.HeightSpring && ((Spring.HeightSpring)height).c == container) {
            constraints.setHeight(Spring.constant(0, 0, Integer.MAX_VALUE));
        }
    }
    
    boolean isCyclic(final Spring spring) {
        if (spring == null) {
            return false;
        }
        if (this.cyclicSprings.contains(spring)) {
            return true;
        }
        if (this.acyclicSprings.contains(spring)) {
            return false;
        }
        this.cyclicSprings.add(spring);
        final boolean cyclic = spring.isCyclic(this);
        if (!cyclic) {
            this.acyclicSprings.add(spring);
            this.cyclicSprings.remove(spring);
        }
        else {
            System.err.println(spring + " is cyclic. ");
        }
        return cyclic;
    }
    
    private Spring abandonCycles(final Spring spring) {
        return this.isCyclic(spring) ? this.cyclicReference : spring;
    }
    
    @Override
    public void addLayoutComponent(final String s, final Component component) {
    }
    
    @Override
    public void removeLayoutComponent(final Component component) {
        this.componentConstraints.remove(component);
    }
    
    private static Dimension addInsets(final int n, final int n2, final Container container) {
        final Insets insets = container.getInsets();
        return new Dimension(n + insets.left + insets.right, n2 + insets.top + insets.bottom);
    }
    
    @Override
    public Dimension minimumLayoutSize(final Container parent) {
        this.setParent(parent);
        final Constraints constraints = this.getConstraints(parent);
        return addInsets(this.abandonCycles(constraints.getWidth()).getMinimumValue(), this.abandonCycles(constraints.getHeight()).getMinimumValue(), parent);
    }
    
    @Override
    public Dimension preferredLayoutSize(final Container parent) {
        this.setParent(parent);
        final Constraints constraints = this.getConstraints(parent);
        return addInsets(this.abandonCycles(constraints.getWidth()).getPreferredValue(), this.abandonCycles(constraints.getHeight()).getPreferredValue(), parent);
    }
    
    @Override
    public Dimension maximumLayoutSize(final Container parent) {
        this.setParent(parent);
        final Constraints constraints = this.getConstraints(parent);
        return addInsets(this.abandonCycles(constraints.getWidth()).getMaximumValue(), this.abandonCycles(constraints.getHeight()).getMaximumValue(), parent);
    }
    
    @Override
    public void addLayoutComponent(final Component component, final Object o) {
        if (o instanceof Constraints) {
            this.putConstraints(component, (Constraints)o);
        }
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
    
    public void putConstraint(final String s, final Component component, final int n, final String s2, final Component component2) {
        this.putConstraint(s, component, Spring.constant(n), s2, component2);
    }
    
    public void putConstraint(final String s, final Component component, final Spring spring, final String s2, final Component component2) {
        this.putConstraint(s, component, Spring.sum(spring, this.getConstraint(s2, component2)));
    }
    
    private void putConstraint(final String s, final Component component, final Spring spring) {
        if (spring != null) {
            this.getConstraints(component).setConstraint(s, spring);
        }
    }
    
    private Constraints applyDefaults(final Component component, Constraints constraints) {
        if (constraints == null) {
            constraints = new Constraints();
        }
        if (constraints.c == null) {
            constraints.c = component;
        }
        if (constraints.horizontalHistory.size() < 2) {
            this.applyDefaults(constraints, "West", Spring.constant(0), "Width", Spring.width(component), constraints.horizontalHistory);
        }
        if (constraints.verticalHistory.size() < 2) {
            this.applyDefaults(constraints, "North", Spring.constant(0), "Height", Spring.height(component), constraints.verticalHistory);
        }
        return constraints;
    }
    
    private void applyDefaults(final Constraints constraints, final String s, final Spring spring, final String s2, final Spring spring2, final List<String> list) {
        if (list.size() == 0) {
            constraints.setConstraint(s, spring);
            constraints.setConstraint(s2, spring2);
        }
        else {
            if (constraints.getConstraint(s2) == null) {
                constraints.setConstraint(s2, spring2);
            }
            else {
                constraints.setConstraint(s, spring);
            }
            Collections.rotate(list, 1);
        }
    }
    
    private void putConstraints(final Component component, final Constraints constraints) {
        this.componentConstraints.put(component, this.applyDefaults(component, constraints));
    }
    
    public Constraints getConstraints(final Component component) {
        Constraints constraints = this.componentConstraints.get(component);
        if (constraints == null) {
            if (component instanceof JComponent) {
                final Object clientProperty = ((JComponent)component).getClientProperty(SpringLayout.class);
                if (clientProperty instanceof Constraints) {
                    return this.applyDefaults(component, (Constraints)clientProperty);
                }
            }
            constraints = new Constraints();
            this.putConstraints(component, constraints);
        }
        return constraints;
    }
    
    public Spring getConstraint(String intern, final Component component) {
        intern = intern.intern();
        return new SpringProxy(intern, component, this);
    }
    
    @Override
    public void layoutContainer(final Container parent) {
        this.setParent(parent);
        final int componentCount = parent.getComponentCount();
        this.getConstraints(parent).reset();
        for (int i = 0; i < componentCount; ++i) {
            this.getConstraints(parent.getComponent(i)).reset();
        }
        final Insets insets = parent.getInsets();
        final Constraints constraints = this.getConstraints(parent);
        this.abandonCycles(constraints.getX()).setValue(0);
        this.abandonCycles(constraints.getY()).setValue(0);
        this.abandonCycles(constraints.getWidth()).setValue(parent.getWidth() - insets.left - insets.right);
        this.abandonCycles(constraints.getHeight()).setValue(parent.getHeight() - insets.top - insets.bottom);
        for (int j = 0; j < componentCount; ++j) {
            final Component component = parent.getComponent(j);
            final Constraints constraints2 = this.getConstraints(component);
            component.setBounds(insets.left + this.abandonCycles(constraints2.getX()).getValue(), insets.top + this.abandonCycles(constraints2.getY()).getValue(), this.abandonCycles(constraints2.getWidth()).getValue(), this.abandonCycles(constraints2.getHeight()).getValue());
        }
    }
    
    static {
        SpringLayout.ALL_HORIZONTAL = new String[] { "West", "Width", "East", "HorizontalCenter" };
        SpringLayout.ALL_VERTICAL = new String[] { "North", "Height", "South", "VerticalCenter", "Baseline" };
    }
    
    public static class Constraints
    {
        private Spring x;
        private Spring y;
        private Spring width;
        private Spring height;
        private Spring east;
        private Spring south;
        private Spring horizontalCenter;
        private Spring verticalCenter;
        private Spring baseline;
        private List<String> horizontalHistory;
        private List<String> verticalHistory;
        private Component c;
        
        public Constraints() {
            this.horizontalHistory = new ArrayList<String>(2);
            this.verticalHistory = new ArrayList<String>(2);
        }
        
        public Constraints(final Spring x, final Spring y) {
            this.horizontalHistory = new ArrayList<String>(2);
            this.verticalHistory = new ArrayList<String>(2);
            this.setX(x);
            this.setY(y);
        }
        
        public Constraints(final Spring x, final Spring y, final Spring width, final Spring height) {
            this.horizontalHistory = new ArrayList<String>(2);
            this.verticalHistory = new ArrayList<String>(2);
            this.setX(x);
            this.setY(y);
            this.setWidth(width);
            this.setHeight(height);
        }
        
        public Constraints(final Component c) {
            this.horizontalHistory = new ArrayList<String>(2);
            this.verticalHistory = new ArrayList<String>(2);
            this.c = c;
            this.setX(Spring.constant(c.getX()));
            this.setY(Spring.constant(c.getY()));
            this.setWidth(Spring.width(c));
            this.setHeight(Spring.height(c));
        }
        
        private void pushConstraint(final String s, final Spring spring, final boolean b) {
            boolean b2 = true;
            final List<String> list = b ? this.horizontalHistory : this.verticalHistory;
            if (list.contains(s)) {
                list.remove(s);
                b2 = false;
            }
            else if (list.size() == 2 && spring != null) {
                list.remove(0);
                b2 = false;
            }
            if (spring != null) {
                list.add(s);
            }
            if (!b2) {
                for (final String s2 : b ? SpringLayout.ALL_HORIZONTAL : SpringLayout.ALL_VERTICAL) {
                    if (!list.contains(s2)) {
                        this.setConstraint(s2, null);
                    }
                }
            }
        }
        
        private Spring sum(final Spring spring, final Spring spring2) {
            return (spring == null || spring2 == null) ? null : Spring.sum(spring, spring2);
        }
        
        private Spring difference(final Spring spring, final Spring spring2) {
            return (spring == null || spring2 == null) ? null : Spring.difference(spring, spring2);
        }
        
        private Spring scale(final Spring spring, final float n) {
            return (spring == null) ? null : Spring.scale(spring, n);
        }
        
        private int getBaselineFromHeight(final int n) {
            if (n < 0) {
                return -this.c.getBaseline(this.c.getPreferredSize().width, -n);
            }
            return this.c.getBaseline(this.c.getPreferredSize().width, n);
        }
        
        private int getHeightFromBaseLine(final int n) {
            final Dimension preferredSize = this.c.getPreferredSize();
            final int height = preferredSize.height;
            final int baseline = this.c.getBaseline(preferredSize.width, height);
            if (baseline == n) {
                return height;
            }
            switch (this.c.getBaselineResizeBehavior()) {
                case CONSTANT_DESCENT: {
                    return height + (n - baseline);
                }
                case CENTER_OFFSET: {
                    return height + 2 * (n - baseline);
                }
                default: {
                    return Integer.MIN_VALUE;
                }
            }
        }
        
        private Spring heightToRelativeBaseline(final Spring spring) {
            return new Spring.SpringMap(spring) {
                @Override
                protected int map(final int n) {
                    return Constraints.this.getBaselineFromHeight(n);
                }
                
                @Override
                protected int inv(final int n) {
                    return Constraints.this.getHeightFromBaseLine(n);
                }
            };
        }
        
        private Spring relativeBaselineToHeight(final Spring spring) {
            return new Spring.SpringMap(spring) {
                @Override
                protected int map(final int n) {
                    return Constraints.this.getHeightFromBaseLine(n);
                }
                
                @Override
                protected int inv(final int n) {
                    return Constraints.this.getBaselineFromHeight(n);
                }
            };
        }
        
        private boolean defined(final List list, final String s, final String s2) {
            return list.contains(s) && list.contains(s2);
        }
        
        public void setX(final Spring x) {
            this.pushConstraint("West", this.x = x, true);
        }
        
        public Spring getX() {
            if (this.x == null) {
                if (this.defined(this.horizontalHistory, "East", "Width")) {
                    this.x = this.difference(this.east, this.width);
                }
                else if (this.defined(this.horizontalHistory, "HorizontalCenter", "Width")) {
                    this.x = this.difference(this.horizontalCenter, this.scale(this.width, 0.5f));
                }
                else if (this.defined(this.horizontalHistory, "HorizontalCenter", "East")) {
                    this.x = this.difference(this.scale(this.horizontalCenter, 2.0f), this.east);
                }
            }
            return this.x;
        }
        
        public void setY(final Spring y) {
            this.pushConstraint("North", this.y = y, false);
        }
        
        public Spring getY() {
            if (this.y == null) {
                if (this.defined(this.verticalHistory, "South", "Height")) {
                    this.y = this.difference(this.south, this.height);
                }
                else if (this.defined(this.verticalHistory, "VerticalCenter", "Height")) {
                    this.y = this.difference(this.verticalCenter, this.scale(this.height, 0.5f));
                }
                else if (this.defined(this.verticalHistory, "VerticalCenter", "South")) {
                    this.y = this.difference(this.scale(this.verticalCenter, 2.0f), this.south);
                }
                else if (this.defined(this.verticalHistory, "Baseline", "Height")) {
                    this.y = this.difference(this.baseline, this.heightToRelativeBaseline(this.height));
                }
                else if (this.defined(this.verticalHistory, "Baseline", "South")) {
                    this.y = this.scale(this.difference(this.baseline, this.heightToRelativeBaseline(this.south)), 2.0f);
                }
            }
            return this.y;
        }
        
        public void setWidth(final Spring width) {
            this.pushConstraint("Width", this.width = width, true);
        }
        
        public Spring getWidth() {
            if (this.width == null) {
                if (this.horizontalHistory.contains("East")) {
                    this.width = this.difference(this.east, this.getX());
                }
                else if (this.horizontalHistory.contains("HorizontalCenter")) {
                    this.width = this.scale(this.difference(this.horizontalCenter, this.getX()), 2.0f);
                }
            }
            return this.width;
        }
        
        public void setHeight(final Spring height) {
            this.pushConstraint("Height", this.height = height, false);
        }
        
        public Spring getHeight() {
            if (this.height == null) {
                if (this.verticalHistory.contains("South")) {
                    this.height = this.difference(this.south, this.getY());
                }
                else if (this.verticalHistory.contains("VerticalCenter")) {
                    this.height = this.scale(this.difference(this.verticalCenter, this.getY()), 2.0f);
                }
                else if (this.verticalHistory.contains("Baseline")) {
                    this.height = this.relativeBaselineToHeight(this.difference(this.baseline, this.getY()));
                }
            }
            return this.height;
        }
        
        private void setEast(final Spring east) {
            this.pushConstraint("East", this.east = east, true);
        }
        
        private Spring getEast() {
            if (this.east == null) {
                this.east = this.sum(this.getX(), this.getWidth());
            }
            return this.east;
        }
        
        private void setSouth(final Spring south) {
            this.pushConstraint("South", this.south = south, false);
        }
        
        private Spring getSouth() {
            if (this.south == null) {
                this.south = this.sum(this.getY(), this.getHeight());
            }
            return this.south;
        }
        
        private Spring getHorizontalCenter() {
            if (this.horizontalCenter == null) {
                this.horizontalCenter = this.sum(this.getX(), this.scale(this.getWidth(), 0.5f));
            }
            return this.horizontalCenter;
        }
        
        private void setHorizontalCenter(final Spring horizontalCenter) {
            this.pushConstraint("HorizontalCenter", this.horizontalCenter = horizontalCenter, true);
        }
        
        private Spring getVerticalCenter() {
            if (this.verticalCenter == null) {
                this.verticalCenter = this.sum(this.getY(), this.scale(this.getHeight(), 0.5f));
            }
            return this.verticalCenter;
        }
        
        private void setVerticalCenter(final Spring verticalCenter) {
            this.pushConstraint("VerticalCenter", this.verticalCenter = verticalCenter, false);
        }
        
        private Spring getBaseline() {
            if (this.baseline == null) {
                this.baseline = this.sum(this.getY(), this.heightToRelativeBaseline(this.getHeight()));
            }
            return this.baseline;
        }
        
        private void setBaseline(final Spring baseline) {
            this.pushConstraint("Baseline", this.baseline = baseline, false);
        }
        
        public void setConstraint(String intern, final Spring baseline) {
            intern = intern.intern();
            if (intern == "West") {
                this.setX(baseline);
            }
            else if (intern == "North") {
                this.setY(baseline);
            }
            else if (intern == "East") {
                this.setEast(baseline);
            }
            else if (intern == "South") {
                this.setSouth(baseline);
            }
            else if (intern == "HorizontalCenter") {
                this.setHorizontalCenter(baseline);
            }
            else if (intern == "Width") {
                this.setWidth(baseline);
            }
            else if (intern == "Height") {
                this.setHeight(baseline);
            }
            else if (intern == "VerticalCenter") {
                this.setVerticalCenter(baseline);
            }
            else if (intern == "Baseline") {
                this.setBaseline(baseline);
            }
        }
        
        public Spring getConstraint(String intern) {
            intern = intern.intern();
            return (intern == "West") ? this.getX() : ((intern == "North") ? this.getY() : ((intern == "East") ? this.getEast() : ((intern == "South") ? this.getSouth() : ((intern == "Width") ? this.getWidth() : ((intern == "Height") ? this.getHeight() : ((intern == "HorizontalCenter") ? this.getHorizontalCenter() : ((intern == "VerticalCenter") ? this.getVerticalCenter() : ((intern == "Baseline") ? this.getBaseline() : null))))))));
        }
        
        void reset() {
            for (final Spring spring : new Spring[] { this.x, this.y, this.width, this.height, this.east, this.south, this.horizontalCenter, this.verticalCenter, this.baseline }) {
                if (spring != null) {
                    spring.setValue(Integer.MIN_VALUE);
                }
            }
        }
    }
    
    private static class SpringProxy extends Spring
    {
        private String edgeName;
        private Component c;
        private SpringLayout l;
        
        public SpringProxy(final String edgeName, final Component c, final SpringLayout l) {
            this.edgeName = edgeName;
            this.c = c;
            this.l = l;
        }
        
        private Spring getConstraint() {
            return this.l.getConstraints(this.c).getConstraint(this.edgeName);
        }
        
        @Override
        public int getMinimumValue() {
            return this.getConstraint().getMinimumValue();
        }
        
        @Override
        public int getPreferredValue() {
            return this.getConstraint().getPreferredValue();
        }
        
        @Override
        public int getMaximumValue() {
            return this.getConstraint().getMaximumValue();
        }
        
        @Override
        public int getValue() {
            return this.getConstraint().getValue();
        }
        
        @Override
        public void setValue(final int value) {
            this.getConstraint().setValue(value);
        }
        
        @Override
        boolean isCyclic(final SpringLayout springLayout) {
            return springLayout.isCyclic(this.getConstraint());
        }
        
        @Override
        public String toString() {
            return "SpringProxy for " + this.edgeName + " edge of " + this.c.getName() + ".";
        }
    }
}
