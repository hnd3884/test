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

public interface XmlGMonthDay extends XmlAnySimpleType
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_gMonthDay");
    
    Calendar getCalendarValue();
    
    void setCalendarValue(final Calendar p0);
    
    GDate getGDateValue();
    
    void setGDateValue(final GDate p0);
    
    @Deprecated
    Calendar calendarValue();
    
    @Deprecated
    void set(final Calendar p0);
    
    @Deprecated
    GDate gDateValue();
    
    @Deprecated
    void set(final GDateSpecification p0);
    
    public static final class Factory
    {
        public static XmlGMonthDay newInstance() {
            return (XmlGMonthDay)XmlBeans.getContextTypeLoader().newInstance(XmlGMonthDay.type, null);
        }
        
        public static XmlGMonthDay newInstance(final XmlOptions options) {
            return (XmlGMonthDay)XmlBeans.getContextTypeLoader().newInstance(XmlGMonthDay.type, options);
        }
        
        public static XmlGMonthDay newValue(final Object obj) {
            return (XmlGMonthDay)XmlGMonthDay.type.newValue(obj);
        }
        
        public static XmlGMonthDay parse(final String s) throws XmlException {
            return (XmlGMonthDay)XmlBeans.getContextTypeLoader().parse(s, XmlGMonthDay.type, null);
        }
        
        public static XmlGMonthDay parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlGMonthDay)XmlBeans.getContextTypeLoader().parse(s, XmlGMonthDay.type, options);
        }
        
        public static XmlGMonthDay parse(final File f) throws XmlException, IOException {
            return (XmlGMonthDay)XmlBeans.getContextTypeLoader().parse(f, XmlGMonthDay.type, null);
        }
        
        public static XmlGMonthDay parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlGMonthDay)XmlBeans.getContextTypeLoader().parse(f, XmlGMonthDay.type, options);
        }
        
        public static XmlGMonthDay parse(final URL u) throws XmlException, IOException {
            return (XmlGMonthDay)XmlBeans.getContextTypeLoader().parse(u, XmlGMonthDay.type, null);
        }
        
        public static XmlGMonthDay parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlGMonthDay)XmlBeans.getContextTypeLoader().parse(u, XmlGMonthDay.type, options);
        }
        
        public static XmlGMonthDay parse(final InputStream is) throws XmlException, IOException {
            return (XmlGMonthDay)XmlBeans.getContextTypeLoader().parse(is, XmlGMonthDay.type, null);
        }
        
        public static XmlGMonthDay parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlGMonthDay)XmlBeans.getContextTypeLoader().parse(is, XmlGMonthDay.type, options);
        }
        
        public static XmlGMonthDay parse(final Reader r) throws XmlException, IOException {
            return (XmlGMonthDay)XmlBeans.getContextTypeLoader().parse(r, XmlGMonthDay.type, null);
        }
        
        public static XmlGMonthDay parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlGMonthDay)XmlBeans.getContextTypeLoader().parse(r, XmlGMonthDay.type, options);
        }
        
        public static XmlGMonthDay parse(final Node node) throws XmlException {
            return (XmlGMonthDay)XmlBeans.getContextTypeLoader().parse(node, XmlGMonthDay.type, null);
        }
        
        public static XmlGMonthDay parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlGMonthDay)XmlBeans.getContextTypeLoader().parse(node, XmlGMonthDay.type, options);
        }
        
        @Deprecated
        public static XmlGMonthDay parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlGMonthDay)XmlBeans.getContextTypeLoader().parse(xis, XmlGMonthDay.type, null);
        }
        
        @Deprecated
        public static XmlGMonthDay parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlGMonthDay)XmlBeans.getContextTypeLoader().parse(xis, XmlGMonthDay.type, options);
        }
        
        public static XmlGMonthDay parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlGMonthDay)XmlBeans.getContextTypeLoader().parse(xsr, XmlGMonthDay.type, null);
        }
        
        public static XmlGMonthDay parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlGMonthDay)XmlBeans.getContextTypeLoader().parse(xsr, XmlGMonthDay.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlGMonthDay.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlGMonthDay.type, options);
        }
        
        private Factory() {
        }
    }
}
