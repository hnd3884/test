package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.ChildAnchor;
import org.openxmlformats.schemas.drawingml.x2006.main.STPresetLineDashVal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetLineDashProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNoFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.apache.poi.ss.usermodel.Shape;

public abstract class XSSFShape implements Shape
{
    protected XSSFDrawing drawing;
    protected XSSFShapeGroup parent;
    protected XSSFAnchor anchor;
    
    public XSSFDrawing getDrawing() {
        return this.drawing;
    }
    
    public XSSFShapeGroup getParent() {
        return this.parent;
    }
    
    public XSSFAnchor getAnchor() {
        return this.anchor;
    }
    
    protected abstract CTShapeProperties getShapeProperties();
    
    public boolean isNoFill() {
        return this.getShapeProperties().isSetNoFill();
    }
    
    public void setNoFill(final boolean noFill) {
        final CTShapeProperties props = this.getShapeProperties();
        if (props.isSetPattFill()) {
            props.unsetPattFill();
        }
        if (props.isSetSolidFill()) {
            props.unsetSolidFill();
        }
        props.setNoFill(CTNoFillProperties.Factory.newInstance());
    }
    
    public void setFillColor(final int red, final int green, final int blue) {
        final CTShapeProperties props = this.getShapeProperties();
        final CTSolidColorFillProperties fill = props.isSetSolidFill() ? props.getSolidFill() : props.addNewSolidFill();
        final CTSRgbColor rgb = CTSRgbColor.Factory.newInstance();
        rgb.setVal(new byte[] { (byte)red, (byte)green, (byte)blue });
        fill.setSrgbClr(rgb);
    }
    
    public void setLineStyleColor(final int red, final int green, final int blue) {
        final CTShapeProperties props = this.getShapeProperties();
        final CTLineProperties ln = props.isSetLn() ? props.getLn() : props.addNewLn();
        final CTSolidColorFillProperties fill = ln.isSetSolidFill() ? ln.getSolidFill() : ln.addNewSolidFill();
        final CTSRgbColor rgb = CTSRgbColor.Factory.newInstance();
        rgb.setVal(new byte[] { (byte)red, (byte)green, (byte)blue });
        fill.setSrgbClr(rgb);
    }
    
    public void setLineWidth(final double lineWidth) {
        final CTShapeProperties props = this.getShapeProperties();
        final CTLineProperties ln = props.isSetLn() ? props.getLn() : props.addNewLn();
        ln.setW((int)(lineWidth * 12700.0));
    }
    
    public void setLineStyle(final int lineStyle) {
        final CTShapeProperties props = this.getShapeProperties();
        final CTLineProperties ln = props.isSetLn() ? props.getLn() : props.addNewLn();
        final CTPresetLineDashProperties dashStyle = CTPresetLineDashProperties.Factory.newInstance();
        dashStyle.setVal(STPresetLineDashVal.Enum.forInt(lineStyle + 1));
        ln.setPrstDash(dashStyle);
    }
}
