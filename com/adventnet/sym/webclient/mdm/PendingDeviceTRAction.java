package com.adventnet.sym.webclient.mdm;

import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;

public class PendingDeviceTRAction extends MDMTableRetrieverAction
{
    public Logger logger;
    
    public PendingDeviceTRAction() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        Criteria criteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)3, 0);
        final String modelName = viewCtx.getRequest().getParameter("modelName");
        if (modelName != null && !modelName.equalsIgnoreCase("all")) {
            final Criteria modelNameCri = new Criteria(Column.getColumn("MdModelInfo", "MODEL_NAME"), (Object)modelName, 0);
            criteria = criteria.and(modelNameCri);
        }
        query.setCriteria(criteria);
        super.setCriteria(query, viewCtx);
    }
}
