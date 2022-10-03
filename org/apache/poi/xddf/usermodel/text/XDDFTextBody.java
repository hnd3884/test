package org.apache.poi.xddf.usermodel.text;

import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import java.util.Optional;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import java.util.function.Function;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextListStyle;
import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Locale;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;

public class XDDFTextBody
{
    private CTTextBody _body;
    private TextContainer _parent;
    
    public XDDFTextBody(final TextContainer parent) {
        this(parent, CTTextBody.Factory.newInstance());
    }
    
    @Internal
    public XDDFTextBody(final TextContainer parent, final CTTextBody body) {
        this._parent = parent;
        this._body = body;
    }
    
    @Internal
    public CTTextBody getXmlObject() {
        return this._body;
    }
    
    public TextContainer getParentShape() {
        return this._parent;
    }
    
    public XDDFTextParagraph initialize() {
        this._body.addNewLstStyle();
        this._body.addNewBodyPr();
        final XDDFBodyProperties bp = this.getBodyProperties();
        bp.setAnchoring(AnchorType.TOP);
        bp.setRightToLeft(false);
        final XDDFTextParagraph p = this.addNewParagraph();
        p.setTextAlignment(TextAlignment.LEFT);
        p.appendRegularRun("");
        final XDDFRunProperties end = p.addAfterLastRunProperties();
        end.setLanguage(Locale.US);
        end.setFontSize(11.0);
        return p;
    }
    
    public void setText(final String text) {
        if (this._body.sizeOfPArray() > 0) {
            for (int i = this._body.sizeOfPArray() - 1; i > 0; --i) {
                this._body.removeP(i);
            }
            this.getParagraph(0).setText(text);
        }
        else {
            this.initialize().setText(text);
        }
    }
    
    public XDDFTextParagraph addNewParagraph() {
        return new XDDFTextParagraph(this._body.addNewP(), this);
    }
    
    public XDDFTextParagraph insertNewParagraph(final int index) {
        return new XDDFTextParagraph(this._body.insertNewP(index), this);
    }
    
    public void removeParagraph(final int index) {
        this._body.removeP(index);
    }
    
    public XDDFTextParagraph getParagraph(final int index) {
        return new XDDFTextParagraph(this._body.getPArray(index), this);
    }
    
    public List<XDDFTextParagraph> getParagraphs() {
        return Collections.unmodifiableList((List<? extends XDDFTextParagraph>)this._body.getPList().stream().map(ds -> new XDDFTextParagraph(ds, this)).collect(Collectors.toList()));
    }
    
    public XDDFBodyProperties getBodyProperties() {
        return new XDDFBodyProperties(this._body.getBodyPr());
    }
    
    public void setBodyProperties(final XDDFBodyProperties properties) {
        if (properties == null) {
            this._body.addNewBodyPr();
        }
        else {
            this._body.setBodyPr(properties.getXmlObject());
        }
    }
    
    public XDDFParagraphProperties getDefaultProperties() {
        if (this._body.isSetLstStyle() && this._body.getLstStyle().isSetDefPPr()) {
            return new XDDFParagraphProperties(this._body.getLstStyle().getDefPPr());
        }
        return null;
    }
    
    public void setDefaultProperties(final XDDFParagraphProperties properties) {
        if (properties == null) {
            if (this._body.isSetLstStyle()) {
                final CTTextListStyle style = this._body.getLstStyle();
                if (style.isSetDefPPr()) {
                    style.unsetDefPPr();
                }
            }
        }
        else {
            final CTTextListStyle style = this._body.isSetLstStyle() ? this._body.getLstStyle() : this._body.addNewLstStyle();
            style.setDefPPr(properties.getXmlObject());
        }
    }
    
    public XDDFParagraphProperties getLevel1Properties() {
        if (this._body.isSetLstStyle() && this._body.getLstStyle().isSetLvl1PPr()) {
            return new XDDFParagraphProperties(this._body.getLstStyle().getLvl1PPr());
        }
        return null;
    }
    
    public void setLevel1Properties(final XDDFParagraphProperties properties) {
        if (properties == null) {
            if (this._body.isSetLstStyle()) {
                final CTTextListStyle style = this._body.getLstStyle();
                if (style.isSetLvl1PPr()) {
                    style.unsetLvl1PPr();
                }
            }
        }
        else {
            final CTTextListStyle style = this._body.isSetLstStyle() ? this._body.getLstStyle() : this._body.addNewLstStyle();
            style.setLvl1PPr(properties.getXmlObject());
        }
    }
    
    public XDDFParagraphProperties getLevel2Properties() {
        if (this._body.isSetLstStyle() && this._body.getLstStyle().isSetLvl2PPr()) {
            return new XDDFParagraphProperties(this._body.getLstStyle().getLvl2PPr());
        }
        return null;
    }
    
    public void setLevel2Properties(final XDDFParagraphProperties properties) {
        if (properties == null) {
            if (this._body.isSetLstStyle()) {
                final CTTextListStyle style = this._body.getLstStyle();
                if (style.isSetLvl2PPr()) {
                    style.unsetLvl2PPr();
                }
            }
        }
        else {
            final CTTextListStyle style = this._body.isSetLstStyle() ? this._body.getLstStyle() : this._body.addNewLstStyle();
            style.setLvl2PPr(properties.getXmlObject());
        }
    }
    
    public XDDFParagraphProperties getLevel3Properties() {
        if (this._body.isSetLstStyle() && this._body.getLstStyle().isSetLvl3PPr()) {
            return new XDDFParagraphProperties(this._body.getLstStyle().getLvl3PPr());
        }
        return null;
    }
    
    public void setLevel3Properties(final XDDFParagraphProperties properties) {
        if (properties == null) {
            if (this._body.isSetLstStyle()) {
                final CTTextListStyle style = this._body.getLstStyle();
                if (style.isSetLvl3PPr()) {
                    style.unsetLvl3PPr();
                }
            }
        }
        else {
            final CTTextListStyle style = this._body.isSetLstStyle() ? this._body.getLstStyle() : this._body.addNewLstStyle();
            style.setLvl3PPr(properties.getXmlObject());
        }
    }
    
    public XDDFParagraphProperties getLevel4Properties() {
        if (this._body.isSetLstStyle() && this._body.getLstStyle().isSetLvl4PPr()) {
            return new XDDFParagraphProperties(this._body.getLstStyle().getLvl4PPr());
        }
        return null;
    }
    
    public void setLevel4Properties(final XDDFParagraphProperties properties) {
        if (properties == null) {
            if (this._body.isSetLstStyle()) {
                final CTTextListStyle style = this._body.getLstStyle();
                if (style.isSetLvl4PPr()) {
                    style.unsetLvl4PPr();
                }
            }
        }
        else {
            final CTTextListStyle style = this._body.isSetLstStyle() ? this._body.getLstStyle() : this._body.addNewLstStyle();
            style.setLvl4PPr(properties.getXmlObject());
        }
    }
    
    public XDDFParagraphProperties getLevel5Properties() {
        if (this._body.isSetLstStyle() && this._body.getLstStyle().isSetLvl5PPr()) {
            return new XDDFParagraphProperties(this._body.getLstStyle().getLvl5PPr());
        }
        return null;
    }
    
    public void setLevel5Properties(final XDDFParagraphProperties properties) {
        if (properties == null) {
            if (this._body.isSetLstStyle()) {
                final CTTextListStyle style = this._body.getLstStyle();
                if (style.isSetLvl5PPr()) {
                    style.unsetLvl5PPr();
                }
            }
        }
        else {
            final CTTextListStyle style = this._body.isSetLstStyle() ? this._body.getLstStyle() : this._body.addNewLstStyle();
            style.setLvl5PPr(properties.getXmlObject());
        }
    }
    
    public XDDFParagraphProperties getLevel6Properties() {
        if (this._body.isSetLstStyle() && this._body.getLstStyle().isSetLvl6PPr()) {
            return new XDDFParagraphProperties(this._body.getLstStyle().getLvl6PPr());
        }
        return null;
    }
    
    public void setLevel6Properties(final XDDFParagraphProperties properties) {
        if (properties == null) {
            if (this._body.isSetLstStyle()) {
                final CTTextListStyle style = this._body.getLstStyle();
                if (style.isSetLvl6PPr()) {
                    style.unsetLvl6PPr();
                }
            }
        }
        else {
            final CTTextListStyle style = this._body.isSetLstStyle() ? this._body.getLstStyle() : this._body.addNewLstStyle();
            style.setLvl6PPr(properties.getXmlObject());
        }
    }
    
    public XDDFParagraphProperties getLevel7Properties() {
        if (this._body.isSetLstStyle() && this._body.getLstStyle().isSetLvl7PPr()) {
            return new XDDFParagraphProperties(this._body.getLstStyle().getLvl7PPr());
        }
        return null;
    }
    
    public void setLevel7Properties(final XDDFParagraphProperties properties) {
        if (properties == null) {
            if (this._body.isSetLstStyle()) {
                final CTTextListStyle style = this._body.getLstStyle();
                if (style.isSetLvl7PPr()) {
                    style.unsetLvl7PPr();
                }
            }
        }
        else {
            final CTTextListStyle style = this._body.isSetLstStyle() ? this._body.getLstStyle() : this._body.addNewLstStyle();
            style.setLvl7PPr(properties.getXmlObject());
        }
    }
    
    public XDDFParagraphProperties getLevel8Properties() {
        if (this._body.isSetLstStyle() && this._body.getLstStyle().isSetLvl8PPr()) {
            return new XDDFParagraphProperties(this._body.getLstStyle().getLvl8PPr());
        }
        return null;
    }
    
    public void setLevel8Properties(final XDDFParagraphProperties properties) {
        if (properties == null) {
            if (this._body.isSetLstStyle()) {
                final CTTextListStyle style = this._body.getLstStyle();
                if (style.isSetLvl8PPr()) {
                    style.unsetLvl8PPr();
                }
            }
        }
        else {
            final CTTextListStyle style = this._body.isSetLstStyle() ? this._body.getLstStyle() : this._body.addNewLstStyle();
            style.setLvl8PPr(properties.getXmlObject());
        }
    }
    
    public XDDFParagraphProperties getLevel9Properties() {
        if (this._body.isSetLstStyle() && this._body.getLstStyle().isSetLvl9PPr()) {
            return new XDDFParagraphProperties(this._body.getLstStyle().getLvl9PPr());
        }
        return null;
    }
    
    public void setLevel9Properties(final XDDFParagraphProperties properties) {
        if (properties == null) {
            if (this._body.isSetLstStyle()) {
                final CTTextListStyle style = this._body.getLstStyle();
                if (style.isSetLvl9PPr()) {
                    style.unsetLvl9PPr();
                }
            }
        }
        else {
            final CTTextListStyle style = this._body.isSetLstStyle() ? this._body.getLstStyle() : this._body.addNewLstStyle();
            style.setLvl9PPr(properties.getXmlObject());
        }
    }
    
    @Internal
    protected <R> Optional<R> findDefinedParagraphProperty(final Function<CTTextParagraphProperties, Boolean> isSet, final Function<CTTextParagraphProperties, R> getter, final int level) {
        if (this._body.isSetLstStyle() && level >= 0) {
            final CTTextListStyle list = this._body.getLstStyle();
            final CTTextParagraphProperties props = (level == 0) ? list.getDefPPr() : this.retrieveProperties(list, level);
            if (props != null && isSet.apply(props)) {
                return Optional.of(getter.apply(props));
            }
            return (Optional<R>)this.findDefinedParagraphProperty(isSet, (Function<CTTextParagraphProperties, Object>)getter, level - 1);
        }
        else {
            if (this._parent != null) {
                return this._parent.findDefinedParagraphProperty(isSet, getter);
            }
            return Optional.empty();
        }
    }
    
    @Internal
    protected <R> Optional<R> findDefinedRunProperty(final Function<CTTextCharacterProperties, Boolean> isSet, final Function<CTTextCharacterProperties, R> getter, final int level) {
        if (this._body.isSetLstStyle() && level >= 0) {
            final CTTextListStyle list = this._body.getLstStyle();
            final CTTextParagraphProperties props = (level == 0) ? list.getDefPPr() : this.retrieveProperties(list, level);
            if (props != null && props.isSetDefRPr() && isSet.apply(props.getDefRPr())) {
                return Optional.of(getter.apply(props.getDefRPr()));
            }
            return (Optional<R>)this.findDefinedRunProperty(isSet, (Function<CTTextCharacterProperties, Object>)getter, level - 1);
        }
        else {
            if (this._parent != null) {
                return this._parent.findDefinedRunProperty(isSet, getter);
            }
            return Optional.empty();
        }
    }
    
    private CTTextParagraphProperties retrieveProperties(final CTTextListStyle list, final int level) {
        switch (level) {
            case 1: {
                if (list.isSetLvl1PPr()) {
                    return list.getLvl1PPr();
                }
                return null;
            }
            case 2: {
                if (list.isSetLvl2PPr()) {
                    return list.getLvl2PPr();
                }
                return null;
            }
            case 3: {
                if (list.isSetLvl3PPr()) {
                    return list.getLvl3PPr();
                }
                return null;
            }
            case 4: {
                if (list.isSetLvl4PPr()) {
                    return list.getLvl4PPr();
                }
                return null;
            }
            case 5: {
                if (list.isSetLvl5PPr()) {
                    return list.getLvl5PPr();
                }
                return null;
            }
            case 6: {
                if (list.isSetLvl6PPr()) {
                    return list.getLvl6PPr();
                }
                return null;
            }
            case 7: {
                if (list.isSetLvl7PPr()) {
                    return list.getLvl7PPr();
                }
                return null;
            }
            case 8: {
                if (list.isSetLvl8PPr()) {
                    return list.getLvl8PPr();
                }
                return null;
            }
            case 9: {
                if (list.isSetLvl9PPr()) {
                    return list.getLvl9PPr();
                }
                return null;
            }
            default: {
                return null;
            }
        }
    }
}
