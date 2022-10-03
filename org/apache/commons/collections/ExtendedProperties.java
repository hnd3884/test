package org.apache.commons.collections;

import java.util.StringTokenizer;
import java.io.LineNumberReader;
import java.util.NoSuchElementException;
import java.util.Collection;
import java.util.Properties;
import java.util.Enumeration;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.util.Vector;
import java.io.UnsupportedEncodingException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;

public class ExtendedProperties extends Hashtable
{
    private ExtendedProperties defaults;
    protected String file;
    protected String basePath;
    protected String fileSeparator;
    protected boolean isInitialized;
    protected static String include;
    protected ArrayList keysAsListed;
    protected static final String START_TOKEN = "${";
    protected static final String END_TOKEN = "}";
    
    protected String interpolate(final String base) {
        return this.interpolateHelper(base, null);
    }
    
    protected String interpolateHelper(final String base, List priorVariables) {
        if (base == null) {
            return null;
        }
        if (priorVariables == null) {
            priorVariables = new ArrayList();
            priorVariables.add(base);
        }
        int begin = -1;
        int end = -1;
        int prec = 0 - "}".length();
        String variable = null;
        final StringBuffer result = new StringBuffer();
        while ((begin = base.indexOf("${", prec + "}".length())) > -1 && (end = base.indexOf("}", begin)) > -1) {
            result.append(base.substring(prec + "}".length(), begin));
            variable = base.substring(begin + "${".length(), end);
            if (priorVariables.contains(variable)) {
                final String initialBase = priorVariables.remove(0).toString();
                priorVariables.add(variable);
                final StringBuffer priorVariableSb = new StringBuffer();
                final Iterator it = priorVariables.iterator();
                while (it.hasNext()) {
                    priorVariableSb.append(it.next());
                    if (it.hasNext()) {
                        priorVariableSb.append("->");
                    }
                }
                throw new IllegalStateException("infinite loop in property interpolation of " + initialBase + ": " + priorVariableSb.toString());
            }
            priorVariables.add(variable);
            final Object value = this.getProperty(variable);
            if (value != null) {
                result.append(this.interpolateHelper(value.toString(), priorVariables));
                priorVariables.remove(priorVariables.size() - 1);
            }
            else if (this.defaults != null && this.defaults.getString(variable, null) != null) {
                result.append(this.defaults.getString(variable));
            }
            else {
                result.append("${").append(variable).append("}");
            }
            prec = end;
        }
        result.append(base.substring(prec + "}".length(), base.length()));
        return result.toString();
    }
    
    private static String escape(final String s) {
        final StringBuffer buf = new StringBuffer(s);
        for (int i = 0; i < buf.length(); ++i) {
            final char c = buf.charAt(i);
            if (c == ',' || c == '\\') {
                buf.insert(i, '\\');
                ++i;
            }
        }
        return buf.toString();
    }
    
    private static String unescape(final String s) {
        final StringBuffer buf = new StringBuffer(s);
        for (int i = 0; i < buf.length() - 1; ++i) {
            final char c1 = buf.charAt(i);
            final char c2 = buf.charAt(i + 1);
            if (c1 == '\\' && c2 == '\\') {
                buf.deleteCharAt(i);
            }
        }
        return buf.toString();
    }
    
    private static int countPreceding(final String line, final int index, final char ch) {
        int i;
        for (i = index - 1; i >= 0 && line.charAt(i) == ch; --i) {}
        return index - 1 - i;
    }
    
    private static boolean endsWithSlash(final String line) {
        return line.endsWith("\\") && countPreceding(line, line.length() - 1, '\\') % 2 == 0;
    }
    
    public ExtendedProperties() {
        try {
            this.fileSeparator = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
                public Object run() {
                    return System.getProperty("file.separator");
                }
            });
        }
        catch (final SecurityException ex) {
            this.fileSeparator = File.separator;
        }
        this.isInitialized = false;
        this.keysAsListed = new ArrayList();
    }
    
    public ExtendedProperties(final String file) throws IOException {
        this(file, null);
    }
    
    public ExtendedProperties(final String file, final String defaultFile) throws IOException {
        try {
            this.fileSeparator = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
                public Object run() {
                    return System.getProperty("file.separator");
                }
            });
        }
        catch (final SecurityException ex) {
            this.fileSeparator = File.separator;
        }
        this.isInitialized = false;
        this.keysAsListed = new ArrayList();
        this.file = file;
        this.basePath = new File(file).getAbsolutePath();
        this.basePath = this.basePath.substring(0, this.basePath.lastIndexOf(this.fileSeparator) + 1);
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            this.load(in);
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final IOException ex2) {}
        }
        if (defaultFile != null) {
            this.defaults = new ExtendedProperties(defaultFile);
        }
    }
    
    public boolean isInitialized() {
        return this.isInitialized;
    }
    
    public String getInclude() {
        return ExtendedProperties.include;
    }
    
    public void setInclude(final String inc) {
        ExtendedProperties.include = inc;
    }
    
    public void load(final InputStream input) throws IOException {
        this.load(input, null);
    }
    
    public synchronized void load(final InputStream input, final String enc) throws IOException {
        PropertiesReader reader = null;
        if (enc != null) {
            try {
                reader = new PropertiesReader(new InputStreamReader(input, enc));
            }
            catch (final UnsupportedEncodingException ex2) {}
        }
        Label_0071: {
            if (reader != null) {
                break Label_0071;
            }
            try {
                reader = new PropertiesReader(new InputStreamReader(input, "8859_1"));
                break Label_0071;
            }
            catch (final UnsupportedEncodingException ex) {
                reader = new PropertiesReader(new InputStreamReader(input));
            }
            try {
                while (true) {
                    final String line = reader.readProperty();
                    if (line == null) {
                        break;
                    }
                    final int equalSign = line.indexOf(61);
                    if (equalSign <= 0) {
                        continue;
                    }
                    final String key = line.substring(0, equalSign).trim();
                    String value = line.substring(equalSign + 1).trim();
                    if ("".equals(value)) {
                        continue;
                    }
                    if (this.getInclude() != null && key.equalsIgnoreCase(this.getInclude())) {
                        File file = null;
                        if (value.startsWith(this.fileSeparator)) {
                            file = new File(value);
                        }
                        else {
                            if (value.startsWith("." + this.fileSeparator)) {
                                value = value.substring(2);
                            }
                            file = new File(this.basePath + value);
                        }
                        if (file == null || !file.exists() || !file.canRead()) {
                            continue;
                        }
                        this.load(new FileInputStream(file));
                    }
                    else {
                        this.addProperty(key, value);
                    }
                }
            }
            finally {
                this.isInitialized = true;
            }
        }
    }
    
    public Object getProperty(final String key) {
        Object obj = this.get(key);
        if (obj == null && this.defaults != null) {
            obj = this.defaults.get(key);
        }
        return obj;
    }
    
    public void addProperty(final String key, final Object value) {
        if (value instanceof String) {
            final String str = (String)value;
            if (str.indexOf(",") > 0) {
                final PropertiesTokenizer tokenizer = new PropertiesTokenizer(str);
                while (tokenizer.hasMoreTokens()) {
                    final String token = tokenizer.nextToken();
                    this.addPropertyInternal(key, unescape(token));
                }
            }
            else {
                this.addPropertyInternal(key, unescape(str));
            }
        }
        else {
            this.addPropertyInternal(key, value);
        }
        this.isInitialized = true;
    }
    
    private void addPropertyDirect(final String key, final Object value) {
        if (!this.containsKey(key)) {
            this.keysAsListed.add(key);
        }
        this.put(key, value);
    }
    
    private void addPropertyInternal(final String key, final Object value) {
        final Object current = this.get(key);
        if (current instanceof String) {
            final List values = new Vector(2);
            values.add(current);
            values.add(value);
            this.put(key, values);
        }
        else if (current instanceof List) {
            ((List)current).add(value);
        }
        else {
            if (!this.containsKey(key)) {
                this.keysAsListed.add(key);
            }
            this.put(key, value);
        }
    }
    
    public void setProperty(final String key, final Object value) {
        this.clearProperty(key);
        this.addProperty(key, value);
    }
    
    public synchronized void save(final OutputStream output, final String header) throws IOException {
        if (output == null) {
            return;
        }
        final PrintWriter theWrtr = new PrintWriter(output);
        if (header != null) {
            theWrtr.println(header);
        }
        final Enumeration theKeys = this.keys();
        while (theKeys.hasMoreElements()) {
            final String key = theKeys.nextElement();
            final Object value = this.get(key);
            if (value != null) {
                if (value instanceof String) {
                    final StringBuffer currentOutput = new StringBuffer();
                    currentOutput.append(key);
                    currentOutput.append("=");
                    currentOutput.append(escape((String)value));
                    theWrtr.println(currentOutput.toString());
                }
                else if (value instanceof List) {
                    final List values = (List)value;
                    final Iterator it = values.iterator();
                    while (it.hasNext()) {
                        final String currentElement = it.next();
                        final StringBuffer currentOutput2 = new StringBuffer();
                        currentOutput2.append(key);
                        currentOutput2.append("=");
                        currentOutput2.append(escape(currentElement));
                        theWrtr.println(currentOutput2.toString());
                    }
                }
            }
            theWrtr.println();
            theWrtr.flush();
        }
    }
    
    public void combine(final ExtendedProperties props) {
        final Iterator it = props.getKeys();
        while (it.hasNext()) {
            final String key = it.next();
            this.setProperty(key, props.get(key));
        }
    }
    
    public void clearProperty(final String key) {
        if (this.containsKey(key)) {
            for (int i = 0; i < this.keysAsListed.size(); ++i) {
                if (this.keysAsListed.get(i).equals(key)) {
                    this.keysAsListed.remove(i);
                    break;
                }
            }
            this.remove(key);
        }
    }
    
    public Iterator getKeys() {
        return this.keysAsListed.iterator();
    }
    
    public Iterator getKeys(final String prefix) {
        final Iterator keys = this.getKeys();
        final ArrayList matchingKeys = new ArrayList();
        while (keys.hasNext()) {
            final Object key = keys.next();
            if (key instanceof String && ((String)key).startsWith(prefix)) {
                matchingKeys.add(key);
            }
        }
        return matchingKeys.iterator();
    }
    
    public ExtendedProperties subset(final String prefix) {
        final ExtendedProperties c = new ExtendedProperties();
        final Iterator keys = this.getKeys();
        boolean validSubset = false;
        while (keys.hasNext()) {
            final Object key = keys.next();
            if (key instanceof String && ((String)key).startsWith(prefix)) {
                if (!validSubset) {
                    validSubset = true;
                }
                String newKey = null;
                if (((String)key).length() == prefix.length()) {
                    newKey = prefix;
                }
                else {
                    newKey = ((String)key).substring(prefix.length() + 1);
                }
                c.addPropertyDirect(newKey, this.get(key));
            }
        }
        if (validSubset) {
            return c;
        }
        return null;
    }
    
    public void display() {
        final Iterator i = this.getKeys();
        while (i.hasNext()) {
            final String key = i.next();
            final Object value = this.get(key);
            System.out.println(key + " => " + value);
        }
    }
    
    public String getString(final String key) {
        return this.getString(key, null);
    }
    
    public String getString(final String key, final String defaultValue) {
        final Object value = this.get(key);
        if (value instanceof String) {
            return this.interpolate((String)value);
        }
        if (value == null) {
            if (this.defaults != null) {
                return this.interpolate(this.defaults.getString(key, defaultValue));
            }
            return this.interpolate(defaultValue);
        }
        else {
            if (value instanceof List) {
                return this.interpolate(((List)value).get(0));
            }
            throw new ClassCastException('\'' + key + "' doesn't map to a String object");
        }
    }
    
    public Properties getProperties(final String key) {
        return this.getProperties(key, new Properties());
    }
    
    public Properties getProperties(final String key, final Properties defaults) {
        final String[] tokens = this.getStringArray(key);
        final Properties props = new Properties(defaults);
        for (int i = 0; i < tokens.length; ++i) {
            final String token = tokens[i];
            final int equalSign = token.indexOf(61);
            if (equalSign <= 0) {
                throw new IllegalArgumentException('\'' + token + "' does not contain " + "an equals sign");
            }
            final String pkey = token.substring(0, equalSign).trim();
            final String pvalue = token.substring(equalSign + 1).trim();
            ((Hashtable<String, String>)props).put(pkey, pvalue);
        }
        return props;
    }
    
    public String[] getStringArray(final String key) {
        final Object value = this.get(key);
        List values;
        if (value instanceof String) {
            values = new Vector(1);
            values.add(value);
        }
        else if (value instanceof List) {
            values = (List)value;
        }
        else {
            if (value != null) {
                throw new ClassCastException('\'' + key + "' doesn't map to a String/List object");
            }
            if (this.defaults != null) {
                return this.defaults.getStringArray(key);
            }
            return new String[0];
        }
        final String[] tokens = new String[values.size()];
        for (int i = 0; i < tokens.length; ++i) {
            tokens[i] = values.get(i);
        }
        return tokens;
    }
    
    public Vector getVector(final String key) {
        return this.getVector(key, null);
    }
    
    public Vector getVector(final String key, final Vector defaultValue) {
        final Object value = this.get(key);
        if (value instanceof List) {
            return new Vector((Collection<? extends E>)value);
        }
        if (value instanceof String) {
            final Vector values = new Vector(1);
            values.add(value);
            this.put(key, values);
            return values;
        }
        if (value != null) {
            throw new ClassCastException('\'' + key + "' doesn't map to a Vector object");
        }
        if (this.defaults != null) {
            return this.defaults.getVector(key, defaultValue);
        }
        return (defaultValue == null) ? new Vector() : defaultValue;
    }
    
    public List getList(final String key) {
        return this.getList(key, null);
    }
    
    public List getList(final String key, final List defaultValue) {
        final Object value = this.get(key);
        if (value instanceof List) {
            return new ArrayList((Collection)value);
        }
        if (value instanceof String) {
            final List values = new ArrayList(1);
            values.add(value);
            this.put(key, values);
            return values;
        }
        if (value != null) {
            throw new ClassCastException('\'' + key + "' doesn't map to a List object");
        }
        if (this.defaults != null) {
            return this.defaults.getList(key, defaultValue);
        }
        return (defaultValue == null) ? new ArrayList() : defaultValue;
    }
    
    public boolean getBoolean(final String key) {
        final Boolean b = this.getBoolean(key, null);
        if (b != null) {
            return b;
        }
        throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
    }
    
    public boolean getBoolean(final String key, final boolean defaultValue) {
        return this.getBoolean(key, new Boolean(defaultValue));
    }
    
    public Boolean getBoolean(final String key, final Boolean defaultValue) {
        final Object value = this.get(key);
        if (value instanceof Boolean) {
            return (Boolean)value;
        }
        if (value instanceof String) {
            final String s = this.testBoolean((String)value);
            final Boolean b = new Boolean(s);
            this.put(key, b);
            return b;
        }
        if (value != null) {
            throw new ClassCastException('\'' + key + "' doesn't map to a Boolean object");
        }
        if (this.defaults != null) {
            return this.defaults.getBoolean(key, defaultValue);
        }
        return defaultValue;
    }
    
    public String testBoolean(final String value) {
        final String s = value.toLowerCase();
        if (s.equals("true") || s.equals("on") || s.equals("yes")) {
            return "true";
        }
        if (s.equals("false") || s.equals("off") || s.equals("no")) {
            return "false";
        }
        return null;
    }
    
    public byte getByte(final String key) {
        final Byte b = this.getByte(key, null);
        if (b != null) {
            return b;
        }
        throw new NoSuchElementException('\'' + key + " doesn't map to an existing object");
    }
    
    public byte getByte(final String key, final byte defaultValue) {
        return this.getByte(key, new Byte(defaultValue));
    }
    
    public Byte getByte(final String key, final Byte defaultValue) {
        final Object value = this.get(key);
        if (value instanceof Byte) {
            return (Byte)value;
        }
        if (value instanceof String) {
            final Byte b = new Byte((String)value);
            this.put(key, b);
            return b;
        }
        if (value != null) {
            throw new ClassCastException('\'' + key + "' doesn't map to a Byte object");
        }
        if (this.defaults != null) {
            return this.defaults.getByte(key, defaultValue);
        }
        return defaultValue;
    }
    
    public short getShort(final String key) {
        final Short s = this.getShort(key, null);
        if (s != null) {
            return s;
        }
        throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
    }
    
    public short getShort(final String key, final short defaultValue) {
        return this.getShort(key, new Short(defaultValue));
    }
    
    public Short getShort(final String key, final Short defaultValue) {
        final Object value = this.get(key);
        if (value instanceof Short) {
            return (Short)value;
        }
        if (value instanceof String) {
            final Short s = new Short((String)value);
            this.put(key, s);
            return s;
        }
        if (value != null) {
            throw new ClassCastException('\'' + key + "' doesn't map to a Short object");
        }
        if (this.defaults != null) {
            return this.defaults.getShort(key, defaultValue);
        }
        return defaultValue;
    }
    
    public int getInt(final String name) {
        return this.getInteger(name);
    }
    
    public int getInt(final String name, final int def) {
        return this.getInteger(name, def);
    }
    
    public int getInteger(final String key) {
        final Integer i = this.getInteger(key, null);
        if (i != null) {
            return i;
        }
        throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
    }
    
    public int getInteger(final String key, final int defaultValue) {
        final Integer i = this.getInteger(key, null);
        if (i == null) {
            return defaultValue;
        }
        return i;
    }
    
    public Integer getInteger(final String key, final Integer defaultValue) {
        final Object value = this.get(key);
        if (value instanceof Integer) {
            return (Integer)value;
        }
        if (value instanceof String) {
            final Integer i = new Integer((String)value);
            this.put(key, i);
            return i;
        }
        if (value != null) {
            throw new ClassCastException('\'' + key + "' doesn't map to a Integer object");
        }
        if (this.defaults != null) {
            return this.defaults.getInteger(key, defaultValue);
        }
        return defaultValue;
    }
    
    public long getLong(final String key) {
        final Long l = this.getLong(key, null);
        if (l != null) {
            return l;
        }
        throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
    }
    
    public long getLong(final String key, final long defaultValue) {
        return this.getLong(key, new Long(defaultValue));
    }
    
    public Long getLong(final String key, final Long defaultValue) {
        final Object value = this.get(key);
        if (value instanceof Long) {
            return (Long)value;
        }
        if (value instanceof String) {
            final Long l = new Long((String)value);
            this.put(key, l);
            return l;
        }
        if (value != null) {
            throw new ClassCastException('\'' + key + "' doesn't map to a Long object");
        }
        if (this.defaults != null) {
            return this.defaults.getLong(key, defaultValue);
        }
        return defaultValue;
    }
    
    public float getFloat(final String key) {
        final Float f = this.getFloat(key, null);
        if (f != null) {
            return f;
        }
        throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
    }
    
    public float getFloat(final String key, final float defaultValue) {
        return this.getFloat(key, new Float(defaultValue));
    }
    
    public Float getFloat(final String key, final Float defaultValue) {
        final Object value = this.get(key);
        if (value instanceof Float) {
            return (Float)value;
        }
        if (value instanceof String) {
            final Float f = new Float((String)value);
            this.put(key, f);
            return f;
        }
        if (value != null) {
            throw new ClassCastException('\'' + key + "' doesn't map to a Float object");
        }
        if (this.defaults != null) {
            return this.defaults.getFloat(key, defaultValue);
        }
        return defaultValue;
    }
    
    public double getDouble(final String key) {
        final Double d = this.getDouble(key, null);
        if (d != null) {
            return d;
        }
        throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
    }
    
    public double getDouble(final String key, final double defaultValue) {
        return this.getDouble(key, new Double(defaultValue));
    }
    
    public Double getDouble(final String key, final Double defaultValue) {
        final Object value = this.get(key);
        if (value instanceof Double) {
            return (Double)value;
        }
        if (value instanceof String) {
            final Double d = new Double((String)value);
            this.put(key, d);
            return d;
        }
        if (value != null) {
            throw new ClassCastException('\'' + key + "' doesn't map to a Double object");
        }
        if (this.defaults != null) {
            return this.defaults.getDouble(key, defaultValue);
        }
        return defaultValue;
    }
    
    public static ExtendedProperties convertProperties(final Properties props) {
        final ExtendedProperties c = new ExtendedProperties();
        final Enumeration e = props.propertyNames();
        while (e.hasMoreElements()) {
            final String s = e.nextElement();
            c.setProperty(s, props.getProperty(s));
        }
        return c;
    }
    
    static {
        ExtendedProperties.include = "include";
    }
    
    static class PropertiesReader extends LineNumberReader
    {
        public PropertiesReader(final Reader reader) {
            super(reader);
        }
        
        public String readProperty() throws IOException {
            final StringBuffer buffer = new StringBuffer();
            for (String line = this.readLine(); line != null; line = this.readLine()) {
                line = line.trim();
                if (line.length() != 0 && line.charAt(0) != '#') {
                    if (!endsWithSlash(line)) {
                        buffer.append(line);
                        return buffer.toString();
                    }
                    line = line.substring(0, line.length() - 1);
                    buffer.append(line);
                }
            }
            return null;
        }
    }
    
    static class PropertiesTokenizer extends StringTokenizer
    {
        static final String DELIMITER = ",";
        
        public PropertiesTokenizer(final String string) {
            super(string, ",");
        }
        
        public boolean hasMoreTokens() {
            return super.hasMoreTokens();
        }
        
        public String nextToken() {
            final StringBuffer buffer = new StringBuffer();
            while (this.hasMoreTokens()) {
                final String token = super.nextToken();
                if (!endsWithSlash(token)) {
                    buffer.append(token);
                    break;
                }
                buffer.append(token.substring(0, token.length() - 1));
                buffer.append(",");
            }
            return buffer.toString().trim();
        }
    }
}
