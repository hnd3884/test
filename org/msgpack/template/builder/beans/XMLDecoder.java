package org.msgpack.template.builder.beans;

import org.xml.sax.SAXParseException;
import java.util.Iterator;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.lang.reflect.Array;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.util.HashMap;
import org.apache.harmony.beans.internal.nls.Messages;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import java.util.Stack;
import java.io.InputStream;

public class XMLDecoder
{
    private ClassLoader defaultClassLoader;
    private InputStream inputStream;
    private ExceptionListener listener;
    private Object owner;
    private Stack<Elem> readObjs;
    private int readObjIndex;
    private SAXHandler saxHandler;
    
    public XMLDecoder(final InputStream inputStream) {
        this(inputStream, null, null, null);
    }
    
    public XMLDecoder(final InputStream inputStream, final Object owner) {
        this(inputStream, owner, null, null);
    }
    
    public XMLDecoder(final InputStream inputStream, final Object owner, final ExceptionListener listener) {
        this(inputStream, owner, listener, null);
    }
    
    public XMLDecoder(final InputStream inputStream, final Object owner, final ExceptionListener listener, final ClassLoader cl) {
        this.defaultClassLoader = null;
        this.readObjs = new Stack<Elem>();
        this.readObjIndex = 0;
        this.saxHandler = null;
        this.inputStream = inputStream;
        this.owner = owner;
        this.listener = ((listener == null) ? new DefaultExceptionListener() : listener);
        this.defaultClassLoader = cl;
    }
    
    public void close() {
        if (this.inputStream == null) {
            return;
        }
        try {
            this.inputStream.close();
        }
        catch (final Exception e) {
            this.listener.exceptionThrown(e);
        }
    }
    
    public ExceptionListener getExceptionListener() {
        return this.listener;
    }
    
    public Object getOwner() {
        return this.owner;
    }
    
    public Object readObject() {
        if (this.inputStream == null) {
            return null;
        }
        if (this.saxHandler == null) {
            this.saxHandler = new SAXHandler();
            try {
                SAXParserFactory.newInstance().newSAXParser().parse(this.inputStream, this.saxHandler);
            }
            catch (final Exception e) {
                this.listener.exceptionThrown(e);
            }
        }
        if (this.readObjIndex >= this.readObjs.size()) {
            throw new ArrayIndexOutOfBoundsException(Messages.getString("custom.beans.70"));
        }
        final Elem elem = this.readObjs.get(this.readObjIndex);
        if (!elem.isClosed) {
            throw new ArrayIndexOutOfBoundsException(Messages.getString("custom.beans.70"));
        }
        ++this.readObjIndex;
        return elem.result;
    }
    
    public void setExceptionListener(final ExceptionListener listener) {
        if (listener != null) {
            this.listener = listener;
        }
    }
    
    public void setOwner(final Object owner) {
        this.owner = owner;
    }
    
    private static class DefaultExceptionListener implements ExceptionListener
    {
        @Override
        public void exceptionThrown(final Exception e) {
            System.err.println(e.getMessage());
            System.err.println("Continue...");
        }
    }
    
    private class SAXHandler extends DefaultHandler
    {
        boolean inJavaElem;
        HashMap<String, Object> idObjMap;
        
        private SAXHandler() {
            this.inJavaElem = false;
            this.idObjMap = new HashMap<String, Object>();
        }
        
        @Override
        public void characters(final char[] ch, final int start, final int length) throws SAXException {
            if (!this.inJavaElem) {
                return;
            }
            if (XMLDecoder.this.readObjs.size() > 0) {
                final Elem elem = XMLDecoder.this.readObjs.peek();
                if (elem.isBasicType) {
                    final String str = new String(ch, start, length);
                    elem.methodName = ((elem.methodName == null) ? str : (elem.methodName + str));
                }
            }
        }
        
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
            if (!this.inJavaElem) {
                if ("java".equals(qName)) {
                    this.inJavaElem = true;
                }
                else {
                    XMLDecoder.this.listener.exceptionThrown(new Exception(Messages.getString("custom.beans.72", qName)));
                }
                return;
            }
            if ("object".equals(qName)) {
                this.startObjectElem(attributes);
            }
            else if ("array".equals(qName)) {
                this.startArrayElem(attributes);
            }
            else if ("void".equals(qName)) {
                this.startVoidElem(attributes);
            }
            else if ("boolean".equals(qName) || "byte".equals(qName) || "char".equals(qName) || "class".equals(qName) || "double".equals(qName) || "float".equals(qName) || "int".equals(qName) || "long".equals(qName) || "short".equals(qName) || "string".equals(qName) || "null".equals(qName)) {
                this.startBasicElem(qName, attributes);
            }
        }
        
        private void startObjectElem(final Attributes attributes) {
            final Elem elem = new Elem();
            elem.isExpression = true;
            elem.id = attributes.getValue("id");
            elem.idref = attributes.getValue("idref");
            elem.attributes = attributes;
            if (elem.idref == null) {
                this.obtainTarget(elem, attributes);
                this.obtainMethod(elem, attributes);
            }
            XMLDecoder.this.readObjs.push(elem);
        }
        
        private void obtainTarget(final Elem elem, final Attributes attributes) {
            final String className = attributes.getValue("class");
            if (className != null) {
                try {
                    elem.target = this.classForName(className);
                }
                catch (final ClassNotFoundException e) {
                    XMLDecoder.this.listener.exceptionThrown(e);
                }
            }
            else {
                final Elem parent = this.latestUnclosedElem();
                if (parent == null) {
                    elem.target = XMLDecoder.this.owner;
                    return;
                }
                elem.target = this.execute(parent);
            }
        }
        
        private void obtainMethod(final Elem elem, final Attributes attributes) {
            elem.methodName = attributes.getValue("method");
            if (elem.methodName != null) {
                return;
            }
            elem.methodName = attributes.getValue("property");
            if (elem.methodName != null) {
                elem.fromProperty = true;
                return;
            }
            elem.methodName = attributes.getValue("index");
            if (elem.methodName != null) {
                elem.fromIndex = true;
                return;
            }
            elem.methodName = attributes.getValue("field");
            if (elem.methodName != null) {
                elem.fromField = true;
                return;
            }
            elem.methodName = attributes.getValue("owner");
            if (elem.methodName != null) {
                elem.fromOwner = true;
                return;
            }
            elem.methodName = "new";
        }
        
        private Class<?> classForName(final String className) throws ClassNotFoundException {
            if ("boolean".equals(className)) {
                return Boolean.TYPE;
            }
            if ("byte".equals(className)) {
                return Byte.TYPE;
            }
            if ("char".equals(className)) {
                return Character.TYPE;
            }
            if ("double".equals(className)) {
                return Double.TYPE;
            }
            if ("float".equals(className)) {
                return Float.TYPE;
            }
            if ("int".equals(className)) {
                return Integer.TYPE;
            }
            if ("long".equals(className)) {
                return Long.TYPE;
            }
            if ("short".equals(className)) {
                return Short.TYPE;
            }
            return Class.forName(className, true, (XMLDecoder.this.defaultClassLoader == null) ? Thread.currentThread().getContextClassLoader() : XMLDecoder.this.defaultClassLoader);
        }
        
        private void startArrayElem(final Attributes attributes) {
            final Elem elem = new Elem();
            elem.isExpression = true;
            elem.id = attributes.getValue("id");
            elem.attributes = attributes;
            try {
                final Class<?> compClass = this.classForName(attributes.getValue("class"));
                final String lengthValue = attributes.getValue("length");
                if (lengthValue != null) {
                    final int length = Integer.parseInt(attributes.getValue("length"));
                    elem.result = Array.newInstance(compClass, length);
                    elem.isExecuted = true;
                }
                else {
                    elem.target = compClass;
                    elem.methodName = "newArray";
                    elem.isExecuted = false;
                }
            }
            catch (final Exception e) {
                XMLDecoder.this.listener.exceptionThrown(e);
            }
            XMLDecoder.this.readObjs.push(elem);
        }
        
        private void startVoidElem(final Attributes attributes) {
            final Elem elem = new Elem();
            elem.id = attributes.getValue("id");
            this.obtainTarget(elem, elem.attributes = attributes);
            this.obtainMethod(elem, attributes);
            XMLDecoder.this.readObjs.push(elem);
        }
        
        private void startBasicElem(final String tagName, final Attributes attributes) {
            final Elem elem = new Elem();
            elem.isBasicType = true;
            elem.isExpression = true;
            elem.id = attributes.getValue("id");
            elem.idref = attributes.getValue("idref");
            elem.attributes = attributes;
            elem.target = tagName;
            XMLDecoder.this.readObjs.push(elem);
        }
        
        @Override
        public void endElement(final String uri, final String localName, final String qName) throws SAXException {
            if (!this.inJavaElem) {
                return;
            }
            if ("java".equals(qName)) {
                this.inJavaElem = false;
                return;
            }
            final Elem toClose = this.latestUnclosedElem();
            if ("string".equals(toClose.target)) {
                final StringBuilder sb = new StringBuilder();
                for (int index = XMLDecoder.this.readObjs.size() - 1; index >= 0; --index) {
                    final Elem elem = (Elem)XMLDecoder.this.readObjs.get(index);
                    if (toClose == elem) {
                        break;
                    }
                    if ("char".equals(elem.target)) {
                        sb.insert(0, elem.methodName);
                    }
                }
                toClose.methodName = ((toClose.methodName != null) ? (toClose.methodName + sb.toString()) : sb.toString());
            }
            this.execute(toClose);
            toClose.isClosed = true;
            while (XMLDecoder.this.readObjs.pop() != toClose) {}
            if (toClose.isExpression) {
                XMLDecoder.this.readObjs.push(toClose);
            }
        }
        
        private Elem latestUnclosedElem() {
            for (int i = XMLDecoder.this.readObjs.size() - 1; i >= 0; --i) {
                final Elem elem = (Elem)XMLDecoder.this.readObjs.get(i);
                if (!elem.isClosed) {
                    return elem;
                }
            }
            return null;
        }
        
        private Object execute(final Elem elem) {
            if (elem.isExecuted) {
                return elem.result;
            }
            try {
                if (elem.idref != null) {
                    elem.result = this.idObjMap.get(elem.idref);
                }
                else if (elem.isBasicType) {
                    elem.result = this.executeBasic(elem);
                }
                else {
                    elem.result = this.executeCommon(elem);
                }
            }
            catch (final Exception e) {
                XMLDecoder.this.listener.exceptionThrown(e);
            }
            if (elem.id != null) {
                this.idObjMap.put(elem.id, elem.result);
            }
            elem.isExecuted = true;
            return elem.result;
        }
        
        private Object executeCommon(final Elem elem) throws Exception {
            final ArrayList<Object> args = new ArrayList<Object>(5);
            while (XMLDecoder.this.readObjs.peek() != elem) {
                final Elem argElem = XMLDecoder.this.readObjs.pop();
                args.add(0, argElem.result);
            }
            String method = elem.methodName;
            if (elem.fromProperty) {
                method = ((args.size() == 0) ? "get" : "set") + this.capitalize(method);
            }
            if (elem.fromIndex) {
                final Integer index = Integer.valueOf(method);
                args.add(0, index);
                method = ((args.size() == 1) ? "get" : "set");
            }
            if (elem.fromField) {
                final Field f = ((Class)elem.target).getField(method);
                return new Expression(f, "get", new Object[] { null }).getValue();
            }
            if (elem.fromOwner) {
                return XMLDecoder.this.owner;
            }
            if (elem.target == XMLDecoder.this.owner) {
                if ("getOwner".equals(method)) {
                    return XMLDecoder.this.owner;
                }
                final Class<?>[] c = new Class[args.size()];
                for (int i = 0; i < args.size(); ++i) {
                    final Object arg = args.get(i);
                    c[i] = ((arg == null) ? null : arg.getClass());
                }
                try {
                    final Method m = XMLDecoder.this.owner.getClass().getMethod(method, c);
                    return m.invoke(XMLDecoder.this.owner, args.toArray());
                }
                catch (final NoSuchMethodException e) {
                    final Method mostSpecificMethod = this.findMethod((XMLDecoder.this.owner instanceof Class) ? ((Class)XMLDecoder.this.owner) : XMLDecoder.this.owner.getClass(), method, c);
                    return mostSpecificMethod.invoke(XMLDecoder.this.owner, args.toArray());
                }
            }
            final Expression exp = new Expression(elem.target, method, args.toArray());
            return exp.getValue();
        }
        
        private Method findMethod(final Class<?> clazz, final String methodName, final Class<?>[] clazzes) throws Exception {
            final Method[] methods = clazz.getMethods();
            final ArrayList<Method> matchMethods = new ArrayList<Method>();
            for (final Method method : methods) {
                if (methodName.equals(method.getName())) {
                    final Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length == clazzes.length) {
                        boolean match = true;
                        for (int i = 0; i < parameterTypes.length; ++i) {
                            final boolean isNull = clazzes[i] == null;
                            final boolean isPrimitive = this.isPrimitiveWrapper(clazzes[i], parameterTypes[i]);
                            final boolean isAssignable = !isNull && parameterTypes[i].isAssignableFrom(clazzes[i]);
                            if (!isNull && !isPrimitive) {
                                if (!isAssignable) {
                                    match = false;
                                }
                            }
                        }
                        if (match) {
                            matchMethods.add(method);
                        }
                    }
                }
            }
            final int size = matchMethods.size();
            if (size == 1) {
                return matchMethods.get(0);
            }
            if (size == 0) {
                throw new NoSuchMethodException(Messages.getString("custom.beans.41", methodName));
            }
            final Statement.MethodComparator comparator = new Statement.MethodComparator(methodName, clazzes);
            Method chosenOne = matchMethods.get(0);
            matchMethods.remove(0);
            int methodCounter = 1;
            for (final Method method2 : matchMethods) {
                final int difference = comparator.compare(chosenOne, method2);
                if (difference > 0) {
                    chosenOne = method2;
                    methodCounter = 1;
                }
                else {
                    if (difference != 0) {
                        continue;
                    }
                    ++methodCounter;
                }
            }
            if (methodCounter > 1) {
                throw new NoSuchMethodException(Messages.getString("custom.beans.62", methodName));
            }
            return chosenOne;
        }
        
        private boolean isPrimitiveWrapper(final Class<?> wrapper, final Class<?> base) {
            return (base == Boolean.TYPE && wrapper == Boolean.class) || (base == Byte.TYPE && wrapper == Byte.class) || (base == Character.TYPE && wrapper == Character.class) || (base == Short.TYPE && wrapper == Short.class) || (base == Integer.TYPE && wrapper == Integer.class) || (base == Long.TYPE && wrapper == Long.class) || (base == Float.TYPE && wrapper == Float.class) || (base == Double.TYPE && wrapper == Double.class);
        }
        
        private String capitalize(final String str) {
            final StringBuilder buf = new StringBuilder(str);
            buf.setCharAt(0, Character.toUpperCase(buf.charAt(0)));
            return buf.toString();
        }
        
        private Object executeBasic(final Elem elem) throws Exception {
            final String tag = (String)elem.target;
            final String value = elem.methodName;
            if ("null".equals(tag)) {
                return null;
            }
            if ("string".equals(tag)) {
                return (value == null) ? "" : value;
            }
            if ("class".equals(tag)) {
                return this.classForName(value);
            }
            if ("boolean".equals(tag)) {
                return Boolean.valueOf(value);
            }
            if ("byte".equals(tag)) {
                return Byte.valueOf(value);
            }
            if ("char".equals(tag)) {
                if (value == null && elem.attributes != null) {
                    final String codeAttr = elem.attributes.getValue("code");
                    if (codeAttr != null) {
                        final Character character = new Character((char)(int)Integer.valueOf(codeAttr.substring(1), 16));
                        elem.methodName = character.toString();
                        return character;
                    }
                }
                return value.charAt(0);
            }
            if ("double".equals(tag)) {
                return Double.valueOf(value);
            }
            if ("float".equals(tag)) {
                return Float.valueOf(value);
            }
            if ("int".equals(tag)) {
                return Integer.valueOf(value);
            }
            if ("long".equals(tag)) {
                return Long.valueOf(value);
            }
            if ("short".equals(tag)) {
                return Short.valueOf(value);
            }
            throw new Exception(Messages.getString("custom.beans.71", tag));
        }
        
        @Override
        public void error(final SAXParseException e) throws SAXException {
            XMLDecoder.this.listener.exceptionThrown(e);
        }
        
        @Override
        public void fatalError(final SAXParseException e) throws SAXException {
            XMLDecoder.this.listener.exceptionThrown(e);
        }
        
        @Override
        public void warning(final SAXParseException e) throws SAXException {
            XMLDecoder.this.listener.exceptionThrown(e);
        }
    }
    
    private static class Elem
    {
        String id;
        String idref;
        boolean isExecuted;
        boolean isExpression;
        boolean isBasicType;
        boolean isClosed;
        Object target;
        String methodName;
        boolean fromProperty;
        boolean fromIndex;
        boolean fromField;
        boolean fromOwner;
        Attributes attributes;
        Object result;
    }
}
