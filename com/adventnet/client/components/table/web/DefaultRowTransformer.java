package com.adventnet.client.components.table.web;

import com.adventnet.client.view.web.ViewContext;
import java.util.Arrays;
import com.adventnet.client.components.table.TableViewState;
import java.util.ArrayList;
import com.adventnet.client.components.web.TransformerContext;
import java.util.List;

public class DefaultRowTransformer implements RowTransformer
{
    @Override
    public void setRowSelectionDetails(final List<String> selectionList, final TableTransformerContext transContext) {
        if (transContext.getViewContext().isCSRComponent() || selectionList == null || selectionList.isEmpty()) {
            return;
        }
        transContext.getViewContext().setStateParameter("_RS", (Object)selectionList);
    }
    
    @Override
    public boolean canSelectRow(final TransformerContext context) {
        final ViewContext vc = context.getViewContext();
        final String pkcol = vc.getModel().getFeatureValue("PKCOL");
        if (context.getAssociatedPropertyValue(pkcol) == null) {
            return false;
        }
        List<String> rowSelectionDetails = new ArrayList<String>();
        if (vc.isCSRComponent()) {
            final String[] selectedRowIndices = ((TableViewState)vc.getViewState()).getSelectedRowIndices();
            rowSelectionDetails = Arrays.asList(selectedRowIndices);
        }
        else if (vc.getStateOrURLStateParameter("_RS") instanceof List) {
            rowSelectionDetails = (List)vc.getStateOrURLStateParameter("_RS");
        }
        return rowSelectionDetails.contains(String.valueOf(context.getAssociatedPropertyValue(pkcol)));
    }
}
