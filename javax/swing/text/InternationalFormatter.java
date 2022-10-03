package javax.swing.text;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.swing.Action;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import java.text.ParseException;
import javax.swing.JFormattedTextField;
import java.text.AttributedCharacterIterator;
import java.util.BitSet;
import java.text.Format;

public class InternationalFormatter extends DefaultFormatter
{
    private static final Format.Field[] EMPTY_FIELD_ARRAY;
    private Format format;
    private Comparable max;
    private Comparable min;
    private transient BitSet literalMask;
    private transient AttributedCharacterIterator iterator;
    private transient boolean validMask;
    private transient String string;
    private transient boolean ignoreDocumentMutate;
    
    public InternationalFormatter() {
        this.setOverwriteMode(false);
    }
    
    public InternationalFormatter(final Format format) {
        this();
        this.setFormat(format);
    }
    
    public void setFormat(final Format format) {
        this.format = format;
    }
    
    public Format getFormat() {
        return this.format;
    }
    
    public void setMinimum(final Comparable min) {
        if (this.getValueClass() == null && min != null) {
            this.setValueClass(min.getClass());
        }
        this.min = min;
    }
    
    public Comparable getMinimum() {
        return this.min;
    }
    
    public void setMaximum(final Comparable max) {
        if (this.getValueClass() == null && max != null) {
            this.setValueClass(max.getClass());
        }
        this.max = max;
    }
    
    public Comparable getMaximum() {
        return this.max;
    }
    
    @Override
    public void install(final JFormattedTextField formattedTextField) {
        super.install(formattedTextField);
        this.updateMaskIfNecessary();
        this.positionCursorAtInitialLocation();
    }
    
    @Override
    public String valueToString(final Object o) throws ParseException {
        if (o == null) {
            return "";
        }
        final Format format = this.getFormat();
        if (format == null) {
            return o.toString();
        }
        return format.format(o);
    }
    
    @Override
    public Object stringToValue(final String s) throws ParseException {
        Object o = this.stringToValue(s, this.getFormat());
        if (o != null && this.getValueClass() != null && !this.getValueClass().isInstance(o)) {
            o = super.stringToValue(o.toString());
        }
        try {
            if (!this.isValidValue(o, true)) {
                throw new ParseException("Value not within min/max range", 0);
            }
        }
        catch (final ClassCastException ex) {
            throw new ParseException("Class cast exception comparing values: " + ex, 0);
        }
        return o;
    }
    
    public Format.Field[] getFields(final int n) {
        if (this.getAllowsInvalid()) {
            this.updateMask();
        }
        final Map<AttributedCharacterIterator.Attribute, Object> attributes = this.getAttributes(n);
        if (attributes != null && attributes.size() > 0) {
            final ArrayList list = new ArrayList();
            list.addAll(attributes.keySet());
            return list.toArray(InternationalFormatter.EMPTY_FIELD_ARRAY);
        }
        return InternationalFormatter.EMPTY_FIELD_ARRAY;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        final InternationalFormatter internationalFormatter = (InternationalFormatter)super.clone();
        internationalFormatter.literalMask = null;
        internationalFormatter.iterator = null;
        internationalFormatter.validMask = false;
        internationalFormatter.string = null;
        return internationalFormatter;
    }
    
    @Override
    protected Action[] getActions() {
        if (this.getSupportsIncrement()) {
            return new Action[] { new IncrementAction("increment", 1), new IncrementAction("decrement", -1) };
        }
        return null;
    }
    
    Object stringToValue(final String s, final Format format) throws ParseException {
        if (format == null) {
            return s;
        }
        return format.parseObject(s);
    }
    
    boolean isValidValue(final Object o, final boolean b) {
        final Comparable minimum = this.getMinimum();
        try {
            if (minimum != null && minimum.compareTo(o) > 0) {
                return false;
            }
        }
        catch (final ClassCastException ex) {
            if (b) {
                throw ex;
            }
            return false;
        }
        final Comparable maximum = this.getMaximum();
        try {
            if (maximum != null && maximum.compareTo(o) < 0) {
                return false;
            }
        }
        catch (final ClassCastException ex2) {
            if (b) {
                throw ex2;
            }
            return false;
        }
        return true;
    }
    
    Map<AttributedCharacterIterator.Attribute, Object> getAttributes(final int index) {
        if (this.isValidMask()) {
            final AttributedCharacterIterator iterator = this.getIterator();
            if (index >= 0 && index <= iterator.getEndIndex()) {
                iterator.setIndex(index);
                return iterator.getAttributes();
            }
        }
        return null;
    }
    
    int getAttributeStart(final AttributedCharacterIterator.Attribute attribute) {
        if (this.isValidMask()) {
            final AttributedCharacterIterator iterator = this.getIterator();
            iterator.first();
            while (iterator.current() != '\uffff') {
                if (iterator.getAttribute(attribute) != null) {
                    return iterator.getIndex();
                }
                iterator.next();
            }
        }
        return -1;
    }
    
    AttributedCharacterIterator getIterator() {
        return this.iterator;
    }
    
    void updateMaskIfNecessary() {
        if (!this.getAllowsInvalid() && this.getFormat() != null) {
            if (!this.isValidMask()) {
                this.updateMask();
            }
            else if (!this.getFormattedTextField().getText().equals(this.string)) {
                this.updateMask();
            }
        }
    }
    
    void updateMask() {
        if (this.getFormat() != null) {
            final Document document = this.getFormattedTextField().getDocument();
            this.validMask = false;
            if (document != null) {
                try {
                    this.string = document.getText(0, document.getLength());
                }
                catch (final BadLocationException ex) {
                    this.string = null;
                }
                if (this.string != null) {
                    try {
                        this.updateMask(this.getFormat().formatToCharacterIterator(this.stringToValue(this.string)));
                    }
                    catch (final ParseException ex2) {}
                    catch (final IllegalArgumentException ex3) {}
                    catch (final NullPointerException ex4) {}
                }
            }
        }
    }
    
    int getLiteralCountTo(final int n) {
        int n2 = 0;
        for (int i = 0; i < n; ++i) {
            if (this.isLiteral(i)) {
                ++n2;
            }
        }
        return n2;
    }
    
    boolean isLiteral(final int n) {
        return this.isValidMask() && n < this.string.length() && this.literalMask.get(n);
    }
    
    char getLiteral(final int n) {
        if (this.isValidMask() && this.string != null && n < this.string.length()) {
            return this.string.charAt(n);
        }
        return '\0';
    }
    
    @Override
    boolean isNavigatable(final int n) {
        return !this.isLiteral(n);
    }
    
    @Override
    void updateValue(final Object o) {
        super.updateValue(o);
        this.updateMaskIfNecessary();
    }
    
    @Override
    void replace(final DocumentFilter.FilterBypass filterBypass, final int n, final int n2, final String s, final AttributeSet set) throws BadLocationException {
        if (this.ignoreDocumentMutate) {
            filterBypass.replace(n, n2, s, set);
            return;
        }
        super.replace(filterBypass, n, n2, s, set);
    }
    
    private int getNextNonliteralIndex(int n, final int n2) {
        for (int length = this.getFormattedTextField().getDocument().getLength(); n >= 0 && n < length; n += n2) {
            if (!this.isLiteral(n)) {
                return n;
            }
        }
        final int length;
        return (n2 == -1) ? 0 : length;
    }
    
    @Override
    boolean canReplace(final ReplaceHolder replaceHolder) {
        if (!this.getAllowsInvalid()) {
            final String text = replaceHolder.text;
            final int n = (text != null) ? text.length() : 0;
            final JFormattedTextField formattedTextField = this.getFormattedTextField();
            if (n == 0 && replaceHolder.length == 1 && formattedTextField.getSelectionStart() != replaceHolder.offset) {
                replaceHolder.offset = this.getNextNonliteralIndex(replaceHolder.offset, -1);
            }
            else if (this.getOverwriteMode()) {
                int offset;
                int n2 = offset = replaceHolder.offset;
                boolean b = false;
                for (int i = 0; i < replaceHolder.length; ++i) {
                    while (this.isLiteral(n2)) {
                        ++n2;
                    }
                    if (n2 >= this.string.length()) {
                        n2 = offset;
                        b = true;
                        break;
                    }
                    offset = ++n2;
                }
                if (b || formattedTextField.getSelectedText() == null) {
                    replaceHolder.length = n2 - replaceHolder.offset;
                }
            }
            else if (n > 0) {
                replaceHolder.offset = this.getNextNonliteralIndex(replaceHolder.offset, 1);
            }
            else {
                replaceHolder.offset = this.getNextNonliteralIndex(replaceHolder.offset, -1);
            }
            ((ExtendedReplaceHolder)replaceHolder).endOffset = replaceHolder.offset;
            ((ExtendedReplaceHolder)replaceHolder).endTextLength = ((replaceHolder.text != null) ? replaceHolder.text.length() : 0);
        }
        else {
            ((ExtendedReplaceHolder)replaceHolder).endOffset = replaceHolder.offset;
            ((ExtendedReplaceHolder)replaceHolder).endTextLength = ((replaceHolder.text != null) ? replaceHolder.text.length() : 0);
        }
        final boolean canReplace = super.canReplace(replaceHolder);
        if (canReplace && !this.getAllowsInvalid()) {
            ((ExtendedReplaceHolder)replaceHolder).resetFromValue(this);
        }
        return canReplace;
    }
    
    @Override
    boolean replace(final ReplaceHolder replaceHolder) throws BadLocationException {
        int n = -1;
        int n2 = 1;
        int literalCountTo = -1;
        if (replaceHolder.length > 0 && (replaceHolder.text == null || replaceHolder.text.length() == 0) && (this.getFormattedTextField().getSelectionStart() != replaceHolder.offset || replaceHolder.length > 1)) {
            n2 = -1;
        }
        if (!this.getAllowsInvalid()) {
            if ((replaceHolder.text == null || replaceHolder.text.length() == 0) && replaceHolder.length > 0) {
                n = this.getFormattedTextField().getSelectionStart();
            }
            else {
                n = replaceHolder.offset;
            }
            literalCountTo = this.getLiteralCountTo(n);
        }
        if (super.replace(replaceHolder)) {
            if (n != -1) {
                this.repositionCursor(literalCountTo, ((ExtendedReplaceHolder)replaceHolder).endOffset + ((ExtendedReplaceHolder)replaceHolder).endTextLength, n2);
            }
            else {
                int endOffset = ((ExtendedReplaceHolder)replaceHolder).endOffset;
                if (n2 == 1) {
                    endOffset += ((ExtendedReplaceHolder)replaceHolder).endTextLength;
                }
                this.repositionCursor(endOffset, n2);
            }
            return true;
        }
        return false;
    }
    
    private void repositionCursor(final int n, int n2, final int n3) {
        if (this.getLiteralCountTo(n2) != n2) {
            n2 -= n;
            for (int i = 0; i < n2; ++i) {
                if (this.isLiteral(i)) {
                    ++n2;
                }
            }
        }
        this.repositionCursor(n2, 1);
    }
    
    char getBufferedChar(final int n) {
        if (this.isValidMask() && this.string != null && n < this.string.length()) {
            return this.string.charAt(n);
        }
        return '\0';
    }
    
    boolean isValidMask() {
        return this.validMask;
    }
    
    boolean isLiteral(final Map map) {
        return map == null || map.size() == 0;
    }
    
    private void updateMask(final AttributedCharacterIterator iterator) {
        if (iterator != null) {
            this.validMask = true;
            this.iterator = iterator;
            if (this.literalMask == null) {
                this.literalMask = new BitSet();
            }
            else {
                for (int i = this.literalMask.length() - 1; i >= 0; --i) {
                    this.literalMask.clear(i);
                }
            }
            iterator.first();
            while (iterator.current() != '\uffff') {
                final boolean literal = this.isLiteral(iterator.getAttributes());
                int j;
                for (j = iterator.getIndex(); j < iterator.getRunLimit(); ++j) {
                    if (literal) {
                        this.literalMask.set(j);
                    }
                    else {
                        this.literalMask.clear(j);
                    }
                }
                iterator.setIndex(j);
            }
        }
    }
    
    boolean canIncrement(final Object o, final int n) {
        return o != null;
    }
    
    void selectField(final Object o, int n) {
        final AttributedCharacterIterator iterator = this.getIterator();
        if (iterator != null && o instanceof AttributedCharacterIterator.Attribute) {
            final AttributedCharacterIterator.Attribute attribute = (AttributedCharacterIterator.Attribute)o;
            iterator.first();
            while (iterator.current() != '\uffff') {
                while (iterator.getAttribute(attribute) == null && iterator.next() != '\uffff') {}
                if (iterator.current() != '\uffff') {
                    final int runLimit = iterator.getRunLimit(attribute);
                    if (--n <= 0) {
                        this.getFormattedTextField().select(iterator.getIndex(), runLimit);
                        break;
                    }
                    iterator.setIndex(runLimit);
                    iterator.next();
                }
            }
        }
    }
    
    Object getAdjustField(final int n, final Map map) {
        return null;
    }
    
    private int getFieldTypeCountTo(final Object o, final int n) {
        final AttributedCharacterIterator iterator = this.getIterator();
        int n2 = 0;
        if (iterator != null && o instanceof AttributedCharacterIterator.Attribute) {
            final AttributedCharacterIterator.Attribute attribute = (AttributedCharacterIterator.Attribute)o;
            iterator.first();
            while (iterator.getIndex() < n) {
                while (iterator.getAttribute(attribute) == null && iterator.next() != '\uffff') {}
                if (iterator.current() == '\uffff') {
                    break;
                }
                iterator.setIndex(iterator.getRunLimit(attribute));
                iterator.next();
                ++n2;
            }
        }
        return n2;
    }
    
    Object adjustValue(final Object o, final Map map, final Object o2, final int n) throws BadLocationException, ParseException {
        return null;
    }
    
    boolean getSupportsIncrement() {
        return false;
    }
    
    void resetValue(final Object o) throws BadLocationException, ParseException {
        final Document document = this.getFormattedTextField().getDocument();
        final String valueToString = this.valueToString(o);
        try {
            this.ignoreDocumentMutate = true;
            document.remove(0, document.getLength());
            document.insertString(0, valueToString, null);
        }
        finally {
            this.ignoreDocumentMutate = false;
        }
        this.updateValue(o);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.updateMaskIfNecessary();
    }
    
    @Override
    ReplaceHolder getReplaceHolder(final DocumentFilter.FilterBypass filterBypass, final int n, final int n2, final String s, final AttributeSet set) {
        if (this.replaceHolder == null) {
            this.replaceHolder = new ExtendedReplaceHolder();
        }
        return super.getReplaceHolder(filterBypass, n, n2, s, set);
    }
    
    static {
        EMPTY_FIELD_ARRAY = new Format.Field[0];
    }
    
    static class ExtendedReplaceHolder extends ReplaceHolder
    {
        int endOffset;
        int endTextLength;
        
        void resetFromValue(final InternationalFormatter internationalFormatter) {
            this.offset = 0;
            try {
                this.text = internationalFormatter.valueToString(this.value);
            }
            catch (final ParseException ex) {
                this.text = "";
            }
            this.length = this.fb.getDocument().getLength();
        }
    }
    
    private class IncrementAction extends AbstractAction
    {
        private int direction;
        
        IncrementAction(final String s, final int direction) {
            super(s);
            this.direction = direction;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (AbstractFormatter.this.getFormattedTextField().isEditable()) {
                if (InternationalFormatter.this.getAllowsInvalid()) {
                    InternationalFormatter.this.updateMask();
                }
                boolean b = false;
                if (InternationalFormatter.this.isValidMask()) {
                    final int selectionStart = AbstractFormatter.this.getFormattedTextField().getSelectionStart();
                    if (selectionStart != -1) {
                        final AttributedCharacterIterator iterator = InternationalFormatter.this.getIterator();
                        iterator.setIndex(selectionStart);
                        final Map<AttributedCharacterIterator.Attribute, Object> attributes = iterator.getAttributes();
                        final Object adjustField = InternationalFormatter.this.getAdjustField(selectionStart, attributes);
                        if (InternationalFormatter.this.canIncrement(adjustField, selectionStart)) {
                            try {
                                final Object stringToValue = InternationalFormatter.this.stringToValue(AbstractFormatter.this.getFormattedTextField().getText());
                                final int access$300 = InternationalFormatter.this.getFieldTypeCountTo(adjustField, selectionStart);
                                final Object adjustValue = InternationalFormatter.this.adjustValue(stringToValue, attributes, adjustField, this.direction);
                                if (adjustValue != null && InternationalFormatter.this.isValidValue(adjustValue, false)) {
                                    InternationalFormatter.this.resetValue(adjustValue);
                                    InternationalFormatter.this.updateMask();
                                    if (InternationalFormatter.this.isValidMask()) {
                                        InternationalFormatter.this.selectField(adjustField, access$300);
                                    }
                                    b = true;
                                }
                            }
                            catch (final ParseException ex) {}
                            catch (final BadLocationException ex2) {}
                        }
                    }
                }
                if (!b) {
                    AbstractFormatter.this.invalidEdit();
                }
            }
        }
    }
}
