package org.apache.poi.xddf.usermodel.chart;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrVal;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrData;

public interface XDDFDataSource<T>
{
    int getPointCount();
    
    T getPointAt(final int p0);
    
    boolean isLiteral();
    
    boolean isCellRange();
    
    boolean isReference();
    
    boolean isNumeric();
    
    int getColIndex();
    
    String getDataRangeReference();
    
    String getFormula();
    
    @Internal
    default void fillStringCache(final CTStrData cache) {
        cache.setPtArray((CTStrVal[])null);
        final int numOfPoints = this.getPointCount();
        int effectiveNumOfPoints = 0;
        for (int i = 0; i < numOfPoints; ++i) {
            final Object value = this.getPointAt(i);
            if (value != null) {
                final CTStrVal ctStrVal = cache.addNewPt();
                ctStrVal.setIdx((long)i);
                ctStrVal.setV(value.toString());
                ++effectiveNumOfPoints;
            }
        }
        if (effectiveNumOfPoints == 0) {
            if (cache.isSetPtCount()) {
                cache.unsetPtCount();
            }
        }
        else if (cache.isSetPtCount()) {
            cache.getPtCount().setVal((long)numOfPoints);
        }
        else {
            cache.addNewPtCount().setVal((long)numOfPoints);
        }
    }
}
