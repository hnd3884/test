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
import java.util.Calendar;

public interface XmlGYear extends XmlAnySimpleType
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_gYear");
    
    Calendar getCalendarValue();
    
    void setCalendarValue(final Calendar p0);
    
    GDate getGDateValue();
    
    void setGDateValue(final GDate p0);
    
    int getIntValue();
    
    void setIntValue(final int p0);
    
    @Deprecated
    Calendar calendarValue();
    
    @Deprecated
    void set(final Calendar p0);
    
    @Deprecated
    GDate gDateValue();
    
    @Deprecated
    void set(final GDateSpecification p0);
    
    @Deprecated
    int intValue();
    
    @Deprecated
    void set(final int p0);
    
    public static final class Factory
    {
        public static XmlGYear newInstance() {
            return (XmlGYear)XmlBeans.getContextTypeLoader().newInstance(XmlGYear.type, null);
        }
        
        public static XmlGYear newInstance(final XmlOptions options) {
            return (XmlGYear)XmlBeans.getContextTypeLoader().newInstance(XmlGYear.type, options);
        }
        
        public static XmlGYear newValue(final Object obj) {
            return (XmlGYear)XmlGYear.type.newValue(obj);
        }
        
        public static XmlGYear parse(final String s) throws XmlException {
            return (XmlGYear)XmlBeans.getContextTypeLoader().parse(s, XmlGYear.type, null);
        }
        
        public static XmlGYear parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlGYear)XmlBeans.getContextTypeLoader().parse(s, XmlGYear.type, options);
        }
        
        public static XmlGYear parse(final File f) throws XmlException, IOException {
            return (XmlGYear)XmlBeans.getContextTypeLoader().parse(f, XmlGYear.type, null);
        }
        
        public static XmlGYear parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlGYear)XmlBeans.getContextTypeLoader().parse(f, XmlGYear.type, options);
        }
        
        public static XmlGYear parse(final URL u) throws XmlException, IOException {
            return (XmlGYear)XmlBeans.getContextTypeLoader().parse(u, XmlGYear.type, null);
        }
        
        public static XmlGYear parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlGYear)XmlBeans.getContextTypeLoader().parse(u, XmlGYear.type, options);
        }
        
        public static XmlGYear parse(final InputStream is) throws XmlException, IOException {
            return (XmlGYear)XmlBeans.getContextTypeLoader().parse(is, XmlGYear.type, null);
        }
        
        public static XmlGYear parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlGYear)XmlBeans.getContextTypeLoader().parse(is, XmlGYear.type, options);
        }
        
        public static XmlGYear parse(final Reader r) throws XmlException, IOException {
            return (XmlGYear)XmlBeans.getContextTypeLoader().parse(r, XmlGYear.type, null);
        }
        
        public static XmlGYear parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlGYear)XmlBeans.getContextTypeLoader().parse(r, XmlGYear.type, options);
        }
        
        public static XmlGYear parse(final Node node) throws XmlException {
            return (XmlGYear)XmlBeans.getContextTypeLoader().parse(node, XmlGYear.type, null);
        }
        
        public static XmlGYear parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlGYear)XmlBeans.getContextTypeLoader().parse(node, XmlGYear.type, options);
        }
        
        @Deprecated
        public static XmlGYear parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlGYear)XmlBeans.getContextTypeLoader().parse(xis, XmlGYear.type, null);
        }
        
        @Deprecated
        public static XmlGYear parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlGYear)XmlBeans.getContextTypeLoader().parse(xis, XmlGYear.type, options);
        }
        
        public static XmlGYear parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlGYear)XmlBeans.getContextTypeLoader().parse(xsr, XmlGYear.type, null);
        }
        
        public static XmlGYear parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlGYear)XmlBeans.getContextTypeLoader().parse(xsr, XmlGYear.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlGYear.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlGYear.type, options);
        }
        
        private Factory() {
        }
    }
}
