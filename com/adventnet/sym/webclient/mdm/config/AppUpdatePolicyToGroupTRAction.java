package com.adventnet.sym.webclient.mdm.config;

import javax.servlet.http.HttpServletRequest;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class AppUpdatePolicyToGroupTRAction extends MDMEmberTableRetrieverAction
{
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext viewCtx) throws Exception {
        return super.fetchAndCacheSelectQuery(viewCtx);
    }
    
    @Override
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        final HttpServletRequest httpServletRequest = viewCtx.getRequest();
        final Long profileId = Long.parseLong(httpServletRequest.getParameter("appUpdatePolicyId"));
        final Criteria profileCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"), (Object)profileId, 0);
        final Criteria disassociateCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0);
        query.setCriteria(profileCriteria.and(disassociateCriteria));
        super.setCriteria(query, viewCtx);
    }
}
