package javax.swing;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleStateSet;
import java.beans.PropertyChangeEvent;
import java.awt.Container;
import javax.swing.plaf.UIResource;
import java.io.Serializable;
import java.awt.LayoutManager2;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ToolBarUI;
import java.beans.PropertyChangeListener;
import java.awt.LayoutManager;
import java.awt.Insets;
import javax.accessibility.Accessible;

public class JToolBar extends JComponent implements SwingConstants, Accessible
{
    private static final String uiClassID = "ToolBarUI";
    private boolean paintBorder;
    private Insets margin;
    private boolean floatable;
    private int orientation;
    
    public JToolBar() {
        this(0);
    }
    
    public JToolBar(final int n) {
        this(null, n);
    }
    
    public JToolBar(final String s) {
        this(s, 0);
    }
    
    public JToolBar(final String name, final int orientation) {
        this.paintBorder = true;
        this.margin = null;
        this.floatable = true;
        this.orientation = 0;
        this.setName(name);
        this.checkOrientation(orientation);
        this.orientation = orientation;
        final DefaultToolBarLayout layout = new DefaultToolBarLayout(orientation);
        this.setLayout(layout);
        this.addPropertyChangeListener(layout);
        this.updateUI();
    }
    
    public ToolBarUI getUI() {
        return (ToolBarUI)this.ui;
    }
    
    public void setUI(final ToolBarUI ui) {
        super.setUI(ui);
    }
    
    @Override
    public void updateUI() {
        this.setUI((ToolBarUI)UIManager.getUI(this));
        if (this.getLayout() == null) {
            this.setLayout(new DefaultToolBarLayout(this.getOrientation()));
        }
        this.invalidate();
    }
    
    @Override
    public String getUIClassID() {
        return "ToolBarUI";
    }
    
    public int getComponentIndex(final Component component) {
        final int componentCount = this.getComponentCount();
        final Component[] components = this.getComponents();
        for (int i = 0; i < componentCount; ++i) {
            if (components[i] == component) {
                return i;
            }
        }
        return -1;
    }
    
    public Component getComponentAtIndex(final int n) {
        final int componentCount = this.getComponentCount();
        if (n >= 0 && n < componentCount) {
            return this.getComponents()[n];
        }
        return null;
    }
    
    public void setMargin(final Insets margin) {
        this.firePropertyChange("margin", this.margin, this.margin = margin);
        this.revalidate();
        this.repaint();
    }
    
    public Insets getMargin() {
        if (this.margin == null) {
            return new Insets(0, 0, 0, 0);
        }
        return this.margin;
    }
    
    public boolean isBorderPainted() {
        return this.paintBorder;
    }
    
    public void setBorderPainted(final boolean paintBorder) {
        if (this.paintBorder != paintBorder) {
            this.firePropertyChange("borderPainted", this.paintBorder, this.paintBorder = paintBorder);
            this.revalidate();
            this.repaint();
        }
    }
    
    @Override
    protected void paintBorder(final Graphics graphics) {
        if (this.isBorderPainted()) {
            super.paintBorder(graphics);
        }
    }
    
    public boolean isFloatable() {
        return this.floatable;
    }
    
    public void setFloatable(final boolean floatable) {
        if (this.floatable != floatable) {
            this.firePropertyChange("floatable", this.floatable, this.floatable = floatable);
            this.revalidate();
            this.repaint();
        }
    }
    
    public int getOrientation() {
        return this.orientation;
    }
    
    public void setOrientation(final int orientation) {
        this.checkOrientation(orientation);
        if (this.orientation != orientation) {
            this.firePropertyChange("orientation", this.orientation, this.orientation = orientation);
            this.revalidate();
            this.repaint();
        }
    }
    
    public void setRollover(final boolean b) {
        this.putClientProperty("JToolBar.isRollover", b ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public boolean isRollover() {
        final Boolean b = (Boolean)this.getClientProperty("JToolBar.isRollover");
        return b != null && b;
    }
    
    private void checkOrientation(final int n) {
        switch (n) {
            case 0:
            case 1: {
                return;
            }
            default: {
                throw new IllegalArgumentException("orientation must be one of: VERTICAL, HORIZONTAL");
            }
        }
    }
    
    public void addSeparator() {
        this.addSeparator(null);
    }
    
    public void addSeparator(final Dimension dimension) {
        this.add(new Separator(dimension));
    }
    
    public JButton add(final Action action) {
        final JButton actionComponent = this.createActionComponent(action);
        actionComponent.setAction(action);
        this.add(actionComponent);
        return actionComponent;
    }
    
    protected JButton createActionComponent(final Action action) {
        final JButton button = new JButton() {
            @Override
            protected PropertyChangeListener createActionPropertyChangeListener(final Action action) {
                PropertyChangeListener propertyChangeListener = JToolBar.this.createActionChangeListener(this);
                if (propertyChangeListener == null) {
                    propertyChangeListener = super.createActionPropertyChangeListener(action);
                }
                return propertyChangeListener;
            }
        };
        if (action != null && (action.getValue("SmallIcon") != null || action.getValue("SwingLargeIconKey") != null)) {
            button.setHideActionText(true);
        }
        button.setHorizontalTextPosition(0);
        button.setVerticalTextPosition(3);
        return button;
    }
    
    protected PropertyChangeListener createActionChangeListener(final JButton button) {
        return null;
    }
    
    @Override
    protected void addImpl(final Component component, final Object o, final int n) {
        if (component instanceof Separator) {
            if (this.getOrientation() == 1) {
                ((Separator)component).setOrientation(0);
            }
            else {
                ((Separator)component).setOrientation(1);
            }
        }
        super.addImpl(component, o, n);
        if (component instanceof JButton) {
            ((JButton)component).setDefaultCapable(false);
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("ToolBarUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",floatable=" + (this.floatable ? "true" : "false") + ",margin=" + ((this.margin != null) ? this.margin.toString() : "") + ",orientation=" + ((this.orientation == 0) ? "HORIZONTAL" : "VERTICAL") + ",paintBorder=" + (this.paintBorder ? "true" : "false");
    }
    
    @Override
    public void setLayout(final LayoutManager layout) {
        final LayoutManager layout2 = this.getLayout();
        if (layout2 instanceof PropertyChangeListener) {
            this.removePropertyChangeListener((PropertyChangeListener)layout2);
        }
        super.setLayout(layout);
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJToolBar();
        }
        return this.accessibleContext;
    }
    
    public static class Separator extends JSeparator
    {
        private Dimension separatorSize;
        
        public Separator() {
            this(null);
        }
        
        public Separator(final Dimension separatorSize) {
            super(0);
            this.setSeparatorSize(separatorSize);
        }
        
        @Override
        public String getUIClassID() {
            return "ToolBarSeparatorUI";
        }
        
        public void setSeparatorSize(final Dimension separatorSize) {
            if (separatorSize != null) {
                this.separatorSize = separatorSize;
            }
            else {
                super.updateUI();
            }
            this.invalidate();
        }
        
        public Dimension getSeparatorSize() {
            return this.separatorSize;
        }
        
        @Override
        public Dimension getMinimumSize() {
            if (this.separatorSize != null) {
                return this.separatorSize.getSize();
            }
            return super.getMinimumSize();
        }
        
        @Override
        public Dimension getMaximumSize() {
            if (this.separatorSize != null) {
                return this.separatorSize.getSize();
            }
            return super.getMaximumSize();
        }
        
        @Override
        public Dimension getPreferredSize() {
            if (this.separatorSize != null) {
                return this.separatorSize.getSize();
            }
            return super.getPreferredSize();
        }
    }
    
    private class DefaultToolBarLayout implements LayoutManager2, Serializable, PropertyChangeListener, UIResource
    {
        BoxLayout lm;
        
        DefaultToolBarLayout(final int n) {
            if (n == 1) {
                this.lm = new BoxLayout(JToolBar.this, 3);
            }
            else {
                this.lm = new BoxLayout(JToolBar.this, 2);
            }
        }
        
        @Override
        public void addLayoutComponent(final String s, final Component component) {
            this.lm.addLayoutComponent(s, component);
        }
        
        @Override
        public void addLayoutComponent(final Component component, final Object o) {
            this.lm.addLayoutComponent(component, o);
        }
        
        @Override
        public void removeLayoutComponent(final Component component) {
            this.lm.removeLayoutComponent(component);
        }
        
        @Override
        public Dimension preferredLayoutSize(final Container container) {
            return this.lm.preferredLayoutSize(container);
        }
        
        @Override
        public Dimension minimumLayoutSize(final Container container) {
            return this.lm.minimumLayoutSize(container);
        }
        
        @Override
        public Dimension maximumLayoutSize(final Container container) {
            return this.lm.maximumLayoutSize(container);
        }
        
        @Override
        public void layoutContainer(final Container container) {
            this.lm.layoutContainer(container);
        }
        
        @Override
        public float getLayoutAlignmentX(final Container container) {
            return this.lm.getLayoutAlignmentX(container);
        }
        
        @Override
        public float getLayoutAlignmentY(final Container container) {
            return this.lm.getLayoutAlignmentY(container);
        }
        
        @Override
        public void invalidateLayout(final Container container) {
            this.lm.invalidateLayout(container);
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getPropertyName().equals("orientation")) {
                if ((int)propertyChangeEvent.getNewValue() == 1) {
                    this.lm = new BoxLayout(JToolBar.this, 3);
                }
                else {
                    this.lm = new BoxLayout(JToolBar.this, 2);
                }
            }
        }
    }
    
    protected class AccessibleJToolBar extends AccessibleJComponent
    {
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            return super.getAccessibleStateSet();
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.TOOL_BAR;
        }
    }
}
