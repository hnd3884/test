package javax.swing.text;

import java.lang.reflect.Constructor;
import sun.swing.SwingUtilities2;
import sun.reflect.misc.ReflectUtil;
import java.util.Iterator;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.NumberFormat;

public class NumberFormatter extends InternationalFormatter
{
    private String specialChars;
    
    public NumberFormatter() {
        this(NumberFormat.getNumberInstance());
    }
    
    public NumberFormatter(final NumberFormat format) {
        super(format);
        this.setFormat(format);
        this.setAllowsInvalid(true);
        this.setCommitsOnValidEdit(false);
        this.setOverwriteMode(false);
    }
    
    @Override
    public void setFormat(final Format format) {
        super.setFormat(format);
        final DecimalFormatSymbols decimalFormatSymbols = this.getDecimalFormatSymbols();
        if (decimalFormatSymbols != null) {
            final StringBuilder sb = new StringBuilder();
            sb.append(decimalFormatSymbols.getCurrencySymbol());
            sb.append(decimalFormatSymbols.getDecimalSeparator());
            sb.append(decimalFormatSymbols.getGroupingSeparator());
            sb.append(decimalFormatSymbols.getInfinity());
            sb.append(decimalFormatSymbols.getInternationalCurrencySymbol());
            sb.append(decimalFormatSymbols.getMinusSign());
            sb.append(decimalFormatSymbols.getMonetaryDecimalSeparator());
            sb.append(decimalFormatSymbols.getNaN());
            sb.append(decimalFormatSymbols.getPercent());
            sb.append('+');
            this.specialChars = sb.toString();
        }
        else {
            this.specialChars = "";
        }
    }
    
    @Override
    Object stringToValue(final String s, final Format format) throws ParseException {
        if (format == null) {
            return s;
        }
        return this.convertValueToValueClass(format.parseObject(s), this.getValueClass());
    }
    
    private Object convertValueToValueClass(final Object o, final Class clazz) {
        if (clazz != null && o instanceof Number) {
            final Number n = (Number)o;
            if (clazz == Integer.class) {
                return n.intValue();
            }
            if (clazz == Long.class) {
                return n.longValue();
            }
            if (clazz == Float.class) {
                return n.floatValue();
            }
            if (clazz == Double.class) {
                return n.doubleValue();
            }
            if (clazz == Byte.class) {
                return n.byteValue();
            }
            if (clazz == Short.class) {
                return n.shortValue();
            }
        }
        return o;
    }
    
    private char getPositiveSign() {
        return '+';
    }
    
    private char getMinusSign() {
        final DecimalFormatSymbols decimalFormatSymbols = this.getDecimalFormatSymbols();
        if (decimalFormatSymbols != null) {
            return decimalFormatSymbols.getMinusSign();
        }
        return '-';
    }
    
    private char getDecimalSeparator() {
        final DecimalFormatSymbols decimalFormatSymbols = this.getDecimalFormatSymbols();
        if (decimalFormatSymbols != null) {
            return decimalFormatSymbols.getDecimalSeparator();
        }
        return '.';
    }
    
    private DecimalFormatSymbols getDecimalFormatSymbols() {
        final Format format = this.getFormat();
        if (format instanceof DecimalFormat) {
            return ((DecimalFormat)format).getDecimalFormatSymbols();
        }
        return null;
    }
    
    @Override
    boolean isLegalInsertText(final String s) {
        if (this.getAllowsInvalid()) {
            return true;
        }
        for (int i = s.length() - 1; i >= 0; --i) {
            final char char1 = s.charAt(i);
            if (!Character.isDigit(char1) && this.specialChars.indexOf(char1) == -1) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    boolean isLiteral(final Map map) {
        if (super.isLiteral(map)) {
            return true;
        }
        if (map == null) {
            return false;
        }
        int size = map.size();
        if (map.get(NumberFormat.Field.GROUPING_SEPARATOR) != null) {
            --size;
            if (map.get(NumberFormat.Field.INTEGER) != null) {
                --size;
            }
        }
        if (map.get(NumberFormat.Field.EXPONENT_SYMBOL) != null) {
            --size;
        }
        if (map.get(NumberFormat.Field.PERCENT) != null) {
            --size;
        }
        if (map.get(NumberFormat.Field.PERMILLE) != null) {
            --size;
        }
        if (map.get(NumberFormat.Field.CURRENCY) != null) {
            --size;
        }
        if (map.get(NumberFormat.Field.SIGN) != null) {
            --size;
        }
        return size == 0;
    }
    
    @Override
    boolean isNavigatable(final int n) {
        return super.isNavigatable(n) || this.getBufferedChar(n) == this.getDecimalSeparator();
    }
    
    private NumberFormat.Field getFieldFrom(int index, final int n) {
        if (this.isValidMask()) {
            final int length = this.getFormattedTextField().getDocument().getLength();
            final AttributedCharacterIterator iterator = this.getIterator();
            if (index >= length) {
                index += n;
            }
            while (index >= 0 && index < length) {
                iterator.setIndex(index);
                final Map<AttributedCharacterIterator.Attribute, Object> attributes = iterator.getAttributes();
                if (attributes != null && attributes.size() > 0) {
                    for (final Object next : attributes.keySet()) {
                        if (next instanceof NumberFormat.Field) {
                            return (NumberFormat.Field)next;
                        }
                    }
                }
                index += n;
            }
        }
        return null;
    }
    
    @Override
    void replace(final DocumentFilter.FilterBypass filterBypass, final int n, final int n2, final String s, final AttributeSet set) throws BadLocationException {
        if (!this.getAllowsInvalid() && n2 == 0 && s != null && s.length() == 1 && this.toggleSignIfNecessary(filterBypass, n, s.charAt(0))) {
            return;
        }
        super.replace(filterBypass, n, n2, s, set);
    }
    
    private boolean toggleSignIfNecessary(final DocumentFilter.FilterBypass filterBypass, final int n, final char c) throws BadLocationException {
        if (c == this.getMinusSign() || c == this.getPositiveSign()) {
            final NumberFormat.Field field = this.getFieldFrom(n, -1);
            try {
                Object o;
                if (field == null || (field != NumberFormat.Field.EXPONENT && field != NumberFormat.Field.EXPONENT_SYMBOL && field != NumberFormat.Field.EXPONENT_SIGN)) {
                    o = this.toggleSign(c == this.getPositiveSign());
                }
                else {
                    o = this.toggleExponentSign(n, c);
                }
                if (o != null && this.isValidValue(o, false)) {
                    final int literalCountTo = this.getLiteralCountTo(n);
                    final String valueToString = this.valueToString(o);
                    filterBypass.remove(0, filterBypass.getDocument().getLength());
                    filterBypass.insertString(0, valueToString, null);
                    this.updateValue(o);
                    this.repositionCursor(this.getLiteralCountTo(n) - literalCountTo + n, 1);
                    return true;
                }
            }
            catch (final ParseException ex) {
                this.invalidEdit();
            }
        }
        return false;
    }
    
    private Object toggleSign(final boolean b) throws ParseException {
        final Object stringToValue = this.stringToValue(this.getFormattedTextField().getText());
        if (stringToValue != null) {
            String s = stringToValue.toString();
            if (s != null && s.length() > 0) {
                if (b) {
                    if (s.charAt(0) == '-') {
                        s = s.substring(1);
                    }
                }
                else {
                    if (s.charAt(0) == '+') {
                        s = s.substring(1);
                    }
                    if (s.length() > 0 && s.charAt(0) != '-') {
                        s = "-" + s;
                    }
                }
                if (s != null) {
                    Class<?> clazz = this.getValueClass();
                    if (clazz == null) {
                        clazz = stringToValue.getClass();
                    }
                    try {
                        ReflectUtil.checkPackageAccess(clazz);
                        SwingUtilities2.checkAccess(clazz.getModifiers());
                        final Constructor constructor = clazz.getConstructor(String.class);
                        if (constructor != null) {
                            SwingUtilities2.checkAccess(constructor.getModifiers());
                            return constructor.newInstance(s);
                        }
                    }
                    catch (final Throwable t) {}
                }
            }
        }
        return null;
    }
    
    private Object toggleExponentSign(int n, final char c) throws BadLocationException, ParseException {
        this.getFormattedTextField().getText();
        int n2 = 0;
        final int attributeStart = this.getAttributeStart(NumberFormat.Field.EXPONENT_SIGN);
        if (attributeStart >= 0) {
            n2 = 1;
            n = attributeStart;
        }
        String s;
        if (c == this.getPositiveSign()) {
            s = this.getReplaceString(n, n2, null);
        }
        else {
            s = this.getReplaceString(n, n2, new String(new char[] { c }));
        }
        return this.stringToValue(s);
    }
}
