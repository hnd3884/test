package com.sun.beans.decoder;

import com.sun.beans.finder.ClassFinder;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.security.PrivilegedAction;
import sun.misc.SharedSecrets;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import java.io.Reader;
import java.io.StringReader;
import org.xml.sax.InputSource;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.security.AccessController;
import java.beans.ExceptionListener;
import java.lang.ref.Reference;
import java.util.List;
import java.util.Map;
import java.security.AccessControlContext;
import org.xml.sax.helpers.DefaultHandler;

public final class DocumentHandler extends DefaultHandler
{
    private final AccessControlContext acc;
    private final Map<String, Class<? extends ElementHandler>> handlers;
    private final Map<String, Object> environment;
    private final List<Object> objects;
    private Reference<ClassLoader> loader;
    private ExceptionListener listener;
    private Object owner;
    private ElementHandler handler;
    
    public DocumentHandler() {
        this.acc = AccessController.getContext();
        this.handlers = new HashMap<String, Class<? extends ElementHandler>>();
        this.environment = new HashMap<String, Object>();
        this.objects = new ArrayList<Object>();
        this.setElementHandler("java", JavaElementHandler.class);
        this.setElementHandler("null", NullElementHandler.class);
        this.setElementHandler("array", ArrayElementHandler.class);
        this.setElementHandler("class", ClassElementHandler.class);
        this.setElementHandler("string", StringElementHandler.class);
        this.setElementHandler("object", ObjectElementHandler.class);
        this.setElementHandler("void", VoidElementHandler.class);
        this.setElementHandler("char", CharElementHandler.class);
        this.setElementHandler("byte", ByteElementHandler.class);
        this.setElementHandler("short", ShortElementHandler.class);
        this.setElementHandler("int", IntElementHandler.class);
        this.setElementHandler("long", LongElementHandler.class);
        this.setElementHandler("float", FloatElementHandler.class);
        this.setElementHandler("double", DoubleElementHandler.class);
        this.setElementHandler("boolean", BooleanElementHandler.class);
        this.setElementHandler("new", NewElementHandler.class);
        this.setElementHandler("var", VarElementHandler.class);
        this.setElementHandler("true", TrueElementHandler.class);
        this.setElementHandler("false", FalseElementHandler.class);
        this.setElementHandler("field", FieldElementHandler.class);
        this.setElementHandler("method", MethodElementHandler.class);
        this.setElementHandler("property", PropertyElementHandler.class);
    }
    
    public ClassLoader getClassLoader() {
        return (this.loader != null) ? this.loader.get() : null;
    }
    
    public void setClassLoader(final ClassLoader classLoader) {
        this.loader = new WeakReference<ClassLoader>(classLoader);
    }
    
    public ExceptionListener getExceptionListener() {
        return this.listener;
    }
    
    public void setExceptionListener(final ExceptionListener listener) {
        this.listener = listener;
    }
    
    public Object getOwner() {
        return this.owner;
    }
    
    public void setOwner(final Object owner) {
        this.owner = owner;
    }
    
    public Class<? extends ElementHandler> getElementHandler(final String s) {
        final Class clazz = this.handlers.get(s);
        if (clazz == null) {
            throw new IllegalArgumentException("Unsupported element: " + s);
        }
        return clazz;
    }
    
    public void setElementHandler(final String s, final Class<? extends ElementHandler> clazz) {
        this.handlers.put(s, clazz);
    }
    
    public boolean hasVariable(final String s) {
        return this.environment.containsKey(s);
    }
    
    public Object getVariable(final String s) {
        if (!this.environment.containsKey(s)) {
            throw new IllegalArgumentException("Unbound variable: " + s);
        }
        return this.environment.get(s);
    }
    
    public void setVariable(final String s, final Object o) {
        this.environment.put(s, o);
    }
    
    public Object[] getObjects() {
        return this.objects.toArray();
    }
    
    void addObject(final Object o) {
        this.objects.add(o);
    }
    
    @Override
    public InputSource resolveEntity(final String s, final String s2) {
        return new InputSource(new StringReader(""));
    }
    
    @Override
    public void startDocument() {
        this.objects.clear();
        this.handler = null;
    }
    
    @Override
    public void startElement(final String s, final String s2, final String s3, final Attributes attributes) throws SAXException {
        final ElementHandler handler = this.handler;
        try {
            (this.handler = (ElementHandler)this.getElementHandler(s3).newInstance()).setOwner(this);
            this.handler.setParent(handler);
        }
        catch (final Exception e) {
            throw new SAXException(e);
        }
        for (int i = 0; i < attributes.getLength(); ++i) {
            try {
                this.handler.addAttribute(attributes.getQName(i), attributes.getValue(i));
            }
            catch (final RuntimeException ex) {
                this.handleException(ex);
            }
        }
        this.handler.startElement();
    }
    
    @Override
    public void endElement(final String s, final String s2, final String s3) {
        try {
            this.handler.endElement();
        }
        catch (final RuntimeException ex) {
            this.handleException(ex);
        }
        finally {
            this.handler = this.handler.getParent();
        }
    }
    
    @Override
    public void characters(final char[] array, int n, int n2) {
        if (this.handler != null) {
            try {
                while (0 < n2--) {
                    this.handler.addCharacter(array[n++]);
                }
            }
            catch (final RuntimeException ex) {
                this.handleException(ex);
            }
        }
    }
    
    public void handleException(final Exception ex) {
        if (this.listener == null) {
            throw new IllegalStateException(ex);
        }
        this.listener.exceptionThrown(ex);
    }
    
    public void parse(final InputSource inputSource) {
        if (this.acc == null && null != System.getSecurityManager()) {
            throw new SecurityException("AccessControlContext is not set");
        }
        SharedSecrets.getJavaSecurityAccess().doIntersectionPrivilege((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                try {
                    SAXParserFactory.newInstance().newSAXParser().parse(inputSource, DocumentHandler.this);
                }
                catch (final ParserConfigurationException ex) {
                    DocumentHandler.this.handleException(ex);
                }
                catch (final SAXException ex2) {
                    Exception exception = ex2.getException();
                    if (exception == null) {
                        exception = ex2;
                    }
                    DocumentHandler.this.handleException(exception);
                }
                catch (final IOException ex3) {
                    DocumentHandler.this.handleException(ex3);
                }
                return null;
            }
        }, AccessController.getContext(), this.acc);
    }
    
    public Class<?> findClass(final String s) {
        try {
            return ClassFinder.resolveClass(s, this.getClassLoader());
        }
        catch (final ClassNotFoundException ex) {
            this.handleException(ex);
            return null;
        }
    }
}
