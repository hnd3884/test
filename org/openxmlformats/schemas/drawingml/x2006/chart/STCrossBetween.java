package org.openxmlformats.schemas.drawingml.x2006.chart;

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

public interface STCrossBetween extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STCrossBetween.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stcrossbetweenf504type");
    public static final Enum BETWEEN = Enum.forString("between");
    public static final Enum MID_CAT = Enum.forString("midCat");
    public static final int INT_BETWEEN = 1;
    public static final int INT_MID_CAT = 2;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STCrossBetween newValue(final Object o) {
            return (STCrossBetween)STCrossBetween.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STCrossBetween.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STCrossBetween newInstance() {
            return (STCrossBetween)getTypeLoader().newInstance(STCrossBetween.type, (XmlOptions)null);
        }
        
        public static STCrossBetween newInstance(final XmlOptions xmlOptions) {
            return (STCrossBetween)getTypeLoader().newInstance(STCrossBetween.type, xmlOptions);
        }
        
        public static STCrossBetween parse(final String s) throws XmlException {
            return (STCrossBetween)getTypeLoader().parse(s, STCrossBetween.type, (XmlOptions)null);
        }
        
        public static STCrossBetween parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STCrossBetween)getTypeLoader().parse(s, STCrossBetween.type, xmlOptions);
        }
        
        public static STCrossBetween parse(final File file) throws XmlException, IOException {
            return (STCrossBetween)getTypeLoader().parse(file, STCrossBetween.type, (XmlOptions)null);
        }
        
        public static STCrossBetween parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCrossBetween)getTypeLoader().parse(file, STCrossBetween.type, xmlOptions);
        }
        
        public static STCrossBetween parse(final URL url) throws XmlException, IOException {
            return (STCrossBetween)getTypeLoader().parse(url, STCrossBetween.type, (XmlOptions)null);
        }
        
        public static STCrossBetween parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCrossBetween)getTypeLoader().parse(url, STCrossBetween.type, xmlOptions);
        }
        
        public static STCrossBetween parse(final InputStream inputStream) throws XmlException, IOException {
            return (STCrossBetween)getTypeLoader().parse(inputStream, STCrossBetween.type, (XmlOptions)null);
        }
        
        public static STCrossBetween parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCrossBetween)getTypeLoader().parse(inputStream, STCrossBetween.type, xmlOptions);
        }
        
        public static STCrossBetween parse(final Reader reader) throws XmlException, IOException {
            return (STCrossBetween)getTypeLoader().parse(reader, STCrossBetween.type, (XmlOptions)null);
        }
        
        public static STCrossBetween parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCrossBetween)getTypeLoader().parse(reader, STCrossBetween.type, xmlOptions);
        }
        
        public static STCrossBetween parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STCrossBetween)getTypeLoader().parse(xmlStreamReader, STCrossBetween.type, (XmlOptions)null);
        }
        
        public static STCrossBetween parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STCrossBetween)getTypeLoader().parse(xmlStreamReader, STCrossBetween.type, xmlOptions);
        }
        
        public static STCrossBetween parse(final Node node) throws XmlException {
            return (STCrossBetween)getTypeLoader().parse(node, STCrossBetween.type, (XmlOptions)null);
        }
        
        public static STCrossBetween parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STCrossBetween)getTypeLoader().parse(node, STCrossBetween.type, xmlOptions);
        }
        
        @Deprecated
        public static STCrossBetween parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STCrossBetween)getTypeLoader().parse(xmlInputStream, STCrossBetween.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STCrossBetween parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STCrossBetween)getTypeLoader().parse(xmlInputStream, STCrossBetween.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCrossBetween.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCrossBetween.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_BETWEEN = 1;
        static final int INT_MID_CAT = 2;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("between", 1), new Enum("midCat", 2) });
        }
    }
}
