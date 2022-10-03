package org.openxmlformats.schemas.drawingml.x2006.chart;

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
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTDLbls extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDLbls.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctdlblsb585type");
    
    List<CTDLbl> getDLblList();
    
    @Deprecated
    CTDLbl[] getDLblArray();
    
    CTDLbl getDLblArray(final int p0);
    
    int sizeOfDLblArray();
    
    void setDLblArray(final CTDLbl[] p0);
    
    void setDLblArray(final int p0, final CTDLbl p1);
    
    CTDLbl insertNewDLbl(final int p0);
    
    CTDLbl addNewDLbl();
    
    void removeDLbl(final int p0);
    
    CTBoolean getDelete();
    
    boolean isSetDelete();
    
    void setDelete(final CTBoolean p0);
    
    CTBoolean addNewDelete();
    
    void unsetDelete();
    
    CTNumFmt getNumFmt();
    
    boolean isSetNumFmt();
    
    void setNumFmt(final CTNumFmt p0);
    
    CTNumFmt addNewNumFmt();
    
    void unsetNumFmt();
    
    CTShapeProperties getSpPr();
    
    boolean isSetSpPr();
    
    void setSpPr(final CTShapeProperties p0);
    
    CTShapeProperties addNewSpPr();
    
    void unsetSpPr();
    
    CTTextBody getTxPr();
    
    boolean isSetTxPr();
    
    void setTxPr(final CTTextBody p0);
    
    CTTextBody addNewTxPr();
    
    void unsetTxPr();
    
    CTDLblPos getDLblPos();
    
    boolean isSetDLblPos();
    
    void setDLblPos(final CTDLblPos p0);
    
    CTDLblPos addNewDLblPos();
    
    void unsetDLblPos();
    
    CTBoolean getShowLegendKey();
    
    boolean isSetShowLegendKey();
    
    void setShowLegendKey(final CTBoolean p0);
    
    CTBoolean addNewShowLegendKey();
    
    void unsetShowLegendKey();
    
    CTBoolean getShowVal();
    
    boolean isSetShowVal();
    
    void setShowVal(final CTBoolean p0);
    
    CTBoolean addNewShowVal();
    
    void unsetShowVal();
    
    CTBoolean getShowCatName();
    
    boolean isSetShowCatName();
    
    void setShowCatName(final CTBoolean p0);
    
    CTBoolean addNewShowCatName();
    
    void unsetShowCatName();
    
    CTBoolean getShowSerName();
    
    boolean isSetShowSerName();
    
    void setShowSerName(final CTBoolean p0);
    
    CTBoolean addNewShowSerName();
    
    void unsetShowSerName();
    
    CTBoolean getShowPercent();
    
    boolean isSetShowPercent();
    
    void setShowPercent(final CTBoolean p0);
    
    CTBoolean addNewShowPercent();
    
    void unsetShowPercent();
    
    CTBoolean getShowBubbleSize();
    
    boolean isSetShowBubbleSize();
    
    void setShowBubbleSize(final CTBoolean p0);
    
    CTBoolean addNewShowBubbleSize();
    
    void unsetShowBubbleSize();
    
    String getSeparator();
    
    XmlString xgetSeparator();
    
    boolean isSetSeparator();
    
    void setSeparator(final String p0);
    
    void xsetSeparator(final XmlString p0);
    
    void unsetSeparator();
    
    CTBoolean getShowLeaderLines();
    
    boolean isSetShowLeaderLines();
    
    void setShowLeaderLines(final CTBoolean p0);
    
    CTBoolean addNewShowLeaderLines();
    
    void unsetShowLeaderLines();
    
    CTChartLines getLeaderLines();
    
    boolean isSetLeaderLines();
    
    void setLeaderLines(final CTChartLines p0);
    
    CTChartLines addNewLeaderLines();
    
    void unsetLeaderLines();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDLbls.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDLbls newInstance() {
            return (CTDLbls)getTypeLoader().newInstance(CTDLbls.type, (XmlOptions)null);
        }
        
        public static CTDLbls newInstance(final XmlOptions xmlOptions) {
            return (CTDLbls)getTypeLoader().newInstance(CTDLbls.type, xmlOptions);
        }
        
        public static CTDLbls parse(final String s) throws XmlException {
            return (CTDLbls)getTypeLoader().parse(s, CTDLbls.type, (XmlOptions)null);
        }
        
        public static CTDLbls parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDLbls)getTypeLoader().parse(s, CTDLbls.type, xmlOptions);
        }
        
        public static CTDLbls parse(final File file) throws XmlException, IOException {
            return (CTDLbls)getTypeLoader().parse(file, CTDLbls.type, (XmlOptions)null);
        }
        
        public static CTDLbls parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDLbls)getTypeLoader().parse(file, CTDLbls.type, xmlOptions);
        }
        
        public static CTDLbls parse(final URL url) throws XmlException, IOException {
            return (CTDLbls)getTypeLoader().parse(url, CTDLbls.type, (XmlOptions)null);
        }
        
        public static CTDLbls parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDLbls)getTypeLoader().parse(url, CTDLbls.type, xmlOptions);
        }
        
        public static CTDLbls parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDLbls)getTypeLoader().parse(inputStream, CTDLbls.type, (XmlOptions)null);
        }
        
        public static CTDLbls parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDLbls)getTypeLoader().parse(inputStream, CTDLbls.type, xmlOptions);
        }
        
        public static CTDLbls parse(final Reader reader) throws XmlException, IOException {
            return (CTDLbls)getTypeLoader().parse(reader, CTDLbls.type, (XmlOptions)null);
        }
        
        public static CTDLbls parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDLbls)getTypeLoader().parse(reader, CTDLbls.type, xmlOptions);
        }
        
        public static CTDLbls parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDLbls)getTypeLoader().parse(xmlStreamReader, CTDLbls.type, (XmlOptions)null);
        }
        
        public static CTDLbls parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDLbls)getTypeLoader().parse(xmlStreamReader, CTDLbls.type, xmlOptions);
        }
        
        public static CTDLbls parse(final Node node) throws XmlException {
            return (CTDLbls)getTypeLoader().parse(node, CTDLbls.type, (XmlOptions)null);
        }
        
        public static CTDLbls parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDLbls)getTypeLoader().parse(node, CTDLbls.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDLbls parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDLbls)getTypeLoader().parse(xmlInputStream, CTDLbls.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDLbls parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDLbls)getTypeLoader().parse(xmlInputStream, CTDLbls.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDLbls.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDLbls.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
