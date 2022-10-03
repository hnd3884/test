package org.apache.poi.xssf.usermodel.charts;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTLayoutTarget;
import org.openxmlformats.schemas.drawingml.x2006.chart.STLayoutTarget;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLayoutMode;
import org.openxmlformats.schemas.drawingml.x2006.chart.STLayoutMode;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLayout;
import org.apache.poi.ss.usermodel.charts.LayoutTarget;
import org.apache.poi.ss.usermodel.charts.LayoutMode;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTManualLayout;
import org.apache.poi.util.Removal;
import org.apache.poi.ss.usermodel.charts.ManualLayout;

@Deprecated
@Removal(version = "4.2")
public final class XSSFManualLayout implements ManualLayout
{
    private CTManualLayout layout;
    private static final LayoutMode defaultLayoutMode;
    private static final LayoutTarget defaultLayoutTarget;
    
    public XSSFManualLayout(final CTLayout ctLayout) {
        this.initLayout(ctLayout);
    }
    
    public XSSFManualLayout(final XSSFChart chart) {
        final CTPlotArea ctPlotArea = chart.getCTChart().getPlotArea();
        final CTLayout ctLayout = ctPlotArea.isSetLayout() ? ctPlotArea.getLayout() : ctPlotArea.addNewLayout();
        this.initLayout(ctLayout);
    }
    
    @Internal
    public CTManualLayout getCTManualLayout() {
        return this.layout;
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
            return XSSFManualLayout.defaultLayoutTarget;
        }
        return this.toLayoutTarget(this.layout.getLayoutTarget());
    }
    
    public void setTarget(final LayoutTarget target) {
        if (!this.layout.isSetLayoutTarget()) {
            this.layout.addNewLayoutTarget();
        }
        this.layout.getLayoutTarget().setVal(this.fromLayoutTarget(target));
    }
    
    public LayoutMode getXMode() {
        if (!this.layout.isSetXMode()) {
            return XSSFManualLayout.defaultLayoutMode;
        }
        return this.toLayoutMode(this.layout.getXMode());
    }
    
    public void setXMode(final LayoutMode mode) {
        if (!this.layout.isSetXMode()) {
            this.layout.addNewXMode();
        }
        this.layout.getXMode().setVal(this.fromLayoutMode(mode));
    }
    
    public LayoutMode getYMode() {
        if (!this.layout.isSetYMode()) {
            return XSSFManualLayout.defaultLayoutMode;
        }
        return this.toLayoutMode(this.layout.getYMode());
    }
    
    public void setYMode(final LayoutMode mode) {
        if (!this.layout.isSetYMode()) {
            this.layout.addNewYMode();
        }
        this.layout.getYMode().setVal(this.fromLayoutMode(mode));
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
            return XSSFManualLayout.defaultLayoutMode;
        }
        return this.toLayoutMode(this.layout.getWMode());
    }
    
    public void setWidthMode(final LayoutMode mode) {
        if (!this.layout.isSetWMode()) {
            this.layout.addNewWMode();
        }
        this.layout.getWMode().setVal(this.fromLayoutMode(mode));
    }
    
    public LayoutMode getHeightMode() {
        if (!this.layout.isSetHMode()) {
            return XSSFManualLayout.defaultLayoutMode;
        }
        return this.toLayoutMode(this.layout.getHMode());
    }
    
    public void setHeightMode(final LayoutMode mode) {
        if (!this.layout.isSetHMode()) {
            this.layout.addNewHMode();
        }
        this.layout.getHMode().setVal(this.fromLayoutMode(mode));
    }
    
    private void initLayout(final CTLayout ctLayout) {
        if (ctLayout.isSetManualLayout()) {
            this.layout = ctLayout.getManualLayout();
        }
        else {
            this.layout = ctLayout.addNewManualLayout();
        }
    }
    
    private STLayoutMode.Enum fromLayoutMode(final LayoutMode mode) {
        switch (mode) {
            case EDGE: {
                return STLayoutMode.EDGE;
            }
            case FACTOR: {
                return STLayoutMode.FACTOR;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    private LayoutMode toLayoutMode(final CTLayoutMode ctLayoutMode) {
        switch (ctLayoutMode.getVal().intValue()) {
            case 1: {
                return LayoutMode.EDGE;
            }
            case 2: {
                return LayoutMode.FACTOR;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    private STLayoutTarget.Enum fromLayoutTarget(final LayoutTarget target) {
        switch (target) {
            case INNER: {
                return STLayoutTarget.INNER;
            }
            case OUTER: {
                return STLayoutTarget.OUTER;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    private LayoutTarget toLayoutTarget(final CTLayoutTarget ctLayoutTarget) {
        switch (ctLayoutTarget.getVal().intValue()) {
            case 1: {
                return LayoutTarget.INNER;
            }
            case 2: {
                return LayoutTarget.OUTER;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    static {
        defaultLayoutMode = LayoutMode.EDGE;
        defaultLayoutTarget = LayoutTarget.INNER;
    }
}
