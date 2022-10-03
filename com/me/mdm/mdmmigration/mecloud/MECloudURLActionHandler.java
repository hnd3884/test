package com.me.mdm.mdmmigration.mecloud;

import javax.servlet.http.HttpServletRequest;
import com.me.mdm.mdmmigration.URLActionHandler;

public class MECloudURLActionHandler extends URLActionHandler
{
    @Override
    protected void setAttributes(final HttpServletRequest request) {
        super.setAttributes(request);
        request.setAttribute("title", (Object)"ME MDM Migration");
        request.setAttribute("isDownloadNetworkProfile", (Object)"false");
    }
}
