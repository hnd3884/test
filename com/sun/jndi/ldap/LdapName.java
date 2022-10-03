package com.sun.jndi.ldap;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.Attributes;
import java.io.StreamCorruptedException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import javax.naming.InvalidNameException;
import java.util.Vector;
import javax.naming.Name;

public final class LdapName implements Name
{
    private transient String unparsed;
    private transient Vector<Rdn> rdns;
    private transient boolean valuesCaseSensitive;
    static final long serialVersionUID = -1595520034788997356L;
    
    public LdapName(final String unparsed) throws InvalidNameException {
        this.valuesCaseSensitive = false;
        this.unparsed = unparsed;
        this.parse();
    }
    
    private LdapName(final String unparsed, final Vector<Rdn> vector) {
        this.valuesCaseSensitive = false;
        this.unparsed = unparsed;
        this.rdns = (Vector)vector.clone();
    }
    
    private LdapName(final String unparsed, final Vector<Rdn> vector, final int n, final int n2) {
        this.valuesCaseSensitive = false;
        this.unparsed = unparsed;
        this.rdns = new Vector<Rdn>();
        for (int i = n; i < n2; ++i) {
            this.rdns.addElement(vector.elementAt(i));
        }
    }
    
    @Override
    public Object clone() {
        return new LdapName(this.unparsed, this.rdns);
    }
    
    @Override
    public String toString() {
        if (this.unparsed != null) {
            return this.unparsed;
        }
        final StringBuffer sb = new StringBuffer();
        for (int i = this.rdns.size() - 1; i >= 0; --i) {
            if (i < this.rdns.size() - 1) {
                sb.append(',');
            }
            sb.append(this.rdns.elementAt(i));
        }
        return this.unparsed = new String(sb);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof LdapName && this.compareTo(o) == 0;
    }
    
    @Override
    public int compareTo(final Object o) {
        final LdapName ldapName = (LdapName)o;
        if (o == this || (this.unparsed != null && this.unparsed.equals(ldapName.unparsed))) {
            return 0;
        }
        for (int min = Math.min(this.rdns.size(), ldapName.rdns.size()), i = 0; i < min; ++i) {
            final int compareTo = this.rdns.elementAt(i).compareTo(ldapName.rdns.elementAt(i));
            if (compareTo != 0) {
                return compareTo;
            }
        }
        return this.rdns.size() - ldapName.rdns.size();
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        for (int i = 0; i < this.rdns.size(); ++i) {
            n += this.rdns.elementAt(i).hashCode();
        }
        return n;
    }
    
    @Override
    public int size() {
        return this.rdns.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.rdns.isEmpty();
    }
    
    @Override
    public Enumeration<String> getAll() {
        return new Enumeration<String>() {
            final /* synthetic */ Enumeration val$enum_ = LdapName.this.rdns.elements();
            
            @Override
            public boolean hasMoreElements() {
                return this.val$enum_.hasMoreElements();
            }
            
            @Override
            public String nextElement() {
                return this.val$enum_.nextElement().toString();
            }
        };
    }
    
    @Override
    public String get(final int n) {
        return this.rdns.elementAt(n).toString();
    }
    
    @Override
    public Name getPrefix(final int n) {
        return new LdapName(null, this.rdns, 0, n);
    }
    
    @Override
    public Name getSuffix(final int n) {
        return new LdapName(null, this.rdns, n, this.rdns.size());
    }
    
    @Override
    public boolean startsWith(final Name name) {
        final int size = this.rdns.size();
        final int size2 = name.size();
        return size >= size2 && this.matches(0, size2, name);
    }
    
    @Override
    public boolean endsWith(final Name name) {
        final int size = this.rdns.size();
        final int size2 = name.size();
        return size >= size2 && this.matches(size - size2, size, name);
    }
    
    public void setValuesCaseSensitive(final boolean valuesCaseSensitive) {
        this.toString();
        this.rdns = null;
        try {
            this.parse();
        }
        catch (final InvalidNameException ex) {
            throw new IllegalStateException("Cannot parse name: " + this.unparsed);
        }
        this.valuesCaseSensitive = valuesCaseSensitive;
    }
    
    private boolean matches(final int n, final int n2, final Name name) {
        for (int i = n; i < n2; ++i) {
            Rdn rdn;
            if (name instanceof LdapName) {
                rdn = ((LdapName)name).rdns.elementAt(i - n);
            }
            else {
                final String value = name.get(i - n);
                try {
                    rdn = new DnParser(value, this.valuesCaseSensitive).getRdn();
                }
                catch (final InvalidNameException ex) {
                    return false;
                }
            }
            if (!rdn.equals(this.rdns.elementAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public Name addAll(final Name name) throws InvalidNameException {
        return this.addAll(this.size(), name);
    }
    
    @Override
    public Name addAll(int n, final Name name) throws InvalidNameException {
        if (name instanceof LdapName) {
            final LdapName ldapName = (LdapName)name;
            for (int i = 0; i < ldapName.rdns.size(); ++i) {
                this.rdns.insertElementAt(ldapName.rdns.elementAt(i), n++);
            }
        }
        else {
            final Enumeration<String> all = name.getAll();
            while (all.hasMoreElements()) {
                this.rdns.insertElementAt(new DnParser(all.nextElement(), this.valuesCaseSensitive).getRdn(), n++);
            }
        }
        this.unparsed = null;
        return this;
    }
    
    @Override
    public Name add(final String s) throws InvalidNameException {
        return this.add(this.size(), s);
    }
    
    @Override
    public Name add(final int n, final String s) throws InvalidNameException {
        this.rdns.insertElementAt(new DnParser(s, this.valuesCaseSensitive).getRdn(), n);
        this.unparsed = null;
        return this;
    }
    
    @Override
    public Object remove(final int n) throws InvalidNameException {
        final String value = this.get(n);
        this.rdns.removeElementAt(n);
        this.unparsed = null;
        return value;
    }
    
    private void parse() throws InvalidNameException {
        this.rdns = new DnParser(this.unparsed, this.valuesCaseSensitive).getDn();
    }
    
    private static boolean isWhitespace(final char c) {
        return c == ' ' || c == '\r';
    }
    
    public static String escapeAttributeValue(final Object o) {
        return TypeAndValue.escapeValue(o);
    }
    
    public static Object unescapeAttributeValue(final String s) {
        return TypeAndValue.unescapeValue(s);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeObject(this.toString());
        objectOutputStream.writeBoolean(this.valuesCaseSensitive);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        this.unparsed = (String)objectInputStream.readObject();
        this.valuesCaseSensitive = objectInputStream.readBoolean();
        try {
            this.parse();
        }
        catch (final InvalidNameException ex) {
            throw new StreamCorruptedException("Invalid name: " + this.unparsed);
        }
    }
    
    static class DnParser
    {
        private final String name;
        private final char[] chars;
        private final int len;
        private int cur;
        private boolean valuesCaseSensitive;
        
        DnParser(final String name, final boolean valuesCaseSensitive) throws InvalidNameException {
            this.cur = 0;
            this.name = name;
            this.len = name.length();
            this.chars = name.toCharArray();
            this.valuesCaseSensitive = valuesCaseSensitive;
        }
        
        Vector<Rdn> getDn() throws InvalidNameException {
            this.cur = 0;
            final Vector vector = new Vector(this.len / 3 + 10);
            if (this.len == 0) {
                return vector;
            }
            vector.addElement(this.parseRdn());
            while (this.cur < this.len) {
                if (this.chars[this.cur] != ',' && this.chars[this.cur] != ';') {
                    throw new InvalidNameException("Invalid name: " + this.name);
                }
                ++this.cur;
                vector.insertElementAt(this.parseRdn(), 0);
            }
            return vector;
        }
        
        Rdn getRdn() throws InvalidNameException {
            final Rdn rdn = this.parseRdn();
            if (this.cur < this.len) {
                throw new InvalidNameException("Invalid RDN: " + this.name);
            }
            return rdn;
        }
        
        private Rdn parseRdn() throws InvalidNameException {
            final Rdn rdn = new Rdn();
            while (this.cur < this.len) {
                this.consumeWhitespace();
                final String attrType = this.parseAttrType();
                this.consumeWhitespace();
                if (this.cur >= this.len || this.chars[this.cur] != '=') {
                    throw new InvalidNameException("Invalid name: " + this.name);
                }
                ++this.cur;
                this.consumeWhitespace();
                final String attrValue = this.parseAttrValue();
                this.consumeWhitespace();
                rdn.add(new TypeAndValue(attrType, attrValue, this.valuesCaseSensitive));
                if (this.cur >= this.len) {
                    break;
                }
                if (this.chars[this.cur] != '+') {
                    break;
                }
                ++this.cur;
            }
            return rdn;
        }
        
        private String parseAttrType() throws InvalidNameException {
            final int cur = this.cur;
            while (this.cur < this.len) {
                final char c = this.chars[this.cur];
                if (!Character.isLetterOrDigit(c) && c != '.' && c != '-' && c != ' ') {
                    break;
                }
                ++this.cur;
            }
            while (this.cur > cur && this.chars[this.cur - 1] == ' ') {
                --this.cur;
            }
            if (cur == this.cur) {
                throw new InvalidNameException("Invalid name: " + this.name);
            }
            return new String(this.chars, cur, this.cur - cur);
        }
        
        private String parseAttrValue() throws InvalidNameException {
            if (this.cur < this.len && this.chars[this.cur] == '#') {
                return this.parseBinaryAttrValue();
            }
            if (this.cur < this.len && this.chars[this.cur] == '\"') {
                return this.parseQuotedAttrValue();
            }
            return this.parseStringAttrValue();
        }
        
        private String parseBinaryAttrValue() throws InvalidNameException {
            final int cur = this.cur;
            ++this.cur;
            while (this.cur < this.len && Character.isLetterOrDigit(this.chars[this.cur])) {
                ++this.cur;
            }
            return new String(this.chars, cur, this.cur - cur);
        }
        
        private String parseQuotedAttrValue() throws InvalidNameException {
            final int cur = this.cur;
            ++this.cur;
            while (this.cur < this.len && this.chars[this.cur] != '\"') {
                if (this.chars[this.cur] == '\\') {
                    ++this.cur;
                }
                ++this.cur;
            }
            if (this.cur >= this.len) {
                throw new InvalidNameException("Invalid name: " + this.name);
            }
            ++this.cur;
            return new String(this.chars, cur, this.cur - cur);
        }
        
        private String parseStringAttrValue() throws InvalidNameException {
            final int cur = this.cur;
            int cur2 = -1;
            while (this.cur < this.len && !this.atTerminator()) {
                if (this.chars[this.cur] == '\\') {
                    ++this.cur;
                    cur2 = this.cur;
                }
                ++this.cur;
            }
            if (this.cur > this.len) {
                throw new InvalidNameException("Invalid name: " + this.name);
            }
            int cur3;
            for (cur3 = this.cur; cur3 > cur && isWhitespace(this.chars[cur3 - 1]) && cur2 != cur3 - 1; --cur3) {}
            return new String(this.chars, cur, cur3 - cur);
        }
        
        private void consumeWhitespace() {
            while (this.cur < this.len && isWhitespace(this.chars[this.cur])) {
                ++this.cur;
            }
        }
        
        private boolean atTerminator() {
            return this.cur < this.len && (this.chars[this.cur] == ',' || this.chars[this.cur] == ';' || this.chars[this.cur] == '+');
        }
    }
    
    static class Rdn
    {
        private final Vector<TypeAndValue> tvs;
        
        Rdn() {
            this.tvs = new Vector<TypeAndValue>();
        }
        
        void add(final TypeAndValue typeAndValue) {
            int i;
            for (i = 0; i < this.tvs.size(); ++i) {
                final int compareTo = typeAndValue.compareTo(this.tvs.elementAt(i));
                if (compareTo == 0) {
                    return;
                }
                if (compareTo < 0) {
                    break;
                }
            }
            this.tvs.insertElementAt(typeAndValue, i);
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            for (int i = 0; i < this.tvs.size(); ++i) {
                if (i > 0) {
                    sb.append('+');
                }
                sb.append(this.tvs.elementAt(i));
            }
            return new String(sb);
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof Rdn && this.compareTo(o) == 0;
        }
        
        public int compareTo(final Object o) {
            final Rdn rdn = (Rdn)o;
            for (int min = Math.min(this.tvs.size(), rdn.tvs.size()), i = 0; i < min; ++i) {
                final int compareTo = this.tvs.elementAt(i).compareTo(rdn.tvs.elementAt(i));
                if (compareTo != 0) {
                    return compareTo;
                }
            }
            return this.tvs.size() - rdn.tvs.size();
        }
        
        @Override
        public int hashCode() {
            int n = 0;
            for (int i = 0; i < this.tvs.size(); ++i) {
                n += this.tvs.elementAt(i).hashCode();
            }
            return n;
        }
        
        Attributes toAttributes() {
            final BasicAttributes basicAttributes = new BasicAttributes(true);
            for (int i = 0; i < this.tvs.size(); ++i) {
                final TypeAndValue typeAndValue = this.tvs.elementAt(i);
                final Attribute value;
                if ((value = basicAttributes.get(typeAndValue.getType())) == null) {
                    basicAttributes.put(typeAndValue.getType(), typeAndValue.getUnescapedValue());
                }
                else {
                    value.add(typeAndValue.getUnescapedValue());
                }
            }
            return basicAttributes;
        }
    }
    
    static class TypeAndValue
    {
        private final String type;
        private final String value;
        private final boolean binary;
        private final boolean valueCaseSensitive;
        private String comparable;
        
        TypeAndValue(final String type, final String value, final boolean valueCaseSensitive) {
            this.comparable = null;
            this.type = type;
            this.value = value;
            this.binary = value.startsWith("#");
            this.valueCaseSensitive = valueCaseSensitive;
        }
        
        @Override
        public String toString() {
            return this.type + "=" + this.value;
        }
        
        public int compareTo(final Object o) {
            final TypeAndValue typeAndValue = (TypeAndValue)o;
            final int compareToIgnoreCase = this.type.compareToIgnoreCase(typeAndValue.type);
            if (compareToIgnoreCase != 0) {
                return compareToIgnoreCase;
            }
            if (this.value.equals(typeAndValue.value)) {
                return 0;
            }
            return this.getValueComparable().compareTo(typeAndValue.getValueComparable());
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof TypeAndValue)) {
                return false;
            }
            final TypeAndValue typeAndValue = (TypeAndValue)o;
            return this.type.equalsIgnoreCase(typeAndValue.type) && (this.value.equals(typeAndValue.value) || this.getValueComparable().equals(typeAndValue.getValueComparable()));
        }
        
        @Override
        public int hashCode() {
            return this.type.toUpperCase(Locale.ENGLISH).hashCode() + this.getValueComparable().hashCode();
        }
        
        String getType() {
            return this.type;
        }
        
        Object getUnescapedValue() {
            return unescapeValue(this.value);
        }
        
        private String getValueComparable() {
            if (this.comparable != null) {
                return this.comparable;
            }
            if (this.binary) {
                this.comparable = this.value.toUpperCase(Locale.ENGLISH);
            }
            else {
                this.comparable = (String)unescapeValue(this.value);
                if (!this.valueCaseSensitive) {
                    this.comparable = this.comparable.toUpperCase(Locale.ENGLISH);
                }
            }
            return this.comparable;
        }
        
        static String escapeValue(final Object o) {
            return (o instanceof byte[]) ? escapeBinaryValue((byte[])o) : escapeStringValue((String)o);
        }
        
        private static String escapeStringValue(final String s) {
            final char[] charArray = s.toCharArray();
            final StringBuffer sb = new StringBuffer(2 * s.length());
            int n;
            for (n = 0; n < charArray.length && isWhitespace(charArray[n]); ++n) {}
            int n2;
            for (n2 = charArray.length - 1; n2 >= 0 && isWhitespace(charArray[n2]); --n2) {}
            for (int i = 0; i < charArray.length; ++i) {
                final char c = charArray[i];
                if (i < n || i > n2 || ",=+<>#;\"\\".indexOf(c) >= 0) {
                    sb.append('\\');
                }
                sb.append(c);
            }
            return new String(sb);
        }
        
        private static String escapeBinaryValue(final byte[] array) {
            final StringBuffer sb = new StringBuffer(1 + 2 * array.length);
            sb.append("#");
            for (int i = 0; i < array.length; ++i) {
                final byte b = array[i];
                sb.append(Character.forDigit(0xF & b >>> 4, 16));
                sb.append(Character.forDigit(0xF & b, 16));
            }
            return new String(sb).toUpperCase(Locale.ENGLISH);
        }
        
        static Object unescapeValue(final String s) {
            char[] charArray;
            int n;
            int length;
            for (charArray = s.toCharArray(), n = 0, length = charArray.length; n < length && isWhitespace(charArray[n]); ++n) {}
            while (n < length && isWhitespace(charArray[length - 1])) {
                --length;
            }
            if (length != charArray.length && n < length && charArray[length - 1] == '\\') {
                ++length;
            }
            if (n >= length) {
                return "";
            }
            if (charArray[n] == '#') {
                return decodeHexPairs(charArray, ++n, length);
            }
            if (charArray[n] == '\"' && charArray[length - 1] == '\"') {
                ++n;
                --length;
            }
            final StringBuffer sb = new StringBuffer(length - n);
            int n2 = -1;
            for (int i = n; i < length; ++i) {
                if (charArray[i] == '\\' && i + 1 < length) {
                    if (!Character.isLetterOrDigit(charArray[i + 1])) {
                        ++i;
                        sb.append(charArray[i]);
                        n2 = i;
                    }
                    else {
                        final byte[] utf8Octets = getUtf8Octets(charArray, i, length);
                        if (utf8Octets.length <= 0) {
                            throw new IllegalArgumentException("Not a valid attribute string value:" + s + ", improper usage of backslash");
                        }
                        try {
                            sb.append(new String(utf8Octets, "UTF8"));
                        }
                        catch (final UnsupportedEncodingException ex) {}
                        i += utf8Octets.length * 3 - 1;
                    }
                }
                else {
                    sb.append(charArray[i]);
                }
            }
            final int length2 = sb.length();
            if (isWhitespace(sb.charAt(length2 - 1)) && n2 != length - 1) {
                sb.setLength(length2 - 1);
            }
            return new String(sb);
        }
        
        private static byte[] decodeHexPairs(final char[] array, int n, final int n2) {
            final byte[] array2 = new byte[(n2 - n) / 2];
            for (int n3 = 0; n + 1 < n2; n += 2, ++n3) {
                final int digit = Character.digit(array[n], 16);
                final int digit2 = Character.digit(array[n + 1], 16);
                if (digit < 0) {
                    break;
                }
                if (digit2 < 0) {
                    break;
                }
                array2[n3] = (byte)((digit << 4) + digit2);
            }
            if (n != n2) {
                throw new IllegalArgumentException("Illegal attribute value: #" + new String(array));
            }
            return array2;
        }
        
        private static byte[] getUtf8Octets(final char[] array, int n, final int n2) {
            final byte[] array2 = new byte[(n2 - n) / 3];
            int n3 = 0;
            while (n + 2 < n2 && array[n++] == '\\') {
                final int digit = Character.digit(array[n++], 16);
                final int digit2 = Character.digit(array[n++], 16);
                if (digit < 0) {
                    break;
                }
                if (digit2 < 0) {
                    break;
                }
                array2[n3++] = (byte)((digit << 4) + digit2);
            }
            if (n3 == array2.length) {
                return array2;
            }
            final byte[] array3 = new byte[n3];
            System.arraycopy(array2, 0, array3, 0, n3);
            return array3;
        }
    }
}
