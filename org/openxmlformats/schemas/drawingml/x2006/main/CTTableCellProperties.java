package org.openxmlformats.schemas.drawingml.x2006.main;

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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTableCellProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTableCellProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttablecellproperties1614type");
    
    CTLineProperties getLnL();
    
    boolean isSetLnL();
    
    void setLnL(final CTLineProperties p0);
    
    CTLineProperties addNewLnL();
    
    void unsetLnL();
    
    CTLineProperties getLnR();
    
    boolean isSetLnR();
    
    void setLnR(final CTLineProperties p0);
    
    CTLineProperties addNewLnR();
    
    void unsetLnR();
    
    CTLineProperties getLnT();
    
    boolean isSetLnT();
    
    void setLnT(final CTLineProperties p0);
    
    CTLineProperties addNewLnT();
    
    void unsetLnT();
    
    CTLineProperties getLnB();
    
    boolean isSetLnB();
    
    void setLnB(final CTLineProperties p0);
    
    CTLineProperties addNewLnB();
    
    void unsetLnB();
    
    CTLineProperties getLnTlToBr();
    
    boolean isSetLnTlToBr();
    
    void setLnTlToBr(final CTLineProperties p0);
    
    CTLineProperties addNewLnTlToBr();
    
    void unsetLnTlToBr();
    
    CTLineProperties getLnBlToTr();
    
    boolean isSetLnBlToTr();
    
    void setLnBlToTr(final CTLineProperties p0);
    
    CTLineProperties addNewLnBlToTr();
    
    void unsetLnBlToTr();
    
    CTCell3D getCell3D();
    
    boolean isSetCell3D();
    
    void setCell3D(final CTCell3D p0);
    
    CTCell3D addNewCell3D();
    
    void unsetCell3D();
    
    CTNoFillProperties getNoFill();
    
    boolean isSetNoFill();
    
    void setNoFill(final CTNoFillProperties p0);
    
    CTNoFillProperties addNewNoFill();
    
    void unsetNoFill();
    
    CTSolidColorFillProperties getSolidFill();
    
    boolean isSetSolidFill();
    
    void setSolidFill(final CTSolidColorFillProperties p0);
    
    CTSolidColorFillProperties addNewSolidFill();
    
    void unsetSolidFill();
    
    CTGradientFillProperties getGradFill();
    
    boolean isSetGradFill();
    
    void setGradFill(final CTGradientFillProperties p0);
    
    CTGradientFillProperties addNewGradFill();
    
    void unsetGradFill();
    
    CTBlipFillProperties getBlipFill();
    
    boolean isSetBlipFill();
    
    void setBlipFill(final CTBlipFillProperties p0);
    
    CTBlipFillProperties addNewBlipFill();
    
    void unsetBlipFill();
    
    CTPatternFillProperties getPattFill();
    
    boolean isSetPattFill();
    
    void setPattFill(final CTPatternFillProperties p0);
    
    CTPatternFillProperties addNewPattFill();
    
    void unsetPattFill();
    
    CTGroupFillProperties getGrpFill();
    
    boolean isSetGrpFill();
    
    void setGrpFill(final CTGroupFillProperties p0);
    
    CTGroupFillProperties addNewGrpFill();
    
    void unsetGrpFill();
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    int getMarL();
    
    STCoordinate32 xgetMarL();
    
    boolean isSetMarL();
    
    void setMarL(final int p0);
    
    void xsetMarL(final STCoordinate32 p0);
    
    void unsetMarL();
    
    int getMarR();
    
    STCoordinate32 xgetMarR();
    
    boolean isSetMarR();
    
    void setMarR(final int p0);
    
    void xsetMarR(final STCoordinate32 p0);
    
    void unsetMarR();
    
    int getMarT();
    
    STCoordinate32 xgetMarT();
    
    boolean isSetMarT();
    
    void setMarT(final int p0);
    
    void xsetMarT(final STCoordinate32 p0);
    
    void unsetMarT();
    
    int getMarB();
    
    STCoordinate32 xgetMarB();
    
    boolean isSetMarB();
    
    void setMarB(final int p0);
    
    void xsetMarB(final STCoordinate32 p0);
    
    void unsetMarB();
    
    STTextVerticalType.Enum getVert();
    
    STTextVerticalType xgetVert();
    
    boolean isSetVert();
    
    void setVert(final STTextVerticalType.Enum p0);
    
    void xsetVert(final STTextVerticalType p0);
    
    void unsetVert();
    
    STTextAnchoringType.Enum getAnchor();
    
    STTextAnchoringType xgetAnchor();
    
    boolean isSetAnchor();
    
    void setAnchor(final STTextAnchoringType.Enum p0);
    
    void xsetAnchor(final STTextAnchoringType p0);
    
    void unsetAnchor();
    
    boolean getAnchorCtr();
    
    XmlBoolean xgetAnchorCtr();
    
    boolean isSetAnchorCtr();
    
    void setAnchorCtr(final boolean p0);
    
    void xsetAnchorCtr(final XmlBoolean p0);
    
    void unsetAnchorCtr();
    
    STTextHorzOverflowType.Enum getHorzOverflow();
    
    STTextHorzOverflowType xgetHorzOverflow();
    
    boolean isSetHorzOverflow();
    
    void setHorzOverflow(final STTextHorzOverflowType.Enum p0);
    
    void xsetHorzOverflow(final STTextHorzOverflowType p0);
    
    void unsetHorzOverflow();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTableCellProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTableCellProperties newInstance() {
            return (CTTableCellProperties)getTypeLoader().newInstance(CTTableCellProperties.type, (XmlOptions)null);
        }
        
        public static CTTableCellProperties newInstance(final XmlOptions xmlOptions) {
            return (CTTableCellProperties)getTypeLoader().newInstance(CTTableCellProperties.type, xmlOptions);
        }
        
        public static CTTableCellProperties parse(final String s) throws XmlException {
            return (CTTableCellProperties)getTypeLoader().parse(s, CTTableCellProperties.type, (XmlOptions)null);
        }
        
        public static CTTableCellProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableCellProperties)getTypeLoader().parse(s, CTTableCellProperties.type, xmlOptions);
        }
        
        public static CTTableCellProperties parse(final File file) throws XmlException, IOException {
            return (CTTableCellProperties)getTypeLoader().parse(file, CTTableCellProperties.type, (XmlOptions)null);
        }
        
        public static CTTableCellProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableCellProperties)getTypeLoader().parse(file, CTTableCellProperties.type, xmlOptions);
        }
        
        public static CTTableCellProperties parse(final URL url) throws XmlException, IOException {
            return (CTTableCellProperties)getTypeLoader().parse(url, CTTableCellProperties.type, (XmlOptions)null);
        }
        
        public static CTTableCellProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableCellProperties)getTypeLoader().parse(url, CTTableCellProperties.type, xmlOptions);
        }
        
        public static CTTableCellProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTableCellProperties)getTypeLoader().parse(inputStream, CTTableCellProperties.type, (XmlOptions)null);
        }
        
        public static CTTableCellProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableCellProperties)getTypeLoader().parse(inputStream, CTTableCellProperties.type, xmlOptions);
        }
        
        public static CTTableCellProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTTableCellProperties)getTypeLoader().parse(reader, CTTableCellProperties.type, (XmlOptions)null);
        }
        
        public static CTTableCellProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableCellProperties)getTypeLoader().parse(reader, CTTableCellProperties.type, xmlOptions);
        }
        
        public static CTTableCellProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTableCellProperties)getTypeLoader().parse(xmlStreamReader, CTTableCellProperties.type, (XmlOptions)null);
        }
        
        public static CTTableCellProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableCellProperties)getTypeLoader().parse(xmlStreamReader, CTTableCellProperties.type, xmlOptions);
        }
        
        public static CTTableCellProperties parse(final Node node) throws XmlException {
            return (CTTableCellProperties)getTypeLoader().parse(node, CTTableCellProperties.type, (XmlOptions)null);
        }
        
        public static CTTableCellProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableCellProperties)getTypeLoader().parse(node, CTTableCellProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTableCellProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTableCellProperties)getTypeLoader().parse(xmlInputStream, CTTableCellProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTableCellProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTableCellProperties)getTypeLoader().parse(xmlInputStream, CTTableCellProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableCellProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableCellProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
