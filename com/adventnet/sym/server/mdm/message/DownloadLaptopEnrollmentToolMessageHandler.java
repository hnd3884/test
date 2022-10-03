package com.adventnet.sym.server.mdm.message;

import com.adventnet.sym.server.mdm.util.MDMUtil;

public class DownloadLaptopEnrollmentToolMessageHandler implements MessageListener
{
    @Override
    public Boolean getMessageStatus(final Long customerId) {
        Boolean isClose = Boolean.TRUE;
        final String laptopToolDownloaded = MDMUtil.getSyMParameter("LAPTOP_TOOL_DOWNLOADED_ALREADY");
        if (laptopToolDownloaded != null && Boolean.valueOf(laptopToolDownloaded)) {
            isClose = Boolean.FALSE;
        }
        return isClose;
    }
}
