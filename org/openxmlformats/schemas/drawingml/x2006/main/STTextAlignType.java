package org.openxmlformats.schemas.drawingml.x2006.main;

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
import org.apache.xmlbeans.XmlToken;

public interface STTextAlignType extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTextAlignType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttextaligntypebc93type");
    public static final Enum L = Enum.forString("l");
    public static final Enum CTR = Enum.forString("ctr");
    public static final Enum R = Enum.forString("r");
    public static final Enum JUST = Enum.forString("just");
    public static final Enum JUST_LOW = Enum.forString("justLow");
    public static final Enum DIST = Enum.forString("dist");
    public static final Enum THAI_DIST = Enum.forString("thaiDist");
    public static final int INT_L = 1;
    public static final int INT_CTR = 2;
    public static final int INT_R = 3;
    public static final int INT_JUST = 4;
    public static final int INT_JUST_LOW = 5;
    public static final int INT_DIST = 6;
    public static final int INT_THAI_DIST = 7;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTextAlignType newValue(final Object o) {
            return (STTextAlignType)STTextAlignType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTextAlignType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTextAlignType newInstance() {
            return (STTextAlignType)getTypeLoader().newInstance(STTextAlignType.type, (XmlOptions)null);
        }
        
        public static STTextAlignType newInstance(final XmlOptions xmlOptions) {
            return (STTextAlignType)getTypeLoader().newInstance(STTextAlignType.type, xmlOptions);
        }
        
        public static STTextAlignType parse(final String s) throws XmlException {
            return (STTextAlignType)getTypeLoader().parse(s, STTextAlignType.type, (XmlOptions)null);
        }
        
        public static STTextAlignType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTextAlignType)getTypeLoader().parse(s, STTextAlignType.type, xmlOptions);
        }
        
        public static STTextAlignType parse(final File file) throws XmlException, IOException {
            return (STTextAlignType)getTypeLoader().parse(file, STTextAlignType.type, (XmlOptions)null);
        }
        
        public static STTextAlignType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextAlignType)getTypeLoader().parse(file, STTextAlignType.type, xmlOptions);
        }
        
        public static STTextAlignType parse(final URL url) throws XmlException, IOException {
            return (STTextAlignType)getTypeLoader().parse(url, STTextAlignType.type, (XmlOptions)null);
        }
        
        public static STTextAlignType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextAlignType)getTypeLoader().parse(url, STTextAlignType.type, xmlOptions);
        }
        
        public static STTextAlignType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTextAlignType)getTypeLoader().parse(inputStream, STTextAlignType.type, (XmlOptions)null);
        }
        
        public static STTextAlignType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextAlignType)getTypeLoader().parse(inputStream, STTextAlignType.type, xmlOptions);
        }
        
        public static STTextAlignType parse(final Reader reader) throws XmlException, IOException {
            return (STTextAlignType)getTypeLoader().parse(reader, STTextAlignType.type, (XmlOptions)null);
        }
        
        public static STTextAlignType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextAlignType)getTypeLoader().parse(reader, STTextAlignType.type, xmlOptions);
        }
        
        public static STTextAlignType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTextAlignType)getTypeLoader().parse(xmlStreamReader, STTextAlignType.type, (XmlOptions)null);
        }
        
        public static STTextAlignType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTextAlignType)getTypeLoader().parse(xmlStreamReader, STTextAlignType.type, xmlOptions);
        }
        
        public static STTextAlignType parse(final Node node) throws XmlException {
            return (STTextAlignType)getTypeLoader().parse(node, STTextAlignType.type, (XmlOptions)null);
        }
        
        public static STTextAlignType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTextAlignType)getTypeLoader().parse(node, STTextAlignType.type, xmlOptions);
        }
        
        @Deprecated
        public static STTextAlignType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTextAlignType)getTypeLoader().parse(xmlInputStream, STTextAlignType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTextAlignType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTextAlignType)getTypeLoader().parse(xmlInputStream, STTextAlignType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextAlignType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextAlignType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_L = 1;
        static final int INT_CTR = 2;
        static final int INT_R = 3;
        static final int INT_JUST = 4;
        static final int INT_JUST_LOW = 5;
        static final int INT_DIST = 6;
        static final int INT_THAI_DIST = 7;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("l", 1), new Enum("ctr", 2), new Enum("r", 3), new Enum("just", 4), new Enum("justLow", 5), new Enum("dist", 6), new Enum("thaiDist", 7) });
        }
    }
}
