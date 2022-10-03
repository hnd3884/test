package com.me.mdm.webclient.eas;

import java.util.HashMap;
import org.json.JSONObject;
import com.adventnet.i18n.I18N;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class EASDeviceListTransformer extends DefaultTransformer
{
    private Logger logger;
    
    public EASDeviceListTransformer() {
        this.logger = Logger.getLogger("EASMgmtLogger");
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        final int reportType = tableContext.getViewContext().getRenderType();
        return (!columnalias.equalsIgnoreCase("EASMailboxDeviceRel.EAS_MAILBOX_DEVICE_ID") || reportType == 4) && super.checkIfColumnRendererable(tableContext);
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
        final Integer accessState = (Integer)tableContext.getAssociatedPropertyValue("EASMailboxDeviceInfo.DEVICE_ACCESS_STATE");
        final String managedUserEmailId = (String)tableContext.getAssociatedPropertyValue("ManagedUser.EMAIL_ADDRESS");
        final String mailBoxEmailId = (String)tableContext.getAssociatedPropertyValue("EASMailboxDetails.EMAIL_ADDRESS");
        final Integer graceDays = (Integer)tableContext.getAssociatedPropertyValue("EASPolicy.GRACE_DAYS");
        final Long gracePeriodStart = (Long)tableContext.getAssociatedPropertyValue("EASMailboxGracePeriod.GRACE_PERIOD_START");
        if (columnalais.equals("ManagedDevice.MANAGED_STATUS")) {
            final Integer managedStatusData = (Integer)data;
            String managedStatus = "--";
            String managedStatusInfo = "";
            final String managedStatusStr = "--";
            String className = "";
            final String infoStr = "";
            Long gracePeriodExpiresInMilli = null;
            Long gracePeriodExpiresIn = null;
            if (gracePeriodStart != null && graceDays != null) {
                final Long graceDaysinMillis = TimeUnit.DAYS.toMillis(graceDays);
                gracePeriodExpiresInMilli = graceDaysinMillis - (System.currentTimeMillis() - gracePeriodStart);
                gracePeriodExpiresIn = TimeUnit.MILLISECONDS.toDays(gracePeriodExpiresInMilli);
                if (gracePeriodExpiresInMilli > 0L && gracePeriodExpiresIn == 0L) {
                    gracePeriodExpiresIn = 1L;
                }
            }
            if (managedStatusData != null && managedStatusData == 2) {
                managedStatus = I18N.getMsg("dc.mdm.enrolled", new Object[0]);
                if (managedUserEmailId.equalsIgnoreCase(mailBoxEmailId)) {
                    className = "ucs-table-status-text__success";
                }
                else if (accessState == 0 && gracePeriodExpiresIn != null && gracePeriodExpiresIn > 0L) {
                    managedStatusInfo = I18N.getMsg("mdm.cea.graceperiod.emailmistch.info", new Object[] { gracePeriodExpiresIn, mailBoxEmailId, managedUserEmailId });
                    className = "ucs-table-status-text__in-progress";
                }
                else {
                    managedStatusInfo = I18N.getMsg("mdm.cea.restricted.emailmistch.info", new Object[] { gracePeriodExpiresIn, mailBoxEmailId, managedUserEmailId });
                    className = "ucs-table-status-text__failed";
                }
            }
            else {
                managedStatus = I18N.getMsg("mdm.cea.managed.status.not.enrolled", new Object[0]);
                if (accessState == 0 && gracePeriodExpiresIn != null && gracePeriodExpiresIn > 0L) {
                    managedStatusInfo = I18N.getMsg("mdm.cea.graceperiod.notenrolled.info", new Object[] { gracePeriodExpiresIn });
                    className = "ucs-table-status-text__in-progress";
                }
                else {
                    className = "ucs-table-status-text__failed";
                }
            }
            final JSONObject payload = new JSONObject();
            payload.put("managedStatusInfo", (Object)managedStatusInfo);
            payload.put("managedStatus", (Object)managedStatus);
            payload.put("className", (Object)className);
            columnProperties.put("PAYLOAD", payload);
            columnProperties.put("VALUE", managedStatus);
        }
        if (columnalais.equals("EASMailboxDeviceInfo.DEVICE_ACCESS_STATE")) {
            final Integer deviceAccessState = (Integer)data;
            String deviceAccessStateStr = "--";
            if (deviceAccessState == 0) {
                deviceAccessStateStr = I18N.getMsg("dc.inv.sw.ALLOWED", new Object[0]);
            }
            if (deviceAccessState == 1) {
                deviceAccessStateStr = I18N.getMsg("dc.inv.sw.Blocked", new Object[0]);
            }
            else if (deviceAccessState == 2) {
                deviceAccessStateStr = I18N.getMsg("dc.inv.sw.Quarantined", new Object[0]);
            }
            columnProperties.put("VALUE", deviceAccessStateStr);
        }
    }
}
