package com.adventnet.beans.criteriatable;

import java.awt.event.FocusEvent;
import java.awt.event.ActionEvent;
import java.util.EventObject;
import javax.swing.JComponent;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import java.awt.event.FocusListener;
import javax.swing.table.TableCellEditor;
import java.awt.event.ActionListener;

class AttributeValueCellEditor implements ActionListener, TableCellEditor, FocusListener
{
    protected EventListenerList listenerList;
    protected ChangeEvent changeEvent;
    private JTextField tf;
    private AttributeValueEditorComponent component;
    private AttributeValueEditorComponentsTable valueEditorsByType;
    
    public AttributeValueCellEditor() {
        this.changeEvent = new ChangeEvent(this);
        this.tf = new JTextField();
        this.valueEditorsByType = new AttributeValueEditorComponentsTable();
        this.listenerList = new EventListenerList();
    }
    
    AttributeValueEditorComponentsTable getAttributeValueEditorComponentsTable() {
        return this.valueEditorsByType;
    }
    
    protected void fireEditingStopped() {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = 0; i < listenerList.length; ++i) {
            if (listenerList[i] == CellEditorListener.class) {
                ((CellEditorListener)listenerList[i + 1]).editingStopped(this.changeEvent);
            }
        }
    }
    
    protected void fireEditingCanceled() {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = 0; i < listenerList.length; ++i) {
            if (listenerList[i] == CellEditorListener.class) {
                ((CellEditorListener)listenerList[i + 1]).editingCanceled(this.changeEvent);
            }
        }
    }
    
    public void addCellEditorListener(final CellEditorListener cellEditorListener) {
        this.listenerList.add(CellEditorListener.class, cellEditorListener);
    }
    
    public void cancelCellEditing() {
        this.fireEditingCanceled();
    }
    
    public Object getCellEditorValue() {
        if (this.component == null) {
            return this.tf.getText();
        }
        return this.component.getAttributeValue();
    }
    
    private AttributeValueEditorComponent getEditor(final Attribute attribute) {
        AttributeValueEditorComponent valueEditorComponent = attribute.getValueEditorComponent();
        if (valueEditorComponent == null) {
            valueEditorComponent = this.valueEditorsByType.get(attribute.getAttributeClass());
        }
        if (valueEditorComponent == null) {
            valueEditorComponent = this.valueEditorsByType.get(Object.class);
        }
        return valueEditorComponent;
    }
    
    public Component getTableCellEditorComponent(final JTable table, final Object attributeValue, final boolean b, final int n, final int n2) {
        this.component = null;
        final Object value = table.getValueAt(n, 1);
        if (value instanceof Attribute) {
            final AttributeValueEditorComponent editor = this.getEditor((Attribute)value);
            if (editor != null && editor instanceof JComponent) {
                editor.addActionListener(this);
                if (attributeValue != null) {
                    editor.setAttributeValue(attributeValue);
                }
                else {
                    editor.initializeComponent();
                }
                this.component = editor;
                return (Component)editor;
            }
        }
        if (attributeValue != null) {
            this.tf.setText(attributeValue.toString());
        }
        else {
            this.tf.setText("");
        }
        return this.tf;
    }
    
    public boolean isCellEditable(final EventObject eventObject) {
        return true;
    }
    
    public void removeCellEditorListener(final CellEditorListener cellEditorListener) {
        this.listenerList.remove(CellEditorListener.class, cellEditorListener);
    }
    
    public boolean shouldSelectCell(final EventObject eventObject) {
        return true;
    }
    
    public boolean stopCellEditing() {
        this.fireEditingStopped();
        return true;
    }
    
    public void actionPerformed(final ActionEvent actionEvent) {
        this.stopCellEditing();
    }
    
    public void focusGained(final FocusEvent focusEvent) {
    }
    
    public void focusLost(final FocusEvent focusEvent) {
        this.stopCellEditing();
    }
}
