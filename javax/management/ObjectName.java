package javax.management;

import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Util;
import java.util.Hashtable;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.io.ObjectStreamField;

public class ObjectName implements Comparable<ObjectName>, QueryExp
{
    private static final long oldSerialVersionUID = -5467795090068647408L;
    private static final long newSerialVersionUID = 1081892073854801359L;
    private static final ObjectStreamField[] oldSerialPersistentFields;
    private static final ObjectStreamField[] newSerialPersistentFields;
    private static final long serialVersionUID;
    private static final ObjectStreamField[] serialPersistentFields;
    private static boolean compat;
    private static final Property[] _Empty_property_array;
    private transient String _canonicalName;
    private transient Property[] _kp_array;
    private transient Property[] _ca_array;
    private transient int _domain_length;
    private transient Map<String, String> _propertyList;
    private transient boolean _domain_pattern;
    private transient boolean _property_list_pattern;
    private transient boolean _property_value_pattern;
    public static final ObjectName WILDCARD;
    
    private void construct(final String s) throws MalformedObjectNameException {
        if (s == null) {
            throw new NullPointerException("name cannot be null");
        }
        if (s.length() == 0) {
            this._canonicalName = "*:*";
            this._kp_array = ObjectName._Empty_property_array;
            this._ca_array = ObjectName._Empty_property_array;
            this._domain_length = 1;
            this._propertyList = null;
            this._domain_pattern = true;
            this._property_list_pattern = true;
            this._property_value_pattern = false;
            return;
        }
        final char[] charArray = s.toCharArray();
        final int length = charArray.length;
        final char[] array = new char[length];
        int i = 0;
    Label_0228:
        while (i < length) {
            switch (charArray[i]) {
                case ':': {
                    this._domain_length = i++;
                    break Label_0228;
                }
                case '=': {
                    int n = ++i;
                    while (n < length && charArray[n++] != ':') {
                        if (n == length) {
                            throw new MalformedObjectNameException("Domain part must be specified");
                        }
                    }
                    continue;
                }
                case '\n': {
                    throw new MalformedObjectNameException("Invalid character '\\n' in domain name");
                }
                case '*':
                case '?': {
                    this._domain_pattern = true;
                    ++i;
                    continue;
                }
                default: {
                    ++i;
                    continue;
                }
            }
        }
        if (i == length) {
            throw new MalformedObjectNameException("Key properties cannot be empty");
        }
        System.arraycopy(charArray, 0, array, 0, this._domain_length);
        array[this._domain_length] = ':';
        final int n2 = this._domain_length + 1;
        final HashMap<String, Property> hashMap = new HashMap<String, Property>();
        int n3 = 0;
        String[] array2 = new String[10];
        this._kp_array = new Property[10];
        this._property_list_pattern = false;
        this._property_value_pattern = false;
        while (i < length) {
            if (charArray[i] == '*') {
                if (this._property_list_pattern) {
                    throw new MalformedObjectNameException("Cannot have several '*' characters in pattern property list");
                }
                this._property_list_pattern = true;
                if (++i < length && charArray[i] != ',') {
                    throw new MalformedObjectNameException("Invalid character found after '*': end of name or ',' expected");
                }
                if (i == length) {
                    if (n3 == 0) {
                        this._kp_array = ObjectName._Empty_property_array;
                        this._ca_array = ObjectName._Empty_property_array;
                        this._propertyList = Collections.emptyMap();
                        break;
                    }
                    break;
                }
                else {
                    ++i;
                }
            }
            else {
                final int n5;
                int n4 = n5 = i;
                if (charArray[n4] == '=') {
                    throw new MalformedObjectNameException("Invalid key (empty)");
                }
                char c;
                while (n4 < length && (c = charArray[n4++]) != '=') {
                    switch (c) {
                        case 10:
                        case 42:
                        case 44:
                        case 58:
                        case 63: {
                            throw new MalformedObjectNameException("Invalid character '" + ((c == '\n') ? "\\n" : ("" + c)) + "' in key part of property");
                        }
                        default: {
                            continue;
                        }
                    }
                }
                if (charArray[n4 - 1] != '=') {
                    throw new MalformedObjectNameException("Unterminated key property part");
                }
                final int n6 = n4;
                final int n7 = n6 - n5 - 1;
                boolean b = false;
                boolean b2;
                int n8;
                if (n4 < length && charArray[n4] == '\"') {
                    b2 = true;
                    char c2;
                    while (++n4 < length && (c2 = charArray[n4]) != '\"') {
                        if (c2 == '\\') {
                            if (++n4 == length) {
                                throw new MalformedObjectNameException("Unterminated quoted value");
                            }
                            final char c3;
                            switch (c3 = charArray[n4]) {
                                case '\"':
                                case '*':
                                case '?':
                                case '\\':
                                case 'n': {
                                    continue;
                                }
                                default: {
                                    throw new MalformedObjectNameException("Invalid escape sequence '\\" + c3 + "' in quoted value");
                                }
                            }
                        }
                        else {
                            if (c2 == '\n') {
                                throw new MalformedObjectNameException("Newline in quoted value");
                            }
                            switch (c2) {
                                case 42:
                                case 63: {
                                    b = true;
                                    continue;
                                }
                            }
                        }
                    }
                    if (n4 == length) {
                        throw new MalformedObjectNameException("Unterminated quoted value");
                    }
                    n8 = ++n4 - n6;
                }
                else {
                    b2 = false;
                    char c4;
                    while (n4 < length && (c4 = charArray[n4]) != ',') {
                        switch (c4) {
                            case 42:
                            case 63: {
                                b = true;
                                ++n4;
                                continue;
                            }
                            case 10:
                            case 34:
                            case 58:
                            case 61: {
                                throw new MalformedObjectNameException("Invalid character '" + ((c4 == '\n') ? "\\n" : ("" + c4)) + "' in value part of property");
                            }
                            default: {
                                ++n4;
                                continue;
                            }
                        }
                    }
                    n8 = n4 - n6;
                }
                if (n4 == length - 1) {
                    if (b2) {
                        throw new MalformedObjectNameException("Invalid ending character `" + charArray[n4] + "'");
                    }
                    throw new MalformedObjectNameException("Invalid ending comma");
                }
                else {
                    ++n4;
                    Property property;
                    if (!b) {
                        property = new Property(n5, n7, n8);
                    }
                    else {
                        this._property_value_pattern = true;
                        property = new PatternProperty(n5, n7, n8);
                    }
                    final String substring = s.substring(n5, n5 + n7);
                    if (n3 == array2.length) {
                        final String[] array3 = new String[n3 + 10];
                        System.arraycopy(array2, 0, array3, 0, n3);
                        array2 = array3;
                    }
                    this.addProperty(property, n3, hashMap, array2[n3] = substring);
                    ++n3;
                    i = n4;
                }
            }
        }
        this.setCanonicalName(charArray, array, array2, hashMap, n2, n3);
    }
    
    private void construct(final String s, final Map<String, String> map) throws MalformedObjectNameException {
        if (s == null) {
            throw new NullPointerException("domain cannot be null");
        }
        if (map == null) {
            throw new NullPointerException("key property list cannot be null");
        }
        if (map.isEmpty()) {
            throw new MalformedObjectNameException("key property list cannot be empty");
        }
        if (!this.isDomain(s)) {
            throw new MalformedObjectNameException("Invalid domain: " + s);
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(s).append(':');
        this._domain_length = s.length();
        final int size = map.size();
        this._kp_array = new Property[size];
        final String[] array = new String[size];
        final HashMap hashMap = new HashMap();
        int n = 0;
        for (final Map.Entry entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            final String s2 = (String)entry.getKey();
            String s3;
            try {
                s3 = (String)entry.getValue();
            }
            catch (final ClassCastException ex) {
                throw new MalformedObjectNameException(ex.getMessage());
            }
            final int length = sb.length();
            checkKey(s2);
            sb.append(s2);
            array[n] = s2;
            sb.append("=");
            final boolean checkValue = checkValue(s3);
            sb.append(s3);
            Property property;
            if (!checkValue) {
                property = new Property(length, s2.length(), s3.length());
            }
            else {
                this._property_value_pattern = true;
                property = new PatternProperty(length, s2.length(), s3.length());
            }
            this.addProperty(property, n, hashMap, s2);
            ++n;
        }
        final int length2 = sb.length();
        final char[] array2 = new char[length2];
        sb.getChars(0, length2, array2, 0);
        final char[] array3 = new char[length2];
        System.arraycopy(array2, 0, array3, 0, this._domain_length + 1);
        this.setCanonicalName(array2, array3, array, hashMap, this._domain_length + 1, this._kp_array.length);
    }
    
    private void addProperty(final Property property, final int n, final Map<String, Property> map, final String s) throws MalformedObjectNameException {
        if (map.containsKey(s)) {
            throw new MalformedObjectNameException("key `" + s + "' already defined");
        }
        if (n == this._kp_array.length) {
            final Property[] kp_array = new Property[n + 10];
            System.arraycopy(this._kp_array, 0, kp_array, 0, n);
            this._kp_array = kp_array;
        }
        map.put(s, this._kp_array[n] = property);
    }
    
    private void setCanonicalName(final char[] array, final char[] array2, String[] array3, final Map<String, Property> map, int keyIndex, final int n) {
        if (this._kp_array != ObjectName._Empty_property_array) {
            final String[] array4 = new String[n];
            final Property[] kp_array = new Property[n];
            System.arraycopy(array3, 0, array4, 0, n);
            Arrays.sort(array4);
            array3 = array4;
            System.arraycopy(this._kp_array, 0, kp_array, 0, n);
            this._kp_array = kp_array;
            this._ca_array = new Property[n];
            for (int i = 0; i < n; ++i) {
                this._ca_array[i] = map.get(array3[i]);
            }
            for (int n2 = n - 1, j = 0; j <= n2; ++j) {
                final Property property = this._ca_array[j];
                final int n3 = property._key_length + property._value_length + 1;
                System.arraycopy(array, property._key_index, array2, keyIndex, n3);
                property.setKeyIndex(keyIndex);
                keyIndex += n3;
                if (j != n2) {
                    array2[keyIndex] = ',';
                    ++keyIndex;
                }
            }
        }
        if (this._property_list_pattern) {
            if (this._kp_array != ObjectName._Empty_property_array) {
                array2[keyIndex++] = ',';
            }
            array2[keyIndex++] = '*';
        }
        this._canonicalName = new String(array2, 0, keyIndex).intern();
    }
    
    private static int parseKey(final char[] array, final int n) throws MalformedObjectNameException {
        int i = n;
        int n2 = n;
        final int length = array.length;
    Label_0172:
        while (i < length) {
            final char c = array[i++];
            switch (c) {
                case 10:
                case 42:
                case 44:
                case 58:
                case 63: {
                    throw new MalformedObjectNameException("Invalid character in key: `" + ((c == '\n') ? "\\n" : ("" + c)) + "'");
                }
                case 61: {
                    n2 = i - 1;
                    break Label_0172;
                }
                default: {
                    if (i < length) {
                        continue;
                    }
                    n2 = i;
                    break Label_0172;
                }
            }
        }
        return n2;
    }
    
    private static int[] parseValue(final char[] array, final int n) throws MalformedObjectNameException {
        boolean b = false;
        int i = n;
        int n2 = n;
        final int length = array.length;
        Label_0491: {
            if (array[n] == '\"') {
                if (++i == length) {
                    throw new MalformedObjectNameException("Invalid quote");
                }
                while (i < length) {
                    char c = array[i];
                    if (c == '\\') {
                        if (++i == length) {
                            throw new MalformedObjectNameException("Invalid unterminated quoted character sequence");
                        }
                        c = array[i];
                        switch (c) {
                            case 42:
                            case 63:
                            case 92:
                            case 110: {
                                break;
                            }
                            case 34: {
                                if (i + 1 == length) {
                                    throw new MalformedObjectNameException("Missing termination quote");
                                }
                                break;
                            }
                            default: {
                                throw new MalformedObjectNameException("Invalid quoted character sequence '\\" + c + "'");
                            }
                        }
                    }
                    else {
                        if (c == '\n') {
                            throw new MalformedObjectNameException("Newline in quoted value");
                        }
                        if (c == '\"') {
                            ++i;
                            break;
                        }
                        switch (c) {
                            case '*':
                            case '?': {
                                b = true;
                                break;
                            }
                        }
                    }
                    if (++i >= length && c != '\"') {
                        throw new MalformedObjectNameException("Missing termination quote");
                    }
                }
                if ((n2 = i) < length && array[i++] != ',') {
                    throw new MalformedObjectNameException("Invalid quote");
                }
            }
            else {
                while (i < length) {
                    final char c2 = array[i++];
                    switch (c2) {
                        case 42:
                        case 63: {
                            b = true;
                            if (i < length) {
                                continue;
                            }
                            n2 = i;
                            break Label_0491;
                        }
                        case 10:
                        case 58:
                        case 61: {
                            throw new MalformedObjectNameException("Invalid character `" + ((c2 == '\n') ? "\\n" : ("" + c2)) + "' in value");
                        }
                        case 44: {
                            n2 = i - 1;
                            break Label_0491;
                        }
                        default: {
                            if (i < length) {
                                continue;
                            }
                            n2 = i;
                            break Label_0491;
                        }
                    }
                }
            }
        }
        return new int[] { n2, b ? 1 : 0 };
    }
    
    private static boolean checkValue(final String s) throws MalformedObjectNameException {
        if (s == null) {
            throw new NullPointerException("Invalid value (null)");
        }
        final int length = s.length();
        if (length == 0) {
            return false;
        }
        final char[] charArray = s.toCharArray();
        final int[] value = parseValue(charArray, 0);
        final int n = value[0];
        final boolean b = value[1] == 1;
        if (n < length) {
            throw new MalformedObjectNameException("Invalid character in value: `" + charArray[n] + "'");
        }
        return b;
    }
    
    private static void checkKey(final String s) throws MalformedObjectNameException {
        if (s == null) {
            throw new NullPointerException("Invalid key (null)");
        }
        final int length = s.length();
        if (length == 0) {
            throw new MalformedObjectNameException("Invalid key (empty)");
        }
        final char[] charArray = s.toCharArray();
        final int key = parseKey(charArray, 0);
        if (key < length) {
            throw new MalformedObjectNameException("Invalid character in value: `" + charArray[key] + "'");
        }
    }
    
    private boolean isDomain(final String s) {
        if (s == null) {
            return true;
        }
        final int length = s.length();
        int i = 0;
        while (i < length) {
            switch (s.charAt(i++)) {
                case '\n':
                case ':': {
                    return false;
                }
                case '*':
                case '?': {
                    this._domain_pattern = true;
                    continue;
                }
            }
        }
        return true;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        String string;
        if (ObjectName.compat) {
            final ObjectInputStream.GetField fields = objectInputStream.readFields();
            String s = (String)fields.get("propertyListString", "");
            if (fields.get("propertyPattern", false)) {
                s = ((s.length() == 0) ? "*" : (s + ",*"));
            }
            string = (String)fields.get("domain", "default") + ":" + s;
        }
        else {
            objectInputStream.defaultReadObject();
            string = (String)objectInputStream.readObject();
        }
        try {
            this.construct(string);
        }
        catch (final NullPointerException ex) {
            throw new InvalidObjectException(ex.toString());
        }
        catch (final MalformedObjectNameException ex2) {
            throw new InvalidObjectException(ex2.toString());
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (ObjectName.compat) {
            final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
            putFields.put("domain", this._canonicalName.substring(0, this._domain_length));
            putFields.put("propertyList", this.getKeyPropertyList());
            putFields.put("propertyListString", this.getKeyPropertyListString());
            putFields.put("canonicalName", this._canonicalName);
            putFields.put("pattern", this._domain_pattern || this._property_list_pattern);
            putFields.put("propertyPattern", this._property_list_pattern);
            objectOutputStream.writeFields();
        }
        else {
            objectOutputStream.defaultWriteObject();
            objectOutputStream.writeObject(this.getSerializedNameString());
        }
    }
    
    public static ObjectName getInstance(final String s) throws MalformedObjectNameException, NullPointerException {
        return new ObjectName(s);
    }
    
    public static ObjectName getInstance(final String s, final String s2, final String s3) throws MalformedObjectNameException {
        return new ObjectName(s, s2, s3);
    }
    
    public static ObjectName getInstance(final String s, final Hashtable<String, String> hashtable) throws MalformedObjectNameException {
        return new ObjectName(s, hashtable);
    }
    
    public static ObjectName getInstance(final ObjectName objectName) {
        if (objectName.getClass().equals(ObjectName.class)) {
            return objectName;
        }
        return Util.newObjectName(objectName.getSerializedNameString());
    }
    
    public ObjectName(final String s) throws MalformedObjectNameException {
        this._domain_length = 0;
        this._domain_pattern = false;
        this._property_list_pattern = false;
        this._property_value_pattern = false;
        this.construct(s);
    }
    
    public ObjectName(final String s, final String s2, final String s3) throws MalformedObjectNameException {
        this._domain_length = 0;
        this._domain_pattern = false;
        this._property_list_pattern = false;
        this._property_value_pattern = false;
        this.construct(s, Collections.singletonMap(s2, s3));
    }
    
    public ObjectName(final String s, final Hashtable<String, String> hashtable) throws MalformedObjectNameException {
        this._domain_length = 0;
        this._domain_pattern = false;
        this._property_list_pattern = false;
        this._property_value_pattern = false;
        this.construct(s, hashtable);
    }
    
    public boolean isPattern() {
        return this._domain_pattern || this._property_list_pattern || this._property_value_pattern;
    }
    
    public boolean isDomainPattern() {
        return this._domain_pattern;
    }
    
    public boolean isPropertyPattern() {
        return this._property_list_pattern || this._property_value_pattern;
    }
    
    public boolean isPropertyListPattern() {
        return this._property_list_pattern;
    }
    
    public boolean isPropertyValuePattern() {
        return this._property_value_pattern;
    }
    
    public boolean isPropertyValuePattern(final String s) {
        if (s == null) {
            throw new NullPointerException("key property can't be null");
        }
        for (int i = 0; i < this._ca_array.length; ++i) {
            final Property property = this._ca_array[i];
            if (property.getKeyString(this._canonicalName).equals(s)) {
                return property instanceof PatternProperty;
            }
        }
        throw new IllegalArgumentException("key property not found");
    }
    
    public String getCanonicalName() {
        return this._canonicalName;
    }
    
    public String getDomain() {
        return this._canonicalName.substring(0, this._domain_length);
    }
    
    public String getKeyProperty(final String s) {
        return this._getKeyPropertyList().get(s);
    }
    
    private Map<String, String> _getKeyPropertyList() {
        synchronized (this) {
            if (this._propertyList == null) {
                this._propertyList = new HashMap<String, String>();
                for (int i = this._ca_array.length - 1; i >= 0; --i) {
                    final Property property = this._ca_array[i];
                    this._propertyList.put(property.getKeyString(this._canonicalName), property.getValueString(this._canonicalName));
                }
            }
        }
        return this._propertyList;
    }
    
    public Hashtable<String, String> getKeyPropertyList() {
        return new Hashtable<String, String>(this._getKeyPropertyList());
    }
    
    public String getKeyPropertyListString() {
        if (this._kp_array.length == 0) {
            return "";
        }
        final char[] array = new char[this._canonicalName.length() - this._domain_length - 1 - (this._property_list_pattern ? 2 : 0)];
        this.writeKeyPropertyListString(this._canonicalName.toCharArray(), array, 0);
        return new String(array);
    }
    
    private String getSerializedNameString() {
        final char[] array = new char[this._canonicalName.length()];
        final char[] charArray = this._canonicalName.toCharArray();
        final int n = this._domain_length + 1;
        System.arraycopy(charArray, 0, array, 0, n);
        final int writeKeyPropertyListString = this.writeKeyPropertyListString(charArray, array, n);
        if (this._property_list_pattern) {
            if (writeKeyPropertyListString == n) {
                array[writeKeyPropertyListString] = '*';
            }
            else {
                array[writeKeyPropertyListString] = ',';
                array[writeKeyPropertyListString + 1] = '*';
            }
        }
        return new String(array);
    }
    
    private int writeKeyPropertyListString(final char[] array, final char[] array2, final int n) {
        if (this._kp_array.length == 0) {
            return n;
        }
        int n2 = n;
        final int length = this._kp_array.length;
        final int n3 = length - 1;
        for (int i = 0; i < length; ++i) {
            final Property property = this._kp_array[i];
            final int n4 = property._key_length + property._value_length + 1;
            System.arraycopy(array, property._key_index, array2, n2, n4);
            n2 += n4;
            if (i < n3) {
                array2[n2++] = ',';
            }
        }
        return n2;
    }
    
    public String getCanonicalKeyPropertyListString() {
        if (this._ca_array.length == 0) {
            return "";
        }
        int length = this._canonicalName.length();
        if (this._property_list_pattern) {
            length -= 2;
        }
        return this._canonicalName.substring(this._domain_length + 1, length);
    }
    
    @Override
    public String toString() {
        return this.getSerializedNameString();
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof ObjectName && this._canonicalName == ((ObjectName)o)._canonicalName);
    }
    
    @Override
    public int hashCode() {
        return this._canonicalName.hashCode();
    }
    
    public static String quote(final String s) {
        final StringBuilder sb = new StringBuilder("\"");
        for (int length = s.length(), i = 0; i < length; ++i) {
            char char1 = s.charAt(i);
            switch (char1) {
                case 10: {
                    char1 = 'n';
                    sb.append('\\');
                    break;
                }
                case 34:
                case 42:
                case 63:
                case 92: {
                    sb.append('\\');
                    break;
                }
            }
            sb.append(char1);
        }
        sb.append('\"');
        return sb.toString();
    }
    
    public static String unquote(final String s) {
        final StringBuilder sb = new StringBuilder();
        final int length = s.length();
        if (length < 2 || s.charAt(0) != '\"' || s.charAt(length - 1) != '\"') {
            throw new IllegalArgumentException("Argument not quoted");
        }
        for (int i = 1; i < length - 1; ++i) {
            char c = s.charAt(i);
            if (c == '\\') {
                if (i == length - 2) {
                    throw new IllegalArgumentException("Trailing backslash");
                }
                c = s.charAt(++i);
                switch (c) {
                    case 110: {
                        c = '\n';
                        break;
                    }
                    case 34:
                    case 42:
                    case 63:
                    case 92: {
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Bad character '" + c + "' after backslash");
                    }
                }
            }
            else {
                switch (c) {
                    case '\n':
                    case '\"':
                    case '*':
                    case '?': {
                        throw new IllegalArgumentException("Invalid unescaped character '" + c + "' in the string to unquote");
                    }
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }
    
    @Override
    public boolean apply(final ObjectName objectName) {
        if (objectName == null) {
            throw new NullPointerException();
        }
        if (objectName._domain_pattern || objectName._property_list_pattern || objectName._property_value_pattern) {
            return false;
        }
        if (!this._domain_pattern && !this._property_list_pattern && !this._property_value_pattern) {
            return this._canonicalName.equals(objectName._canonicalName);
        }
        return this.matchDomains(objectName) && this.matchKeys(objectName);
    }
    
    private final boolean matchDomains(final ObjectName objectName) {
        if (this._domain_pattern) {
            return Util.wildmatch(objectName.getDomain(), this.getDomain());
        }
        return this.getDomain().equals(objectName.getDomain());
    }
    
    private final boolean matchKeys(final ObjectName objectName) {
        if (this._property_value_pattern && !this._property_list_pattern && objectName._ca_array.length != this._ca_array.length) {
            return false;
        }
        if (this._property_value_pattern || this._property_list_pattern) {
            final Map<String, String> getKeyPropertyList = objectName._getKeyPropertyList();
            final Property[] ca_array = this._ca_array;
            final String canonicalName = this._canonicalName;
            for (int i = ca_array.length - 1; i >= 0; --i) {
                final Property property = ca_array[i];
                final String s = getKeyPropertyList.get(property.getKeyString(canonicalName));
                if (s == null) {
                    return false;
                }
                if (this._property_value_pattern && property instanceof PatternProperty) {
                    if (!Util.wildmatch(s, property.getValueString(canonicalName))) {
                        return false;
                    }
                }
                else if (!s.equals(property.getValueString(canonicalName))) {
                    return false;
                }
            }
            return true;
        }
        return objectName.getCanonicalKeyPropertyListString().equals(this.getCanonicalKeyPropertyListString());
    }
    
    @Override
    public void setMBeanServer(final MBeanServer mBeanServer) {
    }
    
    @Override
    public int compareTo(final ObjectName objectName) {
        if (objectName == this) {
            return 0;
        }
        final int compareTo = this.getDomain().compareTo(objectName.getDomain());
        if (compareTo != 0) {
            return compareTo;
        }
        String keyProperty = this.getKeyProperty("type");
        String keyProperty2 = objectName.getKeyProperty("type");
        if (keyProperty == null) {
            keyProperty = "";
        }
        if (keyProperty2 == null) {
            keyProperty2 = "";
        }
        final int compareTo2 = keyProperty.compareTo(keyProperty2);
        if (compareTo2 != 0) {
            return compareTo2;
        }
        return this.getCanonicalName().compareTo(objectName.getCanonicalName());
    }
    
    static {
        oldSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("domain", String.class), new ObjectStreamField("propertyList", Hashtable.class), new ObjectStreamField("propertyListString", String.class), new ObjectStreamField("canonicalName", String.class), new ObjectStreamField("pattern", Boolean.TYPE), new ObjectStreamField("propertyPattern", Boolean.TYPE) };
        newSerialPersistentFields = new ObjectStreamField[0];
        ObjectName.compat = false;
        try {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.serial.form"));
            ObjectName.compat = (s != null && s.equals("1.0"));
        }
        catch (final Exception ex) {}
        if (ObjectName.compat) {
            serialPersistentFields = ObjectName.oldSerialPersistentFields;
            serialVersionUID = -5467795090068647408L;
        }
        else {
            serialPersistentFields = ObjectName.newSerialPersistentFields;
            serialVersionUID = 1081892073854801359L;
        }
        _Empty_property_array = new Property[0];
        WILDCARD = Util.newObjectName("*:*");
    }
    
    private static class Property
    {
        int _key_index;
        int _key_length;
        int _value_length;
        
        Property(final int key_index, final int key_length, final int value_length) {
            this._key_index = key_index;
            this._key_length = key_length;
            this._value_length = value_length;
        }
        
        void setKeyIndex(final int key_index) {
            this._key_index = key_index;
        }
        
        String getKeyString(final String s) {
            return s.substring(this._key_index, this._key_index + this._key_length);
        }
        
        String getValueString(final String s) {
            final int n = this._key_index + this._key_length + 1;
            return s.substring(n, n + this._value_length);
        }
    }
    
    private static class PatternProperty extends Property
    {
        PatternProperty(final int n, final int n2, final int n3) {
            super(n, n2, n3);
        }
    }
}
