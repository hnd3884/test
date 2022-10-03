package org.apache.tika.pipes.emitter;

import java.io.IOException;
import org.apache.tika.metadata.Metadata;
import java.util.List;

public interface Emitter
{
    String getName();
    
    void emit(final String p0, final List<Metadata> p1) throws IOException, TikaEmitterException;
    
    void emit(final List<? extends EmitData> p0) throws IOException, TikaEmitterException;
}
