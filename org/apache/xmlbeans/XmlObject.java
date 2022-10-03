package org.apache.xmlbeans;

import org.w3c.dom.DOMImplementation;
import org.apache.xmlbeans.xml.stream.XMLStreamException;
import org.apache.xmlbeans.xml.stream.XMLInputStream;
import org.w3c.dom.Node;
import java.io.Reader;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import java.io.File;
import javax.xml.namespace.QName;

public interface XmlObject extends XmlTokenSource
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_anyType");
    public static final int LESS_THAN = -1;
    public static final int EQUAL = 0;
    public static final int GREATER_THAN = 1;
    public static final int NOT_EQUAL = 2;
    
    SchemaType schemaType();
    
    boolean validate();
    
    boolean validate(final XmlOptions p0);
    
    XmlObject[] selectPath(final String p0);
    
    XmlObject[] selectPath(final String p0, final XmlOptions p1);
    
    XmlObject[] execQuery(final String p0);
    
    XmlObject[] execQuery(final String p0, final XmlOptions p1);
    
    XmlObject changeType(final SchemaType p0);
    
    XmlObject substitute(final QName p0, final SchemaType p1);
    
    boolean isNil();
    
    void setNil();
    
    String toString();
    
    boolean isImmutable();
    
    XmlObject set(final XmlObject p0);
    
    XmlObject copy();
    
    XmlObject copy(final XmlOptions p0);
    
    boolean valueEquals(final XmlObject p0);
    
    int valueHashCode();
    
    int compareTo(final Object p0);
    
    int compareValue(final XmlObject p0);
    
    XmlObject[] selectChildren(final QName p0);
    
    XmlObject[] selectChildren(final String p0, final String p1);
    
    XmlObject[] selectChildren(final QNameSet p0);
    
    XmlObject selectAttribute(final QName p0);
    
    XmlObject selectAttribute(final String p0, final String p1);
    
    XmlObject[] selectAttributes(final QNameSet p0);
    
    public static final class Factory
    {
        public static XmlObject newInstance() {
            return XmlBeans.getContextTypeLoader().newInstance(null, null);
        }
        
        public static XmlObject newInstance(final XmlOptions options) {
            return XmlBeans.getContextTypeLoader().newInstance(null, options);
        }
        
        public static XmlObject newValue(final Object obj) {
            return XmlObject.type.newValue(obj);
        }
        
        public static XmlObject parse(final String xmlAsString) throws XmlException {
            return XmlBeans.getContextTypeLoader().parse(xmlAsString, null, null);
        }
        
        public static XmlObject parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return XmlBeans.getContextTypeLoader().parse(xmlAsString, null, options);
        }
        
        public static XmlObject parse(final File file) throws XmlException, IOException {
            return XmlBeans.getContextTypeLoader().parse(file, null, null);
        }
        
        public static XmlObject parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return XmlBeans.getContextTypeLoader().parse(file, null, options);
        }
        
        public static XmlObject parse(final URL u) throws XmlException, IOException {
            return XmlBeans.getContextTypeLoader().parse(u, null, null);
        }
        
        public static XmlObject parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return XmlBeans.getContextTypeLoader().parse(u, null, options);
        }
        
        public static XmlObject parse(final InputStream is) throws XmlException, IOException {
            return XmlBeans.getContextTypeLoader().parse(is, null, null);
        }
        
        public static XmlObject parse(final XMLStreamReader xsr) throws XmlException {
            return XmlBeans.getContextTypeLoader().parse(xsr, null, null);
        }
        
        public static XmlObject parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return XmlBeans.getContextTypeLoader().parse(is, null, options);
        }
        
        public static XmlObject parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return XmlBeans.getContextTypeLoader().parse(xsr, null, options);
        }
        
        public static XmlObject parse(final Reader r) throws XmlException, IOException {
            return XmlBeans.getContextTypeLoader().parse(r, null, null);
        }
        
        public static XmlObject parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return XmlBeans.getContextTypeLoader().parse(r, null, options);
        }
        
        public static XmlObject parse(final Node node) throws XmlException {
            return XmlBeans.getContextTypeLoader().parse(node, null, null);
        }
        
        public static XmlObject parse(final Node node, final XmlOptions options) throws XmlException {
            return XmlBeans.getContextTypeLoader().parse(node, null, options);
        }
        
        @Deprecated
        public static XmlObject parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().parse(xis, null, null);
        }
        
        @Deprecated
        public static XmlObject parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().parse(xis, null, options);
        }
        
        public static XmlSaxHandler newXmlSaxHandler() {
            return XmlBeans.getContextTypeLoader().newXmlSaxHandler(null, null);
        }
        
        public static XmlSaxHandler newXmlSaxHandler(final XmlOptions options) {
            return XmlBeans.getContextTypeLoader().newXmlSaxHandler(null, options);
        }
        
        public static DOMImplementation newDomImplementation() {
            return XmlBeans.getContextTypeLoader().newDomImplementation(null);
        }
        
        public static DOMImplementation newDomImplementation(final XmlOptions options) {
            return XmlBeans.getContextTypeLoader().newDomImplementation(options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, null, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, null, options);
        }
        
        private Factory() {
        }
    }
}
