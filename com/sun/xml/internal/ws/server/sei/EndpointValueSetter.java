package com.sun.xml.internal.ws.server.sei;

import javax.xml.ws.Holder;
import com.sun.xml.internal.ws.model.ParameterImpl;

public abstract class EndpointValueSetter
{
    private static final EndpointValueSetter[] POOL;
    
    private EndpointValueSetter() {
    }
    
    abstract void put(final Object p0, final Object[] p1);
    
    public static EndpointValueSetter get(final ParameterImpl p) {
        final int idx = p.getIndex();
        if (!p.isIN()) {
            return new HolderParam(idx);
        }
        if (idx < EndpointValueSetter.POOL.length) {
            return EndpointValueSetter.POOL[idx];
        }
        return new Param(idx);
    }
    
    static {
        POOL = new EndpointValueSetter[16];
        for (int i = 0; i < EndpointValueSetter.POOL.length; ++i) {
            EndpointValueSetter.POOL[i] = new Param(i);
        }
    }
    
    static class Param extends EndpointValueSetter
    {
        protected final int idx;
        
        public Param(final int idx) {
            super(null);
            this.idx = idx;
        }
        
        @Override
        void put(final Object obj, final Object[] args) {
            if (obj != null) {
                args[this.idx] = obj;
            }
        }
    }
    
    static final class HolderParam extends Param
    {
        public HolderParam(final int idx) {
            super(idx);
        }
        
        @Override
        void put(final Object obj, final Object[] args) {
            final Holder holder = new Holder();
            if (obj != null) {
                holder.value = (T)obj;
            }
            args[this.idx] = holder;
        }
    }
}
