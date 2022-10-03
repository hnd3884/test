package com.me.devicemanagement.framework.webclient.export.pdf;

import com.me.devicemanagement.framework.webclient.export.ExportAuditUtils;
import com.adventnet.client.view.common.ExportAuditModel;

public class PropertySheetRenderer extends com.adventnet.client.components.table.pdf.PropertySheetRenderer
{
    public void auditExport(final ExportAuditModel exportAuditModel) {
        ExportAuditUtils.auditExport(exportAuditModel);
    }
}
