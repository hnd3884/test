package com.sun.xml.internal.ws.api.pipe.helper;

import com.sun.xml.internal.ws.api.pipe.PipeCloner;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.Tube;

public abstract class AbstractTubeImpl implements Tube, Pipe
{
    protected AbstractTubeImpl() {
    }
    
    protected AbstractTubeImpl(final AbstractTubeImpl that, final TubeCloner cloner) {
        cloner.add(that, this);
    }
    
    protected final NextAction doInvoke(final Tube next, final Packet packet) {
        final NextAction na = new NextAction();
        na.invoke(next, packet);
        return na;
    }
    
    protected final NextAction doInvokeAndForget(final Tube next, final Packet packet) {
        final NextAction na = new NextAction();
        na.invokeAndForget(next, packet);
        return na;
    }
    
    protected final NextAction doReturnWith(final Packet response) {
        final NextAction na = new NextAction();
        na.returnWith(response);
        return na;
    }
    
    protected final NextAction doThrow(final Packet response, final Throwable t) {
        final NextAction na = new NextAction();
        na.throwException(response, t);
        return na;
    }
    
    @Deprecated
    protected final NextAction doSuspend() {
        final NextAction na = new NextAction();
        na.suspend();
        return na;
    }
    
    protected final NextAction doSuspend(final Runnable onExitRunnable) {
        final NextAction na = new NextAction();
        na.suspend(onExitRunnable);
        return na;
    }
    
    @Deprecated
    protected final NextAction doSuspend(final Tube next) {
        final NextAction na = new NextAction();
        na.suspend(next);
        return na;
    }
    
    protected final NextAction doSuspend(final Tube next, final Runnable onExitRunnable) {
        final NextAction na = new NextAction();
        na.suspend(next, onExitRunnable);
        return na;
    }
    
    protected final NextAction doThrow(final Throwable t) {
        final NextAction na = new NextAction();
        na.throwException(t);
        return na;
    }
    
    @Override
    public Packet process(final Packet p) {
        return Fiber.current().runSync(this, p);
    }
    
    @Override
    public final AbstractTubeImpl copy(final PipeCloner cloner) {
        return this.copy((TubeCloner)cloner);
    }
    
    @Override
    public abstract AbstractTubeImpl copy(final TubeCloner p0);
}
