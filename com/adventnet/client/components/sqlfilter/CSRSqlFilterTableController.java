package com.adventnet.client.components.sqlfilter;

import com.adventnet.client.components.filter.web.FilterModel;
import com.adventnet.persistence.Row;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.components.filter.web.FilterAPI;
import com.adventnet.client.components.table.TableViewState;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.table.CSRSqlTableController;

public class CSRSqlFilterTableController extends CSRSqlTableController
{
    @Override
    public String getSQLString(final ViewContext viewCtx) throws Exception {
        final Row filterRow = viewCtx.getModel().getViewConfiguration().getRow("ACTableFilterListRel");
        final Long filterListId = (Long)filterRow.get(2);
        String filterName = ((TableViewState)viewCtx.getViewState()).getSelectedFilter();
        if (filterName == null) {
            filterName = (String)filterRow.get(3);
        }
        if (filterName != null) {
            final Object filter = FilterAPI.getFilter(filterListId, filterName);
            if (filter instanceof SelectQuery) {
                return (String)filter;
            }
            viewCtx.setTransientState("FILTER", filter);
        }
        final FilterModel fm = FilterAPI.getFilterModel(filterListId, WebClientUtil.getAccountId());
        fm.setSelectedFilter(filterName);
        viewCtx.setTransientState("FILTERMODEL", (Object)fm);
        return super.getSQLString(viewCtx);
    }
}
