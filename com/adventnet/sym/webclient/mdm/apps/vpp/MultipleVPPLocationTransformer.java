package com.adventnet.sym.webclient.mdm.apps.vpp;

import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.server.apps.ios.vpp.IOSVPPEnterpriseBusinessStore;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.HashMap;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

class MultipleVPPLocationTransformer extends DefaultTransformer
{
    Logger logger;
    
    MultipleVPPLocationTransformer() {
        this.logger = Logger.getLogger("MDMBStoreLogger");
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        return super.checkIfColumnRendererable(tableContext);
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
        final HashMap headerProperties = tableContext.getRenderedAttributes();
        final String columnalais = tableContext.getPropertyName();
        final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
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
                columnProperties.put("VALUE", org);
            }
        }
        else if (columnalais.equalsIgnoreCase("MdVPPTokenDetails.LOCATION_NAME")) {
            final String locationName = (String)tableContext.getPropertyValue();
            try {
                final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
                final Long bussinessStoreID = (Long)tableContext.getAssociatedPropertyValue("MDBSSS.BUSINESSSTORE_ID");
                final IOSVPPEnterpriseBusinessStore store = new IOSVPPEnterpriseBusinessStore(bussinessStoreID, customerID);
                columnProperties.put("PAYLOAD", store.getSyncStoreStatus(null));
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "MultipleVPPTransformerError - StoreFacade", ex);
            }
            columnProperties.put("VALUE", locationName);
        }
    }
}
