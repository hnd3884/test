package com.sun.xml.internal.ws.api.pipe.helper;

import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.PipeCloner;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.Pipe;

public class PipeAdapter extends AbstractTubeImpl
{
    private final Pipe next;
    
    public static Tube adapt(final Pipe p) {
        if (p instanceof Tube) {
            return (Tube)p;
        }
        return new PipeAdapter(p);
    }
    
    public static Pipe adapt(final Tube p) {
        if (p instanceof Pipe) {
            return (Pipe)p;
        }
        class TubeAdapter extends AbstractPipeImpl
        {
            private final Tube t = p;
            
            public TubeAdapter(final Tube t) {
            }
            
            private TubeAdapter(final TubeAdapter that, final PipeCloner cloner) {
                super(that, cloner);
                this.t = cloner.copy(that.t);
            }
            
            @Override
            public Packet process(final Packet request) {
                return Fiber.current().runSync(this.t, request);
            }
            
            @Override
            public Pipe copy(final PipeCloner cloner) {
                return new TubeAdapter(this, cloner);
            }
        }
        return new TubeAdapter();
    }
    
    private PipeAdapter(final Pipe next) {
        this.next = next;
    }
    
    private PipeAdapter(final PipeAdapter that, final TubeCloner cloner) {
        super(that, cloner);
        this.next = ((PipeCloner)cloner).copy(that.next);
    }
    
    @NotNull
    @Override
    public NextAction processRequest(@NotNull final Packet p) {
        return this.doReturnWith(this.next.process(p));
    }
    
    @NotNull
    @Override
    public NextAction processResponse(@NotNull final Packet p) {
        throw new IllegalStateException();
    }
    
    @NotNull
    @Override
    public NextAction processException(@NotNull final Throwable t) {
        throw new IllegalStateException();
    }
    
    @Override
    public void preDestroy() {
        this.next.preDestroy();
    }
    
    @Override
    public PipeAdapter copy(final TubeCloner cloner) {
        return new PipeAdapter(this, cloner);
    }
    
    @Override
    public String toString() {
        return super.toString() + "[" + this.next.toString() + "]";
    }
}
