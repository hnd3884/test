package org.apache.xmlbeans.impl.jam.xml;

import org.apache.xmlbeans.impl.jam.JAnnotationValue;
import org.apache.xmlbeans.impl.jam.JSourcePosition;
import org.apache.xmlbeans.impl.jam.JComment;
import org.apache.xmlbeans.impl.jam.JAnnotation;
import org.apache.xmlbeans.impl.jam.JParameter;
import org.apache.xmlbeans.impl.jam.JInvokable;
import org.apache.xmlbeans.impl.jam.JMethod;
import org.apache.xmlbeans.impl.jam.JConstructor;
import org.apache.xmlbeans.impl.jam.JField;
import org.apache.xmlbeans.impl.jam.JAnnotatedElement;
import org.apache.xmlbeans.impl.jam.JClass;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLOutputFactory;
import java.io.Writer;
import javax.xml.stream.XMLStreamWriter;

class JamXmlWriter implements JamXmlElements
{
    private XMLStreamWriter mOut;
    private boolean mInBody;
    private boolean mWriteSourceURI;
    
    public JamXmlWriter(final Writer out) throws XMLStreamException {
        this.mInBody = false;
        this.mWriteSourceURI = false;
        if (out == null) {
            throw new IllegalArgumentException("null out");
        }
        this.mOut = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
    }
    
    public JamXmlWriter(final XMLStreamWriter out) {
        this.mInBody = false;
        this.mWriteSourceURI = false;
        if (out == null) {
            throw new IllegalArgumentException("null out");
        }
        this.mOut = out;
    }
    
    public void begin() throws XMLStreamException {
        if (this.mInBody) {
            throw new XMLStreamException("begin() already called");
        }
        this.mOut.writeStartElement("jam-service");
        this.mInBody = true;
    }
    
    public void end() throws XMLStreamException {
        if (!this.mInBody) {
            throw new XMLStreamException("begin() never called");
        }
        this.mOut.writeEndElement();
        this.mInBody = false;
    }
    
    public void write(final JClass clazz) throws XMLStreamException {
        this.assertStarted();
        this.mOut.writeStartElement("class");
        this.writeValueElement("name", clazz.getFieldDescriptor());
        this.writeValueElement("is-interface", clazz.isInterface());
        this.writeModifiers(clazz.getModifiers());
        final JClass sc = clazz.getSuperclass();
        if (sc != null) {
            this.writeValueElement("superclass", sc.getFieldDescriptor());
        }
        this.writeClassList("interface", clazz.getInterfaces());
        final JField[] f = clazz.getDeclaredFields();
        for (int i = 0; i < f.length; ++i) {
            this.write(f[i]);
        }
        final JConstructor[] c = clazz.getConstructors();
        for (int i = 0; i < c.length; ++i) {
            this.write(c[i]);
        }
        final JMethod[] m = clazz.getDeclaredMethods();
        for (int i = 0; i < m.length; ++i) {
            this.write(m[i]);
        }
        this.writeAnnotatedElement(clazz);
        this.mOut.writeEndElement();
    }
    
    private void write(final JMethod method) throws XMLStreamException {
        this.mOut.writeStartElement("method");
        this.writeValueElement("name", method.getSimpleName());
        this.writeValueElement("return-type", method.getReturnType().getFieldDescriptor());
        this.writeInvokable(method);
        this.mOut.writeEndElement();
    }
    
    private void write(final JConstructor ctor) throws XMLStreamException {
        this.mOut.writeStartElement("constructor");
        this.writeInvokable(ctor);
        this.mOut.writeEndElement();
    }
    
    private void write(final JField field) throws XMLStreamException {
        this.mOut.writeStartElement("field");
        this.writeValueElement("name", field.getSimpleName());
        this.writeModifiers(field.getModifiers());
        this.writeValueElement("type", field.getType().getFieldDescriptor());
        this.writeAnnotatedElement(field);
        this.mOut.writeEndElement();
    }
    
    private void writeInvokable(final JInvokable ji) throws XMLStreamException {
        this.writeModifiers(ji.getModifiers());
        final JParameter[] params = ji.getParameters();
        for (int i = 0; i < params.length; ++i) {
            this.mOut.writeStartElement("parameter");
            this.writeValueElement("name", params[i].getSimpleName());
            this.writeValueElement("type", params[i].getType().getFieldDescriptor());
            this.writeAnnotatedElement(params[i]);
            this.mOut.writeEndElement();
        }
        this.writeAnnotatedElement(ji);
    }
    
    private void writeClassList(final String elementName, final JClass[] clazzes) throws XMLStreamException {
        for (int i = 0; i < clazzes.length; ++i) {
            this.mOut.writeStartElement(elementName);
            this.mOut.writeCharacters(clazzes[i].getFieldDescriptor());
            this.mOut.writeEndElement();
        }
    }
    
    private void writeModifiers(final int mods) throws XMLStreamException {
        this.mOut.writeStartElement("modifiers");
        this.mOut.writeCharacters(String.valueOf(mods));
        this.mOut.writeEndElement();
    }
    
    private void writeValueElement(final String elementName, final boolean b) throws XMLStreamException {
        this.mOut.writeStartElement(elementName);
        this.mOut.writeCharacters(String.valueOf(b));
        this.mOut.writeEndElement();
    }
    
    private void writeValueElement(final String elementName, final int x) throws XMLStreamException {
        this.mOut.writeStartElement(elementName);
        this.mOut.writeCharacters(String.valueOf(x));
        this.mOut.writeEndElement();
    }
    
    private void writeValueElement(final String elementName, final String val) throws XMLStreamException {
        this.mOut.writeStartElement(elementName);
        this.mOut.writeCharacters(val);
        this.mOut.writeEndElement();
    }
    
    private void writeValueElement(final String elementName, final String[] vals) throws XMLStreamException {
        for (int i = 0; i < vals.length; ++i) {
            this.writeValueElement(elementName, vals[i]);
        }
    }
    
    private void writeAnnotatedElement(final JAnnotatedElement ae) throws XMLStreamException {
        final JAnnotation[] anns = ae.getAnnotations();
        for (int i = 0; i < anns.length; ++i) {
            this.writeAnnotation(anns[i]);
        }
        final JComment jc = ae.getComment();
        if (jc != null) {
            String text = jc.getText();
            if (text != null) {
                text = text.trim();
                if (text.length() > 0) {
                    this.mOut.writeStartElement("comment");
                    this.mOut.writeCData(jc.getText());
                    this.mOut.writeEndElement();
                }
            }
        }
        final JSourcePosition pos = ae.getSourcePosition();
        if (pos != null) {
            this.mOut.writeStartElement("source-position");
            if (pos.getLine() != -1) {
                this.writeValueElement("line", pos.getLine());
            }
            if (pos.getColumn() != -1) {
                this.writeValueElement("column", pos.getColumn());
            }
            if (this.mWriteSourceURI && pos.getSourceURI() != null) {
                this.writeValueElement("source-uri", pos.getSourceURI().toString());
            }
            this.mOut.writeEndElement();
        }
    }
    
    private void writeAnnotation(final JAnnotation ann) throws XMLStreamException {
        this.mOut.writeStartElement("annotation");
        this.writeValueElement("name", ann.getQualifiedName());
        final JAnnotationValue[] values = ann.getValues();
        for (int i = 0; i < values.length; ++i) {
            this.writeAnnotationValue(values[i]);
        }
        this.mOut.writeEndElement();
    }
    
    private void writeAnnotationValue(final JAnnotationValue val) throws XMLStreamException {
        this.mOut.writeStartElement("annotation-value");
        this.writeValueElement("name", val.getName());
        this.writeValueElement("type", val.getType().getFieldDescriptor());
        if (val.getType().isArrayType()) {
            this.writeValueElement("value", val.asStringArray());
        }
        else {
            this.writeValueElement("value", val.asString());
        }
        this.mOut.writeEndElement();
    }
    
    private void assertStarted() throws XMLStreamException {
        if (!this.mInBody) {
            throw new XMLStreamException("begin() not called");
        }
    }
}
