package com.adventnet.sym.webclient.mdm.certificate;

import java.util.List;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMCertificateDevicesTableRetriverAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMCertificateDevicesTableRetriverAction() {
        this.logger = Logger.getLogger(MDMCertificateProfileRetriverAction.class.getName());
    }
    
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext arg0) throws Exception {
        final SelectQuery selectQuery = super.fetchAndCacheSelectQuery(arg0);
        selectQuery.addJoin(new Join("CollnToResources", "RecentProfileForResource", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 2));
        selectQuery.setDistinct(true);
        return selectQuery;
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        final Long certID = Long.parseLong(viewCtx.getRequest().getParameter("certificateId"));
        final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
        final List certIds = new ArrayList();
        certIds.add(certID);
        final SelectQuery criteriaQuery = ProfileCertificateUtil.getInstance().getCertConfigSelectQuery(certIds);
        criteriaQuery.addJoin(new Join("ConfigDataItem", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        criteriaQuery.removeSelectColumn(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ITEM_ID"));
        criteriaQuery.addSelectColumn(Column.getColumn("CfgDataToCollection", "COLLECTION_ID"));
        final DerivedColumn derivedColumn = new DerivedColumn("CertConfigDataItems", criteriaQuery);
        selectQuery.setCriteria(new Criteria(Column.getColumn("CollnToResources", "COLLECTION_ID"), (Object)derivedColumn, 8).and(new Criteria(Column.getColumn("CollnToResources", "STATUS"), (Object)6, 0)).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0)));
        super.setCriteria(selectQuery, viewCtx);
    }
}
