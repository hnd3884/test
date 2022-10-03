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

public interface STCellFormulaType extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STCellFormulaType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stcellformulatypee2cdtype");
    public static final Enum NORMAL = Enum.forString("normal");
    public static final Enum ARRAY = Enum.forString("array");
    public static final Enum DATA_TABLE = Enum.forString("dataTable");
    public static final Enum SHARED = Enum.forString("shared");
    public static final int INT_NORMAL = 1;
    public static final int INT_ARRAY = 2;
    public static final int INT_DATA_TABLE = 3;
    public static final int INT_SHARED = 4;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STCellFormulaType newValue(final Object o) {
            return (STCellFormulaType)STCellFormulaType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STCellFormulaType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STCellFormulaType newInstance() {
            return (STCellFormulaType)getTypeLoader().newInstance(STCellFormulaType.type, (XmlOptions)null);
        }
        
        public static STCellFormulaType newInstance(final XmlOptions xmlOptions) {
            return (STCellFormulaType)getTypeLoader().newInstance(STCellFormulaType.type, xmlOptions);
        }
        
        public static STCellFormulaType parse(final String s) throws XmlException {
            return (STCellFormulaType)getTypeLoader().parse(s, STCellFormulaType.type, (XmlOptions)null);
        }
        
        public static STCellFormulaType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STCellFormulaType)getTypeLoader().parse(s, STCellFormulaType.type, xmlOptions);
        }
        
        public static STCellFormulaType parse(final File file) throws XmlException, IOException {
            return (STCellFormulaType)getTypeLoader().parse(file, STCellFormulaType.type, (XmlOptions)null);
        }
        
        public static STCellFormulaType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCellFormulaType)getTypeLoader().parse(file, STCellFormulaType.type, xmlOptions);
        }
        
        public static STCellFormulaType parse(final URL url) throws XmlException, IOException {
            return (STCellFormulaType)getTypeLoader().parse(url, STCellFormulaType.type, (XmlOptions)null);
        }
        
        public static STCellFormulaType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCellFormulaType)getTypeLoader().parse(url, STCellFormulaType.type, xmlOptions);
        }
        
        public static STCellFormulaType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STCellFormulaType)getTypeLoader().parse(inputStream, STCellFormulaType.type, (XmlOptions)null);
        }
        
        public static STCellFormulaType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCellFormulaType)getTypeLoader().parse(inputStream, STCellFormulaType.type, xmlOptions);
        }
        
        public static STCellFormulaType parse(final Reader reader) throws XmlException, IOException {
            return (STCellFormulaType)getTypeLoader().parse(reader, STCellFormulaType.type, (XmlOptions)null);
        }
        
        public static STCellFormulaType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCellFormulaType)getTypeLoader().parse(reader, STCellFormulaType.type, xmlOptions);
        }
        
        public static STCellFormulaType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STCellFormulaType)getTypeLoader().parse(xmlStreamReader, STCellFormulaType.type, (XmlOptions)null);
        }
        
        public static STCellFormulaType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STCellFormulaType)getTypeLoader().parse(xmlStreamReader, STCellFormulaType.type, xmlOptions);
        }
        
        public static STCellFormulaType parse(final Node node) throws XmlException {
            return (STCellFormulaType)getTypeLoader().parse(node, STCellFormulaType.type, (XmlOptions)null);
        }
        
        public static STCellFormulaType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STCellFormulaType)getTypeLoader().parse(node, STCellFormulaType.type, xmlOptions);
        }
        
        @Deprecated
        public static STCellFormulaType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STCellFormulaType)getTypeLoader().parse(xmlInputStream, STCellFormulaType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STCellFormulaType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STCellFormulaType)getTypeLoader().parse(xmlInputStream, STCellFormulaType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCellFormulaType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCellFormulaType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_NORMAL = 1;
        static final int INT_ARRAY = 2;
        static final int INT_DATA_TABLE = 3;
        static final int INT_SHARED = 4;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("normal", 1), new Enum("array", 2), new Enum("dataTable", 3), new Enum("shared", 4) });
        }
    }
}
