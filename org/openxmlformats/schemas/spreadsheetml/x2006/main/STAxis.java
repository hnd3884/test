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

public interface STAxis extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STAxis.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("staxis45batype");
    public static final Enum AXIS_ROW = Enum.forString("axisRow");
    public static final Enum AXIS_COL = Enum.forString("axisCol");
    public static final Enum AXIS_PAGE = Enum.forString("axisPage");
    public static final Enum AXIS_VALUES = Enum.forString("axisValues");
    public static final int INT_AXIS_ROW = 1;
    public static final int INT_AXIS_COL = 2;
    public static final int INT_AXIS_PAGE = 3;
    public static final int INT_AXIS_VALUES = 4;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STAxis newValue(final Object o) {
            return (STAxis)STAxis.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STAxis.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STAxis newInstance() {
            return (STAxis)getTypeLoader().newInstance(STAxis.type, (XmlOptions)null);
        }
        
        public static STAxis newInstance(final XmlOptions xmlOptions) {
            return (STAxis)getTypeLoader().newInstance(STAxis.type, xmlOptions);
        }
        
        public static STAxis parse(final String s) throws XmlException {
            return (STAxis)getTypeLoader().parse(s, STAxis.type, (XmlOptions)null);
        }
        
        public static STAxis parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STAxis)getTypeLoader().parse(s, STAxis.type, xmlOptions);
        }
        
        public static STAxis parse(final File file) throws XmlException, IOException {
            return (STAxis)getTypeLoader().parse(file, STAxis.type, (XmlOptions)null);
        }
        
        public static STAxis parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STAxis)getTypeLoader().parse(file, STAxis.type, xmlOptions);
        }
        
        public static STAxis parse(final URL url) throws XmlException, IOException {
            return (STAxis)getTypeLoader().parse(url, STAxis.type, (XmlOptions)null);
        }
        
        public static STAxis parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STAxis)getTypeLoader().parse(url, STAxis.type, xmlOptions);
        }
        
        public static STAxis parse(final InputStream inputStream) throws XmlException, IOException {
            return (STAxis)getTypeLoader().parse(inputStream, STAxis.type, (XmlOptions)null);
        }
        
        public static STAxis parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STAxis)getTypeLoader().parse(inputStream, STAxis.type, xmlOptions);
        }
        
        public static STAxis parse(final Reader reader) throws XmlException, IOException {
            return (STAxis)getTypeLoader().parse(reader, STAxis.type, (XmlOptions)null);
        }
        
        public static STAxis parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STAxis)getTypeLoader().parse(reader, STAxis.type, xmlOptions);
        }
        
        public static STAxis parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STAxis)getTypeLoader().parse(xmlStreamReader, STAxis.type, (XmlOptions)null);
        }
        
        public static STAxis parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STAxis)getTypeLoader().parse(xmlStreamReader, STAxis.type, xmlOptions);
        }
        
        public static STAxis parse(final Node node) throws XmlException {
            return (STAxis)getTypeLoader().parse(node, STAxis.type, (XmlOptions)null);
        }
        
        public static STAxis parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STAxis)getTypeLoader().parse(node, STAxis.type, xmlOptions);
        }
        
        @Deprecated
        public static STAxis parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STAxis)getTypeLoader().parse(xmlInputStream, STAxis.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STAxis parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STAxis)getTypeLoader().parse(xmlInputStream, STAxis.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STAxis.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STAxis.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_AXIS_ROW = 1;
        static final int INT_AXIS_COL = 2;
        static final int INT_AXIS_PAGE = 3;
        static final int INT_AXIS_VALUES = 4;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("axisRow", 1), new Enum("axisCol", 2), new Enum("axisPage", 3), new Enum("axisValues", 4) });
        }
    }
}
