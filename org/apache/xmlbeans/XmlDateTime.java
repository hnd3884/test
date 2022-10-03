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
import java.util.Date;
import java.util.Calendar;

public interface XmlDateTime extends XmlAnySimpleType
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_dateTime");
    
    @Deprecated
    Calendar calendarValue();
    
    @Deprecated
    void set(final Calendar p0);
    
    @Deprecated
    GDate gDateValue();
    
    @Deprecated
    void set(final GDateSpecification p0);
    
    @Deprecated
    Date dateValue();
    
    @Deprecated
    void set(final Date p0);
    
    Calendar getCalendarValue();
    
    void setCalendarValue(final Calendar p0);
    
    GDate getGDateValue();
    
    void setGDateValue(final GDate p0);
    
    Date getDateValue();
    
    void setDateValue(final Date p0);
    
    public static final class Factory
    {
        public static XmlDateTime newInstance() {
            return (XmlDateTime)XmlBeans.getContextTypeLoader().newInstance(XmlDateTime.type, null);
        }
        
        public static XmlDateTime newInstance(final XmlOptions options) {
            return (XmlDateTime)XmlBeans.getContextTypeLoader().newInstance(XmlDateTime.type, options);
        }
        
        public static XmlDateTime newValue(final Object obj) {
            return (XmlDateTime)XmlDateTime.type.newValue(obj);
        }
        
        public static XmlDateTime parse(final String s) throws XmlException {
            return (XmlDateTime)XmlBeans.getContextTypeLoader().parse(s, XmlDateTime.type, null);
        }
        
        public static XmlDateTime parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlDateTime)XmlBeans.getContextTypeLoader().parse(s, XmlDateTime.type, options);
        }
        
        public static XmlDateTime parse(final File f) throws XmlException, IOException {
            return (XmlDateTime)XmlBeans.getContextTypeLoader().parse(f, XmlDateTime.type, null);
        }
        
        public static XmlDateTime parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlDateTime)XmlBeans.getContextTypeLoader().parse(f, XmlDateTime.type, options);
        }
        
        public static XmlDateTime parse(final URL u) throws XmlException, IOException {
            return (XmlDateTime)XmlBeans.getContextTypeLoader().parse(u, XmlDateTime.type, null);
        }
        
        public static XmlDateTime parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlDateTime)XmlBeans.getContextTypeLoader().parse(u, XmlDateTime.type, options);
        }
        
        public static XmlDateTime parse(final InputStream is) throws XmlException, IOException {
            return (XmlDateTime)XmlBeans.getContextTypeLoader().parse(is, XmlDateTime.type, null);
        }
        
        public static XmlDateTime parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlDateTime)XmlBeans.getContextTypeLoader().parse(is, XmlDateTime.type, options);
        }
        
        public static XmlDateTime parse(final Reader r) throws XmlException, IOException {
            return (XmlDateTime)XmlBeans.getContextTypeLoader().parse(r, XmlDateTime.type, null);
        }
        
        public static XmlDateTime parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlDateTime)XmlBeans.getContextTypeLoader().parse(r, XmlDateTime.type, options);
        }
        
        public static XmlDateTime parse(final Node node) throws XmlException {
            return (XmlDateTime)XmlBeans.getContextTypeLoader().parse(node, XmlDateTime.type, null);
        }
        
        public static XmlDateTime parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlDateTime)XmlBeans.getContextTypeLoader().parse(node, XmlDateTime.type, options);
        }
        
        @Deprecated
        public static XmlDateTime parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlDateTime)XmlBeans.getContextTypeLoader().parse(xis, XmlDateTime.type, null);
        }
        
        @Deprecated
        public static XmlDateTime parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlDateTime)XmlBeans.getContextTypeLoader().parse(xis, XmlDateTime.type, options);
        }
        
        public static XmlDateTime parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlDateTime)XmlBeans.getContextTypeLoader().parse(xsr, XmlDateTime.type, null);
        }
        
        public static XmlDateTime parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlDateTime)XmlBeans.getContextTypeLoader().parse(xsr, XmlDateTime.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlDateTime.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlDateTime.type, options);
        }
        
        private Factory() {
        }
    }
}
