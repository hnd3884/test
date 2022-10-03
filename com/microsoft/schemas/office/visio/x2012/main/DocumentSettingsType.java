package com.microsoft.schemas.office.visio.x2012.main;

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
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface DocumentSettingsType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(DocumentSettingsType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("documentsettingstype945btype");
    
    GlueSettingsType getGlueSettings();
    
    boolean isSetGlueSettings();
    
    void setGlueSettings(final GlueSettingsType p0);
    
    GlueSettingsType addNewGlueSettings();
    
    void unsetGlueSettings();
    
    SnapSettingsType getSnapSettings();
    
    boolean isSetSnapSettings();
    
    void setSnapSettings(final SnapSettingsType p0);
    
    SnapSettingsType addNewSnapSettings();
    
    void unsetSnapSettings();
    
    SnapExtensionsType getSnapExtensions();
    
    boolean isSetSnapExtensions();
    
    void setSnapExtensions(final SnapExtensionsType p0);
    
    SnapExtensionsType addNewSnapExtensions();
    
    void unsetSnapExtensions();
    
    SnapAnglesType getSnapAngles();
    
    boolean isSetSnapAngles();
    
    void setSnapAngles(final SnapAnglesType p0);
    
    SnapAnglesType addNewSnapAngles();
    
    void unsetSnapAngles();
    
    DynamicGridEnabledType getDynamicGridEnabled();
    
    boolean isSetDynamicGridEnabled();
    
    void setDynamicGridEnabled(final DynamicGridEnabledType p0);
    
    DynamicGridEnabledType addNewDynamicGridEnabled();
    
    void unsetDynamicGridEnabled();
    
    ProtectStylesType getProtectStyles();
    
    boolean isSetProtectStyles();
    
    void setProtectStyles(final ProtectStylesType p0);
    
    ProtectStylesType addNewProtectStyles();
    
    void unsetProtectStyles();
    
    ProtectShapesType getProtectShapes();
    
    boolean isSetProtectShapes();
    
    void setProtectShapes(final ProtectShapesType p0);
    
    ProtectShapesType addNewProtectShapes();
    
    void unsetProtectShapes();
    
    ProtectMastersType getProtectMasters();
    
    boolean isSetProtectMasters();
    
    void setProtectMasters(final ProtectMastersType p0);
    
    ProtectMastersType addNewProtectMasters();
    
    void unsetProtectMasters();
    
    ProtectBkgndsType getProtectBkgnds();
    
    boolean isSetProtectBkgnds();
    
    void setProtectBkgnds(final ProtectBkgndsType p0);
    
    ProtectBkgndsType addNewProtectBkgnds();
    
    void unsetProtectBkgnds();
    
    CustomMenusFileType getCustomMenusFile();
    
    boolean isSetCustomMenusFile();
    
    void setCustomMenusFile(final CustomMenusFileType p0);
    
    CustomMenusFileType addNewCustomMenusFile();
    
    void unsetCustomMenusFile();
    
    CustomToolbarsFileType getCustomToolbarsFile();
    
    boolean isSetCustomToolbarsFile();
    
    void setCustomToolbarsFile(final CustomToolbarsFileType p0);
    
    CustomToolbarsFileType addNewCustomToolbarsFile();
    
    void unsetCustomToolbarsFile();
    
    AttachedToolbarsType getAttachedToolbars();
    
    boolean isSetAttachedToolbars();
    
    void setAttachedToolbars(final AttachedToolbarsType p0);
    
    AttachedToolbarsType addNewAttachedToolbars();
    
    void unsetAttachedToolbars();
    
    long getTopPage();
    
    XmlUnsignedInt xgetTopPage();
    
    boolean isSetTopPage();
    
    void setTopPage(final long p0);
    
    void xsetTopPage(final XmlUnsignedInt p0);
    
    void unsetTopPage();
    
    long getDefaultTextStyle();
    
    XmlUnsignedInt xgetDefaultTextStyle();
    
    boolean isSetDefaultTextStyle();
    
    void setDefaultTextStyle(final long p0);
    
    void xsetDefaultTextStyle(final XmlUnsignedInt p0);
    
    void unsetDefaultTextStyle();
    
    long getDefaultLineStyle();
    
    XmlUnsignedInt xgetDefaultLineStyle();
    
    boolean isSetDefaultLineStyle();
    
    void setDefaultLineStyle(final long p0);
    
    void xsetDefaultLineStyle(final XmlUnsignedInt p0);
    
    void unsetDefaultLineStyle();
    
    long getDefaultFillStyle();
    
    XmlUnsignedInt xgetDefaultFillStyle();
    
    boolean isSetDefaultFillStyle();
    
    void setDefaultFillStyle(final long p0);
    
    void xsetDefaultFillStyle(final XmlUnsignedInt p0);
    
    void unsetDefaultFillStyle();
    
    long getDefaultGuideStyle();
    
    XmlUnsignedInt xgetDefaultGuideStyle();
    
    boolean isSetDefaultGuideStyle();
    
    void setDefaultGuideStyle(final long p0);
    
    void xsetDefaultGuideStyle(final XmlUnsignedInt p0);
    
    void unsetDefaultGuideStyle();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(DocumentSettingsType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static DocumentSettingsType newInstance() {
            return (DocumentSettingsType)getTypeLoader().newInstance(DocumentSettingsType.type, (XmlOptions)null);
        }
        
        public static DocumentSettingsType newInstance(final XmlOptions xmlOptions) {
            return (DocumentSettingsType)getTypeLoader().newInstance(DocumentSettingsType.type, xmlOptions);
        }
        
        public static DocumentSettingsType parse(final String s) throws XmlException {
            return (DocumentSettingsType)getTypeLoader().parse(s, DocumentSettingsType.type, (XmlOptions)null);
        }
        
        public static DocumentSettingsType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (DocumentSettingsType)getTypeLoader().parse(s, DocumentSettingsType.type, xmlOptions);
        }
        
        public static DocumentSettingsType parse(final File file) throws XmlException, IOException {
            return (DocumentSettingsType)getTypeLoader().parse(file, DocumentSettingsType.type, (XmlOptions)null);
        }
        
        public static DocumentSettingsType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (DocumentSettingsType)getTypeLoader().parse(file, DocumentSettingsType.type, xmlOptions);
        }
        
        public static DocumentSettingsType parse(final URL url) throws XmlException, IOException {
            return (DocumentSettingsType)getTypeLoader().parse(url, DocumentSettingsType.type, (XmlOptions)null);
        }
        
        public static DocumentSettingsType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (DocumentSettingsType)getTypeLoader().parse(url, DocumentSettingsType.type, xmlOptions);
        }
        
        public static DocumentSettingsType parse(final InputStream inputStream) throws XmlException, IOException {
            return (DocumentSettingsType)getTypeLoader().parse(inputStream, DocumentSettingsType.type, (XmlOptions)null);
        }
        
        public static DocumentSettingsType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (DocumentSettingsType)getTypeLoader().parse(inputStream, DocumentSettingsType.type, xmlOptions);
        }
        
        public static DocumentSettingsType parse(final Reader reader) throws XmlException, IOException {
            return (DocumentSettingsType)getTypeLoader().parse(reader, DocumentSettingsType.type, (XmlOptions)null);
        }
        
        public static DocumentSettingsType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (DocumentSettingsType)getTypeLoader().parse(reader, DocumentSettingsType.type, xmlOptions);
        }
        
        public static DocumentSettingsType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (DocumentSettingsType)getTypeLoader().parse(xmlStreamReader, DocumentSettingsType.type, (XmlOptions)null);
        }
        
        public static DocumentSettingsType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (DocumentSettingsType)getTypeLoader().parse(xmlStreamReader, DocumentSettingsType.type, xmlOptions);
        }
        
        public static DocumentSettingsType parse(final Node node) throws XmlException {
            return (DocumentSettingsType)getTypeLoader().parse(node, DocumentSettingsType.type, (XmlOptions)null);
        }
        
        public static DocumentSettingsType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (DocumentSettingsType)getTypeLoader().parse(node, DocumentSettingsType.type, xmlOptions);
        }
        
        @Deprecated
        public static DocumentSettingsType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (DocumentSettingsType)getTypeLoader().parse(xmlInputStream, DocumentSettingsType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static DocumentSettingsType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (DocumentSettingsType)getTypeLoader().parse(xmlInputStream, DocumentSettingsType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, DocumentSettingsType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, DocumentSettingsType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
