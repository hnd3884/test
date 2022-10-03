package org.apache.xmlbeans.impl.jam.xml;

import java.io.StringWriter;
import java.util.Collection;
import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.impl.jam.mutable.MAnnotation;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.jam.mutable.MParameter;
import org.apache.xmlbeans.impl.jam.mutable.MSourcePosition;
import java.net.URISyntaxException;
import java.net.URI;
import org.apache.xmlbeans.impl.jam.mutable.MMethod;
import org.apache.xmlbeans.impl.jam.mutable.MConstructor;
import org.apache.xmlbeans.impl.jam.mutable.MInvokable;
import org.apache.xmlbeans.impl.jam.mutable.MField;
import org.apache.xmlbeans.impl.jam.mutable.MClass;
import org.apache.xmlbeans.impl.jam.internal.elements.ClassImpl;
import org.apache.xmlbeans.impl.jam.mutable.MAnnotatedElement;
import java.io.Reader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLInputFactory;
import java.io.InputStream;
import org.apache.xmlbeans.impl.jam.internal.elements.ElementContext;
import org.apache.xmlbeans.impl.jam.internal.CachedClassBuilder;
import javax.xml.stream.XMLStreamReader;

class JamXmlReader implements JamXmlElements
{
    private XMLStreamReader mIn;
    private CachedClassBuilder mCache;
    private ElementContext mContext;
    
    public JamXmlReader(final CachedClassBuilder cache, final InputStream in, final ElementContext ctx) throws XMLStreamException {
        this(cache, XMLInputFactory.newInstance().createXMLStreamReader(in), ctx);
    }
    
    public JamXmlReader(final CachedClassBuilder cache, final Reader in, final ElementContext ctx) throws XMLStreamException {
        this(cache, XMLInputFactory.newInstance().createXMLStreamReader(in), ctx);
    }
    
    public JamXmlReader(final CachedClassBuilder cache, final XMLStreamReader in, final ElementContext ctx) {
        if (cache == null) {
            throw new IllegalArgumentException("null cache");
        }
        if (in == null) {
            throw new IllegalArgumentException("null cache");
        }
        if (ctx == null) {
            throw new IllegalArgumentException("null ctx");
        }
        this.mIn = in;
        this.mCache = cache;
        this.mContext = ctx;
    }
    
    public void read() throws XMLStreamException {
        this.nextElement();
        this.assertStart("jam-service");
        this.nextElement();
        while ("class".equals(this.getElementName())) {
            this.readClass();
        }
        this.assertEnd("jam-service");
    }
    
    private void readClass() throws XMLStreamException {
        this.assertStart("class");
        this.nextElement();
        String clazzName = this.assertCurrentString("name");
        final int dot = clazzName.lastIndexOf(46);
        String pkgName = "";
        if (dot != -1) {
            pkgName = clazzName.substring(0, dot);
            clazzName = clazzName.substring(dot + 1);
        }
        final MClass clazz = this.mCache.createClassToBuild(pkgName, clazzName, null);
        clazz.setIsInterface(this.assertCurrentBoolean("is-interface"));
        clazz.setModifiers(this.assertCurrentInt("modifiers"));
        String supername = this.checkCurrentString("superclass");
        if (supername != null) {
            clazz.setSuperclass(supername);
        }
        while ((supername = this.checkCurrentString("interface")) != null) {
            clazz.addInterface(supername);
        }
        while ("field".equals(this.getElementName())) {
            this.readField(clazz);
        }
        while ("constructor".equals(this.getElementName())) {
            this.readConstructor(clazz);
        }
        while ("method".equals(this.getElementName())) {
            this.readMethod(clazz);
        }
        this.readAnnotatedElement(clazz);
        this.assertEnd("class");
        ((ClassImpl)clazz).setState(6);
        this.nextElement();
    }
    
    private void readField(final MClass clazz) throws XMLStreamException {
        this.assertStart("field");
        final MField field = clazz.addNewField();
        this.nextElement();
        field.setSimpleName(this.assertCurrentString("name"));
        field.setModifiers(this.assertCurrentInt("modifiers"));
        field.setType(this.assertCurrentString("type"));
        this.readAnnotatedElement(field);
        this.assertEnd("field");
        this.nextElement();
    }
    
    private void readConstructor(final MClass clazz) throws XMLStreamException {
        this.assertStart("constructor");
        final MConstructor ctor = clazz.addNewConstructor();
        this.nextElement();
        this.readInvokableContents(ctor);
        this.assertEnd("constructor");
        this.nextElement();
    }
    
    private void readMethod(final MClass clazz) throws XMLStreamException {
        this.assertStart("method");
        final MMethod method = clazz.addNewMethod();
        this.nextElement();
        method.setSimpleName(this.assertCurrentString("name"));
        method.setReturnType(this.assertCurrentString("return-type"));
        this.readInvokableContents(method);
        this.assertEnd("method");
        this.nextElement();
    }
    
    private void readSourcePosition(final MAnnotatedElement element) throws XMLStreamException {
        this.assertStart("source-position");
        final MSourcePosition pos = element.createSourcePosition();
        this.nextElement();
        if ("line".equals(this.getElementName())) {
            pos.setLine(this.assertCurrentInt("line"));
        }
        if ("column".equals(this.getElementName())) {
            pos.setColumn(this.assertCurrentInt("column"));
        }
        if ("source-uri".equals(this.getElementName())) {
            try {
                pos.setSourceURI(new URI(this.assertCurrentString("source-uri")));
            }
            catch (final URISyntaxException use) {
                throw new XMLStreamException(use);
            }
        }
        this.assertEnd("source-position");
        this.nextElement();
    }
    
    private void readInvokableContents(final MInvokable out) throws XMLStreamException {
        out.setModifiers(this.assertCurrentInt("modifiers"));
        while ("parameter".equals(this.getElementName())) {
            this.nextElement();
            final MParameter param = out.addNewParameter();
            param.setSimpleName(this.assertCurrentString("name"));
            param.setType(this.assertCurrentString("type"));
            this.readAnnotatedElement(param);
            this.assertEnd("parameter");
            this.nextElement();
        }
        this.readAnnotatedElement(out);
    }
    
    private void readAnnotatedElement(final MAnnotatedElement element) throws XMLStreamException {
        while ("annotation".equals(this.getElementName())) {
            this.nextElement();
            final MAnnotation ann = element.addLiteralAnnotation(this.assertCurrentString("name"));
            while ("annotation-value".equals(this.getElementName())) {
                this.nextElement();
                final String name = this.assertCurrentString("name");
                final String type = this.assertCurrentString("type");
                final JClass jclass = this.mContext.getClassLoader().loadClass(type);
                if (jclass.isArrayType()) {
                    final Collection list = new ArrayList();
                    while ("value".equals(this.getElementName())) {
                        final String value = this.assertCurrentString("value");
                        list.add(value);
                    }
                    final String[] vals = new String[list.size()];
                    list.toArray(vals);
                    ann.setSimpleValue(name, vals, jclass);
                }
                else {
                    final String value2 = this.assertCurrentString("value");
                    ann.setSimpleValue(name, value2, jclass);
                }
                this.assertEnd("annotation-value");
                this.nextElement();
            }
            this.assertEnd("annotation");
            this.nextElement();
        }
        if ("comment".equals(this.getElementName())) {
            element.createComment().setText(this.mIn.getElementText());
            this.assertEnd("comment");
            this.nextElement();
        }
        if ("source-position".equals(this.getElementName())) {
            this.readSourcePosition(element);
        }
    }
    
    private void assertStart(final String named) throws XMLStreamException {
        if (!this.mIn.isStartElement() || !named.equals(this.getElementName())) {
            this.error("expected to get a <" + named + ">, ");
        }
    }
    
    private void assertEnd(final String named) throws XMLStreamException {
        if (!this.mIn.isEndElement() || !named.equals(this.getElementName())) {
            this.error("expected to get a </" + named + ">, ");
        }
    }
    
    private String checkCurrentString(final String named) throws XMLStreamException {
        if (named.equals(this.getElementName())) {
            final String val = this.mIn.getElementText();
            this.assertEnd(named);
            this.nextElement();
            return val;
        }
        return null;
    }
    
    private String assertCurrentString(final String named) throws XMLStreamException {
        this.assertStart(named);
        final String val = this.mIn.getElementText();
        this.assertEnd(named);
        this.nextElement();
        return val;
    }
    
    private int assertCurrentInt(final String named) throws XMLStreamException {
        this.assertStart(named);
        final String val = this.mIn.getElementText();
        this.assertEnd(named);
        this.nextElement();
        return Integer.valueOf(val);
    }
    
    private boolean assertCurrentBoolean(final String named) throws XMLStreamException {
        this.assertStart(named);
        final String val = this.mIn.getElementText();
        this.assertEnd(named);
        this.nextElement();
        return Boolean.valueOf(val);
    }
    
    private void error(final String message) throws XMLStreamException {
        final StringWriter out = new StringWriter();
        out.write("<");
        out.write(this.mIn.getLocalName());
        out.write("> line:");
        out.write("" + this.mIn.getLocation().getLineNumber());
        out.write(" col:");
        out.write("" + this.mIn.getLocation().getColumnNumber());
        out.write("]");
        throw new XMLStreamException(message + ":\n " + out.toString());
    }
    
    private void nextElement() throws XMLStreamException {
        while (this.mIn.next() != -1) {
            if (this.mIn.isEndElement() || this.mIn.isStartElement()) {
                return;
            }
        }
        throw new XMLStreamException("Unexpected end of file");
    }
    
    private String getElementName() {
        return this.mIn.getLocalName();
    }
}
