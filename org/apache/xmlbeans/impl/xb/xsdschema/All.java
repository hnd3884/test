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

public interface All extends ExplicitGroup
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(All.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("all3c04type");
    
    public interface MinOccurs extends XmlNonNegativeInteger
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(MinOccurs.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("minoccurs9283attrtype");
        
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
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(MaxOccurs.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("maxoccurse8b1attrtype");
        
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
        public static All newInstance() {
            return (All)XmlBeans.getContextTypeLoader().newInstance(All.type, null);
        }
        
        public static All newInstance(final XmlOptions options) {
            return (All)XmlBeans.getContextTypeLoader().newInstance(All.type, options);
        }
        
        public static All parse(final String xmlAsString) throws XmlException {
            return (All)XmlBeans.getContextTypeLoader().parse(xmlAsString, All.type, null);
        }
        
        public static All parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (All)XmlBeans.getContextTypeLoader().parse(xmlAsString, All.type, options);
        }
        
        public static All parse(final File file) throws XmlException, IOException {
            return (All)XmlBeans.getContextTypeLoader().parse(file, All.type, null);
        }
        
        public static All parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (All)XmlBeans.getContextTypeLoader().parse(file, All.type, options);
        }
        
        public static All parse(final URL u) throws XmlException, IOException {
            return (All)XmlBeans.getContextTypeLoader().parse(u, All.type, null);
        }
        
        public static All parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (All)XmlBeans.getContextTypeLoader().parse(u, All.type, options);
        }
        
        public static All parse(final InputStream is) throws XmlException, IOException {
            return (All)XmlBeans.getContextTypeLoader().parse(is, All.type, null);
        }
        
        public static All parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (All)XmlBeans.getContextTypeLoader().parse(is, All.type, options);
        }
        
        public static All parse(final Reader r) throws XmlException, IOException {
            return (All)XmlBeans.getContextTypeLoader().parse(r, All.type, null);
        }
        
        public static All parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (All)XmlBeans.getContextTypeLoader().parse(r, All.type, options);
        }
        
        public static All parse(final XMLStreamReader sr) throws XmlException {
            return (All)XmlBeans.getContextTypeLoader().parse(sr, All.type, null);
        }
        
        public static All parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (All)XmlBeans.getContextTypeLoader().parse(sr, All.type, options);
        }
        
        public static All parse(final Node node) throws XmlException {
            return (All)XmlBeans.getContextTypeLoader().parse(node, All.type, null);
        }
        
        public static All parse(final Node node, final XmlOptions options) throws XmlException {
            return (All)XmlBeans.getContextTypeLoader().parse(node, All.type, options);
        }
        
        @Deprecated
        public static All parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (All)XmlBeans.getContextTypeLoader().parse(xis, All.type, null);
        }
        
        @Deprecated
        public static All parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (All)XmlBeans.getContextTypeLoader().parse(xis, All.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, All.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, All.type, options);
        }
        
        private Factory() {
        }
    }
}
