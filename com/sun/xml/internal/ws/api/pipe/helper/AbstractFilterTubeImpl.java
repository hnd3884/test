package com.sun.xml.internal.ws.api.pipe.helper;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.Tube;

public abstract class AbstractFilterTubeImpl extends AbstractTubeImpl
{
    protected final Tube next;
    
    protected AbstractFilterTubeImpl(final Tube next) {
        this.next = next;
    }
    
    protected AbstractFilterTubeImpl(final AbstractFilterTubeImpl that, final TubeCloner cloner) {
        super(that, cloner);
        if (that.next != null) {
            this.next = cloner.copy(that.next);
        }
        else {
            this.next = null;
        }
    }
    
    @NotNull
    @Override
    public NextAction processRequest(final Packet request) {
        return this.doInvoke(this.next, request);
    }
    
    @NotNull
    @Override
    public NextAction processResponse(final Packet response) {
        return this.doReturnWith(response);
    }
    
    @NotNull
    @Override
    public NextAction processException(final Throwable t) {
        return this.doThrow(t);
    }
    
    @Override
    public void preDestroy() {
        if (this.next != null) {
            this.next.preDestroy();
        }
    }
}
