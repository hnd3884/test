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

public interface XmlGMonth extends XmlAnySimpleType
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_gMonth");
    
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
        public static XmlGMonth newInstance() {
            return (XmlGMonth)XmlBeans.getContextTypeLoader().newInstance(XmlGMonth.type, null);
        }
        
        public static XmlGMonth newInstance(final XmlOptions options) {
            return (XmlGMonth)XmlBeans.getContextTypeLoader().newInstance(XmlGMonth.type, options);
        }
        
        public static XmlGMonth newValue(final Object obj) {
            return (XmlGMonth)XmlGMonth.type.newValue(obj);
        }
        
        public static XmlGMonth parse(final String s) throws XmlException {
            return (XmlGMonth)XmlBeans.getContextTypeLoader().parse(s, XmlGMonth.type, null);
        }
        
        public static XmlGMonth parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlGMonth)XmlBeans.getContextTypeLoader().parse(s, XmlGMonth.type, options);
        }
        
        public static XmlGMonth parse(final File f) throws XmlException, IOException {
            return (XmlGMonth)XmlBeans.getContextTypeLoader().parse(f, XmlGMonth.type, null);
        }
        
        public static XmlGMonth parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlGMonth)XmlBeans.getContextTypeLoader().parse(f, XmlGMonth.type, options);
        }
        
        public static XmlGMonth parse(final URL u) throws XmlException, IOException {
            return (XmlGMonth)XmlBeans.getContextTypeLoader().parse(u, XmlGMonth.type, null);
        }
        
        public static XmlGMonth parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlGMonth)XmlBeans.getContextTypeLoader().parse(u, XmlGMonth.type, options);
        }
        
        public static XmlGMonth parse(final InputStream is) throws XmlException, IOException {
            return (XmlGMonth)XmlBeans.getContextTypeLoader().parse(is, XmlGMonth.type, null);
        }
        
        public static XmlGMonth parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlGMonth)XmlBeans.getContextTypeLoader().parse(is, XmlGMonth.type, options);
        }
        
        public static XmlGMonth parse(final Reader r) throws XmlException, IOException {
            return (XmlGMonth)XmlBeans.getContextTypeLoader().parse(r, XmlGMonth.type, null);
        }
        
        public static XmlGMonth parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlGMonth)XmlBeans.getContextTypeLoader().parse(r, XmlGMonth.type, options);
        }
        
        public static XmlGMonth parse(final Node node) throws XmlException {
            return (XmlGMonth)XmlBeans.getContextTypeLoader().parse(node, XmlGMonth.type, null);
        }
        
        public static XmlGMonth parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlGMonth)XmlBeans.getContextTypeLoader().parse(node, XmlGMonth.type, options);
        }
        
        @Deprecated
        public static XmlGMonth parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlGMonth)XmlBeans.getContextTypeLoader().parse(xis, XmlGMonth.type, null);
        }
        
        @Deprecated
        public static XmlGMonth parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlGMonth)XmlBeans.getContextTypeLoader().parse(xis, XmlGMonth.type, options);
        }
        
        public static XmlGMonth parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlGMonth)XmlBeans.getContextTypeLoader().parse(xsr, XmlGMonth.type, null);
        }
        
        public static XmlGMonth parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlGMonth)XmlBeans.getContextTypeLoader().parse(xsr, XmlGMonth.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlGMonth.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlGMonth.type, options);
        }
        
        private Factory() {
        }
    }
}
