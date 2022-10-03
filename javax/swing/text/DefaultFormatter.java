package javax.swing.text;

import java.lang.reflect.Constructor;
import java.text.ParseException;
import sun.swing.SwingUtilities2;
import sun.reflect.misc.ReflectUtil;
import java.io.Serializable;
import javax.swing.JFormattedTextField;

public class DefaultFormatter extends JFormattedTextField.AbstractFormatter implements Cloneable, Serializable
{
    private boolean allowsInvalid;
    private boolean overwriteMode;
    private boolean commitOnEdit;
    private Class<?> valueClass;
    private NavigationFilter navigationFilter;
    private DocumentFilter documentFilter;
    transient ReplaceHolder replaceHolder;
    
    public DefaultFormatter() {
        this.overwriteMode = true;
        this.allowsInvalid = true;
    }
    
    @Override
    public void install(final JFormattedTextField formattedTextField) {
        super.install(formattedTextField);
        this.positionCursorAtInitialLocation();
    }
    
    public void setCommitsOnValidEdit(final boolean commitOnEdit) {
        this.commitOnEdit = commitOnEdit;
    }
    
    public boolean getCommitsOnValidEdit() {
        return this.commitOnEdit;
    }
    
    public void setOverwriteMode(final boolean overwriteMode) {
        this.overwriteMode = overwriteMode;
    }
    
    public boolean getOverwriteMode() {
        return this.overwriteMode;
    }
    
    public void setAllowsInvalid(final boolean allowsInvalid) {
        this.allowsInvalid = allowsInvalid;
    }
    
    public boolean getAllowsInvalid() {
        return this.allowsInvalid;
    }
    
    public void setValueClass(final Class<?> valueClass) {
        this.valueClass = valueClass;
    }
    
    public Class<?> getValueClass() {
        return this.valueClass;
    }
    
    @Override
    public Object stringToValue(final String s) throws ParseException {
        Class<?> clazz = this.getValueClass();
        final JFormattedTextField formattedTextField = this.getFormattedTextField();
        if (clazz == null && formattedTextField != null) {
            final Object value = formattedTextField.getValue();
            if (value != null) {
                clazz = value.getClass();
            }
        }
        if (clazz != null) {
            Constructor constructor;
            try {
                ReflectUtil.checkPackageAccess(clazz);
                SwingUtilities2.checkAccess(clazz.getModifiers());
                constructor = clazz.getConstructor(String.class);
            }
            catch (final NoSuchMethodException ex) {
                constructor = null;
            }
            if (constructor != null) {
                try {
                    SwingUtilities2.checkAccess(constructor.getModifiers());
                    return constructor.newInstance(s);
                }
                catch (final Throwable t) {
                    throw new ParseException("Error creating instance", 0);
                }
            }
        }
        return s;
    }
    
    @Override
    public String valueToString(final Object o) throws ParseException {
        if (o == null) {
            return "";
        }
        return o.toString();
    }
    
    @Override
    protected DocumentFilter getDocumentFilter() {
        if (this.documentFilter == null) {
            this.documentFilter = new DefaultDocumentFilter();
        }
        return this.documentFilter;
    }
    
    @Override
    protected NavigationFilter getNavigationFilter() {
        if (this.navigationFilter == null) {
            this.navigationFilter = new DefaultNavigationFilter();
        }
        return this.navigationFilter;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final DefaultFormatter defaultFormatter = (DefaultFormatter)super.clone();
        defaultFormatter.navigationFilter = null;
        defaultFormatter.documentFilter = null;
        defaultFormatter.replaceHolder = null;
        return defaultFormatter;
    }
    
    void positionCursorAtInitialLocation() {
        final JFormattedTextField formattedTextField = this.getFormattedTextField();
        if (formattedTextField != null) {
            formattedTextField.setCaretPosition(this.getInitialVisualPosition());
        }
    }
    
    int getInitialVisualPosition() {
        return this.getNextNavigatableChar(0, 1);
    }
    
    boolean isNavigatable(final int n) {
        return true;
    }
    
    boolean isLegalInsertText(final String s) {
        return true;
    }
    
    private int getNextNavigatableChar(int n, final int n2) {
        for (int length = this.getFormattedTextField().getDocument().getLength(); n >= 0 && n < length; n += n2) {
            if (this.isNavigatable(n)) {
                return n;
            }
        }
        return n;
    }
    
    String getReplaceString(final int n, final int n2, final String s) {
        final String text = this.getFormattedTextField().getText();
        String s2 = text.substring(0, n);
        if (s != null) {
            s2 += s;
        }
        if (n + n2 < text.length()) {
            s2 += text.substring(n + n2);
        }
        return s2;
    }
    
    boolean isValidEdit(final ReplaceHolder replaceHolder) {
        if (!this.getAllowsInvalid()) {
            final String replaceString = this.getReplaceString(replaceHolder.offset, replaceHolder.length, replaceHolder.text);
            try {
                replaceHolder.value = this.stringToValue(replaceString);
                return true;
            }
            catch (final ParseException ex) {
                return false;
            }
        }
        return true;
    }
    
    void commitEdit() throws ParseException {
        final JFormattedTextField formattedTextField = this.getFormattedTextField();
        if (formattedTextField != null) {
            formattedTextField.commitEdit();
        }
    }
    
    void updateValue() {
        this.updateValue(null);
    }
    
    void updateValue(Object stringToValue) {
        try {
            if (stringToValue == null) {
                stringToValue = this.stringToValue(this.getFormattedTextField().getText());
            }
            if (this.getCommitsOnValidEdit()) {
                this.commitEdit();
            }
            this.setEditValid(true);
        }
        catch (final ParseException ex) {
            this.setEditValid(false);
        }
    }
    
    int getNextCursorPosition(final int n, final int n2) {
        int n3 = this.getNextNavigatableChar(n, n2);
        final int length = this.getFormattedTextField().getDocument().getLength();
        if (!this.getAllowsInvalid()) {
            if (n2 == -1 && n == n3) {
                n3 = this.getNextNavigatableChar(n3, 1);
                if (n3 >= length) {
                    n3 = n;
                }
            }
            else if (n2 == 1 && n3 >= length) {
                n3 = this.getNextNavigatableChar(length - 1, -1);
                if (n3 < length) {
                    ++n3;
                }
            }
        }
        return n3;
    }
    
    void repositionCursor(final int n, final int n2) {
        this.getFormattedTextField().getCaret().setDot(this.getNextCursorPosition(n, n2));
    }
    
    int getNextVisualPositionFrom(final JTextComponent textComponent, final int n, final Position.Bias bias, final int n2, final Position.Bias[] array) throws BadLocationException {
        int n3 = textComponent.getUI().getNextVisualPositionFrom(textComponent, n, bias, n2, array);
        if (n3 == -1) {
            return -1;
        }
        if (!this.getAllowsInvalid() && (n2 == 3 || n2 == 7)) {
            int n4;
            for (n4 = -1; !this.isNavigatable(n3) && n3 != n4; n4 = n3, n3 = textComponent.getUI().getNextVisualPositionFrom(textComponent, n3, bias, n2, array)) {}
            final int length = this.getFormattedTextField().getDocument().getLength();
            if (n4 == n3 || n3 == length) {
                if (n3 == 0) {
                    array[0] = Position.Bias.Forward;
                    n3 = this.getInitialVisualPosition();
                }
                if (n3 >= length && length > 0) {
                    array[0] = Position.Bias.Forward;
                    n3 = this.getNextNavigatableChar(length - 1, -1) + 1;
                }
            }
        }
        return n3;
    }
    
    boolean canReplace(final ReplaceHolder replaceHolder) {
        return this.isValidEdit(replaceHolder);
    }
    
    void replace(final DocumentFilter.FilterBypass filterBypass, final int n, final int n2, final String s, final AttributeSet set) throws BadLocationException {
        this.replace(this.getReplaceHolder(filterBypass, n, n2, s, set));
    }
    
    boolean replace(final ReplaceHolder replaceHolder) throws BadLocationException {
        boolean b = true;
        int n = 1;
        if (replaceHolder.length > 0 && (replaceHolder.text == null || replaceHolder.text.length() == 0) && (this.getFormattedTextField().getSelectionStart() != replaceHolder.offset || replaceHolder.length > 1)) {
            n = -1;
        }
        if (this.getOverwriteMode() && replaceHolder.text != null && this.getFormattedTextField().getSelectedText() == null) {
            replaceHolder.length = Math.min(Math.max(replaceHolder.length, replaceHolder.text.length()), replaceHolder.fb.getDocument().getLength() - replaceHolder.offset);
        }
        if ((replaceHolder.text != null && !this.isLegalInsertText(replaceHolder.text)) || !this.canReplace(replaceHolder) || (replaceHolder.length == 0 && (replaceHolder.text == null || replaceHolder.text.length() == 0))) {
            b = false;
        }
        if (b) {
            int n2 = replaceHolder.cursorPosition;
            replaceHolder.fb.replace(replaceHolder.offset, replaceHolder.length, replaceHolder.text, replaceHolder.attrs);
            if (n2 == -1) {
                n2 = replaceHolder.offset;
                if (n == 1 && replaceHolder.text != null) {
                    n2 = replaceHolder.offset + replaceHolder.text.length();
                }
            }
            this.updateValue(replaceHolder.value);
            this.repositionCursor(n2, n);
            return true;
        }
        this.invalidEdit();
        return false;
    }
    
    void setDot(final NavigationFilter.FilterBypass filterBypass, final int n, final Position.Bias bias) {
        filterBypass.setDot(n, bias);
    }
    
    void moveDot(final NavigationFilter.FilterBypass filterBypass, final int n, final Position.Bias bias) {
        filterBypass.moveDot(n, bias);
    }
    
    ReplaceHolder getReplaceHolder(final DocumentFilter.FilterBypass filterBypass, final int n, final int n2, final String s, final AttributeSet set) {
        if (this.replaceHolder == null) {
            this.replaceHolder = new ReplaceHolder();
        }
        this.replaceHolder.reset(filterBypass, n, n2, s, set);
        return this.replaceHolder;
    }
    
    static class ReplaceHolder
    {
        DocumentFilter.FilterBypass fb;
        int offset;
        int length;
        String text;
        AttributeSet attrs;
        Object value;
        int cursorPosition;
        
        void reset(final DocumentFilter.FilterBypass fb, final int offset, final int length, final String text, final AttributeSet attrs) {
            this.fb = fb;
            this.offset = offset;
            this.length = length;
            this.text = text;
            this.attrs = attrs;
            this.value = null;
            this.cursorPosition = -1;
        }
    }
    
    private class DefaultNavigationFilter extends NavigationFilter implements Serializable
    {
        @Override
        public void setDot(final FilterBypass filterBypass, final int n, final Position.Bias bias) {
            if (AbstractFormatter.this.getFormattedTextField().composedTextExists()) {
                filterBypass.setDot(n, bias);
            }
            else {
                DefaultFormatter.this.setDot(filterBypass, n, bias);
            }
        }
        
        @Override
        public void moveDot(final FilterBypass filterBypass, final int n, final Position.Bias bias) {
            if (AbstractFormatter.this.getFormattedTextField().composedTextExists()) {
                filterBypass.moveDot(n, bias);
            }
            else {
                DefaultFormatter.this.moveDot(filterBypass, n, bias);
            }
        }
        
        @Override
        public int getNextVisualPositionFrom(final JTextComponent textComponent, final int n, final Position.Bias bias, final int n2, final Position.Bias[] array) throws BadLocationException {
            if (textComponent.composedTextExists()) {
                return textComponent.getUI().getNextVisualPositionFrom(textComponent, n, bias, n2, array);
            }
            return DefaultFormatter.this.getNextVisualPositionFrom(textComponent, n, bias, n2, array);
        }
    }
    
    private class DefaultDocumentFilter extends DocumentFilter implements Serializable
    {
        @Override
        public void remove(final FilterBypass filterBypass, final int n, final int n2) throws BadLocationException {
            if (AbstractFormatter.this.getFormattedTextField().composedTextExists()) {
                filterBypass.remove(n, n2);
            }
            else {
                DefaultFormatter.this.replace(filterBypass, n, n2, null, null);
            }
        }
        
        @Override
        public void insertString(final FilterBypass filterBypass, final int n, final String s, final AttributeSet set) throws BadLocationException {
            if (AbstractFormatter.this.getFormattedTextField().composedTextExists() || Utilities.isComposedTextAttributeDefined(set)) {
                filterBypass.insertString(n, s, set);
            }
            else {
                DefaultFormatter.this.replace(filterBypass, n, 0, s, set);
            }
        }
        
        @Override
        public void replace(final FilterBypass filterBypass, final int n, final int n2, final String s, final AttributeSet set) throws BadLocationException {
            if (AbstractFormatter.this.getFormattedTextField().composedTextExists() || Utilities.isComposedTextAttributeDefined(set)) {
                filterBypass.replace(n, n2, s, set);
            }
            else {
                DefaultFormatter.this.replace(filterBypass, n, n2, s, set);
            }
        }
    }
}
