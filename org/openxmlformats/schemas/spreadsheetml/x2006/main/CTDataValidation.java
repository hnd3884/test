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
import java.util.List;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTDataValidation extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDataValidation.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctdatavalidation9d0ctype");
    
    String getFormula1();
    
    STFormula xgetFormula1();
    
    boolean isSetFormula1();
    
    void setFormula1(final String p0);
    
    void xsetFormula1(final STFormula p0);
    
    void unsetFormula1();
    
    String getFormula2();
    
    STFormula xgetFormula2();
    
    boolean isSetFormula2();
    
    void setFormula2(final String p0);
    
    void xsetFormula2(final STFormula p0);
    
    void unsetFormula2();
    
    STDataValidationType.Enum getType();
    
    STDataValidationType xgetType();
    
    boolean isSetType();
    
    void setType(final STDataValidationType.Enum p0);
    
    void xsetType(final STDataValidationType p0);
    
    void unsetType();
    
    STDataValidationErrorStyle.Enum getErrorStyle();
    
    STDataValidationErrorStyle xgetErrorStyle();
    
    boolean isSetErrorStyle();
    
    void setErrorStyle(final STDataValidationErrorStyle.Enum p0);
    
    void xsetErrorStyle(final STDataValidationErrorStyle p0);
    
    void unsetErrorStyle();
    
    STDataValidationImeMode.Enum getImeMode();
    
    STDataValidationImeMode xgetImeMode();
    
    boolean isSetImeMode();
    
    void setImeMode(final STDataValidationImeMode.Enum p0);
    
    void xsetImeMode(final STDataValidationImeMode p0);
    
    void unsetImeMode();
    
    STDataValidationOperator.Enum getOperator();
    
    STDataValidationOperator xgetOperator();
    
    boolean isSetOperator();
    
    void setOperator(final STDataValidationOperator.Enum p0);
    
    void xsetOperator(final STDataValidationOperator p0);
    
    void unsetOperator();
    
    boolean getAllowBlank();
    
    XmlBoolean xgetAllowBlank();
    
    boolean isSetAllowBlank();
    
    void setAllowBlank(final boolean p0);
    
    void xsetAllowBlank(final XmlBoolean p0);
    
    void unsetAllowBlank();
    
    boolean getShowDropDown();
    
    XmlBoolean xgetShowDropDown();
    
    boolean isSetShowDropDown();
    
    void setShowDropDown(final boolean p0);
    
    void xsetShowDropDown(final XmlBoolean p0);
    
    void unsetShowDropDown();
    
    boolean getShowInputMessage();
    
    XmlBoolean xgetShowInputMessage();
    
    boolean isSetShowInputMessage();
    
    void setShowInputMessage(final boolean p0);
    
    void xsetShowInputMessage(final XmlBoolean p0);
    
    void unsetShowInputMessage();
    
    boolean getShowErrorMessage();
    
    XmlBoolean xgetShowErrorMessage();
    
    boolean isSetShowErrorMessage();
    
    void setShowErrorMessage(final boolean p0);
    
    void xsetShowErrorMessage(final XmlBoolean p0);
    
    void unsetShowErrorMessage();
    
    String getErrorTitle();
    
    STXstring xgetErrorTitle();
    
    boolean isSetErrorTitle();
    
    void setErrorTitle(final String p0);
    
    void xsetErrorTitle(final STXstring p0);
    
    void unsetErrorTitle();
    
    String getError();
    
    STXstring xgetError();
    
    boolean isSetError();
    
    void setError(final String p0);
    
    void xsetError(final STXstring p0);
    
    void unsetError();
    
    String getPromptTitle();
    
    STXstring xgetPromptTitle();
    
    boolean isSetPromptTitle();
    
    void setPromptTitle(final String p0);
    
    void xsetPromptTitle(final STXstring p0);
    
    void unsetPromptTitle();
    
    String getPrompt();
    
    STXstring xgetPrompt();
    
    boolean isSetPrompt();
    
    void setPrompt(final String p0);
    
    void xsetPrompt(final STXstring p0);
    
    void unsetPrompt();
    
    List getSqref();
    
    STSqref xgetSqref();
    
    void setSqref(final List p0);
    
    void xsetSqref(final STSqref p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDataValidation.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDataValidation newInstance() {
            return (CTDataValidation)getTypeLoader().newInstance(CTDataValidation.type, (XmlOptions)null);
        }
        
        public static CTDataValidation newInstance(final XmlOptions xmlOptions) {
            return (CTDataValidation)getTypeLoader().newInstance(CTDataValidation.type, xmlOptions);
        }
        
        public static CTDataValidation parse(final String s) throws XmlException {
            return (CTDataValidation)getTypeLoader().parse(s, CTDataValidation.type, (XmlOptions)null);
        }
        
        public static CTDataValidation parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDataValidation)getTypeLoader().parse(s, CTDataValidation.type, xmlOptions);
        }
        
        public static CTDataValidation parse(final File file) throws XmlException, IOException {
            return (CTDataValidation)getTypeLoader().parse(file, CTDataValidation.type, (XmlOptions)null);
        }
        
        public static CTDataValidation parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDataValidation)getTypeLoader().parse(file, CTDataValidation.type, xmlOptions);
        }
        
        public static CTDataValidation parse(final URL url) throws XmlException, IOException {
            return (CTDataValidation)getTypeLoader().parse(url, CTDataValidation.type, (XmlOptions)null);
        }
        
        public static CTDataValidation parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDataValidation)getTypeLoader().parse(url, CTDataValidation.type, xmlOptions);
        }
        
        public static CTDataValidation parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDataValidation)getTypeLoader().parse(inputStream, CTDataValidation.type, (XmlOptions)null);
        }
        
        public static CTDataValidation parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDataValidation)getTypeLoader().parse(inputStream, CTDataValidation.type, xmlOptions);
        }
        
        public static CTDataValidation parse(final Reader reader) throws XmlException, IOException {
            return (CTDataValidation)getTypeLoader().parse(reader, CTDataValidation.type, (XmlOptions)null);
        }
        
        public static CTDataValidation parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDataValidation)getTypeLoader().parse(reader, CTDataValidation.type, xmlOptions);
        }
        
        public static CTDataValidation parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDataValidation)getTypeLoader().parse(xmlStreamReader, CTDataValidation.type, (XmlOptions)null);
        }
        
        public static CTDataValidation parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDataValidation)getTypeLoader().parse(xmlStreamReader, CTDataValidation.type, xmlOptions);
        }
        
        public static CTDataValidation parse(final Node node) throws XmlException {
            return (CTDataValidation)getTypeLoader().parse(node, CTDataValidation.type, (XmlOptions)null);
        }
        
        public static CTDataValidation parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDataValidation)getTypeLoader().parse(node, CTDataValidation.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDataValidation parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDataValidation)getTypeLoader().parse(xmlInputStream, CTDataValidation.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDataValidation parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDataValidation)getTypeLoader().parse(xmlInputStream, CTDataValidation.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDataValidation.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDataValidation.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
