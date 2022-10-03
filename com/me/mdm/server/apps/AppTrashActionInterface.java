package com.me.mdm.server.apps;

import org.json.JSONObject;
import java.util.HashMap;

public interface AppTrashActionInterface
{
    void performUninstallForGroup(final HashMap p0);
    
    void performUninstallForResource(final HashMap p0);
    
    boolean isDeleteAllowed(final Long p0, final JSONObject p1);
}
