package org.glassfish.jersey.process.internal;

public abstract class AbstractChainableStage<DATA> implements ChainableStage<DATA>
{
    private Stage<DATA> nextStage;
    
    protected AbstractChainableStage() {
        this(null);
    }
    
    protected AbstractChainableStage(final Stage<DATA> nextStage) {
        this.nextStage = nextStage;
    }
    
    @Override
    public final void setDefaultNext(final Stage<DATA> next) {
        this.nextStage = next;
    }
    
    public final Stage<DATA> getDefaultNext() {
        return this.nextStage;
    }
}
