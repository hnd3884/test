package com.adventnet.sym.webclient.mdm.config;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMTableRetrieverAction;

public class ProfileDetailsRetrieverAction extends MDMTableRetrieverAction
{
    private Logger logger;
    
    public ProfileDetailsRetrieverAction() {
        this.logger = Logger.getLogger(ProfileDetailsRetrieverAction.class.getName());
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final long updateResourceId = Long.parseLong(request.getParameter("resourceId"));
            final long profileId = Long.parseLong(request.getParameter("profileId"));
            final Criteria criGroup = new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)updateResourceId, 0);
            final Criteria criProfile = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileId, 0);
            final Criteria cri = criGroup.and(criProfile);
            selectQuery.setCriteria(cri);
            super.setCriteria(selectQuery, viewCtx);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in MDMGroupProfileExecutionStatusRetrieverAction...", e);
        }
    }
}
