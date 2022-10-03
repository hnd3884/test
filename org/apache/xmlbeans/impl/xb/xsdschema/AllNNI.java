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
import org.apache.xmlbeans.XmlAnySimpleType;

public interface AllNNI extends XmlAnySimpleType
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(AllNNI.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("allnni78cbtype");
    
    Object getObjectValue();
    
    void setObjectValue(final Object p0);
    
    @Deprecated
    Object objectValue();
    
    @Deprecated
    void objectSet(final Object p0);
    
    SchemaType instanceType();
    
    public interface Member extends XmlNMTOKEN
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Member.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("anon0330type");
        public static final Enum UNBOUNDED = Enum.forString("unbounded");
        public static final int INT_UNBOUNDED = 1;
        
        StringEnumAbstractBase enumValue();
        
        void set(final StringEnumAbstractBase p0);
        
        public static final class Enum extends StringEnumAbstractBase
        {
            static final int INT_UNBOUNDED = 1;
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
                table = new Table(new Enum[] { new Enum("unbounded", 1) });
            }
        }
        
        public static final class Factory
        {
            public static Member newValue(final Object obj) {
                return (Member)Member.type.newValue(obj);
            }
            
            public static Member newInstance() {
                return (Member)XmlBeans.getContextTypeLoader().newInstance(Member.type, null);
            }
            
            public static Member newInstance(final XmlOptions options) {
                return (Member)XmlBeans.getContextTypeLoader().newInstance(Member.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static AllNNI newValue(final Object obj) {
            return (AllNNI)AllNNI.type.newValue(obj);
        }
        
        public static AllNNI newInstance() {
            return (AllNNI)XmlBeans.getContextTypeLoader().newInstance(AllNNI.type, null);
        }
        
        public static AllNNI newInstance(final XmlOptions options) {
            return (AllNNI)XmlBeans.getContextTypeLoader().newInstance(AllNNI.type, options);
        }
        
        public static AllNNI parse(final String xmlAsString) throws XmlException {
            return (AllNNI)XmlBeans.getContextTypeLoader().parse(xmlAsString, AllNNI.type, null);
        }
        
        public static AllNNI parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (AllNNI)XmlBeans.getContextTypeLoader().parse(xmlAsString, AllNNI.type, options);
        }
        
        public static AllNNI parse(final File file) throws XmlException, IOException {
            return (AllNNI)XmlBeans.getContextTypeLoader().parse(file, AllNNI.type, null);
        }
        
        public static AllNNI parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (AllNNI)XmlBeans.getContextTypeLoader().parse(file, AllNNI.type, options);
        }
        
        public static AllNNI parse(final URL u) throws XmlException, IOException {
            return (AllNNI)XmlBeans.getContextTypeLoader().parse(u, AllNNI.type, null);
        }
        
        public static AllNNI parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (AllNNI)XmlBeans.getContextTypeLoader().parse(u, AllNNI.type, options);
        }
        
        public static AllNNI parse(final InputStream is) throws XmlException, IOException {
            return (AllNNI)XmlBeans.getContextTypeLoader().parse(is, AllNNI.type, null);
        }
        
        public static AllNNI parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (AllNNI)XmlBeans.getContextTypeLoader().parse(is, AllNNI.type, options);
        }
        
        public static AllNNI parse(final Reader r) throws XmlException, IOException {
            return (AllNNI)XmlBeans.getContextTypeLoader().parse(r, AllNNI.type, null);
        }
        
        public static AllNNI parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (AllNNI)XmlBeans.getContextTypeLoader().parse(r, AllNNI.type, options);
        }
        
        public static AllNNI parse(final XMLStreamReader sr) throws XmlException {
            return (AllNNI)XmlBeans.getContextTypeLoader().parse(sr, AllNNI.type, null);
        }
        
        public static AllNNI parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (AllNNI)XmlBeans.getContextTypeLoader().parse(sr, AllNNI.type, options);
        }
        
        public static AllNNI parse(final Node node) throws XmlException {
            return (AllNNI)XmlBeans.getContextTypeLoader().parse(node, AllNNI.type, null);
        }
        
        public static AllNNI parse(final Node node, final XmlOptions options) throws XmlException {
            return (AllNNI)XmlBeans.getContextTypeLoader().parse(node, AllNNI.type, options);
        }
        
        @Deprecated
        public static AllNNI parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (AllNNI)XmlBeans.getContextTypeLoader().parse(xis, AllNNI.type, null);
        }
        
        @Deprecated
        public static AllNNI parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (AllNNI)XmlBeans.getContextTypeLoader().parse(xis, AllNNI.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, AllNNI.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, AllNNI.type, options);
        }
        
        private Factory() {
        }
    }
}
