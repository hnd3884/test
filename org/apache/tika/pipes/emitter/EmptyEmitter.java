package org.apache.tika.pipes.emitter;

import java.io.IOException;
import org.apache.tika.metadata.Metadata;
import java.util.List;

public class EmptyEmitter implements Emitter
{
    @Override
    public String getName() {
        return "empty";
    }
    
    @Override
    public void emit(final String emitKey, final List<Metadata> metadataList) throws IOException, TikaEmitterException {
    }
    
    @Override
    public void emit(final List<? extends EmitData> emitData) throws IOException, TikaEmitterException {
    }
}
