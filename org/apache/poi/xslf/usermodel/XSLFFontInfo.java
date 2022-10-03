package org.apache.poi.xslf.usermodel;

import java.io.OutputStream;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.util.IOUtils;
import org.apache.poi.ooxml.POIXMLFactory;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;
import java.io.IOException;
import org.openxmlformats.schemas.presentationml.x2006.main.CTEmbeddedFontDataId;
import org.apache.poi.common.usermodel.fonts.FontHeader;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.poi.common.usermodel.fonts.FontFacet;
import java.util.List;
import org.apache.poi.common.usermodel.fonts.FontPitch;
import org.apache.poi.common.usermodel.fonts.FontFamily;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.openxmlformats.schemas.presentationml.x2006.main.CTEmbeddedFontList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPresentation;
import org.openxmlformats.schemas.presentationml.x2006.main.CTEmbeddedFontListEntry;
import org.apache.poi.common.usermodel.fonts.FontInfo;

public class XSLFFontInfo implements FontInfo
{
    final XMLSlideShow ppt;
    final String typeface;
    final CTEmbeddedFontListEntry fontListEntry;
    
    public XSLFFontInfo(final XMLSlideShow ppt, final String typeface) {
        this.ppt = ppt;
        this.typeface = typeface;
        final CTPresentation pres = ppt.getCTPresentation();
        final CTEmbeddedFontList fontList = pres.isSetEmbeddedFontLst() ? pres.getEmbeddedFontLst() : pres.addNewEmbeddedFontLst();
        for (final CTEmbeddedFontListEntry fe : fontList.getEmbeddedFontArray()) {
            if (typeface.equalsIgnoreCase(fe.getFont().getTypeface())) {
                this.fontListEntry = fe;
                return;
            }
        }
        this.fontListEntry = fontList.addNewEmbeddedFont();
        this.fontListEntry.addNewFont().setTypeface(typeface);
    }
    
    public XSLFFontInfo(final XMLSlideShow ppt, final CTEmbeddedFontListEntry fontListEntry) {
        this.ppt = ppt;
        this.typeface = fontListEntry.getFont().getTypeface();
        this.fontListEntry = fontListEntry;
    }
    
    public String getTypeface() {
        return this.getFont().getTypeface();
    }
    
    public void setTypeface(final String typeface) {
        this.getFont().setTypeface(typeface);
    }
    
    public FontCharset getCharset() {
        return FontCharset.valueOf((int)this.getFont().getCharset());
    }
    
    public void setCharset(final FontCharset charset) {
        this.getFont().setCharset((byte)charset.getNativeId());
    }
    
    public FontFamily getFamily() {
        return FontFamily.valueOfPitchFamily(this.getFont().getPitchFamily());
    }
    
    public void setFamily(final FontFamily family) {
        final byte pitchAndFamily = this.getFont().getPitchFamily();
        final FontPitch pitch = FontPitch.valueOfPitchFamily(pitchAndFamily);
        this.getFont().setPitchFamily(FontPitch.getNativeId(pitch, family));
    }
    
    public FontPitch getPitch() {
        return FontPitch.valueOfPitchFamily(this.getFont().getPitchFamily());
    }
    
    public void setPitch(final FontPitch pitch) {
        final byte pitchAndFamily = this.getFont().getPitchFamily();
        final FontFamily family = FontFamily.valueOfPitchFamily(pitchAndFamily);
        this.getFont().setPitchFamily(FontPitch.getNativeId(pitch, family));
    }
    
    public byte[] getPanose() {
        return this.getFont().getPanose();
    }
    
    public List<FontFacet> getFacets() {
        final List<FontFacet> facetList = new ArrayList<FontFacet>();
        if (this.fontListEntry.isSetRegular()) {
            facetList.add((FontFacet)new XSLFFontFacet(this.fontListEntry.getRegular()));
        }
        if (this.fontListEntry.isSetItalic()) {
            facetList.add((FontFacet)new XSLFFontFacet(this.fontListEntry.getItalic()));
        }
        if (this.fontListEntry.isSetBold()) {
            facetList.add((FontFacet)new XSLFFontFacet(this.fontListEntry.getBold()));
        }
        if (this.fontListEntry.isSetBoldItalic()) {
            facetList.add((FontFacet)new XSLFFontFacet(this.fontListEntry.getBoldItalic()));
        }
        return facetList;
    }
    
    public FontFacet addFacet(final InputStream fontData) throws IOException {
        final FontHeader header = new FontHeader();
        final InputStream is = header.bufferInit(fontData);
        final CTPresentation pres = this.ppt.getCTPresentation();
        pres.setEmbedTrueTypeFonts(true);
        pres.setSaveSubsetFonts(true);
        final int style = ((header.getWeight() > 400) ? 1 : 0) | (header.isItalic() ? 2 : 0);
        CTEmbeddedFontDataId dataId = null;
        switch (style) {
            case 0: {
                dataId = (this.fontListEntry.isSetRegular() ? this.fontListEntry.getRegular() : this.fontListEntry.addNewRegular());
                break;
            }
            case 1: {
                dataId = (this.fontListEntry.isSetBold() ? this.fontListEntry.getBold() : this.fontListEntry.addNewBold());
                break;
            }
            case 2: {
                dataId = (this.fontListEntry.isSetItalic() ? this.fontListEntry.getItalic() : this.fontListEntry.addNewItalic());
                break;
            }
            default: {
                dataId = (this.fontListEntry.isSetBoldItalic() ? this.fontListEntry.getBoldItalic() : this.fontListEntry.addNewBoldItalic());
                break;
            }
        }
        final XSLFFontFacet facet = new XSLFFontFacet(dataId);
        facet.setFontData(is);
        return (FontFacet)facet;
    }
    
    private CTTextFont getFont() {
        return this.fontListEntry.getFont();
    }
    
    public static XSLFFontInfo addFontToSlideShow(final XMLSlideShow ppt, final InputStream fontStream) throws IOException {
        final FontHeader header = new FontHeader();
        final InputStream is = header.bufferInit(fontStream);
        final XSLFFontInfo fontInfo = new XSLFFontInfo(ppt, header.getFamilyName());
        fontInfo.addFacet(is);
        return fontInfo;
    }
    
    public static List<XSLFFontInfo> getFonts(final XMLSlideShow ppt) {
        final CTPresentation pres = ppt.getCTPresentation();
        return (List<XSLFFontInfo>)(pres.isSetEmbeddedFontLst() ? Stream.of(pres.getEmbeddedFontLst().getEmbeddedFontArray()).map(fe -> new XSLFFontInfo(ppt, fe)).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()) : Collections.emptyList());
    }
    
    private final class XSLFFontFacet implements FontFacet
    {
        private final CTEmbeddedFontDataId fontEntry;
        private final FontHeader header;
        
        private XSLFFontFacet(final CTEmbeddedFontDataId fontEntry) {
            this.header = new FontHeader();
            this.fontEntry = fontEntry;
        }
        
        public int getWeight() {
            this.init();
            return this.header.getWeight();
        }
        
        public boolean isItalic() {
            this.init();
            return this.header.isItalic();
        }
        
        public XSLFFontData getFontData() {
            return XSLFFontInfo.this.ppt.getRelationPartById(this.fontEntry.getId()).getDocumentPart();
        }
        
        void setFontData(final InputStream is) throws IOException {
            final XSLFRelation fntRel = XSLFRelation.FONT;
            final String relId = this.fontEntry.getId();
            XSLFFontData fntData = null;
            Label_0124: {
                if (relId != null) {
                    if (!relId.isEmpty()) {
                        fntData = (XSLFFontData)XSLFFontInfo.this.ppt.getRelationById(relId);
                        break Label_0124;
                    }
                }
                int fntDataIdx;
                try {
                    fntDataIdx = XSLFFontInfo.this.ppt.getPackage().getUnusedPartIndex(fntRel.getDefaultFileName());
                }
                catch (final InvalidFormatException e) {
                    throw new RuntimeException(e);
                }
                final POIXMLDocumentPart.RelationPart rp = XSLFFontInfo.this.ppt.createRelationship(fntRel, XSLFFactory.getInstance(), fntDataIdx, false);
                fntData = rp.getDocumentPart();
                this.fontEntry.setId(rp.getRelationship().getId());
            }
            assert fntData != null;
            try (final OutputStream os = fntData.getOutputStream()) {
                IOUtils.copy(is, os);
            }
        }
        
        private void init() {
            if (this.header.getFamilyName() == null) {
                try (final InputStream is = this.getFontData().getInputStream()) {
                    final byte[] buf = IOUtils.toByteArray(is, 1000);
                    this.header.init(buf, 0, buf.length);
                }
                catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
