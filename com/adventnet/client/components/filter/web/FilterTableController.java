package com.adventnet.client.components.filter.web;

import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.client.view.dynamiccontentarea.web.DynamicContentAreaAPI;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.client.util.DataUtils;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.util.web.WebConstants;
import com.adventnet.client.components.table.web.TableRetrieverAction;

public class FilterTableController extends TableRetrieverAction implements WebConstants
{
    Object column;
    
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext viewCtx) throws Exception {
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
        String filterName = request.getParameter("SELFILTER");
        if (filterName == null) {
            filterName = (String)viewCtx.getStateOrURLStateParameter("SELFILTER");
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
            if (filter instanceof SelectQuery) {
                return (SelectQuery)filter;
            }
            viewCtx.setTransientState("FILTER", filter);
        }
        final FilterModel fm = FilterAPI.getFilterModel(filterListId, WebClientUtil.getAccountId());
        fm.setSelectedFilter(filterName);
        viewCtx.setTransientState("FILTERMODEL", (Object)fm);
        return super.fetchAndCacheSelectQuery(viewCtx);
    }
    
    @Override
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        final Criteria filter = (Criteria)viewCtx.getTransientState("FILTER");
        if (filter != null) {
            Criteria cr = query.getCriteria();
            cr = ((cr != null) ? cr.and(filter) : filter);
            query.setCriteria(cr);
        }
        super.setCriteria(query, viewCtx);
    }
    
    public ActionForward processEvent(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String eventType) throws Exception {
        if (eventType.equals("Edit")) {
            String cvView = viewCtx.getModel().getFeatureValue("FILTEREDITVIEW");
            if (cvView == null) {
                cvView = "FilterCreateForm";
            }
            DynamicContentAreaAPI.updateDynamicContentArea(request, (Object)cvView, (String)null, DynamicContentAreaAPI.getContentAreaFromState(viewCtx, request), "VIEWNAME=" + viewCtx.getModel().getViewName() + "&" + "EVENT_TYPE" + "=Edit", false, true);
        }
        else if (eventType.equals("Delete")) {
            final String filterName = (String)viewCtx.getStateOrURLStateParameter("SELFILTER");
            final Long listId = (Long)viewCtx.getModel().getViewConfiguration().getFirstValue("ACTableFilterListRel", "FILTERLISTID");
            FilterAPI.deleteFilter(listId, filterName);
            viewCtx.setStateOrURLStateParam("SELFILTER", (Object)null);
        }
        else if (eventType.equals("Create")) {
            String cvView = viewCtx.getModel().getFeatureValue("FILTERCREATEVIEW");
            if (cvView == null) {
                cvView = "FilterCreateForm";
            }
            DynamicContentAreaAPI.updateDynamicContentArea(request, (Object)cvView, (String)null, DynamicContentAreaAPI.getContentAreaFromState(viewCtx, request), "VIEWNAME=" + viewCtx.getModel().getViewName() + "&" + "EVENT_TYPE" + "=Add", false, true);
        }
        return new ActionForward(WebViewAPI.getRootViewURL(request));
    }
}
