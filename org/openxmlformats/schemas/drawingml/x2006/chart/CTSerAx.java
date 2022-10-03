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
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTSerAx extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSerAx.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctserax2c0ftype");
    
    CTUnsignedInt getAxId();
    
    void setAxId(final CTUnsignedInt p0);
    
    CTUnsignedInt addNewAxId();
    
    CTScaling getScaling();
    
    void setScaling(final CTScaling p0);
    
    CTScaling addNewScaling();
    
    CTBoolean getDelete();
    
    boolean isSetDelete();
    
    void setDelete(final CTBoolean p0);
    
    CTBoolean addNewDelete();
    
    void unsetDelete();
    
    CTAxPos getAxPos();
    
    void setAxPos(final CTAxPos p0);
    
    CTAxPos addNewAxPos();
    
    CTChartLines getMajorGridlines();
    
    boolean isSetMajorGridlines();
    
    void setMajorGridlines(final CTChartLines p0);
    
    CTChartLines addNewMajorGridlines();
    
    void unsetMajorGridlines();
    
    CTChartLines getMinorGridlines();
    
    boolean isSetMinorGridlines();
    
    void setMinorGridlines(final CTChartLines p0);
    
    CTChartLines addNewMinorGridlines();
    
    void unsetMinorGridlines();
    
    CTTitle getTitle();
    
    boolean isSetTitle();
    
    void setTitle(final CTTitle p0);
    
    CTTitle addNewTitle();
    
    void unsetTitle();
    
    CTNumFmt getNumFmt();
    
    boolean isSetNumFmt();
    
    void setNumFmt(final CTNumFmt p0);
    
    CTNumFmt addNewNumFmt();
    
    void unsetNumFmt();
    
    CTTickMark getMajorTickMark();
    
    boolean isSetMajorTickMark();
    
    void setMajorTickMark(final CTTickMark p0);
    
    CTTickMark addNewMajorTickMark();
    
    void unsetMajorTickMark();
    
    CTTickMark getMinorTickMark();
    
    boolean isSetMinorTickMark();
    
    void setMinorTickMark(final CTTickMark p0);
    
    CTTickMark addNewMinorTickMark();
    
    void unsetMinorTickMark();
    
    CTTickLblPos getTickLblPos();
    
    boolean isSetTickLblPos();
    
    void setTickLblPos(final CTTickLblPos p0);
    
    CTTickLblPos addNewTickLblPos();
    
    void unsetTickLblPos();
    
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
    
    CTUnsignedInt getCrossAx();
    
    void setCrossAx(final CTUnsignedInt p0);
    
    CTUnsignedInt addNewCrossAx();
    
    CTCrosses getCrosses();
    
    boolean isSetCrosses();
    
    void setCrosses(final CTCrosses p0);
    
    CTCrosses addNewCrosses();
    
    void unsetCrosses();
    
    CTDouble getCrossesAt();
    
    boolean isSetCrossesAt();
    
    void setCrossesAt(final CTDouble p0);
    
    CTDouble addNewCrossesAt();
    
    void unsetCrossesAt();
    
    CTSkip getTickLblSkip();
    
    boolean isSetTickLblSkip();
    
    void setTickLblSkip(final CTSkip p0);
    
    CTSkip addNewTickLblSkip();
    
    void unsetTickLblSkip();
    
    CTSkip getTickMarkSkip();
    
    boolean isSetTickMarkSkip();
    
    void setTickMarkSkip(final CTSkip p0);
    
    CTSkip addNewTickMarkSkip();
    
    void unsetTickMarkSkip();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSerAx.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSerAx newInstance() {
            return (CTSerAx)getTypeLoader().newInstance(CTSerAx.type, (XmlOptions)null);
        }
        
        public static CTSerAx newInstance(final XmlOptions xmlOptions) {
            return (CTSerAx)getTypeLoader().newInstance(CTSerAx.type, xmlOptions);
        }
        
        public static CTSerAx parse(final String s) throws XmlException {
            return (CTSerAx)getTypeLoader().parse(s, CTSerAx.type, (XmlOptions)null);
        }
        
        public static CTSerAx parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSerAx)getTypeLoader().parse(s, CTSerAx.type, xmlOptions);
        }
        
        public static CTSerAx parse(final File file) throws XmlException, IOException {
            return (CTSerAx)getTypeLoader().parse(file, CTSerAx.type, (XmlOptions)null);
        }
        
        public static CTSerAx parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSerAx)getTypeLoader().parse(file, CTSerAx.type, xmlOptions);
        }
        
        public static CTSerAx parse(final URL url) throws XmlException, IOException {
            return (CTSerAx)getTypeLoader().parse(url, CTSerAx.type, (XmlOptions)null);
        }
        
        public static CTSerAx parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSerAx)getTypeLoader().parse(url, CTSerAx.type, xmlOptions);
        }
        
        public static CTSerAx parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSerAx)getTypeLoader().parse(inputStream, CTSerAx.type, (XmlOptions)null);
        }
        
        public static CTSerAx parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSerAx)getTypeLoader().parse(inputStream, CTSerAx.type, xmlOptions);
        }
        
        public static CTSerAx parse(final Reader reader) throws XmlException, IOException {
            return (CTSerAx)getTypeLoader().parse(reader, CTSerAx.type, (XmlOptions)null);
        }
        
        public static CTSerAx parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSerAx)getTypeLoader().parse(reader, CTSerAx.type, xmlOptions);
        }
        
        public static CTSerAx parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSerAx)getTypeLoader().parse(xmlStreamReader, CTSerAx.type, (XmlOptions)null);
        }
        
        public static CTSerAx parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSerAx)getTypeLoader().parse(xmlStreamReader, CTSerAx.type, xmlOptions);
        }
        
        public static CTSerAx parse(final Node node) throws XmlException {
            return (CTSerAx)getTypeLoader().parse(node, CTSerAx.type, (XmlOptions)null);
        }
        
        public static CTSerAx parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSerAx)getTypeLoader().parse(node, CTSerAx.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSerAx parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSerAx)getTypeLoader().parse(xmlInputStream, CTSerAx.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSerAx parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSerAx)getTypeLoader().parse(xmlInputStream, CTSerAx.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSerAx.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSerAx.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
