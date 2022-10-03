package com.adventnet.sym.server.mdm.message;

import com.me.devicemanagement.framework.server.util.SyMUtil;

public class DownloadPPKGToolMessageHandler implements MessageListener
{
    @Override
    public Boolean getMessageStatus(final Long customerId) {
        Boolean isClose = Boolean.TRUE;
        final String ppkgDownloaded = SyMUtil.getSyMParameter("PPKG_DOWNLOADED_ALREADY");
        if (ppkgDownloaded != null && Boolean.valueOf(ppkgDownloaded)) {
            isClose = Boolean.FALSE;
        }
        return isClose;
    }
}
