package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.xml.stream.XMLStreamException;
import org.apache.xmlbeans.xml.stream.XMLInputStream;
import org.w3c.dom.Node;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import java.io.File;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;

public interface TypeDerivationControl extends DerivationControl
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(TypeDerivationControl.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("typederivationcontrol3239type");
    public static final Enum EXTENSION = DerivationControl.EXTENSION;
    public static final Enum RESTRICTION = DerivationControl.RESTRICTION;
    public static final Enum LIST = DerivationControl.LIST;
    public static final Enum UNION = DerivationControl.UNION;
    public static final int INT_EXTENSION = 2;
    public static final int INT_RESTRICTION = 3;
    public static final int INT_LIST = 4;
    public static final int INT_UNION = 5;
    
    public static final class Factory
    {
        public static TypeDerivationControl newValue(final Object obj) {
            return (TypeDerivationControl)TypeDerivationControl.type.newValue(obj);
        }
        
        public static TypeDerivationControl newInstance() {
            return (TypeDerivationControl)XmlBeans.getContextTypeLoader().newInstance(TypeDerivationControl.type, null);
        }
        
        public static TypeDerivationControl newInstance(final XmlOptions options) {
            return (TypeDerivationControl)XmlBeans.getContextTypeLoader().newInstance(TypeDerivationControl.type, options);
        }
        
        public static TypeDerivationControl parse(final String xmlAsString) throws XmlException {
            return (TypeDerivationControl)XmlBeans.getContextTypeLoader().parse(xmlAsString, TypeDerivationControl.type, null);
        }
        
        public static TypeDerivationControl parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (TypeDerivationControl)XmlBeans.getContextTypeLoader().parse(xmlAsString, TypeDerivationControl.type, options);
        }
        
        public static TypeDerivationControl parse(final File file) throws XmlException, IOException {
            return (TypeDerivationControl)XmlBeans.getContextTypeLoader().parse(file, TypeDerivationControl.type, null);
        }
        
        public static TypeDerivationControl parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (TypeDerivationControl)XmlBeans.getContextTypeLoader().parse(file, TypeDerivationControl.type, options);
        }
        
        public static TypeDerivationControl parse(final URL u) throws XmlException, IOException {
            return (TypeDerivationControl)XmlBeans.getContextTypeLoader().parse(u, TypeDerivationControl.type, null);
        }
        
        public static TypeDerivationControl parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (TypeDerivationControl)XmlBeans.getContextTypeLoader().parse(u, TypeDerivationControl.type, options);
        }
        
        public static TypeDerivationControl parse(final InputStream is) throws XmlException, IOException {
            return (TypeDerivationControl)XmlBeans.getContextTypeLoader().parse(is, TypeDerivationControl.type, null);
        }
        
        public static TypeDerivationControl parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (TypeDerivationControl)XmlBeans.getContextTypeLoader().parse(is, TypeDerivationControl.type, options);
        }
        
        public static TypeDerivationControl parse(final Reader r) throws XmlException, IOException {
            return (TypeDerivationControl)XmlBeans.getContextTypeLoader().parse(r, TypeDerivationControl.type, null);
        }
        
        public static TypeDerivationControl parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (TypeDerivationControl)XmlBeans.getContextTypeLoader().parse(r, TypeDerivationControl.type, options);
        }
        
        public static TypeDerivationControl parse(final XMLStreamReader sr) throws XmlException {
            return (TypeDerivationControl)XmlBeans.getContextTypeLoader().parse(sr, TypeDerivationControl.type, null);
        }
        
        public static TypeDerivationControl parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (TypeDerivationControl)XmlBeans.getContextTypeLoader().parse(sr, TypeDerivationControl.type, options);
        }
        
        public static TypeDerivationControl parse(final Node node) throws XmlException {
            return (TypeDerivationControl)XmlBeans.getContextTypeLoader().parse(node, TypeDerivationControl.type, null);
        }
        
        public static TypeDerivationControl parse(final Node node, final XmlOptions options) throws XmlException {
            return (TypeDerivationControl)XmlBeans.getContextTypeLoader().parse(node, TypeDerivationControl.type, options);
        }
        
        @Deprecated
        public static TypeDerivationControl parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (TypeDerivationControl)XmlBeans.getContextTypeLoader().parse(xis, TypeDerivationControl.type, null);
        }
        
        @Deprecated
        public static TypeDerivationControl parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (TypeDerivationControl)XmlBeans.getContextTypeLoader().parse(xis, TypeDerivationControl.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, TypeDerivationControl.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, TypeDerivationControl.type, options);
        }
        
        private Factory() {
        }
    }
}
