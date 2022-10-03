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

public interface STTextTabAlignType extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTextTabAlignType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttexttabaligntypec202type");
    public static final Enum L = Enum.forString("l");
    public static final Enum CTR = Enum.forString("ctr");
    public static final Enum R = Enum.forString("r");
    public static final Enum DEC = Enum.forString("dec");
    public static final int INT_L = 1;
    public static final int INT_CTR = 2;
    public static final int INT_R = 3;
    public static final int INT_DEC = 4;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTextTabAlignType newValue(final Object o) {
            return (STTextTabAlignType)STTextTabAlignType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTextTabAlignType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTextTabAlignType newInstance() {
            return (STTextTabAlignType)getTypeLoader().newInstance(STTextTabAlignType.type, (XmlOptions)null);
        }
        
        public static STTextTabAlignType newInstance(final XmlOptions xmlOptions) {
            return (STTextTabAlignType)getTypeLoader().newInstance(STTextTabAlignType.type, xmlOptions);
        }
        
        public static STTextTabAlignType parse(final String s) throws XmlException {
            return (STTextTabAlignType)getTypeLoader().parse(s, STTextTabAlignType.type, (XmlOptions)null);
        }
        
        public static STTextTabAlignType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTextTabAlignType)getTypeLoader().parse(s, STTextTabAlignType.type, xmlOptions);
        }
        
        public static STTextTabAlignType parse(final File file) throws XmlException, IOException {
            return (STTextTabAlignType)getTypeLoader().parse(file, STTextTabAlignType.type, (XmlOptions)null);
        }
        
        public static STTextTabAlignType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextTabAlignType)getTypeLoader().parse(file, STTextTabAlignType.type, xmlOptions);
        }
        
        public static STTextTabAlignType parse(final URL url) throws XmlException, IOException {
            return (STTextTabAlignType)getTypeLoader().parse(url, STTextTabAlignType.type, (XmlOptions)null);
        }
        
        public static STTextTabAlignType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextTabAlignType)getTypeLoader().parse(url, STTextTabAlignType.type, xmlOptions);
        }
        
        public static STTextTabAlignType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTextTabAlignType)getTypeLoader().parse(inputStream, STTextTabAlignType.type, (XmlOptions)null);
        }
        
        public static STTextTabAlignType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextTabAlignType)getTypeLoader().parse(inputStream, STTextTabAlignType.type, xmlOptions);
        }
        
        public static STTextTabAlignType parse(final Reader reader) throws XmlException, IOException {
            return (STTextTabAlignType)getTypeLoader().parse(reader, STTextTabAlignType.type, (XmlOptions)null);
        }
        
        public static STTextTabAlignType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextTabAlignType)getTypeLoader().parse(reader, STTextTabAlignType.type, xmlOptions);
        }
        
        public static STTextTabAlignType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTextTabAlignType)getTypeLoader().parse(xmlStreamReader, STTextTabAlignType.type, (XmlOptions)null);
        }
        
        public static STTextTabAlignType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTextTabAlignType)getTypeLoader().parse(xmlStreamReader, STTextTabAlignType.type, xmlOptions);
        }
        
        public static STTextTabAlignType parse(final Node node) throws XmlException {
            return (STTextTabAlignType)getTypeLoader().parse(node, STTextTabAlignType.type, (XmlOptions)null);
        }
        
        public static STTextTabAlignType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTextTabAlignType)getTypeLoader().parse(node, STTextTabAlignType.type, xmlOptions);
        }
        
        @Deprecated
        public static STTextTabAlignType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTextTabAlignType)getTypeLoader().parse(xmlInputStream, STTextTabAlignType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTextTabAlignType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTextTabAlignType)getTypeLoader().parse(xmlInputStream, STTextTabAlignType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextTabAlignType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextTabAlignType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_L = 1;
        static final int INT_CTR = 2;
        static final int INT_R = 3;
        static final int INT_DEC = 4;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("l", 1), new Enum("ctr", 2), new Enum("r", 3), new Enum("dec", 4) });
        }
    }
}
