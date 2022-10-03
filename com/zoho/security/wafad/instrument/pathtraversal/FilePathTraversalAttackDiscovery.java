package com.zoho.security.wafad.instrument.pathtraversal;

import java.io.File;
import java.util.Map;

public final class FilePathTraversalAttackDiscovery extends PathTraversalAttackDiscovery
{
    public static final String FILE_OBJECT_PARAM_NAME = "FILE_OBJECT";
    
    @Override
    protected boolean matchesCondition(final Map<String, Object> params) {
        final File file = params.get("FILE_OBJECT");
        if (file == null) {
            return false;
        }
        params.put("PATH_PAYLOAD", file.getPath());
        return super.matchesCondition(params);
    }
}
