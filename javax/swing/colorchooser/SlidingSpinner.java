package javax.swing.colorchooser;

import javax.swing.event.ChangeEvent;
import javax.swing.SpinnerModel;
import javax.swing.JSpinner;
import javax.swing.JSlider;
import javax.swing.SpinnerNumberModel;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;

final class SlidingSpinner implements ChangeListener
{
    private final ColorPanel panel;
    private final JComponent label;
    private final SpinnerNumberModel model;
    private final JSlider slider;
    private final JSpinner spinner;
    private float value;
    private boolean internal;
    
    SlidingSpinner(final ColorPanel panel, final JComponent label) {
        this.model = new SpinnerNumberModel();
        this.slider = new JSlider();
        this.spinner = new JSpinner(this.model);
        this.panel = panel;
        this.label = label;
        this.slider.addChangeListener(this);
        this.spinner.addChangeListener(this);
        final JSpinner.DefaultEditor defaultEditor = (JSpinner.DefaultEditor)this.spinner.getEditor();
        ValueFormatter.init(3, false, defaultEditor.getTextField());
        defaultEditor.setFocusable(false);
        this.spinner.setFocusable(false);
    }
    
    JComponent getLabel() {
        return this.label;
    }
    
    JSlider getSlider() {
        return this.slider;
    }
    
    JSpinner getSpinner() {
        return this.spinner;
    }
    
    float getValue() {
        return this.value;
    }
    
    void setValue(final float value) {
        final int minimum = this.slider.getMinimum();
        final int maximum = this.slider.getMaximum();
        this.internal = true;
        this.slider.setValue(minimum + (int)(value * (maximum - minimum)));
        this.spinner.setValue(this.slider.getValue());
        this.internal = false;
        this.value = value;
    }
    
    void setRange(final int minimum, final int maximum) {
        this.internal = true;
        this.slider.setMinimum(minimum);
        this.slider.setMaximum(maximum);
        this.model.setMinimum(minimum);
        this.model.setMaximum(maximum);
        this.internal = false;
    }
    
    void setVisible(final boolean visible) {
        this.label.setVisible(visible);
        this.slider.setVisible(visible);
        this.spinner.setVisible(visible);
    }
    
    @Override
    public void stateChanged(final ChangeEvent changeEvent) {
        if (!this.internal) {
            if (this.spinner == changeEvent.getSource()) {
                final Object value = this.spinner.getValue();
                if (value instanceof Integer) {
                    this.internal = true;
                    this.slider.setValue((int)value);
                    this.internal = false;
                }
            }
            final int value2 = this.slider.getValue();
            this.internal = true;
            this.spinner.setValue(value2);
            this.internal = false;
            final int minimum = this.slider.getMinimum();
            this.value = (value2 - minimum) / (float)(this.slider.getMaximum() - minimum);
            this.panel.colorChanged();
        }
    }
}
