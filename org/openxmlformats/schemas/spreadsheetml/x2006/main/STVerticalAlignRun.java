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

public interface STVerticalAlignRun extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STVerticalAlignRun.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stverticalalignrun4db5type");
    public static final Enum BASELINE = Enum.forString("baseline");
    public static final Enum SUPERSCRIPT = Enum.forString("superscript");
    public static final Enum SUBSCRIPT = Enum.forString("subscript");
    public static final int INT_BASELINE = 1;
    public static final int INT_SUPERSCRIPT = 2;
    public static final int INT_SUBSCRIPT = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STVerticalAlignRun newValue(final Object o) {
            return (STVerticalAlignRun)STVerticalAlignRun.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STVerticalAlignRun.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STVerticalAlignRun newInstance() {
            return (STVerticalAlignRun)getTypeLoader().newInstance(STVerticalAlignRun.type, (XmlOptions)null);
        }
        
        public static STVerticalAlignRun newInstance(final XmlOptions xmlOptions) {
            return (STVerticalAlignRun)getTypeLoader().newInstance(STVerticalAlignRun.type, xmlOptions);
        }
        
        public static STVerticalAlignRun parse(final String s) throws XmlException {
            return (STVerticalAlignRun)getTypeLoader().parse(s, STVerticalAlignRun.type, (XmlOptions)null);
        }
        
        public static STVerticalAlignRun parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STVerticalAlignRun)getTypeLoader().parse(s, STVerticalAlignRun.type, xmlOptions);
        }
        
        public static STVerticalAlignRun parse(final File file) throws XmlException, IOException {
            return (STVerticalAlignRun)getTypeLoader().parse(file, STVerticalAlignRun.type, (XmlOptions)null);
        }
        
        public static STVerticalAlignRun parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STVerticalAlignRun)getTypeLoader().parse(file, STVerticalAlignRun.type, xmlOptions);
        }
        
        public static STVerticalAlignRun parse(final URL url) throws XmlException, IOException {
            return (STVerticalAlignRun)getTypeLoader().parse(url, STVerticalAlignRun.type, (XmlOptions)null);
        }
        
        public static STVerticalAlignRun parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STVerticalAlignRun)getTypeLoader().parse(url, STVerticalAlignRun.type, xmlOptions);
        }
        
        public static STVerticalAlignRun parse(final InputStream inputStream) throws XmlException, IOException {
            return (STVerticalAlignRun)getTypeLoader().parse(inputStream, STVerticalAlignRun.type, (XmlOptions)null);
        }
        
        public static STVerticalAlignRun parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STVerticalAlignRun)getTypeLoader().parse(inputStream, STVerticalAlignRun.type, xmlOptions);
        }
        
        public static STVerticalAlignRun parse(final Reader reader) throws XmlException, IOException {
            return (STVerticalAlignRun)getTypeLoader().parse(reader, STVerticalAlignRun.type, (XmlOptions)null);
        }
        
        public static STVerticalAlignRun parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STVerticalAlignRun)getTypeLoader().parse(reader, STVerticalAlignRun.type, xmlOptions);
        }
        
        public static STVerticalAlignRun parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STVerticalAlignRun)getTypeLoader().parse(xmlStreamReader, STVerticalAlignRun.type, (XmlOptions)null);
        }
        
        public static STVerticalAlignRun parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STVerticalAlignRun)getTypeLoader().parse(xmlStreamReader, STVerticalAlignRun.type, xmlOptions);
        }
        
        public static STVerticalAlignRun parse(final Node node) throws XmlException {
            return (STVerticalAlignRun)getTypeLoader().parse(node, STVerticalAlignRun.type, (XmlOptions)null);
        }
        
        public static STVerticalAlignRun parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STVerticalAlignRun)getTypeLoader().parse(node, STVerticalAlignRun.type, xmlOptions);
        }
        
        @Deprecated
        public static STVerticalAlignRun parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STVerticalAlignRun)getTypeLoader().parse(xmlInputStream, STVerticalAlignRun.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STVerticalAlignRun parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STVerticalAlignRun)getTypeLoader().parse(xmlInputStream, STVerticalAlignRun.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STVerticalAlignRun.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STVerticalAlignRun.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_BASELINE = 1;
        static final int INT_SUPERSCRIPT = 2;
        static final int INT_SUBSCRIPT = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("baseline", 1), new Enum("superscript", 2), new Enum("subscript", 3) });
        }
    }
}
