package com.adventnet.beans.criteriatable;

import java.util.Date;
import javax.swing.SpinnerDateModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SpinnerModel;
import java.util.Vector;
import javax.swing.JSpinner;

public class SpinnerValueEditor extends JSpinner implements AttributeValueEditorComponent
{
    Vector listeners;
    
    public SpinnerValueEditor(final SpinnerModel model) {
        this.setModel(model);
        this.listeners = new Vector(1);
        ((DefaultEditor)this.getEditor()).getTextField().addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                SpinnerValueEditor.this.fireActionEvent();
            }
        });
    }
    
    public Object getAttributeValue() {
        return this.getValue();
    }
    
    public void setAttributeValue(final Object value) {
        this.setValue(value);
    }
    
    protected void fireActionEvent() {
        final ActionEvent actionEvent = new ActionEvent(this, 1001, null);
        for (int i = 0; i < this.listeners.size(); ++i) {
            ((ActionListener)this.listeners.elementAt(i)).actionPerformed(actionEvent);
        }
    }
    
    public void addActionListener(final ActionListener actionListener) {
        this.listeners.add(actionListener);
    }
    
    public void removeActionListener(final ActionListener actionListener) {
        this.listeners.remove(actionListener);
    }
    
    public void initializeComponent() {
        if (this.getModel() instanceof SpinnerDateModel) {
            this.setValue(new Date());
        }
    }
}
