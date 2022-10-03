package com.me.mdm.server.apps;

import java.util.HashMap;

public interface CloudFileStorageInterface
{
    void updateCloudFileStorageDetails(final HashMap p0, final long p1);
    
    void deleteCloudFileStorageDetails(final String p0);
    
    boolean validateGadgetsURL(final String p0);
}
