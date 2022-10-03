package com.adventnet.sym.webclient.mdm.reports;

import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import java.util.HashMap;
import com.adventnet.client.view.common.ExportAuditModel;
import com.me.devicemanagement.framework.webclient.export.ExportAuditUtils;

public class MDMExportAuditUtils extends ExportAuditUtils
{
    public HashMap<String, Object> getEventDetails(final String selectedPIIColumn, final int export_type, final ExportAuditModel auditModel) {
        final ViewContext viewCtx = auditModel.getViewContext();
        final HttpServletRequest request = viewCtx.getRequest();
        final long custId = MSPWebClientUtil.getCustomerID(request);
        final HashMap values = new HashMap();
        values.put("event_id", 2307);
        values.put("resMap", null);
        values.put("remarks", "mdm.gdpr.audit.remarks");
        values.put("remarks_arg", viewCtx.getUniqueId() + "-" + selectedPIIColumn);
        values.put("customer_id", custId);
        return values;
    }
}
