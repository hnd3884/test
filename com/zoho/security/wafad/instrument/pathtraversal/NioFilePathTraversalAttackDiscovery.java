package com.zoho.security.wafad.instrument.pathtraversal;

import java.nio.file.Path;
import java.util.Map;

public class NioFilePathTraversalAttackDiscovery extends PathTraversalAttackDiscovery
{
    public static final String PATH_OBJECT_PARAM_NAME = "PATH_OBJECT";
    
    @Override
    protected boolean matchesCondition(final Map<String, Object> params) {
        final Path path = params.get("PATH_OBJECT");
        if (path == null) {
            return false;
        }
        params.put("PATH_PAYLOAD", path.toFile().getPath());
        return super.matchesCondition(params);
    }
}
