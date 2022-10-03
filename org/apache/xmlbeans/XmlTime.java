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

public interface XmlTime extends XmlAnySimpleType
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_time");
    
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
        public static XmlTime newInstance() {
            return (XmlTime)XmlBeans.getContextTypeLoader().newInstance(XmlTime.type, null);
        }
        
        public static XmlTime newInstance(final XmlOptions options) {
            return (XmlTime)XmlBeans.getContextTypeLoader().newInstance(XmlTime.type, options);
        }
        
        public static XmlTime newValue(final Object obj) {
            return (XmlTime)XmlTime.type.newValue(obj);
        }
        
        public static XmlTime parse(final String s) throws XmlException {
            return (XmlTime)XmlBeans.getContextTypeLoader().parse(s, XmlTime.type, null);
        }
        
        public static XmlTime parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlTime)XmlBeans.getContextTypeLoader().parse(s, XmlTime.type, options);
        }
        
        public static XmlTime parse(final File f) throws XmlException, IOException {
            return (XmlTime)XmlBeans.getContextTypeLoader().parse(f, XmlTime.type, null);
        }
        
        public static XmlTime parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlTime)XmlBeans.getContextTypeLoader().parse(f, XmlTime.type, options);
        }
        
        public static XmlTime parse(final URL u) throws XmlException, IOException {
            return (XmlTime)XmlBeans.getContextTypeLoader().parse(u, XmlTime.type, null);
        }
        
        public static XmlTime parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlTime)XmlBeans.getContextTypeLoader().parse(u, XmlTime.type, options);
        }
        
        public static XmlTime parse(final InputStream is) throws XmlException, IOException {
            return (XmlTime)XmlBeans.getContextTypeLoader().parse(is, XmlTime.type, null);
        }
        
        public static XmlTime parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlTime)XmlBeans.getContextTypeLoader().parse(is, XmlTime.type, options);
        }
        
        public static XmlTime parse(final Reader r) throws XmlException, IOException {
            return (XmlTime)XmlBeans.getContextTypeLoader().parse(r, XmlTime.type, null);
        }
        
        public static XmlTime parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlTime)XmlBeans.getContextTypeLoader().parse(r, XmlTime.type, options);
        }
        
        public static XmlTime parse(final Node node) throws XmlException {
            return (XmlTime)XmlBeans.getContextTypeLoader().parse(node, XmlTime.type, null);
        }
        
        public static XmlTime parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlTime)XmlBeans.getContextTypeLoader().parse(node, XmlTime.type, options);
        }
        
        @Deprecated
        public static XmlTime parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlTime)XmlBeans.getContextTypeLoader().parse(xis, XmlTime.type, null);
        }
        
        @Deprecated
        public static XmlTime parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlTime)XmlBeans.getContextTypeLoader().parse(xis, XmlTime.type, options);
        }
        
        public static XmlTime parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlTime)XmlBeans.getContextTypeLoader().parse(xsr, XmlTime.type, null);
        }
        
        public static XmlTime parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlTime)XmlBeans.getContextTypeLoader().parse(xsr, XmlTime.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlTime.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlTime.type, options);
        }
        
        private Factory() {
        }
    }
}
