package org.apache.xmlbeans.impl.xb.xmlconfig;

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

public interface NamespaceList extends XmlAnySimpleType
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(NamespaceList.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLCONFIG").resolveHandle("namespacelist20datype");
    
    Object getObjectValue();
    
    void setObjectValue(final Object p0);
    
    @Deprecated
    Object objectValue();
    
    @Deprecated
    void objectSet(final Object p0);
    
    SchemaType instanceType();
    
    public interface Member extends XmlToken
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Member.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLCONFIG").resolveHandle("anonc6fftype");
        public static final Enum ANY = Enum.forString("##any");
        public static final int INT_ANY = 1;
        
        StringEnumAbstractBase enumValue();
        
        void set(final StringEnumAbstractBase p0);
        
        public static final class Enum extends StringEnumAbstractBase
        {
            static final int INT_ANY = 1;
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
                table = new Table(new Enum[] { new Enum("##any", 1) });
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
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Member2.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLCONFIG").resolveHandle("anon5680type");
        
        List getListValue();
        
        List xgetListValue();
        
        void setListValue(final List p0);
        
        @Deprecated
        List listValue();
        
        @Deprecated
        List xlistValue();
        
        @Deprecated
        void set(final List p0);
        
        public interface Item extends XmlAnySimpleType
        {
            public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Item.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLCONFIG").resolveHandle("anon0798type");
            
            Object getObjectValue();
            
            void setObjectValue(final Object p0);
            
            @Deprecated
            Object objectValue();
            
            @Deprecated
            void objectSet(final Object p0);
            
            SchemaType instanceType();
            
            public interface Member extends XmlToken
            {
                public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Member.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLCONFIG").resolveHandle("anon1dd3type");
                public static final Enum LOCAL = Enum.forString("##local");
                public static final int INT_LOCAL = 1;
                
                StringEnumAbstractBase enumValue();
                
                void set(final StringEnumAbstractBase p0);
                
                public static final class Enum extends StringEnumAbstractBase
                {
                    static final int INT_LOCAL = 1;
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
                        table = new Table(new Enum[] { new Enum("##local", 1) });
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
        public static NamespaceList newValue(final Object obj) {
            return (NamespaceList)NamespaceList.type.newValue(obj);
        }
        
        public static NamespaceList newInstance() {
            return (NamespaceList)XmlBeans.getContextTypeLoader().newInstance(NamespaceList.type, null);
        }
        
        public static NamespaceList newInstance(final XmlOptions options) {
            return (NamespaceList)XmlBeans.getContextTypeLoader().newInstance(NamespaceList.type, options);
        }
        
        public static NamespaceList parse(final String xmlAsString) throws XmlException {
            return (NamespaceList)XmlBeans.getContextTypeLoader().parse(xmlAsString, NamespaceList.type, null);
        }
        
        public static NamespaceList parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (NamespaceList)XmlBeans.getContextTypeLoader().parse(xmlAsString, NamespaceList.type, options);
        }
        
        public static NamespaceList parse(final File file) throws XmlException, IOException {
            return (NamespaceList)XmlBeans.getContextTypeLoader().parse(file, NamespaceList.type, null);
        }
        
        public static NamespaceList parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (NamespaceList)XmlBeans.getContextTypeLoader().parse(file, NamespaceList.type, options);
        }
        
        public static NamespaceList parse(final URL u) throws XmlException, IOException {
            return (NamespaceList)XmlBeans.getContextTypeLoader().parse(u, NamespaceList.type, null);
        }
        
        public static NamespaceList parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (NamespaceList)XmlBeans.getContextTypeLoader().parse(u, NamespaceList.type, options);
        }
        
        public static NamespaceList parse(final InputStream is) throws XmlException, IOException {
            return (NamespaceList)XmlBeans.getContextTypeLoader().parse(is, NamespaceList.type, null);
        }
        
        public static NamespaceList parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (NamespaceList)XmlBeans.getContextTypeLoader().parse(is, NamespaceList.type, options);
        }
        
        public static NamespaceList parse(final Reader r) throws XmlException, IOException {
            return (NamespaceList)XmlBeans.getContextTypeLoader().parse(r, NamespaceList.type, null);
        }
        
        public static NamespaceList parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (NamespaceList)XmlBeans.getContextTypeLoader().parse(r, NamespaceList.type, options);
        }
        
        public static NamespaceList parse(final XMLStreamReader sr) throws XmlException {
            return (NamespaceList)XmlBeans.getContextTypeLoader().parse(sr, NamespaceList.type, null);
        }
        
        public static NamespaceList parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (NamespaceList)XmlBeans.getContextTypeLoader().parse(sr, NamespaceList.type, options);
        }
        
        public static NamespaceList parse(final Node node) throws XmlException {
            return (NamespaceList)XmlBeans.getContextTypeLoader().parse(node, NamespaceList.type, null);
        }
        
        public static NamespaceList parse(final Node node, final XmlOptions options) throws XmlException {
            return (NamespaceList)XmlBeans.getContextTypeLoader().parse(node, NamespaceList.type, options);
        }
        
        @Deprecated
        public static NamespaceList parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (NamespaceList)XmlBeans.getContextTypeLoader().parse(xis, NamespaceList.type, null);
        }
        
        @Deprecated
        public static NamespaceList parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (NamespaceList)XmlBeans.getContextTypeLoader().parse(xis, NamespaceList.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, NamespaceList.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, NamespaceList.type, options);
        }
        
        private Factory() {
        }
    }
}
