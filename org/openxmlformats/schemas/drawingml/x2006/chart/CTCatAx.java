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

public interface CTCatAx extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTCatAx.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcatax7159type");
    
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
    
    CTBoolean getAuto();
    
    boolean isSetAuto();
    
    void setAuto(final CTBoolean p0);
    
    CTBoolean addNewAuto();
    
    void unsetAuto();
    
    CTLblAlgn getLblAlgn();
    
    boolean isSetLblAlgn();
    
    void setLblAlgn(final CTLblAlgn p0);
    
    CTLblAlgn addNewLblAlgn();
    
    void unsetLblAlgn();
    
    CTLblOffset getLblOffset();
    
    boolean isSetLblOffset();
    
    void setLblOffset(final CTLblOffset p0);
    
    CTLblOffset addNewLblOffset();
    
    void unsetLblOffset();
    
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
    
    CTBoolean getNoMultiLvlLbl();
    
    boolean isSetNoMultiLvlLbl();
    
    void setNoMultiLvlLbl(final CTBoolean p0);
    
    CTBoolean addNewNoMultiLvlLbl();
    
    void unsetNoMultiLvlLbl();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTCatAx.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTCatAx newInstance() {
            return (CTCatAx)getTypeLoader().newInstance(CTCatAx.type, (XmlOptions)null);
        }
        
        public static CTCatAx newInstance(final XmlOptions xmlOptions) {
            return (CTCatAx)getTypeLoader().newInstance(CTCatAx.type, xmlOptions);
        }
        
        public static CTCatAx parse(final String s) throws XmlException {
            return (CTCatAx)getTypeLoader().parse(s, CTCatAx.type, (XmlOptions)null);
        }
        
        public static CTCatAx parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTCatAx)getTypeLoader().parse(s, CTCatAx.type, xmlOptions);
        }
        
        public static CTCatAx parse(final File file) throws XmlException, IOException {
            return (CTCatAx)getTypeLoader().parse(file, CTCatAx.type, (XmlOptions)null);
        }
        
        public static CTCatAx parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCatAx)getTypeLoader().parse(file, CTCatAx.type, xmlOptions);
        }
        
        public static CTCatAx parse(final URL url) throws XmlException, IOException {
            return (CTCatAx)getTypeLoader().parse(url, CTCatAx.type, (XmlOptions)null);
        }
        
        public static CTCatAx parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCatAx)getTypeLoader().parse(url, CTCatAx.type, xmlOptions);
        }
        
        public static CTCatAx parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTCatAx)getTypeLoader().parse(inputStream, CTCatAx.type, (XmlOptions)null);
        }
        
        public static CTCatAx parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCatAx)getTypeLoader().parse(inputStream, CTCatAx.type, xmlOptions);
        }
        
        public static CTCatAx parse(final Reader reader) throws XmlException, IOException {
            return (CTCatAx)getTypeLoader().parse(reader, CTCatAx.type, (XmlOptions)null);
        }
        
        public static CTCatAx parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCatAx)getTypeLoader().parse(reader, CTCatAx.type, xmlOptions);
        }
        
        public static CTCatAx parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTCatAx)getTypeLoader().parse(xmlStreamReader, CTCatAx.type, (XmlOptions)null);
        }
        
        public static CTCatAx parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTCatAx)getTypeLoader().parse(xmlStreamReader, CTCatAx.type, xmlOptions);
        }
        
        public static CTCatAx parse(final Node node) throws XmlException {
            return (CTCatAx)getTypeLoader().parse(node, CTCatAx.type, (XmlOptions)null);
        }
        
        public static CTCatAx parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTCatAx)getTypeLoader().parse(node, CTCatAx.type, xmlOptions);
        }
        
        @Deprecated
        public static CTCatAx parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTCatAx)getTypeLoader().parse(xmlInputStream, CTCatAx.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTCatAx parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTCatAx)getTypeLoader().parse(xmlInputStream, CTCatAx.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCatAx.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCatAx.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
