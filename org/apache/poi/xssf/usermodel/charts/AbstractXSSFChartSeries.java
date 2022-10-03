package org.apache.poi.xssf.usermodel.charts;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerTx;
import org.apache.poi.ss.usermodel.charts.TitleType;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.Removal;
import org.apache.poi.ss.usermodel.charts.ChartSeries;

@Deprecated
@Removal(version = "4.2")
public abstract class AbstractXSSFChartSeries implements ChartSeries
{
    private String titleValue;
    private CellReference titleRef;
    private TitleType titleType;
    
    public void setTitle(final CellReference titleReference) {
        this.titleType = TitleType.CELL_REFERENCE;
        this.titleRef = titleReference;
    }
    
    public void setTitle(final String title) {
        this.titleType = TitleType.STRING;
        this.titleValue = title;
    }
    
    public CellReference getTitleCellReference() {
        if (TitleType.CELL_REFERENCE.equals((Object)this.titleType)) {
            return this.titleRef;
        }
        throw new IllegalStateException("Title type is not CellReference.");
    }
    
    public String getTitleString() {
        if (TitleType.STRING.equals((Object)this.titleType)) {
            return this.titleValue;
        }
        throw new IllegalStateException("Title type is not String.");
    }
    
    public TitleType getTitleType() {
        return this.titleType;
    }
    
    protected boolean isTitleSet() {
        return this.titleType != null;
    }
    
    protected CTSerTx getCTSerTx() {
        final CTSerTx tx = CTSerTx.Factory.newInstance();
        switch (this.titleType) {
            case CELL_REFERENCE: {
                tx.addNewStrRef().setF(this.titleRef.formatAsString());
                return tx;
            }
            case STRING: {
                tx.setV(this.titleValue);
                return tx;
            }
            default: {
                throw new IllegalStateException("Unkown title type: " + this.titleType);
            }
        }
    }
}
