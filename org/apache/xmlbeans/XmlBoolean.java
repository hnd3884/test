package org.apache.xmlbeans;

import javax.xml.stream.XMLStreamReader;
import org.apache.xmlbeans.xml.stream.XMLStreamException;
import org.apache.xmlbeans.xml.stream.XMLInputStream;
import org.w3c.dom.Node;
import java.io.Reader;
import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import java.io.File;

public interface XmlBoolean extends XmlAnySimpleType
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_boolean");
    
    @Deprecated
    boolean booleanValue();
    
    @Deprecated
    void set(final boolean p0);
    
    boolean getBooleanValue();
    
    void setBooleanValue(final boolean p0);
    
    public static final class Factory
    {
        public static XmlBoolean newInstance() {
            return (XmlBoolean)XmlBeans.getContextTypeLoader().newInstance(XmlBoolean.type, null);
        }
        
        public static XmlBoolean newInstance(final XmlOptions options) {
            return (XmlBoolean)XmlBeans.getContextTypeLoader().newInstance(XmlBoolean.type, options);
        }
        
        public static XmlBoolean newValue(final Object obj) {
            return (XmlBoolean)XmlBoolean.type.newValue(obj);
        }
        
        public static XmlBoolean parse(final String s) throws XmlException {
            return (XmlBoolean)XmlBeans.getContextTypeLoader().parse(s, XmlBoolean.type, null);
        }
        
        public static XmlBoolean parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlBoolean)XmlBeans.getContextTypeLoader().parse(s, XmlBoolean.type, options);
        }
        
        public static XmlBoolean parse(final File f) throws XmlException, IOException {
            return (XmlBoolean)XmlBeans.getContextTypeLoader().parse(f, XmlBoolean.type, null);
        }
        
        public static XmlBoolean parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlBoolean)XmlBeans.getContextTypeLoader().parse(f, XmlBoolean.type, options);
        }
        
        public static XmlBoolean parse(final URL u) throws XmlException, IOException {
            return (XmlBoolean)XmlBeans.getContextTypeLoader().parse(u, XmlBoolean.type, null);
        }
        
        public static XmlBoolean parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlBoolean)XmlBeans.getContextTypeLoader().parse(u, XmlBoolean.type, options);
        }
        
        public static XmlBoolean parse(final InputStream is) throws XmlException, IOException {
            return (XmlBoolean)XmlBeans.getContextTypeLoader().parse(is, XmlBoolean.type, null);
        }
        
        public static XmlBoolean parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlBoolean)XmlBeans.getContextTypeLoader().parse(is, XmlBoolean.type, options);
        }
        
        public static XmlBoolean parse(final Reader r) throws XmlException, IOException {
            return (XmlBoolean)XmlBeans.getContextTypeLoader().parse(r, XmlBoolean.type, null);
        }
        
        public static XmlBoolean parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlBoolean)XmlBeans.getContextTypeLoader().parse(r, XmlBoolean.type, options);
        }
        
        public static XmlBoolean parse(final Node node) throws XmlException {
            return (XmlBoolean)XmlBeans.getContextTypeLoader().parse(node, XmlBoolean.type, null);
        }
        
        public static XmlBoolean parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlBoolean)XmlBeans.getContextTypeLoader().parse(node, XmlBoolean.type, options);
        }
        
        @Deprecated
        public static XmlBoolean parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlBoolean)XmlBeans.getContextTypeLoader().parse(xis, XmlBoolean.type, null);
        }
        
        @Deprecated
        public static XmlBoolean parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlBoolean)XmlBeans.getContextTypeLoader().parse(xis, XmlBoolean.type, options);
        }
        
        public static XmlBoolean parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlBoolean)XmlBeans.getContextTypeLoader().parse(xsr, XmlBoolean.type, null);
        }
        
        public static XmlBoolean parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlBoolean)XmlBeans.getContextTypeLoader().parse(xsr, XmlBoolean.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlBoolean.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlBoolean.type, options);
        }
        
        private Factory() {
        }
    }
}
