package org.apache.xmlbeans.impl.xb.xsdschema;

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
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlNMTOKEN;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;

public interface Wildcard extends Annotated
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Wildcard.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("wildcarde0b9type");
    
    Object getNamespace();
    
    NamespaceList xgetNamespace();
    
    boolean isSetNamespace();
    
    void setNamespace(final Object p0);
    
    void xsetNamespace(final NamespaceList p0);
    
    void unsetNamespace();
    
    ProcessContents.Enum getProcessContents();
    
    ProcessContents xgetProcessContents();
    
    boolean isSetProcessContents();
    
    void setProcessContents(final ProcessContents.Enum p0);
    
    void xsetProcessContents(final ProcessContents p0);
    
    void unsetProcessContents();
    
    public interface ProcessContents extends XmlNMTOKEN
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ProcessContents.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("processcontents864aattrtype");
        public static final Enum SKIP = Enum.forString("skip");
        public static final Enum LAX = Enum.forString("lax");
        public static final Enum STRICT = Enum.forString("strict");
        public static final int INT_SKIP = 1;
        public static final int INT_LAX = 2;
        public static final int INT_STRICT = 3;
        
        StringEnumAbstractBase enumValue();
        
        void set(final StringEnumAbstractBase p0);
        
        public static final class Enum extends StringEnumAbstractBase
        {
            static final int INT_SKIP = 1;
            static final int INT_LAX = 2;
            static final int INT_STRICT = 3;
            public static final Table table;
            private static final long serialVersionUID = 1L;
            
            public static Enum forString(final String s) {
                return (Enum)Enum.table.forString(s);
            }
            
            public static Enum forInt(final int i) {
                return (Enum)Enum.table.forInt(i);
            }
            
            private Enum(final String s, final int i) {
                super(s, i);
            }
            
            private Object readResolve() {
                return forInt(this.intValue());
            }
            
            static {
                table = new Table(new Enum[] { new Enum("skip", 1), new Enum("lax", 2), new Enum("strict", 3) });
            }
        }
        
        public static final class Factory
        {
            public static ProcessContents newValue(final Object obj) {
                return (ProcessContents)ProcessContents.type.newValue(obj);
            }
            
            public static ProcessContents newInstance() {
                return (ProcessContents)XmlBeans.getContextTypeLoader().newInstance(ProcessContents.type, null);
            }
            
            public static ProcessContents newInstance(final XmlOptions options) {
                return (ProcessContents)XmlBeans.getContextTypeLoader().newInstance(ProcessContents.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static Wildcard newInstance() {
            return (Wildcard)XmlBeans.getContextTypeLoader().newInstance(Wildcard.type, null);
        }
        
        public static Wildcard newInstance(final XmlOptions options) {
            return (Wildcard)XmlBeans.getContextTypeLoader().newInstance(Wildcard.type, options);
        }
        
        public static Wildcard parse(final String xmlAsString) throws XmlException {
            return (Wildcard)XmlBeans.getContextTypeLoader().parse(xmlAsString, Wildcard.type, null);
        }
        
        public static Wildcard parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (Wildcard)XmlBeans.getContextTypeLoader().parse(xmlAsString, Wildcard.type, options);
        }
        
        public static Wildcard parse(final File file) throws XmlException, IOException {
            return (Wildcard)XmlBeans.getContextTypeLoader().parse(file, Wildcard.type, null);
        }
        
        public static Wildcard parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (Wildcard)XmlBeans.getContextTypeLoader().parse(file, Wildcard.type, options);
        }
        
        public static Wildcard parse(final URL u) throws XmlException, IOException {
            return (Wildcard)XmlBeans.getContextTypeLoader().parse(u, Wildcard.type, null);
        }
        
        public static Wildcard parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (Wildcard)XmlBeans.getContextTypeLoader().parse(u, Wildcard.type, options);
        }
        
        public static Wildcard parse(final InputStream is) throws XmlException, IOException {
            return (Wildcard)XmlBeans.getContextTypeLoader().parse(is, Wildcard.type, null);
        }
        
        public static Wildcard parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (Wildcard)XmlBeans.getContextTypeLoader().parse(is, Wildcard.type, options);
        }
        
        public static Wildcard parse(final Reader r) throws XmlException, IOException {
            return (Wildcard)XmlBeans.getContextTypeLoader().parse(r, Wildcard.type, null);
        }
        
        public static Wildcard parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (Wildcard)XmlBeans.getContextTypeLoader().parse(r, Wildcard.type, options);
        }
        
        public static Wildcard parse(final XMLStreamReader sr) throws XmlException {
            return (Wildcard)XmlBeans.getContextTypeLoader().parse(sr, Wildcard.type, null);
        }
        
        public static Wildcard parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (Wildcard)XmlBeans.getContextTypeLoader().parse(sr, Wildcard.type, options);
        }
        
        public static Wildcard parse(final Node node) throws XmlException {
            return (Wildcard)XmlBeans.getContextTypeLoader().parse(node, Wildcard.type, null);
        }
        
        public static Wildcard parse(final Node node, final XmlOptions options) throws XmlException {
            return (Wildcard)XmlBeans.getContextTypeLoader().parse(node, Wildcard.type, options);
        }
        
        @Deprecated
        public static Wildcard parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (Wildcard)XmlBeans.getContextTypeLoader().parse(xis, Wildcard.type, null);
        }
        
        @Deprecated
        public static Wildcard parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (Wildcard)XmlBeans.getContextTypeLoader().parse(xis, Wildcard.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Wildcard.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Wildcard.type, options);
        }
        
        private Factory() {
        }
    }
}
