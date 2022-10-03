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

public interface XmlGDay extends XmlAnySimpleType
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_gDay");
    
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
        public static XmlGDay newInstance() {
            return (XmlGDay)XmlBeans.getContextTypeLoader().newInstance(XmlGDay.type, null);
        }
        
        public static XmlGDay newInstance(final XmlOptions options) {
            return (XmlGDay)XmlBeans.getContextTypeLoader().newInstance(XmlGDay.type, options);
        }
        
        public static XmlGDay newValue(final Object obj) {
            return (XmlGDay)XmlGDay.type.newValue(obj);
        }
        
        public static XmlGDay parse(final String s) throws XmlException {
            return (XmlGDay)XmlBeans.getContextTypeLoader().parse(s, XmlGDay.type, null);
        }
        
        public static XmlGDay parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlGDay)XmlBeans.getContextTypeLoader().parse(s, XmlGDay.type, options);
        }
        
        public static XmlGDay parse(final File f) throws XmlException, IOException {
            return (XmlGDay)XmlBeans.getContextTypeLoader().parse(f, XmlGDay.type, null);
        }
        
        public static XmlGDay parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlGDay)XmlBeans.getContextTypeLoader().parse(f, XmlGDay.type, options);
        }
        
        public static XmlGDay parse(final URL u) throws XmlException, IOException {
            return (XmlGDay)XmlBeans.getContextTypeLoader().parse(u, XmlGDay.type, null);
        }
        
        public static XmlGDay parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlGDay)XmlBeans.getContextTypeLoader().parse(u, XmlGDay.type, options);
        }
        
        public static XmlGDay parse(final InputStream is) throws XmlException, IOException {
            return (XmlGDay)XmlBeans.getContextTypeLoader().parse(is, XmlGDay.type, null);
        }
        
        public static XmlGDay parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlGDay)XmlBeans.getContextTypeLoader().parse(is, XmlGDay.type, options);
        }
        
        public static XmlGDay parse(final Reader r) throws XmlException, IOException {
            return (XmlGDay)XmlBeans.getContextTypeLoader().parse(r, XmlGDay.type, null);
        }
        
        public static XmlGDay parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlGDay)XmlBeans.getContextTypeLoader().parse(r, XmlGDay.type, options);
        }
        
        public static XmlGDay parse(final Node node) throws XmlException {
            return (XmlGDay)XmlBeans.getContextTypeLoader().parse(node, XmlGDay.type, null);
        }
        
        public static XmlGDay parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlGDay)XmlBeans.getContextTypeLoader().parse(node, XmlGDay.type, options);
        }
        
        @Deprecated
        public static XmlGDay parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlGDay)XmlBeans.getContextTypeLoader().parse(xis, XmlGDay.type, null);
        }
        
        @Deprecated
        public static XmlGDay parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlGDay)XmlBeans.getContextTypeLoader().parse(xis, XmlGDay.type, options);
        }
        
        public static XmlGDay parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlGDay)XmlBeans.getContextTypeLoader().parse(xsr, XmlGDay.type, null);
        }
        
        public static XmlGDay parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlGDay)XmlBeans.getContextTypeLoader().parse(xsr, XmlGDay.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlGDay.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlGDay.type, options);
        }
        
        private Factory() {
        }
    }
}
