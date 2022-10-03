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

public interface SimpleDerivationSet extends XmlAnySimpleType
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SimpleDerivationSet.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("simplederivationsetf70ctype");
    
    Object getObjectValue();
    
    void setObjectValue(final Object p0);
    
    @Deprecated
    Object objectValue();
    
    @Deprecated
    void objectSet(final Object p0);
    
    SchemaType instanceType();
    
    public interface Member extends XmlToken
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Member.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("anon38c7type");
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
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Member2.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("anon8ba6type");
        
        List getListValue();
        
        List xgetListValue();
        
        void setListValue(final List p0);
        
        @Deprecated
        List listValue();
        
        @Deprecated
        List xlistValue();
        
        @Deprecated
        void set(final List p0);
        
        public interface Item extends DerivationControl
        {
            public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Item.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("anonf38etype");
            public static final Enum LIST = DerivationControl.LIST;
            public static final Enum UNION = DerivationControl.UNION;
            public static final Enum RESTRICTION = DerivationControl.RESTRICTION;
            public static final int INT_LIST = 4;
            public static final int INT_UNION = 5;
            public static final int INT_RESTRICTION = 3;
            
            public static final class Factory
            {
                public static Item newValue(final Object obj) {
                    return (Item)Item.type.newValue(obj);
                }
                
                public static Item newInstance() {
                    return (Item)XmlBeans.getContextTypeLoader().newInstance(Item.type, null);
                }
                
                public static Item newInstance(final XmlOptions options) {
                    return (Item)XmlBeans.getContextTypeLoader().newInstance(Item.type, options);
                }
                
                private Factory() {
                }
            }
        }
        
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
        public static SimpleDerivationSet newValue(final Object obj) {
            return (SimpleDerivationSet)SimpleDerivationSet.type.newValue(obj);
        }
        
        public static SimpleDerivationSet newInstance() {
            return (SimpleDerivationSet)XmlBeans.getContextTypeLoader().newInstance(SimpleDerivationSet.type, null);
        }
        
        public static SimpleDerivationSet newInstance(final XmlOptions options) {
            return (SimpleDerivationSet)XmlBeans.getContextTypeLoader().newInstance(SimpleDerivationSet.type, options);
        }
        
        public static SimpleDerivationSet parse(final String xmlAsString) throws XmlException {
            return (SimpleDerivationSet)XmlBeans.getContextTypeLoader().parse(xmlAsString, SimpleDerivationSet.type, null);
        }
        
        public static SimpleDerivationSet parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (SimpleDerivationSet)XmlBeans.getContextTypeLoader().parse(xmlAsString, SimpleDerivationSet.type, options);
        }
        
        public static SimpleDerivationSet parse(final File file) throws XmlException, IOException {
            return (SimpleDerivationSet)XmlBeans.getContextTypeLoader().parse(file, SimpleDerivationSet.type, null);
        }
        
        public static SimpleDerivationSet parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (SimpleDerivationSet)XmlBeans.getContextTypeLoader().parse(file, SimpleDerivationSet.type, options);
        }
        
        public static SimpleDerivationSet parse(final URL u) throws XmlException, IOException {
            return (SimpleDerivationSet)XmlBeans.getContextTypeLoader().parse(u, SimpleDerivationSet.type, null);
        }
        
        public static SimpleDerivationSet parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (SimpleDerivationSet)XmlBeans.getContextTypeLoader().parse(u, SimpleDerivationSet.type, options);
        }
        
        public static SimpleDerivationSet parse(final InputStream is) throws XmlException, IOException {
            return (SimpleDerivationSet)XmlBeans.getContextTypeLoader().parse(is, SimpleDerivationSet.type, null);
        }
        
        public static SimpleDerivationSet parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (SimpleDerivationSet)XmlBeans.getContextTypeLoader().parse(is, SimpleDerivationSet.type, options);
        }
        
        public static SimpleDerivationSet parse(final Reader r) throws XmlException, IOException {
            return (SimpleDerivationSet)XmlBeans.getContextTypeLoader().parse(r, SimpleDerivationSet.type, null);
        }
        
        public static SimpleDerivationSet parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (SimpleDerivationSet)XmlBeans.getContextTypeLoader().parse(r, SimpleDerivationSet.type, options);
        }
        
        public static SimpleDerivationSet parse(final XMLStreamReader sr) throws XmlException {
            return (SimpleDerivationSet)XmlBeans.getContextTypeLoader().parse(sr, SimpleDerivationSet.type, null);
        }
        
        public static SimpleDerivationSet parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (SimpleDerivationSet)XmlBeans.getContextTypeLoader().parse(sr, SimpleDerivationSet.type, options);
        }
        
        public static SimpleDerivationSet parse(final Node node) throws XmlException {
            return (SimpleDerivationSet)XmlBeans.getContextTypeLoader().parse(node, SimpleDerivationSet.type, null);
        }
        
        public static SimpleDerivationSet parse(final Node node, final XmlOptions options) throws XmlException {
            return (SimpleDerivationSet)XmlBeans.getContextTypeLoader().parse(node, SimpleDerivationSet.type, options);
        }
        
        @Deprecated
        public static SimpleDerivationSet parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (SimpleDerivationSet)XmlBeans.getContextTypeLoader().parse(xis, SimpleDerivationSet.type, null);
        }
        
        @Deprecated
        public static SimpleDerivationSet parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (SimpleDerivationSet)XmlBeans.getContextTypeLoader().parse(xis, SimpleDerivationSet.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, SimpleDerivationSet.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, SimpleDerivationSet.type, options);
        }
        
        private Factory() {
        }
    }
}
