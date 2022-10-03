package javax.swing.text;

import java.io.Serializable;
import javax.swing.JFormattedTextField;

public class DefaultFormatterFactory extends JFormattedTextField.AbstractFormatterFactory implements Serializable
{
    private JFormattedTextField.AbstractFormatter defaultFormat;
    private JFormattedTextField.AbstractFormatter displayFormat;
    private JFormattedTextField.AbstractFormatter editFormat;
    private JFormattedTextField.AbstractFormatter nullFormat;
    
    public DefaultFormatterFactory() {
    }
    
    public DefaultFormatterFactory(final JFormattedTextField.AbstractFormatter abstractFormatter) {
        this(abstractFormatter, null);
    }
    
    public DefaultFormatterFactory(final JFormattedTextField.AbstractFormatter abstractFormatter, final JFormattedTextField.AbstractFormatter abstractFormatter2) {
        this(abstractFormatter, abstractFormatter2, null);
    }
    
    public DefaultFormatterFactory(final JFormattedTextField.AbstractFormatter abstractFormatter, final JFormattedTextField.AbstractFormatter abstractFormatter2, final JFormattedTextField.AbstractFormatter abstractFormatter3) {
        this(abstractFormatter, abstractFormatter2, abstractFormatter3, null);
    }
    
    public DefaultFormatterFactory(final JFormattedTextField.AbstractFormatter defaultFormat, final JFormattedTextField.AbstractFormatter displayFormat, final JFormattedTextField.AbstractFormatter editFormat, final JFormattedTextField.AbstractFormatter nullFormat) {
        this.defaultFormat = defaultFormat;
        this.displayFormat = displayFormat;
        this.editFormat = editFormat;
        this.nullFormat = nullFormat;
    }
    
    public void setDefaultFormatter(final JFormattedTextField.AbstractFormatter defaultFormat) {
        this.defaultFormat = defaultFormat;
    }
    
    public JFormattedTextField.AbstractFormatter getDefaultFormatter() {
        return this.defaultFormat;
    }
    
    public void setDisplayFormatter(final JFormattedTextField.AbstractFormatter displayFormat) {
        this.displayFormat = displayFormat;
    }
    
    public JFormattedTextField.AbstractFormatter getDisplayFormatter() {
        return this.displayFormat;
    }
    
    public void setEditFormatter(final JFormattedTextField.AbstractFormatter editFormat) {
        this.editFormat = editFormat;
    }
    
    public JFormattedTextField.AbstractFormatter getEditFormatter() {
        return this.editFormat;
    }
    
    public void setNullFormatter(final JFormattedTextField.AbstractFormatter nullFormat) {
        this.nullFormat = nullFormat;
    }
    
    public JFormattedTextField.AbstractFormatter getNullFormatter() {
        return this.nullFormat;
    }
    
    @Override
    public JFormattedTextField.AbstractFormatter getFormatter(final JFormattedTextField formattedTextField) {
        JFormattedTextField.AbstractFormatter abstractFormatter = null;
        if (formattedTextField == null) {
            return null;
        }
        if (formattedTextField.getValue() == null) {
            abstractFormatter = this.getNullFormatter();
        }
        if (abstractFormatter == null) {
            if (formattedTextField.hasFocus()) {
                abstractFormatter = this.getEditFormatter();
            }
            else {
                abstractFormatter = this.getDisplayFormatter();
            }
            if (abstractFormatter == null) {
                abstractFormatter = this.getDefaultFormatter();
            }
        }
        return abstractFormatter;
    }
}
