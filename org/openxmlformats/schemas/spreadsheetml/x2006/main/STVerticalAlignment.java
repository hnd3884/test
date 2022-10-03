package org.openxmlformats.schemas.spreadsheetml.x2006.main;

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
import org.apache.xmlbeans.SchemaTypeLoader;
import java.lang.ref.SoftReference;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlString;

public interface STVerticalAlignment extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STVerticalAlignment.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stverticalalignmentd35ctype");
    public static final Enum TOP = Enum.forString("top");
    public static final Enum CENTER = Enum.forString("center");
    public static final Enum BOTTOM = Enum.forString("bottom");
    public static final Enum JUSTIFY = Enum.forString("justify");
    public static final Enum DISTRIBUTED = Enum.forString("distributed");
    public static final int INT_TOP = 1;
    public static final int INT_CENTER = 2;
    public static final int INT_BOTTOM = 3;
    public static final int INT_JUSTIFY = 4;
    public static final int INT_DISTRIBUTED = 5;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STVerticalAlignment newValue(final Object o) {
            return (STVerticalAlignment)STVerticalAlignment.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STVerticalAlignment.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STVerticalAlignment newInstance() {
            return (STVerticalAlignment)getTypeLoader().newInstance(STVerticalAlignment.type, (XmlOptions)null);
        }
        
        public static STVerticalAlignment newInstance(final XmlOptions xmlOptions) {
            return (STVerticalAlignment)getTypeLoader().newInstance(STVerticalAlignment.type, xmlOptions);
        }
        
        public static STVerticalAlignment parse(final String s) throws XmlException {
            return (STVerticalAlignment)getTypeLoader().parse(s, STVerticalAlignment.type, (XmlOptions)null);
        }
        
        public static STVerticalAlignment parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STVerticalAlignment)getTypeLoader().parse(s, STVerticalAlignment.type, xmlOptions);
        }
        
        public static STVerticalAlignment parse(final File file) throws XmlException, IOException {
            return (STVerticalAlignment)getTypeLoader().parse(file, STVerticalAlignment.type, (XmlOptions)null);
        }
        
        public static STVerticalAlignment parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STVerticalAlignment)getTypeLoader().parse(file, STVerticalAlignment.type, xmlOptions);
        }
        
        public static STVerticalAlignment parse(final URL url) throws XmlException, IOException {
            return (STVerticalAlignment)getTypeLoader().parse(url, STVerticalAlignment.type, (XmlOptions)null);
        }
        
        public static STVerticalAlignment parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STVerticalAlignment)getTypeLoader().parse(url, STVerticalAlignment.type, xmlOptions);
        }
        
        public static STVerticalAlignment parse(final InputStream inputStream) throws XmlException, IOException {
            return (STVerticalAlignment)getTypeLoader().parse(inputStream, STVerticalAlignment.type, (XmlOptions)null);
        }
        
        public static STVerticalAlignment parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STVerticalAlignment)getTypeLoader().parse(inputStream, STVerticalAlignment.type, xmlOptions);
        }
        
        public static STVerticalAlignment parse(final Reader reader) throws XmlException, IOException {
            return (STVerticalAlignment)getTypeLoader().parse(reader, STVerticalAlignment.type, (XmlOptions)null);
        }
        
        public static STVerticalAlignment parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STVerticalAlignment)getTypeLoader().parse(reader, STVerticalAlignment.type, xmlOptions);
        }
        
        public static STVerticalAlignment parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STVerticalAlignment)getTypeLoader().parse(xmlStreamReader, STVerticalAlignment.type, (XmlOptions)null);
        }
        
        public static STVerticalAlignment parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STVerticalAlignment)getTypeLoader().parse(xmlStreamReader, STVerticalAlignment.type, xmlOptions);
        }
        
        public static STVerticalAlignment parse(final Node node) throws XmlException {
            return (STVerticalAlignment)getTypeLoader().parse(node, STVerticalAlignment.type, (XmlOptions)null);
        }
        
        public static STVerticalAlignment parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STVerticalAlignment)getTypeLoader().parse(node, STVerticalAlignment.type, xmlOptions);
        }
        
        @Deprecated
        public static STVerticalAlignment parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STVerticalAlignment)getTypeLoader().parse(xmlInputStream, STVerticalAlignment.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STVerticalAlignment parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STVerticalAlignment)getTypeLoader().parse(xmlInputStream, STVerticalAlignment.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STVerticalAlignment.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STVerticalAlignment.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_TOP = 1;
        static final int INT_CENTER = 2;
        static final int INT_BOTTOM = 3;
        static final int INT_JUSTIFY = 4;
        static final int INT_DISTRIBUTED = 5;
        public static final StringEnumAbstractBase.Table table;
        private static final long serialVersionUID = 1L;
        
        public static Enum forString(final String s) {
            return (Enum)Enum.table.forString(s);
        }
        
        public static Enum forInt(final int n) {
            return (Enum)Enum.table.forInt(n);
        }
        
        private Enum(final String s, final int n) {
            super(s, n);
        }
        
        private Object readResolve() {
            return forInt(this.intValue());
        }
        
        static {
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("top", 1), new Enum("center", 2), new Enum("bottom", 3), new Enum("justify", 4), new Enum("distributed", 5) });
        }
    }
}
