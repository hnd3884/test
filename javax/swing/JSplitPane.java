package javax.swing;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleValue;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.Graphics;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SplitPaneUI;
import java.awt.LayoutManager;
import java.beans.ConstructorProperties;
import java.awt.Component;
import javax.accessibility.Accessible;

public class JSplitPane extends JComponent implements Accessible
{
    private static final String uiClassID = "SplitPaneUI";
    public static final int VERTICAL_SPLIT = 0;
    public static final int HORIZONTAL_SPLIT = 1;
    public static final String LEFT = "left";
    public static final String RIGHT = "right";
    public static final String TOP = "top";
    public static final String BOTTOM = "bottom";
    public static final String DIVIDER = "divider";
    public static final String ORIENTATION_PROPERTY = "orientation";
    public static final String CONTINUOUS_LAYOUT_PROPERTY = "continuousLayout";
    public static final String DIVIDER_SIZE_PROPERTY = "dividerSize";
    public static final String ONE_TOUCH_EXPANDABLE_PROPERTY = "oneTouchExpandable";
    public static final String LAST_DIVIDER_LOCATION_PROPERTY = "lastDividerLocation";
    public static final String DIVIDER_LOCATION_PROPERTY = "dividerLocation";
    public static final String RESIZE_WEIGHT_PROPERTY = "resizeWeight";
    protected int orientation;
    protected boolean continuousLayout;
    protected Component leftComponent;
    protected Component rightComponent;
    protected int dividerSize;
    private boolean dividerSizeSet;
    protected boolean oneTouchExpandable;
    private boolean oneTouchExpandableSet;
    protected int lastDividerLocation;
    private double resizeWeight;
    private int dividerLocation;
    
    public JSplitPane() {
        this(1, UIManager.getBoolean("SplitPane.continuousLayout"), new JButton(UIManager.getString("SplitPane.leftButtonText")), new JButton(UIManager.getString("SplitPane.rightButtonText")));
    }
    
    @ConstructorProperties({ "orientation" })
    public JSplitPane(final int n) {
        this(n, UIManager.getBoolean("SplitPane.continuousLayout"));
    }
    
    public JSplitPane(final int n, final boolean b) {
        this(n, b, null, null);
    }
    
    public JSplitPane(final int n, final Component component, final Component component2) {
        this(n, UIManager.getBoolean("SplitPane.continuousLayout"), component, component2);
    }
    
    public JSplitPane(final int orientation, final boolean continuousLayout, final Component leftComponent, final Component rightComponent) {
        this.dividerSizeSet = false;
        this.dividerLocation = -1;
        this.setLayout(null);
        this.setUIProperty("opaque", Boolean.TRUE);
        this.orientation = orientation;
        if (this.orientation != 1 && this.orientation != 0) {
            throw new IllegalArgumentException("cannot create JSplitPane, orientation must be one of JSplitPane.HORIZONTAL_SPLIT or JSplitPane.VERTICAL_SPLIT");
        }
        this.continuousLayout = continuousLayout;
        if (leftComponent != null) {
            this.setLeftComponent(leftComponent);
        }
        if (rightComponent != null) {
            this.setRightComponent(rightComponent);
        }
        this.updateUI();
    }
    
    public void setUI(final SplitPaneUI ui) {
        if (this.ui != ui) {
            super.setUI(ui);
            this.revalidate();
        }
    }
    
    public SplitPaneUI getUI() {
        return (SplitPaneUI)this.ui;
    }
    
    @Override
    public void updateUI() {
        this.setUI((SplitPaneUI)UIManager.getUI(this));
        this.revalidate();
    }
    
    @Override
    public String getUIClassID() {
        return "SplitPaneUI";
    }
    
    public void setDividerSize(final int dividerSize) {
        final int dividerSize2 = this.dividerSize;
        this.dividerSizeSet = true;
        if (dividerSize2 != dividerSize) {
            this.firePropertyChange("dividerSize", dividerSize2, this.dividerSize = dividerSize);
        }
    }
    
    public int getDividerSize() {
        return this.dividerSize;
    }
    
    public void setLeftComponent(final Component component) {
        if (component == null) {
            if (this.leftComponent != null) {
                this.remove(this.leftComponent);
                this.leftComponent = null;
            }
        }
        else {
            this.add(component, "left");
        }
    }
    
    public Component getLeftComponent() {
        return this.leftComponent;
    }
    
    public void setTopComponent(final Component leftComponent) {
        this.setLeftComponent(leftComponent);
    }
    
    public Component getTopComponent() {
        return this.leftComponent;
    }
    
    public void setRightComponent(final Component component) {
        if (component == null) {
            if (this.rightComponent != null) {
                this.remove(this.rightComponent);
                this.rightComponent = null;
            }
        }
        else {
            this.add(component, "right");
        }
    }
    
    public Component getRightComponent() {
        return this.rightComponent;
    }
    
    public void setBottomComponent(final Component rightComponent) {
        this.setRightComponent(rightComponent);
    }
    
    public Component getBottomComponent() {
        return this.rightComponent;
    }
    
    public void setOneTouchExpandable(final boolean oneTouchExpandable) {
        final boolean oneTouchExpandable2 = this.oneTouchExpandable;
        this.oneTouchExpandable = oneTouchExpandable;
        this.oneTouchExpandableSet = true;
        this.firePropertyChange("oneTouchExpandable", oneTouchExpandable2, oneTouchExpandable);
        this.repaint();
    }
    
    public boolean isOneTouchExpandable() {
        return this.oneTouchExpandable;
    }
    
    public void setLastDividerLocation(final int lastDividerLocation) {
        this.firePropertyChange("lastDividerLocation", this.lastDividerLocation, this.lastDividerLocation = lastDividerLocation);
    }
    
    public int getLastDividerLocation() {
        return this.lastDividerLocation;
    }
    
    public void setOrientation(final int orientation) {
        if (orientation != 0 && orientation != 1) {
            throw new IllegalArgumentException("JSplitPane: orientation must be one of JSplitPane.VERTICAL_SPLIT or JSplitPane.HORIZONTAL_SPLIT");
        }
        this.firePropertyChange("orientation", this.orientation, this.orientation = orientation);
    }
    
    public int getOrientation() {
        return this.orientation;
    }
    
    public void setContinuousLayout(final boolean continuousLayout) {
        this.firePropertyChange("continuousLayout", this.continuousLayout, this.continuousLayout = continuousLayout);
    }
    
    public boolean isContinuousLayout() {
        return this.continuousLayout;
    }
    
    public void setResizeWeight(final double resizeWeight) {
        if (resizeWeight < 0.0 || resizeWeight > 1.0) {
            throw new IllegalArgumentException("JSplitPane weight must be between 0 and 1");
        }
        this.firePropertyChange("resizeWeight", this.resizeWeight, this.resizeWeight = resizeWeight);
    }
    
    public double getResizeWeight() {
        return this.resizeWeight;
    }
    
    public void resetToPreferredSizes() {
        final SplitPaneUI ui = this.getUI();
        if (ui != null) {
            ui.resetToPreferredSizes(this);
        }
    }
    
    public void setDividerLocation(final double n) {
        if (n < 0.0 || n > 1.0) {
            throw new IllegalArgumentException("proportional location must be between 0.0 and 1.0.");
        }
        if (this.getOrientation() == 0) {
            this.setDividerLocation((int)((this.getHeight() - this.getDividerSize()) * n));
        }
        else {
            this.setDividerLocation((int)((this.getWidth() - this.getDividerSize()) * n));
        }
    }
    
    public void setDividerLocation(final int dividerLocation) {
        final int dividerLocation2 = this.dividerLocation;
        this.dividerLocation = dividerLocation;
        final SplitPaneUI ui = this.getUI();
        if (ui != null) {
            ui.setDividerLocation(this, dividerLocation);
        }
        this.firePropertyChange("dividerLocation", dividerLocation2, dividerLocation);
        this.setLastDividerLocation(dividerLocation2);
    }
    
    public int getDividerLocation() {
        return this.dividerLocation;
    }
    
    public int getMinimumDividerLocation() {
        final SplitPaneUI ui = this.getUI();
        if (ui != null) {
            return ui.getMinimumDividerLocation(this);
        }
        return -1;
    }
    
    public int getMaximumDividerLocation() {
        final SplitPaneUI ui = this.getUI();
        if (ui != null) {
            return ui.getMaximumDividerLocation(this);
        }
        return -1;
    }
    
    @Override
    public void remove(final Component component) {
        if (component == this.leftComponent) {
            this.leftComponent = null;
        }
        else if (component == this.rightComponent) {
            this.rightComponent = null;
        }
        super.remove(component);
        this.revalidate();
        this.repaint();
    }
    
    @Override
    public void remove(final int n) {
        final Component component = this.getComponent(n);
        if (component == this.leftComponent) {
            this.leftComponent = null;
        }
        else if (component == this.rightComponent) {
            this.rightComponent = null;
        }
        super.remove(n);
        this.revalidate();
        this.repaint();
    }
    
    @Override
    public void removeAll() {
        final Component component = null;
        this.rightComponent = component;
        this.leftComponent = component;
        super.removeAll();
        this.revalidate();
        this.repaint();
    }
    
    @Override
    public boolean isValidateRoot() {
        return true;
    }
    
    @Override
    protected void addImpl(final Component component, Object o, int n) {
        if (o != null && !(o instanceof String)) {
            throw new IllegalArgumentException("cannot add to layout: constraint must be a string (or null)");
        }
        if (o == null) {
            if (this.getLeftComponent() == null) {
                o = "left";
            }
            else if (this.getRightComponent() == null) {
                o = "right";
            }
        }
        if (o != null && (o.equals("left") || o.equals("top"))) {
            final Component leftComponent = this.getLeftComponent();
            if (leftComponent != null) {
                this.remove(leftComponent);
            }
            this.leftComponent = component;
            n = -1;
        }
        else if (o != null && (o.equals("right") || o.equals("bottom"))) {
            final Component rightComponent = this.getRightComponent();
            if (rightComponent != null) {
                this.remove(rightComponent);
            }
            this.rightComponent = component;
            n = -1;
        }
        else if (o != null && o.equals("divider")) {
            n = -1;
        }
        super.addImpl(component, o, n);
        this.revalidate();
        this.repaint();
    }
    
    @Override
    protected void paintChildren(final Graphics graphics) {
        super.paintChildren(graphics);
        final SplitPaneUI ui = this.getUI();
        if (ui != null) {
            final Graphics create = graphics.create();
            ui.finishedPaintingChildren(this, create);
            create.dispose();
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("SplitPaneUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    @Override
    void setUIProperty(final String s, final Object o) {
        if (s == "dividerSize") {
            if (!this.dividerSizeSet) {
                this.setDividerSize(((Number)o).intValue());
                this.dividerSizeSet = false;
            }
        }
        else if (s == "oneTouchExpandable") {
            if (!this.oneTouchExpandableSet) {
                this.setOneTouchExpandable((boolean)o);
                this.oneTouchExpandableSet = false;
            }
        }
        else {
            super.setUIProperty(s, o);
        }
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",continuousLayout=" + (this.continuousLayout ? "true" : "false") + ",dividerSize=" + this.dividerSize + ",lastDividerLocation=" + this.lastDividerLocation + ",oneTouchExpandable=" + (this.oneTouchExpandable ? "true" : "false") + ",orientation=" + ((this.orientation == 1) ? "HORIZONTAL_SPLIT" : "VERTICAL_SPLIT");
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJSplitPane();
        }
        return this.accessibleContext;
    }
    
    protected class AccessibleJSplitPane extends AccessibleJComponent implements AccessibleValue
    {
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            final AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
            if (JSplitPane.this.getOrientation() == 0) {
                accessibleStateSet.add(AccessibleState.VERTICAL);
            }
            else {
                accessibleStateSet.add(AccessibleState.HORIZONTAL);
            }
            return accessibleStateSet;
        }
        
        @Override
        public AccessibleValue getAccessibleValue() {
            return this;
        }
        
        @Override
        public Number getCurrentAccessibleValue() {
            return JSplitPane.this.getDividerLocation();
        }
        
        @Override
        public boolean setCurrentAccessibleValue(final Number n) {
            if (n == null) {
                return false;
            }
            JSplitPane.this.setDividerLocation(n.intValue());
            return true;
        }
        
        @Override
        public Number getMinimumAccessibleValue() {
            return JSplitPane.this.getUI().getMinimumDividerLocation(JSplitPane.this);
        }
        
        @Override
        public Number getMaximumAccessibleValue() {
            return JSplitPane.this.getUI().getMaximumDividerLocation(JSplitPane.this);
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.SPLIT_PANE;
        }
    }
}
