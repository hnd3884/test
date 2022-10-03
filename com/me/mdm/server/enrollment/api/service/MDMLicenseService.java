package com.me.mdm.server.enrollment.api.service;

import com.me.mdm.server.util.MDMLicenseUtil;
import com.me.mdm.server.enrollment.api.model.LicenseResolveModel;

public class MDMLicenseService
{
    public void resolveMDMLicenseCount(final LicenseResolveModel licenseResolveModel) throws Exception {
        new MDMLicenseUtil().moveRemainingDevicesToWaitingForLicenseStatus(licenseResolveModel);
    }
}
