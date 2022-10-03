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
import java.util.List;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTAreaSer extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTAreaSer.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctareaser4f73type");
    
    CTUnsignedInt getIdx();
    
    void setIdx(final CTUnsignedInt p0);
    
    CTUnsignedInt addNewIdx();
    
    CTUnsignedInt getOrder();
    
    void setOrder(final CTUnsignedInt p0);
    
    CTUnsignedInt addNewOrder();
    
    CTSerTx getTx();
    
    boolean isSetTx();
    
    void setTx(final CTSerTx p0);
    
    CTSerTx addNewTx();
    
    void unsetTx();
    
    CTShapeProperties getSpPr();
    
    boolean isSetSpPr();
    
    void setSpPr(final CTShapeProperties p0);
    
    CTShapeProperties addNewSpPr();
    
    void unsetSpPr();
    
    CTPictureOptions getPictureOptions();
    
    boolean isSetPictureOptions();
    
    void setPictureOptions(final CTPictureOptions p0);
    
    CTPictureOptions addNewPictureOptions();
    
    void unsetPictureOptions();
    
    List<CTDPt> getDPtList();
    
    @Deprecated
    CTDPt[] getDPtArray();
    
    CTDPt getDPtArray(final int p0);
    
    int sizeOfDPtArray();
    
    void setDPtArray(final CTDPt[] p0);
    
    void setDPtArray(final int p0, final CTDPt p1);
    
    CTDPt insertNewDPt(final int p0);
    
    CTDPt addNewDPt();
    
    void removeDPt(final int p0);
    
    CTDLbls getDLbls();
    
    boolean isSetDLbls();
    
    void setDLbls(final CTDLbls p0);
    
    CTDLbls addNewDLbls();
    
    void unsetDLbls();
    
    List<CTTrendline> getTrendlineList();
    
    @Deprecated
    CTTrendline[] getTrendlineArray();
    
    CTTrendline getTrendlineArray(final int p0);
    
    int sizeOfTrendlineArray();
    
    void setTrendlineArray(final CTTrendline[] p0);
    
    void setTrendlineArray(final int p0, final CTTrendline p1);
    
    CTTrendline insertNewTrendline(final int p0);
    
    CTTrendline addNewTrendline();
    
    void removeTrendline(final int p0);
    
    List<CTErrBars> getErrBarsList();
    
    @Deprecated
    CTErrBars[] getErrBarsArray();
    
    CTErrBars getErrBarsArray(final int p0);
    
    int sizeOfErrBarsArray();
    
    void setErrBarsArray(final CTErrBars[] p0);
    
    void setErrBarsArray(final int p0, final CTErrBars p1);
    
    CTErrBars insertNewErrBars(final int p0);
    
    CTErrBars addNewErrBars();
    
    void removeErrBars(final int p0);
    
    CTAxDataSource getCat();
    
    boolean isSetCat();
    
    void setCat(final CTAxDataSource p0);
    
    CTAxDataSource addNewCat();
    
    void unsetCat();
    
    CTNumDataSource getVal();
    
    boolean isSetVal();
    
    void setVal(final CTNumDataSource p0);
    
    CTNumDataSource addNewVal();
    
    void unsetVal();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTAreaSer.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTAreaSer newInstance() {
            return (CTAreaSer)getTypeLoader().newInstance(CTAreaSer.type, (XmlOptions)null);
        }
        
        public static CTAreaSer newInstance(final XmlOptions xmlOptions) {
            return (CTAreaSer)getTypeLoader().newInstance(CTAreaSer.type, xmlOptions);
        }
        
        public static CTAreaSer parse(final String s) throws XmlException {
            return (CTAreaSer)getTypeLoader().parse(s, CTAreaSer.type, (XmlOptions)null);
        }
        
        public static CTAreaSer parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTAreaSer)getTypeLoader().parse(s, CTAreaSer.type, xmlOptions);
        }
        
        public static CTAreaSer parse(final File file) throws XmlException, IOException {
            return (CTAreaSer)getTypeLoader().parse(file, CTAreaSer.type, (XmlOptions)null);
        }
        
        public static CTAreaSer parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAreaSer)getTypeLoader().parse(file, CTAreaSer.type, xmlOptions);
        }
        
        public static CTAreaSer parse(final URL url) throws XmlException, IOException {
            return (CTAreaSer)getTypeLoader().parse(url, CTAreaSer.type, (XmlOptions)null);
        }
        
        public static CTAreaSer parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAreaSer)getTypeLoader().parse(url, CTAreaSer.type, xmlOptions);
        }
        
        public static CTAreaSer parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTAreaSer)getTypeLoader().parse(inputStream, CTAreaSer.type, (XmlOptions)null);
        }
        
        public static CTAreaSer parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAreaSer)getTypeLoader().parse(inputStream, CTAreaSer.type, xmlOptions);
        }
        
        public static CTAreaSer parse(final Reader reader) throws XmlException, IOException {
            return (CTAreaSer)getTypeLoader().parse(reader, CTAreaSer.type, (XmlOptions)null);
        }
        
        public static CTAreaSer parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAreaSer)getTypeLoader().parse(reader, CTAreaSer.type, xmlOptions);
        }
        
        public static CTAreaSer parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTAreaSer)getTypeLoader().parse(xmlStreamReader, CTAreaSer.type, (XmlOptions)null);
        }
        
        public static CTAreaSer parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTAreaSer)getTypeLoader().parse(xmlStreamReader, CTAreaSer.type, xmlOptions);
        }
        
        public static CTAreaSer parse(final Node node) throws XmlException {
            return (CTAreaSer)getTypeLoader().parse(node, CTAreaSer.type, (XmlOptions)null);
        }
        
        public static CTAreaSer parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTAreaSer)getTypeLoader().parse(node, CTAreaSer.type, xmlOptions);
        }
        
        @Deprecated
        public static CTAreaSer parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTAreaSer)getTypeLoader().parse(xmlInputStream, CTAreaSer.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTAreaSer parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTAreaSer)getTypeLoader().parse(xmlInputStream, CTAreaSer.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAreaSer.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAreaSer.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
