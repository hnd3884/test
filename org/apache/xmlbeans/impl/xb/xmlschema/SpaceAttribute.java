package org.apache.xmlbeans.impl.xb.xmlschema;

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
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface SpaceAttribute extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SpaceAttribute.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLLANG").resolveHandle("space9344attrtypetype");
    
    Space.Enum getSpace();
    
    Space xgetSpace();
    
    boolean isSetSpace();
    
    void setSpace(final Space.Enum p0);
    
    void xsetSpace(final Space p0);
    
    void unsetSpace();
    
    public interface Space extends XmlNCName
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Space.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLLANG").resolveHandle("spaceb986attrtype");
        public static final Enum DEFAULT = Enum.forString("default");
        public static final Enum PRESERVE = Enum.forString("preserve");
        public static final int INT_DEFAULT = 1;
        public static final int INT_PRESERVE = 2;
        
        StringEnumAbstractBase enumValue();
        
        void set(final StringEnumAbstractBase p0);
        
        public static final class Enum extends StringEnumAbstractBase
        {
            static final int INT_DEFAULT = 1;
            static final int INT_PRESERVE = 2;
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
                table = new Table(new Enum[] { new Enum("default", 1), new Enum("preserve", 2) });
            }
        }
        
        public static final class Factory
        {
            public static Space newValue(final Object obj) {
                return (Space)Space.type.newValue(obj);
            }
            
            public static Space newInstance() {
                return (Space)XmlBeans.getContextTypeLoader().newInstance(Space.type, null);
            }
            
            public static Space newInstance(final XmlOptions options) {
                return (Space)XmlBeans.getContextTypeLoader().newInstance(Space.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static SpaceAttribute newInstance() {
            return (SpaceAttribute)XmlBeans.getContextTypeLoader().newInstance(SpaceAttribute.type, null);
        }
        
        public static SpaceAttribute newInstance(final XmlOptions options) {
            return (SpaceAttribute)XmlBeans.getContextTypeLoader().newInstance(SpaceAttribute.type, options);
        }
        
        public static SpaceAttribute parse(final String xmlAsString) throws XmlException {
            return (SpaceAttribute)XmlBeans.getContextTypeLoader().parse(xmlAsString, SpaceAttribute.type, null);
        }
        
        public static SpaceAttribute parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (SpaceAttribute)XmlBeans.getContextTypeLoader().parse(xmlAsString, SpaceAttribute.type, options);
        }
        
        public static SpaceAttribute parse(final File file) throws XmlException, IOException {
            return (SpaceAttribute)XmlBeans.getContextTypeLoader().parse(file, SpaceAttribute.type, null);
        }
        
        public static SpaceAttribute parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (SpaceAttribute)XmlBeans.getContextTypeLoader().parse(file, SpaceAttribute.type, options);
        }
        
        public static SpaceAttribute parse(final URL u) throws XmlException, IOException {
            return (SpaceAttribute)XmlBeans.getContextTypeLoader().parse(u, SpaceAttribute.type, null);
        }
        
        public static SpaceAttribute parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (SpaceAttribute)XmlBeans.getContextTypeLoader().parse(u, SpaceAttribute.type, options);
        }
        
        public static SpaceAttribute parse(final InputStream is) throws XmlException, IOException {
            return (SpaceAttribute)XmlBeans.getContextTypeLoader().parse(is, SpaceAttribute.type, null);
        }
        
        public static SpaceAttribute parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (SpaceAttribute)XmlBeans.getContextTypeLoader().parse(is, SpaceAttribute.type, options);
        }
        
        public static SpaceAttribute parse(final Reader r) throws XmlException, IOException {
            return (SpaceAttribute)XmlBeans.getContextTypeLoader().parse(r, SpaceAttribute.type, null);
        }
        
        public static SpaceAttribute parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (SpaceAttribute)XmlBeans.getContextTypeLoader().parse(r, SpaceAttribute.type, options);
        }
        
        public static SpaceAttribute parse(final XMLStreamReader sr) throws XmlException {
            return (SpaceAttribute)XmlBeans.getContextTypeLoader().parse(sr, SpaceAttribute.type, null);
        }
        
        public static SpaceAttribute parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (SpaceAttribute)XmlBeans.getContextTypeLoader().parse(sr, SpaceAttribute.type, options);
        }
        
        public static SpaceAttribute parse(final Node node) throws XmlException {
            return (SpaceAttribute)XmlBeans.getContextTypeLoader().parse(node, SpaceAttribute.type, null);
        }
        
        public static SpaceAttribute parse(final Node node, final XmlOptions options) throws XmlException {
            return (SpaceAttribute)XmlBeans.getContextTypeLoader().parse(node, SpaceAttribute.type, options);
        }
        
        @Deprecated
        public static SpaceAttribute parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (SpaceAttribute)XmlBeans.getContextTypeLoader().parse(xis, SpaceAttribute.type, null);
        }
        
        @Deprecated
        public static SpaceAttribute parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (SpaceAttribute)XmlBeans.getContextTypeLoader().parse(xis, SpaceAttribute.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, SpaceAttribute.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, SpaceAttribute.type, options);
        }
        
        private Factory() {
        }
    }
}
