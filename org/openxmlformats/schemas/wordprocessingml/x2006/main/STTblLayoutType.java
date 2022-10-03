package org.openxmlformats.schemas.wordprocessingml.x2006.main;

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

public interface STTblLayoutType extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTblLayoutType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttbllayouttype5040type");
    public static final Enum FIXED = Enum.forString("fixed");
    public static final Enum AUTOFIT = Enum.forString("autofit");
    public static final int INT_FIXED = 1;
    public static final int INT_AUTOFIT = 2;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTblLayoutType newValue(final Object o) {
            return (STTblLayoutType)STTblLayoutType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTblLayoutType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTblLayoutType newInstance() {
            return (STTblLayoutType)getTypeLoader().newInstance(STTblLayoutType.type, (XmlOptions)null);
        }
        
        public static STTblLayoutType newInstance(final XmlOptions xmlOptions) {
            return (STTblLayoutType)getTypeLoader().newInstance(STTblLayoutType.type, xmlOptions);
        }
        
        public static STTblLayoutType parse(final String s) throws XmlException {
            return (STTblLayoutType)getTypeLoader().parse(s, STTblLayoutType.type, (XmlOptions)null);
        }
        
        public static STTblLayoutType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTblLayoutType)getTypeLoader().parse(s, STTblLayoutType.type, xmlOptions);
        }
        
        public static STTblLayoutType parse(final File file) throws XmlException, IOException {
            return (STTblLayoutType)getTypeLoader().parse(file, STTblLayoutType.type, (XmlOptions)null);
        }
        
        public static STTblLayoutType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTblLayoutType)getTypeLoader().parse(file, STTblLayoutType.type, xmlOptions);
        }
        
        public static STTblLayoutType parse(final URL url) throws XmlException, IOException {
            return (STTblLayoutType)getTypeLoader().parse(url, STTblLayoutType.type, (XmlOptions)null);
        }
        
        public static STTblLayoutType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTblLayoutType)getTypeLoader().parse(url, STTblLayoutType.type, xmlOptions);
        }
        
        public static STTblLayoutType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTblLayoutType)getTypeLoader().parse(inputStream, STTblLayoutType.type, (XmlOptions)null);
        }
        
        public static STTblLayoutType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTblLayoutType)getTypeLoader().parse(inputStream, STTblLayoutType.type, xmlOptions);
        }
        
        public static STTblLayoutType parse(final Reader reader) throws XmlException, IOException {
            return (STTblLayoutType)getTypeLoader().parse(reader, STTblLayoutType.type, (XmlOptions)null);
        }
        
        public static STTblLayoutType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTblLayoutType)getTypeLoader().parse(reader, STTblLayoutType.type, xmlOptions);
        }
        
        public static STTblLayoutType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTblLayoutType)getTypeLoader().parse(xmlStreamReader, STTblLayoutType.type, (XmlOptions)null);
        }
        
        public static STTblLayoutType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTblLayoutType)getTypeLoader().parse(xmlStreamReader, STTblLayoutType.type, xmlOptions);
        }
        
        public static STTblLayoutType parse(final Node node) throws XmlException {
            return (STTblLayoutType)getTypeLoader().parse(node, STTblLayoutType.type, (XmlOptions)null);
        }
        
        public static STTblLayoutType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTblLayoutType)getTypeLoader().parse(node, STTblLayoutType.type, xmlOptions);
        }
        
        @Deprecated
        public static STTblLayoutType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTblLayoutType)getTypeLoader().parse(xmlInputStream, STTblLayoutType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTblLayoutType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTblLayoutType)getTypeLoader().parse(xmlInputStream, STTblLayoutType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTblLayoutType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTblLayoutType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_FIXED = 1;
        static final int INT_AUTOFIT = 2;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("fixed", 1), new Enum("autofit", 2) });
        }
    }
}
