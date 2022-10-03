package com.adventnet.client.components.sqlfilter.web;

import com.adventnet.client.components.filter.web.FilterModel;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.components.filter.web.FilterAPI;
import com.adventnet.client.util.DataUtils;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.table.web.SqlViewController;

public class FilterSqlViewController extends SqlViewController
{
    Object column;
    
    @Override
    public String getSQLString(final ViewContext viewCtx) throws Exception {
        final HttpServletRequest request = viewCtx.getRequest();
        final Row filterRow = viewCtx.getModel().getViewConfiguration().getRow("ACTableFilterListRel");
        final Long filterListId = (Long)filterRow.get(2);
        final Long filterConfigListName = (Long)filterRow.get(4);
        final Criteria criteria = new Criteria(new Column("ACFilterConfigList", "ID"), (Object)filterConfigListName, 0);
        final Boolean bool = (Boolean)filterRow.get(5);
        request.setAttribute("ISEDIT", (Object)bool);
        if (bool) {
            final DataObject dobject = DataUtils.getFromCache("FilterConfiguration", "ACFilterConfigList", "ID", filterRow.get("FILTERCONFIGLISTNAME"));
            final String controllerViewName = (String)dobject.getValue("ACFilterConfigList", 3, criteria);
            request.setAttribute("controllerViewName", (Object)controllerViewName);
        }
        String filterName = (String)viewCtx.getStateOrURLStateParameter("SELFILTER");
        if (filterName == null) {
            filterName = request.getParameter("SELFILTER");
        }
        if (filterName == null) {
            filterName = (String)filterRow.get(3);
        }
        if (filterName != null) {
            viewCtx.setStateOrURLStateParam("SELFILTER", (Object)filterName);
            Object filter = viewCtx.getTransientState("EXTFILTERQUERY");
            if (filter == null) {
                filter = FilterAPI.getFilter(filterListId, filterName);
            }
            viewCtx.setTransientState("FILTER", filter);
            if (filter instanceof SelectQuery) {
                return (String)filter;
            }
        }
        final FilterModel fm = FilterAPI.getFilterModel(filterListId, WebClientUtil.getAccountId());
        fm.setSelectedFilter(filterName);
        viewCtx.setTransientState("FILTERMODEL", (Object)fm);
        return super.getSQLString(viewCtx);
    }
}
