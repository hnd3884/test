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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTWorkbook extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTWorkbook.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctworkbook83c3type");
    
    CTFileVersion getFileVersion();
    
    boolean isSetFileVersion();
    
    void setFileVersion(final CTFileVersion p0);
    
    CTFileVersion addNewFileVersion();
    
    void unsetFileVersion();
    
    CTFileSharing getFileSharing();
    
    boolean isSetFileSharing();
    
    void setFileSharing(final CTFileSharing p0);
    
    CTFileSharing addNewFileSharing();
    
    void unsetFileSharing();
    
    CTWorkbookPr getWorkbookPr();
    
    boolean isSetWorkbookPr();
    
    void setWorkbookPr(final CTWorkbookPr p0);
    
    CTWorkbookPr addNewWorkbookPr();
    
    void unsetWorkbookPr();
    
    CTWorkbookProtection getWorkbookProtection();
    
    boolean isSetWorkbookProtection();
    
    void setWorkbookProtection(final CTWorkbookProtection p0);
    
    CTWorkbookProtection addNewWorkbookProtection();
    
    void unsetWorkbookProtection();
    
    CTBookViews getBookViews();
    
    boolean isSetBookViews();
    
    void setBookViews(final CTBookViews p0);
    
    CTBookViews addNewBookViews();
    
    void unsetBookViews();
    
    CTSheets getSheets();
    
    void setSheets(final CTSheets p0);
    
    CTSheets addNewSheets();
    
    CTFunctionGroups getFunctionGroups();
    
    boolean isSetFunctionGroups();
    
    void setFunctionGroups(final CTFunctionGroups p0);
    
    CTFunctionGroups addNewFunctionGroups();
    
    void unsetFunctionGroups();
    
    CTExternalReferences getExternalReferences();
    
    boolean isSetExternalReferences();
    
    void setExternalReferences(final CTExternalReferences p0);
    
    CTExternalReferences addNewExternalReferences();
    
    void unsetExternalReferences();
    
    CTDefinedNames getDefinedNames();
    
    boolean isSetDefinedNames();
    
    void setDefinedNames(final CTDefinedNames p0);
    
    CTDefinedNames addNewDefinedNames();
    
    void unsetDefinedNames();
    
    CTCalcPr getCalcPr();
    
    boolean isSetCalcPr();
    
    void setCalcPr(final CTCalcPr p0);
    
    CTCalcPr addNewCalcPr();
    
    void unsetCalcPr();
    
    CTOleSize getOleSize();
    
    boolean isSetOleSize();
    
    void setOleSize(final CTOleSize p0);
    
    CTOleSize addNewOleSize();
    
    void unsetOleSize();
    
    CTCustomWorkbookViews getCustomWorkbookViews();
    
    boolean isSetCustomWorkbookViews();
    
    void setCustomWorkbookViews(final CTCustomWorkbookViews p0);
    
    CTCustomWorkbookViews addNewCustomWorkbookViews();
    
    void unsetCustomWorkbookViews();
    
    CTPivotCaches getPivotCaches();
    
    boolean isSetPivotCaches();
    
    void setPivotCaches(final CTPivotCaches p0);
    
    CTPivotCaches addNewPivotCaches();
    
    void unsetPivotCaches();
    
    CTSmartTagPr getSmartTagPr();
    
    boolean isSetSmartTagPr();
    
    void setSmartTagPr(final CTSmartTagPr p0);
    
    CTSmartTagPr addNewSmartTagPr();
    
    void unsetSmartTagPr();
    
    CTSmartTagTypes getSmartTagTypes();
    
    boolean isSetSmartTagTypes();
    
    void setSmartTagTypes(final CTSmartTagTypes p0);
    
    CTSmartTagTypes addNewSmartTagTypes();
    
    void unsetSmartTagTypes();
    
    CTWebPublishing getWebPublishing();
    
    boolean isSetWebPublishing();
    
    void setWebPublishing(final CTWebPublishing p0);
    
    CTWebPublishing addNewWebPublishing();
    
    void unsetWebPublishing();
    
    List<CTFileRecoveryPr> getFileRecoveryPrList();
    
    @Deprecated
    CTFileRecoveryPr[] getFileRecoveryPrArray();
    
    CTFileRecoveryPr getFileRecoveryPrArray(final int p0);
    
    int sizeOfFileRecoveryPrArray();
    
    void setFileRecoveryPrArray(final CTFileRecoveryPr[] p0);
    
    void setFileRecoveryPrArray(final int p0, final CTFileRecoveryPr p1);
    
    CTFileRecoveryPr insertNewFileRecoveryPr(final int p0);
    
    CTFileRecoveryPr addNewFileRecoveryPr();
    
    void removeFileRecoveryPr(final int p0);
    
    CTWebPublishObjects getWebPublishObjects();
    
    boolean isSetWebPublishObjects();
    
    void setWebPublishObjects(final CTWebPublishObjects p0);
    
    CTWebPublishObjects addNewWebPublishObjects();
    
    void unsetWebPublishObjects();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTWorkbook.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTWorkbook newInstance() {
            return (CTWorkbook)getTypeLoader().newInstance(CTWorkbook.type, (XmlOptions)null);
        }
        
        public static CTWorkbook newInstance(final XmlOptions xmlOptions) {
            return (CTWorkbook)getTypeLoader().newInstance(CTWorkbook.type, xmlOptions);
        }
        
        public static CTWorkbook parse(final String s) throws XmlException {
            return (CTWorkbook)getTypeLoader().parse(s, CTWorkbook.type, (XmlOptions)null);
        }
        
        public static CTWorkbook parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTWorkbook)getTypeLoader().parse(s, CTWorkbook.type, xmlOptions);
        }
        
        public static CTWorkbook parse(final File file) throws XmlException, IOException {
            return (CTWorkbook)getTypeLoader().parse(file, CTWorkbook.type, (XmlOptions)null);
        }
        
        public static CTWorkbook parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTWorkbook)getTypeLoader().parse(file, CTWorkbook.type, xmlOptions);
        }
        
        public static CTWorkbook parse(final URL url) throws XmlException, IOException {
            return (CTWorkbook)getTypeLoader().parse(url, CTWorkbook.type, (XmlOptions)null);
        }
        
        public static CTWorkbook parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTWorkbook)getTypeLoader().parse(url, CTWorkbook.type, xmlOptions);
        }
        
        public static CTWorkbook parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTWorkbook)getTypeLoader().parse(inputStream, CTWorkbook.type, (XmlOptions)null);
        }
        
        public static CTWorkbook parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTWorkbook)getTypeLoader().parse(inputStream, CTWorkbook.type, xmlOptions);
        }
        
        public static CTWorkbook parse(final Reader reader) throws XmlException, IOException {
            return (CTWorkbook)getTypeLoader().parse(reader, CTWorkbook.type, (XmlOptions)null);
        }
        
        public static CTWorkbook parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTWorkbook)getTypeLoader().parse(reader, CTWorkbook.type, xmlOptions);
        }
        
        public static CTWorkbook parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTWorkbook)getTypeLoader().parse(xmlStreamReader, CTWorkbook.type, (XmlOptions)null);
        }
        
        public static CTWorkbook parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTWorkbook)getTypeLoader().parse(xmlStreamReader, CTWorkbook.type, xmlOptions);
        }
        
        public static CTWorkbook parse(final Node node) throws XmlException {
            return (CTWorkbook)getTypeLoader().parse(node, CTWorkbook.type, (XmlOptions)null);
        }
        
        public static CTWorkbook parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTWorkbook)getTypeLoader().parse(node, CTWorkbook.type, xmlOptions);
        }
        
        @Deprecated
        public static CTWorkbook parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTWorkbook)getTypeLoader().parse(xmlInputStream, CTWorkbook.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTWorkbook parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTWorkbook)getTypeLoader().parse(xmlInputStream, CTWorkbook.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTWorkbook.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTWorkbook.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
