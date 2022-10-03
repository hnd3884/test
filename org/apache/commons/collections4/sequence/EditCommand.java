package org.apache.commons.collections4.sequence;

public abstract class EditCommand<T>
{
    private final T object;
    
    protected EditCommand(final T object) {
        this.object = object;
    }
    
    protected T getObject() {
        return this.object;
    }
    
    public abstract void accept(final CommandVisitor<T> p0);
}
