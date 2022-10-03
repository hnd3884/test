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
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTWorkbookPr extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTWorkbookPr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctworkbookpr03a5type");
    
    boolean getDate1904();
    
    XmlBoolean xgetDate1904();
    
    boolean isSetDate1904();
    
    void setDate1904(final boolean p0);
    
    void xsetDate1904(final XmlBoolean p0);
    
    void unsetDate1904();
    
    STObjects.Enum getShowObjects();
    
    STObjects xgetShowObjects();
    
    boolean isSetShowObjects();
    
    void setShowObjects(final STObjects.Enum p0);
    
    void xsetShowObjects(final STObjects p0);
    
    void unsetShowObjects();
    
    boolean getShowBorderUnselectedTables();
    
    XmlBoolean xgetShowBorderUnselectedTables();
    
    boolean isSetShowBorderUnselectedTables();
    
    void setShowBorderUnselectedTables(final boolean p0);
    
    void xsetShowBorderUnselectedTables(final XmlBoolean p0);
    
    void unsetShowBorderUnselectedTables();
    
    boolean getFilterPrivacy();
    
    XmlBoolean xgetFilterPrivacy();
    
    boolean isSetFilterPrivacy();
    
    void setFilterPrivacy(final boolean p0);
    
    void xsetFilterPrivacy(final XmlBoolean p0);
    
    void unsetFilterPrivacy();
    
    boolean getPromptedSolutions();
    
    XmlBoolean xgetPromptedSolutions();
    
    boolean isSetPromptedSolutions();
    
    void setPromptedSolutions(final boolean p0);
    
    void xsetPromptedSolutions(final XmlBoolean p0);
    
    void unsetPromptedSolutions();
    
    boolean getShowInkAnnotation();
    
    XmlBoolean xgetShowInkAnnotation();
    
    boolean isSetShowInkAnnotation();
    
    void setShowInkAnnotation(final boolean p0);
    
    void xsetShowInkAnnotation(final XmlBoolean p0);
    
    void unsetShowInkAnnotation();
    
    boolean getBackupFile();
    
    XmlBoolean xgetBackupFile();
    
    boolean isSetBackupFile();
    
    void setBackupFile(final boolean p0);
    
    void xsetBackupFile(final XmlBoolean p0);
    
    void unsetBackupFile();
    
    boolean getSaveExternalLinkValues();
    
    XmlBoolean xgetSaveExternalLinkValues();
    
    boolean isSetSaveExternalLinkValues();
    
    void setSaveExternalLinkValues(final boolean p0);
    
    void xsetSaveExternalLinkValues(final XmlBoolean p0);
    
    void unsetSaveExternalLinkValues();
    
    STUpdateLinks.Enum getUpdateLinks();
    
    STUpdateLinks xgetUpdateLinks();
    
    boolean isSetUpdateLinks();
    
    void setUpdateLinks(final STUpdateLinks.Enum p0);
    
    void xsetUpdateLinks(final STUpdateLinks p0);
    
    void unsetUpdateLinks();
    
    String getCodeName();
    
    XmlString xgetCodeName();
    
    boolean isSetCodeName();
    
    void setCodeName(final String p0);
    
    void xsetCodeName(final XmlString p0);
    
    void unsetCodeName();
    
    boolean getHidePivotFieldList();
    
    XmlBoolean xgetHidePivotFieldList();
    
    boolean isSetHidePivotFieldList();
    
    void setHidePivotFieldList(final boolean p0);
    
    void xsetHidePivotFieldList(final XmlBoolean p0);
    
    void unsetHidePivotFieldList();
    
    boolean getShowPivotChartFilter();
    
    XmlBoolean xgetShowPivotChartFilter();
    
    boolean isSetShowPivotChartFilter();
    
    void setShowPivotChartFilter(final boolean p0);
    
    void xsetShowPivotChartFilter(final XmlBoolean p0);
    
    void unsetShowPivotChartFilter();
    
    boolean getAllowRefreshQuery();
    
    XmlBoolean xgetAllowRefreshQuery();
    
    boolean isSetAllowRefreshQuery();
    
    void setAllowRefreshQuery(final boolean p0);
    
    void xsetAllowRefreshQuery(final XmlBoolean p0);
    
    void unsetAllowRefreshQuery();
    
    boolean getPublishItems();
    
    XmlBoolean xgetPublishItems();
    
    boolean isSetPublishItems();
    
    void setPublishItems(final boolean p0);
    
    void xsetPublishItems(final XmlBoolean p0);
    
    void unsetPublishItems();
    
    boolean getCheckCompatibility();
    
    XmlBoolean xgetCheckCompatibility();
    
    boolean isSetCheckCompatibility();
    
    void setCheckCompatibility(final boolean p0);
    
    void xsetCheckCompatibility(final XmlBoolean p0);
    
    void unsetCheckCompatibility();
    
    boolean getAutoCompressPictures();
    
    XmlBoolean xgetAutoCompressPictures();
    
    boolean isSetAutoCompressPictures();
    
    void setAutoCompressPictures(final boolean p0);
    
    void xsetAutoCompressPictures(final XmlBoolean p0);
    
    void unsetAutoCompressPictures();
    
    boolean getRefreshAllConnections();
    
    XmlBoolean xgetRefreshAllConnections();
    
    boolean isSetRefreshAllConnections();
    
    void setRefreshAllConnections(final boolean p0);
    
    void xsetRefreshAllConnections(final XmlBoolean p0);
    
    void unsetRefreshAllConnections();
    
    long getDefaultThemeVersion();
    
    XmlUnsignedInt xgetDefaultThemeVersion();
    
    boolean isSetDefaultThemeVersion();
    
    void setDefaultThemeVersion(final long p0);
    
    void xsetDefaultThemeVersion(final XmlUnsignedInt p0);
    
    void unsetDefaultThemeVersion();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTWorkbookPr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTWorkbookPr newInstance() {
            return (CTWorkbookPr)getTypeLoader().newInstance(CTWorkbookPr.type, (XmlOptions)null);
        }
        
        public static CTWorkbookPr newInstance(final XmlOptions xmlOptions) {
            return (CTWorkbookPr)getTypeLoader().newInstance(CTWorkbookPr.type, xmlOptions);
        }
        
        public static CTWorkbookPr parse(final String s) throws XmlException {
            return (CTWorkbookPr)getTypeLoader().parse(s, CTWorkbookPr.type, (XmlOptions)null);
        }
        
        public static CTWorkbookPr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTWorkbookPr)getTypeLoader().parse(s, CTWorkbookPr.type, xmlOptions);
        }
        
        public static CTWorkbookPr parse(final File file) throws XmlException, IOException {
            return (CTWorkbookPr)getTypeLoader().parse(file, CTWorkbookPr.type, (XmlOptions)null);
        }
        
        public static CTWorkbookPr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTWorkbookPr)getTypeLoader().parse(file, CTWorkbookPr.type, xmlOptions);
        }
        
        public static CTWorkbookPr parse(final URL url) throws XmlException, IOException {
            return (CTWorkbookPr)getTypeLoader().parse(url, CTWorkbookPr.type, (XmlOptions)null);
        }
        
        public static CTWorkbookPr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTWorkbookPr)getTypeLoader().parse(url, CTWorkbookPr.type, xmlOptions);
        }
        
        public static CTWorkbookPr parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTWorkbookPr)getTypeLoader().parse(inputStream, CTWorkbookPr.type, (XmlOptions)null);
        }
        
        public static CTWorkbookPr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTWorkbookPr)getTypeLoader().parse(inputStream, CTWorkbookPr.type, xmlOptions);
        }
        
        public static CTWorkbookPr parse(final Reader reader) throws XmlException, IOException {
            return (CTWorkbookPr)getTypeLoader().parse(reader, CTWorkbookPr.type, (XmlOptions)null);
        }
        
        public static CTWorkbookPr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTWorkbookPr)getTypeLoader().parse(reader, CTWorkbookPr.type, xmlOptions);
        }
        
        public static CTWorkbookPr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTWorkbookPr)getTypeLoader().parse(xmlStreamReader, CTWorkbookPr.type, (XmlOptions)null);
        }
        
        public static CTWorkbookPr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTWorkbookPr)getTypeLoader().parse(xmlStreamReader, CTWorkbookPr.type, xmlOptions);
        }
        
        public static CTWorkbookPr parse(final Node node) throws XmlException {
            return (CTWorkbookPr)getTypeLoader().parse(node, CTWorkbookPr.type, (XmlOptions)null);
        }
        
        public static CTWorkbookPr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTWorkbookPr)getTypeLoader().parse(node, CTWorkbookPr.type, xmlOptions);
        }
        
        @Deprecated
        public static CTWorkbookPr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTWorkbookPr)getTypeLoader().parse(xmlInputStream, CTWorkbookPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTWorkbookPr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTWorkbookPr)getTypeLoader().parse(xmlInputStream, CTWorkbookPr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTWorkbookPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTWorkbookPr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
