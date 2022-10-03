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

public interface STCalcMode extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STCalcMode.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stcalcmode5e71type");
    public static final Enum MANUAL = Enum.forString("manual");
    public static final Enum AUTO = Enum.forString("auto");
    public static final Enum AUTO_NO_TABLE = Enum.forString("autoNoTable");
    public static final int INT_MANUAL = 1;
    public static final int INT_AUTO = 2;
    public static final int INT_AUTO_NO_TABLE = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STCalcMode newValue(final Object o) {
            return (STCalcMode)STCalcMode.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STCalcMode.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STCalcMode newInstance() {
            return (STCalcMode)getTypeLoader().newInstance(STCalcMode.type, (XmlOptions)null);
        }
        
        public static STCalcMode newInstance(final XmlOptions xmlOptions) {
            return (STCalcMode)getTypeLoader().newInstance(STCalcMode.type, xmlOptions);
        }
        
        public static STCalcMode parse(final String s) throws XmlException {
            return (STCalcMode)getTypeLoader().parse(s, STCalcMode.type, (XmlOptions)null);
        }
        
        public static STCalcMode parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STCalcMode)getTypeLoader().parse(s, STCalcMode.type, xmlOptions);
        }
        
        public static STCalcMode parse(final File file) throws XmlException, IOException {
            return (STCalcMode)getTypeLoader().parse(file, STCalcMode.type, (XmlOptions)null);
        }
        
        public static STCalcMode parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCalcMode)getTypeLoader().parse(file, STCalcMode.type, xmlOptions);
        }
        
        public static STCalcMode parse(final URL url) throws XmlException, IOException {
            return (STCalcMode)getTypeLoader().parse(url, STCalcMode.type, (XmlOptions)null);
        }
        
        public static STCalcMode parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCalcMode)getTypeLoader().parse(url, STCalcMode.type, xmlOptions);
        }
        
        public static STCalcMode parse(final InputStream inputStream) throws XmlException, IOException {
            return (STCalcMode)getTypeLoader().parse(inputStream, STCalcMode.type, (XmlOptions)null);
        }
        
        public static STCalcMode parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCalcMode)getTypeLoader().parse(inputStream, STCalcMode.type, xmlOptions);
        }
        
        public static STCalcMode parse(final Reader reader) throws XmlException, IOException {
            return (STCalcMode)getTypeLoader().parse(reader, STCalcMode.type, (XmlOptions)null);
        }
        
        public static STCalcMode parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCalcMode)getTypeLoader().parse(reader, STCalcMode.type, xmlOptions);
        }
        
        public static STCalcMode parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STCalcMode)getTypeLoader().parse(xmlStreamReader, STCalcMode.type, (XmlOptions)null);
        }
        
        public static STCalcMode parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STCalcMode)getTypeLoader().parse(xmlStreamReader, STCalcMode.type, xmlOptions);
        }
        
        public static STCalcMode parse(final Node node) throws XmlException {
            return (STCalcMode)getTypeLoader().parse(node, STCalcMode.type, (XmlOptions)null);
        }
        
        public static STCalcMode parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STCalcMode)getTypeLoader().parse(node, STCalcMode.type, xmlOptions);
        }
        
        @Deprecated
        public static STCalcMode parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STCalcMode)getTypeLoader().parse(xmlInputStream, STCalcMode.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STCalcMode parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STCalcMode)getTypeLoader().parse(xmlInputStream, STCalcMode.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCalcMode.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCalcMode.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_MANUAL = 1;
        static final int INT_AUTO = 2;
        static final int INT_AUTO_NO_TABLE = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("manual", 1), new Enum("auto", 2), new Enum("autoNoTable", 3) });
        }
    }
}
