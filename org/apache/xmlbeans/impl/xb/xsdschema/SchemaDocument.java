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
import org.apache.xmlbeans.XmlLanguage;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface SchemaDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SchemaDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("schema0782doctype");
    
    Schema getSchema();
    
    void setSchema(final Schema p0);
    
    Schema addNewSchema();
    
    public interface Schema extends OpenAttrs
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Schema.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("schemad77felemtype");
        
        IncludeDocument.Include[] getIncludeArray();
        
        IncludeDocument.Include getIncludeArray(final int p0);
        
        int sizeOfIncludeArray();
        
        void setIncludeArray(final IncludeDocument.Include[] p0);
        
        void setIncludeArray(final int p0, final IncludeDocument.Include p1);
        
        IncludeDocument.Include insertNewInclude(final int p0);
        
        IncludeDocument.Include addNewInclude();
        
        void removeInclude(final int p0);
        
        ImportDocument.Import[] getImportArray();
        
        ImportDocument.Import getImportArray(final int p0);
        
        int sizeOfImportArray();
        
        void setImportArray(final ImportDocument.Import[] p0);
        
        void setImportArray(final int p0, final ImportDocument.Import p1);
        
        ImportDocument.Import insertNewImport(final int p0);
        
        ImportDocument.Import addNewImport();
        
        void removeImport(final int p0);
        
        RedefineDocument.Redefine[] getRedefineArray();
        
        RedefineDocument.Redefine getRedefineArray(final int p0);
        
        int sizeOfRedefineArray();
        
        void setRedefineArray(final RedefineDocument.Redefine[] p0);
        
        void setRedefineArray(final int p0, final RedefineDocument.Redefine p1);
        
        RedefineDocument.Redefine insertNewRedefine(final int p0);
        
        RedefineDocument.Redefine addNewRedefine();
        
        void removeRedefine(final int p0);
        
        AnnotationDocument.Annotation[] getAnnotationArray();
        
        AnnotationDocument.Annotation getAnnotationArray(final int p0);
        
        int sizeOfAnnotationArray();
        
        void setAnnotationArray(final AnnotationDocument.Annotation[] p0);
        
        void setAnnotationArray(final int p0, final AnnotationDocument.Annotation p1);
        
        AnnotationDocument.Annotation insertNewAnnotation(final int p0);
        
        AnnotationDocument.Annotation addNewAnnotation();
        
        void removeAnnotation(final int p0);
        
        TopLevelSimpleType[] getSimpleTypeArray();
        
        TopLevelSimpleType getSimpleTypeArray(final int p0);
        
        int sizeOfSimpleTypeArray();
        
        void setSimpleTypeArray(final TopLevelSimpleType[] p0);
        
        void setSimpleTypeArray(final int p0, final TopLevelSimpleType p1);
        
        TopLevelSimpleType insertNewSimpleType(final int p0);
        
        TopLevelSimpleType addNewSimpleType();
        
        void removeSimpleType(final int p0);
        
        TopLevelComplexType[] getComplexTypeArray();
        
        TopLevelComplexType getComplexTypeArray(final int p0);
        
        int sizeOfComplexTypeArray();
        
        void setComplexTypeArray(final TopLevelComplexType[] p0);
        
        void setComplexTypeArray(final int p0, final TopLevelComplexType p1);
        
        TopLevelComplexType insertNewComplexType(final int p0);
        
        TopLevelComplexType addNewComplexType();
        
        void removeComplexType(final int p0);
        
        NamedGroup[] getGroupArray();
        
        NamedGroup getGroupArray(final int p0);
        
        int sizeOfGroupArray();
        
        void setGroupArray(final NamedGroup[] p0);
        
        void setGroupArray(final int p0, final NamedGroup p1);
        
        NamedGroup insertNewGroup(final int p0);
        
        NamedGroup addNewGroup();
        
        void removeGroup(final int p0);
        
        NamedAttributeGroup[] getAttributeGroupArray();
        
        NamedAttributeGroup getAttributeGroupArray(final int p0);
        
        int sizeOfAttributeGroupArray();
        
        void setAttributeGroupArray(final NamedAttributeGroup[] p0);
        
        void setAttributeGroupArray(final int p0, final NamedAttributeGroup p1);
        
        NamedAttributeGroup insertNewAttributeGroup(final int p0);
        
        NamedAttributeGroup addNewAttributeGroup();
        
        void removeAttributeGroup(final int p0);
        
        TopLevelElement[] getElementArray();
        
        TopLevelElement getElementArray(final int p0);
        
        int sizeOfElementArray();
        
        void setElementArray(final TopLevelElement[] p0);
        
        void setElementArray(final int p0, final TopLevelElement p1);
        
        TopLevelElement insertNewElement(final int p0);
        
        TopLevelElement addNewElement();
        
        void removeElement(final int p0);
        
        TopLevelAttribute[] getAttributeArray();
        
        TopLevelAttribute getAttributeArray(final int p0);
        
        int sizeOfAttributeArray();
        
        void setAttributeArray(final TopLevelAttribute[] p0);
        
        void setAttributeArray(final int p0, final TopLevelAttribute p1);
        
        TopLevelAttribute insertNewAttribute(final int p0);
        
        TopLevelAttribute addNewAttribute();
        
        void removeAttribute(final int p0);
        
        NotationDocument.Notation[] getNotationArray();
        
        NotationDocument.Notation getNotationArray(final int p0);
        
        int sizeOfNotationArray();
        
        void setNotationArray(final NotationDocument.Notation[] p0);
        
        void setNotationArray(final int p0, final NotationDocument.Notation p1);
        
        NotationDocument.Notation insertNewNotation(final int p0);
        
        NotationDocument.Notation addNewNotation();
        
        void removeNotation(final int p0);
        
        String getTargetNamespace();
        
        XmlAnyURI xgetTargetNamespace();
        
        boolean isSetTargetNamespace();
        
        void setTargetNamespace(final String p0);
        
        void xsetTargetNamespace(final XmlAnyURI p0);
        
        void unsetTargetNamespace();
        
        String getVersion();
        
        XmlToken xgetVersion();
        
        boolean isSetVersion();
        
        void setVersion(final String p0);
        
        void xsetVersion(final XmlToken p0);
        
        void unsetVersion();
        
        Object getFinalDefault();
        
        FullDerivationSet xgetFinalDefault();
        
        boolean isSetFinalDefault();
        
        void setFinalDefault(final Object p0);
        
        void xsetFinalDefault(final FullDerivationSet p0);
        
        void unsetFinalDefault();
        
        Object getBlockDefault();
        
        BlockSet xgetBlockDefault();
        
        boolean isSetBlockDefault();
        
        void setBlockDefault(final Object p0);
        
        void xsetBlockDefault(final BlockSet p0);
        
        void unsetBlockDefault();
        
        FormChoice.Enum getAttributeFormDefault();
        
        FormChoice xgetAttributeFormDefault();
        
        boolean isSetAttributeFormDefault();
        
        void setAttributeFormDefault(final FormChoice.Enum p0);
        
        void xsetAttributeFormDefault(final FormChoice p0);
        
        void unsetAttributeFormDefault();
        
        FormChoice.Enum getElementFormDefault();
        
        FormChoice xgetElementFormDefault();
        
        boolean isSetElementFormDefault();
        
        void setElementFormDefault(final FormChoice.Enum p0);
        
        void xsetElementFormDefault(final FormChoice p0);
        
        void unsetElementFormDefault();
        
        String getId();
        
        XmlID xgetId();
        
        boolean isSetId();
        
        void setId(final String p0);
        
        void xsetId(final XmlID p0);
        
        void unsetId();
        
        String getLang();
        
        XmlLanguage xgetLang();
        
        boolean isSetLang();
        
        void setLang(final String p0);
        
        void xsetLang(final XmlLanguage p0);
        
        void unsetLang();
        
        public static final class Factory
        {
            public static Schema newInstance() {
                return (Schema)XmlBeans.getContextTypeLoader().newInstance(Schema.type, null);
            }
            
            public static Schema newInstance(final XmlOptions options) {
                return (Schema)XmlBeans.getContextTypeLoader().newInstance(Schema.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static SchemaDocument newInstance() {
            return (SchemaDocument)XmlBeans.getContextTypeLoader().newInstance(SchemaDocument.type, null);
        }
        
        public static SchemaDocument newInstance(final XmlOptions options) {
            return (SchemaDocument)XmlBeans.getContextTypeLoader().newInstance(SchemaDocument.type, options);
        }
        
        public static SchemaDocument parse(final String xmlAsString) throws XmlException {
            return (SchemaDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, SchemaDocument.type, null);
        }
        
        public static SchemaDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (SchemaDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, SchemaDocument.type, options);
        }
        
        public static SchemaDocument parse(final File file) throws XmlException, IOException {
            return (SchemaDocument)XmlBeans.getContextTypeLoader().parse(file, SchemaDocument.type, null);
        }
        
        public static SchemaDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (SchemaDocument)XmlBeans.getContextTypeLoader().parse(file, SchemaDocument.type, options);
        }
        
        public static SchemaDocument parse(final URL u) throws XmlException, IOException {
            return (SchemaDocument)XmlBeans.getContextTypeLoader().parse(u, SchemaDocument.type, null);
        }
        
        public static SchemaDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (SchemaDocument)XmlBeans.getContextTypeLoader().parse(u, SchemaDocument.type, options);
        }
        
        public static SchemaDocument parse(final InputStream is) throws XmlException, IOException {
            return (SchemaDocument)XmlBeans.getContextTypeLoader().parse(is, SchemaDocument.type, null);
        }
        
        public static SchemaDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (SchemaDocument)XmlBeans.getContextTypeLoader().parse(is, SchemaDocument.type, options);
        }
        
        public static SchemaDocument parse(final Reader r) throws XmlException, IOException {
            return (SchemaDocument)XmlBeans.getContextTypeLoader().parse(r, SchemaDocument.type, null);
        }
        
        public static SchemaDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (SchemaDocument)XmlBeans.getContextTypeLoader().parse(r, SchemaDocument.type, options);
        }
        
        public static SchemaDocument parse(final XMLStreamReader sr) throws XmlException {
            return (SchemaDocument)XmlBeans.getContextTypeLoader().parse(sr, SchemaDocument.type, null);
        }
        
        public static SchemaDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (SchemaDocument)XmlBeans.getContextTypeLoader().parse(sr, SchemaDocument.type, options);
        }
        
        public static SchemaDocument parse(final Node node) throws XmlException {
            return (SchemaDocument)XmlBeans.getContextTypeLoader().parse(node, SchemaDocument.type, null);
        }
        
        public static SchemaDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (SchemaDocument)XmlBeans.getContextTypeLoader().parse(node, SchemaDocument.type, options);
        }
        
        @Deprecated
        public static SchemaDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (SchemaDocument)XmlBeans.getContextTypeLoader().parse(xis, SchemaDocument.type, null);
        }
        
        @Deprecated
        public static SchemaDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (SchemaDocument)XmlBeans.getContextTypeLoader().parse(xis, SchemaDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, SchemaDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, SchemaDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
