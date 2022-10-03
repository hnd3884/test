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
import org.apache.xmlbeans.XmlNonNegativeInteger;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;

public interface NarrowMaxMin extends LocalElement
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(NarrowMaxMin.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("narrowmaxmin926atype");
    
    public interface MinOccurs extends XmlNonNegativeInteger
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(MinOccurs.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("minoccurs1acbattrtype");
        
        public static final class Factory
        {
            public static MinOccurs newValue(final Object obj) {
                return (MinOccurs)MinOccurs.type.newValue(obj);
            }
            
            public static MinOccurs newInstance() {
                return (MinOccurs)XmlBeans.getContextTypeLoader().newInstance(MinOccurs.type, null);
            }
            
            public static MinOccurs newInstance(final XmlOptions options) {
                return (MinOccurs)XmlBeans.getContextTypeLoader().newInstance(MinOccurs.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public interface MaxOccurs extends AllNNI
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(MaxOccurs.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("maxoccursd85dattrtype");
        
        Object getObjectValue();
        
        void setObjectValue(final Object p0);
        
        @Deprecated
        Object objectValue();
        
        @Deprecated
        void objectSet(final Object p0);
        
        SchemaType instanceType();
        
        public static final class Factory
        {
            public static MaxOccurs newValue(final Object obj) {
                return (MaxOccurs)MaxOccurs.type.newValue(obj);
            }
            
            public static MaxOccurs newInstance() {
                return (MaxOccurs)XmlBeans.getContextTypeLoader().newInstance(MaxOccurs.type, null);
            }
            
            public static MaxOccurs newInstance(final XmlOptions options) {
                return (MaxOccurs)XmlBeans.getContextTypeLoader().newInstance(MaxOccurs.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static NarrowMaxMin newInstance() {
            return (NarrowMaxMin)XmlBeans.getContextTypeLoader().newInstance(NarrowMaxMin.type, null);
        }
        
        public static NarrowMaxMin newInstance(final XmlOptions options) {
            return (NarrowMaxMin)XmlBeans.getContextTypeLoader().newInstance(NarrowMaxMin.type, options);
        }
        
        public static NarrowMaxMin parse(final String xmlAsString) throws XmlException {
            return (NarrowMaxMin)XmlBeans.getContextTypeLoader().parse(xmlAsString, NarrowMaxMin.type, null);
        }
        
        public static NarrowMaxMin parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (NarrowMaxMin)XmlBeans.getContextTypeLoader().parse(xmlAsString, NarrowMaxMin.type, options);
        }
        
        public static NarrowMaxMin parse(final File file) throws XmlException, IOException {
            return (NarrowMaxMin)XmlBeans.getContextTypeLoader().parse(file, NarrowMaxMin.type, null);
        }
        
        public static NarrowMaxMin parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (NarrowMaxMin)XmlBeans.getContextTypeLoader().parse(file, NarrowMaxMin.type, options);
        }
        
        public static NarrowMaxMin parse(final URL u) throws XmlException, IOException {
            return (NarrowMaxMin)XmlBeans.getContextTypeLoader().parse(u, NarrowMaxMin.type, null);
        }
        
        public static NarrowMaxMin parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (NarrowMaxMin)XmlBeans.getContextTypeLoader().parse(u, NarrowMaxMin.type, options);
        }
        
        public static NarrowMaxMin parse(final InputStream is) throws XmlException, IOException {
            return (NarrowMaxMin)XmlBeans.getContextTypeLoader().parse(is, NarrowMaxMin.type, null);
        }
        
        public static NarrowMaxMin parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (NarrowMaxMin)XmlBeans.getContextTypeLoader().parse(is, NarrowMaxMin.type, options);
        }
        
        public static NarrowMaxMin parse(final Reader r) throws XmlException, IOException {
            return (NarrowMaxMin)XmlBeans.getContextTypeLoader().parse(r, NarrowMaxMin.type, null);
        }
        
        public static NarrowMaxMin parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (NarrowMaxMin)XmlBeans.getContextTypeLoader().parse(r, NarrowMaxMin.type, options);
        }
        
        public static NarrowMaxMin parse(final XMLStreamReader sr) throws XmlException {
            return (NarrowMaxMin)XmlBeans.getContextTypeLoader().parse(sr, NarrowMaxMin.type, null);
        }
        
        public static NarrowMaxMin parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (NarrowMaxMin)XmlBeans.getContextTypeLoader().parse(sr, NarrowMaxMin.type, options);
        }
        
        public static NarrowMaxMin parse(final Node node) throws XmlException {
            return (NarrowMaxMin)XmlBeans.getContextTypeLoader().parse(node, NarrowMaxMin.type, null);
        }
        
        public static NarrowMaxMin parse(final Node node, final XmlOptions options) throws XmlException {
            return (NarrowMaxMin)XmlBeans.getContextTypeLoader().parse(node, NarrowMaxMin.type, options);
        }
        
        @Deprecated
        public static NarrowMaxMin parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (NarrowMaxMin)XmlBeans.getContextTypeLoader().parse(xis, NarrowMaxMin.type, null);
        }
        
        @Deprecated
        public static NarrowMaxMin parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (NarrowMaxMin)XmlBeans.getContextTypeLoader().parse(xis, NarrowMaxMin.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, NarrowMaxMin.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, NarrowMaxMin.type, options);
        }
        
        private Factory() {
        }
    }
}
