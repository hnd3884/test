package org.apache.poi.ss.formula;

import org.apache.poi.util.Internal;

@Internal
public interface EvaluationSheet
{
    EvaluationCell getCell(final int p0, final int p1);
    
    void clearAllCachedResultValues();
    
    int getLastRowNum();
    
    boolean isRowHidden(final int p0);
}
