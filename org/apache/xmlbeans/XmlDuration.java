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

public interface XmlDuration extends XmlAnySimpleType
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_duration");
    
    GDuration getGDurationValue();
    
    void setGDurationValue(final GDuration p0);
    
    @Deprecated
    GDuration gDurationValue();
    
    @Deprecated
    void set(final GDurationSpecification p0);
    
    public static final class Factory
    {
        public static XmlDuration newInstance() {
            return (XmlDuration)XmlBeans.getContextTypeLoader().newInstance(XmlDuration.type, null);
        }
        
        public static XmlDuration newInstance(final XmlOptions options) {
            return (XmlDuration)XmlBeans.getContextTypeLoader().newInstance(XmlDuration.type, options);
        }
        
        public static XmlDuration newValue(final Object obj) {
            return (XmlDuration)XmlDuration.type.newValue(obj);
        }
        
        public static XmlDuration parse(final String s) throws XmlException {
            return (XmlDuration)XmlBeans.getContextTypeLoader().parse(s, XmlDuration.type, null);
        }
        
        public static XmlDuration parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlDuration)XmlBeans.getContextTypeLoader().parse(s, XmlDuration.type, options);
        }
        
        public static XmlDuration parse(final File f) throws XmlException, IOException {
            return (XmlDuration)XmlBeans.getContextTypeLoader().parse(f, XmlDuration.type, null);
        }
        
        public static XmlDuration parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlDuration)XmlBeans.getContextTypeLoader().parse(f, XmlDuration.type, options);
        }
        
        public static XmlDuration parse(final URL u) throws XmlException, IOException {
            return (XmlDuration)XmlBeans.getContextTypeLoader().parse(u, XmlDuration.type, null);
        }
        
        public static XmlDuration parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlDuration)XmlBeans.getContextTypeLoader().parse(u, XmlDuration.type, options);
        }
        
        public static XmlDuration parse(final InputStream is) throws XmlException, IOException {
            return (XmlDuration)XmlBeans.getContextTypeLoader().parse(is, XmlDuration.type, null);
        }
        
        public static XmlDuration parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlDuration)XmlBeans.getContextTypeLoader().parse(is, XmlDuration.type, options);
        }
        
        public static XmlDuration parse(final Reader r) throws XmlException, IOException {
            return (XmlDuration)XmlBeans.getContextTypeLoader().parse(r, XmlDuration.type, null);
        }
        
        public static XmlDuration parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlDuration)XmlBeans.getContextTypeLoader().parse(r, XmlDuration.type, options);
        }
        
        public static XmlDuration parse(final Node node) throws XmlException {
            return (XmlDuration)XmlBeans.getContextTypeLoader().parse(node, XmlDuration.type, null);
        }
        
        public static XmlDuration parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlDuration)XmlBeans.getContextTypeLoader().parse(node, XmlDuration.type, options);
        }
        
        @Deprecated
        public static XmlDuration parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlDuration)XmlBeans.getContextTypeLoader().parse(xis, XmlDuration.type, null);
        }
        
        @Deprecated
        public static XmlDuration parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlDuration)XmlBeans.getContextTypeLoader().parse(xis, XmlDuration.type, options);
        }
        
        public static XmlDuration parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlDuration)XmlBeans.getContextTypeLoader().parse(xsr, XmlDuration.type, null);
        }
        
        public static XmlDuration parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlDuration)XmlBeans.getContextTypeLoader().parse(xsr, XmlDuration.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlDuration.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlDuration.type, options);
        }
        
        private Factory() {
        }
    }
}
