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
import org.apache.xmlbeans.XmlBoolean;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTSheetView extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSheetView.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsheetview0f43type");
    
    CTPane getPane();
    
    boolean isSetPane();
    
    void setPane(final CTPane p0);
    
    CTPane addNewPane();
    
    void unsetPane();
    
    List<CTSelection> getSelectionList();
    
    @Deprecated
    CTSelection[] getSelectionArray();
    
    CTSelection getSelectionArray(final int p0);
    
    int sizeOfSelectionArray();
    
    void setSelectionArray(final CTSelection[] p0);
    
    void setSelectionArray(final int p0, final CTSelection p1);
    
    CTSelection insertNewSelection(final int p0);
    
    CTSelection addNewSelection();
    
    void removeSelection(final int p0);
    
    List<CTPivotSelection> getPivotSelectionList();
    
    @Deprecated
    CTPivotSelection[] getPivotSelectionArray();
    
    CTPivotSelection getPivotSelectionArray(final int p0);
    
    int sizeOfPivotSelectionArray();
    
    void setPivotSelectionArray(final CTPivotSelection[] p0);
    
    void setPivotSelectionArray(final int p0, final CTPivotSelection p1);
    
    CTPivotSelection insertNewPivotSelection(final int p0);
    
    CTPivotSelection addNewPivotSelection();
    
    void removePivotSelection(final int p0);
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    boolean getWindowProtection();
    
    XmlBoolean xgetWindowProtection();
    
    boolean isSetWindowProtection();
    
    void setWindowProtection(final boolean p0);
    
    void xsetWindowProtection(final XmlBoolean p0);
    
    void unsetWindowProtection();
    
    boolean getShowFormulas();
    
    XmlBoolean xgetShowFormulas();
    
    boolean isSetShowFormulas();
    
    void setShowFormulas(final boolean p0);
    
    void xsetShowFormulas(final XmlBoolean p0);
    
    void unsetShowFormulas();
    
    boolean getShowGridLines();
    
    XmlBoolean xgetShowGridLines();
    
    boolean isSetShowGridLines();
    
    void setShowGridLines(final boolean p0);
    
    void xsetShowGridLines(final XmlBoolean p0);
    
    void unsetShowGridLines();
    
    boolean getShowRowColHeaders();
    
    XmlBoolean xgetShowRowColHeaders();
    
    boolean isSetShowRowColHeaders();
    
    void setShowRowColHeaders(final boolean p0);
    
    void xsetShowRowColHeaders(final XmlBoolean p0);
    
    void unsetShowRowColHeaders();
    
    boolean getShowZeros();
    
    XmlBoolean xgetShowZeros();
    
    boolean isSetShowZeros();
    
    void setShowZeros(final boolean p0);
    
    void xsetShowZeros(final XmlBoolean p0);
    
    void unsetShowZeros();
    
    boolean getRightToLeft();
    
    XmlBoolean xgetRightToLeft();
    
    boolean isSetRightToLeft();
    
    void setRightToLeft(final boolean p0);
    
    void xsetRightToLeft(final XmlBoolean p0);
    
    void unsetRightToLeft();
    
    boolean getTabSelected();
    
    XmlBoolean xgetTabSelected();
    
    boolean isSetTabSelected();
    
    void setTabSelected(final boolean p0);
    
    void xsetTabSelected(final XmlBoolean p0);
    
    void unsetTabSelected();
    
    boolean getShowRuler();
    
    XmlBoolean xgetShowRuler();
    
    boolean isSetShowRuler();
    
    void setShowRuler(final boolean p0);
    
    void xsetShowRuler(final XmlBoolean p0);
    
    void unsetShowRuler();
    
    boolean getShowOutlineSymbols();
    
    XmlBoolean xgetShowOutlineSymbols();
    
    boolean isSetShowOutlineSymbols();
    
    void setShowOutlineSymbols(final boolean p0);
    
    void xsetShowOutlineSymbols(final XmlBoolean p0);
    
    void unsetShowOutlineSymbols();
    
    boolean getDefaultGridColor();
    
    XmlBoolean xgetDefaultGridColor();
    
    boolean isSetDefaultGridColor();
    
    void setDefaultGridColor(final boolean p0);
    
    void xsetDefaultGridColor(final XmlBoolean p0);
    
    void unsetDefaultGridColor();
    
    boolean getShowWhiteSpace();
    
    XmlBoolean xgetShowWhiteSpace();
    
    boolean isSetShowWhiteSpace();
    
    void setShowWhiteSpace(final boolean p0);
    
    void xsetShowWhiteSpace(final XmlBoolean p0);
    
    void unsetShowWhiteSpace();
    
    STSheetViewType.Enum getView();
    
    STSheetViewType xgetView();
    
    boolean isSetView();
    
    void setView(final STSheetViewType.Enum p0);
    
    void xsetView(final STSheetViewType p0);
    
    void unsetView();
    
    String getTopLeftCell();
    
    STCellRef xgetTopLeftCell();
    
    boolean isSetTopLeftCell();
    
    void setTopLeftCell(final String p0);
    
    void xsetTopLeftCell(final STCellRef p0);
    
    void unsetTopLeftCell();
    
    long getColorId();
    
    XmlUnsignedInt xgetColorId();
    
    boolean isSetColorId();
    
    void setColorId(final long p0);
    
    void xsetColorId(final XmlUnsignedInt p0);
    
    void unsetColorId();
    
    long getZoomScale();
    
    XmlUnsignedInt xgetZoomScale();
    
    boolean isSetZoomScale();
    
    void setZoomScale(final long p0);
    
    void xsetZoomScale(final XmlUnsignedInt p0);
    
    void unsetZoomScale();
    
    long getZoomScaleNormal();
    
    XmlUnsignedInt xgetZoomScaleNormal();
    
    boolean isSetZoomScaleNormal();
    
    void setZoomScaleNormal(final long p0);
    
    void xsetZoomScaleNormal(final XmlUnsignedInt p0);
    
    void unsetZoomScaleNormal();
    
    long getZoomScaleSheetLayoutView();
    
    XmlUnsignedInt xgetZoomScaleSheetLayoutView();
    
    boolean isSetZoomScaleSheetLayoutView();
    
    void setZoomScaleSheetLayoutView(final long p0);
    
    void xsetZoomScaleSheetLayoutView(final XmlUnsignedInt p0);
    
    void unsetZoomScaleSheetLayoutView();
    
    long getZoomScalePageLayoutView();
    
    XmlUnsignedInt xgetZoomScalePageLayoutView();
    
    boolean isSetZoomScalePageLayoutView();
    
    void setZoomScalePageLayoutView(final long p0);
    
    void xsetZoomScalePageLayoutView(final XmlUnsignedInt p0);
    
    void unsetZoomScalePageLayoutView();
    
    long getWorkbookViewId();
    
    XmlUnsignedInt xgetWorkbookViewId();
    
    void setWorkbookViewId(final long p0);
    
    void xsetWorkbookViewId(final XmlUnsignedInt p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSheetView.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSheetView newInstance() {
            return (CTSheetView)getTypeLoader().newInstance(CTSheetView.type, (XmlOptions)null);
        }
        
        public static CTSheetView newInstance(final XmlOptions xmlOptions) {
            return (CTSheetView)getTypeLoader().newInstance(CTSheetView.type, xmlOptions);
        }
        
        public static CTSheetView parse(final String s) throws XmlException {
            return (CTSheetView)getTypeLoader().parse(s, CTSheetView.type, (XmlOptions)null);
        }
        
        public static CTSheetView parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheetView)getTypeLoader().parse(s, CTSheetView.type, xmlOptions);
        }
        
        public static CTSheetView parse(final File file) throws XmlException, IOException {
            return (CTSheetView)getTypeLoader().parse(file, CTSheetView.type, (XmlOptions)null);
        }
        
        public static CTSheetView parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetView)getTypeLoader().parse(file, CTSheetView.type, xmlOptions);
        }
        
        public static CTSheetView parse(final URL url) throws XmlException, IOException {
            return (CTSheetView)getTypeLoader().parse(url, CTSheetView.type, (XmlOptions)null);
        }
        
        public static CTSheetView parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetView)getTypeLoader().parse(url, CTSheetView.type, xmlOptions);
        }
        
        public static CTSheetView parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSheetView)getTypeLoader().parse(inputStream, CTSheetView.type, (XmlOptions)null);
        }
        
        public static CTSheetView parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetView)getTypeLoader().parse(inputStream, CTSheetView.type, xmlOptions);
        }
        
        public static CTSheetView parse(final Reader reader) throws XmlException, IOException {
            return (CTSheetView)getTypeLoader().parse(reader, CTSheetView.type, (XmlOptions)null);
        }
        
        public static CTSheetView parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetView)getTypeLoader().parse(reader, CTSheetView.type, xmlOptions);
        }
        
        public static CTSheetView parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSheetView)getTypeLoader().parse(xmlStreamReader, CTSheetView.type, (XmlOptions)null);
        }
        
        public static CTSheetView parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheetView)getTypeLoader().parse(xmlStreamReader, CTSheetView.type, xmlOptions);
        }
        
        public static CTSheetView parse(final Node node) throws XmlException {
            return (CTSheetView)getTypeLoader().parse(node, CTSheetView.type, (XmlOptions)null);
        }
        
        public static CTSheetView parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheetView)getTypeLoader().parse(node, CTSheetView.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSheetView parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSheetView)getTypeLoader().parse(xmlInputStream, CTSheetView.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSheetView parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSheetView)getTypeLoader().parse(xmlInputStream, CTSheetView.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSheetView.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSheetView.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
