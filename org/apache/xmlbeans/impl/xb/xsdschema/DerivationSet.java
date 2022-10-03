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
import java.util.List;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;

public interface DerivationSet extends XmlAnySimpleType
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(DerivationSet.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("derivationset037atype");
    
    Object getObjectValue();
    
    void setObjectValue(final Object p0);
    
    @Deprecated
    Object objectValue();
    
    @Deprecated
    void objectSet(final Object p0);
    
    SchemaType instanceType();
    
    public interface Member extends XmlToken
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Member.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("anoned75type");
        public static final Enum ALL = Enum.forString("#all");
        public static final int INT_ALL = 1;
        
        StringEnumAbstractBase enumValue();
        
        void set(final StringEnumAbstractBase p0);
        
        public static final class Enum extends StringEnumAbstractBase
        {
            static final int INT_ALL = 1;
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
                table = new Table(new Enum[] { new Enum("#all", 1) });
            }
        }
        
        public static final class Factory
        {
            public static Member newValue(final Object obj) {
                return (Member)Member.type.newValue(obj);
            }
            
            public static Member newInstance() {
                return (Member)XmlBeans.getContextTypeLoader().newInstance(Member.type, null);
            }
            
            public static Member newInstance(final XmlOptions options) {
                return (Member)XmlBeans.getContextTypeLoader().newInstance(Member.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public interface Member2 extends XmlAnySimpleType
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Member2.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("anon9394type");
        
        List getListValue();
        
        List xgetListValue();
        
        void setListValue(final List p0);
        
        @Deprecated
        List listValue();
        
        @Deprecated
        List xlistValue();
        
        @Deprecated
        void set(final List p0);
        
        public static final class Factory
        {
            public static Member2 newValue(final Object obj) {
                return (Member2)Member2.type.newValue(obj);
            }
            
            public static Member2 newInstance() {
                return (Member2)XmlBeans.getContextTypeLoader().newInstance(Member2.type, null);
            }
            
            public static Member2 newInstance(final XmlOptions options) {
                return (Member2)XmlBeans.getContextTypeLoader().newInstance(Member2.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static DerivationSet newValue(final Object obj) {
            return (DerivationSet)DerivationSet.type.newValue(obj);
        }
        
        public static DerivationSet newInstance() {
            return (DerivationSet)XmlBeans.getContextTypeLoader().newInstance(DerivationSet.type, null);
        }
        
        public static DerivationSet newInstance(final XmlOptions options) {
            return (DerivationSet)XmlBeans.getContextTypeLoader().newInstance(DerivationSet.type, options);
        }
        
        public static DerivationSet parse(final String xmlAsString) throws XmlException {
            return (DerivationSet)XmlBeans.getContextTypeLoader().parse(xmlAsString, DerivationSet.type, null);
        }
        
        public static DerivationSet parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (DerivationSet)XmlBeans.getContextTypeLoader().parse(xmlAsString, DerivationSet.type, options);
        }
        
        public static DerivationSet parse(final File file) throws XmlException, IOException {
            return (DerivationSet)XmlBeans.getContextTypeLoader().parse(file, DerivationSet.type, null);
        }
        
        public static DerivationSet parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (DerivationSet)XmlBeans.getContextTypeLoader().parse(file, DerivationSet.type, options);
        }
        
        public static DerivationSet parse(final URL u) throws XmlException, IOException {
            return (DerivationSet)XmlBeans.getContextTypeLoader().parse(u, DerivationSet.type, null);
        }
        
        public static DerivationSet parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (DerivationSet)XmlBeans.getContextTypeLoader().parse(u, DerivationSet.type, options);
        }
        
        public static DerivationSet parse(final InputStream is) throws XmlException, IOException {
            return (DerivationSet)XmlBeans.getContextTypeLoader().parse(is, DerivationSet.type, null);
        }
        
        public static DerivationSet parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (DerivationSet)XmlBeans.getContextTypeLoader().parse(is, DerivationSet.type, options);
        }
        
        public static DerivationSet parse(final Reader r) throws XmlException, IOException {
            return (DerivationSet)XmlBeans.getContextTypeLoader().parse(r, DerivationSet.type, null);
        }
        
        public static DerivationSet parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (DerivationSet)XmlBeans.getContextTypeLoader().parse(r, DerivationSet.type, options);
        }
        
        public static DerivationSet parse(final XMLStreamReader sr) throws XmlException {
            return (DerivationSet)XmlBeans.getContextTypeLoader().parse(sr, DerivationSet.type, null);
        }
        
        public static DerivationSet parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (DerivationSet)XmlBeans.getContextTypeLoader().parse(sr, DerivationSet.type, options);
        }
        
        public static DerivationSet parse(final Node node) throws XmlException {
            return (DerivationSet)XmlBeans.getContextTypeLoader().parse(node, DerivationSet.type, null);
        }
        
        public static DerivationSet parse(final Node node, final XmlOptions options) throws XmlException {
            return (DerivationSet)XmlBeans.getContextTypeLoader().parse(node, DerivationSet.type, options);
        }
        
        @Deprecated
        public static DerivationSet parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (DerivationSet)XmlBeans.getContextTypeLoader().parse(xis, DerivationSet.type, null);
        }
        
        @Deprecated
        public static DerivationSet parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (DerivationSet)XmlBeans.getContextTypeLoader().parse(xis, DerivationSet.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, DerivationSet.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, DerivationSet.type, options);
        }
        
        private Factory() {
        }
    }
}
