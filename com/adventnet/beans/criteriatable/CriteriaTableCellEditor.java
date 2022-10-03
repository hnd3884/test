package com.adventnet.beans.criteriatable;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.awt.Font;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import javax.swing.DefaultCellEditor;

class CriteriaTableCellEditor extends DefaultCellEditor implements FocusListener, ActionListener
{
    protected ComparatorsTable comparatorsByClassName;
    protected AttributeCellEditor attrCellEditor;
    private int column;
    
    public CriteriaTableCellEditor() {
        super(new JComboBox());
        this.comparatorsByClassName = new ComparatorsTable();
    }
    
    public void setAttributeCellEditor(final AttributeCellEditor attrCellEditor) {
        (this.attrCellEditor = attrCellEditor).addActionListener(this);
    }
    
    String[] getComparators(final Attribute attribute) {
        String[] comparators = attribute.getComparators();
        if (comparators == null) {
            comparators = this.comparatorsByClassName.get(attribute.getAttributeClass());
        }
        return comparators;
    }
    
    ComparatorsTable getComparatorsTable() {
        return this.comparatorsByClassName;
    }
    
    public Component getTableCellEditorComponent(final JTable table, final Object selectedItem, final boolean b, final int n, final int column) {
        this.column = column;
        final JComboBox comboBox = (JComboBox)super.getTableCellEditorComponent(table, selectedItem, b, n, column);
        comboBox.setBackground(table.getBackground());
        comboBox.setForeground(table.getForeground());
        final Font font = comboBox.getFont();
        comboBox.setFont(new Font(font.getName(), 0, font.getSize()));
        comboBox.removeAllItems();
        final AttributeModel attributeModel = ((CriteriaTable)table).getAttributeModel();
        if (column == 1) {
            if (table.getValueAt(n, 0) != null) {
                if (table.getValueAt(n, 0).equals("GROUP_START_INDEX")) {
                    comboBox.addItem("(");
                }
                else {
                    comboBox.addItem(")");
                }
                for (int i = 0; i < attributeModel.getGroupingElementsCount(); ++i) {
                    comboBox.addItem(attributeModel.getGroupingElement(i));
                }
            }
            else {
                final ArrayList list = new ArrayList();
                for (int j = 0; j < attributeModel.getAttributeCount(); ++j) {
                    final Attribute attribute = attributeModel.getAttribute(j);
                    list.add(attribute);
                    comboBox.addItem(attribute);
                }
                if (this.attrCellEditor != null) {
                    return this.attrCellEditor.getAttributeCellEditorComponent(table, selectedItem, b, n, column, (Attribute[])list.toArray(new Attribute[0]));
                }
            }
        }
        else if (column == 2) {
            final String[] comparators = this.getComparators((Attribute)table.getValueAt(n, 1));
            if (comparators != null) {
                for (int k = 0; k < comparators.length; ++k) {
                    comboBox.addItem(comparators[k]);
                }
            }
        }
        else if (column == 4) {
            for (int l = 0; l < attributeModel.getOperatorsCount(); ++l) {
                comboBox.addItem(attributeModel.getOperator(l));
            }
        }
        if (selectedItem != null) {
            comboBox.setSelectedItem(selectedItem);
        }
        return comboBox;
    }
    
    public void focusGained(final FocusEvent focusEvent) {
    }
    
    public void focusLost(final FocusEvent focusEvent) {
        this.stopCellEditing();
    }
    
    public Object getCellEditorValue() {
        if (this.column == 1 && this.attrCellEditor != null) {
            return this.attrCellEditor.getCellEditorValue();
        }
        return super.getCellEditorValue();
    }
    
    public void actionPerformed(final ActionEvent actionEvent) {
        this.stopCellEditing();
    }
}
