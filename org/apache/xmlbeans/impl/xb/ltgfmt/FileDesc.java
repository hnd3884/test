package org.apache.xmlbeans.impl.xb.ltgfmt;

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
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface FileDesc extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(FileDesc.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLTOOLS").resolveHandle("filedesc9392type");
    
    Code getCode();
    
    boolean isSetCode();
    
    void setCode(final Code p0);
    
    Code addNewCode();
    
    void unsetCode();
    
    String getTsDir();
    
    XmlToken xgetTsDir();
    
    boolean isSetTsDir();
    
    void setTsDir(final String p0);
    
    void xsetTsDir(final XmlToken p0);
    
    void unsetTsDir();
    
    String getFolder();
    
    XmlToken xgetFolder();
    
    boolean isSetFolder();
    
    void setFolder(final String p0);
    
    void xsetFolder(final XmlToken p0);
    
    void unsetFolder();
    
    String getFileName();
    
    XmlToken xgetFileName();
    
    boolean isSetFileName();
    
    void setFileName(final String p0);
    
    void xsetFileName(final XmlToken p0);
    
    void unsetFileName();
    
    Role.Enum getRole();
    
    Role xgetRole();
    
    boolean isSetRole();
    
    void setRole(final Role.Enum p0);
    
    void xsetRole(final Role p0);
    
    void unsetRole();
    
    boolean getValidity();
    
    XmlBoolean xgetValidity();
    
    boolean isSetValidity();
    
    void setValidity(final boolean p0);
    
    void xsetValidity(final XmlBoolean p0);
    
    void unsetValidity();
    
    public interface Role extends XmlToken
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Role.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLTOOLS").resolveHandle("role21a8attrtype");
        public static final Enum SCHEMA = Enum.forString("schema");
        public static final Enum INSTANCE = Enum.forString("instance");
        public static final Enum RESOURCE = Enum.forString("resource");
        public static final int INT_SCHEMA = 1;
        public static final int INT_INSTANCE = 2;
        public static final int INT_RESOURCE = 3;
        
        StringEnumAbstractBase enumValue();
        
        void set(final StringEnumAbstractBase p0);
        
        public static final class Enum extends StringEnumAbstractBase
        {
            static final int INT_SCHEMA = 1;
            static final int INT_INSTANCE = 2;
            static final int INT_RESOURCE = 3;
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
                table = new Table(new Enum[] { new Enum("schema", 1), new Enum("instance", 2), new Enum("resource", 3) });
            }
        }
        
        public static final class Factory
        {
            public static Role newValue(final Object obj) {
                return (Role)Role.type.newValue(obj);
            }
            
            public static Role newInstance() {
                return (Role)XmlBeans.getContextTypeLoader().newInstance(Role.type, null);
            }
            
            public static Role newInstance(final XmlOptions options) {
                return (Role)XmlBeans.getContextTypeLoader().newInstance(Role.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static FileDesc newInstance() {
            return (FileDesc)XmlBeans.getContextTypeLoader().newInstance(FileDesc.type, null);
        }
        
        public static FileDesc newInstance(final XmlOptions options) {
            return (FileDesc)XmlBeans.getContextTypeLoader().newInstance(FileDesc.type, options);
        }
        
        public static FileDesc parse(final String xmlAsString) throws XmlException {
            return (FileDesc)XmlBeans.getContextTypeLoader().parse(xmlAsString, FileDesc.type, null);
        }
        
        public static FileDesc parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (FileDesc)XmlBeans.getContextTypeLoader().parse(xmlAsString, FileDesc.type, options);
        }
        
        public static FileDesc parse(final File file) throws XmlException, IOException {
            return (FileDesc)XmlBeans.getContextTypeLoader().parse(file, FileDesc.type, null);
        }
        
        public static FileDesc parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (FileDesc)XmlBeans.getContextTypeLoader().parse(file, FileDesc.type, options);
        }
        
        public static FileDesc parse(final URL u) throws XmlException, IOException {
            return (FileDesc)XmlBeans.getContextTypeLoader().parse(u, FileDesc.type, null);
        }
        
        public static FileDesc parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (FileDesc)XmlBeans.getContextTypeLoader().parse(u, FileDesc.type, options);
        }
        
        public static FileDesc parse(final InputStream is) throws XmlException, IOException {
            return (FileDesc)XmlBeans.getContextTypeLoader().parse(is, FileDesc.type, null);
        }
        
        public static FileDesc parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (FileDesc)XmlBeans.getContextTypeLoader().parse(is, FileDesc.type, options);
        }
        
        public static FileDesc parse(final Reader r) throws XmlException, IOException {
            return (FileDesc)XmlBeans.getContextTypeLoader().parse(r, FileDesc.type, null);
        }
        
        public static FileDesc parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (FileDesc)XmlBeans.getContextTypeLoader().parse(r, FileDesc.type, options);
        }
        
        public static FileDesc parse(final XMLStreamReader sr) throws XmlException {
            return (FileDesc)XmlBeans.getContextTypeLoader().parse(sr, FileDesc.type, null);
        }
        
        public static FileDesc parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (FileDesc)XmlBeans.getContextTypeLoader().parse(sr, FileDesc.type, options);
        }
        
        public static FileDesc parse(final Node node) throws XmlException {
            return (FileDesc)XmlBeans.getContextTypeLoader().parse(node, FileDesc.type, null);
        }
        
        public static FileDesc parse(final Node node, final XmlOptions options) throws XmlException {
            return (FileDesc)XmlBeans.getContextTypeLoader().parse(node, FileDesc.type, options);
        }
        
        @Deprecated
        public static FileDesc parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (FileDesc)XmlBeans.getContextTypeLoader().parse(xis, FileDesc.type, null);
        }
        
        @Deprecated
        public static FileDesc parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (FileDesc)XmlBeans.getContextTypeLoader().parse(xis, FileDesc.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, FileDesc.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, FileDesc.type, options);
        }
        
        private Factory() {
        }
    }
}
