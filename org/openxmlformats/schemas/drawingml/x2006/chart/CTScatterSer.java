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

public interface CTScatterSer extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTScatterSer.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctscatterser2f7atype");
    
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
    
    CTMarker getMarker();
    
    boolean isSetMarker();
    
    void setMarker(final CTMarker p0);
    
    CTMarker addNewMarker();
    
    void unsetMarker();
    
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
    
    CTAxDataSource getXVal();
    
    boolean isSetXVal();
    
    void setXVal(final CTAxDataSource p0);
    
    CTAxDataSource addNewXVal();
    
    void unsetXVal();
    
    CTNumDataSource getYVal();
    
    boolean isSetYVal();
    
    void setYVal(final CTNumDataSource p0);
    
    CTNumDataSource addNewYVal();
    
    void unsetYVal();
    
    CTBoolean getSmooth();
    
    boolean isSetSmooth();
    
    void setSmooth(final CTBoolean p0);
    
    CTBoolean addNewSmooth();
    
    void unsetSmooth();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTScatterSer.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTScatterSer newInstance() {
            return (CTScatterSer)getTypeLoader().newInstance(CTScatterSer.type, (XmlOptions)null);
        }
        
        public static CTScatterSer newInstance(final XmlOptions xmlOptions) {
            return (CTScatterSer)getTypeLoader().newInstance(CTScatterSer.type, xmlOptions);
        }
        
        public static CTScatterSer parse(final String s) throws XmlException {
            return (CTScatterSer)getTypeLoader().parse(s, CTScatterSer.type, (XmlOptions)null);
        }
        
        public static CTScatterSer parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTScatterSer)getTypeLoader().parse(s, CTScatterSer.type, xmlOptions);
        }
        
        public static CTScatterSer parse(final File file) throws XmlException, IOException {
            return (CTScatterSer)getTypeLoader().parse(file, CTScatterSer.type, (XmlOptions)null);
        }
        
        public static CTScatterSer parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTScatterSer)getTypeLoader().parse(file, CTScatterSer.type, xmlOptions);
        }
        
        public static CTScatterSer parse(final URL url) throws XmlException, IOException {
            return (CTScatterSer)getTypeLoader().parse(url, CTScatterSer.type, (XmlOptions)null);
        }
        
        public static CTScatterSer parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTScatterSer)getTypeLoader().parse(url, CTScatterSer.type, xmlOptions);
        }
        
        public static CTScatterSer parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTScatterSer)getTypeLoader().parse(inputStream, CTScatterSer.type, (XmlOptions)null);
        }
        
        public static CTScatterSer parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTScatterSer)getTypeLoader().parse(inputStream, CTScatterSer.type, xmlOptions);
        }
        
        public static CTScatterSer parse(final Reader reader) throws XmlException, IOException {
            return (CTScatterSer)getTypeLoader().parse(reader, CTScatterSer.type, (XmlOptions)null);
        }
        
        public static CTScatterSer parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTScatterSer)getTypeLoader().parse(reader, CTScatterSer.type, xmlOptions);
        }
        
        public static CTScatterSer parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTScatterSer)getTypeLoader().parse(xmlStreamReader, CTScatterSer.type, (XmlOptions)null);
        }
        
        public static CTScatterSer parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTScatterSer)getTypeLoader().parse(xmlStreamReader, CTScatterSer.type, xmlOptions);
        }
        
        public static CTScatterSer parse(final Node node) throws XmlException {
            return (CTScatterSer)getTypeLoader().parse(node, CTScatterSer.type, (XmlOptions)null);
        }
        
        public static CTScatterSer parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTScatterSer)getTypeLoader().parse(node, CTScatterSer.type, xmlOptions);
        }
        
        @Deprecated
        public static CTScatterSer parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTScatterSer)getTypeLoader().parse(xmlInputStream, CTScatterSer.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTScatterSer parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTScatterSer)getTypeLoader().parse(xmlInputStream, CTScatterSer.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTScatterSer.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTScatterSer.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
