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

public interface ReducedDerivationControl extends DerivationControl
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ReducedDerivationControl.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("reducedderivationcontrole1cbtype");
    public static final Enum EXTENSION = DerivationControl.EXTENSION;
    public static final Enum RESTRICTION = DerivationControl.RESTRICTION;
    public static final int INT_EXTENSION = 2;
    public static final int INT_RESTRICTION = 3;
    
    public static final class Factory
    {
        public static ReducedDerivationControl newValue(final Object obj) {
            return (ReducedDerivationControl)ReducedDerivationControl.type.newValue(obj);
        }
        
        public static ReducedDerivationControl newInstance() {
            return (ReducedDerivationControl)XmlBeans.getContextTypeLoader().newInstance(ReducedDerivationControl.type, null);
        }
        
        public static ReducedDerivationControl newInstance(final XmlOptions options) {
            return (ReducedDerivationControl)XmlBeans.getContextTypeLoader().newInstance(ReducedDerivationControl.type, options);
        }
        
        public static ReducedDerivationControl parse(final String xmlAsString) throws XmlException {
            return (ReducedDerivationControl)XmlBeans.getContextTypeLoader().parse(xmlAsString, ReducedDerivationControl.type, null);
        }
        
        public static ReducedDerivationControl parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (ReducedDerivationControl)XmlBeans.getContextTypeLoader().parse(xmlAsString, ReducedDerivationControl.type, options);
        }
        
        public static ReducedDerivationControl parse(final File file) throws XmlException, IOException {
            return (ReducedDerivationControl)XmlBeans.getContextTypeLoader().parse(file, ReducedDerivationControl.type, null);
        }
        
        public static ReducedDerivationControl parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (ReducedDerivationControl)XmlBeans.getContextTypeLoader().parse(file, ReducedDerivationControl.type, options);
        }
        
        public static ReducedDerivationControl parse(final URL u) throws XmlException, IOException {
            return (ReducedDerivationControl)XmlBeans.getContextTypeLoader().parse(u, ReducedDerivationControl.type, null);
        }
        
        public static ReducedDerivationControl parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (ReducedDerivationControl)XmlBeans.getContextTypeLoader().parse(u, ReducedDerivationControl.type, options);
        }
        
        public static ReducedDerivationControl parse(final InputStream is) throws XmlException, IOException {
            return (ReducedDerivationControl)XmlBeans.getContextTypeLoader().parse(is, ReducedDerivationControl.type, null);
        }
        
        public static ReducedDerivationControl parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (ReducedDerivationControl)XmlBeans.getContextTypeLoader().parse(is, ReducedDerivationControl.type, options);
        }
        
        public static ReducedDerivationControl parse(final Reader r) throws XmlException, IOException {
            return (ReducedDerivationControl)XmlBeans.getContextTypeLoader().parse(r, ReducedDerivationControl.type, null);
        }
        
        public static ReducedDerivationControl parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (ReducedDerivationControl)XmlBeans.getContextTypeLoader().parse(r, ReducedDerivationControl.type, options);
        }
        
        public static ReducedDerivationControl parse(final XMLStreamReader sr) throws XmlException {
            return (ReducedDerivationControl)XmlBeans.getContextTypeLoader().parse(sr, ReducedDerivationControl.type, null);
        }
        
        public static ReducedDerivationControl parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (ReducedDerivationControl)XmlBeans.getContextTypeLoader().parse(sr, ReducedDerivationControl.type, options);
        }
        
        public static ReducedDerivationControl parse(final Node node) throws XmlException {
            return (ReducedDerivationControl)XmlBeans.getContextTypeLoader().parse(node, ReducedDerivationControl.type, null);
        }
        
        public static ReducedDerivationControl parse(final Node node, final XmlOptions options) throws XmlException {
            return (ReducedDerivationControl)XmlBeans.getContextTypeLoader().parse(node, ReducedDerivationControl.type, options);
        }
        
        @Deprecated
        public static ReducedDerivationControl parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (ReducedDerivationControl)XmlBeans.getContextTypeLoader().parse(xis, ReducedDerivationControl.type, null);
        }
        
        @Deprecated
        public static ReducedDerivationControl parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (ReducedDerivationControl)XmlBeans.getContextTypeLoader().parse(xis, ReducedDerivationControl.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, ReducedDerivationControl.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, ReducedDerivationControl.type, options);
        }
        
        private Factory() {
        }
    }
}
