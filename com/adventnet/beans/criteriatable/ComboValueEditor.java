package com.adventnet.beans.criteriatable;

import java.awt.Font;
import java.awt.Color;
import javax.swing.JComboBox;

public class ComboValueEditor extends JComboBox implements AttributeValueEditorComponent
{
    public ComboValueEditor(final Object[] array) {
        for (int i = 0; i < array.length; ++i) {
            this.addItem(array[i]);
        }
        this.setBackground(Color.white);
        final Font font = this.getFont();
        this.setFont(new Font(font.getName(), 0, font.getSize()));
    }
    
    public Object getAttributeValue() {
        return this.getSelectedItem();
    }
    
    public void setAttributeValue(final Object selectedItem) {
        this.setSelectedItem(selectedItem);
    }
    
    public void initializeComponent() {
        this.setSelectedIndex(0);
    }
}
