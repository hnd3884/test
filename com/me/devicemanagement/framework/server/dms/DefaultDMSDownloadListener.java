package com.me.devicemanagement.framework.server.dms;

public class DefaultDMSDownloadListener implements DMSDownloadListener
{
    @Override
    public DMSDownloadEvent preFileDownload(final DMSDownloadEvent event) {
        return event;
    }
    
    @Override
    public DMSDownloadEvent postDownloadEvent(final DMSDownloadEvent event) {
        return event;
    }
}
