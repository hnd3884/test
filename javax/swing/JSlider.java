package javax.swing;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleValue;
import java.io.Serializable;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.Color;
import javax.swing.plaf.UIResource;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.Enumeration;
import java.awt.Component;
import java.awt.Image;
import java.awt.Font;
import javax.accessibility.AccessibleState;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SliderUI;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Dictionary;
import javax.accessibility.Accessible;

public class JSlider extends JComponent implements SwingConstants, Accessible
{
    private static final String uiClassID = "SliderUI";
    private boolean paintTicks;
    private boolean paintTrack;
    private boolean paintLabels;
    private boolean isInverted;
    protected BoundedRangeModel sliderModel;
    protected int majorTickSpacing;
    protected int minorTickSpacing;
    protected boolean snapToTicks;
    boolean snapToValue;
    protected int orientation;
    private Dictionary labelTable;
    protected ChangeListener changeListener;
    protected transient ChangeEvent changeEvent;
    
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
    
    public JSlider() {
        this(0, 0, 100, 50);
    }
    
    public JSlider(final int n) {
        this(n, 0, 100, 50);
    }
    
    public JSlider(final int n, final int n2) {
        this(0, n, n2, (n + n2) / 2);
    }
    
    public JSlider(final int n, final int n2, final int n3) {
        this(0, n, n2, n3);
    }
    
    public JSlider(final int orientation, final int n, final int n2, final int n3) {
        this.paintTicks = false;
        this.paintTrack = true;
        this.paintLabels = false;
        this.isInverted = false;
        this.snapToTicks = false;
        this.snapToValue = true;
        this.changeListener = this.createChangeListener();
        this.changeEvent = null;
        this.checkOrientation(orientation);
        this.orientation = orientation;
        this.setModel(new DefaultBoundedRangeModel(n3, 0, n, n2));
        this.updateUI();
    }
    
    public JSlider(final BoundedRangeModel model) {
        this.paintTicks = false;
        this.paintTrack = true;
        this.paintLabels = false;
        this.isInverted = false;
        this.snapToTicks = false;
        this.snapToValue = true;
        this.changeListener = this.createChangeListener();
        this.changeEvent = null;
        this.orientation = 0;
        this.setModel(model);
        this.updateUI();
    }
    
    public SliderUI getUI() {
        return (SliderUI)this.ui;
    }
    
    public void setUI(final SliderUI ui) {
        super.setUI(ui);
    }
    
    @Override
    public void updateUI() {
        this.setUI((SliderUI)UIManager.getUI(this));
        this.updateLabelUIs();
    }
    
    @Override
    public String getUIClassID() {
        return "SliderUI";
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
        return this.sliderModel;
    }
    
    public void setModel(final BoundedRangeModel sliderModel) {
        final BoundedRangeModel model = this.getModel();
        if (model != null) {
            model.removeChangeListener(this.changeListener);
        }
        if ((this.sliderModel = sliderModel) != null) {
            sliderModel.addChangeListener(this.changeListener);
        }
        if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleValue", (model == null) ? null : Integer.valueOf(model.getValue()), (sliderModel == null) ? null : Integer.valueOf(sliderModel.getValue()));
        }
        this.firePropertyChange("model", model, this.sliderModel);
    }
    
    public int getValue() {
        return this.getModel().getValue();
    }
    
    public void setValue(final int value) {
        final BoundedRangeModel model = this.getModel();
        final int value2 = model.getValue();
        if (value2 == value) {
            return;
        }
        model.setValue(value);
        if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleValue", value2, model.getValue());
        }
    }
    
    public int getMinimum() {
        return this.getModel().getMinimum();
    }
    
    public void setMinimum(final int minimum) {
        final int minimum2 = this.getModel().getMinimum();
        this.getModel().setMinimum(minimum);
        this.firePropertyChange("minimum", minimum2, (Object)minimum);
    }
    
    public int getMaximum() {
        return this.getModel().getMaximum();
    }
    
    public void setMaximum(final int maximum) {
        final int maximum2 = this.getModel().getMaximum();
        this.getModel().setMaximum(maximum);
        this.firePropertyChange("maximum", maximum2, (Object)maximum);
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
    
    public int getExtent() {
        return this.getModel().getExtent();
    }
    
    public void setExtent(final int extent) {
        this.getModel().setExtent(extent);
    }
    
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
    
    @Override
    public void setFont(final Font font) {
        super.setFont(font);
        this.updateLabelSizes();
    }
    
    @Override
    public boolean imageUpdate(final Image image, final int n, final int n2, final int n3, final int n4, final int n5) {
        if (!this.isShowing()) {
            return false;
        }
        final Enumeration elements = this.labelTable.elements();
        while (elements.hasMoreElements()) {
            final Component component = (Component)elements.nextElement();
            if (component instanceof JLabel) {
                final JLabel label = (JLabel)component;
                if (SwingUtilities.doesIconReferenceImage(label.getIcon(), image) || SwingUtilities.doesIconReferenceImage(label.getDisabledIcon(), image)) {
                    return super.imageUpdate(image, n, n2, n3, n4, n5);
                }
                continue;
            }
        }
        return false;
    }
    
    public Dictionary getLabelTable() {
        return this.labelTable;
    }
    
    public void setLabelTable(final Dictionary labelTable) {
        final Dictionary labelTable2 = this.labelTable;
        this.labelTable = labelTable;
        this.updateLabelUIs();
        this.firePropertyChange("labelTable", labelTable2, this.labelTable);
        if (labelTable != labelTable2) {
            this.revalidate();
            this.repaint();
        }
    }
    
    protected void updateLabelUIs() {
        final Dictionary labelTable = this.getLabelTable();
        if (labelTable == null) {
            return;
        }
        final Enumeration keys = labelTable.keys();
        while (keys.hasMoreElements()) {
            final JComponent component = labelTable.get(keys.nextElement());
            component.updateUI();
            component.setSize(component.getPreferredSize());
        }
    }
    
    private void updateLabelSizes() {
        final Dictionary labelTable = this.getLabelTable();
        if (labelTable != null) {
            final Enumeration elements = labelTable.elements();
            while (elements.hasMoreElements()) {
                final JComponent component = (JComponent)elements.nextElement();
                component.setSize(component.getPreferredSize());
            }
        }
    }
    
    public Hashtable createStandardLabels(final int n) {
        return this.createStandardLabels(n, this.getMinimum());
    }
    
    public Hashtable createStandardLabels(final int n, final int n2) {
        if (n2 > this.getMaximum() || n2 < this.getMinimum()) {
            throw new IllegalArgumentException("Slider label start point out of range.");
        }
        if (n <= 0) {
            throw new IllegalArgumentException("Label incremement must be > 0");
        }
        class SmartHashtable extends Hashtable<Object, Object> implements PropertyChangeListener
        {
            int increment = n;
            int start = n2;
            boolean startAtMin;
            
            public SmartHashtable(final int start) {
                this.increment = 0;
                this.start = 0;
                this.startAtMin = false;
                this.startAtMin = (start == JSlider.this.getMinimum());
                this.createLabels();
            }
            
            @Override
            public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
                if (propertyChangeEvent.getPropertyName().equals("minimum") && this.startAtMin) {
                    this.start = JSlider.this.getMinimum();
                }
                if (propertyChangeEvent.getPropertyName().equals("minimum") || propertyChangeEvent.getPropertyName().equals("maximum")) {
                    final Enumeration keys = JSlider.this.getLabelTable().keys();
                    final Hashtable hashtable = new Hashtable();
                    while (keys.hasMoreElements()) {
                        final Object nextElement = keys.nextElement();
                        final Object value = JSlider.this.labelTable.get(nextElement);
                        if (!(value instanceof LabelUIResource)) {
                            hashtable.put(nextElement, value);
                        }
                    }
                    this.clear();
                    this.createLabels();
                    final Enumeration keys2 = hashtable.keys();
                    while (keys2.hasMoreElements()) {
                        final Object nextElement2 = keys2.nextElement();
                        this.put(nextElement2, hashtable.get(nextElement2));
                    }
                    ((JSlider)propertyChangeEvent.getSource()).setLabelTable(this);
                }
            }
            
            void createLabels() {
                for (int i = this.start; i <= JSlider.this.getMaximum(); i += this.increment) {
                    ((Hashtable<Integer, LabelUIResource>)this).put(i, new LabelUIResource("" + i, 0));
                }
            }
            
            class LabelUIResource extends JLabel implements UIResource
            {
                public LabelUIResource(final int n) {
                    super(s, n);
                    this.setName("Slider.label");
                }
                
                @Override
                public Font getFont() {
                    final Font font = super.getFont();
                    if (font != null && !(font instanceof UIResource)) {
                        return font;
                    }
                    return JSlider.this.getFont();
                }
                
                @Override
                public Color getForeground() {
                    final Color foreground = super.getForeground();
                    if (foreground != null && !(foreground instanceof UIResource)) {
                        return foreground;
                    }
                    if (!(JSlider.this.getForeground() instanceof UIResource)) {
                        return JSlider.this.getForeground();
                    }
                    return foreground;
                }
            }
        }
        final SmartHashtable smartHashtable = new SmartHashtable();
        final Dictionary labelTable = this.getLabelTable();
        if (labelTable != null && labelTable instanceof PropertyChangeListener) {
            this.removePropertyChangeListener((PropertyChangeListener)labelTable);
        }
        this.addPropertyChangeListener(smartHashtable);
        return smartHashtable;
    }
    
    public boolean getInverted() {
        return this.isInverted;
    }
    
    public void setInverted(final boolean isInverted) {
        final boolean isInverted2 = this.isInverted;
        this.firePropertyChange("inverted", isInverted2, this.isInverted = isInverted);
        if (isInverted != isInverted2) {
            this.repaint();
        }
    }
    
    public int getMajorTickSpacing() {
        return this.majorTickSpacing;
    }
    
    public void setMajorTickSpacing(final int majorTickSpacing) {
        final int majorTickSpacing2 = this.majorTickSpacing;
        this.majorTickSpacing = majorTickSpacing;
        if (this.labelTable == null && this.getMajorTickSpacing() > 0 && this.getPaintLabels()) {
            this.setLabelTable(this.createStandardLabels(this.getMajorTickSpacing()));
        }
        this.firePropertyChange("majorTickSpacing", majorTickSpacing2, this.majorTickSpacing);
        if (this.majorTickSpacing != majorTickSpacing2 && this.getPaintTicks()) {
            this.repaint();
        }
    }
    
    public int getMinorTickSpacing() {
        return this.minorTickSpacing;
    }
    
    public void setMinorTickSpacing(final int minorTickSpacing) {
        final int minorTickSpacing2 = this.minorTickSpacing;
        this.firePropertyChange("minorTickSpacing", minorTickSpacing2, this.minorTickSpacing = minorTickSpacing);
        if (this.minorTickSpacing != minorTickSpacing2 && this.getPaintTicks()) {
            this.repaint();
        }
    }
    
    public boolean getSnapToTicks() {
        return this.snapToTicks;
    }
    
    boolean getSnapToValue() {
        return this.snapToValue;
    }
    
    public void setSnapToTicks(final boolean snapToTicks) {
        this.firePropertyChange("snapToTicks", this.snapToTicks, this.snapToTicks = snapToTicks);
    }
    
    void setSnapToValue(final boolean snapToValue) {
        this.firePropertyChange("snapToValue", this.snapToValue, this.snapToValue = snapToValue);
    }
    
    public boolean getPaintTicks() {
        return this.paintTicks;
    }
    
    public void setPaintTicks(final boolean paintTicks) {
        final boolean paintTicks2 = this.paintTicks;
        this.firePropertyChange("paintTicks", paintTicks2, this.paintTicks = paintTicks);
        if (this.paintTicks != paintTicks2) {
            this.revalidate();
            this.repaint();
        }
    }
    
    public boolean getPaintTrack() {
        return this.paintTrack;
    }
    
    public void setPaintTrack(final boolean paintTrack) {
        final boolean paintTrack2 = this.paintTrack;
        this.firePropertyChange("paintTrack", paintTrack2, this.paintTrack = paintTrack);
        if (this.paintTrack != paintTrack2) {
            this.repaint();
        }
    }
    
    public boolean getPaintLabels() {
        return this.paintLabels;
    }
    
    public void setPaintLabels(final boolean paintLabels) {
        final boolean paintLabels2 = this.paintLabels;
        this.paintLabels = paintLabels;
        if (this.labelTable == null && this.getMajorTickSpacing() > 0) {
            this.setLabelTable(this.createStandardLabels(this.getMajorTickSpacing()));
        }
        this.firePropertyChange("paintLabels", paintLabels2, this.paintLabels);
        if (this.paintLabels != paintLabels2) {
            this.revalidate();
            this.repaint();
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("SliderUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",isInverted=" + (this.isInverted ? "true" : "false") + ",majorTickSpacing=" + this.majorTickSpacing + ",minorTickSpacing=" + this.minorTickSpacing + ",orientation=" + ((this.orientation == 0) ? "HORIZONTAL" : "VERTICAL") + ",paintLabels=" + (this.paintLabels ? "true" : "false") + ",paintTicks=" + (this.paintTicks ? "true" : "false") + ",paintTrack=" + (this.paintTrack ? "true" : "false") + ",snapToTicks=" + (this.snapToTicks ? "true" : "false") + ",snapToValue=" + (this.snapToValue ? "true" : "false");
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJSlider();
        }
        return this.accessibleContext;
    }
    
    private class ModelListener implements ChangeListener, Serializable
    {
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            JSlider.this.fireStateChanged();
        }
    }
    
    protected class AccessibleJSlider extends AccessibleJComponent implements AccessibleValue
    {
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            final AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
            if (JSlider.this.getValueIsAdjusting()) {
                accessibleStateSet.add(AccessibleState.BUSY);
            }
            if (JSlider.this.getOrientation() == 1) {
                accessibleStateSet.add(AccessibleState.VERTICAL);
            }
            else {
                accessibleStateSet.add(AccessibleState.HORIZONTAL);
            }
            return accessibleStateSet;
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.SLIDER;
        }
        
        @Override
        public AccessibleValue getAccessibleValue() {
            return this;
        }
        
        @Override
        public Number getCurrentAccessibleValue() {
            return JSlider.this.getValue();
        }
        
        @Override
        public boolean setCurrentAccessibleValue(final Number n) {
            if (n == null) {
                return false;
            }
            JSlider.this.setValue(n.intValue());
            return true;
        }
        
        @Override
        public Number getMinimumAccessibleValue() {
            return JSlider.this.getMinimum();
        }
        
        @Override
        public Number getMaximumAccessibleValue() {
            final BoundedRangeModel model = JSlider.this.getModel();
            return model.getMaximum() - model.getExtent();
        }
    }
}
