package org.apache.poi.xddf.usermodel;

import org.openxmlformats.schemas.drawingml.x2006.main.CTDashStop;
import org.apache.poi.util.Units;
import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineProperties;

public class XDDFLineProperties
{
    private CTLineProperties props;
    
    public XDDFLineProperties() {
        this(CTLineProperties.Factory.newInstance());
    }
    
    public XDDFLineProperties(final XDDFFillProperties fill) {
        this();
        this.setFillProperties(fill);
    }
    
    @Internal
    public XDDFLineProperties(final CTLineProperties properties) {
        this.props = properties;
    }
    
    @Internal
    public CTLineProperties getXmlObject() {
        return this.props;
    }
    
    public PenAlignment getPenAlignment() {
        if (this.props.isSetAlgn()) {
            return PenAlignment.valueOf(this.props.getAlgn());
        }
        return null;
    }
    
    public void setPenAlignment(final PenAlignment alignment) {
        if (alignment == null) {
            if (this.props.isSetAlgn()) {
                this.props.unsetAlgn();
            }
        }
        else {
            this.props.setAlgn(alignment.underlying);
        }
    }
    
    public LineCap getLineCap() {
        if (this.props.isSetCap()) {
            return LineCap.valueOf(this.props.getCap());
        }
        return null;
    }
    
    public void setLineCap(final LineCap cap) {
        if (cap == null) {
            if (this.props.isSetCap()) {
                this.props.unsetCap();
            }
        }
        else {
            this.props.setCap(cap.underlying);
        }
    }
    
    public CompoundLine getCompoundLine() {
        if (this.props.isSetCmpd()) {
            return CompoundLine.valueOf(this.props.getCmpd());
        }
        return null;
    }
    
    public void setCompoundLine(final CompoundLine compound) {
        if (compound == null) {
            if (this.props.isSetCmpd()) {
                this.props.unsetCmpd();
            }
        }
        else {
            this.props.setCmpd(compound.underlying);
        }
    }
    
    public XDDFDashStop addDashStop() {
        if (!this.props.isSetCustDash()) {
            this.props.addNewCustDash();
        }
        return new XDDFDashStop(this.props.getCustDash().addNewDs());
    }
    
    public XDDFDashStop insertDashStop(final int index) {
        if (!this.props.isSetCustDash()) {
            this.props.addNewCustDash();
        }
        return new XDDFDashStop(this.props.getCustDash().insertNewDs(index));
    }
    
    public void removeDashStop(final int index) {
        if (this.props.isSetCustDash()) {
            this.props.getCustDash().removeDs(index);
        }
    }
    
    public XDDFDashStop getDashStop(final int index) {
        if (this.props.isSetCustDash()) {
            return new XDDFDashStop(this.props.getCustDash().getDsArray(index));
        }
        return null;
    }
    
    public List<XDDFDashStop> getDashStops() {
        if (this.props.isSetCustDash()) {
            return Collections.unmodifiableList((List<? extends XDDFDashStop>)this.props.getCustDash().getDsList().stream().map(ds -> new XDDFDashStop(ds)).collect(Collectors.toList()));
        }
        return Collections.emptyList();
    }
    
    public int countDashStops() {
        if (this.props.isSetCustDash()) {
            return this.props.getCustDash().sizeOfDsArray();
        }
        return 0;
    }
    
    public XDDFPresetLineDash getPresetDash() {
        if (this.props.isSetPrstDash()) {
            return new XDDFPresetLineDash(this.props.getPrstDash());
        }
        return null;
    }
    
    public void setPresetDash(final XDDFPresetLineDash properties) {
        if (properties == null) {
            if (this.props.isSetPrstDash()) {
                this.props.unsetPrstDash();
            }
        }
        else {
            this.props.setPrstDash(properties.getXmlObject());
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
    
    public XDDFFillProperties getFillProperties() {
        if (this.props.isSetGradFill()) {
            return new XDDFGradientFillProperties(this.props.getGradFill());
        }
        if (this.props.isSetNoFill()) {
            return new XDDFNoFillProperties(this.props.getNoFill());
        }
        if (this.props.isSetPattFill()) {
            return new XDDFPatternFillProperties(this.props.getPattFill());
        }
        if (this.props.isSetSolidFill()) {
            return new XDDFSolidFillProperties(this.props.getSolidFill());
        }
        return null;
    }
    
    public void setFillProperties(final XDDFFillProperties properties) {
        if (this.props.isSetGradFill()) {
            this.props.unsetGradFill();
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
        else if (properties instanceof XDDFNoFillProperties) {
            this.props.setNoFill(((XDDFNoFillProperties)properties).getXmlObject());
        }
        else if (properties instanceof XDDFPatternFillProperties) {
            this.props.setPattFill(((XDDFPatternFillProperties)properties).getXmlObject());
        }
        else if (properties instanceof XDDFSolidFillProperties) {
            this.props.setSolidFill(((XDDFSolidFillProperties)properties).getXmlObject());
        }
    }
    
    public XDDFLineJoinProperties getLineJoinProperties() {
        if (this.props.isSetBevel()) {
            return new XDDFLineJoinBevelProperties(this.props.getBevel());
        }
        if (this.props.isSetMiter()) {
            return new XDDFLineJoinMiterProperties(this.props.getMiter());
        }
        if (this.props.isSetRound()) {
            return new XDDFLineJoinRoundProperties(this.props.getRound());
        }
        return null;
    }
    
    public void setLineJoinProperties(final XDDFLineJoinProperties properties) {
        if (this.props.isSetBevel()) {
            this.props.unsetBevel();
        }
        if (this.props.isSetMiter()) {
            this.props.unsetMiter();
        }
        if (this.props.isSetRound()) {
            this.props.unsetRound();
        }
        if (properties == null) {
            return;
        }
        if (properties instanceof XDDFLineJoinBevelProperties) {
            this.props.setBevel(((XDDFLineJoinBevelProperties)properties).getXmlObject());
        }
        else if (properties instanceof XDDFLineJoinMiterProperties) {
            this.props.setMiter(((XDDFLineJoinMiterProperties)properties).getXmlObject());
        }
        else if (properties instanceof XDDFLineJoinRoundProperties) {
            this.props.setRound(((XDDFLineJoinRoundProperties)properties).getXmlObject());
        }
    }
    
    public XDDFLineEndProperties getHeadEnd() {
        if (this.props.isSetHeadEnd()) {
            return new XDDFLineEndProperties(this.props.getHeadEnd());
        }
        return null;
    }
    
    public void setHeadEnd(final XDDFLineEndProperties properties) {
        if (properties == null) {
            if (this.props.isSetHeadEnd()) {
                this.props.unsetHeadEnd();
            }
        }
        else {
            this.props.setHeadEnd(properties.getXmlObject());
        }
    }
    
    public XDDFLineEndProperties getTailEnd() {
        if (this.props.isSetTailEnd()) {
            return new XDDFLineEndProperties(this.props.getTailEnd());
        }
        return null;
    }
    
    public void setTailEnd(final XDDFLineEndProperties properties) {
        if (properties == null) {
            if (this.props.isSetTailEnd()) {
                this.props.unsetTailEnd();
            }
        }
        else {
            this.props.setTailEnd(properties.getXmlObject());
        }
    }
    
    public Double getWidth() {
        if (this.props.isSetW()) {
            return Units.toPoints((long)this.props.getW());
        }
        return null;
    }
    
    public void setWidth(final Double width) {
        if (width == null) {
            if (this.props.isSetW()) {
                this.props.unsetW();
            }
        }
        else {
            this.props.setW(Units.toEMU((double)width));
        }
    }
}
