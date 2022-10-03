package javax.naming.ldap;

import java.util.Locale;
import java.io.StreamCorruptedException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import javax.naming.directory.BasicAttributes;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.InvalidNameException;
import javax.naming.directory.Attributes;
import java.util.ArrayList;
import java.io.Serializable;

public class Rdn implements Serializable, Comparable<Object>
{
    private transient ArrayList<RdnEntry> entries;
    private static final int DEFAULT_SIZE = 1;
    private static final long serialVersionUID = -5994465067210009656L;
    private static final String escapees = ",=+<>#;\"\\";
    
    public Rdn(final Attributes attributes) throws InvalidNameException {
        if (attributes.size() == 0) {
            throw new InvalidNameException("Attributes cannot be empty");
        }
        this.entries = new ArrayList<RdnEntry>(attributes.size());
        final NamingEnumeration<? extends Attribute> all = attributes.getAll();
        try {
            int n = 0;
            while (all.hasMore()) {
                final RdnEntry rdnEntry = new RdnEntry();
                final Attribute attribute = (Attribute)all.next();
                rdnEntry.type = attribute.getID();
                rdnEntry.value = attribute.get();
                this.entries.add(n, rdnEntry);
                ++n;
            }
        }
        catch (final NamingException ex) {
            final InvalidNameException ex2 = new InvalidNameException(ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
        this.sort();
    }
    
    public Rdn(final String s) throws InvalidNameException {
        this.entries = new ArrayList<RdnEntry>(1);
        new Rfc2253Parser(s).parseRdn(this);
    }
    
    public Rdn(final Rdn rdn) {
        (this.entries = new ArrayList<RdnEntry>(rdn.entries.size())).addAll(rdn.entries);
    }
    
    public Rdn(final String s, final Object o) throws InvalidNameException {
        if (o == null) {
            throw new NullPointerException("Cannot set value to null");
        }
        if (s.equals("") || this.isEmptyValue(o)) {
            throw new InvalidNameException("type or value cannot be empty, type:" + s + " value:" + o);
        }
        this.entries = new ArrayList<RdnEntry>(1);
        this.put(s, o);
    }
    
    private boolean isEmptyValue(final Object o) {
        return (o instanceof String && o.equals("")) || (o instanceof byte[] && ((byte[])o).length == 0);
    }
    
    Rdn() {
        this.entries = new ArrayList<RdnEntry>(1);
    }
    
    Rdn put(final String s, final Object o) {
        final RdnEntry rdnEntry = new RdnEntry();
        rdnEntry.type = s;
        if (o instanceof byte[]) {
            rdnEntry.value = ((byte[])o).clone();
        }
        else {
            rdnEntry.value = o;
        }
        this.entries.add(rdnEntry);
        return this;
    }
    
    void sort() {
        if (this.entries.size() > 1) {
            Collections.sort(this.entries);
        }
    }
    
    public Object getValue() {
        return this.entries.get(0).getValue();
    }
    
    public String getType() {
        return this.entries.get(0).getType();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final int size = this.entries.size();
        if (size > 0) {
            sb.append(this.entries.get(0));
        }
        for (int i = 1; i < size; ++i) {
            sb.append('+');
            sb.append(this.entries.get(i));
        }
        return sb.toString();
    }
    
    @Override
    public int compareTo(final Object o) {
        if (!(o instanceof Rdn)) {
            throw new ClassCastException("The obj is not a Rdn");
        }
        if (o == this) {
            return 0;
        }
        final Rdn rdn = (Rdn)o;
        for (int min = Math.min(this.entries.size(), rdn.entries.size()), i = 0; i < min; ++i) {
            final int compareTo = this.entries.get(i).compareTo((RdnEntry)rdn.entries.get(i));
            if (compareTo != 0) {
                return compareTo;
            }
        }
        return this.entries.size() - rdn.entries.size();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Rdn)) {
            return false;
        }
        final Rdn rdn = (Rdn)o;
        if (this.entries.size() != rdn.size()) {
            return false;
        }
        for (int i = 0; i < this.entries.size(); ++i) {
            if (!this.entries.get(i).equals(rdn.entries.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        for (int i = 0; i < this.entries.size(); ++i) {
            n += this.entries.get(i).hashCode();
        }
        return n;
    }
    
    public Attributes toAttributes() {
        final BasicAttributes basicAttributes = new BasicAttributes(true);
        for (int i = 0; i < this.entries.size(); ++i) {
            final RdnEntry rdnEntry = this.entries.get(i);
            final Attribute put = basicAttributes.put(rdnEntry.getType(), rdnEntry.getValue());
            if (put != null) {
                put.add(rdnEntry.getValue());
                basicAttributes.put(put);
            }
        }
        return basicAttributes;
    }
    
    public int size() {
        return this.entries.size();
    }
    
    public static String escapeValue(final Object o) {
        return (o instanceof byte[]) ? escapeBinaryValue((byte[])o) : escapeStringValue((String)o);
    }
    
    private static String escapeStringValue(final String s) {
        final char[] charArray = s.toCharArray();
        final StringBuilder sb = new StringBuilder(2 * s.length());
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
        return sb.toString();
    }
    
    private static String escapeBinaryValue(final byte[] array) {
        final StringBuilder sb = new StringBuilder(1 + 2 * array.length);
        sb.append("#");
        for (int i = 0; i < array.length; ++i) {
            final byte b = array[i];
            sb.append(Character.forDigit(0xF & b >>> 4, 16));
            sb.append(Character.forDigit(0xF & b, 16));
        }
        return sb.toString();
    }
    
    public static Object unescapeValue(final String s) {
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
        final StringBuilder sb = new StringBuilder(length - n);
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
                        throw new IllegalArgumentException("Not a valid attribute string value:" + s + ",improper usage of backslash");
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
        return sb.toString();
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
            throw new IllegalArgumentException("Illegal attribute value: " + new String(array));
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
    
    private static boolean isWhitespace(final char c) {
        return c == ' ' || c == '\r';
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.toString());
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.entries = new ArrayList<RdnEntry>(1);
        final String s = (String)objectInputStream.readObject();
        try {
            new Rfc2253Parser(s).parseRdn(this);
        }
        catch (final InvalidNameException ex) {
            throw new StreamCorruptedException("Invalid name: " + s);
        }
    }
    
    private static class RdnEntry implements Comparable<RdnEntry>
    {
        private String type;
        private Object value;
        private String comparable;
        
        private RdnEntry() {
            this.comparable = null;
        }
        
        String getType() {
            return this.type;
        }
        
        Object getValue() {
            return this.value;
        }
        
        @Override
        public int compareTo(final RdnEntry rdnEntry) {
            final int compareToIgnoreCase = this.type.compareToIgnoreCase(rdnEntry.type);
            if (compareToIgnoreCase != 0) {
                return compareToIgnoreCase;
            }
            if (this.value.equals(rdnEntry.value)) {
                return 0;
            }
            return this.getValueComparable().compareTo(rdnEntry.getValueComparable());
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof RdnEntry)) {
                return false;
            }
            final RdnEntry rdnEntry = (RdnEntry)o;
            return this.type.equalsIgnoreCase(rdnEntry.type) && this.getValueComparable().equals(rdnEntry.getValueComparable());
        }
        
        @Override
        public int hashCode() {
            return this.type.toUpperCase(Locale.ENGLISH).hashCode() + this.getValueComparable().hashCode();
        }
        
        @Override
        public String toString() {
            return this.type + "=" + Rdn.escapeValue(this.value);
        }
        
        private String getValueComparable() {
            if (this.comparable != null) {
                return this.comparable;
            }
            if (this.value instanceof byte[]) {
                this.comparable = escapeBinaryValue((byte[])this.value);
            }
            else {
                this.comparable = ((String)this.value).toUpperCase(Locale.ENGLISH);
            }
            return this.comparable;
        }
    }
}
