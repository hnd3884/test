package org.apache.poi.xddf.usermodel.chart;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLayout;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTManualLayout;

public final class XDDFManualLayout
{
    private CTManualLayout layout;
    private static final LayoutMode defaultLayoutMode;
    private static final LayoutTarget defaultLayoutTarget;
    
    public XDDFManualLayout(final CTLayout ctLayout) {
        this.initializeLayout(ctLayout);
    }
    
    public XDDFManualLayout(final CTPlotArea ctPlotArea) {
        final CTLayout ctLayout = ctPlotArea.isSetLayout() ? ctPlotArea.getLayout() : ctPlotArea.addNewLayout();
        this.initializeLayout(ctLayout);
    }
    
    @Internal
    protected CTManualLayout getXmlObject() {
        return this.layout;
    }
    
    public void setExtensionList(final XDDFChartExtensionList list) {
        if (list == null) {
            if (this.layout.isSetExtLst()) {
                this.layout.unsetExtLst();
            }
        }
        else {
            this.layout.setExtLst(list.getXmlObject());
        }
    }
    
    public XDDFChartExtensionList getExtensionList() {
        if (this.layout.isSetExtLst()) {
            return new XDDFChartExtensionList(this.layout.getExtLst());
        }
        return null;
    }
    
    public void setWidthRatio(final double ratio) {
        if (!this.layout.isSetW()) {
            this.layout.addNewW();
        }
        this.layout.getW().setVal(ratio);
    }
    
    public double getWidthRatio() {
        if (!this.layout.isSetW()) {
            return 0.0;
        }
        return this.layout.getW().getVal();
    }
    
    public void setHeightRatio(final double ratio) {
        if (!this.layout.isSetH()) {
            this.layout.addNewH();
        }
        this.layout.getH().setVal(ratio);
    }
    
    public double getHeightRatio() {
        if (!this.layout.isSetH()) {
            return 0.0;
        }
        return this.layout.getH().getVal();
    }
    
    public LayoutTarget getTarget() {
        if (!this.layout.isSetLayoutTarget()) {
            return XDDFManualLayout.defaultLayoutTarget;
        }
        return LayoutTarget.valueOf(this.layout.getLayoutTarget().getVal());
    }
    
    public void setTarget(final LayoutTarget target) {
        if (!this.layout.isSetLayoutTarget()) {
            this.layout.addNewLayoutTarget();
        }
        this.layout.getLayoutTarget().setVal(target.underlying);
    }
    
    public LayoutMode getXMode() {
        if (!this.layout.isSetXMode()) {
            return XDDFManualLayout.defaultLayoutMode;
        }
        return LayoutMode.valueOf(this.layout.getXMode().getVal());
    }
    
    public void setXMode(final LayoutMode mode) {
        if (!this.layout.isSetXMode()) {
            this.layout.addNewXMode();
        }
        this.layout.getXMode().setVal(mode.underlying);
    }
    
    public LayoutMode getYMode() {
        if (!this.layout.isSetYMode()) {
            return XDDFManualLayout.defaultLayoutMode;
        }
        return LayoutMode.valueOf(this.layout.getYMode().getVal());
    }
    
    public void setYMode(final LayoutMode mode) {
        if (!this.layout.isSetYMode()) {
            this.layout.addNewYMode();
        }
        this.layout.getYMode().setVal(mode.underlying);
    }
    
    public double getX() {
        if (!this.layout.isSetX()) {
            return 0.0;
        }
        return this.layout.getX().getVal();
    }
    
    public void setX(final double x) {
        if (!this.layout.isSetX()) {
            this.layout.addNewX();
        }
        this.layout.getX().setVal(x);
    }
    
    public double getY() {
        if (!this.layout.isSetY()) {
            return 0.0;
        }
        return this.layout.getY().getVal();
    }
    
    public void setY(final double y) {
        if (!this.layout.isSetY()) {
            this.layout.addNewY();
        }
        this.layout.getY().setVal(y);
    }
    
    public LayoutMode getWidthMode() {
        if (!this.layout.isSetWMode()) {
            return XDDFManualLayout.defaultLayoutMode;
        }
        return LayoutMode.valueOf(this.layout.getWMode().getVal());
    }
    
    public void setWidthMode(final LayoutMode mode) {
        if (!this.layout.isSetWMode()) {
            this.layout.addNewWMode();
        }
        this.layout.getWMode().setVal(mode.underlying);
    }
    
    public LayoutMode getHeightMode() {
        if (!this.layout.isSetHMode()) {
            return XDDFManualLayout.defaultLayoutMode;
        }
        return LayoutMode.valueOf(this.layout.getHMode().getVal());
    }
    
    public void setHeightMode(final LayoutMode mode) {
        if (!this.layout.isSetHMode()) {
            this.layout.addNewHMode();
        }
        this.layout.getHMode().setVal(mode.underlying);
    }
    
    private void initializeLayout(final CTLayout ctLayout) {
        if (ctLayout.isSetManualLayout()) {
            this.layout = ctLayout.getManualLayout();
        }
        else {
            this.layout = ctLayout.addNewManualLayout();
        }
    }
    
    static {
        defaultLayoutMode = LayoutMode.EDGE;
        defaultLayoutTarget = LayoutTarget.INNER;
    }
}
