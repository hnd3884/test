package com.adventnet.client.components.table.web;

import java.util.List;
import com.adventnet.client.components.web.TransformerContext;

public interface RowTransformer
{
    boolean canSelectRow(final TransformerContext p0);
    
    void setRowSelectionDetails(final List<String> p0, final TableTransformerContext p1);
}
