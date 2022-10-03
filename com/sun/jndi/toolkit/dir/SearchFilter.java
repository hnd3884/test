package com.sun.jndi.toolkit.dir;

import java.util.Locale;
import java.util.StringTokenizer;
import javax.naming.OperationNotSupportedException;
import java.util.Vector;
import javax.naming.directory.BasicAttributes;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InvalidSearchFilterException;

public class SearchFilter implements AttrFilter
{
    String filter;
    int pos;
    private StringFilter rootFilter;
    protected static final boolean debug = false;
    protected static final char BEGIN_FILTER_TOKEN = '(';
    protected static final char END_FILTER_TOKEN = ')';
    protected static final char AND_TOKEN = '&';
    protected static final char OR_TOKEN = '|';
    protected static final char NOT_TOKEN = '!';
    protected static final char EQUAL_TOKEN = '=';
    protected static final char APPROX_TOKEN = '~';
    protected static final char LESS_TOKEN = '<';
    protected static final char GREATER_TOKEN = '>';
    protected static final char EXTEND_TOKEN = ':';
    protected static final char WILDCARD_TOKEN = '*';
    static final int EQUAL_MATCH = 1;
    static final int APPROX_MATCH = 2;
    static final int GREATER_MATCH = 3;
    static final int LESS_MATCH = 4;
    
    public SearchFilter(final String filter) throws InvalidSearchFilterException {
        this.filter = filter;
        this.pos = 0;
        this.normalizeFilter();
        this.rootFilter = this.createNextFilter();
    }
    
    @Override
    public boolean check(final Attributes attributes) throws NamingException {
        return attributes != null && this.rootFilter.check(attributes);
    }
    
    protected void normalizeFilter() {
        this.skipWhiteSpace();
        if (this.getCurrentChar() != '(') {
            this.filter = '(' + this.filter + ')';
        }
    }
    
    private void skipWhiteSpace() {
        while (Character.isWhitespace(this.getCurrentChar())) {
            this.consumeChar();
        }
    }
    
    protected StringFilter createNextFilter() throws InvalidSearchFilterException {
        this.skipWhiteSpace();
        StringFilter stringFilter = null;
        try {
            if (this.getCurrentChar() != '(') {
                throw new InvalidSearchFilterException("expected \"(\" at position " + this.pos);
            }
            this.consumeChar();
            this.skipWhiteSpace();
            switch (this.getCurrentChar()) {
                case '&': {
                    stringFilter = new CompoundFilter(true);
                    stringFilter.parse();
                    break;
                }
                case '|': {
                    stringFilter = new CompoundFilter(false);
                    stringFilter.parse();
                    break;
                }
                case '!': {
                    stringFilter = new NotFilter();
                    stringFilter.parse();
                    break;
                }
                default: {
                    stringFilter = new AtomicFilter();
                    stringFilter.parse();
                    break;
                }
            }
            this.skipWhiteSpace();
            if (this.getCurrentChar() != ')') {
                throw new InvalidSearchFilterException("expected \")\" at position " + this.pos);
            }
            this.consumeChar();
        }
        catch (final InvalidSearchFilterException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new InvalidSearchFilterException("Unable to parse character " + this.pos + " in \"" + this.filter + "\"");
        }
        return stringFilter;
    }
    
    protected char getCurrentChar() {
        return this.filter.charAt(this.pos);
    }
    
    protected char relCharAt(final int n) {
        return this.filter.charAt(this.pos + n);
    }
    
    protected void consumeChar() {
        ++this.pos;
    }
    
    protected void consumeChars(final int n) {
        this.pos += n;
    }
    
    protected int relIndexOf(final int n) {
        return this.filter.indexOf(n, this.pos) - this.pos;
    }
    
    protected String relSubstring(final int n, final int n2) {
        return this.filter.substring(n + this.pos, n2 + this.pos);
    }
    
    public static String format(final Attributes attributes) throws NamingException {
        if (attributes == null || attributes.size() == 0) {
            return "objectClass=*";
        }
        String s = "(& ";
        final NamingEnumeration<? extends Attribute> all = attributes.getAll();
        while (all.hasMore()) {
            final Attribute attribute = (Attribute)all.next();
            if (attribute.size() == 0 || (attribute.size() == 1 && attribute.get() == null)) {
                s = s + "(" + attribute.getID() + "=*)";
            }
            else {
                final NamingEnumeration<?> all2 = attribute.getAll();
                while (all2.hasMore()) {
                    final String encodedStringRep = getEncodedStringRep(all2.next());
                    if (encodedStringRep != null) {
                        s = s + "(" + attribute.getID() + "=" + encodedStringRep + ")";
                    }
                }
            }
        }
        return s + ")";
    }
    
    private static void hexDigit(final StringBuffer sb, final byte b) {
        final char c = (char)(b >> 4 & 0xF);
        char c2;
        if (c > '\t') {
            c2 = (char)(c - '\n' + 65);
        }
        else {
            c2 = (char)(c + '0');
        }
        sb.append(c2);
        final char c3 = (char)(b & 0xF);
        char c4;
        if (c3 > '\t') {
            c4 = (char)(c3 - '\n' + 65);
        }
        else {
            c4 = (char)(c3 + '0');
        }
        sb.append(c4);
    }
    
    private static String getEncodedStringRep(final Object o) throws NamingException {
        if (o == null) {
            return null;
        }
        if (o instanceof byte[]) {
            final byte[] array = (byte[])o;
            final StringBuffer sb = new StringBuffer(array.length * 3);
            for (int i = 0; i < array.length; ++i) {
                sb.append('\\');
                hexDigit(sb, array[i]);
            }
            return sb.toString();
        }
        String string;
        if (!(o instanceof String)) {
            string = o.toString();
        }
        else {
            string = (String)o;
        }
        final int length = string.length();
        final StringBuffer sb2 = new StringBuffer(length);
        for (int j = 0; j < length; ++j) {
            final char char1;
            switch (char1 = string.charAt(j)) {
                case '*': {
                    sb2.append("\\2a");
                    break;
                }
                case '(': {
                    sb2.append("\\28");
                    break;
                }
                case ')': {
                    sb2.append("\\29");
                    break;
                }
                case '\\': {
                    sb2.append("\\5c");
                    break;
                }
                case '\0': {
                    sb2.append("\\00");
                    break;
                }
                default: {
                    sb2.append(char1);
                    break;
                }
            }
        }
        return sb2.toString();
    }
    
    public static int findUnescaped(final char c, final String s, int i) {
        while (i < s.length()) {
            final int index = s.indexOf(c, i);
            if (index == i || index == -1 || s.charAt(index - 1) != '\\') {
                return index;
            }
            i = index + 1;
        }
        return -1;
    }
    
    public static String format(final String s, final Object[] array) throws NamingException {
        int n = 0;
        final StringBuffer sb = new StringBuffer(s.length());
        int unescaped;
        while ((unescaped = findUnescaped('{', s, n)) >= 0) {
            final int n2 = unescaped + 1;
            final int index = s.indexOf(125, n2);
            if (index < 0) {
                throw new InvalidSearchFilterException("unbalanced {: " + s);
            }
            int int1;
            try {
                int1 = Integer.parseInt(s.substring(n2, index));
            }
            catch (final NumberFormatException ex) {
                throw new InvalidSearchFilterException("integer expected inside {}: " + s);
            }
            if (int1 >= array.length) {
                throw new InvalidSearchFilterException("number exceeds argument list: " + int1);
            }
            sb.append(s.substring(n, unescaped)).append(getEncodedStringRep(array[int1]));
            n = index + 1;
        }
        if (n < s.length()) {
            sb.append(s.substring(n));
        }
        return sb.toString();
    }
    
    public static Attributes selectAttributes(final Attributes attributes, final String[] array) throws NamingException {
        if (array == null) {
            return attributes;
        }
        final BasicAttributes basicAttributes = new BasicAttributes();
        for (int i = 0; i < array.length; ++i) {
            final Attribute value = attributes.get(array[i]);
            if (value != null) {
                basicAttributes.put(value);
            }
        }
        return basicAttributes;
    }
    
    final class CompoundFilter implements StringFilter
    {
        private Vector<StringFilter> subFilters;
        private boolean polarity;
        
        CompoundFilter(final boolean polarity) {
            this.subFilters = new Vector<StringFilter>();
            this.polarity = polarity;
        }
        
        @Override
        public void parse() throws InvalidSearchFilterException {
            SearchFilter.this.consumeChar();
            while (SearchFilter.this.getCurrentChar() != ')') {
                this.subFilters.addElement(SearchFilter.this.createNextFilter());
                SearchFilter.this.skipWhiteSpace();
            }
        }
        
        @Override
        public boolean check(final Attributes attributes) throws NamingException {
            for (int i = 0; i < this.subFilters.size(); ++i) {
                if (this.subFilters.elementAt(i).check(attributes) != this.polarity) {
                    return !this.polarity;
                }
            }
            return this.polarity;
        }
    }
    
    final class NotFilter implements StringFilter
    {
        private StringFilter filter;
        
        @Override
        public void parse() throws InvalidSearchFilterException {
            SearchFilter.this.consumeChar();
            this.filter = SearchFilter.this.createNextFilter();
        }
        
        @Override
        public boolean check(final Attributes attributes) throws NamingException {
            return !this.filter.check(attributes);
        }
    }
    
    final class AtomicFilter implements StringFilter
    {
        private String attrID;
        private String value;
        private int matchType;
        
        @Override
        public void parse() throws InvalidSearchFilterException {
            SearchFilter.this.skipWhiteSpace();
            try {
                final int relIndex = SearchFilter.this.relIndexOf(41);
                final int relIndex2 = SearchFilter.this.relIndexOf(61);
                switch (SearchFilter.this.relCharAt(relIndex2 - 1)) {
                    case '~': {
                        this.matchType = 2;
                        this.attrID = SearchFilter.this.relSubstring(0, relIndex2 - 1);
                        this.value = SearchFilter.this.relSubstring(relIndex2 + 1, relIndex);
                        break;
                    }
                    case '>': {
                        this.matchType = 3;
                        this.attrID = SearchFilter.this.relSubstring(0, relIndex2 - 1);
                        this.value = SearchFilter.this.relSubstring(relIndex2 + 1, relIndex);
                        break;
                    }
                    case '<': {
                        this.matchType = 4;
                        this.attrID = SearchFilter.this.relSubstring(0, relIndex2 - 1);
                        this.value = SearchFilter.this.relSubstring(relIndex2 + 1, relIndex);
                        break;
                    }
                    case ':': {
                        throw new OperationNotSupportedException("Extensible match not supported");
                    }
                    default: {
                        this.matchType = 1;
                        this.attrID = SearchFilter.this.relSubstring(0, relIndex2);
                        this.value = SearchFilter.this.relSubstring(relIndex2 + 1, relIndex);
                        break;
                    }
                }
                this.attrID = this.attrID.trim();
                this.value = this.value.trim();
                SearchFilter.this.consumeChars(relIndex);
            }
            catch (final Exception rootCause) {
                final InvalidSearchFilterException ex = new InvalidSearchFilterException("Unable to parse character " + SearchFilter.this.pos + " in \"" + SearchFilter.this.filter + "\"");
                ex.setRootCause(rootCause);
                throw ex;
            }
        }
        
        @Override
        public boolean check(final Attributes attributes) {
            NamingEnumeration<?> all;
            try {
                final Attribute value = attributes.get(this.attrID);
                if (value == null) {
                    return false;
                }
                all = value.getAll();
            }
            catch (final NamingException ex) {
                return false;
            }
            while (all.hasMoreElements()) {
                final String string = all.nextElement().toString();
                switch (this.matchType) {
                    case 1:
                    case 2: {
                        if (this.substringMatch(this.value, string)) {
                            return true;
                        }
                        continue;
                    }
                    case 3: {
                        if (string.compareTo(this.value) >= 0) {
                            return true;
                        }
                        continue;
                    }
                    case 4: {
                        if (string.compareTo(this.value) <= 0) {
                            return true;
                        }
                        continue;
                    }
                }
            }
            return false;
        }
        
        private boolean substringMatch(final String s, final String s2) {
            if (s.equals(new Character('*').toString())) {
                return true;
            }
            if (s.indexOf(42) == -1) {
                return s.equalsIgnoreCase(s2);
            }
            int n = 0;
            final StringTokenizer stringTokenizer = new StringTokenizer(s, "*", false);
            if (s.charAt(0) != '*' && !s2.toLowerCase(Locale.ENGLISH).startsWith(stringTokenizer.nextToken().toLowerCase(Locale.ENGLISH))) {
                return false;
            }
            while (stringTokenizer.hasMoreTokens()) {
                final String nextToken = stringTokenizer.nextToken();
                final int index = s2.toLowerCase(Locale.ENGLISH).indexOf(nextToken.toLowerCase(Locale.ENGLISH), n);
                if (index == -1) {
                    return false;
                }
                n = index + nextToken.length();
            }
            return s.charAt(s.length() - 1) == '*' || n == s2.length();
        }
    }
    
    interface StringFilter extends AttrFilter
    {
        void parse() throws InvalidSearchFilterException;
    }
}
