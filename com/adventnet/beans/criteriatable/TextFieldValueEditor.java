package com.adventnet.beans.criteriatable;

import javax.swing.text.Document;
import javax.swing.JTextField;

public class TextFieldValueEditor extends JTextField implements AttributeValueEditorComponent
{
    public TextFieldValueEditor() {
    }
    
    public TextFieldValueEditor(final Document document) {
        this.setDocument(document);
    }
    
    public Object getAttributeValue() {
        return this.getText();
    }
    
    public void setAttributeValue(final Object o) {
        this.setText(o.toString());
    }
    
    public void initializeComponent() {
        this.setText("");
    }
}
