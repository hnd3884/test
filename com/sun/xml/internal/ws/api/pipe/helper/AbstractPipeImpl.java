package com.sun.xml.internal.ws.api.pipe.helper;

import com.sun.xml.internal.ws.api.pipe.PipeCloner;
import com.sun.xml.internal.ws.api.pipe.Pipe;

public abstract class AbstractPipeImpl implements Pipe
{
    protected AbstractPipeImpl() {
    }
    
    protected AbstractPipeImpl(final Pipe that, final PipeCloner cloner) {
        cloner.add(that, this);
    }
    
    @Override
    public void preDestroy() {
    }
}
