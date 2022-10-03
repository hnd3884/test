package com.sun.xml.internal.ws.assembler;

import java.util.Iterator;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;
import java.util.LinkedList;
import java.util.List;
import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.assembler.dev.TubelineAssemblyContext;

class TubelineAssemblyContextImpl implements TubelineAssemblyContext
{
    private static final Logger LOGGER;
    private Tube head;
    private Pipe adaptedHead;
    private List<Tube> tubes;
    
    TubelineAssemblyContextImpl() {
        this.tubes = new LinkedList<Tube>();
    }
    
    @Override
    public Tube getTubelineHead() {
        return this.head;
    }
    
    @Override
    public Pipe getAdaptedTubelineHead() {
        if (this.adaptedHead == null) {
            this.adaptedHead = PipeAdapter.adapt(this.head);
        }
        return this.adaptedHead;
    }
    
    boolean setTubelineHead(final Tube newHead) {
        if (newHead == this.head || newHead == this.adaptedHead) {
            return false;
        }
        this.head = newHead;
        this.tubes.add(this.head);
        this.adaptedHead = null;
        if (TubelineAssemblyContextImpl.LOGGER.isLoggable(Level.FINER)) {
            TubelineAssemblyContextImpl.LOGGER.finer(MessageFormat.format("Added '{0}' tube instance to the tubeline.", (newHead == null) ? null : newHead.getClass().getName()));
        }
        return true;
    }
    
    @Override
    public <T> T getImplementation(final Class<T> type) {
        for (final Tube tube : this.tubes) {
            if (type.isInstance(tube)) {
                return type.cast(tube);
            }
        }
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger(TubelineAssemblyContextImpl.class);
    }
}
