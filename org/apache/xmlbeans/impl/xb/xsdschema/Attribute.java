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
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlQName;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.SchemaType;

public interface Attribute extends Annotated
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Attribute.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("attribute83a9type");
    
    LocalSimpleType getSimpleType();
    
    boolean isSetSimpleType();
    
    void setSimpleType(final LocalSimpleType p0);
    
    LocalSimpleType addNewSimpleType();
    
    void unsetSimpleType();
    
    String getName();
    
    XmlNCName xgetName();
    
    boolean isSetName();
    
    void setName(final String p0);
    
    void xsetName(final XmlNCName p0);
    
    void unsetName();
    
    QName getRef();
    
    XmlQName xgetRef();
    
    boolean isSetRef();
    
    void setRef(final QName p0);
    
    void xsetRef(final XmlQName p0);
    
    void unsetRef();
    
    QName getType();
    
    XmlQName xgetType();
    
    boolean isSetType();
    
    void setType(final QName p0);
    
    void xsetType(final XmlQName p0);
    
    void unsetType();
    
    Use.Enum getUse();
    
    Use xgetUse();
    
    boolean isSetUse();
    
    void setUse(final Use.Enum p0);
    
    void xsetUse(final Use p0);
    
    void unsetUse();
    
    String getDefault();
    
    XmlString xgetDefault();
    
    boolean isSetDefault();
    
    void setDefault(final String p0);
    
    void xsetDefault(final XmlString p0);
    
    void unsetDefault();
    
    String getFixed();
    
    XmlString xgetFixed();
    
    boolean isSetFixed();
    
    void setFixed(final String p0);
    
    void xsetFixed(final XmlString p0);
    
    void unsetFixed();
    
    FormChoice.Enum getForm();
    
    FormChoice xgetForm();
    
    boolean isSetForm();
    
    void setForm(final FormChoice.Enum p0);
    
    void xsetForm(final FormChoice p0);
    
    void unsetForm();
    
    public interface Use extends XmlNMTOKEN
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Use.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("usea41aattrtype");
        public static final Enum PROHIBITED = Enum.forString("prohibited");
        public static final Enum OPTIONAL = Enum.forString("optional");
        public static final Enum REQUIRED = Enum.forString("required");
        public static final int INT_PROHIBITED = 1;
        public static final int INT_OPTIONAL = 2;
        public static final int INT_REQUIRED = 3;
        
        StringEnumAbstractBase enumValue();
        
        void set(final StringEnumAbstractBase p0);
        
        public static final class Enum extends StringEnumAbstractBase
        {
            static final int INT_PROHIBITED = 1;
            static final int INT_OPTIONAL = 2;
            static final int INT_REQUIRED = 3;
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
                table = new Table(new Enum[] { new Enum("prohibited", 1), new Enum("optional", 2), new Enum("required", 3) });
            }
        }
        
        public static final class Factory
        {
            public static Use newValue(final Object obj) {
                return (Use)Use.type.newValue(obj);
            }
            
            public static Use newInstance() {
                return (Use)XmlBeans.getContextTypeLoader().newInstance(Use.type, null);
            }
            
            public static Use newInstance(final XmlOptions options) {
                return (Use)XmlBeans.getContextTypeLoader().newInstance(Use.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static Attribute newInstance() {
            return (Attribute)XmlBeans.getContextTypeLoader().newInstance(Attribute.type, null);
        }
        
        public static Attribute newInstance(final XmlOptions options) {
            return (Attribute)XmlBeans.getContextTypeLoader().newInstance(Attribute.type, options);
        }
        
        public static Attribute parse(final String xmlAsString) throws XmlException {
            return (Attribute)XmlBeans.getContextTypeLoader().parse(xmlAsString, Attribute.type, null);
        }
        
        public static Attribute parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (Attribute)XmlBeans.getContextTypeLoader().parse(xmlAsString, Attribute.type, options);
        }
        
        public static Attribute parse(final File file) throws XmlException, IOException {
            return (Attribute)XmlBeans.getContextTypeLoader().parse(file, Attribute.type, null);
        }
        
        public static Attribute parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (Attribute)XmlBeans.getContextTypeLoader().parse(file, Attribute.type, options);
        }
        
        public static Attribute parse(final URL u) throws XmlException, IOException {
            return (Attribute)XmlBeans.getContextTypeLoader().parse(u, Attribute.type, null);
        }
        
        public static Attribute parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (Attribute)XmlBeans.getContextTypeLoader().parse(u, Attribute.type, options);
        }
        
        public static Attribute parse(final InputStream is) throws XmlException, IOException {
            return (Attribute)XmlBeans.getContextTypeLoader().parse(is, Attribute.type, null);
        }
        
        public static Attribute parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (Attribute)XmlBeans.getContextTypeLoader().parse(is, Attribute.type, options);
        }
        
        public static Attribute parse(final Reader r) throws XmlException, IOException {
            return (Attribute)XmlBeans.getContextTypeLoader().parse(r, Attribute.type, null);
        }
        
        public static Attribute parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (Attribute)XmlBeans.getContextTypeLoader().parse(r, Attribute.type, options);
        }
        
        public static Attribute parse(final XMLStreamReader sr) throws XmlException {
            return (Attribute)XmlBeans.getContextTypeLoader().parse(sr, Attribute.type, null);
        }
        
        public static Attribute parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (Attribute)XmlBeans.getContextTypeLoader().parse(sr, Attribute.type, options);
        }
        
        public static Attribute parse(final Node node) throws XmlException {
            return (Attribute)XmlBeans.getContextTypeLoader().parse(node, Attribute.type, null);
        }
        
        public static Attribute parse(final Node node, final XmlOptions options) throws XmlException {
            return (Attribute)XmlBeans.getContextTypeLoader().parse(node, Attribute.type, options);
        }
        
        @Deprecated
        public static Attribute parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (Attribute)XmlBeans.getContextTypeLoader().parse(xis, Attribute.type, null);
        }
        
        @Deprecated
        public static Attribute parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (Attribute)XmlBeans.getContextTypeLoader().parse(xis, Attribute.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Attribute.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Attribute.type, options);
        }
        
        private Factory() {
        }
    }
}
