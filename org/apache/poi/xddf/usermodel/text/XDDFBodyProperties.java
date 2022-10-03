package org.apache.poi.xddf.usermodel.text;

import org.apache.poi.util.Units;
import org.apache.poi.xddf.usermodel.XDDFExtensionList;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBodyProperties;

public class XDDFBodyProperties
{
    private CTTextBodyProperties props;
    
    @Internal
    protected XDDFBodyProperties(final CTTextBodyProperties properties) {
        this.props = properties;
    }
    
    @Internal
    protected CTTextBodyProperties getXmlObject() {
        return this.props;
    }
    
    public AnchorType getAnchoring() {
        if (this.props.isSetAnchor()) {
            return AnchorType.valueOf(this.props.getAnchor());
        }
        return null;
    }
    
    public void setAnchoring(final AnchorType anchor) {
        if (anchor == null) {
            if (this.props.isSetAnchor()) {
                this.props.unsetAnchor();
            }
        }
        else {
            this.props.setAnchor(anchor.underlying);
        }
    }
    
    public Boolean isAnchorCentered() {
        if (this.props.isSetAnchorCtr()) {
            return this.props.getAnchorCtr();
        }
        return null;
    }
    
    public void setAnchorCentered(final Boolean centered) {
        if (centered == null) {
            if (this.props.isSetAnchorCtr()) {
                this.props.unsetAnchorCtr();
            }
        }
        else {
            this.props.setAnchorCtr((boolean)centered);
        }
    }
    
    public XDDFAutoFit getAutoFit() {
        if (this.props.isSetNoAutofit()) {
            return new XDDFNoAutoFit(this.props.getNoAutofit());
        }
        if (this.props.isSetNormAutofit()) {
            return new XDDFNormalAutoFit(this.props.getNormAutofit());
        }
        if (this.props.isSetSpAutoFit()) {
            return new XDDFShapeAutoFit(this.props.getSpAutoFit());
        }
        return new XDDFNormalAutoFit();
    }
    
    public void setAutoFit(final XDDFAutoFit autofit) {
        if (this.props.isSetNoAutofit()) {
            this.props.unsetNoAutofit();
        }
        if (this.props.isSetNormAutofit()) {
            this.props.unsetNormAutofit();
        }
        if (this.props.isSetSpAutoFit()) {
            this.props.unsetSpAutoFit();
        }
        if (autofit instanceof XDDFNoAutoFit) {
            this.props.setNoAutofit(((XDDFNoAutoFit)autofit).getXmlObject());
        }
        else if (autofit instanceof XDDFNormalAutoFit) {
            this.props.setNormAutofit(((XDDFNormalAutoFit)autofit).getXmlObject());
        }
        else if (autofit instanceof XDDFShapeAutoFit) {
            this.props.setSpAutoFit(((XDDFShapeAutoFit)autofit).getXmlObject());
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
    
    public Double getBottomInset() {
        if (this.props.isSetBIns()) {
            return Units.toPoints((long)this.props.getBIns());
        }
        return null;
    }
    
    public void setBottomInset(final Double points) {
        if (points == null || Double.isNaN(points)) {
            if (this.props.isSetBIns()) {
                this.props.unsetBIns();
            }
        }
        else {
            this.props.setBIns(Units.toEMU((double)points));
        }
    }
    
    public Double getLeftInset() {
        if (this.props.isSetLIns()) {
            return Units.toPoints((long)this.props.getLIns());
        }
        return null;
    }
    
    public void setLeftInset(final Double points) {
        if (points == null || Double.isNaN(points)) {
            if (this.props.isSetLIns()) {
                this.props.unsetLIns();
            }
        }
        else {
            this.props.setLIns(Units.toEMU((double)points));
        }
    }
    
    public Double getRightInset() {
        if (this.props.isSetRIns()) {
            return Units.toPoints((long)this.props.getRIns());
        }
        return null;
    }
    
    public void setRightInset(final Double points) {
        if (points == null || Double.isNaN(points)) {
            if (this.props.isSetRIns()) {
                this.props.unsetRIns();
            }
        }
        else {
            this.props.setRIns(Units.toEMU((double)points));
        }
    }
    
    public Double getTopInset() {
        if (this.props.isSetTIns()) {
            return Units.toPoints((long)this.props.getTIns());
        }
        return null;
    }
    
    public void setTopInset(final Double points) {
        if (points == null || Double.isNaN(points)) {
            if (this.props.isSetTIns()) {
                this.props.unsetTIns();
            }
        }
        else {
            this.props.setTIns(Units.toEMU((double)points));
        }
    }
    
    public Boolean hasParagraphSpacing() {
        if (this.props.isSetSpcFirstLastPara()) {
            return this.props.getSpcFirstLastPara();
        }
        return null;
    }
    
    public void setParagraphSpacing(final Boolean spacing) {
        if (spacing == null) {
            if (this.props.isSetSpcFirstLastPara()) {
                this.props.unsetSpcFirstLastPara();
            }
        }
        else {
            this.props.setSpcFirstLastPara((boolean)spacing);
        }
    }
    
    public Boolean isRightToLeft() {
        if (this.props.isSetRtlCol()) {
            return this.props.getRtlCol();
        }
        return null;
    }
    
    public void setRightToLeft(final Boolean rightToLeft) {
        if (rightToLeft == null) {
            if (this.props.isSetRtlCol()) {
                this.props.unsetRtlCol();
            }
        }
        else {
            this.props.setRtlCol((boolean)rightToLeft);
        }
    }
}
