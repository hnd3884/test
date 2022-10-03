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
import org.apache.xmlbeans.XmlAnySimpleType;
import java.util.List;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface UnionDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(UnionDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("union5866doctype");
    
    Union getUnion();
    
    void setUnion(final Union p0);
    
    Union addNewUnion();
    
    public interface Union extends Annotated
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Union.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("union498belemtype");
        
        LocalSimpleType[] getSimpleTypeArray();
        
        LocalSimpleType getSimpleTypeArray(final int p0);
        
        int sizeOfSimpleTypeArray();
        
        void setSimpleTypeArray(final LocalSimpleType[] p0);
        
        void setSimpleTypeArray(final int p0, final LocalSimpleType p1);
        
        LocalSimpleType insertNewSimpleType(final int p0);
        
        LocalSimpleType addNewSimpleType();
        
        void removeSimpleType(final int p0);
        
        List getMemberTypes();
        
        MemberTypes xgetMemberTypes();
        
        boolean isSetMemberTypes();
        
        void setMemberTypes(final List p0);
        
        void xsetMemberTypes(final MemberTypes p0);
        
        void unsetMemberTypes();
        
        public interface MemberTypes extends XmlAnySimpleType
        {
            public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(MemberTypes.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("membertypes2404attrtype");
            
            List getListValue();
            
            List xgetListValue();
            
            void setListValue(final List p0);
            
            @Deprecated
            List listValue();
            
            @Deprecated
            List xlistValue();
            
            @Deprecated
            void set(final List p0);
            
            public static final class Factory
            {
                public static MemberTypes newValue(final Object obj) {
                    return (MemberTypes)MemberTypes.type.newValue(obj);
                }
                
                public static MemberTypes newInstance() {
                    return (MemberTypes)XmlBeans.getContextTypeLoader().newInstance(MemberTypes.type, null);
                }
                
                public static MemberTypes newInstance(final XmlOptions options) {
                    return (MemberTypes)XmlBeans.getContextTypeLoader().newInstance(MemberTypes.type, options);
                }
                
                private Factory() {
                }
            }
        }
        
        public static final class Factory
        {
            public static Union newInstance() {
                return (Union)XmlBeans.getContextTypeLoader().newInstance(Union.type, null);
            }
            
            public static Union newInstance(final XmlOptions options) {
                return (Union)XmlBeans.getContextTypeLoader().newInstance(Union.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static UnionDocument newInstance() {
            return (UnionDocument)XmlBeans.getContextTypeLoader().newInstance(UnionDocument.type, null);
        }
        
        public static UnionDocument newInstance(final XmlOptions options) {
            return (UnionDocument)XmlBeans.getContextTypeLoader().newInstance(UnionDocument.type, options);
        }
        
        public static UnionDocument parse(final String xmlAsString) throws XmlException {
            return (UnionDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, UnionDocument.type, null);
        }
        
        public static UnionDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (UnionDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, UnionDocument.type, options);
        }
        
        public static UnionDocument parse(final File file) throws XmlException, IOException {
            return (UnionDocument)XmlBeans.getContextTypeLoader().parse(file, UnionDocument.type, null);
        }
        
        public static UnionDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (UnionDocument)XmlBeans.getContextTypeLoader().parse(file, UnionDocument.type, options);
        }
        
        public static UnionDocument parse(final URL u) throws XmlException, IOException {
            return (UnionDocument)XmlBeans.getContextTypeLoader().parse(u, UnionDocument.type, null);
        }
        
        public static UnionDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (UnionDocument)XmlBeans.getContextTypeLoader().parse(u, UnionDocument.type, options);
        }
        
        public static UnionDocument parse(final InputStream is) throws XmlException, IOException {
            return (UnionDocument)XmlBeans.getContextTypeLoader().parse(is, UnionDocument.type, null);
        }
        
        public static UnionDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (UnionDocument)XmlBeans.getContextTypeLoader().parse(is, UnionDocument.type, options);
        }
        
        public static UnionDocument parse(final Reader r) throws XmlException, IOException {
            return (UnionDocument)XmlBeans.getContextTypeLoader().parse(r, UnionDocument.type, null);
        }
        
        public static UnionDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (UnionDocument)XmlBeans.getContextTypeLoader().parse(r, UnionDocument.type, options);
        }
        
        public static UnionDocument parse(final XMLStreamReader sr) throws XmlException {
            return (UnionDocument)XmlBeans.getContextTypeLoader().parse(sr, UnionDocument.type, null);
        }
        
        public static UnionDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (UnionDocument)XmlBeans.getContextTypeLoader().parse(sr, UnionDocument.type, options);
        }
        
        public static UnionDocument parse(final Node node) throws XmlException {
            return (UnionDocument)XmlBeans.getContextTypeLoader().parse(node, UnionDocument.type, null);
        }
        
        public static UnionDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (UnionDocument)XmlBeans.getContextTypeLoader().parse(node, UnionDocument.type, options);
        }
        
        @Deprecated
        public static UnionDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (UnionDocument)XmlBeans.getContextTypeLoader().parse(xis, UnionDocument.type, null);
        }
        
        @Deprecated
        public static UnionDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (UnionDocument)XmlBeans.getContextTypeLoader().parse(xis, UnionDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, UnionDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, UnionDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
