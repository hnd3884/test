package com.sun.xml.internal.ws.api.pipe;

import java.util.logging.Level;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

public class PipeClonerImpl extends PipeCloner
{
    private static final Logger LOGGER;
    
    public PipeClonerImpl() {
        super(new HashMap<Object, Object>());
    }
    
    protected PipeClonerImpl(final Map<Object, Object> master2copy) {
        super(master2copy);
    }
    
    @Override
    public <T extends Pipe> T copy(final T p) {
        Pipe r = this.master2copy.get(p);
        if (r == null) {
            r = p.copy(this);
            assert this.master2copy.get(p) == r : "the pipe must call the add(...) method to register itself before start copying other pipes, but " + p + " hasn't done so";
        }
        return (T)r;
    }
    
    @Override
    public void add(final Pipe original, final Pipe copy) {
        assert !this.master2copy.containsKey(original);
        assert original != null && copy != null;
        this.master2copy.put(original, copy);
    }
    
    public void add(final AbstractTubeImpl original, final AbstractTubeImpl copy) {
        this.add(original, (Tube)copy);
    }
    
    @Override
    public void add(final Tube original, final Tube copy) {
        assert !this.master2copy.containsKey(original);
        assert original != null && copy != null;
        this.master2copy.put(original, copy);
    }
    
    @Override
    public <T extends Tube> T copy(final T t) {
        Tube r = this.master2copy.get(t);
        if (r == null) {
            if (t != null) {
                r = t.copy(this);
            }
            else if (PipeClonerImpl.LOGGER.isLoggable(Level.FINER)) {
                PipeClonerImpl.LOGGER.fine("WARNING, tube passed to 'copy' in " + this + " was null, so no copy was made");
            }
        }
        return (T)r;
    }
    
    static {
        LOGGER = Logger.getLogger(PipeClonerImpl.class.getName());
    }
}
