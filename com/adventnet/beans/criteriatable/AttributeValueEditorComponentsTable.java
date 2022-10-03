package com.adventnet.beans.criteriatable;

import javax.swing.text.Document;
import com.adventnet.beans.utils.JTextFieldFilter;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerDateModel;
import java.util.Hashtable;

public class AttributeValueEditorComponentsTable extends Hashtable
{
    public AttributeValueEditorComponentsTable() {
        final ComboValueEditor comboValueEditor = new ComboValueEditor(new Object[] { Boolean.TRUE, Boolean.FALSE });
        final SpinnerValueEditor spinnerValueEditor = new SpinnerValueEditor(new SpinnerDateModel());
        final TextFieldValueEditor textFieldValueEditor = new TextFieldValueEditor();
        final TextFieldValueEditor textFieldValueEditor2 = new TextFieldValueEditor(new JTextFieldFilter("0123456789"));
        final TextFieldValueEditor textFieldValueEditor3 = new TextFieldValueEditor(new JTextFieldFilter("0123456789."));
        this.put(Attribute.STRING_TYPE, textFieldValueEditor);
        this.put(Attribute.BOOLEAN_TYPE, comboValueEditor);
        this.put(Attribute.INTEGER_TYPE, textFieldValueEditor2);
        this.put(Attribute.LONG_TYPE, textFieldValueEditor2);
        this.put(Attribute.FLOAT_TYPE, textFieldValueEditor3);
        this.put(Attribute.DOUBLE_TYPE, textFieldValueEditor3);
        this.put(Attribute.DATE_TYPE, spinnerValueEditor);
        this.put(Attribute.OBJECT_TYPE, spinnerValueEditor);
    }
    
    public void setDefaultEditorForType(final Class clazz, final AttributeValueEditorComponent attributeValueEditorComponent) {
        if (clazz != null && attributeValueEditorComponent != null) {
            this.put(clazz, attributeValueEditorComponent);
        }
    }
    
    public AttributeValueEditorComponent getDefaultEditorForType(final Class clazz) {
        return this.get(clazz);
    }
}
