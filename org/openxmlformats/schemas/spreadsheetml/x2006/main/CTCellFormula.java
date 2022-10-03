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
import org.apache.xmlbeans.SchemaType;

public interface CTCellFormula extends STFormula
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTCellFormula.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcellformula3583type");
    
    STCellFormulaType.Enum getT();
    
    STCellFormulaType xgetT();
    
    boolean isSetT();
    
    void setT(final STCellFormulaType.Enum p0);
    
    void xsetT(final STCellFormulaType p0);
    
    void unsetT();
    
    boolean getAca();
    
    XmlBoolean xgetAca();
    
    boolean isSetAca();
    
    void setAca(final boolean p0);
    
    void xsetAca(final XmlBoolean p0);
    
    void unsetAca();
    
    String getRef();
    
    STRef xgetRef();
    
    boolean isSetRef();
    
    void setRef(final String p0);
    
    void xsetRef(final STRef p0);
    
    void unsetRef();
    
    boolean getDt2D();
    
    XmlBoolean xgetDt2D();
    
    boolean isSetDt2D();
    
    void setDt2D(final boolean p0);
    
    void xsetDt2D(final XmlBoolean p0);
    
    void unsetDt2D();
    
    boolean getDtr();
    
    XmlBoolean xgetDtr();
    
    boolean isSetDtr();
    
    void setDtr(final boolean p0);
    
    void xsetDtr(final XmlBoolean p0);
    
    void unsetDtr();
    
    boolean getDel1();
    
    XmlBoolean xgetDel1();
    
    boolean isSetDel1();
    
    void setDel1(final boolean p0);
    
    void xsetDel1(final XmlBoolean p0);
    
    void unsetDel1();
    
    boolean getDel2();
    
    XmlBoolean xgetDel2();
    
    boolean isSetDel2();
    
    void setDel2(final boolean p0);
    
    void xsetDel2(final XmlBoolean p0);
    
    void unsetDel2();
    
    String getR1();
    
    STCellRef xgetR1();
    
    boolean isSetR1();
    
    void setR1(final String p0);
    
    void xsetR1(final STCellRef p0);
    
    void unsetR1();
    
    String getR2();
    
    STCellRef xgetR2();
    
    boolean isSetR2();
    
    void setR2(final String p0);
    
    void xsetR2(final STCellRef p0);
    
    void unsetR2();
    
    boolean getCa();
    
    XmlBoolean xgetCa();
    
    boolean isSetCa();
    
    void setCa(final boolean p0);
    
    void xsetCa(final XmlBoolean p0);
    
    void unsetCa();
    
    long getSi();
    
    XmlUnsignedInt xgetSi();
    
    boolean isSetSi();
    
    void setSi(final long p0);
    
    void xsetSi(final XmlUnsignedInt p0);
    
    void unsetSi();
    
    boolean getBx();
    
    XmlBoolean xgetBx();
    
    boolean isSetBx();
    
    void setBx(final boolean p0);
    
    void xsetBx(final XmlBoolean p0);
    
    void unsetBx();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTCellFormula.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTCellFormula newInstance() {
            return (CTCellFormula)getTypeLoader().newInstance(CTCellFormula.type, (XmlOptions)null);
        }
        
        public static CTCellFormula newInstance(final XmlOptions xmlOptions) {
            return (CTCellFormula)getTypeLoader().newInstance(CTCellFormula.type, xmlOptions);
        }
        
        public static CTCellFormula parse(final String s) throws XmlException {
            return (CTCellFormula)getTypeLoader().parse(s, CTCellFormula.type, (XmlOptions)null);
        }
        
        public static CTCellFormula parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTCellFormula)getTypeLoader().parse(s, CTCellFormula.type, xmlOptions);
        }
        
        public static CTCellFormula parse(final File file) throws XmlException, IOException {
            return (CTCellFormula)getTypeLoader().parse(file, CTCellFormula.type, (XmlOptions)null);
        }
        
        public static CTCellFormula parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCellFormula)getTypeLoader().parse(file, CTCellFormula.type, xmlOptions);
        }
        
        public static CTCellFormula parse(final URL url) throws XmlException, IOException {
            return (CTCellFormula)getTypeLoader().parse(url, CTCellFormula.type, (XmlOptions)null);
        }
        
        public static CTCellFormula parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCellFormula)getTypeLoader().parse(url, CTCellFormula.type, xmlOptions);
        }
        
        public static CTCellFormula parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTCellFormula)getTypeLoader().parse(inputStream, CTCellFormula.type, (XmlOptions)null);
        }
        
        public static CTCellFormula parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCellFormula)getTypeLoader().parse(inputStream, CTCellFormula.type, xmlOptions);
        }
        
        public static CTCellFormula parse(final Reader reader) throws XmlException, IOException {
            return (CTCellFormula)getTypeLoader().parse(reader, CTCellFormula.type, (XmlOptions)null);
        }
        
        public static CTCellFormula parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCellFormula)getTypeLoader().parse(reader, CTCellFormula.type, xmlOptions);
        }
        
        public static CTCellFormula parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTCellFormula)getTypeLoader().parse(xmlStreamReader, CTCellFormula.type, (XmlOptions)null);
        }
        
        public static CTCellFormula parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTCellFormula)getTypeLoader().parse(xmlStreamReader, CTCellFormula.type, xmlOptions);
        }
        
        public static CTCellFormula parse(final Node node) throws XmlException {
            return (CTCellFormula)getTypeLoader().parse(node, CTCellFormula.type, (XmlOptions)null);
        }
        
        public static CTCellFormula parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTCellFormula)getTypeLoader().parse(node, CTCellFormula.type, xmlOptions);
        }
        
        @Deprecated
        public static CTCellFormula parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTCellFormula)getTypeLoader().parse(xmlInputStream, CTCellFormula.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTCellFormula parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTCellFormula)getTypeLoader().parse(xmlInputStream, CTCellFormula.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCellFormula.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCellFormula.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
