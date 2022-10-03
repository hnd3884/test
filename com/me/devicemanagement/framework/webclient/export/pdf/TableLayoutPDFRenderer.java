package com.me.devicemanagement.framework.webclient.export.pdf;

import com.me.devicemanagement.framework.webclient.export.ExportAuditUtils;
import com.adventnet.client.view.common.ExportAuditModel;

public class TableLayoutPDFRenderer extends com.adventnet.client.components.layout.table.pdf.TableLayoutPDFRenderer
{
    public void auditExport(final ExportAuditModel exportAuditModel) {
        ExportAuditUtils.auditExport(exportAuditModel);
    }
}
