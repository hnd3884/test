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
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlToken;

public interface STTextAutonumberScheme extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTextAutonumberScheme.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttextautonumberschemed675type");
    public static final Enum ALPHA_LC_PAREN_BOTH = Enum.forString("alphaLcParenBoth");
    public static final Enum ALPHA_UC_PAREN_BOTH = Enum.forString("alphaUcParenBoth");
    public static final Enum ALPHA_LC_PAREN_R = Enum.forString("alphaLcParenR");
    public static final Enum ALPHA_UC_PAREN_R = Enum.forString("alphaUcParenR");
    public static final Enum ALPHA_LC_PERIOD = Enum.forString("alphaLcPeriod");
    public static final Enum ALPHA_UC_PERIOD = Enum.forString("alphaUcPeriod");
    public static final Enum ARABIC_PAREN_BOTH = Enum.forString("arabicParenBoth");
    public static final Enum ARABIC_PAREN_R = Enum.forString("arabicParenR");
    public static final Enum ARABIC_PERIOD = Enum.forString("arabicPeriod");
    public static final Enum ARABIC_PLAIN = Enum.forString("arabicPlain");
    public static final Enum ROMAN_LC_PAREN_BOTH = Enum.forString("romanLcParenBoth");
    public static final Enum ROMAN_UC_PAREN_BOTH = Enum.forString("romanUcParenBoth");
    public static final Enum ROMAN_LC_PAREN_R = Enum.forString("romanLcParenR");
    public static final Enum ROMAN_UC_PAREN_R = Enum.forString("romanUcParenR");
    public static final Enum ROMAN_LC_PERIOD = Enum.forString("romanLcPeriod");
    public static final Enum ROMAN_UC_PERIOD = Enum.forString("romanUcPeriod");
    public static final Enum CIRCLE_NUM_DB_PLAIN = Enum.forString("circleNumDbPlain");
    public static final Enum CIRCLE_NUM_WD_BLACK_PLAIN = Enum.forString("circleNumWdBlackPlain");
    public static final Enum CIRCLE_NUM_WD_WHITE_PLAIN = Enum.forString("circleNumWdWhitePlain");
    public static final Enum ARABIC_DB_PERIOD = Enum.forString("arabicDbPeriod");
    public static final Enum ARABIC_DB_PLAIN = Enum.forString("arabicDbPlain");
    public static final Enum EA_1_CHS_PERIOD = Enum.forString("ea1ChsPeriod");
    public static final Enum EA_1_CHS_PLAIN = Enum.forString("ea1ChsPlain");
    public static final Enum EA_1_CHT_PERIOD = Enum.forString("ea1ChtPeriod");
    public static final Enum EA_1_CHT_PLAIN = Enum.forString("ea1ChtPlain");
    public static final Enum EA_1_JPN_CHS_DB_PERIOD = Enum.forString("ea1JpnChsDbPeriod");
    public static final Enum EA_1_JPN_KOR_PLAIN = Enum.forString("ea1JpnKorPlain");
    public static final Enum EA_1_JPN_KOR_PERIOD = Enum.forString("ea1JpnKorPeriod");
    public static final Enum ARABIC_1_MINUS = Enum.forString("arabic1Minus");
    public static final Enum ARABIC_2_MINUS = Enum.forString("arabic2Minus");
    public static final Enum HEBREW_2_MINUS = Enum.forString("hebrew2Minus");
    public static final Enum THAI_ALPHA_PERIOD = Enum.forString("thaiAlphaPeriod");
    public static final Enum THAI_ALPHA_PAREN_R = Enum.forString("thaiAlphaParenR");
    public static final Enum THAI_ALPHA_PAREN_BOTH = Enum.forString("thaiAlphaParenBoth");
    public static final Enum THAI_NUM_PERIOD = Enum.forString("thaiNumPeriod");
    public static final Enum THAI_NUM_PAREN_R = Enum.forString("thaiNumParenR");
    public static final Enum THAI_NUM_PAREN_BOTH = Enum.forString("thaiNumParenBoth");
    public static final Enum HINDI_ALPHA_PERIOD = Enum.forString("hindiAlphaPeriod");
    public static final Enum HINDI_NUM_PERIOD = Enum.forString("hindiNumPeriod");
    public static final Enum HINDI_NUM_PAREN_R = Enum.forString("hindiNumParenR");
    public static final Enum HINDI_ALPHA_1_PERIOD = Enum.forString("hindiAlpha1Period");
    public static final int INT_ALPHA_LC_PAREN_BOTH = 1;
    public static final int INT_ALPHA_UC_PAREN_BOTH = 2;
    public static final int INT_ALPHA_LC_PAREN_R = 3;
    public static final int INT_ALPHA_UC_PAREN_R = 4;
    public static final int INT_ALPHA_LC_PERIOD = 5;
    public static final int INT_ALPHA_UC_PERIOD = 6;
    public static final int INT_ARABIC_PAREN_BOTH = 7;
    public static final int INT_ARABIC_PAREN_R = 8;
    public static final int INT_ARABIC_PERIOD = 9;
    public static final int INT_ARABIC_PLAIN = 10;
    public static final int INT_ROMAN_LC_PAREN_BOTH = 11;
    public static final int INT_ROMAN_UC_PAREN_BOTH = 12;
    public static final int INT_ROMAN_LC_PAREN_R = 13;
    public static final int INT_ROMAN_UC_PAREN_R = 14;
    public static final int INT_ROMAN_LC_PERIOD = 15;
    public static final int INT_ROMAN_UC_PERIOD = 16;
    public static final int INT_CIRCLE_NUM_DB_PLAIN = 17;
    public static final int INT_CIRCLE_NUM_WD_BLACK_PLAIN = 18;
    public static final int INT_CIRCLE_NUM_WD_WHITE_PLAIN = 19;
    public static final int INT_ARABIC_DB_PERIOD = 20;
    public static final int INT_ARABIC_DB_PLAIN = 21;
    public static final int INT_EA_1_CHS_PERIOD = 22;
    public static final int INT_EA_1_CHS_PLAIN = 23;
    public static final int INT_EA_1_CHT_PERIOD = 24;
    public static final int INT_EA_1_CHT_PLAIN = 25;
    public static final int INT_EA_1_JPN_CHS_DB_PERIOD = 26;
    public static final int INT_EA_1_JPN_KOR_PLAIN = 27;
    public static final int INT_EA_1_JPN_KOR_PERIOD = 28;
    public static final int INT_ARABIC_1_MINUS = 29;
    public static final int INT_ARABIC_2_MINUS = 30;
    public static final int INT_HEBREW_2_MINUS = 31;
    public static final int INT_THAI_ALPHA_PERIOD = 32;
    public static final int INT_THAI_ALPHA_PAREN_R = 33;
    public static final int INT_THAI_ALPHA_PAREN_BOTH = 34;
    public static final int INT_THAI_NUM_PERIOD = 35;
    public static final int INT_THAI_NUM_PAREN_R = 36;
    public static final int INT_THAI_NUM_PAREN_BOTH = 37;
    public static final int INT_HINDI_ALPHA_PERIOD = 38;
    public static final int INT_HINDI_NUM_PERIOD = 39;
    public static final int INT_HINDI_NUM_PAREN_R = 40;
    public static final int INT_HINDI_ALPHA_1_PERIOD = 41;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTextAutonumberScheme newValue(final Object o) {
            return (STTextAutonumberScheme)STTextAutonumberScheme.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTextAutonumberScheme.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTextAutonumberScheme newInstance() {
            return (STTextAutonumberScheme)getTypeLoader().newInstance(STTextAutonumberScheme.type, (XmlOptions)null);
        }
        
        public static STTextAutonumberScheme newInstance(final XmlOptions xmlOptions) {
            return (STTextAutonumberScheme)getTypeLoader().newInstance(STTextAutonumberScheme.type, xmlOptions);
        }
        
        public static STTextAutonumberScheme parse(final String s) throws XmlException {
            return (STTextAutonumberScheme)getTypeLoader().parse(s, STTextAutonumberScheme.type, (XmlOptions)null);
        }
        
        public static STTextAutonumberScheme parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTextAutonumberScheme)getTypeLoader().parse(s, STTextAutonumberScheme.type, xmlOptions);
        }
        
        public static STTextAutonumberScheme parse(final File file) throws XmlException, IOException {
            return (STTextAutonumberScheme)getTypeLoader().parse(file, STTextAutonumberScheme.type, (XmlOptions)null);
        }
        
        public static STTextAutonumberScheme parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextAutonumberScheme)getTypeLoader().parse(file, STTextAutonumberScheme.type, xmlOptions);
        }
        
        public static STTextAutonumberScheme parse(final URL url) throws XmlException, IOException {
            return (STTextAutonumberScheme)getTypeLoader().parse(url, STTextAutonumberScheme.type, (XmlOptions)null);
        }
        
        public static STTextAutonumberScheme parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextAutonumberScheme)getTypeLoader().parse(url, STTextAutonumberScheme.type, xmlOptions);
        }
        
        public static STTextAutonumberScheme parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTextAutonumberScheme)getTypeLoader().parse(inputStream, STTextAutonumberScheme.type, (XmlOptions)null);
        }
        
        public static STTextAutonumberScheme parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextAutonumberScheme)getTypeLoader().parse(inputStream, STTextAutonumberScheme.type, xmlOptions);
        }
        
        public static STTextAutonumberScheme parse(final Reader reader) throws XmlException, IOException {
            return (STTextAutonumberScheme)getTypeLoader().parse(reader, STTextAutonumberScheme.type, (XmlOptions)null);
        }
        
        public static STTextAutonumberScheme parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextAutonumberScheme)getTypeLoader().parse(reader, STTextAutonumberScheme.type, xmlOptions);
        }
        
        public static STTextAutonumberScheme parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTextAutonumberScheme)getTypeLoader().parse(xmlStreamReader, STTextAutonumberScheme.type, (XmlOptions)null);
        }
        
        public static STTextAutonumberScheme parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTextAutonumberScheme)getTypeLoader().parse(xmlStreamReader, STTextAutonumberScheme.type, xmlOptions);
        }
        
        public static STTextAutonumberScheme parse(final Node node) throws XmlException {
            return (STTextAutonumberScheme)getTypeLoader().parse(node, STTextAutonumberScheme.type, (XmlOptions)null);
        }
        
        public static STTextAutonumberScheme parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTextAutonumberScheme)getTypeLoader().parse(node, STTextAutonumberScheme.type, xmlOptions);
        }
        
        @Deprecated
        public static STTextAutonumberScheme parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTextAutonumberScheme)getTypeLoader().parse(xmlInputStream, STTextAutonumberScheme.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTextAutonumberScheme parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTextAutonumberScheme)getTypeLoader().parse(xmlInputStream, STTextAutonumberScheme.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextAutonumberScheme.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextAutonumberScheme.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_ALPHA_LC_PAREN_BOTH = 1;
        static final int INT_ALPHA_UC_PAREN_BOTH = 2;
        static final int INT_ALPHA_LC_PAREN_R = 3;
        static final int INT_ALPHA_UC_PAREN_R = 4;
        static final int INT_ALPHA_LC_PERIOD = 5;
        static final int INT_ALPHA_UC_PERIOD = 6;
        static final int INT_ARABIC_PAREN_BOTH = 7;
        static final int INT_ARABIC_PAREN_R = 8;
        static final int INT_ARABIC_PERIOD = 9;
        static final int INT_ARABIC_PLAIN = 10;
        static final int INT_ROMAN_LC_PAREN_BOTH = 11;
        static final int INT_ROMAN_UC_PAREN_BOTH = 12;
        static final int INT_ROMAN_LC_PAREN_R = 13;
        static final int INT_ROMAN_UC_PAREN_R = 14;
        static final int INT_ROMAN_LC_PERIOD = 15;
        static final int INT_ROMAN_UC_PERIOD = 16;
        static final int INT_CIRCLE_NUM_DB_PLAIN = 17;
        static final int INT_CIRCLE_NUM_WD_BLACK_PLAIN = 18;
        static final int INT_CIRCLE_NUM_WD_WHITE_PLAIN = 19;
        static final int INT_ARABIC_DB_PERIOD = 20;
        static final int INT_ARABIC_DB_PLAIN = 21;
        static final int INT_EA_1_CHS_PERIOD = 22;
        static final int INT_EA_1_CHS_PLAIN = 23;
        static final int INT_EA_1_CHT_PERIOD = 24;
        static final int INT_EA_1_CHT_PLAIN = 25;
        static final int INT_EA_1_JPN_CHS_DB_PERIOD = 26;
        static final int INT_EA_1_JPN_KOR_PLAIN = 27;
        static final int INT_EA_1_JPN_KOR_PERIOD = 28;
        static final int INT_ARABIC_1_MINUS = 29;
        static final int INT_ARABIC_2_MINUS = 30;
        static final int INT_HEBREW_2_MINUS = 31;
        static final int INT_THAI_ALPHA_PERIOD = 32;
        static final int INT_THAI_ALPHA_PAREN_R = 33;
        static final int INT_THAI_ALPHA_PAREN_BOTH = 34;
        static final int INT_THAI_NUM_PERIOD = 35;
        static final int INT_THAI_NUM_PAREN_R = 36;
        static final int INT_THAI_NUM_PAREN_BOTH = 37;
        static final int INT_HINDI_ALPHA_PERIOD = 38;
        static final int INT_HINDI_NUM_PERIOD = 39;
        static final int INT_HINDI_NUM_PAREN_R = 40;
        static final int INT_HINDI_ALPHA_1_PERIOD = 41;
        public static final StringEnumAbstractBase.Table table;
        private static final long serialVersionUID = 1L;
        
        public static Enum forString(final String s) {
            return (Enum)Enum.table.forString(s);
        }
        
        public static Enum forInt(final int n) {
            return (Enum)Enum.table.forInt(n);
        }
        
        private Enum(final String s, final int n) {
            super(s, n);
        }
        
        private Object readResolve() {
            return forInt(this.intValue());
        }
        
        static {
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("alphaLcParenBoth", 1), new Enum("alphaUcParenBoth", 2), new Enum("alphaLcParenR", 3), new Enum("alphaUcParenR", 4), new Enum("alphaLcPeriod", 5), new Enum("alphaUcPeriod", 6), new Enum("arabicParenBoth", 7), new Enum("arabicParenR", 8), new Enum("arabicPeriod", 9), new Enum("arabicPlain", 10), new Enum("romanLcParenBoth", 11), new Enum("romanUcParenBoth", 12), new Enum("romanLcParenR", 13), new Enum("romanUcParenR", 14), new Enum("romanLcPeriod", 15), new Enum("romanUcPeriod", 16), new Enum("circleNumDbPlain", 17), new Enum("circleNumWdBlackPlain", 18), new Enum("circleNumWdWhitePlain", 19), new Enum("arabicDbPeriod", 20), new Enum("arabicDbPlain", 21), new Enum("ea1ChsPeriod", 22), new Enum("ea1ChsPlain", 23), new Enum("ea1ChtPeriod", 24), new Enum("ea1ChtPlain", 25), new Enum("ea1JpnChsDbPeriod", 26), new Enum("ea1JpnKorPlain", 27), new Enum("ea1JpnKorPeriod", 28), new Enum("arabic1Minus", 29), new Enum("arabic2Minus", 30), new Enum("hebrew2Minus", 31), new Enum("thaiAlphaPeriod", 32), new Enum("thaiAlphaParenR", 33), new Enum("thaiAlphaParenBoth", 34), new Enum("thaiNumPeriod", 35), new Enum("thaiNumParenR", 36), new Enum("thaiNumParenBoth", 37), new Enum("hindiAlphaPeriod", 38), new Enum("hindiNumPeriod", 39), new Enum("hindiNumParenR", 40), new Enum("hindiAlpha1Period", 41) });
        }
    }
}
