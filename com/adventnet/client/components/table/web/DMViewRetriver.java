package com.adventnet.client.components.table.web;

import com.adventnet.ds.query.Criteria;
import org.json.JSONObject;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.util.EMSProductUtil;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.dcViewFilter.DCViewFilterUtil;
import com.me.devicemanagement.framework.webclient.reportcriteria.ReportCriteriaUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;

public class DMViewRetriver
{
    public static SelectQuery criteria(SelectQuery selectQuery, final ViewContext viewCtx) {
        final HttpServletRequest request = viewCtx.getRequest();
        final String isDCViewFilterReset = request.getParameter("isDCViewFilterReset");
        String viewId = request.getParameter("viewId");
        final String isScheduleReport = String.valueOf(request.getParameter("isScheduledReport"));
        final String scheduleId = String.valueOf(request.getParameter("scheduleID"));
        final ReportCriteriaUtil reportUtil = ReportCriteriaUtil.getInstance();
        String criteriaJSONString = request.getParameter("criteriaJSON");
        final String dcViewFilterID = request.getParameter("dcViewFilterID");
        viewId = ((viewId == null && isDCViewFilterReset == null) ? request.getParameter("viewId") : viewId);
        criteriaJSONString = ((criteriaJSONString == null && isDCViewFilterReset == null) ? request.getParameter("criteriaJSON") : criteriaJSONString);
        if (criteriaJSONString == null && isDCViewFilterReset == null && dcViewFilterID != null) {
            final JSONObject criteriaJSON = DCViewFilterUtil.getInstance().getCriteriaJSONForFilter(Long.valueOf(dcViewFilterID)).dcViewFilterMapper();
            criteriaJSONString = (criteriaJSON.isNull("criteria") ? null : criteriaJSON.toString());
        }
        if (criteriaJSONString != null && isDCViewFilterReset == null) {
            viewCtx.setStateParameter("criteriaJSON", (Object)criteriaJSONString);
        }
        else if (criteriaJSONString == null && viewId == null) {
            final Long viewID = reportUtil.getViewIdForScheduleReport(scheduleId);
            viewId = ((viewID != null) ? String.valueOf(viewID) : null);
            criteriaJSONString = reportUtil.constructCriteriaJSONForScheduleID(scheduleId);
            if (viewId != null && criteriaJSONString != null) {
                viewCtx.setStateParameter("viewId", (Object)(viewId + ""));
                viewCtx.setStateParameter("criteriaJSON", (Object)criteriaJSONString);
            }
        }
        selectQuery = DCViewFilterUtil.getInstance().checkAndAddDCViewFilterCriteria(selectQuery, viewCtx);
        final Table table = new Table("ErrorCodeToKBUrl");
        if (selectQuery.getTableList().contains(table)) {
            Criteria productCodeCriteria = EMSProductUtil.constructProductCodeCriteria("ErrorCodeToKBUrl", "PRODUCT_CODE");
            productCodeCriteria = productCodeCriteria.or(Column.getColumn("ErrorCodeToKBUrl", "KB_URL"), (Object)null, 0);
            final Criteria criteria = selectQuery.getCriteria();
            if (criteria != null) {
                selectQuery.setCriteria(criteria.and(productCodeCriteria));
            }
            else {
                selectQuery.setCriteria(productCodeCriteria);
            }
        }
        return selectQuery;
    }
}
