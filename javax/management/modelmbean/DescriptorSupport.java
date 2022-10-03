package javax.management.modelmbean;

import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.util.HashMap;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import sun.reflect.misc.ReflectUtil;
import com.sun.jmx.mbeanserver.Util;
import javax.management.ImmutableDescriptor;
import java.util.Iterator;
import java.util.Set;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.StringTokenizer;
import javax.management.MBeanException;
import javax.management.RuntimeOperationsException;
import java.util.logging.Level;
import com.sun.jmx.defaults.JmxProperties;
import java.util.Map;
import java.util.SortedMap;
import java.io.ObjectStreamField;
import javax.management.Descriptor;

public class DescriptorSupport implements Descriptor
{
    private static final long oldSerialVersionUID = 8071560848919417985L;
    private static final long newSerialVersionUID = -6292969195866300415L;
    private static final ObjectStreamField[] oldSerialPersistentFields;
    private static final ObjectStreamField[] newSerialPersistentFields;
    private static final long serialVersionUID;
    private static final ObjectStreamField[] serialPersistentFields;
    private static final String serialForm;
    private transient SortedMap<String, Object> descriptorMap;
    private static final String currClass = "DescriptorSupport";
    private static final String[] entities;
    private static final Map<String, Character> entityToCharMap;
    private static final String[] charToEntityMap;
    
    public DescriptorSupport() {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "DescriptorSupport()", "Constructor");
        }
        this.init(null);
    }
    
    public DescriptorSupport(final int n) throws MBeanException, RuntimeOperationsException {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(initNumFields = " + n + ")", "Constructor");
        }
        if (n <= 0) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(initNumFields)", "Illegal arguments: initNumFields <= 0");
            }
            final String string = "Descriptor field limit invalid: " + n;
            throw new RuntimeOperationsException(new IllegalArgumentException(string), string);
        }
        this.init(null);
    }
    
    public DescriptorSupport(final DescriptorSupport descriptorSupport) {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(Descriptor)", "Constructor");
        }
        if (descriptorSupport == null) {
            this.init(null);
        }
        else {
            this.init(descriptorSupport.descriptorMap);
        }
    }
    
    public DescriptorSupport(final String s) throws MBeanException, RuntimeOperationsException, XMLParseException {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(String = '" + s + "')", "Constructor");
        }
        if (s == null) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(String = null)", "Illegal arguments");
            }
            throw new RuntimeOperationsException(new IllegalArgumentException("String in parameter is null"), "String in parameter is null");
        }
        final String lowerCase = s.toLowerCase();
        if (!lowerCase.startsWith("<descriptor>") || !lowerCase.endsWith("</descriptor>")) {
            throw new XMLParseException("No <descriptor>, </descriptor> pair");
        }
        this.init(null);
        final StringTokenizer stringTokenizer = new StringTokenizer(s, "<> \t\n\r\f");
        boolean b = false;
        boolean b2 = false;
        String s2 = null;
        String s3 = null;
        while (stringTokenizer.hasMoreTokens()) {
            final String nextToken = stringTokenizer.nextToken();
            if (nextToken.equalsIgnoreCase("FIELD")) {
                b = true;
            }
            else if (nextToken.equalsIgnoreCase("/FIELD")) {
                if (s2 != null && s3 != null) {
                    this.setField(s2.substring(s2.indexOf(34) + 1, s2.lastIndexOf(34)), parseQuotedFieldValue(s3));
                }
                s2 = null;
                s3 = null;
                b = false;
            }
            else if (nextToken.equalsIgnoreCase("DESCRIPTOR")) {
                b2 = true;
            }
            else if (nextToken.equalsIgnoreCase("/DESCRIPTOR")) {
                b2 = false;
                s2 = null;
                s3 = null;
                b = false;
            }
            else {
                if (!b || !b2) {
                    continue;
                }
                final int index = nextToken.indexOf("=");
                if (index <= 0) {
                    throw new XMLParseException("Expected `keyword=value', got `" + nextToken + "'");
                }
                final String substring = nextToken.substring(0, index);
                final String substring2 = nextToken.substring(index + 1);
                if (substring.equalsIgnoreCase("NAME")) {
                    s2 = substring2;
                }
                else {
                    if (!substring.equalsIgnoreCase("VALUE")) {
                        throw new XMLParseException("Expected `name' or `value', got `" + nextToken + "'");
                    }
                    s3 = substring2;
                }
            }
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(XMLString)", "Exit");
        }
    }
    
    public DescriptorSupport(final String[] array, final Object[] array2) throws RuntimeOperationsException {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(fieldNames,fieldObjects)", "Constructor");
        }
        if (array == null || array2 == null || array.length != array2.length) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(fieldNames,fieldObjects)", "Illegal arguments");
            }
            throw new RuntimeOperationsException(new IllegalArgumentException("Null or invalid fieldNames or fieldValues"), "Null or invalid fieldNames or fieldValues");
        }
        this.init(null);
        for (int i = 0; i < array.length; ++i) {
            this.setField(array[i], array2[i]);
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(fieldNames,fieldObjects)", "Exit");
        }
    }
    
    public DescriptorSupport(final String... array) {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(String... fields)", "Constructor");
        }
        this.init(null);
        if (array == null || array.length == 0) {
            return;
        }
        this.init(null);
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != null) {
                if (!array[i].equals("")) {
                    final int index = array[i].indexOf("=");
                    if (index < 0) {
                        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
                            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(String... fields)", "Illegal arguments: field does not have '=' as a name and value separator");
                        }
                        throw new RuntimeOperationsException(new IllegalArgumentException("Field in invalid format: no equals sign"), "Field in invalid format: no equals sign");
                    }
                    final String substring = array[i].substring(0, index);
                    Object substring2 = null;
                    if (index < array[i].length()) {
                        substring2 = array[i].substring(index + 1);
                    }
                    if (substring.equals("")) {
                        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
                            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(String... fields)", "Illegal arguments: fieldName is empty");
                        }
                        throw new RuntimeOperationsException(new IllegalArgumentException("Field in invalid format: no fieldName"), "Field in invalid format: no fieldName");
                    }
                    this.setField(substring, substring2);
                }
            }
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(String... fields)", "Exit");
        }
    }
    
    private void init(final Map<String, ?> map) {
        this.descriptorMap = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
        if (map != null) {
            this.descriptorMap.putAll((Map<?, ?>)map);
        }
    }
    
    @Override
    public synchronized Object getFieldValue(final String s) throws RuntimeOperationsException {
        if (s == null || s.equals("")) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldValue(String fieldName)", "Illegal arguments: null field name");
            }
            throw new RuntimeOperationsException(new IllegalArgumentException("Fieldname requested is null"), "Fieldname requested is null");
        }
        final Object value = this.descriptorMap.get(s);
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldValue(String fieldName = " + s + ")", "Returns '" + value + "'");
        }
        return value;
    }
    
    @Override
    public synchronized void setField(final String s, final Object o) throws RuntimeOperationsException {
        if (s == null || s.equals("")) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setField(fieldName,fieldValue)", "Illegal arguments: null or empty field name");
            }
            throw new RuntimeOperationsException(new IllegalArgumentException("Field name to be set is null or empty"), "Field name to be set is null or empty");
        }
        if (!this.validateField(s, o)) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setField(fieldName,fieldValue)", "Illegal arguments");
            }
            final String string = "Field value invalid: " + s + "=" + o;
            throw new RuntimeOperationsException(new IllegalArgumentException(string), string);
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setField(fieldName,fieldValue)", "Entry: setting '" + s + "' to '" + o + "'");
        }
        this.descriptorMap.put(s, o);
    }
    
    @Override
    public synchronized String[] getFields() {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFields()", "Entry");
        }
        final int size = this.descriptorMap.size();
        final String[] array = new String[size];
        final Set<Map.Entry<String, Object>> entrySet = this.descriptorMap.entrySet();
        int n = 0;
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFields()", "Returning " + size + " fields");
        }
        for (final Map.Entry entry : entrySet) {
            if (entry == null) {
                if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
                    JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFields()", "Element is null");
                }
            }
            else {
                final Object value = entry.getValue();
                if (value == null) {
                    array[n] = (String)entry.getKey() + "=";
                }
                else if (value instanceof String) {
                    array[n] = (String)entry.getKey() + "=" + value.toString();
                }
                else {
                    array[n] = (String)entry.getKey() + "=(" + value.toString() + ")";
                }
            }
            ++n;
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFields()", "Exit");
        }
        return array;
    }
    
    @Override
    public synchronized String[] getFieldNames() {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldNames()", "Entry");
        }
        final int size = this.descriptorMap.size();
        final String[] array = new String[size];
        final Set<Map.Entry<String, Object>> entrySet = this.descriptorMap.entrySet();
        int n = 0;
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldNames()", "Returning " + size + " fields");
        }
        for (final Map.Entry entry : entrySet) {
            if (entry == null || entry.getKey() == null) {
                if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
                    JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldNames()", "Field is null");
                }
            }
            else {
                array[n] = ((String)entry.getKey()).toString();
            }
            ++n;
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldNames()", "Exit");
        }
        return array;
    }
    
    @Override
    public synchronized Object[] getFieldValues(final String... array) {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldValues(String... fieldNames)", "Entry");
        }
        final int n = (array == null) ? this.descriptorMap.size() : array.length;
        final Object[] array2 = new Object[n];
        int n2 = 0;
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldValues(String... fieldNames)", "Returning " + n + " fields");
        }
        if (array == null) {
            final Iterator<Object> iterator = this.descriptorMap.values().iterator();
            while (iterator.hasNext()) {
                array2[n2++] = iterator.next();
            }
        }
        else {
            for (int i = 0; i < array.length; ++i) {
                if (array[i] == null || array[i].equals("")) {
                    array2[i] = null;
                }
                else {
                    array2[i] = this.getFieldValue(array[i]);
                }
            }
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldValues(String... fieldNames)", "Exit");
        }
        return array2;
    }
    
    @Override
    public synchronized void setFields(final String[] array, final Object[] array2) throws RuntimeOperationsException {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setFields(fieldNames,fieldValues)", "Entry");
        }
        if (array == null || array2 == null || array.length != array2.length) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setFields(fieldNames,fieldValues)", "Illegal arguments");
            }
            throw new RuntimeOperationsException(new IllegalArgumentException("fieldNames and fieldValues are null or invalid"), "fieldNames and fieldValues are null or invalid");
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == null || array[i].equals("")) {
                if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
                    JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setFields(fieldNames,fieldValues)", "Null field name encountered at element " + i);
                }
                throw new RuntimeOperationsException(new IllegalArgumentException("fieldNames is null or invalid"), "fieldNames is null or invalid");
            }
            this.setField(array[i], array2[i]);
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setFields(fieldNames,fieldValues)", "Exit");
        }
    }
    
    @Override
    public synchronized Object clone() throws RuntimeOperationsException {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "clone()", "Entry");
        }
        return new DescriptorSupport(this);
    }
    
    @Override
    public synchronized void removeField(final String s) {
        if (s == null || s.equals("")) {
            return;
        }
        this.descriptorMap.remove(s);
    }
    
    @Override
    public synchronized boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Descriptor)) {
            return false;
        }
        if (o instanceof ImmutableDescriptor) {
            return o.equals(this);
        }
        return new ImmutableDescriptor(this.descriptorMap).equals(o);
    }
    
    @Override
    public synchronized int hashCode() {
        final int size = this.descriptorMap.size();
        return Util.hashCode(this.descriptorMap.keySet().toArray(new String[size]), this.descriptorMap.values().toArray(new Object[size]));
    }
    
    @Override
    public synchronized boolean isValid() throws RuntimeOperationsException {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "isValid()", "Entry");
        }
        final Set<Map.Entry<String, Object>> entrySet = this.descriptorMap.entrySet();
        if (entrySet == null) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "isValid()", "Returns false (null set)");
            }
            return false;
        }
        final String s = (String)this.getFieldValue("name");
        final String s2 = (String)this.getFieldValue("descriptorType");
        if (s == null || s2 == null || s.equals("") || s2.equals("")) {
            return false;
        }
        for (final Map.Entry entry : entrySet) {
            if (entry != null && entry.getValue() != null) {
                if (this.validateField(((String)entry.getKey()).toString(), entry.getValue().toString())) {
                    continue;
                }
                if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
                    JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "isValid()", "Field " + (String)entry.getKey() + "=" + entry.getValue() + " is not valid");
                }
                return false;
            }
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "isValid()", "Returns true");
        }
        return true;
    }
    
    private boolean validateField(final String s, final Object o) {
        if (s == null || s.equals("")) {
            return false;
        }
        String s2 = "";
        boolean b = false;
        if (o != null && o instanceof String) {
            s2 = (String)o;
            b = true;
        }
        final boolean b2 = s.equalsIgnoreCase("Name") || s.equalsIgnoreCase("DescriptorType");
        if (b2 || s.equalsIgnoreCase("SetMethod") || s.equalsIgnoreCase("GetMethod") || s.equalsIgnoreCase("Role") || s.equalsIgnoreCase("Class")) {
            return o != null && b && (!b2 || !s2.equals(""));
        }
        if (s.equalsIgnoreCase("visibility")) {
            long numeric;
            if (o != null && b) {
                numeric = this.toNumeric(s2);
            }
            else {
                if (!(o instanceof Integer)) {
                    return false;
                }
                numeric = (int)o;
            }
            return numeric >= 1L && numeric <= 4L;
        }
        if (s.equalsIgnoreCase("severity")) {
            long numeric2;
            if (o != null && b) {
                numeric2 = this.toNumeric(s2);
            }
            else {
                if (!(o instanceof Integer)) {
                    return false;
                }
                numeric2 = (int)o;
            }
            return numeric2 >= 0L && numeric2 <= 6L;
        }
        if (s.equalsIgnoreCase("PersistPolicy")) {
            return o != null && b && (s2.equalsIgnoreCase("OnUpdate") || s2.equalsIgnoreCase("OnTimer") || s2.equalsIgnoreCase("NoMoreOftenThan") || s2.equalsIgnoreCase("Always") || s2.equalsIgnoreCase("Never") || s2.equalsIgnoreCase("OnUnregister"));
        }
        if (s.equalsIgnoreCase("PersistPeriod") || s.equalsIgnoreCase("CurrencyTimeLimit") || s.equalsIgnoreCase("LastUpdatedTimeStamp") || s.equalsIgnoreCase("LastReturnedTimeStamp")) {
            long n;
            if (o != null && b) {
                n = this.toNumeric(s2);
            }
            else {
                if (!(o instanceof Number)) {
                    return false;
                }
                n = ((Number)o).longValue();
            }
            return n >= -1L;
        }
        return !s.equalsIgnoreCase("log") || o instanceof Boolean || (b && (s2.equalsIgnoreCase("T") || s2.equalsIgnoreCase("true") || s2.equalsIgnoreCase("F") || s2.equalsIgnoreCase("false")));
    }
    
    public synchronized String toXMLString() {
        final StringBuilder sb = new StringBuilder("<Descriptor>");
        for (final Map.Entry entry : this.descriptorMap.entrySet()) {
            final String s = (String)entry.getKey();
            final Object value = entry.getValue();
            String s2 = null;
            if (value instanceof String) {
                final String s3 = (String)value;
                if (!s3.startsWith("(") || !s3.endsWith(")")) {
                    s2 = quote(s3);
                }
            }
            if (s2 == null) {
                s2 = makeFieldValue(value);
            }
            sb.append("<field name=\"").append(s).append("\" value=\"").append(s2).append("\"></field>");
        }
        sb.append("</Descriptor>");
        return sb.toString();
    }
    
    private static boolean isMagic(final char c) {
        return c < DescriptorSupport.charToEntityMap.length && DescriptorSupport.charToEntityMap[c] != null;
    }
    
    private static String quote(final String s) {
        boolean b = false;
        for (int i = 0; i < s.length(); ++i) {
            if (isMagic(s.charAt(i))) {
                b = true;
                break;
            }
        }
        if (!b) {
            return s;
        }
        final StringBuilder sb = new StringBuilder();
        for (int j = 0; j < s.length(); ++j) {
            final char char1 = s.charAt(j);
            if (isMagic(char1)) {
                sb.append(DescriptorSupport.charToEntityMap[char1]);
            }
            else {
                sb.append(char1);
            }
        }
        return sb.toString();
    }
    
    private static String unquote(final String s) throws XMLParseException {
        if (!s.startsWith("\"") || !s.endsWith("\"")) {
            throw new XMLParseException("Value must be quoted: <" + s + ">");
        }
        final StringBuilder sb = new StringBuilder();
        for (int n = s.length() - 1, i = 1; i < n; ++i) {
            final char char1 = s.charAt(i);
            final int index;
            final Character c;
            if (char1 == '&' && (index = s.indexOf(59, i + 1)) >= 0 && (c = DescriptorSupport.entityToCharMap.get(s.substring(i, index + 1))) != null) {
                sb.append(c);
                i = index;
            }
            else {
                sb.append(char1);
            }
        }
        return sb.toString();
    }
    
    private static String makeFieldValue(final Object o) {
        if (o == null) {
            return "(null)";
        }
        final Class<?> class1 = o.getClass();
        try {
            class1.getConstructor(String.class);
        }
        catch (final NoSuchMethodException ex) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Class " + class1 + " does not have a public constructor with a single string arg"), "Cannot make XML descriptor");
        }
        catch (final SecurityException ex2) {}
        return "(" + class1.getName() + "/" + quote(o.toString()) + ")";
    }
    
    private static Object parseQuotedFieldValue(String unquote) throws XMLParseException {
        unquote = unquote(unquote);
        if (unquote.equalsIgnoreCase("(null)")) {
            return null;
        }
        if (!unquote.startsWith("(") || !unquote.endsWith(")")) {
            return unquote;
        }
        final int index = unquote.indexOf(47);
        if (index < 0) {
            return unquote.substring(1, unquote.length() - 1);
        }
        final String substring = unquote.substring(1, index);
        Constructor<?> constructor;
        try {
            ReflectUtil.checkPackageAccess(substring);
            constructor = Class.forName(substring, false, Thread.currentThread().getContextClassLoader()).getConstructor(String.class);
        }
        catch (final Exception ex) {
            throw new XMLParseException(ex, "Cannot parse value: <" + unquote + ">");
        }
        final String substring2 = unquote.substring(index + 1, unquote.length() - 1);
        try {
            return constructor.newInstance(substring2);
        }
        catch (final Exception ex2) {
            throw new XMLParseException(ex2, "Cannot construct instance of " + substring + " with arg: <" + unquote + ">");
        }
    }
    
    @Override
    public synchronized String toString() {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "toString()", "Entry");
        }
        String s = "";
        final String[] fields = this.getFields();
        if (fields == null || fields.length == 0) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "toString()", "Empty Descriptor");
            }
            return s;
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "toString()", "Printing " + fields.length + " fields");
        }
        for (int i = 0; i < fields.length; ++i) {
            if (i == fields.length - 1) {
                s = s.concat(fields[i]);
            }
            else {
                s = s.concat(fields[i] + ", ");
            }
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "toString()", "Exit returning " + s);
        }
        return s;
    }
    
    private long toNumeric(final String s) {
        try {
            return Long.parseLong(s);
        }
        catch (final Exception ex) {
            return -2L;
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        final Map map = Util.cast(objectInputStream.readFields().get("descriptor", null));
        this.init(null);
        if (map != null) {
            this.descriptorMap.putAll((Map<?, ?>)map);
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
        final boolean equals = "1.0".equals(DescriptorSupport.serialForm);
        if (equals) {
            putFields.put("currClass", "DescriptorSupport");
        }
        SortedMap<String, Object> descriptorMap = this.descriptorMap;
        if (descriptorMap.containsKey("targetObject")) {
            descriptorMap = new TreeMap<String, Object>(this.descriptorMap);
            descriptorMap.remove("targetObject");
        }
        HashMap<String, Object> hashMap;
        if (equals || "1.2.0".equals(DescriptorSupport.serialForm) || "1.2.1".equals(DescriptorSupport.serialForm)) {
            hashMap = new HashMap<String, Object>();
            for (final Map.Entry entry : descriptorMap.entrySet()) {
                hashMap.put(((String)entry.getKey()).toLowerCase(), entry.getValue());
            }
        }
        else {
            hashMap = new HashMap<String, Object>(descriptorMap);
        }
        putFields.put("descriptor", hashMap);
        objectOutputStream.writeFields();
    }
    
    static {
        oldSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("descriptor", HashMap.class), new ObjectStreamField("currClass", String.class) };
        newSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("descriptor", HashMap.class) };
        Object serialForm2 = null;
        boolean equals = false;
        try {
            serialForm2 = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.serial.form"));
            equals = "1.0".equals(serialForm2);
        }
        catch (final Exception ex) {}
        serialForm = (String)serialForm2;
        if (equals) {
            serialPersistentFields = DescriptorSupport.oldSerialPersistentFields;
            serialVersionUID = 8071560848919417985L;
        }
        else {
            serialPersistentFields = DescriptorSupport.newSerialPersistentFields;
            serialVersionUID = -6292969195866300415L;
        }
        entities = new String[] { " &#32;", "\"&quot;", "<&lt;", ">&gt;", "&&amp;", "\r&#13;", "\t&#9;", "\n&#10;", "\f&#12;" };
        entityToCharMap = new HashMap<String, Character>();
        int n = '\0';
        for (int i = 0; i < DescriptorSupport.entities.length; ++i) {
            final char char1 = DescriptorSupport.entities[i].charAt(0);
            if (char1 > n) {
                n = char1;
            }
        }
        charToEntityMap = new String[n + '\u0001'];
        for (int j = 0; j < DescriptorSupport.entities.length; ++j) {
            final char char2 = DescriptorSupport.entities[j].charAt(0);
            final String substring = DescriptorSupport.entities[j].substring(1);
            DescriptorSupport.charToEntityMap[char2] = substring;
            DescriptorSupport.entityToCharMap.put(substring, char2);
        }
    }
}
