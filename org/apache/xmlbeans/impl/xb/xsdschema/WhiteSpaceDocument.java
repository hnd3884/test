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
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlNMTOKEN;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface WhiteSpaceDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(WhiteSpaceDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("whitespaced2c6doctype");
    
    WhiteSpace getWhiteSpace();
    
    void setWhiteSpace(final WhiteSpace p0);
    
    WhiteSpace addNewWhiteSpace();
    
    public interface WhiteSpace extends Facet
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(WhiteSpace.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("whitespace97ffelemtype");
        
        public interface Value extends XmlNMTOKEN
        {
            public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Value.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("value8186attrtype");
            public static final Enum PRESERVE = Enum.forString("preserve");
            public static final Enum REPLACE = Enum.forString("replace");
            public static final Enum COLLAPSE = Enum.forString("collapse");
            public static final int INT_PRESERVE = 1;
            public static final int INT_REPLACE = 2;
            public static final int INT_COLLAPSE = 3;
            
            StringEnumAbstractBase enumValue();
            
            void set(final StringEnumAbstractBase p0);
            
            public static final class Enum extends StringEnumAbstractBase
            {
                static final int INT_PRESERVE = 1;
                static final int INT_REPLACE = 2;
                static final int INT_COLLAPSE = 3;
                public static final Table table;
                private static final long serialVersionUID = 1L;
                
                public static Enum forString(final String s) {
                    return (Enum)Enum.table.forString(s);
                }
                
                public static Enum forInt(final int i) {
                    return (Enum)Enum.table.forInt(i);
                }
                
                private Enum(final String s, final int i) {
                    super(s, i);
                }
                
                private Object readResolve() {
                    return forInt(this.intValue());
                }
                
                static {
                    table = new Table(new Enum[] { new Enum("preserve", 1), new Enum("replace", 2), new Enum("collapse", 3) });
                }
            }
            
            public static final class Factory
            {
                public static Value newValue(final Object obj) {
                    return (Value)Value.type.newValue(obj);
                }
                
                public static Value newInstance() {
                    return (Value)XmlBeans.getContextTypeLoader().newInstance(Value.type, null);
                }
                
                public static Value newInstance(final XmlOptions options) {
                    return (Value)XmlBeans.getContextTypeLoader().newInstance(Value.type, options);
                }
                
                private Factory() {
                }
            }
        }
        
        public static final class Factory
        {
            public static WhiteSpace newInstance() {
                return (WhiteSpace)XmlBeans.getContextTypeLoader().newInstance(WhiteSpace.type, null);
            }
            
            public static WhiteSpace newInstance(final XmlOptions options) {
                return (WhiteSpace)XmlBeans.getContextTypeLoader().newInstance(WhiteSpace.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static WhiteSpaceDocument newInstance() {
            return (WhiteSpaceDocument)XmlBeans.getContextTypeLoader().newInstance(WhiteSpaceDocument.type, null);
        }
        
        public static WhiteSpaceDocument newInstance(final XmlOptions options) {
            return (WhiteSpaceDocument)XmlBeans.getContextTypeLoader().newInstance(WhiteSpaceDocument.type, options);
        }
        
        public static WhiteSpaceDocument parse(final String xmlAsString) throws XmlException {
            return (WhiteSpaceDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, WhiteSpaceDocument.type, null);
        }
        
        public static WhiteSpaceDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (WhiteSpaceDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, WhiteSpaceDocument.type, options);
        }
        
        public static WhiteSpaceDocument parse(final File file) throws XmlException, IOException {
            return (WhiteSpaceDocument)XmlBeans.getContextTypeLoader().parse(file, WhiteSpaceDocument.type, null);
        }
        
        public static WhiteSpaceDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (WhiteSpaceDocument)XmlBeans.getContextTypeLoader().parse(file, WhiteSpaceDocument.type, options);
        }
        
        public static WhiteSpaceDocument parse(final URL u) throws XmlException, IOException {
            return (WhiteSpaceDocument)XmlBeans.getContextTypeLoader().parse(u, WhiteSpaceDocument.type, null);
        }
        
        public static WhiteSpaceDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (WhiteSpaceDocument)XmlBeans.getContextTypeLoader().parse(u, WhiteSpaceDocument.type, options);
        }
        
        public static WhiteSpaceDocument parse(final InputStream is) throws XmlException, IOException {
            return (WhiteSpaceDocument)XmlBeans.getContextTypeLoader().parse(is, WhiteSpaceDocument.type, null);
        }
        
        public static WhiteSpaceDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (WhiteSpaceDocument)XmlBeans.getContextTypeLoader().parse(is, WhiteSpaceDocument.type, options);
        }
        
        public static WhiteSpaceDocument parse(final Reader r) throws XmlException, IOException {
            return (WhiteSpaceDocument)XmlBeans.getContextTypeLoader().parse(r, WhiteSpaceDocument.type, null);
        }
        
        public static WhiteSpaceDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (WhiteSpaceDocument)XmlBeans.getContextTypeLoader().parse(r, WhiteSpaceDocument.type, options);
        }
        
        public static WhiteSpaceDocument parse(final XMLStreamReader sr) throws XmlException {
            return (WhiteSpaceDocument)XmlBeans.getContextTypeLoader().parse(sr, WhiteSpaceDocument.type, null);
        }
        
        public static WhiteSpaceDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (WhiteSpaceDocument)XmlBeans.getContextTypeLoader().parse(sr, WhiteSpaceDocument.type, options);
        }
        
        public static WhiteSpaceDocument parse(final Node node) throws XmlException {
            return (WhiteSpaceDocument)XmlBeans.getContextTypeLoader().parse(node, WhiteSpaceDocument.type, null);
        }
        
        public static WhiteSpaceDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (WhiteSpaceDocument)XmlBeans.getContextTypeLoader().parse(node, WhiteSpaceDocument.type, options);
        }
        
        @Deprecated
        public static WhiteSpaceDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (WhiteSpaceDocument)XmlBeans.getContextTypeLoader().parse(xis, WhiteSpaceDocument.type, null);
        }
        
        @Deprecated
        public static WhiteSpaceDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (WhiteSpaceDocument)XmlBeans.getContextTypeLoader().parse(xis, WhiteSpaceDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, WhiteSpaceDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, WhiteSpaceDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
