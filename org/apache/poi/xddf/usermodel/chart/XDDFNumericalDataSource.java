package org.apache.poi.xddf.usermodel.chart;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumVal;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumData;

public interface XDDFNumericalDataSource<T extends Number> extends XDDFDataSource<T>
{
    String getFormatCode();
    
    void setFormatCode(final String p0);
    
    default boolean isLiteral() {
        return false;
    }
    
    @Internal
    default void fillNumericalCache(final CTNumData cache) {
        final String formatCode = this.getFormatCode();
        if (formatCode == null) {
            if (cache.isSetFormatCode()) {
                cache.unsetFormatCode();
            }
        }
        else {
            cache.setFormatCode(formatCode);
        }
        cache.setPtArray((CTNumVal[])null);
        final int numOfPoints = this.getPointCount();
        int effectiveNumOfPoints = 0;
        for (int i = 0; i < numOfPoints; ++i) {
            final Object value = this.getPointAt(i);
            if (value != null) {
                final CTNumVal ctNumVal = cache.addNewPt();
                ctNumVal.setIdx((long)i);
                ctNumVal.setV(value.toString());
                ++effectiveNumOfPoints;
            }
        }
        if (effectiveNumOfPoints == 0) {
            cache.unsetPtCount();
        }
        else if (cache.isSetPtCount()) {
            cache.getPtCount().setVal((long)numOfPoints);
        }
        else {
            cache.addNewPtCount().setVal((long)numOfPoints);
        }
    }
}
