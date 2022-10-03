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

public interface JavaNameList extends XmlAnySimpleType
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(JavaNameList.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLCONFIG").resolveHandle("javanamelistbcfetype");
    
    Object getObjectValue();
    
    void setObjectValue(final Object p0);
    
    @Deprecated
    Object objectValue();
    
    @Deprecated
    void objectSet(final Object p0);
    
    SchemaType instanceType();
    
    public interface Member extends XmlToken
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Member.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLCONFIG").resolveHandle("anon3e39type");
        public static final Enum X = Enum.forString("*");
        public static final int INT_X = 1;
        
        StringEnumAbstractBase enumValue();
        
        void set(final StringEnumAbstractBase p0);
        
        public static final class Enum extends StringEnumAbstractBase
        {
            static final int INT_X = 1;
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
                table = new Table(new Enum[] { new Enum("*", 1) });
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
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Member2.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLCONFIG").resolveHandle("anon3a98type");
        
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
        public static JavaNameList newValue(final Object obj) {
            return (JavaNameList)JavaNameList.type.newValue(obj);
        }
        
        public static JavaNameList newInstance() {
            return (JavaNameList)XmlBeans.getContextTypeLoader().newInstance(JavaNameList.type, null);
        }
        
        public static JavaNameList newInstance(final XmlOptions options) {
            return (JavaNameList)XmlBeans.getContextTypeLoader().newInstance(JavaNameList.type, options);
        }
        
        public static JavaNameList parse(final String xmlAsString) throws XmlException {
            return (JavaNameList)XmlBeans.getContextTypeLoader().parse(xmlAsString, JavaNameList.type, null);
        }
        
        public static JavaNameList parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (JavaNameList)XmlBeans.getContextTypeLoader().parse(xmlAsString, JavaNameList.type, options);
        }
        
        public static JavaNameList parse(final File file) throws XmlException, IOException {
            return (JavaNameList)XmlBeans.getContextTypeLoader().parse(file, JavaNameList.type, null);
        }
        
        public static JavaNameList parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (JavaNameList)XmlBeans.getContextTypeLoader().parse(file, JavaNameList.type, options);
        }
        
        public static JavaNameList parse(final URL u) throws XmlException, IOException {
            return (JavaNameList)XmlBeans.getContextTypeLoader().parse(u, JavaNameList.type, null);
        }
        
        public static JavaNameList parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (JavaNameList)XmlBeans.getContextTypeLoader().parse(u, JavaNameList.type, options);
        }
        
        public static JavaNameList parse(final InputStream is) throws XmlException, IOException {
            return (JavaNameList)XmlBeans.getContextTypeLoader().parse(is, JavaNameList.type, null);
        }
        
        public static JavaNameList parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (JavaNameList)XmlBeans.getContextTypeLoader().parse(is, JavaNameList.type, options);
        }
        
        public static JavaNameList parse(final Reader r) throws XmlException, IOException {
            return (JavaNameList)XmlBeans.getContextTypeLoader().parse(r, JavaNameList.type, null);
        }
        
        public static JavaNameList parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (JavaNameList)XmlBeans.getContextTypeLoader().parse(r, JavaNameList.type, options);
        }
        
        public static JavaNameList parse(final XMLStreamReader sr) throws XmlException {
            return (JavaNameList)XmlBeans.getContextTypeLoader().parse(sr, JavaNameList.type, null);
        }
        
        public static JavaNameList parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (JavaNameList)XmlBeans.getContextTypeLoader().parse(sr, JavaNameList.type, options);
        }
        
        public static JavaNameList parse(final Node node) throws XmlException {
            return (JavaNameList)XmlBeans.getContextTypeLoader().parse(node, JavaNameList.type, null);
        }
        
        public static JavaNameList parse(final Node node, final XmlOptions options) throws XmlException {
            return (JavaNameList)XmlBeans.getContextTypeLoader().parse(node, JavaNameList.type, options);
        }
        
        @Deprecated
        public static JavaNameList parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (JavaNameList)XmlBeans.getContextTypeLoader().parse(xis, JavaNameList.type, null);
        }
        
        @Deprecated
        public static JavaNameList parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (JavaNameList)XmlBeans.getContextTypeLoader().parse(xis, JavaNameList.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, JavaNameList.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, JavaNameList.type, options);
        }
        
        private Factory() {
        }
    }
}
