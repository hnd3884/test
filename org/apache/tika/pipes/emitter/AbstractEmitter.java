package org.apache.tika.pipes.emitter;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractEmitter implements Emitter
{
    private String name;
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public void emit(final List<? extends EmitData> emitData) throws IOException, TikaEmitterException {
        for (final EmitData d : emitData) {
            this.emit(d.getEmitKey().getEmitKey(), d.getMetadataList());
        }
    }
}
