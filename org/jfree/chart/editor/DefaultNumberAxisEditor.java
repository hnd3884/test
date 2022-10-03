package org.jfree.chart.editor;

import java.awt.event.FocusEvent;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import org.jfree.ui.StrokeChooserPanel;
import java.awt.event.ActionEvent;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.Component;
import javax.swing.BorderFactory;
import java.awt.LayoutManager;
import javax.swing.JPanel;
import org.jfree.layout.LCBLayout;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Color;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.NumberAxis;
import java.util.ResourceBundle;
import org.jfree.ui.StrokeSample;
import org.jfree.ui.PaintSample;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import java.awt.event.FocusListener;

class DefaultNumberAxisEditor extends DefaultAxisEditor implements FocusListener
{
    private boolean autoRange;
    private double minimumValue;
    private double maximumValue;
    private JCheckBox autoRangeCheckBox;
    private JTextField minimumRangeValue;
    private JTextField maximumRangeValue;
    private PaintSample gridPaintSample;
    private StrokeSample gridStrokeSample;
    private StrokeSample[] availableStrokeSamples;
    protected static ResourceBundle localizationResources;
    
    public DefaultNumberAxisEditor(final NumberAxis axis) {
        super(axis);
        this.autoRange = axis.isAutoRange();
        this.minimumValue = axis.getLowerBound();
        this.maximumValue = axis.getUpperBound();
        this.gridPaintSample = new PaintSample((Paint)Color.blue);
        this.gridStrokeSample = new StrokeSample((Stroke)new BasicStroke(1.0f));
        (this.availableStrokeSamples = new StrokeSample[3])[0] = new StrokeSample((Stroke)new BasicStroke(1.0f));
        this.availableStrokeSamples[1] = new StrokeSample((Stroke)new BasicStroke(2.0f));
        this.availableStrokeSamples[2] = new StrokeSample((Stroke)new BasicStroke(3.0f));
        final JTabbedPane other = this.getOtherTabs();
        final JPanel range = new JPanel((LayoutManager)new LCBLayout(3));
        range.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        range.add(new JPanel());
        (this.autoRangeCheckBox = new JCheckBox(DefaultNumberAxisEditor.localizationResources.getString("Auto-adjust_range"), this.autoRange)).setActionCommand("AutoRangeOnOff");
        this.autoRangeCheckBox.addActionListener(this);
        range.add(this.autoRangeCheckBox);
        range.add(new JPanel());
        range.add(new JLabel(DefaultNumberAxisEditor.localizationResources.getString("Minimum_range_value")));
        (this.minimumRangeValue = new JTextField(Double.toString(this.minimumValue))).setEnabled(!this.autoRange);
        this.minimumRangeValue.setActionCommand("MinimumRange");
        this.minimumRangeValue.addActionListener(this);
        this.minimumRangeValue.addFocusListener(this);
        range.add(this.minimumRangeValue);
        range.add(new JPanel());
        range.add(new JLabel(DefaultNumberAxisEditor.localizationResources.getString("Maximum_range_value")));
        (this.maximumRangeValue = new JTextField(Double.toString(this.maximumValue))).setEnabled(!this.autoRange);
        this.maximumRangeValue.setActionCommand("MaximumRange");
        this.maximumRangeValue.addActionListener(this);
        this.maximumRangeValue.addFocusListener(this);
        range.add(this.maximumRangeValue);
        range.add(new JPanel());
        other.add(DefaultNumberAxisEditor.localizationResources.getString("Range"), range);
    }
    
    public boolean isAutoRange() {
        return this.autoRange;
    }
    
    public double getMinimumValue() {
        return this.minimumValue;
    }
    
    public double getMaximumValue() {
        return this.maximumValue;
    }
    
    public void actionPerformed(final ActionEvent event) {
        final String command = event.getActionCommand();
        if (command.equals("GridStroke")) {
            this.attemptGridStrokeSelection();
        }
        else if (command.equals("GridPaint")) {
            this.attemptGridPaintSelection();
        }
        else if (command.equals("AutoRangeOnOff")) {
            this.toggleAutoRange();
        }
        else if (command.equals("MinimumRange")) {
            this.validateMinimum();
        }
        else if (command.equals("MaximumRange")) {
            this.validateMaximum();
        }
        else {
            super.actionPerformed(event);
        }
    }
    
    private void attemptGridStrokeSelection() {
        final StrokeChooserPanel panel = new StrokeChooserPanel((StrokeSample)null, this.availableStrokeSamples);
        final int result = JOptionPane.showConfirmDialog(this, panel, DefaultNumberAxisEditor.localizationResources.getString("Stroke_Selection"), 2, -1);
        if (result == 0) {
            this.gridStrokeSample.setStroke(panel.getSelectedStroke());
        }
    }
    
    private void attemptGridPaintSelection() {
        final Color c = JColorChooser.showDialog(this, DefaultNumberAxisEditor.localizationResources.getString("Grid_Color"), Color.blue);
        if (c != null) {
            this.gridPaintSample.setPaint((Paint)c);
        }
    }
    
    public void focusGained(final FocusEvent event) {
    }
    
    public void focusLost(final FocusEvent event) {
        if (event.getSource() == this.minimumRangeValue) {
            this.validateMinimum();
        }
        else if (event.getSource() == this.maximumRangeValue) {
            this.validateMaximum();
        }
    }
    
    public void toggleAutoRange() {
        this.autoRange = this.autoRangeCheckBox.isSelected();
        if (this.autoRange) {
            this.minimumRangeValue.setText(Double.toString(this.minimumValue));
            this.minimumRangeValue.setEnabled(false);
            this.maximumRangeValue.setText(Double.toString(this.maximumValue));
            this.maximumRangeValue.setEnabled(false);
        }
        else {
            this.minimumRangeValue.setEnabled(true);
            this.maximumRangeValue.setEnabled(true);
        }
    }
    
    public void validateMinimum() {
        double newMin;
        try {
            newMin = Double.parseDouble(this.minimumRangeValue.getText());
            if (newMin >= this.maximumValue) {
                newMin = this.minimumValue;
            }
        }
        catch (final NumberFormatException e) {
            newMin = this.minimumValue;
        }
        this.minimumValue = newMin;
        this.minimumRangeValue.setText(Double.toString(this.minimumValue));
    }
    
    public void validateMaximum() {
        double newMax;
        try {
            newMax = Double.parseDouble(this.maximumRangeValue.getText());
            if (newMax <= this.minimumValue) {
                newMax = this.maximumValue;
            }
        }
        catch (final NumberFormatException e) {
            newMax = this.maximumValue;
        }
        this.maximumValue = newMax;
        this.maximumRangeValue.setText(Double.toString(this.maximumValue));
    }
    
    public void setAxisProperties(final Axis axis) {
        super.setAxisProperties(axis);
        final NumberAxis numberAxis = (NumberAxis)axis;
        numberAxis.setAutoRange(this.autoRange);
        if (!this.autoRange) {
            numberAxis.setRange(this.minimumValue, this.maximumValue);
        }
    }
    
    static {
        DefaultNumberAxisEditor.localizationResources = ResourceBundle.getBundle("org.jfree.chart.editor.LocalizationBundle");
    }
}
