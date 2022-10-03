package com.adventnet.client.components.table.web;

import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.client.components.web.SearchOperator;
import com.adventnet.client.components.web.TransformerContext;

public interface ColumnTransformer
{
    void renderHeader(final TransformerContext p0) throws Exception;
    
    void renderCell(final TransformerContext p0) throws Exception;
    
    void initCellRendering(final TransformerContext p0) throws Exception;
    
    boolean canRenderColumn(final TransformerContext p0) throws Exception;
    
    String formatSumValue(final String p0, final String p1, final String p2, final boolean p3) throws Exception;
    
    SearchOperator[] getSearchOperators(final TransformerContext p0) throws Exception;
    
    Criteria formCriteria(final Column p0, final String p1, final int p2, final int p3) throws Exception;
    
    String getValidateJSFunction(final TransformerContext p0);
    
    String getErrorMsg(final TransformerContext p0) throws Exception;
    
    String getInputFormat(final TransformerContext p0);
}
