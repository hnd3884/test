package javax.swing.colorchooser;

import javax.swing.SwingUtilities;
import java.awt.event.FocusEvent;
import java.text.ParseException;
import java.util.Locale;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.DocumentFilter;
import java.awt.event.FocusListener;
import javax.swing.JFormattedTextField;

final class ValueFormatter extends JFormattedTextField.AbstractFormatter implements FocusListener, Runnable
{
    private final DocumentFilter filter;
    private final int length;
    private final int radix;
    private JFormattedTextField text;
    
    static void init(final int columns, final boolean b, final JFormattedTextField formattedTextField) {
        final ValueFormatter valueFormatter = new ValueFormatter(columns, b);
        formattedTextField.setColumns(columns);
        formattedTextField.setFormatterFactory(new DefaultFormatterFactory(valueFormatter));
        formattedTextField.setHorizontalAlignment(4);
        formattedTextField.setMinimumSize(formattedTextField.getPreferredSize());
        formattedTextField.addFocusListener(valueFormatter);
    }
    
    ValueFormatter(final int length, final boolean b) {
        this.filter = new DocumentFilter() {
            @Override
            public void remove(final FilterBypass filterBypass, final int n, final int n2) throws BadLocationException {
                if (ValueFormatter.this.isValid(filterBypass.getDocument().getLength() - n2)) {
                    filterBypass.remove(n, n2);
                }
            }
            
            @Override
            public void replace(final FilterBypass filterBypass, final int n, final int n2, final String s, final AttributeSet set) throws BadLocationException {
                if (ValueFormatter.this.isValid(filterBypass.getDocument().getLength() + s.length() - n2) && ValueFormatter.this.isValid(s)) {
                    filterBypass.replace(n, n2, s.toUpperCase(Locale.ENGLISH), set);
                }
            }
            
            @Override
            public void insertString(final FilterBypass filterBypass, final int n, final String s, final AttributeSet set) throws BadLocationException {
                if (ValueFormatter.this.isValid(filterBypass.getDocument().getLength() + s.length()) && ValueFormatter.this.isValid(s)) {
                    filterBypass.insertString(n, s.toUpperCase(Locale.ENGLISH), set);
                }
            }
        };
        this.length = length;
        this.radix = (b ? 16 : 10);
    }
    
    @Override
    public Object stringToValue(final String s) throws ParseException {
        try {
            return Integer.valueOf(s, this.radix);
        }
        catch (final NumberFormatException ex) {
            final ParseException ex2 = new ParseException("illegal format", 0);
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    @Override
    public String valueToString(final Object o) throws ParseException {
        if (!(o instanceof Integer)) {
            throw new ParseException("illegal object", 0);
        }
        if (this.radix == 10) {
            return o.toString();
        }
        int intValue = (int)o;
        int length = this.length;
        final char[] array = new char[length];
        while (0 < length--) {
            array[length] = Character.forDigit(intValue & 0xF, this.radix);
            intValue >>= 4;
        }
        return new String(array).toUpperCase(Locale.ENGLISH);
    }
    
    @Override
    protected DocumentFilter getDocumentFilter() {
        return this.filter;
    }
    
    @Override
    public void focusGained(final FocusEvent focusEvent) {
        final Object source = focusEvent.getSource();
        if (source instanceof JFormattedTextField) {
            this.text = (JFormattedTextField)source;
            SwingUtilities.invokeLater(this);
        }
    }
    
    @Override
    public void focusLost(final FocusEvent focusEvent) {
    }
    
    @Override
    public void run() {
        if (this.text != null) {
            this.text.selectAll();
        }
    }
    
    private boolean isValid(final int n) {
        return 0 <= n && n <= this.length;
    }
    
    private boolean isValid(final String s) {
        for (int length = s.length(), i = 0; i < length; ++i) {
            if (Character.digit(s.charAt(i), this.radix) < 0) {
                return false;
            }
        }
        return true;
    }
}
