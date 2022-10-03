package java.beans;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.nio.charset.Charset;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.io.OutputStreamWriter;
import java.nio.charset.CharsetEncoder;

public class XMLEncoder extends Encoder implements AutoCloseable
{
    private final CharsetEncoder encoder;
    private final String charset;
    private final boolean declaration;
    private OutputStreamWriter out;
    private Object owner;
    private int indentation;
    private boolean internal;
    private Map<Object, ValueData> valueToExpression;
    private Map<Object, List<Statement>> targetToStatementList;
    private boolean preambleWritten;
    private NameGenerator nameGenerator;
    
    public XMLEncoder(final OutputStream outputStream) {
        this(outputStream, "UTF-8", true, 0);
    }
    
    public XMLEncoder(final OutputStream outputStream, final String charset, final boolean declaration, final int indentation) {
        this.indentation = 0;
        this.internal = false;
        this.preambleWritten = false;
        if (outputStream == null) {
            throw new IllegalArgumentException("the output stream cannot be null");
        }
        if (indentation < 0) {
            throw new IllegalArgumentException("the indentation must be >= 0");
        }
        final Charset forName = Charset.forName(charset);
        this.encoder = forName.newEncoder();
        this.charset = charset;
        this.declaration = declaration;
        this.indentation = indentation;
        this.out = new OutputStreamWriter(outputStream, forName.newEncoder());
        this.valueToExpression = new IdentityHashMap<Object, ValueData>();
        this.targetToStatementList = new IdentityHashMap<Object, List<Statement>>();
        this.nameGenerator = new NameGenerator();
    }
    
    public void setOwner(final Object owner) {
        this.owner = owner;
        this.writeExpression(new Expression(this, "getOwner", new Object[0]));
    }
    
    public Object getOwner() {
        return this.owner;
    }
    
    public void writeObject(final Object o) {
        if (this.internal) {
            super.writeObject(o);
        }
        else {
            this.writeStatement(new Statement(this, "writeObject", new Object[] { o }));
        }
    }
    
    private List<Statement> statementList(final Object o) {
        List list = this.targetToStatementList.get(o);
        if (list == null) {
            list = new ArrayList();
            this.targetToStatementList.put(o, list);
        }
        return list;
    }
    
    private void mark(final Object o, final boolean b) {
        if (o == null || o == this) {
            return;
        }
        final ValueData valueData = this.getValueData(o);
        final Expression exp = valueData.exp;
        if (o.getClass() == String.class && exp == null) {
            return;
        }
        if (b) {
            final ValueData valueData2 = valueData;
            ++valueData2.refs;
        }
        if (valueData.marked) {
            return;
        }
        valueData.marked = true;
        final Object target = exp.getTarget();
        this.mark(exp);
        if (!(target instanceof Class)) {
            this.statementList(target).add(exp);
            final ValueData valueData3 = valueData;
            ++valueData3.refs;
        }
    }
    
    private void mark(final Statement statement) {
        final Object[] arguments = statement.getArguments();
        for (int i = 0; i < arguments.length; ++i) {
            this.mark(arguments[i], true);
        }
        this.mark(statement.getTarget(), statement instanceof Expression);
    }
    
    @Override
    public void writeStatement(final Statement statement) {
        final boolean internal = this.internal;
        this.internal = true;
        try {
            super.writeStatement(statement);
            this.mark(statement);
            Object target = statement.getTarget();
            if (target instanceof Field) {
                final String methodName = statement.getMethodName();
                final Object[] arguments = statement.getArguments();
                if (methodName != null) {
                    if (arguments != null) {
                        if (methodName.equals("get") && arguments.length == 1) {
                            target = arguments[0];
                        }
                        else if (methodName.equals("set") && arguments.length == 2) {
                            target = arguments[0];
                        }
                    }
                }
            }
            this.statementList(target).add(statement);
        }
        catch (final Exception ex) {
            this.getExceptionListener().exceptionThrown(new Exception("XMLEncoder: discarding statement " + statement, ex));
        }
        this.internal = internal;
    }
    
    @Override
    public void writeExpression(final Expression exp) {
        final boolean internal = this.internal;
        this.internal = true;
        final Object value = this.getValue(exp);
        if (this.get(value) == null || (value instanceof String && !internal)) {
            super.writeExpression(this.getValueData(value).exp = exp);
        }
        this.internal = internal;
    }
    
    public void flush() {
        if (!this.preambleWritten) {
            if (this.declaration) {
                this.writeln("<?xml version=" + this.quote("1.0") + " encoding=" + this.quote(this.charset) + "?>");
            }
            this.writeln("<java version=" + this.quote(System.getProperty("java.version")) + " class=" + this.quote(XMLDecoder.class.getName()) + ">");
            this.preambleWritten = true;
        }
        ++this.indentation;
        final List<Statement> statementList = this.statementList(this);
        while (!statementList.isEmpty()) {
            final Statement statement = statementList.remove(0);
            if ("writeObject".equals(statement.getMethodName())) {
                this.outputValue(statement.getArguments()[0], this, true);
            }
            else {
                this.outputStatement(statement, this, false);
            }
        }
        --this.indentation;
        for (Statement statement2 = this.getMissedStatement(); statement2 != null; statement2 = this.getMissedStatement()) {
            this.outputStatement(statement2, this, false);
        }
        try {
            this.out.flush();
        }
        catch (final IOException ex) {
            this.getExceptionListener().exceptionThrown(ex);
        }
        this.clear();
    }
    
    @Override
    void clear() {
        super.clear();
        this.nameGenerator.clear();
        this.valueToExpression.clear();
        this.targetToStatementList.clear();
    }
    
    Statement getMissedStatement() {
        for (final List list : this.targetToStatementList.values()) {
            for (int i = 0; i < list.size(); ++i) {
                if (Statement.class == ((Statement)list.get(i)).getClass()) {
                    return (Statement)list.remove(i);
                }
            }
        }
        return null;
    }
    
    @Override
    public void close() {
        this.flush();
        this.writeln("</java>");
        try {
            this.out.close();
        }
        catch (final IOException ex) {
            this.getExceptionListener().exceptionThrown(ex);
        }
    }
    
    private String quote(final String s) {
        return "\"" + s + "\"";
    }
    
    private ValueData getValueData(final Object o) {
        ValueData valueData = this.valueToExpression.get(o);
        if (valueData == null) {
            valueData = new ValueData();
            this.valueToExpression.put(o, valueData);
        }
        return valueData;
    }
    
    private static boolean isValidCharCode(final int n) {
        return (32 <= n && n <= 55295) || 10 == n || 9 == n || 13 == n || (57344 <= n && n <= 65533) || (65536 <= n && n <= 1114111);
    }
    
    private void writeln(final String s) {
        try {
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.indentation; ++i) {
                sb.append(' ');
            }
            sb.append(s);
            sb.append('\n');
            this.out.write(sb.toString());
        }
        catch (final IOException ex) {
            this.getExceptionListener().exceptionThrown(ex);
        }
    }
    
    private void outputValue(Object o, final Object o2, final boolean b) {
        if (o == null) {
            this.writeln("<null/>");
            return;
        }
        if (o instanceof Class) {
            this.writeln("<class>" + ((Class)o).getName() + "</class>");
            return;
        }
        final ValueData valueData = this.getValueData(o);
        if (valueData.exp != null) {
            final Object target = valueData.exp.getTarget();
            final String methodName = valueData.exp.getMethodName();
            if (target == null || methodName == null) {
                throw new NullPointerException(((target == null) ? "target" : "methodName") + " should not be null");
            }
            if (b && target instanceof Field && methodName.equals("get")) {
                final Field field = (Field)target;
                if (Modifier.isStatic(field.getModifiers())) {
                    this.writeln("<object class=" + this.quote(field.getDeclaringClass().getName()) + " field=" + this.quote(field.getName()) + "/>");
                    return;
                }
            }
            final Class primitiveType = primitiveTypeFor(o.getClass());
            if (primitiveType != null && target == o.getClass() && methodName.equals("new")) {
                final String name = primitiveType.getName();
                if (primitiveType == Character.TYPE) {
                    final char charValue = (char)o;
                    if (!isValidCharCode(charValue)) {
                        this.writeln(createString(charValue));
                        return;
                    }
                    o = quoteCharCode(charValue);
                    if (o == null) {
                        o = charValue;
                    }
                }
                this.writeln("<" + name + ">" + o + "</" + name + ">");
                return;
            }
        }
        else if (o instanceof String) {
            this.writeln(this.createString((String)o));
            return;
        }
        if (valueData.name != null) {
            if (b) {
                this.writeln("<object idref=" + this.quote(valueData.name) + "/>");
            }
            else {
                this.outputXML("void", " idref=" + this.quote(valueData.name), o, new Object[0]);
            }
        }
        else if (valueData.exp != null) {
            this.outputStatement(valueData.exp, o2, b);
        }
    }
    
    private static String quoteCharCode(final int n) {
        switch (n) {
            case 38: {
                return "&amp;";
            }
            case 60: {
                return "&lt;";
            }
            case 62: {
                return "&gt;";
            }
            case 34: {
                return "&quot;";
            }
            case 39: {
                return "&apos;";
            }
            case 13: {
                return "&#13;";
            }
            default: {
                return null;
            }
        }
    }
    
    private static String createString(final int n) {
        return "<char code=\"#" + Integer.toString(n, 16) + "\"/>";
    }
    
    private String createString(final String s) {
        final StringBuilder sb = new StringBuilder();
        sb.append("<string>");
        int i = 0;
        while (i < s.length()) {
            final int codePoint = s.codePointAt(i);
            final int charCount = Character.charCount(codePoint);
            if (isValidCharCode(codePoint) && this.encoder.canEncode(s.substring(i, i + charCount))) {
                final String quoteCharCode = quoteCharCode(codePoint);
                if (quoteCharCode != null) {
                    sb.append(quoteCharCode);
                }
                else {
                    sb.appendCodePoint(codePoint);
                }
                i += charCount;
            }
            else {
                sb.append(createString(s.charAt(i)));
                ++i;
            }
        }
        sb.append("</string>");
        return sb.toString();
    }
    
    private void outputStatement(final Statement statement, final Object o, final boolean b) {
        final Object target = statement.getTarget();
        final String methodName = statement.getMethodName();
        if (target == null || methodName == null) {
            throw new NullPointerException(((target == null) ? "target" : "methodName") + " should not be null");
        }
        Object[] arguments = statement.getArguments();
        final boolean b2 = statement.getClass() == Expression.class;
        final Object o2 = b2 ? this.getValue((Expression)statement) : null;
        String s = (b2 && b) ? "object" : "void";
        String s2 = "";
        final ValueData valueData = this.getValueData(o2);
        if (target != o) {
            if (target == Array.class && methodName.equals("newInstance")) {
                s = "array";
                s2 = s2 + " class=" + this.quote(((Class)arguments[0]).getName()) + " length=" + this.quote(arguments[1].toString());
                arguments = new Object[0];
            }
            else {
                if (((Class<Array>)target).getClass() != Class.class) {
                    valueData.refs = 2;
                    if (valueData.name == null) {
                        final ValueData valueData2 = this.getValueData(target);
                        ++valueData2.refs;
                        final List<Statement> statementList = this.statementList(target);
                        if (!statementList.contains(statement)) {
                            statementList.add(statement);
                        }
                        this.outputValue(target, o, false);
                    }
                    if (b2) {
                        this.outputValue(o2, o, b);
                    }
                    return;
                }
                s2 = s2 + " class=" + this.quote(((Class<Array>)target).getName());
            }
        }
        if (b2 && valueData.refs > 1) {
            final String instanceName = this.nameGenerator.instanceName(o2);
            valueData.name = instanceName;
            s2 = s2 + " id=" + this.quote(instanceName);
        }
        if ((!b2 && methodName.equals("set") && arguments.length == 2 && arguments[0] instanceof Integer) || (b2 && methodName.equals("get") && arguments.length == 1 && arguments[0] instanceof Integer)) {
            s2 = s2 + " index=" + this.quote(arguments[0].toString());
            arguments = ((arguments.length == 1) ? new Object[0] : new Object[] { arguments[1] });
        }
        else if ((!b2 && methodName.startsWith("set") && arguments.length == 1) || (b2 && methodName.startsWith("get") && arguments.length == 0)) {
            if (3 < methodName.length()) {
                s2 = s2 + " property=" + this.quote(Introspector.decapitalize(methodName.substring(3)));
            }
        }
        else if (!methodName.equals("new") && !methodName.equals("newInstance")) {
            s2 = s2 + " method=" + this.quote(methodName);
        }
        this.outputXML(s, s2, o2, arguments);
    }
    
    private void outputXML(final String s, final String s2, final Object o, final Object... array) {
        final List<Statement> statementList = this.statementList(o);
        if (array.length == 0 && statementList.size() == 0) {
            this.writeln("<" + s + s2 + "/>");
            return;
        }
        this.writeln("<" + s + s2 + ">");
        ++this.indentation;
        for (int i = 0; i < array.length; ++i) {
            this.outputValue(array[i], null, true);
        }
        while (!statementList.isEmpty()) {
            this.outputStatement((Statement)statementList.remove(0), o, false);
        }
        --this.indentation;
        this.writeln("</" + s + ">");
    }
    
    static Class primitiveTypeFor(final Class clazz) {
        if (clazz == Boolean.class) {
            return Boolean.TYPE;
        }
        if (clazz == Byte.class) {
            return Byte.TYPE;
        }
        if (clazz == Character.class) {
            return Character.TYPE;
        }
        if (clazz == Short.class) {
            return Short.TYPE;
        }
        if (clazz == Integer.class) {
            return Integer.TYPE;
        }
        if (clazz == Long.class) {
            return Long.TYPE;
        }
        if (clazz == Float.class) {
            return Float.TYPE;
        }
        if (clazz == Double.class) {
            return Double.TYPE;
        }
        if (clazz == Void.class) {
            return Void.TYPE;
        }
        return null;
    }
    
    private class ValueData
    {
        public int refs;
        public boolean marked;
        public String name;
        public Expression exp;
        
        private ValueData() {
            this.refs = 0;
            this.marked = false;
            this.name = null;
            this.exp = null;
        }
    }
}
