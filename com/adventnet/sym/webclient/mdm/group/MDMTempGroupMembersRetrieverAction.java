package com.adventnet.sym.webclient.mdm.group;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMTempGroupMembersRetrieverAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMTempGroupMembersRetrieverAction() {
        this.logger = Logger.getLogger(MDMTempGroupMembersRetrieverAction.class.getName());
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            final Criteria groupTempCri = new Criteria(Column.getColumn("MemberViewTemp", "USER_ID"), (Object)userId, 0);
            Criteria baseCri = selectQuery.getCriteria();
            if (baseCri != null) {
                baseCri = baseCri.and(groupTempCri);
            }
            else {
                baseCri = groupTempCri;
            }
            selectQuery.setCriteria(baseCri);
            request.setAttribute("isEditable", (Object)true);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, null, ex);
        }
    }
}
