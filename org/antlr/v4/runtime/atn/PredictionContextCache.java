package org.antlr.v4.runtime.atn;

import java.util.HashMap;
import java.util.Map;

public class PredictionContextCache
{
    protected final Map<PredictionContext, PredictionContext> cache;
    
    public PredictionContextCache() {
        this.cache = new HashMap<PredictionContext, PredictionContext>();
    }
    
    public PredictionContext add(final PredictionContext ctx) {
        if (ctx == PredictionContext.EMPTY) {
            return PredictionContext.EMPTY;
        }
        final PredictionContext existing = this.cache.get(ctx);
        if (existing != null) {
            return existing;
        }
        this.cache.put(ctx, ctx);
        return ctx;
    }
    
    public PredictionContext get(final PredictionContext ctx) {
        return this.cache.get(ctx);
    }
    
    public int size() {
        return this.cache.size();
    }
}
