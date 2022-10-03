package org.apache.poi.xwpf.usermodel;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLanguage;
import java.util.Iterator;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPrDefault;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrDefault;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocDefaults;
import java.io.OutputStream;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlOptions;
import java.io.IOException;
import java.io.InputStream;
import org.apache.xmlbeans.XmlException;
import org.apache.poi.ooxml.POIXMLException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.StylesDocument;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import java.util.ArrayList;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.util.List;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyles;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public class XWPFStyles extends POIXMLDocumentPart
{
    private CTStyles ctStyles;
    private List<XWPFStyle> listStyle;
    private XWPFLatentStyles latentStyles;
    private XWPFDefaultRunStyle defaultRunStyle;
    private XWPFDefaultParagraphStyle defaultParaStyle;
    
    public XWPFStyles(final PackagePart part) {
        super(part);
        this.listStyle = new ArrayList<XWPFStyle>();
    }
    
    public XWPFStyles() {
        this.listStyle = new ArrayList<XWPFStyle>();
    }
    
    @Override
    protected void onDocumentRead() throws IOException {
        try (final InputStream is = this.getPackagePart().getInputStream()) {
            final StylesDocument stylesDoc = StylesDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this.setStyles(stylesDoc.getStyles());
            this.latentStyles = new XWPFLatentStyles(this.ctStyles.getLatentStyles(), this);
        }
        catch (final XmlException e) {
            throw new POIXMLException("Unable to read styles", (Throwable)e);
        }
    }
    
    @Override
    protected void commit() throws IOException {
        if (this.ctStyles == null) {
            throw new IllegalStateException("Unable to write out styles that were never read in!");
        }
        final XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTStyles.type.getName().getNamespaceURI(), "styles"));
        final PackagePart part = this.getPackagePart();
        final OutputStream out = part.getOutputStream();
        this.ctStyles.save(out, xmlOptions);
        out.close();
    }
    
    protected void ensureDocDefaults() {
        if (!this.ctStyles.isSetDocDefaults()) {
            this.ctStyles.addNewDocDefaults();
        }
        final CTDocDefaults docDefaults = this.ctStyles.getDocDefaults();
        if (!docDefaults.isSetPPrDefault()) {
            docDefaults.addNewPPrDefault();
        }
        if (!docDefaults.isSetRPrDefault()) {
            docDefaults.addNewRPrDefault();
        }
        final CTPPrDefault pprd = docDefaults.getPPrDefault();
        final CTRPrDefault rprd = docDefaults.getRPrDefault();
        if (!pprd.isSetPPr()) {
            pprd.addNewPPr();
        }
        if (!rprd.isSetRPr()) {
            rprd.addNewRPr();
        }
        this.defaultRunStyle = new XWPFDefaultRunStyle(rprd.getRPr());
        this.defaultParaStyle = new XWPFDefaultParagraphStyle(pprd.getPPr());
    }
    
    public void setStyles(final CTStyles styles) {
        this.ctStyles = styles;
        for (final CTStyle style : this.ctStyles.getStyleArray()) {
            this.listStyle.add(new XWPFStyle(style, this));
        }
        if (this.ctStyles.isSetDocDefaults()) {
            final CTDocDefaults docDefaults = this.ctStyles.getDocDefaults();
            if (docDefaults.isSetRPrDefault() && docDefaults.getRPrDefault().isSetRPr()) {
                this.defaultRunStyle = new XWPFDefaultRunStyle(docDefaults.getRPrDefault().getRPr());
            }
            if (docDefaults.isSetPPrDefault() && docDefaults.getPPrDefault().isSetPPr()) {
                this.defaultParaStyle = new XWPFDefaultParagraphStyle(docDefaults.getPPrDefault().getPPr());
            }
        }
    }
    
    public boolean styleExist(final String styleID) {
        for (final XWPFStyle style : this.listStyle) {
            if (style.getStyleId().equals(styleID)) {
                return true;
            }
        }
        return false;
    }
    
    public void addStyle(final XWPFStyle style) {
        this.listStyle.add(style);
        this.ctStyles.addNewStyle();
        final int pos = this.ctStyles.sizeOfStyleArray() - 1;
        this.ctStyles.setStyleArray(pos, style.getCTStyle());
    }
    
    public XWPFStyle getStyle(final String styleID) {
        for (final XWPFStyle style : this.listStyle) {
            try {
                if (style.getStyleId().equals(styleID)) {
                    return style;
                }
                continue;
            }
            catch (final NullPointerException ex) {}
        }
        return null;
    }
    
    public int getNumberOfStyles() {
        return this.listStyle.size();
    }
    
    public List<XWPFStyle> getUsedStyleList(final XWPFStyle style) {
        final List<XWPFStyle> usedStyleList = new ArrayList<XWPFStyle>();
        usedStyleList.add(style);
        return this.getUsedStyleList(style, usedStyleList);
    }
    
    private List<XWPFStyle> getUsedStyleList(final XWPFStyle style, final List<XWPFStyle> usedStyleList) {
        final String basisStyleID = style.getBasisStyleID();
        final XWPFStyle basisStyle = this.getStyle(basisStyleID);
        if (basisStyle != null && !usedStyleList.contains(basisStyle)) {
            usedStyleList.add(basisStyle);
            this.getUsedStyleList(basisStyle, usedStyleList);
        }
        final String linkStyleID = style.getLinkStyleID();
        final XWPFStyle linkStyle = this.getStyle(linkStyleID);
        if (linkStyle != null && !usedStyleList.contains(linkStyle)) {
            usedStyleList.add(linkStyle);
            this.getUsedStyleList(linkStyle, usedStyleList);
        }
        final String nextStyleID = style.getNextStyleID();
        final XWPFStyle nextStyle = this.getStyle(nextStyleID);
        if (nextStyle != null && !usedStyleList.contains(nextStyle)) {
            usedStyleList.add(linkStyle);
            this.getUsedStyleList(linkStyle, usedStyleList);
        }
        return usedStyleList;
    }
    
    protected CTLanguage getCTLanguage() {
        this.ensureDocDefaults();
        CTLanguage lang = null;
        if (this.defaultRunStyle.getRPr().isSetLang()) {
            lang = this.defaultRunStyle.getRPr().getLang();
        }
        else {
            lang = this.defaultRunStyle.getRPr().addNewLang();
        }
        return lang;
    }
    
    public void setSpellingLanguage(final String strSpellingLanguage) {
        final CTLanguage lang = this.getCTLanguage();
        lang.setVal((Object)strSpellingLanguage);
        lang.setBidi((Object)strSpellingLanguage);
    }
    
    public void setEastAsia(final String strEastAsia) {
        final CTLanguage lang = this.getCTLanguage();
        lang.setEastAsia((Object)strEastAsia);
    }
    
    public void setDefaultFonts(final CTFonts fonts) {
        this.ensureDocDefaults();
        final CTRPr runProps = this.defaultRunStyle.getRPr();
        runProps.setRFonts(fonts);
    }
    
    public XWPFStyle getStyleWithSameName(final XWPFStyle style) {
        for (final XWPFStyle ownStyle : this.listStyle) {
            if (ownStyle.hasSameName(style)) {
                return ownStyle;
            }
        }
        return null;
    }
    
    public XWPFDefaultRunStyle getDefaultRunStyle() {
        this.ensureDocDefaults();
        return this.defaultRunStyle;
    }
    
    public XWPFDefaultParagraphStyle getDefaultParagraphStyle() {
        this.ensureDocDefaults();
        return this.defaultParaStyle;
    }
    
    public XWPFLatentStyles getLatentStyles() {
        return this.latentStyles;
    }
    
    public XWPFStyle getStyleWithName(final String styleName) {
        XWPFStyle style = null;
        for (final XWPFStyle cand : this.listStyle) {
            if (styleName.equals(cand.getName())) {
                style = cand;
                break;
            }
        }
        return style;
    }
}
