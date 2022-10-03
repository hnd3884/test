package com.me.devicemanagement.framework.webclient.export.pdf;

import com.me.devicemanagement.framework.webclient.export.ExportAuditUtils;
import com.adventnet.client.view.common.ExportAuditModel;

public class ChartPDFView extends com.adventnet.client.components.chart.pdf.ChartPDFView
{
    public void auditExport(final ExportAuditModel exportAuditModel) {
        ExportAuditUtils.auditExport(exportAuditModel);
    }
}
