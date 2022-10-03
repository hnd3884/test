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

public interface BlockSet extends XmlAnySimpleType
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(BlockSet.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("blockset815etype");
    
    Object getObjectValue();
    
    void setObjectValue(final Object p0);
    
    @Deprecated
    Object objectValue();
    
    @Deprecated
    void objectSet(final Object p0);
    
    SchemaType instanceType();
    
    public interface Member extends XmlToken
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Member.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("anon0683type");
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
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Member2.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("anonc904type");
        
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
            public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Item.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("anon421ctype");
            public static final Enum EXTENSION = DerivationControl.EXTENSION;
            public static final Enum RESTRICTION = DerivationControl.RESTRICTION;
            public static final Enum SUBSTITUTION = DerivationControl.SUBSTITUTION;
            public static final int INT_EXTENSION = 2;
            public static final int INT_RESTRICTION = 3;
            public static final int INT_SUBSTITUTION = 1;
            
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
        public static BlockSet newValue(final Object obj) {
            return (BlockSet)BlockSet.type.newValue(obj);
        }
        
        public static BlockSet newInstance() {
            return (BlockSet)XmlBeans.getContextTypeLoader().newInstance(BlockSet.type, null);
        }
        
        public static BlockSet newInstance(final XmlOptions options) {
            return (BlockSet)XmlBeans.getContextTypeLoader().newInstance(BlockSet.type, options);
        }
        
        public static BlockSet parse(final String xmlAsString) throws XmlException {
            return (BlockSet)XmlBeans.getContextTypeLoader().parse(xmlAsString, BlockSet.type, null);
        }
        
        public static BlockSet parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (BlockSet)XmlBeans.getContextTypeLoader().parse(xmlAsString, BlockSet.type, options);
        }
        
        public static BlockSet parse(final File file) throws XmlException, IOException {
            return (BlockSet)XmlBeans.getContextTypeLoader().parse(file, BlockSet.type, null);
        }
        
        public static BlockSet parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (BlockSet)XmlBeans.getContextTypeLoader().parse(file, BlockSet.type, options);
        }
        
        public static BlockSet parse(final URL u) throws XmlException, IOException {
            return (BlockSet)XmlBeans.getContextTypeLoader().parse(u, BlockSet.type, null);
        }
        
        public static BlockSet parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (BlockSet)XmlBeans.getContextTypeLoader().parse(u, BlockSet.type, options);
        }
        
        public static BlockSet parse(final InputStream is) throws XmlException, IOException {
            return (BlockSet)XmlBeans.getContextTypeLoader().parse(is, BlockSet.type, null);
        }
        
        public static BlockSet parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (BlockSet)XmlBeans.getContextTypeLoader().parse(is, BlockSet.type, options);
        }
        
        public static BlockSet parse(final Reader r) throws XmlException, IOException {
            return (BlockSet)XmlBeans.getContextTypeLoader().parse(r, BlockSet.type, null);
        }
        
        public static BlockSet parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (BlockSet)XmlBeans.getContextTypeLoader().parse(r, BlockSet.type, options);
        }
        
        public static BlockSet parse(final XMLStreamReader sr) throws XmlException {
            return (BlockSet)XmlBeans.getContextTypeLoader().parse(sr, BlockSet.type, null);
        }
        
        public static BlockSet parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (BlockSet)XmlBeans.getContextTypeLoader().parse(sr, BlockSet.type, options);
        }
        
        public static BlockSet parse(final Node node) throws XmlException {
            return (BlockSet)XmlBeans.getContextTypeLoader().parse(node, BlockSet.type, null);
        }
        
        public static BlockSet parse(final Node node, final XmlOptions options) throws XmlException {
            return (BlockSet)XmlBeans.getContextTypeLoader().parse(node, BlockSet.type, options);
        }
        
        @Deprecated
        public static BlockSet parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (BlockSet)XmlBeans.getContextTypeLoader().parse(xis, BlockSet.type, null);
        }
        
        @Deprecated
        public static BlockSet parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (BlockSet)XmlBeans.getContextTypeLoader().parse(xis, BlockSet.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, BlockSet.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, BlockSet.type, options);
        }
        
        private Factory() {
        }
    }
}
