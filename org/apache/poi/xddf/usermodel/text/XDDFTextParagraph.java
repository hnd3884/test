package org.apache.poi.xddf.usermodel.text;

import org.openxmlformats.schemas.drawingml.x2006.main.STTextAlignType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextFontAlignType;
import java.util.Optional;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import java.util.function.Function;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextSpacing;
import java.util.Collections;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.poi.util.Units;
import org.apache.poi.util.LocaleUtil;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.apache.commons.collections4.iterators.IteratorIterable;
import org.apache.commons.collections4.iterators.ReverseListIterator;
import java.util.List;
import java.util.Iterator;
import org.apache.poi.util.Internal;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextField;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextLineBreak;
import org.apache.xmlbeans.QNameSet;
import java.util.ArrayList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;

public class XDDFTextParagraph
{
    private XDDFTextBody _parent;
    private XDDFParagraphProperties _properties;
    private final CTTextParagraph _p;
    private final ArrayList<XDDFTextRun> _runs;
    
    @Internal
    protected XDDFTextParagraph(final CTTextParagraph paragraph, final XDDFTextBody parent) {
        this._p = paragraph;
        this._parent = parent;
        final int count = paragraph.sizeOfBrArray() + paragraph.sizeOfFldArray() + paragraph.sizeOfRArray();
        this._runs = new ArrayList<XDDFTextRun>(count);
        for (final XmlObject xo : this._p.selectChildren(QNameSet.ALL)) {
            if (xo instanceof CTTextLineBreak) {
                this._runs.add(new XDDFTextRun((CTTextLineBreak)xo, this));
            }
            else if (xo instanceof CTTextField) {
                this._runs.add(new XDDFTextRun((CTTextField)xo, this));
            }
            else if (xo instanceof CTRegularTextRun) {
                this._runs.add(new XDDFTextRun((CTRegularTextRun)xo, this));
            }
        }
        this.addDefaultRunProperties();
        this.addAfterLastRunProperties();
    }
    
    public void setText(final String text) {
        for (int i = this._p.sizeOfBrArray() - 1; i >= 0; --i) {
            this._p.removeBr(i);
        }
        for (int i = this._p.sizeOfFldArray() - 1; i >= 0; --i) {
            this._p.removeFld(i);
        }
        for (int i = this._p.sizeOfRArray() - 1; i >= 0; --i) {
            this._p.removeR(i);
        }
        this._runs.clear();
        this.appendRegularRun(text);
    }
    
    public String getText() {
        final StringBuilder out = new StringBuilder();
        for (final XDDFTextRun r : this._runs) {
            out.append(r.getText());
        }
        return out.toString();
    }
    
    public XDDFTextBody getParentBody() {
        return this._parent;
    }
    
    public List<XDDFTextRun> getTextRuns() {
        return this._runs;
    }
    
    public Iterator<XDDFTextRun> iterator() {
        return this._runs.iterator();
    }
    
    public XDDFTextRun appendLineBreak() {
        final CTTextLineBreak br = this._p.addNewBr();
        for (final XDDFTextRun tr : new IteratorIterable((Iterator)new ReverseListIterator((List)this._runs))) {
            final CTTextCharacterProperties prevProps = tr.getProperties();
            if (prevProps != null) {
                br.setRPr((CTTextCharacterProperties)prevProps.copy());
                break;
            }
        }
        final XDDFTextRun run = new XDDFTextRun(br, this);
        this._runs.add(run);
        return run;
    }
    
    public XDDFTextRun appendField(final String id, final String type, final String text) {
        final CTTextField f = this._p.addNewFld();
        f.setId(id);
        f.setType(type);
        f.setT(text);
        final CTTextCharacterProperties rPr = f.addNewRPr();
        rPr.setLang(LocaleUtil.getUserLocale().toLanguageTag());
        final XDDFTextRun run = new XDDFTextRun(f, this);
        this._runs.add(run);
        return run;
    }
    
    public XDDFTextRun appendRegularRun(final String text) {
        final CTRegularTextRun r = this._p.addNewR();
        r.setT(text);
        final CTTextCharacterProperties rPr = r.addNewRPr();
        rPr.setLang(LocaleUtil.getUserLocale().toLanguageTag());
        final XDDFTextRun run = new XDDFTextRun(r, this);
        this._runs.add(run);
        return run;
    }
    
    public TextAlignment getTextAlignment() {
        return this.findDefinedParagraphProperty(props -> props.isSetAlgn(), props -> props.getAlgn()).map(align -> TextAlignment.valueOf(align)).orElse(null);
    }
    
    public void setTextAlignment(final TextAlignment align) {
        if (align != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setTextAlignment(align);
        }
    }
    
    public FontAlignment getFontAlignment() {
        return this.findDefinedParagraphProperty(props -> props.isSetFontAlgn(), props -> props.getFontAlgn()).map(align -> FontAlignment.valueOf(align)).orElse(null);
    }
    
    public void setFontAlignment(final FontAlignment align) {
        if (align != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setFontAlignment(align);
        }
    }
    
    public Double getIndentation() {
        return this.findDefinedParagraphProperty(props -> props.isSetIndent(), props -> props.getIndent()).map(emu -> Units.toPoints((long)emu)).orElse(null);
    }
    
    public void setIndentation(final Double points) {
        if (points != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setIndentation(points);
        }
    }
    
    public Double getMarginLeft() {
        return this.findDefinedParagraphProperty(props -> props.isSetMarL(), props -> props.getMarL()).map(emu -> Units.toPoints((long)emu)).orElse(null);
    }
    
    public void setMarginLeft(final Double points) {
        if (points != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setMarginLeft(points);
        }
    }
    
    public Double getMarginRight() {
        return this.findDefinedParagraphProperty(props -> props.isSetMarR(), props -> props.getMarR()).map(emu -> Units.toPoints((long)emu)).orElse(null);
    }
    
    public void setMarginRight(final Double points) {
        if (points != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setMarginRight(points);
        }
    }
    
    public Double getDefaultTabSize() {
        return this.findDefinedParagraphProperty(props -> props.isSetDefTabSz(), props -> props.getDefTabSz()).map(emu -> Units.toPoints((long)emu)).orElse(null);
    }
    
    public void setDefaultTabSize(final Double points) {
        if (points != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setDefaultTabSize(points);
        }
    }
    
    public XDDFSpacing getLineSpacing() {
        return this.findDefinedParagraphProperty(props -> props.isSetLnSpc(), props -> props.getLnSpc()).map(spacing -> this.extractSpacing(spacing)).orElse(null);
    }
    
    public void setLineSpacing(final XDDFSpacing linespacing) {
        if (linespacing != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setLineSpacing(linespacing);
        }
    }
    
    public XDDFSpacing getSpaceBefore() {
        return this.findDefinedParagraphProperty(props -> props.isSetSpcBef(), props -> props.getSpcBef()).map(spacing -> this.extractSpacing(spacing)).orElse(null);
    }
    
    public void setSpaceBefore(final XDDFSpacing spaceBefore) {
        if (spaceBefore != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setSpaceBefore(spaceBefore);
        }
    }
    
    public XDDFSpacing getSpaceAfter() {
        return this.findDefinedParagraphProperty(props -> props.isSetSpcAft(), props -> props.getSpcAft()).map(spacing -> this.extractSpacing(spacing)).orElse(null);
    }
    
    public void setSpaceAfter(final XDDFSpacing spaceAfter) {
        if (spaceAfter != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setSpaceAfter(spaceAfter);
        }
    }
    
    public XDDFColor getBulletColor() {
        return this.findDefinedParagraphProperty(props -> props.isSetBuClr() || props.isSetBuClrTx(), props -> new XDDFParagraphBulletProperties(props).getBulletColor()).orElse(null);
    }
    
    public void setBulletColor(final XDDFColor color) {
        if (color != null || this._p.isSetPPr()) {
            this.getOrCreateBulletProperties().setBulletColor(color);
        }
    }
    
    public void setBulletColorFollowText() {
        this.getOrCreateBulletProperties().setBulletColorFollowText();
    }
    
    public XDDFFont getBulletFont() {
        return this.findDefinedParagraphProperty(props -> props.isSetBuFont() || props.isSetBuFontTx(), props -> new XDDFParagraphBulletProperties(props).getBulletFont()).orElse(null);
    }
    
    public void setBulletFont(final XDDFFont font) {
        if (font != null || this._p.isSetPPr()) {
            this.getOrCreateBulletProperties().setBulletFont(font);
        }
    }
    
    public void setBulletFontFollowText() {
        this.getOrCreateBulletProperties().setBulletFontFollowText();
    }
    
    public XDDFBulletSize getBulletSize() {
        return this.findDefinedParagraphProperty(props -> props.isSetBuSzPct() || props.isSetBuSzPts() || props.isSetBuSzTx(), props -> new XDDFParagraphBulletProperties(props).getBulletSize()).orElse(null);
    }
    
    public void setBulletSize(final XDDFBulletSize size) {
        if (size != null || this._p.isSetPPr()) {
            this.getOrCreateBulletProperties().setBulletSize(size);
        }
    }
    
    public XDDFBulletStyle getBulletStyle() {
        return this.findDefinedParagraphProperty(props -> props.isSetBuAutoNum() || props.isSetBuBlip() || props.isSetBuChar() || props.isSetBuNone(), props -> new XDDFParagraphBulletProperties(props).getBulletStyle()).orElse(null);
    }
    
    public void setBulletStyle(final XDDFBulletStyle style) {
        if (style != null || this._p.isSetPPr()) {
            this.getOrCreateBulletProperties().setBulletStyle(style);
        }
    }
    
    public boolean hasEastAsianLineBreak() {
        return this.findDefinedParagraphProperty(props -> props.isSetEaLnBrk(), props -> props.getEaLnBrk()).orElse(false);
    }
    
    public void setEastAsianLineBreak(final Boolean value) {
        if (value != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setEastAsianLineBreak(value);
        }
    }
    
    public boolean hasLatinLineBreak() {
        return this.findDefinedParagraphProperty(props -> props.isSetLatinLnBrk(), props -> props.getLatinLnBrk()).orElse(false);
    }
    
    public void setLatinLineBreak(final Boolean value) {
        if (value != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setLatinLineBreak(value);
        }
    }
    
    public boolean hasHangingPunctuation() {
        return this.findDefinedParagraphProperty(props -> props.isSetHangingPunct(), props -> props.getHangingPunct()).orElse(false);
    }
    
    public void setHangingPunctuation(final Boolean value) {
        if (value != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setHangingPunctuation(value);
        }
    }
    
    public boolean isRightToLeft() {
        return this.findDefinedParagraphProperty(props -> props.isSetRtl(), props -> props.getRtl()).orElse(false);
    }
    
    public void setRightToLeft(final Boolean value) {
        if (value != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setRightToLeft(value);
        }
    }
    
    public XDDFTabStop addTabStop() {
        return this.getOrCreateProperties().addTabStop();
    }
    
    public XDDFTabStop insertTabStop(final int index) {
        return this.getOrCreateProperties().insertTabStop(index);
    }
    
    public void removeTabStop(final int index) {
        if (this._p.isSetPPr()) {
            this.getProperties().removeTabStop(index);
        }
    }
    
    public XDDFTabStop getTabStop(final int index) {
        if (this._p.isSetPPr()) {
            return this.getProperties().getTabStop(index);
        }
        return null;
    }
    
    public List<XDDFTabStop> getTabStops() {
        if (this._p.isSetPPr()) {
            return this.getProperties().getTabStops();
        }
        return Collections.emptyList();
    }
    
    public int countTabStops() {
        if (this._p.isSetPPr()) {
            return this.getProperties().countTabStops();
        }
        return 0;
    }
    
    public XDDFParagraphBulletProperties getOrCreateBulletProperties() {
        return this.getOrCreateProperties().getBulletProperties();
    }
    
    public XDDFParagraphBulletProperties getBulletProperties() {
        if (this._p.isSetPPr()) {
            return this.getProperties().getBulletProperties();
        }
        return null;
    }
    
    public XDDFRunProperties addDefaultRunProperties() {
        return this.getOrCreateProperties().addDefaultRunProperties();
    }
    
    public XDDFRunProperties getDefaultRunProperties() {
        if (this._p.isSetPPr()) {
            return this.getProperties().getDefaultRunProperties();
        }
        return null;
    }
    
    public void setDefaultRunProperties(final XDDFRunProperties properties) {
        if (properties != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setDefaultRunProperties(properties);
        }
    }
    
    public XDDFRunProperties addAfterLastRunProperties() {
        if (!this._p.isSetEndParaRPr()) {
            this._p.addNewEndParaRPr();
        }
        return this.getAfterLastRunProperties();
    }
    
    public XDDFRunProperties getAfterLastRunProperties() {
        if (this._p.isSetEndParaRPr()) {
            return new XDDFRunProperties(this._p.getEndParaRPr());
        }
        return null;
    }
    
    public void setAfterLastRunProperties(final XDDFRunProperties properties) {
        if (properties == null) {
            if (this._p.isSetEndParaRPr()) {
                this._p.unsetEndParaRPr();
            }
        }
        else {
            this._p.setEndParaRPr(properties.getXmlObject());
        }
    }
    
    private XDDFSpacing extractSpacing(final CTTextSpacing spacing) {
        if (spacing.isSetSpcPct()) {
            final double scale = 1.0 - this._parent.getBodyProperties().getAutoFit().getLineSpaceReduction() / 100000.0;
            return new XDDFSpacingPercent(spacing, spacing.getSpcPct(), scale);
        }
        if (spacing.isSetSpcPts()) {
            return new XDDFSpacingPoints(spacing, spacing.getSpcPts());
        }
        return null;
    }
    
    private XDDFParagraphProperties getProperties() {
        if (this._properties == null) {
            this._properties = new XDDFParagraphProperties(this._p.getPPr());
        }
        return this._properties;
    }
    
    private XDDFParagraphProperties getOrCreateProperties() {
        if (!this._p.isSetPPr()) {
            this._properties = new XDDFParagraphProperties(this._p.addNewPPr());
        }
        return this.getProperties();
    }
    
    protected <R> Optional<R> findDefinedParagraphProperty(final Function<CTTextParagraphProperties, Boolean> isSet, final Function<CTTextParagraphProperties, R> getter) {
        if (this._p.isSetPPr()) {
            final int level = this._p.getPPr().isSetLvl() ? (1 + this._p.getPPr().getLvl()) : 0;
            return this.findDefinedParagraphProperty(isSet, getter, level);
        }
        return this._parent.findDefinedParagraphProperty(isSet, getter, 0);
    }
    
    private <R> Optional<R> findDefinedParagraphProperty(final Function<CTTextParagraphProperties, Boolean> isSet, final Function<CTTextParagraphProperties, R> getter, final int level) {
        final CTTextParagraphProperties props = this._p.getPPr();
        if (props != null && isSet.apply(props)) {
            return Optional.ofNullable(getter.apply(props));
        }
        return this._parent.findDefinedParagraphProperty(isSet, getter, level);
    }
    
    protected <R> Optional<R> findDefinedRunProperty(final Function<CTTextCharacterProperties, Boolean> isSet, final Function<CTTextCharacterProperties, R> getter) {
        if (this._p.isSetPPr()) {
            final int level = this._p.getPPr().isSetLvl() ? (1 + this._p.getPPr().getLvl()) : 0;
            return this.findDefinedRunProperty(isSet, getter, level);
        }
        return this._parent.findDefinedRunProperty(isSet, getter, 0);
    }
    
    private <R> Optional<R> findDefinedRunProperty(final Function<CTTextCharacterProperties, Boolean> isSet, final Function<CTTextCharacterProperties, R> getter, final int level) {
        final CTTextCharacterProperties props = this._p.getPPr().isSetDefRPr() ? this._p.getPPr().getDefRPr() : null;
        if (props != null && isSet.apply(props)) {
            return Optional.ofNullable(getter.apply(props));
        }
        return this._parent.findDefinedRunProperty(isSet, getter, level);
    }
}
