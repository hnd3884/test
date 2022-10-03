package com.sun.xml.internal.ws.api.pipe.helper;

import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.PipeCloner;
import com.sun.xml.internal.ws.api.pipe.Pipe;

public abstract class AbstractFilterPipeImpl extends AbstractPipeImpl
{
    protected final Pipe next;
    
    protected AbstractFilterPipeImpl(final Pipe next) {
        this.next = next;
        assert next != null;
    }
    
    protected AbstractFilterPipeImpl(final AbstractFilterPipeImpl that, final PipeCloner cloner) {
        super(that, cloner);
        this.next = cloner.copy(that.next);
        assert this.next != null;
    }
    
    @Override
    public Packet process(final Packet packet) {
        return this.next.process(packet);
    }
    
    @Override
    public void preDestroy() {
        this.next.preDestroy();
    }
}
