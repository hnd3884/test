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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTCell extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTCell.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcell842btype");
    
    CTCellFormula getF();
    
    boolean isSetF();
    
    void setF(final CTCellFormula p0);
    
    CTCellFormula addNewF();
    
    void unsetF();
    
    String getV();
    
    STXstring xgetV();
    
    boolean isSetV();
    
    void setV(final String p0);
    
    void xsetV(final STXstring p0);
    
    void unsetV();
    
    CTRst getIs();
    
    boolean isSetIs();
    
    void setIs(final CTRst p0);
    
    CTRst addNewIs();
    
    void unsetIs();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    String getR();
    
    STCellRef xgetR();
    
    boolean isSetR();
    
    void setR(final String p0);
    
    void xsetR(final STCellRef p0);
    
    void unsetR();
    
    long getS();
    
    XmlUnsignedInt xgetS();
    
    boolean isSetS();
    
    void setS(final long p0);
    
    void xsetS(final XmlUnsignedInt p0);
    
    void unsetS();
    
    STCellType.Enum getT();
    
    STCellType xgetT();
    
    boolean isSetT();
    
    void setT(final STCellType.Enum p0);
    
    void xsetT(final STCellType p0);
    
    void unsetT();
    
    long getCm();
    
    XmlUnsignedInt xgetCm();
    
    boolean isSetCm();
    
    void setCm(final long p0);
    
    void xsetCm(final XmlUnsignedInt p0);
    
    void unsetCm();
    
    long getVm();
    
    XmlUnsignedInt xgetVm();
    
    boolean isSetVm();
    
    void setVm(final long p0);
    
    void xsetVm(final XmlUnsignedInt p0);
    
    void unsetVm();
    
    boolean getPh();
    
    XmlBoolean xgetPh();
    
    boolean isSetPh();
    
    void setPh(final boolean p0);
    
    void xsetPh(final XmlBoolean p0);
    
    void unsetPh();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTCell.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTCell newInstance() {
            return (CTCell)getTypeLoader().newInstance(CTCell.type, (XmlOptions)null);
        }
        
        public static CTCell newInstance(final XmlOptions xmlOptions) {
            return (CTCell)getTypeLoader().newInstance(CTCell.type, xmlOptions);
        }
        
        public static CTCell parse(final String s) throws XmlException {
            return (CTCell)getTypeLoader().parse(s, CTCell.type, (XmlOptions)null);
        }
        
        public static CTCell parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTCell)getTypeLoader().parse(s, CTCell.type, xmlOptions);
        }
        
        public static CTCell parse(final File file) throws XmlException, IOException {
            return (CTCell)getTypeLoader().parse(file, CTCell.type, (XmlOptions)null);
        }
        
        public static CTCell parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCell)getTypeLoader().parse(file, CTCell.type, xmlOptions);
        }
        
        public static CTCell parse(final URL url) throws XmlException, IOException {
            return (CTCell)getTypeLoader().parse(url, CTCell.type, (XmlOptions)null);
        }
        
        public static CTCell parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCell)getTypeLoader().parse(url, CTCell.type, xmlOptions);
        }
        
        public static CTCell parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTCell)getTypeLoader().parse(inputStream, CTCell.type, (XmlOptions)null);
        }
        
        public static CTCell parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCell)getTypeLoader().parse(inputStream, CTCell.type, xmlOptions);
        }
        
        public static CTCell parse(final Reader reader) throws XmlException, IOException {
            return (CTCell)getTypeLoader().parse(reader, CTCell.type, (XmlOptions)null);
        }
        
        public static CTCell parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCell)getTypeLoader().parse(reader, CTCell.type, xmlOptions);
        }
        
        public static CTCell parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTCell)getTypeLoader().parse(xmlStreamReader, CTCell.type, (XmlOptions)null);
        }
        
        public static CTCell parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTCell)getTypeLoader().parse(xmlStreamReader, CTCell.type, xmlOptions);
        }
        
        public static CTCell parse(final Node node) throws XmlException {
            return (CTCell)getTypeLoader().parse(node, CTCell.type, (XmlOptions)null);
        }
        
        public static CTCell parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTCell)getTypeLoader().parse(node, CTCell.type, xmlOptions);
        }
        
        @Deprecated
        public static CTCell parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTCell)getTypeLoader().parse(xmlInputStream, CTCell.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTCell parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTCell)getTypeLoader().parse(xmlInputStream, CTCell.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCell.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCell.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
