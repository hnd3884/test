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

public interface CTRadarSer extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTRadarSer.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctradarser3482type");
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTRadarSer.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTRadarSer newInstance() {
            return (CTRadarSer)getTypeLoader().newInstance(CTRadarSer.type, (XmlOptions)null);
        }
        
        public static CTRadarSer newInstance(final XmlOptions xmlOptions) {
            return (CTRadarSer)getTypeLoader().newInstance(CTRadarSer.type, xmlOptions);
        }
        
        public static CTRadarSer parse(final String s) throws XmlException {
            return (CTRadarSer)getTypeLoader().parse(s, CTRadarSer.type, (XmlOptions)null);
        }
        
        public static CTRadarSer parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTRadarSer)getTypeLoader().parse(s, CTRadarSer.type, xmlOptions);
        }
        
        public static CTRadarSer parse(final File file) throws XmlException, IOException {
            return (CTRadarSer)getTypeLoader().parse(file, CTRadarSer.type, (XmlOptions)null);
        }
        
        public static CTRadarSer parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRadarSer)getTypeLoader().parse(file, CTRadarSer.type, xmlOptions);
        }
        
        public static CTRadarSer parse(final URL url) throws XmlException, IOException {
            return (CTRadarSer)getTypeLoader().parse(url, CTRadarSer.type, (XmlOptions)null);
        }
        
        public static CTRadarSer parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRadarSer)getTypeLoader().parse(url, CTRadarSer.type, xmlOptions);
        }
        
        public static CTRadarSer parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTRadarSer)getTypeLoader().parse(inputStream, CTRadarSer.type, (XmlOptions)null);
        }
        
        public static CTRadarSer parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRadarSer)getTypeLoader().parse(inputStream, CTRadarSer.type, xmlOptions);
        }
        
        public static CTRadarSer parse(final Reader reader) throws XmlException, IOException {
            return (CTRadarSer)getTypeLoader().parse(reader, CTRadarSer.type, (XmlOptions)null);
        }
        
        public static CTRadarSer parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRadarSer)getTypeLoader().parse(reader, CTRadarSer.type, xmlOptions);
        }
        
        public static CTRadarSer parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTRadarSer)getTypeLoader().parse(xmlStreamReader, CTRadarSer.type, (XmlOptions)null);
        }
        
        public static CTRadarSer parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTRadarSer)getTypeLoader().parse(xmlStreamReader, CTRadarSer.type, xmlOptions);
        }
        
        public static CTRadarSer parse(final Node node) throws XmlException {
            return (CTRadarSer)getTypeLoader().parse(node, CTRadarSer.type, (XmlOptions)null);
        }
        
        public static CTRadarSer parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTRadarSer)getTypeLoader().parse(node, CTRadarSer.type, xmlOptions);
        }
        
        @Deprecated
        public static CTRadarSer parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTRadarSer)getTypeLoader().parse(xmlInputStream, CTRadarSer.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTRadarSer parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTRadarSer)getTypeLoader().parse(xmlInputStream, CTRadarSer.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRadarSer.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRadarSer.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
