package org.apache.poi.xddf.usermodel.chart;

import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import java.util.Optional;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import java.util.function.Function;
import org.apache.poi.xddf.usermodel.text.XDDFTextBody;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLegendEntry;
import org.apache.poi.xddf.usermodel.text.TextContainer;

public class XDDFLegendEntry implements TextContainer
{
    private CTLegendEntry entry;
    
    @Internal
    protected XDDFLegendEntry(final CTLegendEntry entry) {
        this.entry = entry;
        if (entry.getIdx() == null) {
            entry.addNewIdx().setVal(0L);
        }
    }
    
    @Internal
    protected CTLegendEntry getXmlObject() {
        return this.entry;
    }
    
    public XDDFTextBody getTextBody() {
        if (this.entry.isSetTxPr()) {
            return new XDDFTextBody(this, this.entry.getTxPr());
        }
        return null;
    }
    
    public void setTextBody(final XDDFTextBody body) {
        if (body == null) {
            if (this.entry.isSetTxPr()) {
                this.entry.unsetTxPr();
            }
        }
        else {
            this.entry.setTxPr(body.getXmlObject());
        }
    }
    
    public boolean getDelete() {
        return this.entry.isSetDelete() && this.entry.getDelete().getVal();
    }
    
    public void setDelete(final Boolean delete) {
        if (delete == null) {
            if (this.entry.isSetDelete()) {
                this.entry.unsetDelete();
            }
        }
        else if (this.entry.isSetDelete()) {
            this.entry.getDelete().setVal((boolean)delete);
        }
        else {
            this.entry.addNewDelete().setVal((boolean)delete);
        }
    }
    
    public long getIndex() {
        return this.entry.getIdx().getVal();
    }
    
    public void setIndex(final long index) {
        this.entry.getIdx().setVal(index);
    }
    
    public void setExtensionList(final XDDFChartExtensionList list) {
        if (list == null) {
            if (this.entry.isSetExtLst()) {
                this.entry.unsetExtLst();
            }
        }
        else {
            this.entry.setExtLst(list.getXmlObject());
        }
    }
    
    public XDDFChartExtensionList getExtensionList() {
        if (this.entry.isSetExtLst()) {
            return new XDDFChartExtensionList(this.entry.getExtLst());
        }
        return null;
    }
    
    @Override
    public <R> Optional<R> findDefinedParagraphProperty(final Function<CTTextParagraphProperties, Boolean> isSet, final Function<CTTextParagraphProperties, R> getter) {
        return Optional.empty();
    }
    
    @Override
    public <R> Optional<R> findDefinedRunProperty(final Function<CTTextCharacterProperties, Boolean> isSet, final Function<CTTextCharacterProperties, R> getter) {
        return Optional.empty();
    }
}
