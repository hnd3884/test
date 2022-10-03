package com.me.mdm.server.windows.profile.payload.content.vpn;

import java.util.HashMap;
import com.adventnet.persistence.DataObject;

public interface PluginProfileGenerator
{
    HashMap createPluginProfileData(final DataObject p0, final Long p1);
    
    void generatePluginXML(final HashMap p0);
    
    void createRootElement();
}
