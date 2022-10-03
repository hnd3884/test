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

public interface STCellComments extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STCellComments.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stcellcomments7e4ftype");
    public static final Enum NONE = Enum.forString("none");
    public static final Enum AS_DISPLAYED = Enum.forString("asDisplayed");
    public static final Enum AT_END = Enum.forString("atEnd");
    public static final int INT_NONE = 1;
    public static final int INT_AS_DISPLAYED = 2;
    public static final int INT_AT_END = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STCellComments newValue(final Object o) {
            return (STCellComments)STCellComments.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STCellComments.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STCellComments newInstance() {
            return (STCellComments)getTypeLoader().newInstance(STCellComments.type, (XmlOptions)null);
        }
        
        public static STCellComments newInstance(final XmlOptions xmlOptions) {
            return (STCellComments)getTypeLoader().newInstance(STCellComments.type, xmlOptions);
        }
        
        public static STCellComments parse(final String s) throws XmlException {
            return (STCellComments)getTypeLoader().parse(s, STCellComments.type, (XmlOptions)null);
        }
        
        public static STCellComments parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STCellComments)getTypeLoader().parse(s, STCellComments.type, xmlOptions);
        }
        
        public static STCellComments parse(final File file) throws XmlException, IOException {
            return (STCellComments)getTypeLoader().parse(file, STCellComments.type, (XmlOptions)null);
        }
        
        public static STCellComments parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCellComments)getTypeLoader().parse(file, STCellComments.type, xmlOptions);
        }
        
        public static STCellComments parse(final URL url) throws XmlException, IOException {
            return (STCellComments)getTypeLoader().parse(url, STCellComments.type, (XmlOptions)null);
        }
        
        public static STCellComments parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCellComments)getTypeLoader().parse(url, STCellComments.type, xmlOptions);
        }
        
        public static STCellComments parse(final InputStream inputStream) throws XmlException, IOException {
            return (STCellComments)getTypeLoader().parse(inputStream, STCellComments.type, (XmlOptions)null);
        }
        
        public static STCellComments parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCellComments)getTypeLoader().parse(inputStream, STCellComments.type, xmlOptions);
        }
        
        public static STCellComments parse(final Reader reader) throws XmlException, IOException {
            return (STCellComments)getTypeLoader().parse(reader, STCellComments.type, (XmlOptions)null);
        }
        
        public static STCellComments parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCellComments)getTypeLoader().parse(reader, STCellComments.type, xmlOptions);
        }
        
        public static STCellComments parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STCellComments)getTypeLoader().parse(xmlStreamReader, STCellComments.type, (XmlOptions)null);
        }
        
        public static STCellComments parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STCellComments)getTypeLoader().parse(xmlStreamReader, STCellComments.type, xmlOptions);
        }
        
        public static STCellComments parse(final Node node) throws XmlException {
            return (STCellComments)getTypeLoader().parse(node, STCellComments.type, (XmlOptions)null);
        }
        
        public static STCellComments parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STCellComments)getTypeLoader().parse(node, STCellComments.type, xmlOptions);
        }
        
        @Deprecated
        public static STCellComments parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STCellComments)getTypeLoader().parse(xmlInputStream, STCellComments.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STCellComments parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STCellComments)getTypeLoader().parse(xmlInputStream, STCellComments.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCellComments.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCellComments.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_NONE = 1;
        static final int INT_AS_DISPLAYED = 2;
        static final int INT_AT_END = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("none", 1), new Enum("asDisplayed", 2), new Enum("atEnd", 3) });
        }
    }
}
