package org.apache.tika.pipes;

import java.io.IOException;
import java.io.Closeable;

public abstract class PipesReporter implements Closeable
{
    public static PipesReporter NO_OP_REPORTER;
    
    public abstract void report(final FetchEmitTuple p0, final PipesResult p1, final long p2);
    
    @Override
    public void close() throws IOException {
    }
    
    static {
        PipesReporter.NO_OP_REPORTER = new PipesReporter() {
            @Override
            public void report(final FetchEmitTuple t, final PipesResult result, final long elapsed) {
            }
        };
    }
}
