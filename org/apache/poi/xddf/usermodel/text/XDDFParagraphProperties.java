package org.apache.poi.xddf.usermodel.text;

import org.openxmlformats.schemas.drawingml.x2006.main.CTTextTabStop;
import org.apache.poi.xddf.usermodel.XDDFExtensionList;
import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import org.apache.poi.util.Units;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;

public class XDDFParagraphProperties
{
    private CTTextParagraphProperties props;
    private XDDFParagraphBulletProperties bullet;
    
    @Internal
    protected XDDFParagraphProperties(final CTTextParagraphProperties properties) {
        this.props = properties;
        this.bullet = new XDDFParagraphBulletProperties(properties);
    }
    
    @Internal
    protected CTTextParagraphProperties getXmlObject() {
        return this.props;
    }
    
    public XDDFParagraphBulletProperties getBulletProperties() {
        return this.bullet;
    }
    
    public int getLevel() {
        if (this.props.isSetLvl()) {
            return 1 + this.props.getLvl();
        }
        return 0;
    }
    
    public void setLevel(final Integer level) {
        if (level == null) {
            if (this.props.isSetLvl()) {
                this.props.unsetLvl();
            }
        }
        else {
            if (level < 1 || 9 < level) {
                throw new IllegalArgumentException("Minimum inclusive: 1. Maximum inclusive: 9.");
            }
            this.props.setLvl(level - 1);
        }
    }
    
    public XDDFRunProperties addDefaultRunProperties() {
        if (!this.props.isSetDefRPr()) {
            this.props.addNewDefRPr();
        }
        return this.getDefaultRunProperties();
    }
    
    public XDDFRunProperties getDefaultRunProperties() {
        if (this.props.isSetDefRPr()) {
            return new XDDFRunProperties(this.props.getDefRPr());
        }
        return null;
    }
    
    public void setDefaultRunProperties(final XDDFRunProperties properties) {
        if (properties == null) {
            if (this.props.isSetDefRPr()) {
                this.props.unsetDefRPr();
            }
        }
        else {
            this.props.setDefRPr(properties.getXmlObject());
        }
    }
    
    public void setEastAsianLineBreak(final Boolean value) {
        if (value == null) {
            if (this.props.isSetEaLnBrk()) {
                this.props.unsetEaLnBrk();
            }
        }
        else {
            this.props.setEaLnBrk((boolean)value);
        }
    }
    
    public void setLatinLineBreak(final Boolean value) {
        if (value == null) {
            if (this.props.isSetLatinLnBrk()) {
                this.props.unsetLatinLnBrk();
            }
        }
        else {
            this.props.setLatinLnBrk((boolean)value);
        }
    }
    
    public void setHangingPunctuation(final Boolean value) {
        if (value == null) {
            if (this.props.isSetHangingPunct()) {
                this.props.unsetHangingPunct();
            }
        }
        else {
            this.props.setHangingPunct((boolean)value);
        }
    }
    
    public void setRightToLeft(final Boolean value) {
        if (value == null) {
            if (this.props.isSetRtl()) {
                this.props.unsetRtl();
            }
        }
        else {
            this.props.setRtl((boolean)value);
        }
    }
    
    public void setFontAlignment(final FontAlignment align) {
        if (align == null) {
            if (this.props.isSetFontAlgn()) {
                this.props.unsetFontAlgn();
            }
        }
        else {
            this.props.setFontAlgn(align.underlying);
        }
    }
    
    public void setTextAlignment(final TextAlignment align) {
        if (align == null) {
            if (this.props.isSetAlgn()) {
                this.props.unsetAlgn();
            }
        }
        else {
            this.props.setAlgn(align.underlying);
        }
    }
    
    public void setDefaultTabSize(final Double points) {
        if (points == null) {
            if (this.props.isSetDefTabSz()) {
                this.props.unsetDefTabSz();
            }
        }
        else {
            this.props.setDefTabSz(Units.toEMU((double)points));
        }
    }
    
    public void setIndentation(final Double points) {
        if (points == null) {
            if (this.props.isSetIndent()) {
                this.props.unsetIndent();
            }
        }
        else {
            if (points < -4032.0 || 4032.0 < points) {
                throw new IllegalArgumentException("Minimum inclusive = -4032. Maximum inclusive = 4032.");
            }
            this.props.setIndent(Units.toEMU((double)points));
        }
    }
    
    public void setMarginLeft(final Double points) {
        if (points == null) {
            if (this.props.isSetMarL()) {
                this.props.unsetMarL();
            }
        }
        else {
            if (points < 0.0 || 4032.0 < points) {
                throw new IllegalArgumentException("Minimum inclusive = 0. Maximum inclusive = 4032.");
            }
            this.props.setMarL(Units.toEMU((double)points));
        }
    }
    
    public void setMarginRight(final Double points) {
        if (points == null) {
            if (this.props.isSetMarR()) {
                this.props.unsetMarR();
            }
        }
        else {
            if (points < 0.0 || 4032.0 < points) {
                throw new IllegalArgumentException("Minimum inclusive = 0. Maximum inclusive = 4032.");
            }
            this.props.setMarR(Units.toEMU((double)points));
        }
    }
    
    public void setLineSpacing(final XDDFSpacing spacing) {
        if (spacing == null) {
            if (this.props.isSetLnSpc()) {
                this.props.unsetLnSpc();
            }
        }
        else {
            this.props.setLnSpc(spacing.getXmlObject());
        }
    }
    
    public void setSpaceAfter(final XDDFSpacing spacing) {
        if (spacing == null) {
            if (this.props.isSetSpcAft()) {
                this.props.unsetSpcAft();
            }
        }
        else {
            this.props.setSpcAft(spacing.getXmlObject());
        }
    }
    
    public void setSpaceBefore(final XDDFSpacing spacing) {
        if (spacing == null) {
            if (this.props.isSetSpcBef()) {
                this.props.unsetSpcBef();
            }
        }
        else {
            this.props.setSpcBef(spacing.getXmlObject());
        }
    }
    
    public XDDFTabStop addTabStop() {
        if (!this.props.isSetTabLst()) {
            this.props.addNewTabLst();
        }
        return new XDDFTabStop(this.props.getTabLst().addNewTab());
    }
    
    public XDDFTabStop insertTabStop(final int index) {
        if (!this.props.isSetTabLst()) {
            this.props.addNewTabLst();
        }
        return new XDDFTabStop(this.props.getTabLst().insertNewTab(index));
    }
    
    public void removeTabStop(final int index) {
        if (this.props.isSetTabLst()) {
            this.props.getTabLst().removeTab(index);
        }
    }
    
    public XDDFTabStop getTabStop(final int index) {
        if (this.props.isSetTabLst()) {
            return new XDDFTabStop(this.props.getTabLst().getTabArray(index));
        }
        return null;
    }
    
    public List<XDDFTabStop> getTabStops() {
        if (this.props.isSetTabLst()) {
            return Collections.unmodifiableList((List<? extends XDDFTabStop>)this.props.getTabLst().getTabList().stream().map(gs -> new XDDFTabStop(gs)).collect(Collectors.toList()));
        }
        return Collections.emptyList();
    }
    
    public int countTabStops() {
        if (this.props.isSetTabLst()) {
            return this.props.getTabLst().sizeOfTabArray();
        }
        return 0;
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
}
