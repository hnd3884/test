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

public interface XmlDate extends XmlAnySimpleType
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_date");
    
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
        public static XmlDate newInstance() {
            return (XmlDate)XmlBeans.getContextTypeLoader().newInstance(XmlDate.type, null);
        }
        
        public static XmlDate newInstance(final XmlOptions options) {
            return (XmlDate)XmlBeans.getContextTypeLoader().newInstance(XmlDate.type, options);
        }
        
        public static XmlDate newValue(final Object obj) {
            return (XmlDate)XmlDate.type.newValue(obj);
        }
        
        public static XmlDate parse(final String s) throws XmlException {
            return (XmlDate)XmlBeans.getContextTypeLoader().parse(s, XmlDate.type, null);
        }
        
        public static XmlDate parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlDate)XmlBeans.getContextTypeLoader().parse(s, XmlDate.type, options);
        }
        
        public static XmlDate parse(final File f) throws XmlException, IOException {
            return (XmlDate)XmlBeans.getContextTypeLoader().parse(f, XmlDate.type, null);
        }
        
        public static XmlDate parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlDate)XmlBeans.getContextTypeLoader().parse(f, XmlDate.type, options);
        }
        
        public static XmlDate parse(final URL u) throws XmlException, IOException {
            return (XmlDate)XmlBeans.getContextTypeLoader().parse(u, XmlDate.type, null);
        }
        
        public static XmlDate parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlDate)XmlBeans.getContextTypeLoader().parse(u, XmlDate.type, options);
        }
        
        public static XmlDate parse(final InputStream is) throws XmlException, IOException {
            return (XmlDate)XmlBeans.getContextTypeLoader().parse(is, XmlDate.type, null);
        }
        
        public static XmlDate parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlDate)XmlBeans.getContextTypeLoader().parse(is, XmlDate.type, options);
        }
        
        public static XmlDate parse(final Reader r) throws XmlException, IOException {
            return (XmlDate)XmlBeans.getContextTypeLoader().parse(r, XmlDate.type, null);
        }
        
        public static XmlDate parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlDate)XmlBeans.getContextTypeLoader().parse(r, XmlDate.type, options);
        }
        
        public static XmlDate parse(final Node node) throws XmlException {
            return (XmlDate)XmlBeans.getContextTypeLoader().parse(node, XmlDate.type, null);
        }
        
        public static XmlDate parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlDate)XmlBeans.getContextTypeLoader().parse(node, XmlDate.type, options);
        }
        
        @Deprecated
        public static XmlDate parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlDate)XmlBeans.getContextTypeLoader().parse(xis, XmlDate.type, null);
        }
        
        @Deprecated
        public static XmlDate parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlDate)XmlBeans.getContextTypeLoader().parse(xis, XmlDate.type, options);
        }
        
        public static XmlDate parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlDate)XmlBeans.getContextTypeLoader().parse(xsr, XmlDate.type, null);
        }
        
        public static XmlDate parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlDate)XmlBeans.getContextTypeLoader().parse(xsr, XmlDate.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlDate.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlDate.type, options);
        }
        
        private Factory() {
        }
    }
}
