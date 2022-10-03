package com.lowagie.text.pdf;

import java.util.HashMap;
import com.lowagie.text.Rectangle;
import java.util.Map;

public class PdfAppearance extends PdfTemplate
{
    public static final Map<String, PdfName> stdFieldFontNames;
    
    PdfAppearance() {
        this.separator = 32;
    }
    
    PdfAppearance(final PdfIndirectReference iref) {
        this.thisReference = iref;
    }
    
    PdfAppearance(final PdfWriter wr) {
        super(wr);
        this.separator = 32;
    }
    
    public static PdfAppearance createAppearance(final PdfWriter writer, final float width, final float height) {
        return createAppearance(writer, width, height, null);
    }
    
    private static PdfAppearance createAppearance(final PdfWriter writer, final float width, final float height, final PdfName forcedName) {
        final PdfAppearance template = new PdfAppearance(writer);
        template.setWidth(width);
        template.setHeight(height);
        writer.addDirectTemplateSimple(template, forcedName);
        return template;
    }
    
    @Override
    public void setFontAndSize(final BaseFont bf, final float size) {
        this.checkWriter();
        this.state.size = size;
        if (bf.getFontType() == 4) {
            this.state.fontDetails = new FontDetails(null, ((DocumentFont)bf).getIndirectReference(), bf);
        }
        else {
            this.state.fontDetails = this.writer.addSimple(bf);
        }
        PdfName psn = PdfAppearance.stdFieldFontNames.get(bf.getPostscriptFontName());
        if (psn == null) {
            if (bf.isSubset() && bf.getFontType() == 3) {
                psn = this.state.fontDetails.getFontName();
            }
            else {
                psn = new PdfName(bf.getPostscriptFontName());
                this.state.fontDetails.setSubset(false);
            }
        }
        final PageResources prs = this.getPageResources();
        prs.addFont(psn, this.state.fontDetails.getIndirectReference());
        this.content.append(psn.getBytes()).append(' ').append(size).append(" Tf").append_i(this.separator);
    }
    
    @Override
    public PdfContentByte getDuplicate() {
        final PdfAppearance tpl = new PdfAppearance();
        tpl.writer = this.writer;
        tpl.pdf = this.pdf;
        tpl.thisReference = this.thisReference;
        tpl.pageResources = this.pageResources;
        tpl.bBox = new Rectangle(this.bBox);
        tpl.group = this.group;
        tpl.layer = this.layer;
        if (this.matrix != null) {
            tpl.matrix = new PdfArray(this.matrix);
        }
        tpl.separator = this.separator;
        return tpl;
    }
    
    static {
        (stdFieldFontNames = new HashMap<String, PdfName>()).put("Courier-BoldOblique", new PdfName("CoBO"));
        PdfAppearance.stdFieldFontNames.put("Courier-Bold", new PdfName("CoBo"));
        PdfAppearance.stdFieldFontNames.put("Courier-Oblique", new PdfName("CoOb"));
        PdfAppearance.stdFieldFontNames.put("Courier", new PdfName("Cour"));
        PdfAppearance.stdFieldFontNames.put("Helvetica-BoldOblique", new PdfName("HeBO"));
        PdfAppearance.stdFieldFontNames.put("Helvetica-Bold", new PdfName("HeBo"));
        PdfAppearance.stdFieldFontNames.put("Helvetica-Oblique", new PdfName("HeOb"));
        PdfAppearance.stdFieldFontNames.put("Helvetica", PdfName.HELV);
        PdfAppearance.stdFieldFontNames.put("Symbol", new PdfName("Symb"));
        PdfAppearance.stdFieldFontNames.put("Times-BoldItalic", new PdfName("TiBI"));
        PdfAppearance.stdFieldFontNames.put("Times-Bold", new PdfName("TiBo"));
        PdfAppearance.stdFieldFontNames.put("Times-Italic", new PdfName("TiIt"));
        PdfAppearance.stdFieldFontNames.put("Times-Roman", new PdfName("TiRo"));
        PdfAppearance.stdFieldFontNames.put("ZapfDingbats", PdfName.ZADB);
        PdfAppearance.stdFieldFontNames.put("HYSMyeongJo-Medium", new PdfName("HySm"));
        PdfAppearance.stdFieldFontNames.put("HYGoThic-Medium", new PdfName("HyGo"));
        PdfAppearance.stdFieldFontNames.put("HeiseiKakuGo-W5", new PdfName("KaGo"));
        PdfAppearance.stdFieldFontNames.put("HeiseiMin-W3", new PdfName("KaMi"));
        PdfAppearance.stdFieldFontNames.put("MHei-Medium", new PdfName("MHei"));
        PdfAppearance.stdFieldFontNames.put("MSung-Light", new PdfName("MSun"));
        PdfAppearance.stdFieldFontNames.put("STSong-Light", new PdfName("STSo"));
        PdfAppearance.stdFieldFontNames.put("MSungStd-Light", new PdfName("MSun"));
        PdfAppearance.stdFieldFontNames.put("STSongStd-Light", new PdfName("STSo"));
        PdfAppearance.stdFieldFontNames.put("HYSMyeongJoStd-Medium", new PdfName("HySm"));
        PdfAppearance.stdFieldFontNames.put("KozMinPro-Regular", new PdfName("KaMi"));
    }
}
