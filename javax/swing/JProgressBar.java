package javax.swing;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleValue;
import java.io.Serializable;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ProgressBarUI;
import java.awt.Graphics;
import java.text.NumberFormat;
import javax.accessibility.AccessibleState;
import java.text.Format;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.accessibility.Accessible;

public class JProgressBar extends JComponent implements SwingConstants, Accessible
{
    private static final String uiClassID = "ProgressBarUI";
    protected int orientation;
    protected boolean paintBorder;
    protected BoundedRangeModel model;
    protected String progressString;
    protected boolean paintString;
    private static final int defaultMinimum = 0;
    private static final int defaultMaximum = 100;
    private static final int defaultOrientation = 0;
    protected transient ChangeEvent changeEvent;
    protected ChangeListener changeListener;
    private transient Format format;
    private boolean indeterminate;
    
    public JProgressBar() {
        this(0);
    }
    
    public JProgressBar(final int n) {
        this(n, 0, 100);
    }
    
    public JProgressBar(final int n, final int n2) {
        this(0, n, n2);
    }
    
    public JProgressBar(final int orientation, final int n, final int n2) {
        this.changeEvent = null;
        this.changeListener = null;
        this.setModel(new DefaultBoundedRangeModel(n, 0, n, n2));
        this.updateUI();
        this.setOrientation(orientation);
        this.setBorderPainted(true);
        this.setStringPainted(false);
        this.setString(null);
        this.setIndeterminate(false);
    }
    
    public JProgressBar(final BoundedRangeModel model) {
        this.changeEvent = null;
        this.changeListener = null;
        this.setModel(model);
        this.updateUI();
        this.setOrientation(0);
        this.setBorderPainted(true);
        this.setStringPainted(false);
        this.setString(null);
        this.setIndeterminate(false);
    }
    
    public int getOrientation() {
        return this.orientation;
    }
    
    public void setOrientation(final int orientation) {
        if (this.orientation != orientation) {
            switch (orientation) {
                case 0:
                case 1: {
                    final int orientation2 = this.orientation;
                    this.firePropertyChange("orientation", orientation2, this.orientation = orientation);
                    if (this.accessibleContext != null) {
                        this.accessibleContext.firePropertyChange("AccessibleState", (orientation2 == 1) ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL, (this.orientation == 1) ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL);
                    }
                    this.revalidate();
                    break;
                }
                default: {
                    throw new IllegalArgumentException(orientation + " is not a legal orientation");
                }
            }
        }
    }
    
    public boolean isStringPainted() {
        return this.paintString;
    }
    
    public void setStringPainted(final boolean paintString) {
        final boolean paintString2 = this.paintString;
        this.firePropertyChange("stringPainted", paintString2, this.paintString = paintString);
        if (this.paintString != paintString2) {
            this.revalidate();
            this.repaint();
        }
    }
    
    public String getString() {
        if (this.progressString != null) {
            return this.progressString;
        }
        if (this.format == null) {
            this.format = NumberFormat.getPercentInstance();
        }
        return this.format.format(new Double(this.getPercentComplete()));
    }
    
    public void setString(final String progressString) {
        final String progressString2 = this.progressString;
        this.firePropertyChange("string", progressString2, this.progressString = progressString);
        if (this.progressString == null || progressString2 == null || !this.progressString.equals(progressString2)) {
            this.repaint();
        }
    }
    
    public double getPercentComplete() {
        return (this.model.getValue() - (double)this.model.getMinimum()) / (this.model.getMaximum() - this.model.getMinimum());
    }
    
    public boolean isBorderPainted() {
        return this.paintBorder;
    }
    
    public void setBorderPainted(final boolean paintBorder) {
        final boolean paintBorder2 = this.paintBorder;
        this.firePropertyChange("borderPainted", paintBorder2, this.paintBorder = paintBorder);
        if (this.paintBorder != paintBorder2) {
            this.repaint();
        }
    }
    
    @Override
    protected void paintBorder(final Graphics graphics) {
        if (this.isBorderPainted()) {
            super.paintBorder(graphics);
        }
    }
    
    public ProgressBarUI getUI() {
        return (ProgressBarUI)this.ui;
    }
    
    public void setUI(final ProgressBarUI ui) {
        super.setUI(ui);
    }
    
    @Override
    public void updateUI() {
        this.setUI((ProgressBarUI)UIManager.getUI(this));
    }
    
    @Override
    public String getUIClassID() {
        return "ProgressBarUI";
    }
    
    protected ChangeListener createChangeListener() {
        return new ModelListener();
    }
    
    public void addChangeListener(final ChangeListener changeListener) {
        this.listenerList.add(ChangeListener.class, changeListener);
    }
    
    public void removeChangeListener(final ChangeListener changeListener) {
        this.listenerList.remove(ChangeListener.class, changeListener);
    }
    
    public ChangeListener[] getChangeListeners() {
        return this.listenerList.getListeners(ChangeListener.class);
    }
    
    protected void fireStateChanged() {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == ChangeListener.class) {
                if (this.changeEvent == null) {
                    this.changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener)listenerList[i + 1]).stateChanged(this.changeEvent);
            }
        }
    }
    
    public BoundedRangeModel getModel() {
        return this.model;
    }
    
    public void setModel(final BoundedRangeModel model) {
        final BoundedRangeModel model2 = this.getModel();
        if (model != model2) {
            if (model2 != null) {
                model2.removeChangeListener(this.changeListener);
                this.changeListener = null;
            }
            if ((this.model = model) != null) {
                model.addChangeListener(this.changeListener = this.createChangeListener());
            }
            if (this.accessibleContext != null) {
                this.accessibleContext.firePropertyChange("AccessibleValue", (model2 == null) ? null : Integer.valueOf(model2.getValue()), (model == null) ? null : Integer.valueOf(model.getValue()));
            }
            if (this.model != null) {
                this.model.setExtent(0);
            }
            this.repaint();
        }
    }
    
    public int getValue() {
        return this.getModel().getValue();
    }
    
    public int getMinimum() {
        return this.getModel().getMinimum();
    }
    
    public int getMaximum() {
        return this.getModel().getMaximum();
    }
    
    public void setValue(final int value) {
        final BoundedRangeModel model = this.getModel();
        final int value2 = model.getValue();
        model.setValue(value);
        if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleValue", value2, model.getValue());
        }
    }
    
    public void setMinimum(final int minimum) {
        this.getModel().setMinimum(minimum);
    }
    
    public void setMaximum(final int maximum) {
        this.getModel().setMaximum(maximum);
    }
    
    public void setIndeterminate(final boolean indeterminate) {
        this.firePropertyChange("indeterminate", this.indeterminate, this.indeterminate = indeterminate);
    }
    
    public boolean isIndeterminate() {
        return this.indeterminate;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("ProgressBarUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",orientation=" + ((this.orientation == 0) ? "HORIZONTAL" : "VERTICAL") + ",paintBorder=" + (this.paintBorder ? "true" : "false") + ",paintString=" + (this.paintString ? "true" : "false") + ",progressString=" + ((this.progressString != null) ? this.progressString : "") + ",indeterminateString=" + (this.indeterminate ? "true" : "false");
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJProgressBar();
        }
        return this.accessibleContext;
    }
    
    private class ModelListener implements ChangeListener, Serializable
    {
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            JProgressBar.this.fireStateChanged();
        }
    }
    
    protected class AccessibleJProgressBar extends AccessibleJComponent implements AccessibleValue
    {
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            final AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
            if (JProgressBar.this.getModel().getValueIsAdjusting()) {
                accessibleStateSet.add(AccessibleState.BUSY);
            }
            if (JProgressBar.this.getOrientation() == 1) {
                accessibleStateSet.add(AccessibleState.VERTICAL);
            }
            else {
                accessibleStateSet.add(AccessibleState.HORIZONTAL);
            }
            return accessibleStateSet;
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PROGRESS_BAR;
        }
        
        @Override
        public AccessibleValue getAccessibleValue() {
            return this;
        }
        
        @Override
        public Number getCurrentAccessibleValue() {
            return JProgressBar.this.getValue();
        }
        
        @Override
        public boolean setCurrentAccessibleValue(final Number n) {
            if (n == null) {
                return false;
            }
            JProgressBar.this.setValue(n.intValue());
            return true;
        }
        
        @Override
        public Number getMinimumAccessibleValue() {
            return JProgressBar.this.getMinimum();
        }
        
        @Override
        public Number getMaximumAccessibleValue() {
            return JProgressBar.this.model.getMaximum() - JProgressBar.this.model.getExtent();
        }
    }
}
