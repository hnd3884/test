package com.me.ems.summaryserver.summary.probeadministration.webclient.transformer;

import java.util.HashMap;
import com.adventnet.client.view.web.ViewContext;
import org.json.JSONObject;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.authorization.NewViewRolecheckerTransformer;

public class ProbeViewTransformer extends NewViewRolecheckerTransformer
{
    static String className;
    static Logger out;
    
    public void renderCell(final TransformerContext tableContext) {
        ProbeViewTransformer.out.log(Level.FINE, "Entered into ProbeViewTransformer.renderCell()");
        try {
            super.renderCell(tableContext);
            final ViewContext viewCtx = tableContext.getViewContext();
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final String displayColumn = tableContext.getDisplayName();
            final Long probeId = (Long)tableContext.getAssociatedPropertyValue("ProbeDetails.PROBE_ID");
            if (I18N.getMsg("dc.admin.servicenow.serverstatus", new Object[0]).equals(displayColumn)) {
                final Integer status = (Integer)tableContext.getAssociatedPropertyValue("ProbeLiveStatus.STATUS");
                if (status == null) {
                    columnProperties.put("VALUE", I18N.getMsg("dc.common.NOT_INSTALLED", new Object[0]));
                    columnProperties.put("ACTUAL_VALUE", I18N.getMsg("dc.common.NOT_INSTALLED", new Object[0]));
                }
                else if (1 == status) {
                    columnProperties.put("VALUE", I18N.getMsg("dc.som.Live", new Object[0]));
                    columnProperties.put("ACTUAL_VALUE", I18N.getMsg("dc.som.Live", new Object[0]));
                }
                else if (2 == status) {
                    columnProperties.put("VALUE", I18N.getMsg("dc.common.down", new Object[0]));
                    columnProperties.put("ACTUAL_VALUE", I18N.getMsg("dc.common.down", new Object[0]));
                }
            }
            if (I18N.getMsg("ems.ss.probemgmt.COMPUTERS_COUNT", new Object[0]).equals(displayColumn)) {
                final int computerCount = ProbeMgmtFactoryProvider.getProbeResourceAPI().getProbeWiseManagedComputersCount(probeId);
                columnProperties.put("VALUE", String.valueOf(computerCount));
                columnProperties.put("ACTUAL_VALUE", String.valueOf(computerCount));
                final JSONObject payload = new JSONObject();
                payload.put("count", computerCount);
                columnProperties.put("PAYLOAD", payload);
            }
            if (I18N.getMsg("ems.ss.probemgmt.MOBILE_DEVICES_COUNT", new Object[0]).equals(displayColumn)) {
                final int mobileDevicesCount = ProbeMgmtFactoryProvider.getProbeResourceAPI().getProbeWiseManagedMobileDevicesCount(probeId);
                columnProperties.put("VALUE", String.valueOf(mobileDevicesCount));
                columnProperties.put("ACTUAL_VALUE", String.valueOf(mobileDevicesCount));
                final JSONObject payload = new JSONObject();
                payload.put("count", mobileDevicesCount);
                columnProperties.put("PAYLOAD", payload);
            }
        }
        catch (final Exception e) {
            ProbeViewTransformer.out.log(Level.SEVERE, "Exception while fetching AllProbesView transformed value", e);
        }
    }
    
    static {
        ProbeViewTransformer.className = ProbeViewTransformer.class.getName();
        ProbeViewTransformer.out = Logger.getLogger(ProbeViewTransformer.className);
    }
}
