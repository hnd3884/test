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
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInt;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTCfRule extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTCfRule.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcfrule3548type");
    
    List<String> getFormulaList();
    
    @Deprecated
    String[] getFormulaArray();
    
    String getFormulaArray(final int p0);
    
    List<STFormula> xgetFormulaList();
    
    @Deprecated
    STFormula[] xgetFormulaArray();
    
    STFormula xgetFormulaArray(final int p0);
    
    int sizeOfFormulaArray();
    
    void setFormulaArray(final String[] p0);
    
    void setFormulaArray(final int p0, final String p1);
    
    void xsetFormulaArray(final STFormula[] p0);
    
    void xsetFormulaArray(final int p0, final STFormula p1);
    
    void insertFormula(final int p0, final String p1);
    
    void addFormula(final String p0);
    
    STFormula insertNewFormula(final int p0);
    
    STFormula addNewFormula();
    
    void removeFormula(final int p0);
    
    CTColorScale getColorScale();
    
    boolean isSetColorScale();
    
    void setColorScale(final CTColorScale p0);
    
    CTColorScale addNewColorScale();
    
    void unsetColorScale();
    
    CTDataBar getDataBar();
    
    boolean isSetDataBar();
    
    void setDataBar(final CTDataBar p0);
    
    CTDataBar addNewDataBar();
    
    void unsetDataBar();
    
    CTIconSet getIconSet();
    
    boolean isSetIconSet();
    
    void setIconSet(final CTIconSet p0);
    
    CTIconSet addNewIconSet();
    
    void unsetIconSet();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    STCfType.Enum getType();
    
    STCfType xgetType();
    
    boolean isSetType();
    
    void setType(final STCfType.Enum p0);
    
    void xsetType(final STCfType p0);
    
    void unsetType();
    
    long getDxfId();
    
    STDxfId xgetDxfId();
    
    boolean isSetDxfId();
    
    void setDxfId(final long p0);
    
    void xsetDxfId(final STDxfId p0);
    
    void unsetDxfId();
    
    int getPriority();
    
    XmlInt xgetPriority();
    
    void setPriority(final int p0);
    
    void xsetPriority(final XmlInt p0);
    
    boolean getStopIfTrue();
    
    XmlBoolean xgetStopIfTrue();
    
    boolean isSetStopIfTrue();
    
    void setStopIfTrue(final boolean p0);
    
    void xsetStopIfTrue(final XmlBoolean p0);
    
    void unsetStopIfTrue();
    
    boolean getAboveAverage();
    
    XmlBoolean xgetAboveAverage();
    
    boolean isSetAboveAverage();
    
    void setAboveAverage(final boolean p0);
    
    void xsetAboveAverage(final XmlBoolean p0);
    
    void unsetAboveAverage();
    
    boolean getPercent();
    
    XmlBoolean xgetPercent();
    
    boolean isSetPercent();
    
    void setPercent(final boolean p0);
    
    void xsetPercent(final XmlBoolean p0);
    
    void unsetPercent();
    
    boolean getBottom();
    
    XmlBoolean xgetBottom();
    
    boolean isSetBottom();
    
    void setBottom(final boolean p0);
    
    void xsetBottom(final XmlBoolean p0);
    
    void unsetBottom();
    
    STConditionalFormattingOperator.Enum getOperator();
    
    STConditionalFormattingOperator xgetOperator();
    
    boolean isSetOperator();
    
    void setOperator(final STConditionalFormattingOperator.Enum p0);
    
    void xsetOperator(final STConditionalFormattingOperator p0);
    
    void unsetOperator();
    
    String getText();
    
    XmlString xgetText();
    
    boolean isSetText();
    
    void setText(final String p0);
    
    void xsetText(final XmlString p0);
    
    void unsetText();
    
    STTimePeriod.Enum getTimePeriod();
    
    STTimePeriod xgetTimePeriod();
    
    boolean isSetTimePeriod();
    
    void setTimePeriod(final STTimePeriod.Enum p0);
    
    void xsetTimePeriod(final STTimePeriod p0);
    
    void unsetTimePeriod();
    
    long getRank();
    
    XmlUnsignedInt xgetRank();
    
    boolean isSetRank();
    
    void setRank(final long p0);
    
    void xsetRank(final XmlUnsignedInt p0);
    
    void unsetRank();
    
    int getStdDev();
    
    XmlInt xgetStdDev();
    
    boolean isSetStdDev();
    
    void setStdDev(final int p0);
    
    void xsetStdDev(final XmlInt p0);
    
    void unsetStdDev();
    
    boolean getEqualAverage();
    
    XmlBoolean xgetEqualAverage();
    
    boolean isSetEqualAverage();
    
    void setEqualAverage(final boolean p0);
    
    void xsetEqualAverage(final XmlBoolean p0);
    
    void unsetEqualAverage();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTCfRule.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTCfRule newInstance() {
            return (CTCfRule)getTypeLoader().newInstance(CTCfRule.type, (XmlOptions)null);
        }
        
        public static CTCfRule newInstance(final XmlOptions xmlOptions) {
            return (CTCfRule)getTypeLoader().newInstance(CTCfRule.type, xmlOptions);
        }
        
        public static CTCfRule parse(final String s) throws XmlException {
            return (CTCfRule)getTypeLoader().parse(s, CTCfRule.type, (XmlOptions)null);
        }
        
        public static CTCfRule parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTCfRule)getTypeLoader().parse(s, CTCfRule.type, xmlOptions);
        }
        
        public static CTCfRule parse(final File file) throws XmlException, IOException {
            return (CTCfRule)getTypeLoader().parse(file, CTCfRule.type, (XmlOptions)null);
        }
        
        public static CTCfRule parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCfRule)getTypeLoader().parse(file, CTCfRule.type, xmlOptions);
        }
        
        public static CTCfRule parse(final URL url) throws XmlException, IOException {
            return (CTCfRule)getTypeLoader().parse(url, CTCfRule.type, (XmlOptions)null);
        }
        
        public static CTCfRule parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCfRule)getTypeLoader().parse(url, CTCfRule.type, xmlOptions);
        }
        
        public static CTCfRule parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTCfRule)getTypeLoader().parse(inputStream, CTCfRule.type, (XmlOptions)null);
        }
        
        public static CTCfRule parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCfRule)getTypeLoader().parse(inputStream, CTCfRule.type, xmlOptions);
        }
        
        public static CTCfRule parse(final Reader reader) throws XmlException, IOException {
            return (CTCfRule)getTypeLoader().parse(reader, CTCfRule.type, (XmlOptions)null);
        }
        
        public static CTCfRule parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCfRule)getTypeLoader().parse(reader, CTCfRule.type, xmlOptions);
        }
        
        public static CTCfRule parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTCfRule)getTypeLoader().parse(xmlStreamReader, CTCfRule.type, (XmlOptions)null);
        }
        
        public static CTCfRule parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTCfRule)getTypeLoader().parse(xmlStreamReader, CTCfRule.type, xmlOptions);
        }
        
        public static CTCfRule parse(final Node node) throws XmlException {
            return (CTCfRule)getTypeLoader().parse(node, CTCfRule.type, (XmlOptions)null);
        }
        
        public static CTCfRule parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTCfRule)getTypeLoader().parse(node, CTCfRule.type, xmlOptions);
        }
        
        @Deprecated
        public static CTCfRule parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTCfRule)getTypeLoader().parse(xmlInputStream, CTCfRule.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTCfRule parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTCfRule)getTypeLoader().parse(xmlInputStream, CTCfRule.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCfRule.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCfRule.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
