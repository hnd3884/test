package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.xml.stream.XMLStreamException;
import org.apache.xmlbeans.xml.stream.XMLInputStream;
import org.w3c.dom.Node;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import java.io.File;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.SchemaType;

public interface Keybase extends Annotated
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Keybase.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("keybase3955type");
    
    SelectorDocument.Selector getSelector();
    
    void setSelector(final SelectorDocument.Selector p0);
    
    SelectorDocument.Selector addNewSelector();
    
    FieldDocument.Field[] getFieldArray();
    
    FieldDocument.Field getFieldArray(final int p0);
    
    int sizeOfFieldArray();
    
    void setFieldArray(final FieldDocument.Field[] p0);
    
    void setFieldArray(final int p0, final FieldDocument.Field p1);
    
    FieldDocument.Field insertNewField(final int p0);
    
    FieldDocument.Field addNewField();
    
    void removeField(final int p0);
    
    String getName();
    
    XmlNCName xgetName();
    
    void setName(final String p0);
    
    void xsetName(final XmlNCName p0);
    
    public static final class Factory
    {
        public static Keybase newInstance() {
            return (Keybase)XmlBeans.getContextTypeLoader().newInstance(Keybase.type, null);
        }
        
        public static Keybase newInstance(final XmlOptions options) {
            return (Keybase)XmlBeans.getContextTypeLoader().newInstance(Keybase.type, options);
        }
        
        public static Keybase parse(final String xmlAsString) throws XmlException {
            return (Keybase)XmlBeans.getContextTypeLoader().parse(xmlAsString, Keybase.type, null);
        }
        
        public static Keybase parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (Keybase)XmlBeans.getContextTypeLoader().parse(xmlAsString, Keybase.type, options);
        }
        
        public static Keybase parse(final File file) throws XmlException, IOException {
            return (Keybase)XmlBeans.getContextTypeLoader().parse(file, Keybase.type, null);
        }
        
        public static Keybase parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (Keybase)XmlBeans.getContextTypeLoader().parse(file, Keybase.type, options);
        }
        
        public static Keybase parse(final URL u) throws XmlException, IOException {
            return (Keybase)XmlBeans.getContextTypeLoader().parse(u, Keybase.type, null);
        }
        
        public static Keybase parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (Keybase)XmlBeans.getContextTypeLoader().parse(u, Keybase.type, options);
        }
        
        public static Keybase parse(final InputStream is) throws XmlException, IOException {
            return (Keybase)XmlBeans.getContextTypeLoader().parse(is, Keybase.type, null);
        }
        
        public static Keybase parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (Keybase)XmlBeans.getContextTypeLoader().parse(is, Keybase.type, options);
        }
        
        public static Keybase parse(final Reader r) throws XmlException, IOException {
            return (Keybase)XmlBeans.getContextTypeLoader().parse(r, Keybase.type, null);
        }
        
        public static Keybase parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (Keybase)XmlBeans.getContextTypeLoader().parse(r, Keybase.type, options);
        }
        
        public static Keybase parse(final XMLStreamReader sr) throws XmlException {
            return (Keybase)XmlBeans.getContextTypeLoader().parse(sr, Keybase.type, null);
        }
        
        public static Keybase parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (Keybase)XmlBeans.getContextTypeLoader().parse(sr, Keybase.type, options);
        }
        
        public static Keybase parse(final Node node) throws XmlException {
            return (Keybase)XmlBeans.getContextTypeLoader().parse(node, Keybase.type, null);
        }
        
        public static Keybase parse(final Node node, final XmlOptions options) throws XmlException {
            return (Keybase)XmlBeans.getContextTypeLoader().parse(node, Keybase.type, options);
        }
        
        @Deprecated
        public static Keybase parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (Keybase)XmlBeans.getContextTypeLoader().parse(xis, Keybase.type, null);
        }
        
        @Deprecated
        public static Keybase parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (Keybase)XmlBeans.getContextTypeLoader().parse(xis, Keybase.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Keybase.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Keybase.type, options);
        }
        
        private Factory() {
        }
    }
}
