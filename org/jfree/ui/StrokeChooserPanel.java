package org.jfree.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Component;
import javax.swing.ListCellRenderer;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;

public class StrokeChooserPanel extends JPanel
{
    private JComboBox selector;
    
    public StrokeChooserPanel(final StrokeSample current, final StrokeSample[] available) {
        this.setLayout(new BorderLayout());
        (this.selector = new JComboBox((E[])available)).setSelectedItem(current);
        this.selector.setRenderer(new StrokeSample(new BasicStroke(1.0f)));
        this.add(this.selector);
        this.selector.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                StrokeChooserPanel.this.getSelector().transferFocus();
            }
        });
    }
    
    public Stroke getSelectedStroke() {
        final StrokeSample sample = (StrokeSample)this.selector.getSelectedItem();
        return sample.getStroke();
    }
    
    protected final JComboBox getSelector() {
        return this.selector;
    }
}
