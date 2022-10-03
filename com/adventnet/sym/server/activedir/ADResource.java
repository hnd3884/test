package com.adventnet.sym.server.activedir;

import com.adventnet.sym.server.resource.Resource;

public class ADResource extends Resource
{
    public ADResource(final String name, final String domainName) {
        super(name, domainName);
    }
    
    public ADResource(final String name, final String domainName, final int type) {
        super(name, domainName, type);
    }
}
