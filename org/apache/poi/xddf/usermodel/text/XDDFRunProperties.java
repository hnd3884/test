package org.apache.poi.xddf.usermodel.text;

import org.apache.poi.xddf.usermodel.XDDFEffectList;
import org.apache.poi.xddf.usermodel.XDDFEffectContainer;
import org.apache.poi.xddf.usermodel.XDDFExtensionList;
import org.apache.poi.xddf.usermodel.XDDFLineProperties;
import org.apache.poi.xddf.usermodel.XDDFColor;
import java.util.Locale;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;
import org.apache.poi.xddf.usermodel.XDDFSolidFillProperties;
import org.apache.poi.xddf.usermodel.XDDFPictureFillProperties;
import org.apache.poi.xddf.usermodel.XDDFPatternFillProperties;
import org.apache.poi.xddf.usermodel.XDDFNoFillProperties;
import org.apache.poi.xddf.usermodel.XDDFGroupFillProperties;
import org.apache.poi.xddf.usermodel.XDDFGradientFillProperties;
import org.apache.poi.xddf.usermodel.XDDFFillProperties;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;

public class XDDFRunProperties
{
    private CTTextCharacterProperties props;
    
    public XDDFRunProperties() {
        this(CTTextCharacterProperties.Factory.newInstance());
    }
    
    @Internal
    public XDDFRunProperties(final CTTextCharacterProperties properties) {
        this.props = properties;
    }
    
    @Internal
    protected CTTextCharacterProperties getXmlObject() {
        return this.props;
    }
    
    public void setBaseline(final Integer value) {
        if (value == null) {
            if (this.props.isSetBaseline()) {
                this.props.unsetBaseline();
            }
        }
        else {
            this.props.setBaseline((int)value);
        }
    }
    
    public void setDirty(final Boolean dirty) {
        if (dirty == null) {
            if (this.props.isSetDirty()) {
                this.props.unsetDirty();
            }
        }
        else {
            this.props.setDirty((boolean)dirty);
        }
    }
    
    public void setSpellError(final Boolean error) {
        if (error == null) {
            if (this.props.isSetErr()) {
                this.props.unsetErr();
            }
        }
        else {
            this.props.setErr((boolean)error);
        }
    }
    
    public void setNoProof(final Boolean noproof) {
        if (noproof == null) {
            if (this.props.isSetNoProof()) {
                this.props.unsetNoProof();
            }
        }
        else {
            this.props.setNoProof((boolean)noproof);
        }
    }
    
    public void setNormalizeHeights(final Boolean normalize) {
        if (normalize == null) {
            if (this.props.isSetNormalizeH()) {
                this.props.unsetNormalizeH();
            }
        }
        else {
            this.props.setNormalizeH((boolean)normalize);
        }
    }
    
    public void setKumimoji(final Boolean kumimoji) {
        if (kumimoji == null) {
            if (this.props.isSetKumimoji()) {
                this.props.unsetKumimoji();
            }
        }
        else {
            this.props.setKumimoji((boolean)kumimoji);
        }
    }
    
    public void setBold(final Boolean bold) {
        if (bold == null) {
            if (this.props.isSetB()) {
                this.props.unsetB();
            }
        }
        else {
            this.props.setB((boolean)bold);
        }
    }
    
    public void setItalic(final Boolean italic) {
        if (italic == null) {
            if (this.props.isSetI()) {
                this.props.unsetI();
            }
        }
        else {
            this.props.setI((boolean)italic);
        }
    }
    
    public void setFontSize(final Double size) {
        if (size == null) {
            if (this.props.isSetSz()) {
                this.props.unsetSz();
            }
        }
        else {
            if (size < 1.0 || 400.0 < size) {
                throw new IllegalArgumentException("Minimum inclusive = 1. Maximum inclusive = 400.");
            }
            this.props.setSz((int)(100.0 * size));
        }
    }
    
    public void setFillProperties(final XDDFFillProperties properties) {
        if (this.props.isSetBlipFill()) {
            this.props.unsetBlipFill();
        }
        if (this.props.isSetGradFill()) {
            this.props.unsetGradFill();
        }
        if (this.props.isSetGrpFill()) {
            this.props.unsetGrpFill();
        }
        if (this.props.isSetNoFill()) {
            this.props.unsetNoFill();
        }
        if (this.props.isSetPattFill()) {
            this.props.unsetPattFill();
        }
        if (this.props.isSetSolidFill()) {
            this.props.unsetSolidFill();
        }
        if (properties == null) {
            return;
        }
        if (properties instanceof XDDFGradientFillProperties) {
            this.props.setGradFill(((XDDFGradientFillProperties)properties).getXmlObject());
        }
        else if (properties instanceof XDDFGroupFillProperties) {
            this.props.setGrpFill(((XDDFGroupFillProperties)properties).getXmlObject());
        }
        else if (properties instanceof XDDFNoFillProperties) {
            this.props.setNoFill(((XDDFNoFillProperties)properties).getXmlObject());
        }
        else if (properties instanceof XDDFPatternFillProperties) {
            this.props.setPattFill(((XDDFPatternFillProperties)properties).getXmlObject());
        }
        else if (properties instanceof XDDFPictureFillProperties) {
            this.props.setBlipFill(((XDDFPictureFillProperties)properties).getXmlObject());
        }
        else if (properties instanceof XDDFSolidFillProperties) {
            this.props.setSolidFill(((XDDFSolidFillProperties)properties).getXmlObject());
        }
    }
    
    public void setCharacterKerning(final Double kerning) {
        if (kerning == null) {
            if (this.props.isSetKern()) {
                this.props.unsetKern();
            }
        }
        else {
            if (kerning < 0.0 || 4000.0 < kerning) {
                throw new IllegalArgumentException("Minimum inclusive = 0. Maximum inclusive = 4000.");
            }
            this.props.setKern((int)(100.0 * kerning));
        }
    }
    
    public void setCharacterSpacing(final Double spacing) {
        if (spacing == null) {
            if (this.props.isSetSpc()) {
                this.props.unsetSpc();
            }
        }
        else {
            if (spacing < -4000.0 || 4000.0 < spacing) {
                throw new IllegalArgumentException("Minimum inclusive = -4000. Maximum inclusive = 4000.");
            }
            this.props.setSpc((int)(100.0 * spacing));
        }
    }
    
    public void setFonts(final XDDFFont[] fonts) {
        for (final XDDFFont font : fonts) {
            final CTTextFont xml = font.getXmlObject();
            switch (font.getGroup()) {
                case COMPLEX_SCRIPT:
                    Label_0108: {
                        if (xml != null) {
                            this.props.setCs(xml);
                            break Label_0108;
                        }
                        if (this.props.isSetCs()) {
                            this.props.unsetCs();
                        }
                        break Label_0108;
                    }
                case EAST_ASIAN:
                    Label_0148: {
                        if (xml != null) {
                            this.props.setEa(xml);
                            break Label_0148;
                        }
                        if (this.props.isSetEa()) {
                            this.props.unsetEa();
                        }
                        break Label_0148;
                    }
                case LATIN:
                    Label_0188: {
                        if (xml != null) {
                            this.props.setLatin(xml);
                            break Label_0188;
                        }
                        if (this.props.isSetLatin()) {
                            this.props.unsetLatin();
                        }
                        break Label_0188;
                    }
                case SYMBOL: {
                    if (xml != null) {
                        this.props.setSym(xml);
                        break;
                    }
                    if (this.props.isSetSym()) {
                        this.props.unsetSym();
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    public void setUnderline(final UnderlineType underline) {
        if (underline == null) {
            if (this.props.isSetU()) {
                this.props.unsetU();
            }
        }
        else {
            this.props.setU(underline.underlying);
        }
    }
    
    public void setStrikeThrough(final StrikeType strike) {
        if (strike == null) {
            if (this.props.isSetStrike()) {
                this.props.unsetStrike();
            }
        }
        else {
            this.props.setStrike(strike.underlying);
        }
    }
    
    public void setCapitals(final CapsType caps) {
        if (caps == null) {
            if (this.props.isSetCap()) {
                this.props.unsetCap();
            }
        }
        else {
            this.props.setCap(caps.underlying);
        }
    }
    
    public void setHyperlink(final XDDFHyperlink link) {
        if (link == null) {
            if (this.props.isSetHlinkClick()) {
                this.props.unsetHlinkClick();
            }
        }
        else {
            this.props.setHlinkClick(link.getXmlObject());
        }
    }
    
    public void setMouseOver(final XDDFHyperlink link) {
        if (link == null) {
            if (this.props.isSetHlinkMouseOver()) {
                this.props.unsetHlinkMouseOver();
            }
        }
        else {
            this.props.setHlinkMouseOver(link.getXmlObject());
        }
    }
    
    public void setLanguage(final Locale lang) {
        if (lang == null) {
            if (this.props.isSetLang()) {
                this.props.unsetLang();
            }
        }
        else {
            this.props.setLang(lang.toLanguageTag());
        }
    }
    
    public void setAlternativeLanguage(final Locale lang) {
        if (lang == null) {
            if (this.props.isSetAltLang()) {
                this.props.unsetAltLang();
            }
        }
        else {
            this.props.setAltLang(lang.toLanguageTag());
        }
    }
    
    public void setHighlight(final XDDFColor color) {
        if (color == null) {
            if (this.props.isSetHighlight()) {
                this.props.unsetHighlight();
            }
        }
        else {
            this.props.setHighlight(color.getColorContainer());
        }
    }
    
    public void setLineProperties(final XDDFLineProperties properties) {
        if (properties == null) {
            if (this.props.isSetLn()) {
                this.props.unsetLn();
            }
        }
        else {
            this.props.setLn(properties.getXmlObject());
        }
    }
    
    public void setBookmark(final String bookmark) {
        if (bookmark == null) {
            if (this.props.isSetBmk()) {
                this.props.unsetBmk();
            }
        }
        else {
            this.props.setBmk(bookmark);
        }
    }
    
    public XDDFExtensionList getExtensionList() {
        if (this.props.isSetExtLst()) {
            return new XDDFExtensionList(this.props.getExtLst());
        }
        return null;
    }
    
    public void setExtensionList(final XDDFExtensionList list) {
        if (list == null) {
            if (this.props.isSetExtLst()) {
                this.props.unsetExtLst();
            }
        }
        else {
            this.props.setExtLst(list.getXmlObject());
        }
    }
    
    public XDDFEffectContainer getEffectContainer() {
        if (this.props.isSetEffectDag()) {
            return new XDDFEffectContainer(this.props.getEffectDag());
        }
        return null;
    }
    
    public void setEffectContainer(final XDDFEffectContainer container) {
        if (container == null) {
            if (this.props.isSetEffectDag()) {
                this.props.unsetEffectDag();
            }
        }
        else {
            this.props.setEffectDag(container.getXmlObject());
        }
    }
    
    public XDDFEffectList getEffectList() {
        if (this.props.isSetEffectLst()) {
            return new XDDFEffectList(this.props.getEffectLst());
        }
        return null;
    }
    
    public void setEffectList(final XDDFEffectList list) {
        if (list == null) {
            if (this.props.isSetEffectLst()) {
                this.props.unsetEffectLst();
            }
        }
        else {
            this.props.setEffectLst(list.getXmlObject());
        }
    }
}
