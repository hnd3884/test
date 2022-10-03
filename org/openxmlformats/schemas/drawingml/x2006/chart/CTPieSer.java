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

public interface CTPieSer extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPieSer.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpieser5248type");
    
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
    
    CTUnsignedInt getExplosion();
    
    boolean isSetExplosion();
    
    void setExplosion(final CTUnsignedInt p0);
    
    CTUnsignedInt addNewExplosion();
    
    void unsetExplosion();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPieSer.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPieSer newInstance() {
            return (CTPieSer)getTypeLoader().newInstance(CTPieSer.type, (XmlOptions)null);
        }
        
        public static CTPieSer newInstance(final XmlOptions xmlOptions) {
            return (CTPieSer)getTypeLoader().newInstance(CTPieSer.type, xmlOptions);
        }
        
        public static CTPieSer parse(final String s) throws XmlException {
            return (CTPieSer)getTypeLoader().parse(s, CTPieSer.type, (XmlOptions)null);
        }
        
        public static CTPieSer parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPieSer)getTypeLoader().parse(s, CTPieSer.type, xmlOptions);
        }
        
        public static CTPieSer parse(final File file) throws XmlException, IOException {
            return (CTPieSer)getTypeLoader().parse(file, CTPieSer.type, (XmlOptions)null);
        }
        
        public static CTPieSer parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPieSer)getTypeLoader().parse(file, CTPieSer.type, xmlOptions);
        }
        
        public static CTPieSer parse(final URL url) throws XmlException, IOException {
            return (CTPieSer)getTypeLoader().parse(url, CTPieSer.type, (XmlOptions)null);
        }
        
        public static CTPieSer parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPieSer)getTypeLoader().parse(url, CTPieSer.type, xmlOptions);
        }
        
        public static CTPieSer parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPieSer)getTypeLoader().parse(inputStream, CTPieSer.type, (XmlOptions)null);
        }
        
        public static CTPieSer parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPieSer)getTypeLoader().parse(inputStream, CTPieSer.type, xmlOptions);
        }
        
        public static CTPieSer parse(final Reader reader) throws XmlException, IOException {
            return (CTPieSer)getTypeLoader().parse(reader, CTPieSer.type, (XmlOptions)null);
        }
        
        public static CTPieSer parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPieSer)getTypeLoader().parse(reader, CTPieSer.type, xmlOptions);
        }
        
        public static CTPieSer parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPieSer)getTypeLoader().parse(xmlStreamReader, CTPieSer.type, (XmlOptions)null);
        }
        
        public static CTPieSer parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPieSer)getTypeLoader().parse(xmlStreamReader, CTPieSer.type, xmlOptions);
        }
        
        public static CTPieSer parse(final Node node) throws XmlException {
            return (CTPieSer)getTypeLoader().parse(node, CTPieSer.type, (XmlOptions)null);
        }
        
        public static CTPieSer parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPieSer)getTypeLoader().parse(node, CTPieSer.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPieSer parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPieSer)getTypeLoader().parse(xmlInputStream, CTPieSer.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPieSer parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPieSer)getTypeLoader().parse(xmlInputStream, CTPieSer.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPieSer.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPieSer.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
