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

public interface STTextStrikeType extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTextStrikeType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttextstriketype4744type");
    public static final Enum NO_STRIKE = Enum.forString("noStrike");
    public static final Enum SNG_STRIKE = Enum.forString("sngStrike");
    public static final Enum DBL_STRIKE = Enum.forString("dblStrike");
    public static final int INT_NO_STRIKE = 1;
    public static final int INT_SNG_STRIKE = 2;
    public static final int INT_DBL_STRIKE = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTextStrikeType newValue(final Object o) {
            return (STTextStrikeType)STTextStrikeType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTextStrikeType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTextStrikeType newInstance() {
            return (STTextStrikeType)getTypeLoader().newInstance(STTextStrikeType.type, (XmlOptions)null);
        }
        
        public static STTextStrikeType newInstance(final XmlOptions xmlOptions) {
            return (STTextStrikeType)getTypeLoader().newInstance(STTextStrikeType.type, xmlOptions);
        }
        
        public static STTextStrikeType parse(final String s) throws XmlException {
            return (STTextStrikeType)getTypeLoader().parse(s, STTextStrikeType.type, (XmlOptions)null);
        }
        
        public static STTextStrikeType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTextStrikeType)getTypeLoader().parse(s, STTextStrikeType.type, xmlOptions);
        }
        
        public static STTextStrikeType parse(final File file) throws XmlException, IOException {
            return (STTextStrikeType)getTypeLoader().parse(file, STTextStrikeType.type, (XmlOptions)null);
        }
        
        public static STTextStrikeType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextStrikeType)getTypeLoader().parse(file, STTextStrikeType.type, xmlOptions);
        }
        
        public static STTextStrikeType parse(final URL url) throws XmlException, IOException {
            return (STTextStrikeType)getTypeLoader().parse(url, STTextStrikeType.type, (XmlOptions)null);
        }
        
        public static STTextStrikeType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextStrikeType)getTypeLoader().parse(url, STTextStrikeType.type, xmlOptions);
        }
        
        public static STTextStrikeType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTextStrikeType)getTypeLoader().parse(inputStream, STTextStrikeType.type, (XmlOptions)null);
        }
        
        public static STTextStrikeType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextStrikeType)getTypeLoader().parse(inputStream, STTextStrikeType.type, xmlOptions);
        }
        
        public static STTextStrikeType parse(final Reader reader) throws XmlException, IOException {
            return (STTextStrikeType)getTypeLoader().parse(reader, STTextStrikeType.type, (XmlOptions)null);
        }
        
        public static STTextStrikeType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextStrikeType)getTypeLoader().parse(reader, STTextStrikeType.type, xmlOptions);
        }
        
        public static STTextStrikeType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTextStrikeType)getTypeLoader().parse(xmlStreamReader, STTextStrikeType.type, (XmlOptions)null);
        }
        
        public static STTextStrikeType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTextStrikeType)getTypeLoader().parse(xmlStreamReader, STTextStrikeType.type, xmlOptions);
        }
        
        public static STTextStrikeType parse(final Node node) throws XmlException {
            return (STTextStrikeType)getTypeLoader().parse(node, STTextStrikeType.type, (XmlOptions)null);
        }
        
        public static STTextStrikeType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTextStrikeType)getTypeLoader().parse(node, STTextStrikeType.type, xmlOptions);
        }
        
        @Deprecated
        public static STTextStrikeType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTextStrikeType)getTypeLoader().parse(xmlInputStream, STTextStrikeType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTextStrikeType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTextStrikeType)getTypeLoader().parse(xmlInputStream, STTextStrikeType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextStrikeType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextStrikeType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_NO_STRIKE = 1;
        static final int INT_SNG_STRIKE = 2;
        static final int INT_DBL_STRIKE = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("noStrike", 1), new Enum("sngStrike", 2), new Enum("dblStrike", 3) });
        }
    }
}
