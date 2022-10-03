package com.me.devicemanagement.onpremise.server.settings.nat;

import java.util.HashMap;

public interface NATListener
{
    String isNATUpdateSafe(final NATObject p0);
    
    void natModified(final NATObject p0);
    
    HashMap setValuesInNATForm(final HashMap p0);
    
    NATObject getNATports();
}
