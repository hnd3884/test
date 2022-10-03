package com.sun.xml.internal.bind.v2.schemagen;

import com.sun.xml.internal.bind.Util;
import java.io.IOException;
import javax.xml.transform.Result;
import java.util.logging.Logger;
import javax.xml.bind.SchemaOutputResolver;

final class FoolProofResolver extends SchemaOutputResolver
{
    private static final Logger logger;
    private final SchemaOutputResolver resolver;
    
    public FoolProofResolver(final SchemaOutputResolver resolver) {
        assert resolver != null;
        this.resolver = resolver;
    }
    
    @Override
    public Result createOutput(final String namespaceUri, final String suggestedFileName) throws IOException {
        FoolProofResolver.logger.entering(this.getClass().getName(), "createOutput", new Object[] { namespaceUri, suggestedFileName });
        final Result r = this.resolver.createOutput(namespaceUri, suggestedFileName);
        if (r != null) {
            final String sysId = r.getSystemId();
            FoolProofResolver.logger.finer("system ID = " + sysId);
            if (sysId == null) {
                throw new AssertionError((Object)"system ID cannot be null");
            }
        }
        FoolProofResolver.logger.exiting(this.getClass().getName(), "createOutput", r);
        return r;
    }
    
    static {
        logger = Util.getClassLogger();
    }
}
