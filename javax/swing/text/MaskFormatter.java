package javax.swing.text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import javax.swing.JFormattedTextField;
import java.text.ParseException;

public class MaskFormatter extends DefaultFormatter
{
    private static final char DIGIT_KEY = '#';
    private static final char LITERAL_KEY = '\'';
    private static final char UPPERCASE_KEY = 'U';
    private static final char LOWERCASE_KEY = 'L';
    private static final char ALPHA_NUMERIC_KEY = 'A';
    private static final char CHARACTER_KEY = '?';
    private static final char ANYTHING_KEY = '*';
    private static final char HEX_KEY = 'H';
    private static final MaskCharacter[] EmptyMaskChars;
    private String mask;
    private transient MaskCharacter[] maskChars;
    private String validCharacters;
    private String invalidCharacters;
    private String placeholderString;
    private char placeholder;
    private boolean containsLiteralChars;
    
    public MaskFormatter() {
        this.setAllowsInvalid(false);
        this.containsLiteralChars = true;
        this.maskChars = MaskFormatter.EmptyMaskChars;
        this.placeholder = ' ';
    }
    
    public MaskFormatter(final String mask) throws ParseException {
        this();
        this.setMask(mask);
    }
    
    public void setMask(final String mask) throws ParseException {
        this.mask = mask;
        this.updateInternalMask();
    }
    
    public String getMask() {
        return this.mask;
    }
    
    public void setValidCharacters(final String validCharacters) {
        this.validCharacters = validCharacters;
    }
    
    public String getValidCharacters() {
        return this.validCharacters;
    }
    
    public void setInvalidCharacters(final String invalidCharacters) {
        this.invalidCharacters = invalidCharacters;
    }
    
    public String getInvalidCharacters() {
        return this.invalidCharacters;
    }
    
    public void setPlaceholder(final String placeholderString) {
        this.placeholderString = placeholderString;
    }
    
    public String getPlaceholder() {
        return this.placeholderString;
    }
    
    public void setPlaceholderCharacter(final char placeholder) {
        this.placeholder = placeholder;
    }
    
    public char getPlaceholderCharacter() {
        return this.placeholder;
    }
    
    public void setValueContainsLiteralCharacters(final boolean containsLiteralChars) {
        this.containsLiteralChars = containsLiteralChars;
    }
    
    public boolean getValueContainsLiteralCharacters() {
        return this.containsLiteralChars;
    }
    
    @Override
    public Object stringToValue(final String s) throws ParseException {
        return this.stringToValue(s, true);
    }
    
    @Override
    public String valueToString(final Object o) throws ParseException {
        final String s = (o == null) ? "" : o.toString();
        final StringBuilder sb = new StringBuilder();
        this.append(sb, s, new int[] { 0 }, this.getPlaceholder(), this.maskChars);
        return sb.toString();
    }
    
    @Override
    public void install(final JFormattedTextField formattedTextField) {
        super.install(formattedTextField);
        if (formattedTextField != null) {
            final Object value = formattedTextField.getValue();
            try {
                this.stringToValue(this.valueToString(value));
            }
            catch (final ParseException ex) {
                this.setEditValid(false);
            }
        }
    }
    
    private Object stringToValue(String stripLiteralChars, final boolean b) throws ParseException {
        final int invalidOffset;
        if ((invalidOffset = this.getInvalidOffset(stripLiteralChars, b)) == -1) {
            if (!this.getValueContainsLiteralCharacters()) {
                stripLiteralChars = this.stripLiteralChars(stripLiteralChars);
            }
            return super.stringToValue(stripLiteralChars);
        }
        throw new ParseException("stringToValue passed invalid value", invalidOffset);
    }
    
    private int getInvalidOffset(final String s, final boolean b) {
        final int length = s.length();
        if (length != this.getMaxLength()) {
            return length;
        }
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if (!this.isValidCharacter(i, char1) && (b || !this.isPlaceholder(i, char1))) {
                return i;
            }
        }
        return -1;
    }
    
    private void append(final StringBuilder sb, final String s, final int[] array, final String s2, final MaskCharacter[] array2) throws ParseException {
        for (int i = 0; i < array2.length; ++i) {
            array2[i].append(sb, s, array, s2);
        }
    }
    
    private void updateInternalMask() throws ParseException {
        final String mask = this.getMask();
        final ArrayList list2;
        final ArrayList list = list2 = new ArrayList();
        if (mask != null) {
            for (int i = 0, length = mask.length(); i < length; ++i) {
                final char char1 = mask.charAt(i);
                switch (char1) {
                    case 35: {
                        list2.add(new DigitMaskCharacter());
                        break;
                    }
                    case 39: {
                        if (++i < length) {
                            list2.add(new LiteralCharacter(mask.charAt(i)));
                            break;
                        }
                        break;
                    }
                    case 85: {
                        list2.add(new UpperCaseCharacter());
                        break;
                    }
                    case 76: {
                        list2.add(new LowerCaseCharacter());
                        break;
                    }
                    case 65: {
                        list2.add(new AlphaNumericCharacter());
                        break;
                    }
                    case 63: {
                        list2.add(new CharCharacter());
                        break;
                    }
                    case 42: {
                        list2.add(new MaskCharacter());
                        break;
                    }
                    case 72: {
                        list2.add(new HexCharacter());
                        break;
                    }
                    default: {
                        list2.add(new LiteralCharacter(char1));
                        break;
                    }
                }
            }
        }
        if (list.size() == 0) {
            this.maskChars = MaskFormatter.EmptyMaskChars;
        }
        else {
            list.toArray(this.maskChars = new MaskCharacter[list.size()]);
        }
    }
    
    private MaskCharacter getMaskCharacter(final int n) {
        if (n >= this.maskChars.length) {
            return null;
        }
        return this.maskChars[n];
    }
    
    private boolean isPlaceholder(final int n, final char c) {
        return this.getPlaceholderCharacter() == c;
    }
    
    private boolean isValidCharacter(final int n, final char c) {
        return this.getMaskCharacter(n).isValidCharacter(c);
    }
    
    private boolean isLiteral(final int n) {
        return this.getMaskCharacter(n).isLiteral();
    }
    
    private int getMaxLength() {
        return this.maskChars.length;
    }
    
    private char getLiteral(final int n) {
        return this.getMaskCharacter(n).getChar('\0');
    }
    
    private char getCharacter(final int n, final char c) {
        return this.getMaskCharacter(n).getChar(c);
    }
    
    private String stripLiteralChars(final String s) {
        StringBuilder sb = null;
        int n = 0;
        for (int i = 0; i < s.length(); ++i) {
            if (this.isLiteral(i)) {
                if (sb == null) {
                    sb = new StringBuilder();
                    if (i > 0) {
                        sb.append(s.substring(0, i));
                    }
                }
                else if (n != i) {
                    sb.append(s.substring(n, i));
                }
                n = i + 1;
            }
        }
        if (sb == null) {
            return s;
        }
        if (n != s.length()) {
            if (sb == null) {
                return s.substring(n);
            }
            sb.append(s.substring(n));
        }
        return sb.toString();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        try {
            this.updateInternalMask();
        }
        catch (final ParseException ex) {}
    }
    
    @Override
    boolean isNavigatable(final int n) {
        return this.getAllowsInvalid() || (n < this.getMaxLength() && !this.isLiteral(n));
    }
    
    @Override
    boolean isValidEdit(final ReplaceHolder replaceHolder) {
        if (!this.getAllowsInvalid()) {
            final String replaceString = this.getReplaceString(replaceHolder.offset, replaceHolder.length, replaceHolder.text);
            try {
                replaceHolder.value = this.stringToValue(replaceString, false);
                return true;
            }
            catch (final ParseException ex) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    boolean canReplace(final ReplaceHolder replaceHolder) {
        if (!this.getAllowsInvalid()) {
            StringBuilder sb = null;
            final String text = replaceHolder.text;
            final int n = (text != null) ? text.length() : 0;
            if (n == 0 && replaceHolder.length == 1 && this.getFormattedTextField().getSelectionStart() != replaceHolder.offset) {
                while (replaceHolder.offset > 0 && this.isLiteral(replaceHolder.offset)) {
                    --replaceHolder.offset;
                }
            }
            int n2 = Math.min(this.getMaxLength() - replaceHolder.offset, Math.max(n, replaceHolder.length));
            int i = 0;
            int n3 = 0;
            while (i < n2) {
                if (n3 < n && this.isValidCharacter(replaceHolder.offset + i, text.charAt(n3))) {
                    final char char1 = text.charAt(n3);
                    if (char1 != this.getCharacter(replaceHolder.offset + i, char1) && sb == null) {
                        sb = new StringBuilder();
                        if (n3 > 0) {
                            sb.append(text.substring(0, n3));
                        }
                    }
                    if (sb != null) {
                        sb.append(this.getCharacter(replaceHolder.offset + i, char1));
                    }
                    ++n3;
                }
                else if (this.isLiteral(replaceHolder.offset + i)) {
                    if (sb != null) {
                        sb.append(this.getLiteral(replaceHolder.offset + i));
                        if (n3 < n) {
                            n2 = Math.min(n2 + 1, this.getMaxLength() - replaceHolder.offset);
                        }
                    }
                    else if (n3 > 0) {
                        sb = new StringBuilder(n2);
                        sb.append(text.substring(0, n3));
                        sb.append(this.getLiteral(replaceHolder.offset + i));
                        if (n3 < n) {
                            n2 = Math.min(n2 + 1, this.getMaxLength() - replaceHolder.offset);
                        }
                        else if (replaceHolder.cursorPosition == -1) {
                            replaceHolder.cursorPosition = replaceHolder.offset + i;
                        }
                    }
                    else {
                        ++replaceHolder.offset;
                        --replaceHolder.length;
                        --i;
                        --n2;
                    }
                }
                else {
                    if (n3 < n) {
                        return false;
                    }
                    if (sb == null) {
                        sb = new StringBuilder();
                        if (text != null) {
                            sb.append(text);
                        }
                    }
                    sb.append(this.getPlaceholderCharacter());
                    if (n > 0 && replaceHolder.cursorPosition == -1) {
                        replaceHolder.cursorPosition = replaceHolder.offset + i;
                    }
                }
                ++i;
            }
            if (sb != null) {
                replaceHolder.text = sb.toString();
            }
            else if (text != null && replaceHolder.offset + n > this.getMaxLength()) {
                replaceHolder.text = text.substring(0, this.getMaxLength() - replaceHolder.offset);
            }
            if (this.getOverwriteMode() && replaceHolder.text != null) {
                replaceHolder.length = replaceHolder.text.length();
            }
        }
        return super.canReplace(replaceHolder);
    }
    
    static {
        EmptyMaskChars = new MaskCharacter[0];
    }
    
    private class MaskCharacter
    {
        public boolean isLiteral() {
            return false;
        }
        
        public boolean isValidCharacter(final char c) {
            if (this.isLiteral()) {
                return this.getChar(c) == c;
            }
            final char char1 = this.getChar(c);
            final String validCharacters = MaskFormatter.this.getValidCharacters();
            if (validCharacters != null && validCharacters.indexOf(char1) == -1) {
                return false;
            }
            final String invalidCharacters = MaskFormatter.this.getInvalidCharacters();
            return invalidCharacters == null || invalidCharacters.indexOf(char1) == -1;
        }
        
        public char getChar(final char c) {
            return c;
        }
        
        public void append(final StringBuilder sb, final String s, final int[] array, final String s2) throws ParseException {
            final boolean b = array[0] < s.length();
            final char c = b ? s.charAt(array[0]) : '\0';
            if (this.isLiteral()) {
                sb.append(this.getChar(c));
                if (MaskFormatter.this.getValueContainsLiteralCharacters()) {
                    if (b && c != this.getChar(c)) {
                        throw new ParseException("Invalid character: " + c, array[0]);
                    }
                    ++array[0];
                }
            }
            else if (array[0] >= s.length()) {
                if (s2 != null && array[0] < s2.length()) {
                    sb.append(s2.charAt(array[0]));
                }
                else {
                    sb.append(MaskFormatter.this.getPlaceholderCharacter());
                }
                ++array[0];
            }
            else {
                if (!this.isValidCharacter(c)) {
                    throw new ParseException("Invalid character: " + c, array[0]);
                }
                sb.append(this.getChar(c));
                ++array[0];
            }
        }
    }
    
    private class LiteralCharacter extends MaskCharacter
    {
        private char fixedChar;
        
        public LiteralCharacter(final char fixedChar) {
            this.fixedChar = fixedChar;
        }
        
        @Override
        public boolean isLiteral() {
            return true;
        }
        
        @Override
        public char getChar(final char c) {
            return this.fixedChar;
        }
    }
    
    private class DigitMaskCharacter extends MaskCharacter
    {
        @Override
        public boolean isValidCharacter(final char c) {
            return Character.isDigit(c) && super.isValidCharacter(c);
        }
    }
    
    private class UpperCaseCharacter extends MaskCharacter
    {
        @Override
        public boolean isValidCharacter(final char c) {
            return Character.isLetter(c) && super.isValidCharacter(c);
        }
        
        @Override
        public char getChar(final char c) {
            return Character.toUpperCase(c);
        }
    }
    
    private class LowerCaseCharacter extends MaskCharacter
    {
        @Override
        public boolean isValidCharacter(final char c) {
            return Character.isLetter(c) && super.isValidCharacter(c);
        }
        
        @Override
        public char getChar(final char c) {
            return Character.toLowerCase(c);
        }
    }
    
    private class AlphaNumericCharacter extends MaskCharacter
    {
        @Override
        public boolean isValidCharacter(final char c) {
            return Character.isLetterOrDigit(c) && super.isValidCharacter(c);
        }
    }
    
    private class CharCharacter extends MaskCharacter
    {
        @Override
        public boolean isValidCharacter(final char c) {
            return Character.isLetter(c) && super.isValidCharacter(c);
        }
    }
    
    private class HexCharacter extends MaskCharacter
    {
        @Override
        public boolean isValidCharacter(final char c) {
            return (c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9' || c == 'a' || c == 'A' || c == 'b' || c == 'B' || c == 'c' || c == 'C' || c == 'd' || c == 'D' || c == 'e' || c == 'E' || c == 'f' || c == 'F') && super.isValidCharacter(c);
        }
        
        @Override
        public char getChar(final char c) {
            if (Character.isDigit(c)) {
                return c;
            }
            return Character.toUpperCase(c);
        }
    }
}
