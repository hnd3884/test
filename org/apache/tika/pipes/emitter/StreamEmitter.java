package org.apache.tika.pipes.emitter;

import java.io.IOException;
import org.apache.tika.metadata.Metadata;
import java.io.InputStream;

public interface StreamEmitter extends Emitter
{
    void emit(final String p0, final InputStream p1, final Metadata p2) throws IOException, TikaEmitterException;
}
