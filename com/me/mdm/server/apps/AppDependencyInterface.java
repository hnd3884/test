package com.me.mdm.server.apps;

import java.util.List;
import java.util.HashMap;

public interface AppDependencyInterface
{
    HashMap extractAndValidateUpload(final String p0);
    
    List getDependenciesFromApp(final String p0);
    
    List getDependencyFilePaths(final String p0);
}
