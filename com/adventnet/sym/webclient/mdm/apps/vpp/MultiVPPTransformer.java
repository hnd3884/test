package com.adventnet.sym.webclient.mdm.apps.vpp;

import org.json.JSONObject;
import java.util.HashMap;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Level;
import com.me.mdm.server.apps.ios.vpp.VPPSyncStatusHandler;
import com.me.mdm.server.apps.ios.vpp.IOSVPPEnterpriseBusinessStore;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import com.me.mdm.server.doc.DocMgmt;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class MultiVPPTransformer extends DefaultTransformer
{
    private Logger logger;
    
    public MultiVPPTransformer() {
        this.logger = DocMgmt.logger;
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        try {
            super.renderCell(tableContext);
            final ViewContext vc = tableContext.getViewContext();
            final Object data = tableContext.getPropertyValue();
            final String columnAlias = tableContext.getPropertyName();
            final int reportType = tableContext.getViewContext().getRenderType();
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            super.renderCell(tableContext);
            final String columnalais = tableContext.getPropertyName();
            final String viewName = tableContext.getViewContext().getUniqueId();
            final ViewContext viewCtx = tableContext.getViewContext();
            final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
            if (columnalais.equalsIgnoreCase("DEPAccountDetails.ORG_TYPE")) {
                final Integer orgType = (Integer)tableContext.getPropertyValue();
                String org = "Apple Business/School Manager";
                if (orgType != null) {
                    if (orgType == 1) {
                        org = "Apple Business Manager";
                    }
                    else if (orgType == 2) {
                        org = "Apple School Manager";
                    }
                }
                columnProperties.put("VALUE", org);
            }
            if (columnalais.equalsIgnoreCase("MdVPPTokenDetails.LOCATION_NAME")) {
                final String locationName = (String)tableContext.getPropertyValue();
                try {
                    final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
                    final Long bussinessStoreID = (Long)tableContext.getAssociatedPropertyValue("MDBSSS.BUSINESSSTORE_ID");
                    final IOSVPPEnterpriseBusinessStore store = new IOSVPPEnterpriseBusinessStore(bussinessStoreID, customerID);
                    final JSONObject syncResponseJSON = new VPPSyncStatusHandler().getSyncStatus(bussinessStoreID);
                    columnProperties.put("PAYLOAD", syncResponseJSON);
                }
                catch (final Exception ex) {
                    this.logger.log(Level.SEVERE, "MultipleVPPTransformerError - StoreFacade", ex);
                }
                columnProperties.put("VALUE", locationName);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in renderCell", e);
        }
    }
}
