package org.apache.poi.xwpf.usermodel;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEm;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STEm;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHighlight;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextScale;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdnRef;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEmpty;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPTab;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFCheckBox;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFldChar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRubyContent;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRuby;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetGeometry2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlip;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualPictureProperties;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPictureNonVisual;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObjectData;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.openxmlformats.schemas.drawingml.x2006.main.STShapeType;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.w3c.dom.Node;
import org.apache.xmlbeans.XmlToken;
import org.apache.poi.ooxml.util.DocumentHelper;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import java.io.InputStream;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBrClear;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBrType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSignedHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSignedTwipsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHighlightColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHpsMeasure;
import java.math.BigInteger;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalAlignRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalAlignRun;
import org.apache.poi.util.Removal;
import org.apache.poi.util.HexDump;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STThemeColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHexColorRGB;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHexColorAuto;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STUnderline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTUnderline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLanguage;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.apache.poi.util.Internal;
import org.apache.xmlbeans.XmlException;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.apache.xmlbeans.XmlCursor;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlString;
import org.w3c.dom.NodeList;
import java.util.Iterator;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTAnchor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture;
import org.w3c.dom.Text;
import java.util.Collection;
import java.util.Arrays;
import org.apache.xmlbeans.XmlObject;
import java.util.ArrayList;
import java.util.List;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.apache.poi.wp.usermodel.CharacterRun;

public class XWPFRun implements ISDTContents, IRunElement, CharacterRun
{
    private CTR run;
    private String pictureText;
    private IRunBody parent;
    private List<XWPFPicture> pictures;
    
    public XWPFRun(final CTR r, final IRunBody p) {
        this.run = r;
        this.parent = p;
        for (final CTDrawing ctDrawing : r.getDrawingArray()) {
            for (final CTAnchor anchor : ctDrawing.getAnchorArray()) {
                if (anchor.getDocPr() != null) {
                    this.getDocument().getDrawingIdManager().reserve(anchor.getDocPr().getId());
                }
            }
            for (final CTInline inline : ctDrawing.getInlineArray()) {
                if (inline.getDocPr() != null) {
                    this.getDocument().getDrawingIdManager().reserve(inline.getDocPr().getId());
                }
            }
        }
        final StringBuilder text = new StringBuilder();
        final List<XmlObject> pictTextObjs = new ArrayList<XmlObject>();
        pictTextObjs.addAll((Collection<? extends XmlObject>)Arrays.asList(r.getPictArray()));
        pictTextObjs.addAll((Collection<? extends XmlObject>)Arrays.asList(r.getDrawingArray()));
        for (final XmlObject o : pictTextObjs) {
            final XmlObject[] selectPath;
            final XmlObject[] ts = selectPath = o.selectPath("declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' .//w:t");
            for (final XmlObject t : selectPath) {
                final NodeList kids = t.getDomNode().getChildNodes();
                for (int n = 0; n < kids.getLength(); ++n) {
                    if (kids.item(n) instanceof Text) {
                        if (text.length() > 0) {
                            text.append("\n");
                        }
                        text.append(kids.item(n).getNodeValue());
                    }
                }
            }
        }
        this.pictureText = text.toString();
        this.pictures = new ArrayList<XWPFPicture>();
        for (final XmlObject o : pictTextObjs) {
            for (final CTPicture pict : this.getCTPictures(o)) {
                final XWPFPicture picture = new XWPFPicture(pict, this);
                this.pictures.add(picture);
            }
        }
    }
    
    @Deprecated
    public XWPFRun(final CTR r, final XWPFParagraph p) {
        this(r, (IRunBody)p);
    }
    
    static void preserveSpaces(final XmlString xs) {
        final String text = xs.getStringValue();
        if (text != null && text.length() >= 1 && (Character.isWhitespace(text.charAt(0)) || Character.isWhitespace(text.charAt(text.length() - 1)))) {
            final XmlCursor c = xs.newCursor();
            c.toNextToken();
            c.insertAttributeWithValue(new QName("http://www.w3.org/XML/1998/namespace", "space"), "preserve");
            c.dispose();
        }
    }
    
    private List<CTPicture> getCTPictures(final XmlObject o) {
        final List<CTPicture> pics = new ArrayList<CTPicture>();
        final XmlObject[] selectPath;
        final XmlObject[] picts = selectPath = o.selectPath("declare namespace pic='" + CTPicture.type.getName().getNamespaceURI() + "' .//pic:pic");
        for (XmlObject pict : selectPath) {
            if (pict instanceof XmlAnyTypeImpl) {
                try {
                    pict = (XmlObject)CTPicture.Factory.parse(pict.toString(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                }
                catch (final XmlException e) {
                    throw new POIXMLException((Throwable)e);
                }
            }
            if (pict instanceof CTPicture) {
                pics.add((CTPicture)pict);
            }
        }
        return pics;
    }
    
    @Internal
    public CTR getCTR() {
        return this.run;
    }
    
    public IRunBody getParent() {
        return this.parent;
    }
    
    @Deprecated
    public XWPFParagraph getParagraph() {
        if (this.parent instanceof XWPFParagraph) {
            return (XWPFParagraph)this.parent;
        }
        return null;
    }
    
    public XWPFDocument getDocument() {
        if (this.parent != null) {
            return this.parent.getDocument();
        }
        return null;
    }
    
    private static boolean isCTOnOff(final CTOnOff onoff) {
        if (!onoff.isSetVal()) {
            return true;
        }
        final STOnOff.Enum val = onoff.getVal();
        return STOnOff.TRUE == val || STOnOff.X_1 == val || STOnOff.ON == val;
    }
    
    public String getLang() {
        final CTRPr pr = this.getRunProperties(false);
        final Object lang = (pr == null || !pr.isSetLang()) ? null : pr.getLang().getVal();
        return (String)lang;
    }
    
    public void setLang(final String lang) {
        final CTRPr pr = this.getRunProperties(true);
        final CTLanguage ctLang = pr.isSetLang() ? pr.getLang() : pr.addNewLang();
        ctLang.setVal((Object)lang);
    }
    
    public boolean isBold() {
        final CTRPr pr = this.getRunProperties(false);
        return pr != null && pr.isSetB() && isCTOnOff(pr.getB());
    }
    
    public void setBold(final boolean value) {
        final CTRPr pr = this.getRunProperties(true);
        final CTOnOff bold = pr.isSetB() ? pr.getB() : pr.addNewB();
        bold.setVal(value ? STOnOff.TRUE : STOnOff.FALSE);
    }
    
    public String getColor() {
        String color = null;
        if (this.run.isSetRPr()) {
            final CTRPr pr = this.getRunProperties(false);
            if (pr != null && pr.isSetColor()) {
                final CTColor clr = pr.getColor();
                color = clr.xgetVal().getStringValue();
            }
        }
        return color;
    }
    
    public void setColor(final String rgbStr) {
        final CTRPr pr = this.getRunProperties(true);
        final CTColor color = pr.isSetColor() ? pr.getColor() : pr.addNewColor();
        color.setVal((Object)rgbStr);
    }
    
    public String getText(final int pos) {
        return (this.run.sizeOfTArray() == 0) ? null : this.run.getTArray(pos).getStringValue();
    }
    
    public String getPictureText() {
        return this.pictureText;
    }
    
    public void setText(final String value) {
        this.setText(value, this.run.sizeOfTArray());
    }
    
    public void setText(final String value, final int pos) {
        if (pos > this.run.sizeOfTArray()) {
            throw new ArrayIndexOutOfBoundsException("Value too large for the parameter position in XWPFRun.setText(String value,int pos)");
        }
        final CTText t = (pos < this.run.sizeOfTArray() && pos >= 0) ? this.run.getTArray(pos) : this.run.addNewT();
        t.setStringValue(value);
        preserveSpaces((XmlString)t);
    }
    
    public boolean isItalic() {
        final CTRPr pr = this.getRunProperties(false);
        return pr != null && pr.isSetI() && isCTOnOff(pr.getI());
    }
    
    public void setItalic(final boolean value) {
        final CTRPr pr = this.getRunProperties(true);
        final CTOnOff italic = pr.isSetI() ? pr.getI() : pr.addNewI();
        italic.setVal(value ? STOnOff.TRUE : STOnOff.FALSE);
    }
    
    public UnderlinePatterns getUnderline() {
        UnderlinePatterns value = UnderlinePatterns.NONE;
        final CTUnderline underline = this.getCTUnderline(false);
        if (underline != null) {
            final STUnderline.Enum baseValue = underline.getVal();
            if (baseValue != null) {
                value = UnderlinePatterns.valueOf(baseValue.intValue());
            }
        }
        return value;
    }
    
    public void setUnderline(final UnderlinePatterns value) {
        final CTUnderline underline = this.getCTUnderline(true);
        underline.setVal(STUnderline.Enum.forInt(value.getValue()));
    }
    
    private CTUnderline getCTUnderline(final boolean create) {
        final CTRPr pr = this.getRunProperties(true);
        CTUnderline underline = pr.getU();
        if (create && underline == null) {
            underline = pr.addNewU();
        }
        return underline;
    }
    
    public void setUnderlineColor(final String color) {
        final CTUnderline underline = this.getCTUnderline(true);
        SimpleValue svColor = null;
        if (color.equals("auto")) {
            final STHexColorAuto hexColor = STHexColorAuto.Factory.newInstance();
            hexColor.set((StringEnumAbstractBase)STHexColorAuto.Enum.forString(color));
            svColor = (SimpleValue)hexColor;
        }
        else {
            final STHexColorRGB rgbColor = STHexColorRGB.Factory.newInstance();
            rgbColor.setStringValue(color);
            svColor = (SimpleValue)rgbColor;
        }
        underline.setColor((Object)svColor);
    }
    
    public void setUnderlineThemeColor(final String themeColor) {
        final CTUnderline underline = this.getCTUnderline(true);
        final STThemeColor.Enum val = STThemeColor.Enum.forString(themeColor);
        if (val != null) {
            underline.setThemeColor(val);
        }
    }
    
    public STThemeColor.Enum getUnderlineThemeColor() {
        final CTUnderline underline = this.getCTUnderline(false);
        STThemeColor.Enum color = STThemeColor.NONE;
        if (underline != null) {
            color = underline.getThemeColor();
        }
        return color;
    }
    
    public String getUnderlineColor() {
        final CTUnderline underline = this.getCTUnderline(true);
        String colorName = "auto";
        final Object rawValue = underline.getColor();
        if (rawValue != null) {
            if (rawValue instanceof String) {
                colorName = (String)rawValue;
            }
            else {
                final byte[] rgbColor = (byte[])rawValue;
                colorName = HexDump.toHex(rgbColor[0]) + HexDump.toHex(rgbColor[1]) + HexDump.toHex(rgbColor[2]);
            }
        }
        return colorName;
    }
    
    public boolean isStrikeThrough() {
        final CTRPr pr = this.getRunProperties(false);
        return pr != null && pr.isSetStrike() && isCTOnOff(pr.getStrike());
    }
    
    public void setStrikeThrough(final boolean value) {
        final CTRPr pr = this.getRunProperties(true);
        final CTOnOff strike = pr.isSetStrike() ? pr.getStrike() : pr.addNewStrike();
        strike.setVal(value ? STOnOff.TRUE : STOnOff.FALSE);
    }
    
    @Deprecated
    public boolean isStrike() {
        return this.isStrikeThrough();
    }
    
    @Deprecated
    public void setStrike(final boolean value) {
        this.setStrikeThrough(value);
    }
    
    public boolean isDoubleStrikeThrough() {
        final CTRPr pr = this.getRunProperties(false);
        return pr != null && pr.isSetDstrike() && isCTOnOff(pr.getDstrike());
    }
    
    public void setDoubleStrikethrough(final boolean value) {
        final CTRPr pr = this.getRunProperties(true);
        final CTOnOff dstrike = pr.isSetDstrike() ? pr.getDstrike() : pr.addNewDstrike();
        dstrike.setVal(value ? STOnOff.TRUE : STOnOff.FALSE);
    }
    
    public boolean isSmallCaps() {
        final CTRPr pr = this.getRunProperties(false);
        return pr != null && pr.isSetSmallCaps() && isCTOnOff(pr.getSmallCaps());
    }
    
    public void setSmallCaps(final boolean value) {
        final CTRPr pr = this.getRunProperties(true);
        final CTOnOff caps = pr.isSetSmallCaps() ? pr.getSmallCaps() : pr.addNewSmallCaps();
        caps.setVal(value ? STOnOff.TRUE : STOnOff.FALSE);
    }
    
    public boolean isCapitalized() {
        final CTRPr pr = this.getRunProperties(false);
        return pr != null && pr.isSetCaps() && isCTOnOff(pr.getCaps());
    }
    
    public void setCapitalized(final boolean value) {
        final CTRPr pr = this.getRunProperties(true);
        final CTOnOff caps = pr.isSetCaps() ? pr.getCaps() : pr.addNewCaps();
        caps.setVal(value ? STOnOff.TRUE : STOnOff.FALSE);
    }
    
    public boolean isShadowed() {
        final CTRPr pr = this.getRunProperties(false);
        return pr != null && pr.isSetShadow() && isCTOnOff(pr.getShadow());
    }
    
    public void setShadow(final boolean value) {
        final CTRPr pr = this.getRunProperties(true);
        final CTOnOff shadow = pr.isSetShadow() ? pr.getShadow() : pr.addNewShadow();
        shadow.setVal(value ? STOnOff.TRUE : STOnOff.FALSE);
    }
    
    public boolean isImprinted() {
        final CTRPr pr = this.getRunProperties(false);
        return pr != null && pr.isSetImprint() && isCTOnOff(pr.getImprint());
    }
    
    public void setImprinted(final boolean value) {
        final CTRPr pr = this.getRunProperties(true);
        final CTOnOff imprinted = pr.isSetImprint() ? pr.getImprint() : pr.addNewImprint();
        imprinted.setVal(value ? STOnOff.TRUE : STOnOff.FALSE);
    }
    
    public boolean isEmbossed() {
        final CTRPr pr = this.getRunProperties(false);
        return pr != null && pr.isSetEmboss() && isCTOnOff(pr.getEmboss());
    }
    
    public void setEmbossed(final boolean value) {
        final CTRPr pr = this.getRunProperties(true);
        final CTOnOff emboss = pr.isSetEmboss() ? pr.getEmboss() : pr.addNewEmboss();
        emboss.setVal(value ? STOnOff.TRUE : STOnOff.FALSE);
    }
    
    @Removal(version = "4.2")
    @Deprecated
    public VerticalAlign getSubscript() {
        final CTRPr pr = this.getRunProperties(false);
        return (pr != null && pr.isSetVertAlign()) ? VerticalAlign.valueOf(pr.getVertAlign().getVal().intValue()) : VerticalAlign.BASELINE;
    }
    
    public void setSubscript(final VerticalAlign valign) {
        final CTRPr pr = this.getRunProperties(true);
        final CTVerticalAlignRun ctValign = pr.isSetVertAlign() ? pr.getVertAlign() : pr.addNewVertAlign();
        ctValign.setVal(STVerticalAlignRun.Enum.forInt(valign.getValue()));
    }
    
    public int getKerning() {
        final CTRPr pr = this.getRunProperties(false);
        if (pr == null || !pr.isSetKern()) {
            return 0;
        }
        return pr.getKern().getVal().intValue();
    }
    
    public void setKerning(final int kern) {
        final CTRPr pr = this.getRunProperties(true);
        final CTHpsMeasure kernmes = pr.isSetKern() ? pr.getKern() : pr.addNewKern();
        kernmes.setVal(BigInteger.valueOf(kern));
    }
    
    public boolean isHighlighted() {
        final CTRPr pr = this.getRunProperties(false);
        if (pr == null || !pr.isSetHighlight()) {
            return false;
        }
        final STHighlightColor.Enum val = pr.getHighlight().getVal();
        return val != null && val != STHighlightColor.NONE;
    }
    
    public int getCharacterSpacing() {
        final CTRPr pr = this.getRunProperties(false);
        if (pr == null || !pr.isSetSpacing()) {
            return 0;
        }
        return pr.getSpacing().getVal().intValue();
    }
    
    public void setCharacterSpacing(final int twips) {
        final CTRPr pr = this.getRunProperties(true);
        final CTSignedTwipsMeasure spc = pr.isSetSpacing() ? pr.getSpacing() : pr.addNewSpacing();
        spc.setVal(BigInteger.valueOf(twips));
    }
    
    public String getFontFamily() {
        return this.getFontFamily(null);
    }
    
    public void setFontFamily(final String fontFamily) {
        this.setFontFamily(fontFamily, null);
    }
    
    public String getFontName() {
        return this.getFontFamily();
    }
    
    public String getFontFamily(final FontCharRange fcr) {
        final CTRPr pr = this.getRunProperties(false);
        if (pr == null || !pr.isSetRFonts()) {
            return null;
        }
        final CTFonts fonts = pr.getRFonts();
        switch ((fcr == null) ? FontCharRange.ascii : fcr) {
            default: {
                return fonts.getAscii();
            }
            case cs: {
                return fonts.getCs();
            }
            case eastAsia: {
                return fonts.getEastAsia();
            }
            case hAnsi: {
                return fonts.getHAnsi();
            }
        }
    }
    
    public void setFontFamily(final String fontFamily, final FontCharRange fcr) {
        final CTRPr pr = this.getRunProperties(true);
        final CTFonts fonts = pr.isSetRFonts() ? pr.getRFonts() : pr.addNewRFonts();
        if (fcr == null) {
            fonts.setAscii(fontFamily);
            if (!fonts.isSetHAnsi()) {
                fonts.setHAnsi(fontFamily);
            }
            if (!fonts.isSetCs()) {
                fonts.setCs(fontFamily);
            }
            if (!fonts.isSetEastAsia()) {
                fonts.setEastAsia(fontFamily);
            }
        }
        else {
            switch (fcr) {
                case ascii: {
                    fonts.setAscii(fontFamily);
                    break;
                }
                case cs: {
                    fonts.setCs(fontFamily);
                    break;
                }
                case eastAsia: {
                    fonts.setEastAsia(fontFamily);
                    break;
                }
                case hAnsi: {
                    fonts.setHAnsi(fontFamily);
                    break;
                }
            }
        }
    }
    
    public int getFontSize() {
        final CTRPr pr = this.getRunProperties(false);
        return (pr != null && pr.isSetSz()) ? pr.getSz().getVal().divide(new BigInteger("2")).intValue() : -1;
    }
    
    public void setFontSize(final int size) {
        final BigInteger bint = new BigInteger(Integer.toString(size));
        final CTRPr pr = this.getRunProperties(true);
        final CTHpsMeasure ctSize = pr.isSetSz() ? pr.getSz() : pr.addNewSz();
        ctSize.setVal(bint.multiply(new BigInteger("2")));
    }
    
    public int getTextPosition() {
        final CTRPr pr = this.getRunProperties(false);
        return (pr != null && pr.isSetPosition()) ? pr.getPosition().getVal().intValue() : -1;
    }
    
    public void setTextPosition(final int val) {
        final BigInteger bint = new BigInteger(Integer.toString(val));
        final CTRPr pr = this.getRunProperties(true);
        final CTSignedHpsMeasure position = pr.isSetPosition() ? pr.getPosition() : pr.addNewPosition();
        position.setVal(bint);
    }
    
    public void removeBreak() {
    }
    
    public void addBreak() {
        this.run.addNewBr();
    }
    
    public void addBreak(final BreakType type) {
        final CTBr br = this.run.addNewBr();
        br.setType(STBrType.Enum.forInt(type.getValue()));
    }
    
    public void addBreak(final BreakClear clear) {
        final CTBr br = this.run.addNewBr();
        br.setType(STBrType.Enum.forInt(BreakType.TEXT_WRAPPING.getValue()));
        br.setClear(STBrClear.Enum.forInt(clear.getValue()));
    }
    
    public void addTab() {
        this.run.addNewTab();
    }
    
    public void removeTab() {
    }
    
    public void addCarriageReturn() {
        this.run.addNewCr();
    }
    
    public void removeCarriageReturn() {
    }
    
    public XWPFPicture addPicture(final InputStream pictureData, final int pictureType, final String filename, final int width, final int height) throws InvalidFormatException, IOException {
        XWPFPictureData picData;
        if (this.parent.getPart() instanceof XWPFHeaderFooter) {
            final XWPFHeaderFooter headerFooter = (XWPFHeaderFooter)this.parent.getPart();
            final String relationId = headerFooter.addPictureData(pictureData, pictureType);
            picData = (XWPFPictureData)headerFooter.getRelationById(relationId);
        }
        else {
            final XWPFDocument doc = this.parent.getDocument();
            final String relationId = doc.addPictureData(pictureData, pictureType);
            picData = (XWPFPictureData)doc.getRelationById(relationId);
        }
        try {
            final CTDrawing drawing = this.run.addNewDrawing();
            final CTInline inline = drawing.addNewInline();
            final String xml = "<a:graphic xmlns:a=\"" + CTGraphicalObject.type.getName().getNamespaceURI() + "\"><a:graphicData uri=\"" + CTPicture.type.getName().getNamespaceURI() + "\"><pic:pic xmlns:pic=\"" + CTPicture.type.getName().getNamespaceURI() + "\" /></a:graphicData></a:graphic>";
            final InputSource is = new InputSource(new StringReader(xml));
            final Document doc2 = DocumentHelper.readDocument(is);
            inline.set((XmlObject)XmlToken.Factory.parse((Node)doc2.getDocumentElement(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS));
            inline.setDistT(0L);
            inline.setDistR(0L);
            inline.setDistB(0L);
            inline.setDistL(0L);
            final CTNonVisualDrawingProps docPr = inline.addNewDocPr();
            final long id = this.getParent().getDocument().getDrawingIdManager().reserveNew();
            docPr.setId(id);
            docPr.setName("Drawing " + id);
            docPr.setDescr(filename);
            final CTPositiveSize2D extent = inline.addNewExtent();
            extent.setCx((long)width);
            extent.setCy((long)height);
            final CTGraphicalObject graphic = inline.getGraphic();
            final CTGraphicalObjectData graphicData = graphic.getGraphicData();
            final CTPicture pic = this.getCTPictures((XmlObject)graphicData).get(0);
            final CTPictureNonVisual nvPicPr = pic.addNewNvPicPr();
            final CTNonVisualDrawingProps cNvPr = nvPicPr.addNewCNvPr();
            cNvPr.setId(0L);
            cNvPr.setName("Picture " + id);
            cNvPr.setDescr(filename);
            final CTNonVisualPictureProperties cNvPicPr = nvPicPr.addNewCNvPicPr();
            cNvPicPr.addNewPicLocks().setNoChangeAspect(true);
            final CTBlipFillProperties blipFill = pic.addNewBlipFill();
            final CTBlip blip = blipFill.addNewBlip();
            blip.setEmbed(this.parent.getPart().getRelationId(picData));
            blipFill.addNewStretch().addNewFillRect();
            final CTShapeProperties spPr = pic.addNewSpPr();
            final CTTransform2D xfrm = spPr.addNewXfrm();
            final CTPoint2D off = xfrm.addNewOff();
            off.setX(0L);
            off.setY(0L);
            final CTPositiveSize2D ext = xfrm.addNewExt();
            ext.setCx((long)width);
            ext.setCy((long)height);
            final CTPresetGeometry2D prstGeom = spPr.addNewPrstGeom();
            prstGeom.setPrst(STShapeType.RECT);
            prstGeom.addNewAvLst();
            final XWPFPicture xwpfPicture = new XWPFPicture(pic, this);
            this.pictures.add(xwpfPicture);
            return xwpfPicture;
        }
        catch (final XmlException | SAXException e) {
            throw new IllegalStateException(e);
        }
    }
    
    @Internal
    public CTInline addChart(final String chartRelId) throws InvalidFormatException, IOException {
        try {
            final CTInline inline = this.run.addNewDrawing().addNewInline();
            final String xml = "<a:graphic xmlns:a=\"" + CTGraphicalObject.type.getName().getNamespaceURI() + "\"><a:graphicData uri=\"" + CTChart.type.getName().getNamespaceURI() + "\"><c:chart xmlns:c=\"" + CTChart.type.getName().getNamespaceURI() + "\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" r:id=\"" + chartRelId + "\" /></a:graphicData></a:graphic>";
            final InputSource is = new InputSource(new StringReader(xml));
            final Document doc = DocumentHelper.readDocument(is);
            inline.set((XmlObject)XmlToken.Factory.parse((Node)doc.getDocumentElement(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS));
            inline.setDistT(0L);
            inline.setDistR(0L);
            inline.setDistB(0L);
            inline.setDistL(0L);
            final CTNonVisualDrawingProps docPr = inline.addNewDocPr();
            final long id = this.getParent().getDocument().getDrawingIdManager().reserveNew();
            docPr.setId(id);
            docPr.setName("chart " + id);
            return inline;
        }
        catch (final XmlException | SAXException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public List<XWPFPicture> getEmbeddedPictures() {
        return this.pictures;
    }
    
    public void setStyle(final String styleId) {
        CTRPr pr = this.getCTR().getRPr();
        if (null == pr) {
            pr = this.getCTR().addNewRPr();
        }
        final CTString style = (pr.getRStyle() != null) ? pr.getRStyle() : pr.addNewRStyle();
        style.setVal(styleId);
    }
    
    public String getStyle() {
        final CTRPr pr = this.getCTR().getRPr();
        if (pr == null) {
            return "";
        }
        final CTString style = pr.getRStyle();
        if (style == null) {
            return "";
        }
        return style.getVal();
    }
    
    @Override
    public String toString() {
        final String phonetic = this.getPhonetic();
        if (phonetic.length() > 0) {
            return this.text() + " (" + phonetic + ")";
        }
        return this.text();
    }
    
    public String text() {
        final StringBuilder text = new StringBuilder(64);
        final XmlCursor c = this.run.newCursor();
        c.selectPath("./*");
        while (c.toNextSelection()) {
            final XmlObject o = c.getObject();
            if (o instanceof CTRuby) {
                this.handleRuby(o, text, false);
            }
            else {
                this._getText(o, text);
            }
        }
        c.dispose();
        return text.toString();
    }
    
    public String getPhonetic() {
        final StringBuilder text = new StringBuilder(64);
        final XmlCursor c = this.run.newCursor();
        c.selectPath("./*");
        while (c.toNextSelection()) {
            final XmlObject o = c.getObject();
            if (o instanceof CTRuby) {
                this.handleRuby(o, text, true);
            }
        }
        if (this.pictureText != null && this.pictureText.length() > 0) {
            text.append("\n").append(this.pictureText).append("\n");
        }
        c.dispose();
        return text.toString();
    }
    
    private void handleRuby(final XmlObject rubyObj, final StringBuilder text, final boolean extractPhonetic) {
        final XmlCursor c = rubyObj.newCursor();
        c.selectPath(".//*");
        boolean inRT = false;
        boolean inBase = false;
        while (c.toNextSelection()) {
            final XmlObject o = c.getObject();
            if (o instanceof CTRubyContent) {
                final String tagName = o.getDomNode().getNodeName();
                if ("w:rt".equals(tagName)) {
                    inRT = true;
                }
                else {
                    if (!"w:rubyBase".equals(tagName)) {
                        continue;
                    }
                    inRT = false;
                    inBase = true;
                }
            }
            else if (extractPhonetic && inRT) {
                this._getText(o, text);
            }
            else {
                if (extractPhonetic || !inBase) {
                    continue;
                }
                this._getText(o, text);
            }
        }
        c.dispose();
    }
    
    private void _getText(final XmlObject o, final StringBuilder text) {
        if (o instanceof CTText) {
            final String tagName = o.getDomNode().getNodeName();
            if (!"w:instrText".equals(tagName)) {
                text.append(((CTText)o).getStringValue());
            }
        }
        if (o instanceof CTFldChar) {
            final CTFldChar ctfldChar = (CTFldChar)o;
            if (ctfldChar.getFldCharType() == STFldCharType.BEGIN && ctfldChar.getFfData() != null) {
                for (final CTFFCheckBox checkBox : ctfldChar.getFfData().getCheckBoxList()) {
                    if (checkBox.getDefault() != null && checkBox.getDefault().getVal() == STOnOff.X_1) {
                        text.append("|X|");
                    }
                    else {
                        text.append("|_|");
                    }
                }
            }
        }
        if (o instanceof CTPTab) {
            text.append('\t');
        }
        if (o instanceof CTBr) {
            text.append('\n');
        }
        if (o instanceof CTEmpty) {
            final String tagName = o.getDomNode().getNodeName();
            if ("w:tab".equals(tagName) || "tab".equals(tagName)) {
                text.append('\t');
            }
            if ("w:br".equals(tagName) || "br".equals(tagName)) {
                text.append('\n');
            }
            if ("w:cr".equals(tagName) || "cr".equals(tagName)) {
                text.append('\n');
            }
        }
        if (o instanceof CTFtnEdnRef) {
            final CTFtnEdnRef ftn = (CTFtnEdnRef)o;
            final String footnoteRef = ftn.getDomNode().getLocalName().equals("footnoteReference") ? ("[footnoteRef:" + ftn.getId().intValue() + "]") : ("[endnoteRef:" + ftn.getId().intValue() + "]");
            text.append(footnoteRef);
        }
    }
    
    public void setTextScale(final int percentage) {
        final CTRPr pr = this.getRunProperties(true);
        final CTTextScale scale = pr.isSetW() ? pr.getW() : pr.addNewW();
        scale.setVal(percentage);
    }
    
    public int getTextScale() {
        final CTRPr pr = this.getRunProperties(true);
        final CTTextScale scale = pr.isSetW() ? pr.getW() : pr.addNewW();
        int value = scale.getVal();
        if (value == 0) {
            value = 100;
        }
        return value;
    }
    
    public void setTextHighlightColor(final String colorName) {
        final CTRPr pr = this.getRunProperties(true);
        final CTHighlight highlight = pr.isSetHighlight() ? pr.getHighlight() : pr.addNewHighlight();
        STHighlightColor color = highlight.xgetVal();
        if (color == null) {
            color = STHighlightColor.Factory.newInstance();
        }
        final STHighlightColor.Enum val = STHighlightColor.Enum.forString(colorName);
        if (val != null) {
            color.setStringValue(val.toString());
            highlight.xsetVal(color);
        }
    }
    
    public STHighlightColor.Enum getTextHightlightColor() {
        final CTRPr pr = this.getRunProperties(true);
        final CTHighlight highlight = pr.isSetHighlight() ? pr.getHighlight() : pr.addNewHighlight();
        STHighlightColor color = highlight.xgetVal();
        if (color == null) {
            color = STHighlightColor.Factory.newInstance();
            color.set((StringEnumAbstractBase)STHighlightColor.NONE);
        }
        return (STHighlightColor.Enum)color.enumValue();
    }
    
    public boolean isVanish() {
        final CTRPr pr = this.getRunProperties(true);
        return pr != null && pr.isSetVanish() && isCTOnOff(pr.getVanish());
    }
    
    public void setVanish(final boolean value) {
        final CTRPr pr = this.getRunProperties(true);
        final CTOnOff vanish = pr.isSetVanish() ? pr.getVanish() : pr.addNewVanish();
        vanish.setVal(value ? STOnOff.TRUE : STOnOff.FALSE);
    }
    
    public STVerticalAlignRun.Enum getVerticalAlignment() {
        final CTRPr pr = this.getRunProperties(true);
        final CTVerticalAlignRun vertAlign = pr.isSetVertAlign() ? pr.getVertAlign() : pr.addNewVertAlign();
        STVerticalAlignRun.Enum val = vertAlign.getVal();
        if (val == null) {
            val = STVerticalAlignRun.BASELINE;
        }
        return val;
    }
    
    public void setVerticalAlignment(final String verticalAlignment) {
        final CTRPr pr = this.getRunProperties(true);
        final CTVerticalAlignRun vertAlign = pr.isSetVertAlign() ? pr.getVertAlign() : pr.addNewVertAlign();
        STVerticalAlignRun align = vertAlign.xgetVal();
        if (align == null) {
            align = STVerticalAlignRun.Factory.newInstance();
        }
        final STVerticalAlignRun.Enum val = STVerticalAlignRun.Enum.forString(verticalAlignment);
        if (val != null) {
            align.setStringValue(val.toString());
            vertAlign.xsetVal(align);
        }
    }
    
    public STEm.Enum getEmphasisMark() {
        final CTRPr pr = this.getRunProperties(true);
        final CTEm emphasis = pr.isSetEm() ? pr.getEm() : pr.addNewEm();
        STEm.Enum val = emphasis.getVal();
        if (val == null) {
            val = STEm.NONE;
        }
        return val;
    }
    
    public void setEmphasisMark(final String markType) {
        final CTRPr pr = this.getRunProperties(true);
        final CTEm emphasisMark = pr.isSetEm() ? pr.getEm() : pr.addNewEm();
        STEm mark = emphasisMark.xgetVal();
        if (mark == null) {
            mark = STEm.Factory.newInstance();
        }
        final STEm.Enum val = STEm.Enum.forString(markType);
        if (val != null) {
            mark.setStringValue(val.toString());
            emphasisMark.xsetVal(mark);
        }
    }
    
    protected CTRPr getRunProperties(final boolean create) {
        CTRPr pr = this.run.isSetRPr() ? this.run.getRPr() : null;
        if (create && pr == null) {
            pr = this.run.addNewRPr();
        }
        return pr;
    }
    
    public enum FontCharRange
    {
        ascii, 
        cs, 
        eastAsia, 
        hAnsi;
    }
}
