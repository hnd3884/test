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

public interface STDocProtect extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STDocProtect.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stdocprotect5801type");
    public static final Enum NONE = Enum.forString("none");
    public static final Enum READ_ONLY = Enum.forString("readOnly");
    public static final Enum COMMENTS = Enum.forString("comments");
    public static final Enum TRACKED_CHANGES = Enum.forString("trackedChanges");
    public static final Enum FORMS = Enum.forString("forms");
    public static final int INT_NONE = 1;
    public static final int INT_READ_ONLY = 2;
    public static final int INT_COMMENTS = 3;
    public static final int INT_TRACKED_CHANGES = 4;
    public static final int INT_FORMS = 5;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STDocProtect newValue(final Object o) {
            return (STDocProtect)STDocProtect.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STDocProtect.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STDocProtect newInstance() {
            return (STDocProtect)getTypeLoader().newInstance(STDocProtect.type, (XmlOptions)null);
        }
        
        public static STDocProtect newInstance(final XmlOptions xmlOptions) {
            return (STDocProtect)getTypeLoader().newInstance(STDocProtect.type, xmlOptions);
        }
        
        public static STDocProtect parse(final String s) throws XmlException {
            return (STDocProtect)getTypeLoader().parse(s, STDocProtect.type, (XmlOptions)null);
        }
        
        public static STDocProtect parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STDocProtect)getTypeLoader().parse(s, STDocProtect.type, xmlOptions);
        }
        
        public static STDocProtect parse(final File file) throws XmlException, IOException {
            return (STDocProtect)getTypeLoader().parse(file, STDocProtect.type, (XmlOptions)null);
        }
        
        public static STDocProtect parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDocProtect)getTypeLoader().parse(file, STDocProtect.type, xmlOptions);
        }
        
        public static STDocProtect parse(final URL url) throws XmlException, IOException {
            return (STDocProtect)getTypeLoader().parse(url, STDocProtect.type, (XmlOptions)null);
        }
        
        public static STDocProtect parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDocProtect)getTypeLoader().parse(url, STDocProtect.type, xmlOptions);
        }
        
        public static STDocProtect parse(final InputStream inputStream) throws XmlException, IOException {
            return (STDocProtect)getTypeLoader().parse(inputStream, STDocProtect.type, (XmlOptions)null);
        }
        
        public static STDocProtect parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDocProtect)getTypeLoader().parse(inputStream, STDocProtect.type, xmlOptions);
        }
        
        public static STDocProtect parse(final Reader reader) throws XmlException, IOException {
            return (STDocProtect)getTypeLoader().parse(reader, STDocProtect.type, (XmlOptions)null);
        }
        
        public static STDocProtect parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDocProtect)getTypeLoader().parse(reader, STDocProtect.type, xmlOptions);
        }
        
        public static STDocProtect parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STDocProtect)getTypeLoader().parse(xmlStreamReader, STDocProtect.type, (XmlOptions)null);
        }
        
        public static STDocProtect parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STDocProtect)getTypeLoader().parse(xmlStreamReader, STDocProtect.type, xmlOptions);
        }
        
        public static STDocProtect parse(final Node node) throws XmlException {
            return (STDocProtect)getTypeLoader().parse(node, STDocProtect.type, (XmlOptions)null);
        }
        
        public static STDocProtect parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STDocProtect)getTypeLoader().parse(node, STDocProtect.type, xmlOptions);
        }
        
        @Deprecated
        public static STDocProtect parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STDocProtect)getTypeLoader().parse(xmlInputStream, STDocProtect.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STDocProtect parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STDocProtect)getTypeLoader().parse(xmlInputStream, STDocProtect.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STDocProtect.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STDocProtect.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_NONE = 1;
        static final int INT_READ_ONLY = 2;
        static final int INT_COMMENTS = 3;
        static final int INT_TRACKED_CHANGES = 4;
        static final int INT_FORMS = 5;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("none", 1), new Enum("readOnly", 2), new Enum("comments", 3), new Enum("trackedChanges", 4), new Enum("forms", 5) });
        }
    }
}
