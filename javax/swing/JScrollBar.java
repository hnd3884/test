package javax.swing;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleValue;
import javax.swing.event.ChangeEvent;
import java.io.Serializable;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.accessibility.AccessibleState;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.event.ChangeListener;
import javax.accessibility.Accessible;
import java.awt.Adjustable;

public class JScrollBar extends JComponent implements Adjustable, Accessible
{
    private static final String uiClassID = "ScrollBarUI";
    private ChangeListener fwdAdjustmentEvents;
    protected BoundedRangeModel model;
    protected int orientation;
    protected int unitIncrement;
    protected int blockIncrement;
    
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
    
    public JScrollBar(final int orientation, final int n, final int n2, final int n3, final int n4) {
        this.fwdAdjustmentEvents = new ModelListener();
        this.checkOrientation(orientation);
        this.unitIncrement = 1;
        this.blockIncrement = ((n2 == 0) ? 1 : n2);
        this.orientation = orientation;
        (this.model = new DefaultBoundedRangeModel(n, n2, n3, n4)).addChangeListener(this.fwdAdjustmentEvents);
        this.setRequestFocusEnabled(false);
        this.updateUI();
    }
    
    public JScrollBar(final int n) {
        this(n, 0, 10, 0, 100);
    }
    
    public JScrollBar() {
        this(1);
    }
    
    public void setUI(final ScrollBarUI ui) {
        super.setUI(ui);
    }
    
    public ScrollBarUI getUI() {
        return (ScrollBarUI)this.ui;
    }
    
    @Override
    public void updateUI() {
        this.setUI((ScrollBarUI)UIManager.getUI(this));
    }
    
    @Override
    public String getUIClassID() {
        return "ScrollBarUI";
    }
    
    @Override
    public int getOrientation() {
        return this.orientation;
    }
    
    public void setOrientation(final int orientation) {
        this.checkOrientation(orientation);
        final int orientation2 = this.orientation;
        this.firePropertyChange("orientation", orientation2, this.orientation = orientation);
        if (orientation2 != orientation && this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleState", (orientation2 == 1) ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL, (orientation == 1) ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL);
        }
        if (orientation != orientation2) {
            this.revalidate();
        }
    }
    
    public BoundedRangeModel getModel() {
        return this.model;
    }
    
    public void setModel(final BoundedRangeModel model) {
        Object value = null;
        final BoundedRangeModel model2 = this.model;
        if (this.model != null) {
            this.model.removeChangeListener(this.fwdAdjustmentEvents);
            value = this.model.getValue();
        }
        this.model = model;
        if (this.model != null) {
            this.model.addChangeListener(this.fwdAdjustmentEvents);
        }
        this.firePropertyChange("model", model2, this.model);
        if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleValue", value, new Integer(this.model.getValue()));
        }
    }
    
    public int getUnitIncrement(final int n) {
        return this.unitIncrement;
    }
    
    @Override
    public void setUnitIncrement(final int unitIncrement) {
        this.firePropertyChange("unitIncrement", this.unitIncrement, this.unitIncrement = unitIncrement);
    }
    
    public int getBlockIncrement(final int n) {
        return this.blockIncrement;
    }
    
    @Override
    public void setBlockIncrement(final int blockIncrement) {
        this.firePropertyChange("blockIncrement", this.blockIncrement, this.blockIncrement = blockIncrement);
    }
    
    @Override
    public int getUnitIncrement() {
        return this.unitIncrement;
    }
    
    @Override
    public int getBlockIncrement() {
        return this.blockIncrement;
    }
    
    @Override
    public int getValue() {
        return this.getModel().getValue();
    }
    
    @Override
    public void setValue(final int value) {
        final BoundedRangeModel model = this.getModel();
        final int value2 = model.getValue();
        model.setValue(value);
        if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleValue", value2, model.getValue());
        }
    }
    
    @Override
    public int getVisibleAmount() {
        return this.getModel().getExtent();
    }
    
    @Override
    public void setVisibleAmount(final int extent) {
        this.getModel().setExtent(extent);
    }
    
    @Override
    public int getMinimum() {
        return this.getModel().getMinimum();
    }
    
    @Override
    public void setMinimum(final int minimum) {
        this.getModel().setMinimum(minimum);
    }
    
    @Override
    public int getMaximum() {
        return this.getModel().getMaximum();
    }
    
    @Override
    public void setMaximum(final int maximum) {
        this.getModel().setMaximum(maximum);
    }
    
    public boolean getValueIsAdjusting() {
        return this.getModel().getValueIsAdjusting();
    }
    
    public void setValueIsAdjusting(final boolean valueIsAdjusting) {
        final BoundedRangeModel model = this.getModel();
        final boolean valueIsAdjusting2 = model.getValueIsAdjusting();
        model.setValueIsAdjusting(valueIsAdjusting);
        if (valueIsAdjusting2 != valueIsAdjusting && this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleState", valueIsAdjusting2 ? AccessibleState.BUSY : null, valueIsAdjusting ? AccessibleState.BUSY : null);
        }
    }
    
    public void setValues(final int n, final int n2, final int n3, final int n4) {
        final BoundedRangeModel model = this.getModel();
        final int value = model.getValue();
        model.setRangeProperties(n, n2, n3, n4, model.getValueIsAdjusting());
        if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleValue", value, model.getValue());
        }
    }
    
    @Override
    public void addAdjustmentListener(final AdjustmentListener adjustmentListener) {
        this.listenerList.add(AdjustmentListener.class, adjustmentListener);
    }
    
    @Override
    public void removeAdjustmentListener(final AdjustmentListener adjustmentListener) {
        this.listenerList.remove(AdjustmentListener.class, adjustmentListener);
    }
    
    public AdjustmentListener[] getAdjustmentListeners() {
        return this.listenerList.getListeners(AdjustmentListener.class);
    }
    
    protected void fireAdjustmentValueChanged(final int n, final int n2, final int n3) {
        this.fireAdjustmentValueChanged(n, n2, n3, this.getValueIsAdjusting());
    }
    
    private void fireAdjustmentValueChanged(final int n, final int n2, final int n3, final boolean b) {
        final Object[] listenerList = this.listenerList.getListenerList();
        AdjustmentEvent adjustmentEvent = null;
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == AdjustmentListener.class) {
                if (adjustmentEvent == null) {
                    adjustmentEvent = new AdjustmentEvent(this, n, n2, n3, b);
                }
                ((AdjustmentListener)listenerList[i + 1]).adjustmentValueChanged(adjustmentEvent);
            }
        }
    }
    
    @Override
    public Dimension getMinimumSize() {
        final Dimension preferredSize = this.getPreferredSize();
        if (this.orientation == 1) {
            return new Dimension(preferredSize.width, 5);
        }
        return new Dimension(5, preferredSize.height);
    }
    
    @Override
    public Dimension getMaximumSize() {
        final Dimension preferredSize = this.getPreferredSize();
        if (this.getOrientation() == 1) {
            return new Dimension(preferredSize.width, 32767);
        }
        return new Dimension(32767, preferredSize.height);
    }
    
    @Override
    public void setEnabled(final boolean b) {
        super.setEnabled(b);
        final Component[] components = this.getComponents();
        for (int length = components.length, i = 0; i < length; ++i) {
            components[i].setEnabled(b);
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("ScrollBarUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",blockIncrement=" + this.blockIncrement + ",orientation=" + ((this.orientation == 0) ? "HORIZONTAL" : "VERTICAL") + ",unitIncrement=" + this.unitIncrement;
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJScrollBar();
        }
        return this.accessibleContext;
    }
    
    private class ModelListener implements ChangeListener, Serializable
    {
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            final Object source = changeEvent.getSource();
            if (source instanceof BoundedRangeModel) {
                final int n = 601;
                final int n2 = 5;
                final BoundedRangeModel boundedRangeModel = (BoundedRangeModel)source;
                JScrollBar.this.fireAdjustmentValueChanged(n, n2, boundedRangeModel.getValue(), boundedRangeModel.getValueIsAdjusting());
            }
        }
    }
    
    protected class AccessibleJScrollBar extends AccessibleJComponent implements AccessibleValue
    {
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            final AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
            if (JScrollBar.this.getValueIsAdjusting()) {
                accessibleStateSet.add(AccessibleState.BUSY);
            }
            if (JScrollBar.this.getOrientation() == 1) {
                accessibleStateSet.add(AccessibleState.VERTICAL);
            }
            else {
                accessibleStateSet.add(AccessibleState.HORIZONTAL);
            }
            return accessibleStateSet;
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.SCROLL_BAR;
        }
        
        @Override
        public AccessibleValue getAccessibleValue() {
            return this;
        }
        
        @Override
        public Number getCurrentAccessibleValue() {
            return JScrollBar.this.getValue();
        }
        
        @Override
        public boolean setCurrentAccessibleValue(final Number n) {
            if (n == null) {
                return false;
            }
            JScrollBar.this.setValue(n.intValue());
            return true;
        }
        
        @Override
        public Number getMinimumAccessibleValue() {
            return JScrollBar.this.getMinimum();
        }
        
        @Override
        public Number getMaximumAccessibleValue() {
            return new Integer(JScrollBar.this.model.getMaximum() - JScrollBar.this.model.getExtent());
        }
    }
}
