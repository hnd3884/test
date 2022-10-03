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

public interface XmlGYearMonth extends XmlAnySimpleType
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_gYearMonth");
    
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
        public static XmlGYearMonth newInstance() {
            return (XmlGYearMonth)XmlBeans.getContextTypeLoader().newInstance(XmlGYearMonth.type, null);
        }
        
        public static XmlGYearMonth newInstance(final XmlOptions options) {
            return (XmlGYearMonth)XmlBeans.getContextTypeLoader().newInstance(XmlGYearMonth.type, options);
        }
        
        public static XmlGYearMonth newValue(final Object obj) {
            return (XmlGYearMonth)XmlGYearMonth.type.newValue(obj);
        }
        
        public static XmlGYearMonth parse(final String s) throws XmlException {
            return (XmlGYearMonth)XmlBeans.getContextTypeLoader().parse(s, XmlGYearMonth.type, null);
        }
        
        public static XmlGYearMonth parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlGYearMonth)XmlBeans.getContextTypeLoader().parse(s, XmlGYearMonth.type, options);
        }
        
        public static XmlGYearMonth parse(final File f) throws XmlException, IOException {
            return (XmlGYearMonth)XmlBeans.getContextTypeLoader().parse(f, XmlGYearMonth.type, null);
        }
        
        public static XmlGYearMonth parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlGYearMonth)XmlBeans.getContextTypeLoader().parse(f, XmlGYearMonth.type, options);
        }
        
        public static XmlGYearMonth parse(final URL u) throws XmlException, IOException {
            return (XmlGYearMonth)XmlBeans.getContextTypeLoader().parse(u, XmlGYearMonth.type, null);
        }
        
        public static XmlGYearMonth parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlGYearMonth)XmlBeans.getContextTypeLoader().parse(u, XmlGYearMonth.type, options);
        }
        
        public static XmlGYearMonth parse(final InputStream is) throws XmlException, IOException {
            return (XmlGYearMonth)XmlBeans.getContextTypeLoader().parse(is, XmlGYearMonth.type, null);
        }
        
        public static XmlGYearMonth parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlGYearMonth)XmlBeans.getContextTypeLoader().parse(is, XmlGYearMonth.type, options);
        }
        
        public static XmlGYearMonth parse(final Reader r) throws XmlException, IOException {
            return (XmlGYearMonth)XmlBeans.getContextTypeLoader().parse(r, XmlGYearMonth.type, null);
        }
        
        public static XmlGYearMonth parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlGYearMonth)XmlBeans.getContextTypeLoader().parse(r, XmlGYearMonth.type, options);
        }
        
        public static XmlGYearMonth parse(final Node node) throws XmlException {
            return (XmlGYearMonth)XmlBeans.getContextTypeLoader().parse(node, XmlGYearMonth.type, null);
        }
        
        public static XmlGYearMonth parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlGYearMonth)XmlBeans.getContextTypeLoader().parse(node, XmlGYearMonth.type, options);
        }
        
        @Deprecated
        public static XmlGYearMonth parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlGYearMonth)XmlBeans.getContextTypeLoader().parse(xis, XmlGYearMonth.type, null);
        }
        
        @Deprecated
        public static XmlGYearMonth parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlGYearMonth)XmlBeans.getContextTypeLoader().parse(xis, XmlGYearMonth.type, options);
        }
        
        public static XmlGYearMonth parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlGYearMonth)XmlBeans.getContextTypeLoader().parse(xsr, XmlGYearMonth.type, null);
        }
        
        public static XmlGYearMonth parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlGYearMonth)XmlBeans.getContextTypeLoader().parse(xsr, XmlGYearMonth.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlGYearMonth.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlGYearMonth.type, options);
        }
        
        private Factory() {
        }
    }
}
