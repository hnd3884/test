package com.me.devicemanagement.framework.webclient.export.pdf;

import com.me.devicemanagement.framework.webclient.export.ExportAuditUtils;
import com.adventnet.client.view.common.ExportAuditModel;

public abstract class AbstractTableRenderer extends com.adventnet.client.components.table.pdf.AbstractTableRenderer
{
    public void auditExport(final ExportAuditModel exportAuditModel) {
        ExportAuditUtils.auditExport(exportAuditModel);
    }
}
