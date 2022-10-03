package org.apache.poi.xddf.usermodel.text;

import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHyperlink;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineProperties;
import java.util.Optional;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import java.util.function.Function;
import org.apache.poi.xddf.usermodel.XDDFLineProperties;
import java.util.Locale;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.common.usermodel.fonts.FontGroup;
import java.util.LinkedList;
import org.apache.poi.xddf.usermodel.XDDFSolidFillProperties;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.poi.xddf.usermodel.XDDFFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextCapsType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextUnderlineType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextStrikeType;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextField;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextLineBreak;

public class XDDFTextRun
{
    private XDDFTextParagraph _parent;
    private XDDFRunProperties _properties;
    private CTTextLineBreak _tlb;
    private CTTextField _tf;
    private CTRegularTextRun _rtr;
    
    @Internal
    protected XDDFTextRun(final CTTextLineBreak run, final XDDFTextParagraph parent) {
        this._tlb = run;
        this._parent = parent;
    }
    
    @Internal
    protected XDDFTextRun(final CTTextField run, final XDDFTextParagraph parent) {
        this._tf = run;
        this._parent = parent;
    }
    
    @Internal
    protected XDDFTextRun(final CTRegularTextRun run, final XDDFTextParagraph parent) {
        this._rtr = run;
        this._parent = parent;
    }
    
    public XDDFTextParagraph getParentParagraph() {
        return this._parent;
    }
    
    public boolean isLineBreak() {
        return this._tlb != null;
    }
    
    public boolean isField() {
        return this._tf != null;
    }
    
    public boolean isRegularRun() {
        return this._rtr != null;
    }
    
    public String getText() {
        if (this.isLineBreak()) {
            return "\n";
        }
        if (this.isField()) {
            return this._tf.getT();
        }
        return this._rtr.getT();
    }
    
    public void setText(final String text) {
        if (this.isField()) {
            this._tf.setT(text);
        }
        else if (this.isRegularRun()) {
            this._rtr.setT(text);
        }
    }
    
    public void setDirty(final Boolean dirty) {
        this.getOrCreateProperties().setDirty(dirty);
    }
    
    public Boolean getDirty() {
        return this.findDefinedProperty(props -> props.isSetDirty(), props -> props.getDirty()).orElse(null);
    }
    
    public void setSpellError(final Boolean error) {
        this.getOrCreateProperties().setSpellError(error);
    }
    
    public Boolean getSpellError() {
        return this.findDefinedProperty(props -> props.isSetErr(), props -> props.getErr()).orElse(null);
    }
    
    public void setNoProof(final Boolean noproof) {
        this.getOrCreateProperties().setNoProof(noproof);
    }
    
    public Boolean getNoProof() {
        return this.findDefinedProperty(props -> props.isSetNoProof(), props -> props.getNoProof()).orElse(null);
    }
    
    public void setNormalizeHeights(final Boolean normalize) {
        this.getOrCreateProperties().setNormalizeHeights(normalize);
    }
    
    public Boolean getNormalizeHeights() {
        return this.findDefinedProperty(props -> props.isSetNormalizeH(), props -> props.getNormalizeH()).orElse(null);
    }
    
    public void setKumimoji(final Boolean kumimoji) {
        this.getOrCreateProperties().setKumimoji(kumimoji);
    }
    
    public boolean isKumimoji() {
        return this.findDefinedProperty(props -> props.isSetKumimoji(), props -> props.getKumimoji()).orElse(false);
    }
    
    public void setBold(final Boolean bold) {
        this.getOrCreateProperties().setBold(bold);
    }
    
    public boolean isBold() {
        return this.findDefinedProperty(props -> props.isSetB(), props -> props.getB()).orElse(false);
    }
    
    public void setItalic(final Boolean italic) {
        this.getOrCreateProperties().setItalic(italic);
    }
    
    public boolean isItalic() {
        return this.findDefinedProperty(props -> props.isSetI(), props -> props.getI()).orElse(false);
    }
    
    public void setStrikeThrough(final StrikeType strike) {
        this.getOrCreateProperties().setStrikeThrough(strike);
    }
    
    public boolean isStrikeThrough() {
        return this.findDefinedProperty(props -> props.isSetStrike(), props -> props.getStrike()).map(strike -> strike != STTextStrikeType.NO_STRIKE).orElse(false);
    }
    
    public StrikeType getStrikeThrough() {
        return this.findDefinedProperty(props -> props.isSetStrike(), props -> props.getStrike()).map(strike -> StrikeType.valueOf(strike)).orElse(null);
    }
    
    public void setUnderline(final UnderlineType underline) {
        this.getOrCreateProperties().setUnderline(underline);
    }
    
    public boolean isUnderline() {
        return this.findDefinedProperty(props -> props.isSetU(), props -> props.getU()).map(underline -> underline != STTextUnderlineType.NONE).orElse(false);
    }
    
    public UnderlineType getUnderline() {
        return this.findDefinedProperty(props -> props.isSetU(), props -> props.getU()).map(underline -> UnderlineType.valueOf(underline)).orElse(null);
    }
    
    public void setCapitals(final CapsType caps) {
        this.getOrCreateProperties().setCapitals(caps);
    }
    
    public boolean isCapitals() {
        return this.findDefinedProperty(props -> props.isSetCap(), props -> props.getCap()).map(caps -> caps != STTextCapsType.NONE).orElse(false);
    }
    
    public CapsType getCapitals() {
        return this.findDefinedProperty(props -> props.isSetCap(), props -> props.getCap()).map(caps -> CapsType.valueOf(caps)).orElse(null);
    }
    
    public boolean isSubscript() {
        return this.findDefinedProperty(props -> props.isSetBaseline(), props -> props.getBaseline()).map(baseline -> baseline < 0).orElse(false);
    }
    
    public boolean isSuperscript() {
        return this.findDefinedProperty(props -> props.isSetBaseline(), props -> props.getBaseline()).map(baseline -> baseline > 0).orElse(false);
    }
    
    public void setBaseline(final Double offset) {
        if (offset == null) {
            this.getOrCreateProperties().setBaseline(null);
        }
        else {
            this.getOrCreateProperties().setBaseline((int)(offset * 1000.0));
        }
    }
    
    public void setSuperscript(final Double offset) {
        this.setBaseline((offset == null) ? null : Double.valueOf(Math.abs(offset)));
    }
    
    public void setSubscript(final Double offset) {
        this.setBaseline((offset == null) ? null : Double.valueOf(-Math.abs(offset)));
    }
    
    public void setFillProperties(final XDDFFillProperties properties) {
        this.getOrCreateProperties().setFillProperties(properties);
    }
    
    public void setFontColor(final XDDFColor color) {
        final XDDFSolidFillProperties props = new XDDFSolidFillProperties();
        props.setColor(color);
        this.setFillProperties(props);
    }
    
    public XDDFColor getFontColor() {
        final XDDFSolidFillProperties solid = this.findDefinedProperty(props -> props.isSetSolidFill(), props -> props.getSolidFill()).map(props -> new XDDFSolidFillProperties(props)).orElse(new XDDFSolidFillProperties());
        return solid.getColor();
    }
    
    public void setFonts(final XDDFFont[] fonts) {
        this.getOrCreateProperties().setFonts(fonts);
    }
    
    public XDDFFont[] getFonts() {
        final LinkedList<XDDFFont> list = new LinkedList<XDDFFont>();
        this.findDefinedProperty(props -> props.isSetCs(), props -> props.getCs()).map(font -> new XDDFFont(FontGroup.COMPLEX_SCRIPT, font)).ifPresent(font -> list.add(font));
        this.findDefinedProperty(props -> props.isSetEa(), props -> props.getEa()).map(font -> new XDDFFont(FontGroup.EAST_ASIAN, font)).ifPresent(font -> list.add(font));
        this.findDefinedProperty(props -> props.isSetLatin(), props -> props.getLatin()).map(font -> new XDDFFont(FontGroup.LATIN, font)).ifPresent(font -> list.add(font));
        this.findDefinedProperty(props -> props.isSetSym(), props -> props.getSym()).map(font -> new XDDFFont(FontGroup.SYMBOL, font)).ifPresent(font -> list.add(font));
        return list.toArray(new XDDFFont[0]);
    }
    
    public void setFontSize(final Double size) {
        this.getOrCreateProperties().setFontSize(size);
    }
    
    public Double getFontSize() {
        final Integer size = this.findDefinedProperty(props -> props.isSetSz(), props -> props.getSz()).orElse(1100);
        final double scale = this._parent.getParentBody().getBodyProperties().getAutoFit().getFontScale() / 1.0E7;
        return size * scale;
    }
    
    public void setCharacterKerning(final Double kerning) {
        this.getOrCreateProperties().setCharacterKerning(kerning);
    }
    
    public Double getCharacterKerning() {
        return this.findDefinedProperty(props -> props.isSetKern(), props -> props.getKern()).map(kerning -> 0.01 * kerning).orElse(null);
    }
    
    public void setCharacterSpacing(final Double spacing) {
        this.getOrCreateProperties().setCharacterSpacing(spacing);
    }
    
    public Double getCharacterSpacing() {
        return this.findDefinedProperty(props -> props.isSetSpc(), props -> props.getSpc()).map(spacing -> 0.01 * spacing).orElse(null);
    }
    
    public void setBookmark(final String bookmark) {
        this.getOrCreateProperties().setBookmark(bookmark);
    }
    
    public String getBookmark() {
        return this.findDefinedProperty(props -> props.isSetBmk(), props -> props.getBmk()).orElse(null);
    }
    
    public XDDFHyperlink linkToExternal(final String url, final PackagePart localPart, final POIXMLRelation relation) {
        final PackageRelationship rel = localPart.addExternalRelationship(url, relation.getRelation());
        final XDDFHyperlink link = new XDDFHyperlink(rel.getId());
        this.getOrCreateProperties().setHyperlink(link);
        return link;
    }
    
    public XDDFHyperlink linkToAction(final String action) {
        final XDDFHyperlink link = new XDDFHyperlink("", action);
        this.getOrCreateProperties().setHyperlink(link);
        return link;
    }
    
    public XDDFHyperlink linkToInternal(final String action, final PackagePart localPart, final POIXMLRelation relation, final PackagePartName target) {
        final PackageRelationship rel = localPart.addRelationship(target, TargetMode.INTERNAL, relation.getRelation());
        final XDDFHyperlink link = new XDDFHyperlink(rel.getId(), action);
        this.getOrCreateProperties().setHyperlink(link);
        return link;
    }
    
    public XDDFHyperlink getHyperlink() {
        return this.findDefinedProperty(props -> props.isSetHlinkClick(), props -> props.getHlinkClick()).map(link -> new XDDFHyperlink(link)).orElse(null);
    }
    
    public XDDFHyperlink createMouseOver(final String action) {
        final XDDFHyperlink link = new XDDFHyperlink("", action);
        this.getOrCreateProperties().setMouseOver(link);
        return link;
    }
    
    public XDDFHyperlink getMouseOver() {
        return this.findDefinedProperty(props -> props.isSetHlinkMouseOver(), props -> props.getHlinkMouseOver()).map(link -> new XDDFHyperlink(link)).orElse(null);
    }
    
    public void setLanguage(final Locale lang) {
        this.getOrCreateProperties().setLanguage(lang);
    }
    
    public Locale getLanguage() {
        return this.findDefinedProperty(props -> props.isSetLang(), props -> props.getLang()).map(lang -> Locale.forLanguageTag(lang)).orElse(null);
    }
    
    public void setAlternativeLanguage(final Locale lang) {
        this.getOrCreateProperties().setAlternativeLanguage(lang);
    }
    
    public Locale getAlternativeLanguage() {
        return this.findDefinedProperty(props -> props.isSetAltLang(), props -> props.getAltLang()).map(lang -> Locale.forLanguageTag(lang)).orElse(null);
    }
    
    public void setHighlight(final XDDFColor color) {
        this.getOrCreateProperties().setHighlight(color);
    }
    
    public XDDFColor getHighlight() {
        return this.findDefinedProperty(props -> props.isSetHighlight(), props -> props.getHighlight()).map(color -> XDDFColor.forColorContainer(color)).orElse(null);
    }
    
    public void setLineProperties(final XDDFLineProperties properties) {
        this.getOrCreateProperties().setLineProperties(properties);
    }
    
    public XDDFLineProperties getLineProperties() {
        return this.findDefinedProperty(props -> props.isSetLn(), props -> props.getLn()).map(props -> new XDDFLineProperties(props)).orElse(null);
    }
    
    private <R> Optional<R> findDefinedProperty(final Function<CTTextCharacterProperties, Boolean> isSet, final Function<CTTextCharacterProperties, R> getter) {
        final CTTextCharacterProperties props = this.getProperties();
        if (props != null && isSet.apply(props)) {
            return Optional.ofNullable(getter.apply(props));
        }
        return this._parent.findDefinedRunProperty(isSet, getter);
    }
    
    @Internal
    protected CTTextCharacterProperties getProperties() {
        if (this.isLineBreak() && this._tlb.isSetRPr()) {
            return this._tlb.getRPr();
        }
        if (this.isField() && this._tf.isSetRPr()) {
            return this._tf.getRPr();
        }
        if (this.isRegularRun() && this._rtr.isSetRPr()) {
            return this._rtr.getRPr();
        }
        return null;
    }
    
    private XDDFRunProperties getOrCreateProperties() {
        if (this._properties == null) {
            if (this.isLineBreak()) {
                this._properties = new XDDFRunProperties(this._tlb.isSetRPr() ? this._tlb.getRPr() : this._tlb.addNewRPr());
            }
            else if (this.isField()) {
                this._properties = new XDDFRunProperties(this._tf.isSetRPr() ? this._tf.getRPr() : this._tf.addNewRPr());
            }
            else if (this.isRegularRun()) {
                this._properties = new XDDFRunProperties(this._rtr.isSetRPr() ? this._rtr.getRPr() : this._rtr.addNewRPr());
            }
        }
        return this._properties;
    }
}
