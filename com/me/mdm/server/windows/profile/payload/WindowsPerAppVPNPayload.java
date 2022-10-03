package com.me.mdm.server.windows.profile.payload;

import java.util.Iterator;
import java.util.List;

public class WindowsPerAppVPNPayload
{
    String baseURI;
    
    WindowsPerAppVPNPayload(final String baseURI) {
        this.baseURI = null;
        this.baseURI = baseURI;
    }
    
    public void setAppTriggers(final List applist, final WindowsPayload windowsPayload) {
        final Iterator iterator = applist.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            final String localURI = this.baseURI + "/AppTriggerList/" + i + "/App/Id";
            final String curPfn = iterator.next();
            windowsPayload.getAddPayloadCommand().addRequestItem(windowsPayload.createCommandItemTagElement(localURI, curPfn, "chr"));
            ++i;
        }
    }
}
