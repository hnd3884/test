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
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTSheetPr extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSheetPr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsheetpr3ae0type");
    
    CTColor getTabColor();
    
    boolean isSetTabColor();
    
    void setTabColor(final CTColor p0);
    
    CTColor addNewTabColor();
    
    void unsetTabColor();
    
    CTOutlinePr getOutlinePr();
    
    boolean isSetOutlinePr();
    
    void setOutlinePr(final CTOutlinePr p0);
    
    CTOutlinePr addNewOutlinePr();
    
    void unsetOutlinePr();
    
    CTPageSetUpPr getPageSetUpPr();
    
    boolean isSetPageSetUpPr();
    
    void setPageSetUpPr(final CTPageSetUpPr p0);
    
    CTPageSetUpPr addNewPageSetUpPr();
    
    void unsetPageSetUpPr();
    
    boolean getSyncHorizontal();
    
    XmlBoolean xgetSyncHorizontal();
    
    boolean isSetSyncHorizontal();
    
    void setSyncHorizontal(final boolean p0);
    
    void xsetSyncHorizontal(final XmlBoolean p0);
    
    void unsetSyncHorizontal();
    
    boolean getSyncVertical();
    
    XmlBoolean xgetSyncVertical();
    
    boolean isSetSyncVertical();
    
    void setSyncVertical(final boolean p0);
    
    void xsetSyncVertical(final XmlBoolean p0);
    
    void unsetSyncVertical();
    
    String getSyncRef();
    
    STRef xgetSyncRef();
    
    boolean isSetSyncRef();
    
    void setSyncRef(final String p0);
    
    void xsetSyncRef(final STRef p0);
    
    void unsetSyncRef();
    
    boolean getTransitionEvaluation();
    
    XmlBoolean xgetTransitionEvaluation();
    
    boolean isSetTransitionEvaluation();
    
    void setTransitionEvaluation(final boolean p0);
    
    void xsetTransitionEvaluation(final XmlBoolean p0);
    
    void unsetTransitionEvaluation();
    
    boolean getTransitionEntry();
    
    XmlBoolean xgetTransitionEntry();
    
    boolean isSetTransitionEntry();
    
    void setTransitionEntry(final boolean p0);
    
    void xsetTransitionEntry(final XmlBoolean p0);
    
    void unsetTransitionEntry();
    
    boolean getPublished();
    
    XmlBoolean xgetPublished();
    
    boolean isSetPublished();
    
    void setPublished(final boolean p0);
    
    void xsetPublished(final XmlBoolean p0);
    
    void unsetPublished();
    
    String getCodeName();
    
    XmlString xgetCodeName();
    
    boolean isSetCodeName();
    
    void setCodeName(final String p0);
    
    void xsetCodeName(final XmlString p0);
    
    void unsetCodeName();
    
    boolean getFilterMode();
    
    XmlBoolean xgetFilterMode();
    
    boolean isSetFilterMode();
    
    void setFilterMode(final boolean p0);
    
    void xsetFilterMode(final XmlBoolean p0);
    
    void unsetFilterMode();
    
    boolean getEnableFormatConditionsCalculation();
    
    XmlBoolean xgetEnableFormatConditionsCalculation();
    
    boolean isSetEnableFormatConditionsCalculation();
    
    void setEnableFormatConditionsCalculation(final boolean p0);
    
    void xsetEnableFormatConditionsCalculation(final XmlBoolean p0);
    
    void unsetEnableFormatConditionsCalculation();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSheetPr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSheetPr newInstance() {
            return (CTSheetPr)getTypeLoader().newInstance(CTSheetPr.type, (XmlOptions)null);
        }
        
        public static CTSheetPr newInstance(final XmlOptions xmlOptions) {
            return (CTSheetPr)getTypeLoader().newInstance(CTSheetPr.type, xmlOptions);
        }
        
        public static CTSheetPr parse(final String s) throws XmlException {
            return (CTSheetPr)getTypeLoader().parse(s, CTSheetPr.type, (XmlOptions)null);
        }
        
        public static CTSheetPr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheetPr)getTypeLoader().parse(s, CTSheetPr.type, xmlOptions);
        }
        
        public static CTSheetPr parse(final File file) throws XmlException, IOException {
            return (CTSheetPr)getTypeLoader().parse(file, CTSheetPr.type, (XmlOptions)null);
        }
        
        public static CTSheetPr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetPr)getTypeLoader().parse(file, CTSheetPr.type, xmlOptions);
        }
        
        public static CTSheetPr parse(final URL url) throws XmlException, IOException {
            return (CTSheetPr)getTypeLoader().parse(url, CTSheetPr.type, (XmlOptions)null);
        }
        
        public static CTSheetPr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetPr)getTypeLoader().parse(url, CTSheetPr.type, xmlOptions);
        }
        
        public static CTSheetPr parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSheetPr)getTypeLoader().parse(inputStream, CTSheetPr.type, (XmlOptions)null);
        }
        
        public static CTSheetPr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetPr)getTypeLoader().parse(inputStream, CTSheetPr.type, xmlOptions);
        }
        
        public static CTSheetPr parse(final Reader reader) throws XmlException, IOException {
            return (CTSheetPr)getTypeLoader().parse(reader, CTSheetPr.type, (XmlOptions)null);
        }
        
        public static CTSheetPr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetPr)getTypeLoader().parse(reader, CTSheetPr.type, xmlOptions);
        }
        
        public static CTSheetPr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSheetPr)getTypeLoader().parse(xmlStreamReader, CTSheetPr.type, (XmlOptions)null);
        }
        
        public static CTSheetPr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheetPr)getTypeLoader().parse(xmlStreamReader, CTSheetPr.type, xmlOptions);
        }
        
        public static CTSheetPr parse(final Node node) throws XmlException {
            return (CTSheetPr)getTypeLoader().parse(node, CTSheetPr.type, (XmlOptions)null);
        }
        
        public static CTSheetPr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheetPr)getTypeLoader().parse(node, CTSheetPr.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSheetPr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSheetPr)getTypeLoader().parse(xmlInputStream, CTSheetPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSheetPr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSheetPr)getTypeLoader().parse(xmlInputStream, CTSheetPr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSheetPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSheetPr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
