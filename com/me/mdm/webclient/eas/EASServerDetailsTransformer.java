package com.me.mdm.webclient.eas;

import org.json.simple.JSONObject;
import java.util.HashMap;
import com.me.mdm.server.easmanagement.EASMgmt;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.easmanagement.EASMgmtDataHandler;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.authorization.RolecheckerTransformer;

public class EASServerDetailsTransformer extends RolecheckerTransformer
{
    private Logger logger;
    
    public EASServerDetailsTransformer() {
        this.logger = Logger.getLogger("EASMgmtLogger");
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        final int reportType = tableContext.getViewContext().getRenderType();
        return (!columnalias.equalsIgnoreCase("EASServerDetails.EAS_SERVER_ID") || reportType == 4) && super.checkIfColumnRendererable(tableContext);
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        this.logger.log(Level.FINE, "Entering EASDeviceListTransformer renderHeader().....");
        super.renderHeader(tableContext);
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final Object data = tableContext.getPropertyValue();
        final String columnalais = tableContext.getPropertyName();
        final long serverId = (long)tableContext.getAssociatedPropertyValue("EASServerDetails.EAS_SERVER_ID");
        if (columnalais.equals("EASServerDetails.EXCHANGE_SERVER_VERSION")) {
            final Integer ExchangeVersion = (Integer)data;
            String ExchangeVersionStr = "--";
            if (ExchangeVersion != null) {
                switch (ExchangeVersion) {
                    case 0: {
                        ExchangeVersionStr = I18N.getMsg("mdm.eas.exchange_online", new Object[0]);
                        break;
                    }
                    case 14: {
                        ExchangeVersionStr = I18N.getMsg("mdm.cea.es10", new Object[0]);
                        break;
                    }
                    case 15: {
                        ExchangeVersionStr = I18N.getMsg("mdm.cea.es13", new Object[0]);
                        break;
                    }
                    case 16: {
                        ExchangeVersionStr = I18N.getMsg("mdm.cea.es16", new Object[0]);
                        break;
                    }
                    case 19: {
                        ExchangeVersionStr = I18N.getMsg("mdm.cea.es19", new Object[0]);
                        break;
                    }
                }
            }
            columnProperties.put("VALUE", ExchangeVersionStr);
        }
        if (columnalais.equals("EASServerDetails.DEFAULT_ACCESS_LEVEL")) {
            final Integer defaultAccessLevel = (Integer)data;
            String deviceAccessStateStr = "--";
            if (defaultAccessLevel != null) {
                deviceAccessStateStr = I18N.getMsg("desktopcentral.common.Allow", new Object[0]);
                if (defaultAccessLevel == 1) {
                    deviceAccessStateStr = I18N.getMsg("desktopcentral.collection.config.usb.configuration.block", new Object[0]);
                }
                else if (defaultAccessLevel == 2) {
                    deviceAccessStateStr = I18N.getMsg("mdm.cea.quarantine", new Object[0]);
                }
            }
            columnProperties.put("VALUE", deviceAccessStateStr);
        }
        if (columnalais.equals("MAILBOXES")) {
            Integer syncUserCount = 0;
            try {
                final JSONObject exchangeServerDetails = EASMgmtDataHandler.getInstance().getExchangeServerDetails(false);
                final Long lastSuccessfulSyncTime = (Long)exchangeServerDetails.get((Object)"LAST_SUCCESSFUL_SYNC_TASK");
                Criteria userCountCriteria = new Criteria(Column.getColumn("EASMailboxDetails", "EAS_SERVER_ID"), (Object)serverId, 0);
                if (lastSuccessfulSyncTime != null) {
                    userCountCriteria = userCountCriteria.and(new Criteria(Column.getColumn("EASMailboxDetails", "LAST_UPDATED_TIME"), (Object)lastSuccessfulSyncTime, 4));
                }
                syncUserCount = DBUtil.getRecordActualCount("EASMailboxDetails", "EAS_MAILBOX_ID", userCountCriteria);
            }
            catch (final Exception ex) {
                EASMgmt.logger.log(Level.SEVERE, null, ex);
            }
            columnProperties.put("VALUE", syncUserCount);
        }
    }
}
