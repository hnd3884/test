package org.apache.tika.pipes.emitter;

import org.apache.tika.exception.TikaException;

public class TikaEmitterException extends TikaException
{
    public TikaEmitterException(final String msg) {
        super(msg);
    }
    
    public TikaEmitterException(final String msg, final Throwable t) {
        super(msg, t);
    }
}
