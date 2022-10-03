package javax.swing.plaf.basic;

import javax.swing.plaf.UIResource;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import sun.reflect.misc.MethodUtil;
import javax.swing.border.Border;
import java.awt.Component;
import javax.swing.JTextField;
import java.awt.event.FocusListener;
import javax.swing.ComboBoxEditor;

public class BasicComboBoxEditor implements ComboBoxEditor, FocusListener
{
    protected JTextField editor;
    private Object oldValue;
    
    public BasicComboBoxEditor() {
        this.editor = this.createEditorComponent();
    }
    
    @Override
    public Component getEditorComponent() {
        return this.editor;
    }
    
    protected JTextField createEditorComponent() {
        final BorderlessTextField borderlessTextField = new BorderlessTextField("", 9);
        borderlessTextField.setBorder(null);
        return borderlessTextField;
    }
    
    @Override
    public void setItem(final Object oldValue) {
        String string;
        if (oldValue != null) {
            string = oldValue.toString();
            if (string == null) {
                string = "";
            }
            this.oldValue = oldValue;
        }
        else {
            string = "";
        }
        if (!string.equals(this.editor.getText())) {
            this.editor.setText(string);
        }
    }
    
    @Override
    public Object getItem() {
        Object o = this.editor.getText();
        if (this.oldValue != null && !(this.oldValue instanceof String)) {
            if (o.equals(this.oldValue.toString())) {
                return this.oldValue;
            }
            final Class<?> class1 = this.oldValue.getClass();
            try {
                o = MethodUtil.invoke(MethodUtil.getMethod(class1, "valueOf", new Class[] { String.class }), this.oldValue, new Object[] { this.editor.getText() });
            }
            catch (final Exception ex) {}
        }
        return o;
    }
    
    @Override
    public void selectAll() {
        this.editor.selectAll();
        this.editor.requestFocus();
    }
    
    @Override
    public void focusGained(final FocusEvent focusEvent) {
    }
    
    @Override
    public void focusLost(final FocusEvent focusEvent) {
    }
    
    @Override
    public void addActionListener(final ActionListener actionListener) {
        this.editor.addActionListener(actionListener);
    }
    
    @Override
    public void removeActionListener(final ActionListener actionListener) {
        this.editor.removeActionListener(actionListener);
    }
    
    static class BorderlessTextField extends JTextField
    {
        public BorderlessTextField(final String s, final int n) {
            super(s, n);
        }
        
        @Override
        public void setText(final String text) {
            if (this.getText().equals(text)) {
                return;
            }
            super.setText(text);
        }
        
        @Override
        public void setBorder(final Border border) {
            if (!(border instanceof UIResource)) {
                super.setBorder(border);
            }
        }
    }
    
    public static class UIResource extends BasicComboBoxEditor implements javax.swing.plaf.UIResource
    {
    }
}
